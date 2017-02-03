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


package net.pr0npaganda.appvoat.api.voat.v1;

import android.content.Context;

import com.android.volley.Request;

import net.pr0npaganda.appvoat.api.ApiRequest;
import net.pr0npaganda.appvoat.api.voat.Voat;
import net.pr0npaganda.appvoat.api.voat.v1.model.RecursiveChildren;
import net.pr0npaganda.appvoat.api.voat.v1.model.RecursiveComments;
import net.pr0npaganda.appvoat.model.Author;
import net.pr0npaganda.appvoat.model.Comment;
import net.pr0npaganda.appvoat.model.Post;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class Comments
{
	private Voat    voat;
	private Context context;


	public Comments(Voat voat, Context context)
	{
		this.voat = voat;
		this.context = context;
	}


	private static RecursiveChildren cacheGetChildren(RecursiveChildren cached, int parentId)
	{
		if (parentId == 0)
			return cached;

		for (RecursiveComments co : cached.comments)
		{
			if (co.id == parentId)
				return co.children;

			if (co.childCount > 0)
			{
				RecursiveChildren ch = cacheGetChildren(co.children, parentId);
				if (ch != null)
					return ch;
			}
		}

		return null;
	}


	private static void parseCache(final RecursiveChildren cache, Post post, Comment prec, List<Comment> old) throws JSONException
	{
		int level = 0;
		int parentId = 0;

		if (prec == null)
		{
			post.setChildRemaining(cache.remainingCount);
			post.setChildTotal(cache.totalCount);
			post.hasMore(cache.hasMore);
			post.setNextIndex(cache.endingIndex + 1);
		}
		else
		{
			level = prec.getLevel();
			parentId = prec.getId();
			prec.setChildRemaining(cache.remainingCount);
			prec.setChildTotal(cache.totalCount);
			prec.hasMore(cache.hasMore);
		}

		level++;
		for (RecursiveComments co : cache.comments)
		{
			Comment comment = new Comment(post, co.id);
			comment.setContent(co.content);
			comment.setFormattedContent(co.formattedContent);

			comment.setParentId(parentId);
			comment.setAuthor(new Author(co.userName));
			comment.setPoint(co.upCount, co.downCount);
			comment.setCommentCount(co.childCount);
			comment.setVote(co.vote);
			comment.setTime(Voat.parseDate(co.creationDate));
			comment.setLevel(level);

			if (old.contains(comment))
				post.getBaseComments().add(old.get(old.indexOf(comment)));
			else
				post.getBaseComments().add(comment);

			if (co.childCount > 0)
				parseCache(co.children, post, comment, old);

			if (comment.getChildRemaining() > 0)
			{
				Comment moresubcomm = new Comment(post, 0).setLevel(level + 1).setType(Comment.COMMENT_LOAD_MORE_SUBCOMMENTS);
				moresubcomm.setChildRemaining((comment.getChildRemaining()));
				moresubcomm.setParentId(comment.getId());
				moresubcomm.setNextIndex(co.children.endingIndex + 1);
				post.getBaseComments().add(moresubcomm);
			}
		}

		if (prec == null)
		{
			if (post.getChildRemaining() > 0)
			{
				Comment morecomm = new Comment(post, 0).setType(Comment.COMMENT_LOAD_MORE_COMMENTS);
				morecomm.setChildRemaining((post.getChildRemaining()));
				morecomm.setParentId(0);
				morecomm.setNextIndex(post.getNextIndex());
				post.getBaseComments().add(morecomm);
				post.setMoreComment(morecomm);
			}
			else
				post.setMoreComment(null);
		}
	}


	private static void generateTemporaryComments(Post post)
	{
		int level = -1;
		int count = 0;
		int commentId = 0;

		post.getTemporaryComments().clear();
		for (Comment comment : post.getBaseComments())
		{
			if (level > -1)
			{
				if (comment.getLevel() > level)
				{
					count++;
					continue;
				}
				else
				{
					Comment moresubcomm = new Comment(post, 0).setLevel(level + 1).setParentId(comment.getId())
							.setType(Comment.COMMENT_DISPLAY_COMMENTS);
					moresubcomm.setChildRemaining(count);
					moresubcomm.setParentId(commentId);
					post.getTemporaryComments().add(moresubcomm);
				}
			}
			level = -1;
			count = 0;

			post.getTemporaryComments().add(comment);

			if (!comment.isSubCommentsDisplayed())
			{
				commentId = comment.getId();
				level = comment.getLevel();
			}
		}
	}


	private static void cacheData(JSONObject data, RecursiveChildren child) throws JSONException
	{
		if (!data.isNull("startingIndex"))
			child.startingIndex = data.getInt("startingIndex");
		if (!data.isNull("endingIndex"))
			child.endingIndex = data.getInt("endingIndex");
		if (!data.isNull("hasMore"))
			child.hasMore = data.getBoolean("hasMore");
		if (!data.isNull("remainingCount"))
			child.remainingCount = data.getInt("remainingCount");
		if (!data.isNull("totalCount"))
			child.totalCount = data.getInt("totalCount");

		JSONArray comments = data.getJSONArray("comments");

		for (int i = 0; i < comments.length(); i++)
		{
			JSONObject item = comments.getJSONObject(i);

			RecursiveComments co = new RecursiveComments();
			if (!item.isNull("childCount"))
				co.childCount = item.getInt("childCount");
			if (!item.isNull("content"))
				co.content = item.getString("content");
			if (!item.isNull("creationDate"))
				co.creationDate = item.getString("creationDate");
			if (!item.isNull("formattedContent"))
				co.formattedContent = item.getString("formattedContent");
			if (!item.isNull("id"))
				co.id = item.getInt("id");
			if (!item.isNull("isAnonymized"))
				co.isAnonymized = item.getBoolean("isAnonymized");
			if (!item.isNull("isCollapsed"))
				co.isCollapsed = item.getBoolean("isCollapsed");
			if (!item.isNull("isDeleted"))
				co.isDeleted = item.getBoolean("isDeleted");
			if (!item.isNull("isSaved"))
				co.isSaved = item.getBoolean("isSaved");
			if (!item.isNull("isDistinguished"))
				co.isDistinguished = item.getBoolean("isDistinguished");
			if (!item.isNull("isOwner"))
				co.isOwner = item.getBoolean("isOwner");
			if (!item.isNull("isSubmitter"))
				co.isSubmitter = item.getBoolean("isSubmitter");
			if (!item.isNull("lastEditDate"))
				co.lastEditDate = item.getString("lastEditDate");
			if (!item.isNull("parentID"))
				co.parentID = item.getInt("parentID");
			if (!item.isNull("submissionID"))
				co.submissionID = item.getInt("submissionID");
			if (!item.isNull("subverse"))
				co.subverse = item.getString("subverse");
			if (!item.isNull("userName"))
				co.userName = item.getString("userName");
			if (!item.isNull("vote"))
				co.vote = item.getInt("vote");
			if (!item.isNull("sum"))
				co.sum = item.getInt("sum");
			if (!item.isNull("upCount"))
				co.upCount = item.getInt("upCount");
			if (!item.isNull("downCount"))
				co.downCount = item.getInt("downCount");

			child.comments.add(i + data.getInt("startingIndex"), co);

			if (item.getInt("childCount") > 0)
				cacheData(item.getJSONObject("children"), co.children);
		}
	}


	public void request(Post post)
	{
		post.getBaseComments().clear();
		post.getComments().reset();
		post.resetCommentCache();

		String url = String.format("https://api.voat.co/api/v1/v/%s/%d/comments/", post.getSub().getName(), post.getId());

		voat.request(new ApiRequest(ApiRequest.REQUEST_TYPE_COMMENTS, url).setMethod(Request.Method.GET)
				             .setJsonType(ApiRequest.REQUEST_JSONTYPE_OBJECT).setPost(post));
	}


	public void requestMore(Comment comment)
	{
		Post post = comment.getPost();
		String url = String.format("https://api.voat.co/api/v1/v/%s/%d/comments/%d/%d", "appvoat", post.getId(), comment
				.getParentId(), comment.getNextIndex());

		voat.request(new ApiRequest(ApiRequest.REQUEST_TYPE_COMMENTS, url).setMethod(Request.Method.GET)
				             .setJsonType(ApiRequest.REQUEST_JSONTYPE_OBJECT).setPost(post).setExtra("parentId", comment.getParentId())
				             .setExtra("startIndex", comment.getNextIndex()));
	}


	public void displaySubComments(Comment comment, boolean display)
	{
		if (comment.getPost().getChildren(comment.getId()).size() == 0)
			return;

		comment.subCommentsDisplay(display);
		generateTemporaryComments(comment.getPost());
		comment.getPost().applyTemporaryComments();
	}


	public void result(ApiRequest request, JSONObject result)
	{
		try
		{
			RecursiveChildren child = this.cacheGetChildren(request.getPost().getCommentCache(), request.getExtraInt("parentId", 0));
			cacheData(result.getJSONObject("data"), child);

			ArrayList<Comment> backup = (ArrayList) ((ArrayList<Comment>) request.getPost().getBaseComments()).clone();
			request.getPost().getBaseComments().clear();
			parseCache(request.getPost().getCommentCache(), request.getPost(), null, backup);

			generateTemporaryComments(request.getPost());
			request.getPost().applyTemporaryComments();
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
	}

}
