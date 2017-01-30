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


package net.pr0npaganda.appvoat.item;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import net.pr0npaganda.appvoat.utils.AppUtils;


public class InterceptClickTextView extends TextView implements View.OnClickListener
{
	private String linkTo = "";


	public InterceptClickTextView(Context context)
	{
		super(context);
	}


	public InterceptClickTextView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}


	public InterceptClickTextView(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
	}


	public void linkTo(String link)
	{
		this.linkTo = link;
	}


	public String getLinkTo()
	{
		return this.linkTo;
	}


	@Override
	public void onClick(View view)
	{

		AppUtils.Log(">>> ON CLICK");
	}
}
