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

import net.pr0npaganda.appvoat.Core;

import java.io.Serializable;


public class Sub implements Serializable
{
	private int     source          = 0;
	private String  name            = "";
	private String  keyname         = "";
	private boolean divider         = false;
	private int     subscriberCount = 0;
	private String  description     = "";
	private Long    creationDate    = 0L;
	private boolean adult           = false;
	private boolean anonymized      = false;
	private int     currentPage     = 0;


	public Sub(int source, String name)
	{
		if (name.startsWith("v/"))
			name = name.substring(2);
		if (name.startsWith("/v/"))
			name = name.substring(3);

		this.source = source;
		this.name = name;
		this.keyname = name;
	}


	public String getKeyname()
	{
		return this.keyname;
	}


	public Sub setKeyname(String keyname)
	{
		this.keyname = keyname;
		return this;
	}


	public int source()
	{
		return source;
	}


	public String getName()
	{
		return name;
	}


	public int getCurrentPage()
	{
		return this.currentPage;
	}


	public String getSearchOptions()
	{
		return "page=" + this.getCurrentPage();
	}


	public void firstPage()
	{
		this.currentPage = 0;
	}


	public void nextPage()
	{
		this.currentPage++;
	}


	public void divider(boolean divider)
	{
		this.divider = divider;
	}


	public boolean hasDivider()
	{
		return this.divider;
	}


	public String nameComplete()
	{
		switch (source)
		{
			case Core.SOURCE_VOAT:
				return "v/" + name;
			case Core.SOURCE_REDDIT:
				return "r/" + name;
		}

		return "";
	}


	public int getSubscriberCount()
	{
		return this.subscriberCount;
	}


	public void setSubscriberCount(int count)
	{
		this.subscriberCount = count;
	}


	public String getDescription()
	{
		return this.description;
	}


	public void setDescription(String description)
	{
		this.description = description;
	}


	public long getCreationDate()
	{
		return this.creationDate;
	}


	public void setCreationDate(long date)
	{
		this.creationDate = date;
	}


	public void anonymized(boolean anonymized)
	{
		this.anonymized = anonymized;
	}


	public boolean isAnonymized()
	{
		return this.anonymized;
	}


	public void adult(boolean adult)
	{
		this.adult = adult;
	}


	public boolean isAdult()
	{
		return this.adult;
	}


	@Override
	public boolean equals(Object object)
	{
		if (object == null || !(object instanceof Post))
			return false;

		if (getName().equalsIgnoreCase(((Sub) object).getName()) && source() == ((Sub) object).source())
			return true;

		return false;
	}
}
