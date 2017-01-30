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

import net.pr0npaganda.appvoat.api.Api;

import java.io.Serializable;


public class Comment extends Conversation implements Serializable
{
	public static final int COMMENT_LOAD_MORE_COMMENTS    = 1;
	public static final int COMMENT_LOAD_MORE_SUBCOMMENTS = 2;
	public static final int COMMENT_DISPLAY_COMMENTS      = 4;

	private int  index    = 0;
	private Post post     = null;
	private int  type     = 0;
	private int  level    = 0;
	private int  parentId = 0;
	private Api  api      = null;

	private boolean displaySubComments = true;


	public Comment(Post post, int id)
	{
		super(id);
		this.post = post;
	}


	public Post getPost()
	{
		return this.post;
	}


	public int getLevel()
	{
		return this.level;
	}


	public Comment setLevel(final int level)
	{
		this.level = level;
		return this;
	}


	public Comment api(Api api)
	{
		this.api = api;
		return this;
	}


	public Api api()
	{
		return api;
	}


	public int getParentId()
	{
		return this.parentId;
	}


	public Comment setParentId(int id)
	{
		this.parentId = id;
		return this;
	}


	public int getType()
	{
		return this.type;
	}


	public Comment setType(int type)
	{
		this.type = type;
		return this;
	}


	@Override
	public boolean equals(Object object)
	{
		if (object == null || !(object instanceof Comment))
			return false;

		if ((getId() == ((Comment) object).getId()) &&
				(getType() == ((Comment) object).getType()) && (getParentId() == ((Comment) object).getParentId()) && (getPost().getSub()
				.source() == ((Comment) object).getPost().getSub().source()))
			return true;

		return false;
	}


	public boolean isSubCommentsDisplayed()
	{
		return this.displaySubComments;
	}


	public void subCommentsDisplay(boolean display)
	{
		this.displaySubComments = display;
	}


	public int getIndex()
	{
		return this.index;
	}


	public void setIndex(int index)
	{
		this.index = index;
	}

}