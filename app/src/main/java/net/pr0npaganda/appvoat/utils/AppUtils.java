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


package net.pr0npaganda.appvoat.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.TypedValue;
import android.webkit.MimeTypeMap;

import net.pr0npaganda.appvoat.R;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;


public class AppUtils
{

	public static void Log(String line)
	{
		Log.w("appvoat", line);
	}


	public static String diffTimeFormat(long time)
	{
		float diff = ((new Date().getTime() / 1000L) - time);
		String hours = String.format("%.1f", (diff / 3600));

		return hours + " hours ago";
	}


	public static int getTheme(String config)
	{
		Log("getTheme " + config);
		if (config.equalsIgnoreCase("AppVoat"))
			return R.style.AppVoat;
		if (config.equalsIgnoreCase("AppVoatLight"))
			return R.style.AppVoatLight;

		return 0;
	}


	public static boolean isTablet(Context context)
	{
		return (context.getResources()
				.getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
	}


	public static boolean isLandscape(Context context)
	{
		return (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE);
	}


	public static int pixelFromDp(Context context, int pixel)
	{
		return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, pixel, context.getResources().getDisplayMetrics()));
	}


	static public String getHostFromUrl(String url)
	{
		try
		{
			URI uri = new URI(url);
			String host = uri.getHost();

			return host;
		}
		catch (URISyntaxException urie)
		{
		}

		return url;
	}


	public static String getMimeType(String url)
	{
		String type = null;
		String extension = MimeTypeMap.getFileExtensionFromUrl(url);
		if (extension != null)
		{
			type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
		}
		return type;
	}


	public static String getMimeTypeFromFile(Context context, String file)
	{
		ContentResolver cR = context.getContentResolver();
		String type = cR.getType(Uri.parse(file));

		return type;
	}


	@Nullable
	public static String getMimeTypeFromFile(String f)
	{
		FileInputStream fis = null;
		try
		{
			fis = new FileInputStream(f);
			byte[] buf = new byte[5]; //max ext size + 1
			fis.read(buf, 0, buf.length);
			StringBuilder builder = new StringBuilder(buf.length);
			for (int i = 1; i < buf.length && buf[i] != '\r' && buf[i] != '\n'; i++)
			{
				builder.append((char) buf[i]);
			}
			return builder.toString().toLowerCase();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if (fis != null)
				{
					fis.close();
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		return null;
	}


	public static Bitmap resizeBitmap(Bitmap image, int maxWidth, int maxHeight)
	{
		if (image == null || maxHeight <= 0 || maxWidth <= 0)
			return image;

		int width = image.getWidth();
		int height = image.getHeight();

		if (width < maxWidth && height < maxHeight)
			return image;

		float ratioBitmap = (float) width / (float) height;
		float ratioMax = (float) maxWidth / (float) maxHeight;

		int finalWidth = maxWidth;
		int finalHeight = maxHeight;
		if (ratioMax > 1)
			finalWidth = (int) ((float) maxHeight * ratioBitmap);
		else
			finalHeight = (int) ((float) maxWidth / ratioBitmap);

		image = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true);
		return image;
	}

}
