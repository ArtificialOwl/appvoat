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

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;

import net.pr0npaganda.appvoat.databinding.ActivityOauthBinding;


public class ActivityOAuth extends ActivityBase implements NavigationView.OnNavigationItemSelectedListener
{
	protected ActivityOauthBinding binding = null;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);

		core = (Core) getIntent().getSerializableExtra("core");
		if (core == null)
		{
			Context context = getBaseContext();
			Intent intent = new Intent(context, ActivityPostList.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
		}

		binding = DataBindingUtil.setContentView(this, R.layout.activity_oauth);

		// tools
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		toolbar.setTitle("OAuth");

		this.setNavView(binding.includeNavView.navView);

		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.setVisibility(View.GONE);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
	}


	@Override
	protected void onResume()
	{
		super.onResume();

		if (((ViewGroup) findViewById(R.id.oauth_container)).getChildCount() == 0)
		{
			Bundle arguments = new Bundle();
			arguments.putSerializable("core", (Core) core.clone());
			FragmentOAuth fragment = new FragmentOAuth();
			fragment.setArguments(arguments);

			getSupportFragmentManager().beginTransaction().add(R.id.oauth_container, fragment).commit();
		}
	}

}