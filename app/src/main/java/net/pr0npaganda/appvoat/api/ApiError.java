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

import com.android.volley.VolleyError;

import net.pr0npaganda.appvoat.utils.AppUtils;


/**
 * Created by Maxence on 17/01/2017.
 */
public class ApiError
{
	public final static int ERROR_NO_PUBLIC_API  = 10;
	public final static int ERROR_NO_PRIVATE_API = 11;
	public final static int ERROR_INVALID_API    = 19;

	public final static int ERROR_INVALID_TOKEN = 21;
	public final static int ERROR_NOT_LOGGED    = 29;

	public final static int ERROR_NO_NETWORK         = 100;
	public final static int ERROR_SUB_DOES_NOT_EXIST = 1001;

	private ApiRequest request;
	private int    code    = 0;
	private String message = "";
	private String type;


	public ApiError(ApiRequest request, VolleyError volleyError)
	{
		this.request = request;

		if (request == null && volleyError == null)
		{
			this.code = ERROR_NO_PUBLIC_API;
			this.message = "API is not set.";
			return;
		}

		if (volleyError.networkResponse == null)
		{
			this.code = ERROR_NO_NETWORK;
			this.message = "Looks like your network is down";
		}
		else
		{
			switch (volleyError.networkResponse.statusCode)
			{
				case 404:
					if (request.getType() == ApiRequest.REQUEST_TYPE_SUB_POSTS)
					{
						this.code = ERROR_SUB_DOES_NOT_EXIST;
						this.message = "The sub does not exists";
						break;
					}

				case 403:
					this.code = ERROR_INVALID_API;
					this.message = "Invalid API";
					break;

				case 401:
					this.code = ERROR_NOT_LOGGED;
					this.message = "You need to be authentified to perform this action";
					break;

				case 400:
					this.code = ERROR_INVALID_TOKEN;
					if (volleyError.getMessage() != null)
						this.message = volleyError.getMessage();
			}
			AppUtils.Log("volleyError: " + volleyError.getMessage() + "__ " + volleyError.networkResponse.statusCode);
		}

		if (this.message.equals(""))
			this.message = "Error while querying Voat server";

	}


	public String getMessage()
	{
		return this.message;
	}


	public int getCode()
	{
		return this.code;
	}


	public ApiRequest getRequest()
	{
		return this.request;
	}
}
