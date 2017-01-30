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

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.concurrent.atomic.AtomicInteger;


public class DatabaseManager
{

	private static DatabaseManager  instance;
	private static SQLiteOpenHelper mDatabaseHelper;
	private AtomicInteger mOpenCounter = new AtomicInteger();
	private SQLiteDatabase mDatabase;


	public static synchronized void initializeInstance(SQLiteOpenHelper helper)
	{
		if (instance == null)
		{
			instance = new DatabaseManager();
			mDatabaseHelper = helper;
		}
	}


	public static synchronized DatabaseManager getInstance()
	{
		if (instance == null)
		{
			throw new IllegalStateException("missing initializeInstance()");
		}

		return instance;
	}


	public synchronized SQLiteDatabase openDatabase()
	{
		if (mOpenCounter.incrementAndGet() == 1)
		{
			// Opening new database
			mDatabase = mDatabaseHelper.getWritableDatabase();
		}
		return mDatabase;
	}


	public synchronized void closeDatabase()
	{
		if (mOpenCounter.decrementAndGet() == 0)
		{
			// Closing database
			mDatabase.close();

		}
	}
}