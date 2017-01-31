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

import net.pr0npaganda.appvoat.db.AccountsDatabase;

import org.json.JSONObject;

import java.io.Serializable;


public class Account implements Serializable
{
	private int    id;
	private int    source;
	private String username;
	private boolean active = false;

	private String token;
	private long   expires;
	private String refresh;


	public Account(int id, int source, String username)
	{
		this.id = id;
		this.source = source;
		this.username = username;
	}


	public static Account fromTokenAuth(int source, JSONObject json)
	{
		//AccountsDatabase.addToken()

		return null;
	}


	public boolean isActive()
	{
		return this.active;
	}


	public Account makeActive()
	{
		AccountsDatabase.setAsActive(this);
		return this;
	}

	public Account setActive(boolean active)
	{
		this.active = active;
		return this;
	}


	public int getId()
	{
		return this.id;
	}


	public int getSource()
	{
		return this.source;
	}


	public String getToken()
	{
		return this.token;
	}


	public Account setToken(String token)
	{
		this.token = token;
		return this;
	}


	public String getTokenRefresh()
	{
		return this.refresh;
	}


	public Account setTokenRefresh(String token)
	{
		this.refresh = token;
		return this;
	}


	public long getExpires()
	{
		return this.expires;
	}


	public Account setExpires(long exp)
	{
		this.expires = exp;
		return this;
	}


	public void update()
	{
		AccountsDatabase.save(this);
	}


	@Override
	public boolean equals(Object object)
	{
		if (object == null || !(object instanceof Account))
			return false;

		if (getId() == ((Account) object).getId())
			return true;

		return false;
	}

}
