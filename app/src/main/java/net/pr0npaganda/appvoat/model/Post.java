/*
 * Appvoat - Do Androids Dream of Electric Goat?
 *
 * This file is licensed under the General Public License version 3 or later.
 * See the COPYING file.
 *
 * @author Maxence Lange <maxence@pontapreta.net>
 * @copyright (C) 2017 Maxence Lange
 * @license GNU GPL version 3 or any later version
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */


package net.pr0npaganda.appvoat.model;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;

import net.pr0npaganda.appvoat.Core;
import net.pr0npaganda.appvoat.api.voat.v1.model.RecursiveChildren;
import net.pr0npaganda.appvoat.list.Comments;
import net.pr0npaganda.appvoat.utils.AppUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class Post extends Conversation implements Serializable
{

	public static final int URL_TYPE_DOMAIN = 1;

	public static final int TYPE_MORE = 1;
	public static final int TYPE_TEXT = 2;
	public static final int TYPE_LINK = 4;

	private int    type     = 0;
	private Sub    sub      = null;
	private String title    = "";
	private String thumbUrl = "";

	private boolean read       = false;
	private boolean linkOpened = false;

	private RecursiveChildren commentCached = new RecursiveChildren();
	private List<Comment>     baseComments  = new ArrayList<>();
	private List<Comment>     tmpComments   = new ArrayList<>();
	private Comments          comments      = new Comments();

	private OpenLink openLink = new OpenLink();

	private Comment moreComments = null;


	public Post(Sub sub, int id)
	{
		super(id);
		this.sub = sub;

		openLink.setUnique(String.format("post_%d_%d", this.sub.source(), this.getId()));
	}


	public RecursiveChildren getCommentCache()
	{
		return this.commentCached;
	}


	public void resetCommentCache()
	{
		this.commentCached = new RecursiveChildren();
	}


	public Sub getSub()
	{
		return this.sub;
	}


	public int getType()
	{
		return this.type;
	}


	public Post setType(int type)
	{
		this.type = type;
		return this;
	}


	public String getTitle()
	{
		return this.title;
	}


	public void setTitle(String title)
	{
		this.title = title;
	}


	public String getThumbUrl()
	{
		if (getType() != TYPE_LINK)
			return "";

		return this.thumbUrl;
	}


	public void setThumbUrl(String url)
	{
		this.thumbUrl = url;
	}


	public Comment getMoreComment()
	{
		return this.moreComments;
	}


	public void setMoreComment(Comment more)
	{
		this.moreComments = more;
	}


	public Comments getComments()
	{
		return this.comments;
	}


	public List<Comment> getTemporaryComments()
	{
		return this.tmpComments;
	}


	public List<Comment> getBaseComments()
	{
		return this.baseComments;
	}


	public Comment getBaseComment(int commentId)
	{
		for (Comment comment : this.getBaseComments())
		{
			if (commentId == comment.getId())
				return comment;
		}

		return null;
	}


	public List<Comment> getChildren(int parentId)
	{
		List<Comment> result = new ArrayList<>();
		for (Comment comment : getBaseComments())
		{
			if (comment.getParentId() == parentId)
				result.add(comment);
		}

		return result;
	}


	public void applyTemporaryComments()
	{
		int index = 0;

		for (Comment comment : this.getTemporaryComments())
		{
			getComments().place(index, comment);
			index++;
		}

		while (getComments().getSize() > this.getTemporaryComments().size())
			getComments().rem(this.getTemporaryComments().size());
	}


	public void read(boolean read)
	{
		this.read = read;
	}


	public boolean isRead()
	{
		return this.read;
	}


	public void linkOpened(boolean linkOpened)
	{
		this.linkOpened = linkOpened;
	}


	public boolean isLinkOpened()
	{
		return this.linkOpened;
	}


	public OpenLink getOpenLink()
	{
		return this.openLink;
	}


	public String getDomain()
	{
		if (this.getOpenLink().getUrl().equals(""))
		{
			switch (this.getSub().source())
			{
				case Core.SOURCE_REDDIT:
					return String.format("self.%s", this.sub.getName());
				case Core.SOURCE_VOAT:
					return String.format("%s", this.sub.getName());
			}
		}

		String domain = AppUtils.getHostFromUrl(this.getOpenLink().getUrl()).toLowerCase();
		//		if (domain.startsWith("www."))
		//			domain = domain.substring(4);

		String[] block = domain.split("\\.");
		if (block.length > 2)
			domain = String.format("%s.%s", block[block.length - 2], block[block.length - 1]);

		return domain;
	}


	public String getDomainFormat()
	{
		return String.format("(%s)", this.getDomain());
	}


	public Spannable getAuthorFormat(int color)
	{
		String by = "by";
		final String line = String.format("%s %s", by, getAuthor().name());

		final int pos1a = by.length() + 1;
		final int pos1b = pos1a + getAuthor().name().length();

		Spannable span = new SpannableString(line);
		span.setSpan(new ForegroundColorSpan(color), pos1a, pos1b, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		span.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), pos1a, pos1b, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		ClickableSpan spanClickAuthor = new ClickableSpan()
		{
			@Override
			public void onClick(View view)
			{
				AppUtils.Log("click Author " + line.substring(pos1a, pos1b));
			}


			@Override
			public void updateDrawState(TextPaint ds)
			{
			}
		};

		span.setSpan(spanClickAuthor, pos1a, pos1b, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		return span;
	}


	public Spannable getSubFormat(int color)
	{
		String in = "in";
		final String line = String.format("%s %s", in, getSub().getName());

		final int pos1a = in.length() + 1;
		final int pos1b = pos1a + getSub().getName().length();

		Spannable span = new SpannableString(line);
		span.setSpan(new ForegroundColorSpan(color), pos1a, pos1b, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		span.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), pos1a, pos1b, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		ClickableSpan spanClickSub = new ClickableSpan()
		{
			@Override
			public void onClick(View view)
			{
				AppUtils.Log("click Subverse " + line.substring(pos1a, pos1b));
			}


			@Override
			public void updateDrawState(TextPaint ds)
			{
			}
		};

		span.setSpan(spanClickSub, pos1a, pos1b, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		return span;
	}


	public Spannable getAuthorAndSubFormat(int color1, int color2)
	{
		String by = "by";
		String in = "in";
		final String line = String.format("%s %s %s %s", by, getAuthor().name(), in, getSub().getName());

		final int pos1a = by.length() + 1;
		final int pos1b = pos1a + getAuthor().name().length();
		final int pos2a = pos1b + 2 + in.length();
		final int pos2b = pos2a + getSub().getName().length();

		Spannable span = new SpannableString(line);
		span.setSpan(new ForegroundColorSpan(color1), pos1a, pos1b, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		span.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), pos1a, pos1b, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		span.setSpan(new ForegroundColorSpan(color2), pos2a, pos2b, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		span.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), pos2a, pos2b, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		ClickableSpan spanClickSub = new ClickableSpan()
		{
			@Override
			public void onClick(View view)
			{
				AppUtils.Log("click Subbverse " + line.substring(pos2a, pos2b));
			}


			@Override
			public void updateDrawState(TextPaint ds)
			{
			}
		};

		span.setSpan(spanClickSub, pos2a, pos2b, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		return span;
	}



	@Override
	public boolean equals(Object object)
	{
		if (object == null || !(object instanceof Post))
			return false;

		if (getId() == ((Post) object).getId() &&
				getSub().source() == ((Post) object).getSub().source() &&
				getSub().getName().equalsIgnoreCase(((Post) object).getSub().getName()))
			return true;

		return false;
	}
}