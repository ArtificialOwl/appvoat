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


public class AppvoatDatabase extends SQLiteOpenHelper
{

	public static final String TABLE_POSTS      = "posts";
	public static final String TABLE_ACCOUNTS   = "accounts";
	public static final String TABLE_ACC_TOKENS = "accounts_tokens";

	public static final String POSTS_COLUMN_ID     = "id";
	public static final String POSTS_COLUMN_SOURCE = "source";
	public static final String POSTS_COLUMN_POSTID = "postid";
	public static final String POSTS_COLUMN_READ   = "read";
	public static final String POSTS_COLUMN_LINK   = "link";
	public static final String POSTS_COLUMN_USERID = "userid";

	public static final String ACCOUNTS_COLUMN_ID       = "id";
	public static final String ACCOUNTS_COLUMN_SOURCE   = "source";
	public static final String ACCOUNTS_COLUMN_USERNAME = "username";
	public static final String ACCOUNTS_COLUMN_ACTIVE   = "active";

	public static final String ACC_TOKENS_COLUMN_USERID      = "userid";
	public static final String ACC_TOKENS_COLUMN_TOKEN       = "token";
	public static final String ACC_TOKENS_COLUMN_REFRESH     = "refresh";
	public static final String ACC_TOKENS_COLUMN_EXPIRES     = "expires";
	public static final String ACC_TOKENS_COLUMN_REFRESHTIME = "refreshtime";

	private static final String DATABASE_NAME    = "appvoat.db";
	private static final int    DATABASE_VERSION = 3;

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
		//		AppUtils.Log("--- create database");
		database.execSQL(String.format(
				"CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s integer, %s INTEGER, %s INTEGER, %s INTEGER, %s INTEGER);",
				TABLE_POSTS,
				POSTS_COLUMN_ID,
				POSTS_COLUMN_SOURCE,
				POSTS_COLUMN_POSTID,
				POSTS_COLUMN_READ,
				POSTS_COLUMN_LINK,
				POSTS_COLUMN_USERID));
		database.execSQL(String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s integer, %s text, %s integer);",
		                               TABLE_ACCOUNTS,
		                               ACCOUNTS_COLUMN_ID,
		                               ACCOUNTS_COLUMN_SOURCE,
		                               ACCOUNTS_COLUMN_USERNAME,
		                               ACCOUNTS_COLUMN_ACTIVE));
		database.execSQL(String.format("CREATE TABLE %s (%s integer, %s text, %s integer, %s text, %s integer);",
		                               TABLE_ACC_TOKENS,
		                               ACC_TOKENS_COLUMN_USERID,
		                               ACC_TOKENS_COLUMN_TOKEN,
		                               ACC_TOKENS_COLUMN_REFRESH,
		                               ACC_TOKENS_COLUMN_EXPIRES,
		                               ACC_TOKENS_COLUMN_REFRESHTIME));
	}


	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		//		AppUtils.Log("--- save database");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_POSTS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACCOUNTS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACC_TOKENS);

		onCreate(db);
	}
}