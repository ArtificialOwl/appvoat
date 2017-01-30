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

import net.pr0npaganda.appvoat.BR;
import net.pr0npaganda.appvoat.R;
import net.pr0npaganda.appvoat.model.Comment;

import java.io.Serializable;

import me.tatarka.bindingcollectionadapter.ItemView;


public class Comments extends BaseObservable implements Serializable
{
	private transient ItemView itemView = null;

	@Bindable
	private ObservableArrayList<Comment> comments;


	public Comments()
	{
		comments = new ObservableArrayList<>();
	}


	public void reset()
	{
		comments.clear();
	}


	public void add(Comment comment)
	{
		comments.add(comment);
	}


	public void rem(int index)
	{
		if (index < comments.size())
			comments.remove(index);
	}


	public void place(int index, Comment comment)
	{
		if (comments.size() <= index)
			this.add(comment);

		else
		{
			if (!comments.get(index).equals(comment.getId()))
			{
				if (comments.contains(comment))
				{
					while (true)
					{
						if (comments.contains(comment) && comments.indexOf(comment) > index)
							comments.remove(index);
						else
							break;
					}
				}
				else
					comments.add(index, comment);
			}
		}
	}


	public int getSize()
	{
		return comments.size();
	}


	// not enable right now (buggy, et gourmand en ressources)
	public void replace(Comment comment, int toIndex)
	{
		int currIndex = this.getItems().indexOf(comment);
		if (toIndex == currIndex)
			return;

		if (toIndex > currIndex)
		{
			// crash
			if (toIndex >= this.getItems().size())
				this.getItems().add(comment);
			else
				this.getItems().set(toIndex, comment);
			this.getItems().remove(currIndex);
		}
		else
		{
			this.getItems().remove(currIndex);
			this.getItems().set(toIndex, comment);
		}
	}


	public Comment getItem(Comment search)
	{
		for (Comment post : this.getItems())
		{
			if (post.equals(search))
				return post;
		}

		return null;
	}


	public ItemView getView()
	{
		if (this.itemView == null)
			this.itemView = ItemView.of(BR.comment, R.layout.comment_row);

		return this.itemView;
	}


	public ObservableArrayList<Comment> getItems()
	{
		return comments;
	}
}
