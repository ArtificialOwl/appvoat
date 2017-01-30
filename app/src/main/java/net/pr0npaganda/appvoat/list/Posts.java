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
import net.pr0npaganda.appvoat.model.Post;
import net.pr0npaganda.appvoat.model.Sub;

import java.io.Serializable;

import me.tatarka.bindingcollectionadapter.ItemView;


public class Posts extends BaseObservable implements Serializable
{
	private transient ItemView itemView = ItemView.of(BR.post, R.layout.post_row);

	@Bindable
	private ObservableArrayList<Post> posts;


	public Posts()
	{
		this.posts = new ObservableArrayList<>();
	}


	public void reset()
	{
		this.posts.clear();
	}


	public void removeLoading()
	{
		for (int i = 0; i < this.getItems().size(); i++)
		{
			if (this.getItems().get(i).getType() == Post.TYPE_MORE)
				this.getItems().remove(i);
		}
	}


	public void insertLoading(Sub sub)
	{
		this.getItems().add(new Post(sub, 0).setType(Post.TYPE_MORE));
	}


	public void add(Post post)
	{
		this.posts.add(post);
	}


	public int getSize()
	{
		return posts.size();
	}


	public void replace(Post post, int toIndex)
	{
		int currIndex = this.getItems().indexOf(post);
		if (toIndex == currIndex)
			return;

		if (toIndex > currIndex)
		{
			if (toIndex > this.getItems().size())
				this.getItems().add(post);
			else
				this.getItems().set(toIndex, post);
			this.getItems().remove(currIndex);
		}
		else
		{
			this.getItems().remove(currIndex);
			this.getItems().set(toIndex, post);
		}
	}


	public ItemView getView()
	{
		if (this.itemView == null)
			this.itemView = ItemView.of(BR.post, R.layout.post_row);

		return this.itemView;
	}


	public Post getItem(Post search)
	{
		for (Post post : this.getItems())
		{
			if (post.equals(search))
				return post;
		}

		return null;
	}


	public ObservableArrayList<Post> getItems()
	{
		return posts;
	}
}
