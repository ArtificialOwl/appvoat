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


package net.pr0npaganda.appvoat.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import net.pr0npaganda.appvoat.Core;
import net.pr0npaganda.appvoat.api.Api;
import net.pr0npaganda.appvoat.api.ApiError;
import net.pr0npaganda.appvoat.api.ApiRequest;
import net.pr0npaganda.appvoat.db.AccountsDatabase;
import net.pr0npaganda.appvoat.db.AppvoatDatabase;
import net.pr0npaganda.appvoat.db.DatabaseManager;
import net.pr0npaganda.appvoat.interfaces.ApiRequestListener;
import net.pr0npaganda.appvoat.model.Account;
import net.pr0npaganda.appvoat.utils.AppUtils;


public class RefreshTokensService extends Service implements ApiRequestListener
{

	public static void start(Context context)
	{
		if (AppUtils.isServiceRunning(context, RefreshTokensService.class))
			return;

		Intent intent = new Intent(context, RefreshTokensService.class);
		PendingIntent pIntent = PendingIntent.getService(context, 0, intent, 0);

		AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), (900 * 1000), pIntent);
	}


	@Override
	public IBinder onBind(Intent intent)
	{
		throw new UnsupportedOperationException("not bindable");
	}


	@Override
	public void onCreate()
	{
		super.onCreate();
	}


	@Override
	public void onDestroy()
	{
		super.onDestroy();
	}


	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		DatabaseManager.initializeInstance(AppvoatDatabase.getInstance(getApplicationContext()));

	//	Core core = Core.get();
		Api api = new Api(getBaseContext(), Core.get(), this);
		AccountsDatabase.getAccounts(Core.get().getAccounts());
		for (Account account : Core.get().getAccounts().getItems())
		{
			if (account.needRefresh())
				api.refreshToken(account);
		}
		return super.onStartCommand(intent, flags, startId);
	}


	@Override
	public void onApiRequestCompleted(ApiRequest request, boolean isOver)
	{
	}


	@Override
	public void onApiRequestEmpty(int type)
	{
	}


	@Override
	public void onApiRequestError(ApiError apiError)
	{
		if (apiError.getCode() == ApiError.ERROR_INVALID_TOKEN)
			AccountsDatabase.resetToken(apiError.getRequest().getExtraInt("userid", 0));
	}


	@Override
	public void onApiMessage(ApiRequest request)
	{
	}

}


