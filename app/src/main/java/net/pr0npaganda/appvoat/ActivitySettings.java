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


package net.pr0npaganda.appvoat;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import net.pr0npaganda.appvoat.utils.AppUtils;

import java.util.List;


public class ActivitySettings extends PreferenceSettings
{

	private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener()
	{
		@Override
		public boolean onPreferenceChange(Preference preference, Object value)
		{
			String stringValue = value.toString();
			if (preference instanceof ListPreference)
			{
				ListPreference listPreference = (ListPreference) preference;
				int index = listPreference.findIndexOfValue(stringValue);

				preference.setSummary(index >= 0 ? listPreference.getEntries()[index] : null);
			}
			else
				preference.setSummary(stringValue);

			return true;
		}
	};
	private String currentTheme;


	private static boolean isXLargeTablet(Context context)
	{
		return (context.getResources()
				.getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
	}


	private static void bindPreferenceSummaryToValue(Preference preference)
	{
		if (preference == null)
			return;

		preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

		sBindPreferenceSummaryToValueListener.onPreferenceChange(preference, PreferenceManager
				.getDefaultSharedPreferences(preference.getContext()).getString(preference.getKey(), ""));
	}


	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.setTheme(AppUtils.getTheme(PreferenceManager.getDefaultSharedPreferences(this).getString("theme", "AppVoatLight")));
		super.onCreate(savedInstanceState);

		currentTheme = PreferenceManager.getDefaultSharedPreferences(this).getString("theme", "AppVoatLight");
		setupActionBar();
	}


	@Override
	protected void onResume()
	{
		super.onResume();

		if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && !currentTheme.equalsIgnoreCase(PreferenceManager
				                                                                                                       .getDefaultSharedPreferences(this)
				                                                                                                       .getString("theme", "AppVoatLight")))
		{
			recreate();
		}
	}


	private void setupActionBar()
	{
		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null)
			actionBar.setDisplayHomeAsUpEnabled(true);
	}


	@Override
	public boolean onIsMultiPane()
	{
		return isXLargeTablet(this);
	}


	@Override
	@TargetApi (Build.VERSION_CODES.HONEYCOMB)
	public void onBuildHeaders(List<PreferenceActivity.Header> target)
	{
		loadHeadersFromResource(R.xml.pref_headers, target);
	}


	protected boolean isValidFragment(String fragmentName)
	{
		return PreferenceFragment.class.getName().equals(fragmentName) || GeneralPreferenceFragment.class.getName().equals(fragmentName);
	}


	@TargetApi (Build.VERSION_CODES.HONEYCOMB)
	public static class GeneralPreferenceFragment extends PreferenceFragment
	{
		@Override
		public void onCreate(Bundle savedInstanceState)
		{
			super.onCreate(savedInstanceState);

			addPreferencesFromResource(R.xml.pref_general);
			setHasOptionsMenu(true);

			bindPreferenceSummaryToValue(findPreference("theme"));
		}


		@Override
		public boolean onOptionsItemSelected(MenuItem item)
		{
			int id = item.getItemId();
			if (id == android.R.id.home)
			{
				startActivity(new Intent(getActivity(), ActivitySettings.class));
				return true;
			}
			return super.onOptionsItemSelected(item);
		}
	}

}
