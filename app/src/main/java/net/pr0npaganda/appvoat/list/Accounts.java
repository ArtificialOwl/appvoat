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


package net.pr0npaganda.appvoat.list;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.ObservableArrayList;

import net.pr0npaganda.appvoat.model.Account;

import java.io.Serializable;


public class Accounts extends BaseObservable implements Serializable
{

	@Bindable
	private ObservableArrayList<Account> accounts;


	public Accounts()
	{
		this.accounts = new ObservableArrayList<>();
	}


	public void reset()
	{
		this.accounts.clear();
	}


	public void add(Account account)
	{
		this.getItems().add(account);
	}


	public int getSize()
	{
		return this.accounts.size();
	}


	public Account getItem(Account search)
	{
		for (Account account : this.getItems())
		{
			if (account.equals(search))
				return account;
		}

		return null;
	}


	public Account getLastItem()
	{
		if (this.getSize() > 0)
			return this.getItems().get(this.getSize() - 1);
		return null;
	}


	public ObservableArrayList<Account> getItems()
	{
		return this.accounts;
	}

}
