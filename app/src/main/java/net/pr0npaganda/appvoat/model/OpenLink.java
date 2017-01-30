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


package net.pr0npaganda.appvoat.model;

import android.content.Context;
import android.webkit.MimeTypeMap;

import net.pr0npaganda.appvoat.utils.AppUtils;

import java.io.File;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;


public class OpenLink implements Serializable
{
	public static final int DISPLAY_AS_IMAGE = 1;
	public static final int DISPLAY_AS_VIDEO = 2;

	private String       origUrl        = "";
	private List<String> moreUrls       = new ArrayList<>();
	private int          urlContentType = 0;
	private int          urlModifiedTry = 0;
	private boolean      cached         = false;
	private String       unique         = "";


	public void addLink(String url)
	{
	}


	public String getUnique()
	{
		if (this.unique.equals(""))
		{

			URI uri = null;
			try
			{
				uri = new URI(this.origUrl);
			}
			catch (URISyntaxException e)
			{
				e.printStackTrace();
			}

			this.unique = String.format("%s_%s", uri.getHost(), uri.getPath());
		}
		return this.unique;
	}


	public void setUnique(String unique)
	{
		this.unique = unique;
	}


	public void resetUrl()
	{
		moreUrls.clear();
	}


	public String getUrl()
	{
		return origUrl;
	}


	public void setUrl(String url)
	{
		this.origUrl = url;
	}


	public int getUrlContentType()
	{
		return this.urlContentType;
	}


	public void setUrlContentType(int type)
	{
		this.urlContentType = type;
		this.urlModifiedTry = 0;
	}


	public void addModifiedUrl(String url)
	{
		this.moreUrls.add(url);
	}


	public List<String> getModifiedUrl()
	{
		return this.moreUrls;
	}


	public void resetModifiedUrl()
	{
		this.urlModifiedTry = 0;
	}


	public String getModifiedUrlNext()
	{
		if (this.getModifiedUrl().size() < this.urlModifiedTry)
			return null;

		String url = this.getModifiedUrl().get(urlModifiedTry);
		urlModifiedTry++;

		return url;
	}


	public boolean parseUrl()
	{
		String domain = AppUtils.getHostFromUrl(this.getUrl());
		String extension = MimeTypeMap.getFileExtensionFromUrl(this.getUrl()).toLowerCase();
		String mimetype = AppUtils.getMimeType(this.getUrl());

		AppUtils.Log("displayUnknown " + domain + " " + extension + " " + mimetype);

		if ("i.imgur.com" .equalsIgnoreCase(domain) && "gifv" .equalsIgnoreCase(extension))
		{
			this.setUrlContentType(OpenLink.DISPLAY_AS_VIDEO);
			this.addModifiedUrl(this.getUrl().replace("gifv", "mp4"));
			//			cacheLink(post);
			return true;
		}

		if ("gfycat.com" .equalsIgnoreCase(domain))
		{
			this.setUrlContentType(OpenLink.DISPLAY_AS_VIDEO);
			//	post.setChangedUrl(post.getUrl().replace("gfycat.com", "giant.gfycat.com") + ".webm"); // high quality - freeze on some mobile.
			//	post.setChangedUrl(post.getUrl().replace("gfycat.com", "zippy.gfycat.com") + ".webm"); // low quality
			this.addModifiedUrl(this.getUrl().replace("gfycat.com", "thumbs.gfycat.com") + "-mobile.mp4");
			//			cacheLink(post);
			return true;
		}

		return false;
	}


	public String getCacheDir(Context context)
	{
		return context.getExternalFilesDir("cache").getAbsolutePath();
	}


	public String getCacheFilename(Context context)
	{
		String filename = String.format("cache_%s", this.getUnique());

		if (context == null)
			return filename;

		return String.format("%s%s%s", getCacheDir(context), File.separator, filename);
	}


	public void cached(boolean cached)
	{
		this.cached = cached;
	}


	public boolean isCached()
	{
		return this.cached;
	}
}
