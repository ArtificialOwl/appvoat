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
import net.pr0npaganda.appvoat.model.Comment;
import net.pr0npaganda.appvoat.model.Post;
import net.pr0npaganda.appvoat.model.Singleton;
import net.pr0npaganda.appvoat.model.Sub;
import net.pr0npaganda.appvoat.utils.AppUtils;

import org.json.JSONObject;

import java.util.Map;


public class Api
{
	//	private Core    core;
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


	public void requestSubList(int source, Subs subs)
	{
		if (source == Core.SOURCE_VOAT)
			voat().subverses().requestList(subs);
	}


	public void requestSubPosts(Sub sub, Posts posts)
	{
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


	public void request(final ApiRequest request)
	{
		countRequest++;

		AppUtils.Log("request url: " + request.getUrl());
		AppUtils.Log("params: " + new JSONObject(request.getParams()));

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
				//				Map<String, String> params = new HashMap<String, String>();
				//				params.put("tag", "test");
				return request.getParams();
			}
		};

		//		queue.add(strRequest);
		//		JsonObjectRequest jsObjRequest = new JsonObjectRequest(request.getMethod(), request
		//				.getUrl(), new JSONObject(request.getParams()), new Response.Listener<JSONObject>()
		//		{
		//			@Override
		//			public void onResponse(JSONObject response)
		//			{
		//				AppUtils.Log("Response: " + response.toString());
		//				result(request, response);
		//			}
		//		}, new Response.ErrorListener()
		//		{
		//			@Override
		//			public void onErrorResponse(VolleyError error)
		//			{
		//				AppUtils.Log("Error: " + error.toString() + " " + error.getMessage());
		//				resultError(request, error);
		//			}
		//		})
		//		{
		//			@Override
		//			public Map<String, String> getHeaders() throws AuthFailureError
		//			{
		//				if (request.getHeaders() != null)
		//					return request.getHeaders();
		//				else
		//					return super.getHeaders();
		//			}
		//
		//		};

		// Access the RequestQueue through your singleton class.
		Singleton.getInstance(context).addToRequestQueue(strRequest);
	}


	public void resultError(ApiRequest request, VolleyError error)
	{
		countRequest--;
		//if (countRequest == 0)
		listener.onApiRequestError(new ApiError(request, error));
	}


	public void resultEmpty(ApiRequest request)
	{
		listener.onApiRequestEmpty(request.getType());
	}

	//	private void result(ApiRequest request, JSONArray result)
	//	{
	//		switch (request.getSource())
	//		{
	//			case ApiRequest.SOURCE_VOAT:
	//				voat().result(request, result);
	//		}
	//
	//		countRequest--;
	//		listener.onApiRequestCompleted((countRequest == 0));
	//	}


	private void result(ApiRequest request, String result)
	{
		AppUtils.Log("result: " + result);
		switch (request.getSource())
		{
			case ApiRequest.SOURCE_VOAT:
				voat().result(request, result);
		}

		countRequest--;
		listener.onApiRequestCompleted((countRequest == 0));
	}

	//	private void result(ApiRequest request, JSONObject result)
	//	{
	//		switch (request.getSource())
	//		{
	//			case ApiRequest.SOURCE_VOAT:
	//				voat().result(request, result);
	//		}
	//
	//		countRequest--;
	//		listener.onApiRequestCompleted((countRequest == 0));
	//	}
}
