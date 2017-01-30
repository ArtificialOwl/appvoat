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
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;


public class Singleton
{
	private static Singleton    mInstance;
	private static Context      mCtx;
	private        RequestQueue mRequestQueue;
	private        ImageLoader  mImageLoader;

	private Singleton(Context context)
	{
		mCtx = context;
		mRequestQueue = getRequestQueue();

		mImageLoader = new ImageLoader(mRequestQueue, new ImageLoader.ImageCache()
		{
			private final LruCache<String, Bitmap> cache = new LruCache<String, Bitmap>(20);

			@Override
			public Bitmap getBitmap(String getUrl)
			{
				return cache.get(getUrl);
			}

			@Override
			public void putBitmap(String getUrl, Bitmap bitmap)
			{
				cache.put(getUrl, bitmap);
			}
		});
	}

	public static synchronized Singleton getInstance(Context context)
	{
		if (mInstance == null)
		{
			mInstance = new Singleton(context);
		}
		return mInstance;
	}


	public RequestQueue getRequestQueue()
	{
		if (mRequestQueue == null)
		{
			// getApplicationContext() is key, it keeps you from leaking the
			// Activity or BroadcastReceiver if someone passes one in.
			mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
		}
		return mRequestQueue;
	}

	public <T> void addToRequestQueue(Request<T> req)
	{
		getRequestQueue().add(req);
	}

	public ImageLoader getImageLoader()
	{
		return mImageLoader;
	}
}