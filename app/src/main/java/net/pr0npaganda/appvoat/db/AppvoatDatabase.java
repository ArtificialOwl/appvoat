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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import net.pr0npaganda.appvoat.utils.AppUtils;


public class AppvoatDatabase extends SQLiteOpenHelper
{

	public static final String TABLE_POSTS         = "posts";
	public static final String POSTS_COLUMN_ID     = "id";
	public static final String POSTS_COLUMN_SOURCE = "source";
	public static final String POSTS_COLUMN_POSTID = "postid";
	public static final String POSTS_COLUMN_READ   = "read";
	public static final String POSTS_COLUMN_LINK   = "link";

	private static final String DATABASE_NAME    = "appvoat.db";
	private static final int    DATABASE_VERSION = 1;
	private static AppvoatDatabase sInstance;

	private Context context;


	private AppvoatDatabase(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context;
	}


	public static synchronized AppvoatDatabase getInstance(Context context)
	{
		if (sInstance == null)
			sInstance = new AppvoatDatabase(context.getApplicationContext());
		return sInstance;
	}


	@Override
	public void onCreate(SQLiteDatabase database)
	{
		AppUtils.Log("--- create database");
		database.execSQL(String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s integer, %s INTEGER, %s INTEGER, %s INTEGER);", TABLE_POSTS, POSTS_COLUMN_ID, POSTS_COLUMN_SOURCE, POSTS_COLUMN_POSTID, POSTS_COLUMN_READ, POSTS_COLUMN_LINK));
	}


	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		AppUtils.Log("--- update database");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_POSTS);
		onCreate(db);
	}
}