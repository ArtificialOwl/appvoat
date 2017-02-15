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


package net.pr0npaganda.appvoat.api;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import net.pr0npaganda.appvoat.Core;
import net.pr0npaganda.appvoat.api.reddit.Reddit;
import net.pr0npaganda.appvoat.api.voat.Voat;
import net.pr0npaganda.appvoat.interfaces.ApiRequestListener;
import net.pr0npaganda.appvoat.list.Posts;
import net.pr0npaganda.appvoat.list.Subs;
import net.pr0npaganda.appvoat.model.Account;
import net.pr0npaganda.appvoat.model.Comment;
import net.pr0npaganda.appvoat.model.Post;
import net.pr0npaganda.appvoat.model.Singleton;
import net.pr0npaganda.appvoat.model.Sub;
import net.pr0npaganda.appvoat.utils.AppUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Map;


public class Api
{
	private Context            context;
	private ApiRequestListener listener;
	private Voat               voat;
	private Reddit             reddit;
	private int countRequest = 0;


	public Api(Context context, ApiRequestListener listener)
	{
		this.context = context;
		this.listener = listener;

		voat = new Voat(this, context);
		reddit = new Reddit();
	}


	private Voat voat()
	{
		return this.voat;
	}


	public void requestTest()
	{
		//	voat().auth().request();
	}


	public void requestToken(int source, String code)
	{
		if (source == Core.SOURCE_VOAT)
			voat().auth().requestToken(code);
	}


	public void refreshToken(Account account)
	{
		if (account.getSource() == Core.SOURCE_VOAT)
			voat().auth().requestRefreshToken(account);
	}


	public void requestSubList(int source, Subs subs)
	{
		if (source == Core.SOURCE_VOAT)
			voat().subverses().requestList(subs);
	}


	public void requestSubPosts(Sub sub, Posts posts)
	{
		if (sub == null)
			return;

		sub.firstPage();
		if (sub.source() == Core.SOURCE_VOAT)
			voat().subverses().requestPosts(sub, posts);
	}


	public void requestMoreSubPosts(Sub sub, Posts posts)
	{
		sub.nextPage();
		if (sub.source() == Core.SOURCE_VOAT)
			voat().subverses().requestPosts(sub, posts);
	}


	public void requestComments(Post post)
	{
		if (post.getSub().source() == Core.SOURCE_VOAT)
			voat().comments().request(post);
	}


	public void requestMoreComment(Comment comment)
	{
		if (comment.getPost().getSub().source() == Core.SOURCE_VOAT)
			voat.comments().requestMore(comment);
	}


	public void displaySubComments(Comment comment, boolean display)
	{
		if (comment.getPost().getSub().source() == Core.SOURCE_VOAT)
			voat().comments().displaySubComments(comment, display);
	}


	public void votingPost(Post post, int vote)
	{
		if (post.getSub().source() == Core.SOURCE_VOAT)
			voat().votes().requestVotingPost(post, vote);
	}


	public void votingComment(Comment comment, int vote)
	{
		if (comment.getPost().getSub().source() == Core.SOURCE_VOAT)
			voat().votes().requestVotingComment(comment, vote);
	}


	public void postingComment(Comment comment)
	{
		if (comment.getPost().getSub().source() == Core.SOURCE_VOAT)
			voat().comments().requestPosting(comment);
	}


	public void request(final ApiRequest request)
	{
		countRequest++;
		StringRequest strRequest = new StringRequest(request.getMethod(), request.getUrl(), new Response.Listener<String>()
		{
			@Override
			public void onResponse(String response)
			{
				result(request, response);
			}
		}, new Response.ErrorListener()
		{
			@Override
			public void onErrorResponse(VolleyError error)
			{
				resultError(request, error);
			}
		})
		{
			@Override
			public Map<String, String> getHeaders() throws AuthFailureError
			{
				if (request.getHeaders() != null)
					return request.getHeaders();
				else
					return super.getHeaders();
			}


			@Override
			protected Map<String, String> getParams()
			{
				return request.getParams();
			}


			@Override
			public String getBodyContentType()
			{
				return "application/json";
			}


			@Override
			public byte[] getBody() throws AuthFailureError
			{
				if (request.getBodyParams().size() == 0)
					return super.getBody();

				String body = new JSONObject(request.getBodyParams()).toString();
				try
				{
					return body.getBytes("utf-8");
				}
				catch (UnsupportedEncodingException e)
				{
					e.printStackTrace();
					return null;
				}
			}

		};

		Singleton.getInstance(context).addToRequestQueue(strRequest);
	}


	public void resultError(ApiRequest request, VolleyError error)
	{
		countRequest--;
		listener.onApiRequestError(new ApiError(request, error));
	}


	public void resultEmpty(ApiRequest request)
	{
		listener.onApiRequestEmpty(request.getType());
	}


	private void result(ApiRequest request, String result)
	{
		JSONObject json;
		try
		{
			json = new JSONObject(result);
		}
		catch (JSONException e)
		{
			return;
		}

		result(request, json);
	}


	private void result(ApiRequest request, JSONObject result)
	{
		AppUtils.Log("result: " + result);
		switch (request.getSource())
		{
			case ApiRequest.SOURCE_VOAT:
				voat().result(request, result);
		}

		if (!request.getMessage().equals(""))
			listener.onApiMessage(request);

		countRequest--;
		listener.onApiRequestCompleted(request, (countRequest == 0));
	}


	public String getAuthorizeUrl(int source)
	{
		switch (source)
		{
			case Core.SOURCE_VOAT:
				return voat.getAuthorizeUrl();
		}

		return "";
	}

}
