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


package net.pr0npaganda.appvoat.api.voat;

import android.content.Context;

import net.pr0npaganda.appvoat.Core;
import net.pr0npaganda.appvoat.R;
import net.pr0npaganda.appvoat.api.Api;
import net.pr0npaganda.appvoat.api.ApiRequest;
import net.pr0npaganda.appvoat.api.voat.v1.Auth;
import net.pr0npaganda.appvoat.api.voat.v1.Comments;
import net.pr0npaganda.appvoat.api.voat.v1.Subverses;
import net.pr0npaganda.appvoat.utils.AppUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


public class Voat
{
	public static final String VOAT_SOURCE = "voat";

	static
	{
		try
		{
			System.loadLibrary("voatapi");
		}
		catch (UnsatisfiedLinkError e)
		{
		}
	}

	private Core      core;
	private Context   context;
	private Api       api;
	private Auth      auth;
	private Comments  comments;
	private Subverses subverses;

	private String apiPublicKey  = "";
	private String apiPrivateKey = "";


	public Voat(Api api, Core core, Context context)
	{
		this.api = api;
		this.core = core;
		this.context = context;

		auth = new Auth(this, context);
		comments = new Comments(this, context);
		subverses = new Subverses(this, context);
	}


	public static long parseDate(String date)
	{
		String format = "yyyy-MM-dd'T'HH:mm:ss";
		SimpleDateFormat formater = new SimpleDateFormat(format);
		formater.setTimeZone(TimeZone.getTimeZone("GMT"));
		try
		{
			Date parsed = formater.parse(date);
			return (parsed.getTime() / 1000L);
		}
		catch (Exception e)
		{
			return -1L;
		}
	}


	public String getPublicApiKey()
	{
		if (!this.apiPublicKey.equals(""))
			return this.apiPublicKey;

		this.apiPublicKey = context.getString(R.string.api_public_key);
		if (this.apiPublicKey.equals(""))
		{
			try
			{
				String apiPub = getPublicKey().substring(0, 36);
				this.apiPublicKey = apiPub;
			}
			catch (UnsatisfiedLinkError e)
			{
			}
		}
		return this.apiPublicKey;
	}


	public String getPrivateApiKey()
	{
		if (!this.apiPrivateKey.equals(""))
			return this.apiPrivateKey;

		this.apiPrivateKey = context.getString(R.string.api_private_key);
		if (this.apiPrivateKey.equals(""))
		{
			try
			{
				String apiPriv = getPrivateKey().substring(0, 64);
				this.apiPrivateKey = apiPriv;
			}
			catch (UnsatisfiedLinkError e)
			{
			}
		}

		return this.apiPrivateKey;
	}

	//	public void result(ApiRequest request, JSONArray result)
	//	{
	//		switch (request.getType())
	//		{
	//		}
	//	}


	public boolean request(ApiRequest request)
	{
		if (getPublicApiKey().equals(""))
		{
			this.api.resultError(null, null);
			return false;
		}

		String contentType = request.getContentType();
		if (contentType != null)
		{
			if (contentType.equals(""))
				contentType = "application/json";

			// Note: Adding Content-Type header while retrieving token will return 400
			request.addHeader("Content-Type", contentType);
		}

		request.setSource(ApiRequest.SOURCE_VOAT);
		request.addHeader("Voat-ApiKey", getPublicApiKey());
		request.addHeader("User-Agent", "Appvoat");

		if (core.getCurrentAccount() != null)
		{
			AppUtils.Log("--- Bearer " + core.getCurrentAccount().getToken());
			request.addHeader("Authorization", "Bearer " + core.getCurrentAccount().getToken());
		}
		this.api.request(request);

		return true;
	}


	public void result(ApiRequest request, String result)
	{
		JSONObject json;
		try
		{
			json = new JSONObject(result);
		}
		catch (JSONException e)
		{
			//e.printStackTrace();
			return;
		}

		switch (request.getType())
		{
			case ApiRequest.REQUEST_TYPE_TOKEN:
				auth().resultToken(request, json);
				break;

			case ApiRequest.REQUEST_TYPE_SUB_POSTS:
				subverses().resultPosts(request, json);
				break;

			case ApiRequest.REQUEST_TYPE_SUB_LIST:
				subverses().resultList(request, json);
				break;

			case ApiRequest.REQUEST_TYPE_COMMENTS:
				comments().result(request, json);
				break;

			case ApiRequest.REQUEST_TYPE_TEST:
				//	subverses().result(request, result);
				break;
		}
	}


	public Api api()
	{
		return this.api;
	}


	public Auth auth()
	{
		return this.auth;
	}


	public Comments comments()
	{
		return this.comments;
	}


	public Subverses subverses()
	{
		return this.subverses;
	}


	public String getAuthorizeUrl()
	{
		return "https://api.voat" +
				".co/oauth/authorize?response_type=code&scope=account&grand_type=authorization_code" +
				"&client_id=" + getPublicApiKey();
	}


	@SuppressWarnings ("JniMissingFunction")
	public native String getPublicKey();


	@SuppressWarnings ("JniMissingFunction")
	public native String getPrivateKey();

}
