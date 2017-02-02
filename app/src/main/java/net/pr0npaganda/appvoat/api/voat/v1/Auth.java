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
import net.pr0npaganda.appvoat.db.AccountsDatabase;
import net.pr0npaganda.appvoat.model.Account;
import net.pr0npaganda.appvoat.utils.AppUtils;

import org.json.JSONException;
import org.json.JSONObject;


public class Auth
{
	private Voat    voat;
	private Context context;


	public Auth(Voat voat, Context context)
	{
		this.voat = voat;
		this.context = context;
	}


	public void requestToken(String code)
	{
		String url = "https://api.voat.co/oauth/token";
		ApiRequest request = new ApiRequest(ApiRequest.REQUEST_TYPE_TOKEN, url).setContentType(null).setMethod(Request.Method.POST)
				.setJsonType(ApiRequest.REQUEST_JSONTYPE_OBJECT);

		request.setParams("grant_type", "authorization_code");
		request.setParams("code", code);
		request.setParams("client_id", voat.getPublicApiKey());
		request.setParams("client_secret", voat.getPrivateApiKey());

		voat.request(request);
	}


	public void resultToken(ApiRequest request, JSONObject result)
	{
		try
		{
			Account account = AccountsDatabase.getAccount(request.getSource(), result.getString("userName"));
			account.setToken(result.getString("access_token"));
			account.setTokenRefresh(result.getString("refresh_token"));
			account.setExpires((System.currentTimeMillis() / 1000L) + result.getInt("expires_in"));
			account.setRefreshTime(System.currentTimeMillis() / 1000L);

			account.save();
			account.makeActive();
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
	}


	public void requestRefreshToken(Account account)
	{
		String url = "https://api.voat.co/oauth/token";
		ApiRequest request = new ApiRequest(ApiRequest.REQUEST_TYPE_REFRESH_TOKEN, url).setContentType(null).setMethod(Request.Method.POST)
				.setJsonType(ApiRequest.REQUEST_JSONTYPE_OBJECT);

		request.setParams("grant_type", "refresh_token");
		request.setParams("refresh_token", account.getTokenRefresh());
		request.setParams("client_id", voat.getPublicApiKey());
		request.setParams("client_secret", voat.getPrivateApiKey());

		voat.request(request);
	}


	public void resultRefreshToken(ApiRequest request, JSONObject result)
	{

		AppUtils.Log("refresh token: " + result.toString());
		try
		{
			Account account = AccountsDatabase.getAccount(request.getSource(), result.getString("userName"));
			account.setToken(result.getString("access_token"));
			account.setTokenRefresh(result.getString("refresh_token"));
			account.setExpires((System.currentTimeMillis() / 1000L) + result.getInt("expires_in"));
			account.setRefreshTime(System.currentTimeMillis() / 1000L);

			account.save();
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}

	}

}
