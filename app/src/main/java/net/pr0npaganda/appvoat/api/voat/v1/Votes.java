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
import net.pr0npaganda.appvoat.model.Comment;
import net.pr0npaganda.appvoat.model.Post;

import org.json.JSONException;
import org.json.JSONObject;


public class Votes
{
	public static final int VOTE_TYPE_SUBMISSION = 1;
	public static final int VOTE_TYPE_COMMENT    = 2;

	private Voat    voat;
	private Core    core;
	private Context context;


	public Votes(Voat voat, Core core, Context context)
	{
		this.voat = voat;
		this.core = core;
		this.context = context;
	}


	public void requestVotingComment(Comment comment, int vote)
	{
		String itemtype = "comment";
		String url = String.format("https://api.voat.co/api/v1/vote/%s/%d/%d?revokeOnRevote=true",
		                                           itemtype,
		                                           comment.getId(),
		                                           vote);

		ApiRequest request = new ApiRequest(ApiRequest.REQUEST_TYPE_VOTES, url).setMethod(Request.Method.POST)
				.setJsonType(ApiRequest.REQUEST_JSONTYPE_OBJECT).setExtra("itemtype", itemtype).setComment(comment);

		voat.request(request);
	}


	public void requestVotingPost(Post post, int vote)
	{
		String itemtype = "submission";
		String url = String.format("https://api.voat.co/api/v1/vote/%s/%d/%d?revokeOnRevote=true",
		                           itemtype,
		                           post.getId(),
		                           vote);

		ApiRequest request = new ApiRequest(ApiRequest.REQUEST_TYPE_VOTES, url).setMethod(Request.Method.POST)
				.setJsonType(ApiRequest.REQUEST_JSONTYPE_OBJECT).setExtra("itemtype", itemtype).setPost(post);

		voat.request(request);
	}


	public void result(ApiRequest request, JSONObject result)
	{
		if (request.getExtraString("itemtype").equals("comment"))
		resultVotingComment(request, result);
		if (request.getExtraString("itemtype").equals("submission"))
			resultVotingPost(request, result);
	}


	private void resultVotingComment(ApiRequest request, JSONObject result)
	{
		try
		{
			if (result.getBoolean("success") != true)
				return;

			JSONObject data = result.getJSONObject("data");
			if (!data.isNull("response"))
			{
				JSONObject points = data.getJSONObject("response");

				Comment comment = request.getComment();
				comment.setPoint(points.getInt("upCount"), points.getInt("downCount"));
				comment.setVote(data.getInt("recordedValue"));
			}

			if (!data.isNull("message"))
				request.setMessage(data.getString("message"));
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
	}


	private void resultVotingPost(ApiRequest request, JSONObject result)
	{
		try
		{
			if (result.getBoolean("success") != true)
				return;

			JSONObject data = result.getJSONObject("data");
			if (!data.isNull("response"))
			{
				JSONObject points = data.getJSONObject("response");

				Post post = request.getPost();
				post.setPoint(points.getInt("upCount"), points.getInt("downCount"));
				post.setVote(data.getInt("recordedValue"));
			}

			if (!data.isNull("message"))
				request.setMessage(data.getString("message"));
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
	}

}
