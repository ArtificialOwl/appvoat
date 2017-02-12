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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import net.pr0npaganda.appvoat.adapters.CommentBindingAdapter;
import net.pr0npaganda.appvoat.api.Api;
import net.pr0npaganda.appvoat.api.ApiError;
import net.pr0npaganda.appvoat.api.ApiRequest;
import net.pr0npaganda.appvoat.db.AccountsDatabase;
import net.pr0npaganda.appvoat.db.AppvoatDatabase;
import net.pr0npaganda.appvoat.db.DatabaseManager;
import net.pr0npaganda.appvoat.dialogs.SimpleEntryDialogFragment;
import net.pr0npaganda.appvoat.interfaces.ApiRequestListener;
import net.pr0npaganda.appvoat.item.InterceptClickTextView;
import net.pr0npaganda.appvoat.model.Account;
import net.pr0npaganda.appvoat.model.Comment;
import net.pr0npaganda.appvoat.model.OpenLink;
import net.pr0npaganda.appvoat.model.Post;
import net.pr0npaganda.appvoat.model.Sub;
import net.pr0npaganda.appvoat.utils.AnimUtils;
import net.pr0npaganda.appvoat.utils.AppUtils;


public class ActivityBase extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, ApiRequestListener
{
	//	protected Core core;
	protected Api api;

	protected SharedPreferences pref;
	protected DrawerLayout          drawer = null;
	protected ActionBarDrawerToggle toggle = null;

	private NavigationView navView       = null;
	private LinearLayout   postingPanel1 = null;
	private String currentTheme;


	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);

		pref = PreferenceManager.getDefaultSharedPreferences(this);
		currentTheme = pref.getString("theme", "AppVoatLight");

		super.setTheme(AppUtils.getTheme(currentTheme));

		api = new Api(getBaseContext(), Core.get(), this);
	}


	@Override
	protected void onResume()
	{
		super.onResume();

		if (!currentTheme.equalsIgnoreCase(pref.getString("theme", "AppVoatLight")))
			recreate();

		DatabaseManager.initializeInstance(AppvoatDatabase.getInstance(getApplicationContext()));

		if (Core.get().getAccounts().getSize() == 0)
			AccountsDatabase.getAccounts(Core.get().getAccounts());

		manageAccounts();
	}


	private void manageAccounts()
	{
		if (navView == null)
			return;

		Menu menu = navView.getMenu();

		for (int i = 1; i < 100; i++)
			menu.removeItem(i);

		Core.get().setCurrentAccount(null);
		for (Account account : Core.get().getAccounts().getItems())
		{
			menu.add(R.id.group_accounts, account.getId(), 10, account.getUserName() + ((!account.isAuthed()) ? " (disconnected)" : ""))
					.setIcon(R.mipmap.icon_voat).setCheckable(true);
			if (account.isActive() && account.isAuthed())
			{
				Core.get().setCurrentAccount(account);
				navView.setCheckedItem(account.getId());
			}
		}

		if (Core.get().getCurrentAccount() != null)
			AppUtils.Log("token expires: " + Core.get().getCurrentAccount().getExpires() + "   (current: " + (System
					.currentTimeMillis() / 1000L) + ")");

		//	api.refreshToken(core.getCurrentAccount());

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

			Core.get().getOpenLink().setUrl(link);
			Context context = getBaseContext();
			Intent intent = new Intent(context, ActivityOpenLink.class);
			intent.putExtra("core", (Core) Core.get().clone());
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
			arguments.putSerializable("core", (Core) Core.get().clone());
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
			intent.putExtra("core", (Core) Core.get().clone());
			intent.putExtra("link", openLink);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
		}
	}


	//
	// posting
	//
	public void clickReplyClose(View v)
	{
		if (this.postingPanel1 == null)
			return;

		EditText eText = (EditText) this.postingPanel1.findViewById(R.id.edittext);
		eText.setText("");

		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(eText.getWindowToken(), 0);
		AnimUtils.displayView(this.postingPanel1, false, 600);
	}


	public void clickReplied(View v)
	{
		EditText eText = (EditText) this.postingPanel1.findViewById(R.id.edittext);
		if (eText.getTag() instanceof Post)
		{
			clickPostReplied(v);
		}

		if (eText.getTag() instanceof Comment)
		{
			clickCommentReplied(v);
		}

		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(this.postingPanel1.findViewById(R.id.edittext).getWindowToken(), 0);
		AnimUtils.displayView(this.postingPanel1, false, 600);
	}


	//
	// Posts
	//
	public void clickPostUpvoat(View v)
	{
		Post post = (Post) v.getTag();
		api.votingPost(post, 1);
	}


	public void clickPostDownvoat(View v)
	{
		Post post = (Post) v.getTag();
		api.votingPost(post, -1);
	}


	public void clickPostComment(View v)
	{
		if (this.postingPanel1 == null)
			return;

		AnimUtils.displayView(this.postingPanel1, true, 600);
		EditText eText = (EditText) this.postingPanel1.findViewById(R.id.edittext);
		eText.setText("");

		if (eText.requestFocus())
		{
			getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(eText, InputMethodManager.SHOW_IMPLICIT);
		}

		eText.setTag(v.getTag());
	}


	private void clickPostReplied(View v)
	{
		EditText eText = (EditText) this.postingPanel1.findViewById(R.id.edittext);

		Post post = (Post) eText.getTag();

		Comment comment = new Comment(post, -1);
		comment.setParentId(0);
		comment.setContent(eText.getText().toString());
		api.postingComment(comment);

	}


	public void clickPostCopy(View v)
	{
		notAvailableRightNow();
	}


	public void clickPostShare(View v)
	{
		notAvailableRightNow();
	}


	//
	// Comments
	//
	public void clickOptionsNull(View v)
	{
		CommentBindingAdapter.resetAllCommentsOptions((FrameLayout) v.getParent());
	}


	public void clickCommentUpvoat(View v)
	{
		Comment comment = (Comment) v.getTag();
		api.votingComment(comment, 1);
		clickOptionsNull((ViewGroup) v.getParent());
	}


	public void clickCommentDownvoat(View v)
	{
		Comment comment = (Comment) v.getTag();
		api.votingComment(comment, -1);
		clickOptionsNull((ViewGroup) v.getParent());
	}


	public void clickCommentReply(View v)
	{
		if (this.postingPanel1 == null)
			return;

		AnimUtils.displayView(this.postingPanel1, true, 600);
		//notAvailableRightNow();
		EditText eText = (EditText) this.postingPanel1.findViewById(R.id.edittext);
		eText.setText("");

		if (eText.requestFocus())
		{
			getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(eText, InputMethodManager.SHOW_IMPLICIT);
		}

		eText.setTag(v.getTag());
		clickOptionsNull((ViewGroup) v.getParent());
	}


	private void clickCommentReplied(View v)
	{
		EditText eText = (EditText) this.postingPanel1.findViewById(R.id.edittext);

		Comment parent = (Comment) eText.getTag();

		Comment comment = new Comment(parent.getPost(), -1);
		comment.setParentId(parent.getId());
		comment.setContent(eText.getText().toString());
		api.postingComment(comment);

	}


	public void clickCommentCopy(View v)
	{
		AppUtils.Log(". clickCommentCopy");
		notAvailableRightNow();
		clickOptionsNull((ViewGroup) v.getParent());
	}


	public void clickCommentShare(View v)
	{
		AppUtils.Log(". clickCommentShare");
		notAvailableRightNow();
		clickOptionsNull((ViewGroup) v.getParent());
	}


	public void clickComment(View v)
	{
		Comment comment = (Comment) v.getTag();
		if (comment == null)
			return;

		if (v.getAlpha() != 1f)
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


	protected void setPostingPanel(LinearLayout view)
	{
		this.postingPanel1 = view;
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
	public void onApiMessage(ApiRequest request)
	{
		if (drawer == null)
			return;

		Snackbar.make(drawer, request.getMessage(), Snackbar.LENGTH_LONG).setAction("Action", null).show();
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


	@Override
	public boolean onNavigationItemSelected(MenuItem item)
	{
		int id = item.getItemId();
		final Context context = getBaseContext();
		final Intent intent;
		switch (id)
		{
			case R.id.nav_create_account:
				goToAccountCreation();
				break;

			case R.id.nav_settings:
				intent = new Intent(context, ActivitySettings.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intent);
				break;

			case R.id.nav_gotosubverse:
				showGoToSubDialogFragment();
				break;

			default:
				navView.setCheckedItem(item.getItemId());
				AccountsDatabase.setAsActive(item.getItemId());

				Account selAccount = Core.get().getAccounts().getItem(id);
				if (selAccount != null && !Core.get().getAccounts().getItem(id).isAuthed())
				{
					goToAccountCreation();
					break;
				}

				Core.get().getAccounts().reset();

				if (this instanceof ActivityPostDetail)
					intent = new Intent(context, ActivityPostDetail.class);
				else if (this instanceof ActivityOpenLink)
					intent = new Intent(context, ActivityOpenLink.class);
				else
					intent = new Intent(context, ActivityPostList.class);

				intent.putExtra("core", (Core) Core.get().clone());
				intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				context.startActivity(intent);
				break;
		}

		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawer.closeDrawer(GravityCompat.START);

		return true;
	}


	protected void goToSub(Sub sub)
	{
		this.nextSub(sub);

		Context context = getBaseContext();
		Intent intent = new Intent(context, ActivityPostList.class);
		intent.putExtra("core", (Core) Core.get().clone());
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}


	protected void nextSub(Sub sub)
	{
		if (Core.get().getStackSubs().contains(sub))
			Core.get().getStackSubs().remove(sub);

		AppUtils.Log(" NEXTSUB: " + Core.get().getCurrentSub());
		if (Core.get().getCurrentSub() != null)
			Core.get().getStackSubs().add(Core.get().getCurrentSub());

		Core.get().setCurrentSub(sub);
	}

	protected boolean prevSub()
	{
		if (Core.get().getStackSubs().size() <= 0)
			return false;

		Core.get().getStackSubs().remove(Core.get().getCurrentSub());

		if (Core.get().getStackSubs().size() == 0)
			return false;

		Core.get().setCurrentSub(Core.get().getStackSubs().get(Core.get().getStackSubs().size() - 1));
		return true;
	}

	protected void showGoToSubDialogFragment()
	{
		FragmentManager fm = getSupportFragmentManager();
		SimpleEntryDialogFragment goToSubDialogFragmentDialogFragment = SimpleEntryDialogFragment
				.newInstance("GoToSubDialogFragment", "Go to a subverse");
		goToSubDialogFragmentDialogFragment.show(fm, "GoToSubDialogFragment");
	}


	private void goToAccountCreation()
	{

		//		if (multiPanel() > 0)
		//		{
		//			Bundle arguments = new Bundle();
		//			arguments.putSerializable("core", (Core) core.clone());
		//			FragmentOAuth fragment = new FragmentOAuth();
		//			fragment.setArguments(arguments);
		//
		//			int container = R.id.center_panel_container;
		//			if (multiPanel() == 3)
		//				container = R.id.right_panel_container;
		//
		//			getSupportFragmentManager().beginTransaction().replace(container, fragment).commit();
		//
		//			if (navView != null)
		//			{
		//				Handler handler = new Handler();
		//				handler.postDelayed(new Runnable()
		//				{
		//					@Override
		//					public void run()
		//					{
		//						navView.setCheckedItem(R.id.nav_voat_anon);
		//					}
		//				}, 50);
		//			}
		//		}
		//		else
		//		{
		final Intent intent = new Intent(getBaseContext(), ActivityOAuth.class);
		//	intent.putExtra("core", (Core)  Core.get().clone());
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		Handler handler = new Handler();
		handler.postDelayed(new Runnable()
		{
			@Override
			public void run()
			{
				getBaseContext().startActivity(intent);
			}
		}, 50);
		//		}
	}


	private void onGoToSubDialogValue(String sub)
	{
		goToSub(new Sub(Core.SOURCE_VOAT, sub));
	}


	private void notAvailableRightNow()
	{
		if (drawer == null)
			return;

		Snackbar.make(drawer, "Not Available Yet", Snackbar.LENGTH_LONG).setAction("Action", null).show();
	}
}
