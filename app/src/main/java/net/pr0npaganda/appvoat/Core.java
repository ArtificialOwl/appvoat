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
import net.pr0npaganda.appvoat.list.Subs;
import net.pr0npaganda.appvoat.model.Account;
import net.pr0npaganda.appvoat.model.OpenLink;
import net.pr0npaganda.appvoat.model.Post;
import net.pr0npaganda.appvoat.model.Sub;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class Core implements Serializable, Cloneable
{
	public static final int SOURCE_VOAT   = 1;
	public static final int SOURCE_REDDIT = 2;

	private static Core instance;

	private Subs     subs     = new Subs();
	private Posts    posts    = new Posts();
	private Accounts accounts = new Accounts();

	private List<Sub> stackSubs         = new ArrayList<>();
	private Sub       currentSub        = null;
	private Post      currentPost       = null;
	private Account   currentAccount    = null;
	private long      lastAccountUpdate = 0;
	private OpenLink  openLink          = new OpenLink();

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


	public static boolean set(Core core)
	{
		if (core == null)
			return false;

		Core.get().setSubs(core.getSubs()).setPosts(core.getPosts()).setAccounts(core.getAccounts()).setCurrentSub(core.getCurrentSub())
				.setCurrentPost(core.getCurrentPost()).setCurrentAccount(core.getCurrentAccount());

		return true;
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

	public Subs getSubs()
	{
		return this.subs;
	}

	public Core setSubs(Subs subs)
	{
		this.subs = subs;
		return this;
	}

	public Posts getPosts()
	{
		return this.posts;
	}

	public Core setPosts(Posts posts)
	{
		this.posts = posts;
		return this;
	}


	public List<Sub> getStackSubs()
	{
		return this.stackSubs;
	}

	public Sub getCurrentSub()
	{
		return this.currentSub;
	}


	public Core setCurrentSub(Sub sub)
	{
		if (sub != null && getSubs().getItem(sub) == null)
			this.getSubs().add(sub);

		this.currentSub = sub;
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