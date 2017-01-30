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
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import net.pr0npaganda.appvoat.api.Api;
import net.pr0npaganda.appvoat.api.ApiError;
import net.pr0npaganda.appvoat.db.AppvoatDatabase;
import net.pr0npaganda.appvoat.db.DatabaseManager;
import net.pr0npaganda.appvoat.dialogs.SimpleEntryDialogFragment;
import net.pr0npaganda.appvoat.interfaces.ApiRequestListener;
import net.pr0npaganda.appvoat.item.InterceptClickTextView;
import net.pr0npaganda.appvoat.model.Comment;
import net.pr0npaganda.appvoat.model.OpenLink;
import net.pr0npaganda.appvoat.model.Sub;
import net.pr0npaganda.appvoat.utils.AppUtils;


public class ActivityBase extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, ApiRequestListener
{
	protected Core core;
	protected Api  api;

	protected SharedPreferences pref;
	protected DrawerLayout          drawer = null;
	protected ActionBarDrawerToggle toggle = null;

	private NavigationView navView = null;
	private String currentTheme;


	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);

		pref = PreferenceManager.getDefaultSharedPreferences(this);
		currentTheme = pref.getString("theme", "AppVoatLight");

		super.setTheme(AppUtils.getTheme(currentTheme));
	}


	@Override
	protected void onResume()
	{
		super.onResume();

		if (!currentTheme.equalsIgnoreCase(pref.getString("theme", "AppVoatLight")))
			recreate();

		DatabaseManager.initializeInstance(AppvoatDatabase.getInstance(getApplicationContext()));
		api = new Api(getBaseContext(), this);
	}


	public SharedPreferences getPref()
	{
		return pref;
	}


	public void clickPostContent(View v)
	{
		if (v instanceof InterceptClickTextView && ((InterceptClickTextView) v).getLinkTo() != "")
		{
			String link = ((InterceptClickTextView) v).getLinkTo();
			((InterceptClickTextView) v).linkTo("");

			core.getOpenLink().setUrl(link);
			Context context = getBaseContext();
			Intent intent = new Intent(context, ActivityOpenLink.class);
			intent.putExtra("core", (Core) core.clone());
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
		}
	}


	public void openLink(String url)
	{
		OpenLink openLink = new OpenLink();
		openLink.setUrl(url);

		if (multiPanel() > 0)
		{
			Bundle arguments = new Bundle();
			arguments.putSerializable("core", (Core) core.clone());
			arguments.putSerializable("link", openLink);
			FragmentOpenLink fragment = new FragmentOpenLink();
			fragment.setArguments(arguments);

			int container = R.id.center_panel_container;
			if (multiPanel() == 3)
				container = R.id.right_panel_container;

			getSupportFragmentManager().beginTransaction().replace(container, fragment).addToBackStack(null).commit();
		}
		else
		{
			Context context = getBaseContext();
			Intent intent = new Intent(context, ActivityOpenLink.class);
			intent.putExtra("core", (Core) core.clone());
			intent.putExtra("link", openLink);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
		}
	}


	public void clickComment(View v)
	{

		Comment comment = (Comment) v.getTag();
		if (comment == null)
			return;

		if (comment.getType() == Comment.COMMENT_LOAD_MORE_SUBCOMMENTS)
		{
			api.requestMoreComment(comment);
			v.setEnabled(false);
		}
		else if (comment.getType() == Comment.COMMENT_DISPLAY_COMMENTS)
		{
			api.displaySubComments(comment.getPost().getBaseComment(comment.getParentId()), true);
		}
		else if (v instanceof InterceptClickTextView && ((InterceptClickTextView) v).getLinkTo() != "")
		{
			String link = ((InterceptClickTextView) v).getLinkTo();
			((InterceptClickTextView) v).linkTo("");

			openLink(link);
		}
		else
		{
			api.displaySubComments(comment, !(comment.isSubCommentsDisplayed()));
		}
	}


	public int multiPanel()
	{
		if (!AppUtils.isTablet(getBaseContext()))
			return 0;

		if (AppUtils.isLandscape(getBaseContext()))
		{
			if (getPref().getBoolean("3panel", false))
				return 3;
			else
				return 2;
		}

		if (getPref().getBoolean("2panel", false))
			return 2;

		return 0;
	}


	protected void setNavView(NavigationView view)
	{
		this.navView = view;

		// binding nav so it join v/appvoat (not clean, mais pas d'autres solutions pour le moment
		navView.getHeaderView(0).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				goToSub(new Sub(Core.SOURCE_VOAT, "appvoat"));
				DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
				drawer.closeDrawer(GravityCompat.START);
				Snackbar.make(view, "Welcome on v/appvoat. The place to report bugs, request features and share experience", Snackbar.LENGTH_LONG)
						.setAction("Action", null).show();
			}
		});

		navView.setNavigationItemSelectedListener(this);
		navView.setItemIconTintList(null);
		navView.setCheckedItem(R.id.nav_voat_anon);
		navView.getMenu().getItem(1).setChecked(false);
	}


	@Override
	public void onApiRequestCompleted(boolean isOver)
	{
	}


	@Override
	public void onApiRequestEmpty(int type)
	{
	}


	@Override
	public void onApiRequestError(ApiError error)
	{
		if (drawer != null)
			Snackbar.make(drawer, error.getMessage(), Snackbar.LENGTH_LONG).setAction("Action", null).show();
	}


	public void onSimpleEntryDialogValue(String tag, String value)
	{
		if (tag.equals("GoToSubDialogFragment"))
			onGoToSubDialogValue(value);
	}


	@SuppressWarnings ("StatementWithEmptyBody")
	@Override
	public boolean onNavigationItemSelected(MenuItem item)
	{
		int id = item.getItemId();
		Context context = getBaseContext();
		Intent intent;
		switch (id)
		{
			case R.id.nav_voat_anon:
				break;

			case R.id.nav_create_account:
				intent = new Intent(context, ActivityOAuth.class);
				intent.putExtra("core", core);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intent);
				if (navView != null)
				{
					Handler handler = new Handler();
					handler.postDelayed(new Runnable()
					{

						@Override
						public void run()
						{
							navView.setCheckedItem(R.id.nav_voat_anon);
						}

					}, 50);
				}
				break;

			case R.id.nav_settings:
				intent = new Intent(context, ActivitySettings.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intent);
				break;

			case R.id.nav_gotosubverse:
				showGoToSubDialogFragment();
				break;
		}

		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawer.closeDrawer(GravityCompat.START);

		return true;
	}


	protected void goToSub(Sub sub)
	{
		if (this instanceof ActivityPostList && core.getCurrentSub().getKeyname().equalsIgnoreCase(sub.getKeyname()))
			return;

		Context context = getBaseContext();
		Intent intent = new Intent(context, ActivityPostList.class);
		core.setCurrentSub(new Sub(Core.SOURCE_VOAT, sub.getKeyname()));
		intent.putExtra("core", (Core) core.clone());
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}


	protected void showGoToSubDialogFragment()
	{
		FragmentManager fm = getSupportFragmentManager();
		SimpleEntryDialogFragment goToSubDialogFragmentDialogFragment = SimpleEntryDialogFragment
				.newInstance("GoToSubDialogFragment", "Go to a subverse");
		goToSubDialogFragmentDialogFragment.show(fm, "GoToSubDialogFragment");
	}


	private void onGoToSubDialogValue(String sub)
	{
		goToSub(new Sub(Core.SOURCE_VOAT, sub));
	}
}
