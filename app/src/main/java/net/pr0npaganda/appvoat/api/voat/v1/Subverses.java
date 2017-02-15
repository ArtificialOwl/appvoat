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

import net.pr0npaganda.appvoat.Core;
import net.pr0npaganda.appvoat.api.ApiRequest;
import net.pr0npaganda.appvoat.api.voat.Voat;
import net.pr0npaganda.appvoat.db.PostsDatabase;
import net.pr0npaganda.appvoat.list.Posts;
import net.pr0npaganda.appvoat.list.Subs;
import net.pr0npaganda.appvoat.model.Author;
import net.pr0npaganda.appvoat.model.Post;
import net.pr0npaganda.appvoat.model.Sub;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class Subverses
{
	private Voat    voat;
	private Context context;


	public Subverses(Voat voat, Context context)
	{
		this.voat = voat;
		this.context = context;
	}


	public void requestList(Subs subs)
	{
		String url = String.format("https://api.voat.co/api/v1/subverse/defaults");
		voat.request(new ApiRequest(ApiRequest.REQUEST_TYPE_SUB_LIST, url).setMethod(Request.Method.GET)
				             .setJsonType(ApiRequest.REQUEST_JSONTYPE_OBJECT).setSubs(subs));
	}


	public void resultList(ApiRequest request, JSONObject result)
	{
		request.getSubs().add(new Sub(Core.SOURCE_VOAT, "Frontpage").setKeyname("_front"));
		request.getSubs().add(new Sub(Core.SOURCE_VOAT, "All").setKeyname("_all"));
		request.getSubs().add(new Sub(Core.SOURCE_VOAT, "Any").setKeyname("_any"));
		//		core.getSubs().add(new Sub(Core.SOURCE_VOAT, "Random"));
		//		core.getSubs().add(new Sub(Core.SOURCE_VOAT, "New").setKeyname("_new"));
		//		core.getSubs().add(new Sub(Core.SOURCE_VOAT, "Top").setKeyname("_top"));
		request.getSubs().addDivider();


		try
		{
			JSONArray data = result.getJSONArray("data");
			for (int i = 0; i < data.length(); i++)
			{
				JSONObject item = data.getJSONObject(i);

				Sub sub = new Sub(Core.SOURCE_VOAT, item.getString("title"));
				sub.setSubscriberCount(item.getInt("subscriberCount"));
				sub.setDescription(item.getString("description"));
				sub.setCreationDate(Voat.parseDate(item.getString("creationDate")));
				sub.adult(item.getBoolean("isAdult"));
				//				sub.anonymized(item.getBoolean("isAnonymized"));

				if (request.getSubs().getItem(sub) == null)
					request.getSubs().add(sub);
				else
				{
					request.getSubs().replace(sub);
				}

			}
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
	}


	//
	//
	public void requestPosts(Sub sub, Posts posts)
	{
		String url = String.format("https://api.voat.co/api/v1/v/%s?%s", sub.getKeyname(), sub.getSearchOptions());
		voat.request(new ApiRequest(ApiRequest.REQUEST_TYPE_SUB_POSTS, url).setMethod(Request.Method.GET)
				             .setJsonType(ApiRequest.REQUEST_JSONTYPE_OBJECT).setPosts(posts).setSub(sub));
	}


	public void resultPosts(ApiRequest request, JSONObject result)
	{
		if (request.getSub().getCurrentPage() == 0)
			request.getPosts().reset();
		else
			request.getPosts().removeLoading();

		try
		{
			JSONArray data = result.getJSONArray("data");
			if (data.length() == 0)
			{
				voat.api().resultEmpty(request);
				return;
			}

			for (int i = 0; i < data.length(); i++)
			{
				JSONObject item = data.getJSONObject(i);

				Post post = new Post(new Sub(Core.SOURCE_VOAT, item.getString("subverse")), item.getInt("id"));
				post.setType(item.getString("type").equalsIgnoreCase("text") ? Post.TYPE_TEXT : Post.TYPE_LINK);
				post.setAuthor(new Author(item.getString("userName")));
				post.setTitle(item.getString("title"));
				post.setPoint(item.getInt("upCount"), item.getInt("downCount"));
				post.setCommentCount(item.getInt("commentCount"));
				post.setTime(Voat.parseDate(item.getString("creationDate")));
				if (!item.isNull("vote"))
					post.setVote(item.getInt("vote"));

				post.read(PostsDatabase.isPostRead(post, Core.get().getCurrentAccount(), Post.TYPE_TEXT));
				post.linkOpened(PostsDatabase.isPostRead(post, Core.get().getCurrentAccount(), Post.TYPE_LINK));

				if (post.getType() == Post.TYPE_TEXT)
				{
					post.setContent(item.getString("content"));
					post.setFormattedContent(item.getString("formattedContent"));
				}
				else
				{
					post.getOpenLink().setUrl(item.getString("url"));
					if (item.getString("thumbnailUrl") != "null")
						post.setThumbUrl(item.getString("thumbnailUrl"));
				}

				if (request.getPosts().getItem(post) == null)
					request.getPosts().add(post);
				else
					request.getPosts().replace(post, i);
			}

			request.getPosts().insertLoading(request.getSub());

		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}

	}

}
