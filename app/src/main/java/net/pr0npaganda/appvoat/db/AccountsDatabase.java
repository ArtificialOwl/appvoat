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
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import net.pr0npaganda.appvoat.Core;
import net.pr0npaganda.appvoat.model.Account;


public class AccountsDatabase
{

	public static Account getAccount(int source, String username)
	{
		return getAccount(source, username, true);
	}


	public static void refreshAccounts()
	{
		Core.get().getAccounts().reset();

		SQLiteDatabase database = DatabaseManager.getInstance().openDatabase();
		String select = String.format(
				"SELECT a.%s, a.%s, a.%s, a.%s, at.%s, at.%s, at.%s, at.%s FROM %s AS a, %s AS at WHERE a.%s=at.%s ORDER " + "BY " +
						"a.%s, a.%s",
				AppvoatDatabase.ACCOUNTS_COLUMN_ID,
				AppvoatDatabase.ACCOUNTS_COLUMN_SOURCE,
				AppvoatDatabase.ACCOUNTS_COLUMN_USERNAME,
				AppvoatDatabase.ACCOUNTS_COLUMN_ACTIVE,
				AppvoatDatabase.ACC_TOKENS_COLUMN_TOKEN,
				AppvoatDatabase.ACC_TOKENS_COLUMN_REFRESH,
				AppvoatDatabase.ACC_TOKENS_COLUMN_EXPIRES,
				AppvoatDatabase.ACC_TOKENS_COLUMN_REFRESHTIME,
				AppvoatDatabase.TABLE_ACCOUNTS,
				AppvoatDatabase.TABLE_ACC_TOKENS,
				AppvoatDatabase.ACCOUNTS_COLUMN_ID,
				AppvoatDatabase.ACC_TOKENS_COLUMN_USERID,
				AppvoatDatabase.ACCOUNTS_COLUMN_SOURCE,
				AppvoatDatabase.ACCOUNTS_COLUMN_USERNAME);

		Cursor cursor = database.rawQuery(select, null);
		while (cursor.moveToNext())
		{
			Account account = new Account(cursor.getInt(cursor.getColumnIndex(AppvoatDatabase.ACCOUNTS_COLUMN_ID)),
			                              cursor.getInt(cursor.getColumnIndex(AppvoatDatabase.ACCOUNTS_COLUMN_SOURCE)),
			                              cursor.getString(cursor.getColumnIndex(AppvoatDatabase.ACCOUNTS_COLUMN_USERNAME)));
			account.setActive(cursor.getInt(cursor.getColumnIndex(AppvoatDatabase.ACCOUNTS_COLUMN_ACTIVE)) == 1);
			account.setToken(cursor.getString(cursor.getColumnIndex(AppvoatDatabase.ACC_TOKENS_COLUMN_TOKEN)));
			account.setTokenRefresh(cursor.getString(cursor.getColumnIndex(AppvoatDatabase.ACC_TOKENS_COLUMN_REFRESH)));
			account.setExpires(cursor.getLong(cursor.getColumnIndex(AppvoatDatabase.ACC_TOKENS_COLUMN_EXPIRES)));
			account.setRefreshTime(cursor.getLong(cursor.getColumnIndex(AppvoatDatabase.ACC_TOKENS_COLUMN_REFRESHTIME)));

			Core.get().getAccounts().add(account);
		}

		cursor.close();
	}


	public static Account getAccount(int source, String username, boolean create)
	{
		SQLiteDatabase database = DatabaseManager.getInstance().openDatabase();

		Account account = null;
		String select = String.format("SELECT %s, %s FROM %s WHERE %s=%d AND %s=%s LIMIT 0, 1",
		                              AppvoatDatabase.ACCOUNTS_COLUMN_ID,
		                              AppvoatDatabase.ACCOUNTS_COLUMN_USERNAME,
		                              AppvoatDatabase.TABLE_ACCOUNTS,
		                              AppvoatDatabase.ACCOUNTS_COLUMN_SOURCE,
		                              source,
		                              AppvoatDatabase.ACCOUNTS_COLUMN_USERNAME,
		                              DatabaseUtils.sqlEscapeString(username));

		Cursor cursor = database.rawQuery(select, null);
		if (cursor.getCount() == 1)
		{
			cursor.moveToFirst();
			account = new Account(cursor.getInt(cursor.getColumnIndex(AppvoatDatabase.ACCOUNTS_COLUMN_ID)),
			                      source,
			                      cursor.getString(cursor.getColumnIndex(AppvoatDatabase.ACCOUNTS_COLUMN_USERNAME)));
		}
		else if (create)
		{
			createAccount(source, username);
			account = getAccount(source, username, false);
		}

		cursor.close();
		return account;
	}


	public static Account getToken(int userid)
	{
		Account account = null;
		SQLiteDatabase database = DatabaseManager.getInstance().openDatabase();

		String select = String.format(
				"SELECT a.%s, a.%s, a.%s, a.%s, at.%s, at.%s, at.%s, at.%s FROM %s AS a, %s AS at WHERE a.%s=at.%s AND a.%s=%d",
				AppvoatDatabase.ACCOUNTS_COLUMN_ID,
				AppvoatDatabase.ACCOUNTS_COLUMN_SOURCE,
				AppvoatDatabase.ACCOUNTS_COLUMN_USERNAME,
				AppvoatDatabase.ACCOUNTS_COLUMN_ACTIVE,
				AppvoatDatabase.ACC_TOKENS_COLUMN_TOKEN,
				AppvoatDatabase.ACC_TOKENS_COLUMN_REFRESH,
				AppvoatDatabase.ACC_TOKENS_COLUMN_EXPIRES,
				AppvoatDatabase.ACC_TOKENS_COLUMN_REFRESHTIME,
				AppvoatDatabase.TABLE_ACCOUNTS,
				AppvoatDatabase.TABLE_ACC_TOKENS,
				AppvoatDatabase.ACCOUNTS_COLUMN_ID,
				AppvoatDatabase.ACC_TOKENS_COLUMN_USERID,
				AppvoatDatabase.ACCOUNTS_COLUMN_ID,
				userid);

		Cursor cursor = database.rawQuery(select, null);
		if (cursor.getCount() == 1)
		{
			cursor.moveToFirst();
			account = new Account(cursor.getInt(cursor.getColumnIndex(AppvoatDatabase.ACCOUNTS_COLUMN_ID)),
			                      cursor.getInt(cursor.getColumnIndex(AppvoatDatabase.ACCOUNTS_COLUMN_SOURCE)),
			                      cursor.getString(cursor.getColumnIndex(AppvoatDatabase.ACCOUNTS_COLUMN_USERNAME)));
			account.setActive(cursor.getInt(cursor.getColumnIndex(AppvoatDatabase.ACCOUNTS_COLUMN_ACTIVE)) == 1);
			account.setToken(cursor.getString(cursor.getColumnIndex(AppvoatDatabase.ACC_TOKENS_COLUMN_TOKEN)));
			account.setTokenRefresh(cursor.getString(cursor.getColumnIndex(AppvoatDatabase.ACC_TOKENS_COLUMN_REFRESH)));
			account.setExpires(cursor.getLong(cursor.getColumnIndex(AppvoatDatabase.ACC_TOKENS_COLUMN_EXPIRES)));
			account.setRefreshTime(cursor.getLong(cursor.getColumnIndex(AppvoatDatabase.ACC_TOKENS_COLUMN_REFRESHTIME)));
		}

		cursor.close();
		return account;
	}


	private static void createAccount(int source, String username)
	{
		SQLiteDatabase database = DatabaseManager.getInstance().openDatabase();
		String insert = String.format("INSERT INTO %s (%s, %s) VALUES (%d, %s)",
		                              AppvoatDatabase.TABLE_ACCOUNTS,
		                              AppvoatDatabase.ACCOUNTS_COLUMN_SOURCE,
		                              AppvoatDatabase.ACCOUNTS_COLUMN_USERNAME,
		                              source,
		                              DatabaseUtils.sqlEscapeString(username));
		database.execSQL(insert);
	}


	public static void save(Account account)
	{
		SQLiteDatabase database = DatabaseManager.getInstance().openDatabase();

		String select;

		// We check that there is an entry for this account in TABLE_ACCOUNTS
		select = String.format("SELECT %s FROM %s WHERE %s=%d AND %s=%d LIMIT 0, 1",
		                       AppvoatDatabase.ACCOUNTS_COLUMN_ID,
		                       AppvoatDatabase.TABLE_ACCOUNTS,
		                       AppvoatDatabase.ACCOUNTS_COLUMN_SOURCE,
		                       account.getSource(),
		                       AppvoatDatabase.ACCOUNTS_COLUMN_ID,
		                       account.getId());

		Cursor cursor = database.rawQuery(select, null);
		if (cursor.getCount() == 0)
		{
			cursor.close();
			return;
		}

		select = String.format("SELECT %s FROM %s WHERE %s=%d LIMIT 0, 1",
		                       AppvoatDatabase.ACC_TOKENS_COLUMN_USERID,
		                       AppvoatDatabase.TABLE_ACC_TOKENS,
		                       AppvoatDatabase.ACC_TOKENS_COLUMN_USERID,
		                       account.getId());

		cursor = database.rawQuery(select, null);

		String insert;

		if (cursor.getCount() == 0)
			insert = String.format("INSERT INTO %s (%s, %s, %s, %s, %s) VALUES (%d, %s, %s, %d, %d)",
			                       AppvoatDatabase.TABLE_ACC_TOKENS,
			                       AppvoatDatabase.ACC_TOKENS_COLUMN_USERID,
			                       AppvoatDatabase.ACC_TOKENS_COLUMN_TOKEN,
			                       AppvoatDatabase.ACC_TOKENS_COLUMN_REFRESH,
			                       AppvoatDatabase.ACC_TOKENS_COLUMN_EXPIRES,
			                       AppvoatDatabase.ACC_TOKENS_COLUMN_REFRESHTIME,
			                       account.getId(),
			                       DatabaseUtils.sqlEscapeString(account.getToken()),
			                       DatabaseUtils.sqlEscapeString(account.getTokenRefresh()),
			                       account.getExpires(),
			                       account.getRefreshTime());
		else
			insert = String.format("UPDATE %s SET %s=%s, %s=%s, %s=%d, %s=%d WHERE %s=%d",
			                       AppvoatDatabase.TABLE_ACC_TOKENS,
			                       AppvoatDatabase.ACC_TOKENS_COLUMN_TOKEN,
			                       DatabaseUtils.sqlEscapeString(account.getToken()),
			                       AppvoatDatabase.ACC_TOKENS_COLUMN_REFRESH,
			                       DatabaseUtils.sqlEscapeString(account.getTokenRefresh()),
			                       AppvoatDatabase.ACC_TOKENS_COLUMN_EXPIRES,
			                       account.getExpires(),
			                       AppvoatDatabase.ACC_TOKENS_COLUMN_REFRESHTIME,
			                       account.getRefreshTime(),
			                       AppvoatDatabase.ACC_TOKENS_COLUMN_USERID,
			                       account.getId());

		cursor.close();

		database.execSQL(insert);
	}


	public static void setAsActive(Account account)
	{
		setAsActive(account.getId());
	}


	public static void resetToken(int userid)
	{
		SQLiteDatabase database = DatabaseManager.getInstance().openDatabase();
		String update = String.format("UPDATE %s SET %s=0, %s='' WHERE %s=%d",
		                              AppvoatDatabase.TABLE_ACC_TOKENS,
		                              AppvoatDatabase.ACC_TOKENS_COLUMN_EXPIRES,
		                              AppvoatDatabase.ACC_TOKENS_COLUMN_REFRESH,
		                              AppvoatDatabase.ACC_TOKENS_COLUMN_USERID,
		                              userid);
		database.execSQL(update);
	}


	public static void setAsActive(int id)
	{
		String update;
		SQLiteDatabase database = DatabaseManager.getInstance().openDatabase();
		update = String.format("UPDATE %s SET %s=0", AppvoatDatabase.TABLE_ACCOUNTS, AppvoatDatabase.ACCOUNTS_COLUMN_ACTIVE);
		database.execSQL(update);
		update = String.format("UPDATE %s SET %s=1 WHERE %s=%d",
		                       AppvoatDatabase.TABLE_ACCOUNTS,
		                       AppvoatDatabase.ACCOUNTS_COLUMN_ACTIVE,
		                       AppvoatDatabase.ACCOUNTS_COLUMN_ID,
		                       id);
		database.execSQL(update);
	}

	//	public static boolean addToken(JSONObject json)
	//	{
	//		if (row.equalsIgnoreCase(""))
	//			return false;
	//
	//		SQLiteDatabase database = DatabaseManager.getInstance().openDatabase();
	//
	//		String select = String
	//				.format("SELECT %s, %s FROM %s WHERE %s=%d AND %s=%d AND %s=%d LIMIT 0, 1", AppvoatDatabase.POSTS_COLUMN_ID, row, AppvoatDatabase.TABLE_POSTS, AppvoatDatabase.POSTS_COLUMN_SOURCE, post
	//						.getSub().source(), AppvoatDatabase.POSTS_COLUMN_POSTID, post.getId(), row, 1);
	//
	//		Cursor cursor = database.rawQuery(select, null);
	//		if (cursor.getCount() == 1)
	//		{
	//			cursor.close();
	//			return true;
	//		}
	//
	//		cursor.close();
	//		return false;
	//		return true;
	//	}
}
