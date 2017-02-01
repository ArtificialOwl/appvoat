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

import net.pr0npaganda.appvoat.list.Accounts;
import net.pr0npaganda.appvoat.list.Posts;
import net.pr0npaganda.appvoat.list.Subs;
import net.pr0npaganda.appvoat.model.Account;
import net.pr0npaganda.appvoat.model.OpenLink;
import net.pr0npaganda.appvoat.model.Post;
import net.pr0npaganda.appvoat.model.Sub;

import java.io.Serializable;


public class Core implements Serializable, Cloneable
{
	public static final int SOURCE_VOAT   = 1;
	public static final int SOURCE_REDDIT = 2;

	private Subs     subs     = new Subs();
	private Posts    posts    = new Posts();
	private Accounts accounts = new Accounts();

	private Sub      currentSub     = null;
	private Post     currentPost    = null;
	private Account  currentAccount = null;
	private OpenLink openLink       = new OpenLink();

	private boolean milked = true;


	public Core()
	{
		//		this.listener = listener;
		//		this.api = api;
	}


	public Accounts getAccounts()
	{
		return this.accounts;
	}


	public Subs getSubs()
	{
		return this.subs;
	}


	public Posts getPosts()
	{
		return this.posts;
	}


	public Sub getCurrentSub()
	{
		return this.currentSub;
	}


	public void setCurrentSub(Sub sub)
	{
		if (sub != null && getSubs().getItem(sub) == null)
			this.getSubs().add(sub);

		this.currentSub = sub;
	}


	public Account getCurrentAccount()
	{
		return this.currentAccount;
	}


	public void setCurrentAccount(final Account account)
	{
		this.currentAccount = account;
	}


	public Post getCurrentPost()
	{
		return this.currentPost;
	}


	public void setCurrentPost(final Post post)
	{
		this.currentPost = post;
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