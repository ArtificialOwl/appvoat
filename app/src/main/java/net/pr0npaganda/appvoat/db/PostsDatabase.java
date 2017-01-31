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


package net.pr0npaganda.appvoat.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import net.pr0npaganda.appvoat.model.Post;


public class PostsDatabase
{
	public static void setPostAsRead(Post post, int type)
	{
		String row = "";
		switch (type)
		{
			case Post.TYPE_TEXT:
				row = AppvoatDatabase.POSTS_COLUMN_READ;
				break;

			case Post.TYPE_LINK:
				row = AppvoatDatabase.POSTS_COLUMN_LINK;
				break;

		}

		if (row.equalsIgnoreCase(""))
			return;

		SQLiteDatabase database = DatabaseManager.getInstance().openDatabase();

		String select = String
				.format("SELECT %s, %s FROM %s WHERE %s=%d AND %s=%d LIMIT 0, 1", AppvoatDatabase.POSTS_COLUMN_ID, row, AppvoatDatabase.TABLE_POSTS, AppvoatDatabase.POSTS_COLUMN_SOURCE, post
						.getSub().source(), AppvoatDatabase.POSTS_COLUMN_POSTID, post.getId());

		String insert;
		Cursor cursor = database.rawQuery(select, null);
		if (cursor.getCount() == 0)
			insert = String
					.format("INSERT INTO %s (%s, %s, %s) VALUES (%d, %d, %d)", AppvoatDatabase.TABLE_POSTS, AppvoatDatabase.POSTS_COLUMN_SOURCE, AppvoatDatabase.POSTS_COLUMN_POSTID, row, post
							.getSub().source(), post.getId(), 1);
		else
		{
			cursor.moveToFirst();
			int id = cursor.getInt(cursor.getColumnIndex(AppvoatDatabase.POSTS_COLUMN_ID));
			insert = String
					.format("UPDATE %s SET %s=%d WHERE %s=%d", AppvoatDatabase.TABLE_POSTS, row, 1, AppvoatDatabase.POSTS_COLUMN_ID, id);
		}
		cursor.close();
		database.execSQL(insert);
	}


	public static boolean isPostRead(Post post, int type)
	{
		String row = "";
		switch (type)
		{
			case Post.TYPE_TEXT:
				row = AppvoatDatabase.POSTS_COLUMN_READ;
				break;

			case Post.TYPE_LINK:
				row = AppvoatDatabase.POSTS_COLUMN_LINK;
				break;
		}

		if (row.equalsIgnoreCase(""))
			return false;

		SQLiteDatabase database = DatabaseManager.getInstance().openDatabase();

		String select = String
				.format("SELECT %s, %s FROM %s WHERE %s=%d AND %s=%d AND %s=%d LIMIT 0, 1", AppvoatDatabase.POSTS_COLUMN_ID, row, AppvoatDatabase.TABLE_POSTS, AppvoatDatabase.POSTS_COLUMN_SOURCE, post
						.getSub().source(), AppvoatDatabase.POSTS_COLUMN_POSTID, post.getId(), row, 1);

		Cursor cursor = database.rawQuery(select, null);
		if (cursor.getCount() == 1)
		{
			cursor.close();
			return true;
		}

		cursor.close();
		return false;
	}
}
