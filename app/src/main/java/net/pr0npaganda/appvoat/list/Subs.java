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
import net.pr0npaganda.appvoat.model.Sub;

import java.io.Serializable;

import me.tatarka.bindingcollectionadapter.ItemView;


public class Subs extends BaseObservable implements Serializable
{
	private transient ItemView itemView = ItemView.of(BR.sub, R.layout.row_sub_spinner);

	@Bindable
	private ObservableArrayList<Sub> subs;


	public Subs()
	{
		this.subs = new ObservableArrayList<>();
	}


	public void reset()
	{
		this.subs.clear();
	}


	public void add(Sub sub)
	{
		if (this.getItem(sub) == null)
			this.getItems().add(sub);
	}


	public void addDivider()
	{
		Sub sub = this.getLastItem();
		if (sub == null)
			return;
		sub.divider(true);
	}


	public void replace(Sub sub)
	{
		int index = this.getItems().indexOf(sub);
		this.getItems().set(index, sub);
		//		this.subs.add(sub);
		//		this.subs.set()
	}


	public int getSize()
	{
		return subs.size();
	}


	public ItemView getView()
	{
		if (this.itemView == null)
			this.itemView = ItemView.of(BR.sub, R.layout.row_sub_spinner);

		return this.itemView;
	}


	public Sub getItem(Sub search)
	{
		for (Sub sub : this.getItems())
		{
			if (sub.equals(search))
				return sub;
		}

		return null;
	}


	public Sub getLastItem()
	{
		if (this.getSize() > 0)
			return this.getItems().get(this.getSize() - 1);
		return null;
	}


	public ObservableArrayList<Sub> getItems()
	{
		return subs;
	}

}
