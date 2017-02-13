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


package net.pr0npaganda.appvoat;

import net.pr0npaganda.appvoat.db.AccountsDatabase;
import net.pr0npaganda.appvoat.list.Accounts;
import net.pr0npaganda.appvoat.list.Posts;
import net.pr0npaganda.appvoat.model.Account;
import net.pr0npaganda.appvoat.model.OpenLink;
import net.pr0npaganda.appvoat.model.Post;

import java.io.Serializable;


public class Core implements Serializable, Cloneable
{
	public static final int SOURCE_VOAT   = 1;
	public static final int SOURCE_REDDIT = 2;

	private static Core instance;

	//	private Subs     subs     = new Subs();
	private Posts    posts    = new Posts();
	private Accounts accounts = new Accounts();

	//	private Sub       currentSub        = null;
	private Post     currentPost       = null;
	private Account  currentAccount    = null;
	private long     lastAccountUpdate = 0;
	private OpenLink openLink          = new OpenLink();

	private boolean milked = true;


	private Core()
	{
	}


	public static synchronized Core get()
	{
		if (instance == null)
		{
			instance = new Core();
		}

		return instance;
	}


	public Accounts getAccounts()
	{
		return this.accounts;
	}

	public Core setAccounts(Accounts accounts)
	{
		this.accounts = accounts;
		return this;
	}


	public Account getCurrentAccount()
	{
		long curr = System.currentTimeMillis() / 1000L;
		if (this.currentAccount != null)
		{
			if (this.lastAccountUpdate < (curr - 60))
			{
				this.currentAccount.update();
				this.lastAccountUpdate = curr;
			}

			if (!this.currentAccount.isAuthed())
			{
				AccountsDatabase.setAsActive(0);
				this.currentAccount = null;
			}
		}

		return this.currentAccount;
	}


	public Core setCurrentAccount(final Account account)
	{
		this.currentAccount = account;
		return this;
	}


	public Post getCurrentPost()
	{
		return this.currentPost;
	}


	public Core setCurrentPost(final Post post)
	{
		this.currentPost = post;
		return this;
	}


	public OpenLink getOpenLink()
	{
		return this.openLink;
	}


	public boolean isMilked()
	{
		return this.milked;
	}


	protected Object clone()
	{
		try
		{
			return super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			e.printStackTrace();
		}

		return this;
	}
}