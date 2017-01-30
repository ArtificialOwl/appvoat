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

import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.CharacterStyle;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.View;

import net.pr0npaganda.appvoat.item.InterceptClickTextView;


public class LinkUtils
{

	public static class RichTextUtils
	{
		public static <A extends CharacterStyle, B extends CharacterStyle> Spannable replaceAll(Spanned original, Class<A> sourceType,
		                                                                                        SpanConverter<A, B> converter)
		{
			SpannableString result = new SpannableString(original);
			A[] spans = result.getSpans(0, result.length(), sourceType);

			for (A span : spans)
			{
				int start = result.getSpanStart(span);
				int end = result.getSpanEnd(span);
				int flags = result.getSpanFlags(span);

				result.removeSpan(span);
				result.setSpan(converter.convert(span), start, end, flags);
			}

			return (result);
		}


		public interface SpanConverter<A extends CharacterStyle, B extends CharacterStyle>
		{
			B convert(A span);
		}
	}

	public static class URLSpanConverter implements RichTextUtils.SpanConverter<URLSpan, ClickSpan>
	{
		@Override
		public ClickSpan convert(URLSpan span)
		{
			return (new ClickSpan(span.getURL()));
		}
	}

	public static class ClickSpan extends ClickableSpan
	{
		private String url;


		public ClickSpan(String url)
		{
			AppUtils.Log("link loaded " + url);
			this.url = url;
		}


		@Override
		public void onClick(View view)
		{
			((InterceptClickTextView) view).linkTo(url);
		}

	}
}