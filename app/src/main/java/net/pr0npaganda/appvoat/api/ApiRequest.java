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

import net.pr0npaganda.appvoat.list.Posts;
import net.pr0npaganda.appvoat.list.Subs;
import net.pr0npaganda.appvoat.model.Post;
import net.pr0npaganda.appvoat.model.Sub;

import java.util.HashMap;
import java.util.Map;


public class ApiRequest
{

	public static final int SOURCE_VOAT   = 1;
	public static final int SOURCE_REDDIT = 2;

	public static final int REQUEST_TYPE_TEST      = -1;
	public static final int REQUEST_TYPE_TOKEN     = 1001;
	public static final int REQUEST_TYPE_FRONTPAGE = 10;
	public static final int REQUEST_TYPE_SUB_POSTS = 11;
	public static final int REQUEST_TYPE_SUB_LIST  = 15;
	public static final int REQUEST_TYPE_COMMENTS  = 50;

	public static final int REQUEST_JSONTYPE_ARRAY  = 1;
	public static final int REQUEST_JSONTYPE_OBJECT = 2;

	private int    source      = 0;
	private int    type        = 1;
	private int    jsontype    = 0;
	private String url         = "";
	private String contentType = "";

	private int                 method  = 0;
	private Sub                 sub     = null;
	private Post                post    = null;
	private Posts               posts   = null;
	private Subs                subs    = null;
	private Map<String, String> headers = new HashMap<>();
	private Map<String, String> params  = new HashMap<>();

	private Map<String, Integer> extraInt    = new HashMap<>();
	private Map<String, String>  extraString = new HashMap<>();

	//	private JSONObject params;


	public ApiRequest(int type, String url)
	{
		this.type = type;
		this.url = url;
	}


	public int getSource()
	{
		return this.source;
	}


	public ApiRequest setSource(int source)
	{
		this.source = source;
		return this;
	}


	public int getMethod()
	{
		return this.method;
	}


	public ApiRequest setMethod(int method)
	{
		this.method = method;
		return this;
	}


	public int getJsonType()
	{
		return this.jsontype;
	}


	public ApiRequest setJsonType(int jsontype)
	{
		this.jsontype = jsontype;
		return this;
	}


	public Sub getSub()
	{
		return this.sub;
	}


	public ApiRequest setSub(Sub sub)
	{
		this.sub = sub;
		return this;
	}


	public Post getPost()
	{
		return this.post;
	}


	public ApiRequest setPost(Post post)
	{
		this.post = post;
		return this;
	}


	public Posts getPosts()
	{
		return this.posts;
	}


	public ApiRequest setPosts(Posts posts)
	{
		this.posts = posts;
		return this;
	}


	public Subs getSubs()
	{
		return this.subs;
	}


	public ApiRequest setSubs(Subs subs)
	{
		this.subs = subs;
		return this;
	}


	public String getContentType()
	{
		return this.contentType;
	}


	public ApiRequest setContentType(String contentType)
	{
		this.contentType = contentType;
		return this;
	}


	public int getType()
	{
		return this.type;
	}


	public String getUrl()
	{
		return this.url;
	}


	public Map<String, String> getHeaders()
	{
		return this.headers;
	}


	public ApiRequest addHeader(String k, String v)
	{
		this.headers.put(k, v);
		return this;
	}


	public ApiRequest setExtra(String k, int v)
	{
		this.extraInt.put(k, v);
		return this;
	}


	public ApiRequest setExtra(String k, String v)
	{
		this.extraString.put(k, v);
		return this;
	}


	public int getExtraInt(String k, int def)
	{
		if (!this.extraInt.containsKey(k))
			return def;

		return this.extraInt.get(k);
	}


	public ApiRequest setParams(String k, String v)
	{
		this.params.put(k, v);
		return this;
	}


	public Map<String, String> getParams()
	{
		return this.params;
	}
}
