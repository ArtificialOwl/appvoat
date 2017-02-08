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

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.URLSpan;

import net.pr0npaganda.appvoat.BR;
import net.pr0npaganda.appvoat.utils.AppUtils;
import net.pr0npaganda.appvoat.utils.LinkUtils;

import java.io.Serializable;


public class Conversation extends BaseObservable implements Serializable
{

	private int    id     = 0;
	private Author author = null;
	private Long   time   = 0L;
	private int    vote   = 0;
	private int    point  = 0;

	private int    upCount          = 0;
	private int    downCount        = 0;
	private int    commentCount     = 0;
	private String content          = "";
	private String formattedContent = "";

	private int childRemaining = 0;
	private int childTotal     = 0;
	private int nextIndex      = 0;

	private boolean hasMore = false;


	public Conversation(int id)
	{
		this.id = id;
	}


	public int getId()
	{
		return this.id;
	}


	public void setId(int id)
	{
		this.id = id;
	}


	public Author getAuthor()
	{
		if (this.author != null)
			return author;

		return new Author();
	}


	public void setAuthor(Author author)
	{
		this.author = author;
	}


	public String getAuthorFormat()
	{
		return getAuthor().name();
	}


	public String getCommentCountFormat(String format)
	{
		return String.format(format, getCommentCount());
	}


	public int getCommentCount()
	{
		return this.commentCount;
	}


	public void setCommentCount(int count)
	{
		commentCount = count;
	}


	public Long getTime()
	{
		return time;
	}


	public void setTime(long time)
	{
		this.time = time;
	}


	public int getVote()
	{
		return this.vote;
	}


	public void setVote(int vote)
	{
		this.vote = vote;
	}


	public String getTimeFormat()
	{
		return AppUtils.diffTimeFormat(getTime());
	}


	public void setPoint(int up, int down)
	{
		this.upCount = up;
		this.downCount = down;
		this.setPoint((up - down));
	}


	@Bindable
	public int getPoint()
	{
		return this.point;
	}


	public void setPoint(int point)
	{
		this.point = point;
		notifyPropertyChanged(BR._all);
	}


	public String getPointFormat()
	{
		return String.valueOf(this.getPoint());
	}


	public String getPointFormat(String format)
	{
		return String.format(format, getPoint());
	}


	public String getVotesFormat()
	{
		return String.format("(+%d | -%d)", this.getUpCount(), this.getDownCount());
	}


	public Spannable getVotesFormat(int color1, int color2)
	{
		String down = "-" + this.getDownCount();
		String up = "+" + this.getUpCount();

		String line = String.format("%s | %s", up, down);

		int pos1a = 0;
		int pos1b = pos1a + up.length();
		int pos2a = pos1b + 3;
		int pos2b = pos2a + down.length();

		Spannable span = new SpannableString(line);
		span.setSpan(new ForegroundColorSpan(color1), pos1a, pos1b, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		span.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), pos1a, pos1b, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		span.setSpan(new ForegroundColorSpan(color2), pos2a, pos2b, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		span.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), pos2a, pos2b, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		return span;
	}


	public int getUpCount()
	{
		return this.upCount;
	}


	public int getDownCount()
	{
		return this.downCount;
	}


	public String getContent()
	{
		return this.content;
	}


	public void setContent(String content)
	{
		this.content = content;
	}


	public Spanned getFormattedContent()
	{
		if (formattedContent.startsWith("<p>"))
			formattedContent = formattedContent.substring(3);
		formattedContent = formattedContent.replace("<p>", "<br /><br />").replace("</p>", "");

		Spanned html;
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N)
			html = Html.fromHtml(formattedContent, Html.FROM_HTML_MODE_LEGACY);
		else
			html = Html.fromHtml(formattedContent);

		Spannable result = LinkUtils.RichTextUtils.replaceAll(html, URLSpan.class, new LinkUtils.URLSpanConverter());
		return result;
	}


	public void setFormattedContent(String content)
	{
		this.formattedContent = content;
	}


	public void hasMore(boolean hasmore)
	{
		this.hasMore = hasmore;
	}


	public boolean hasMore()
	{
		return this.hasMore;
	}


	public int getChildTotal()
	{
		return this.childTotal;
	}


	public void setChildTotal(int total)
	{
		this.childTotal = total;
	}


	public int getChildRemaining()
	{
		return this.childRemaining;
	}


	public Conversation setChildRemaining(int remaining)
	{
		this.childRemaining = remaining;
		return this;
	}


	public int getNextIndex()
	{
		return this.nextIndex;
	}


	public void setNextIndex(int nextIndex)
	{
		this.nextIndex = nextIndex;
	}

}