package net.pr0npaganda.appvoat.api.voat;

import android.content.Context;

import net.pr0npaganda.appvoat.R;
import net.pr0npaganda.appvoat.api.Api;
import net.pr0npaganda.appvoat.api.ApiRequest;
import net.pr0npaganda.appvoat.api.voat.v1.Auth;
import net.pr0npaganda.appvoat.api.voat.v1.Comments;
import net.pr0npaganda.appvoat.api.voat.v1.Subverses;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


/**
 * Created by Maxence on 02/01/2017.
 */
public class Voat
{
	public static final String VOAT_SOURCE = "voat";
	//	public  String    apiPublicKey;
	private Context   context;
	private Api       api;
	private Auth      auth;
	private Comments  comments;
	private Subverses subverses;


	public Voat(Api api, Context context)
	{
		this.api = api;
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
		return context.getString(R.string.api_public_key);
	}


	public String getPrivateApiKey()
	{
		return context.getString(R.string.api_private_key);
	}


	public void request(ApiRequest request)
	{
		String contentType = request.getContentType();
		if (contentType.equals(""))
			contentType = "application/json";

		request.setSource(ApiRequest.SOURCE_VOAT);
		//request.addHeader("Content-Type", contentType);
		request.addHeader("Voat-ApiKey", getPublicApiKey());
		request.addHeader("User-Agent", "Appvoat");

		//request.addHeader("Authorization", "Bearer 4a7ed35e9a8b4c148053a28d2e5c335971f8fb427c934152b4bac30a6c68d386");
		this.api.request(request);
	}

	//	public void result(ApiRequest request, JSONArray result)
	//	{
	//		switch (request.getType())
	//		{
	//		}
	//	}


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
}