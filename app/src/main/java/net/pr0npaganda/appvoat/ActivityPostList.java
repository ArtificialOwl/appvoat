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
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.TableRow;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;

import net.pr0npaganda.appvoat.api.ApiError;
import net.pr0npaganda.appvoat.api.ApiRequest;
import net.pr0npaganda.appvoat.databinding.ActivityPostListBinding;
import net.pr0npaganda.appvoat.interfaces.ApiRequestListener;
import net.pr0npaganda.appvoat.list.Posts;
import net.pr0npaganda.appvoat.list.Subs;
import net.pr0npaganda.appvoat.model.Post;
import net.pr0npaganda.appvoat.model.Sub;
import net.pr0npaganda.appvoat.services.RefreshTokensService;
import net.pr0npaganda.appvoat.utils.AnimUtils;
import net.pr0npaganda.appvoat.utils.AppUtils;

import java.util.ArrayList;
import java.util.List;


public class ActivityPostList extends ActivityBase implements NavigationView.OnNavigationItemSelectedListener, ApiRequestListener
{

	protected ActivityPostListBinding binding = null;

	private boolean toggleIsHomeButton = false;
	private boolean noMorePosts        = false;
	private boolean populatingPosts    = true;

	private Subs      subs         = new Subs();
	private List<Sub> stackSubs    = new ArrayList<>();
	private Sub       displayedSub = null;
	private Posts     posts        = new Posts();


	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);


		// binding
		binding = DataBindingUtil.setContentView(this, R.layout.activity_post_list);


		// toolbar
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayShowTitleEnabled(false);


		// navigation panel
		drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		toggle = new ActionBarDrawerToggle(this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

		drawer.setDrawerListener(toggle);
		toggle.setDrawerIndicatorEnabled(true);
		toggle.syncState();

		getSupportActionBar().setDisplayShowHomeEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);


		// Set few View/Panel to ActivityBase
		this.setNavView(binding.includeNavView.navView);
		this.setPostingPanel(binding.includePost1panel.post1);


		// binding list posts
		binding.includePostList.setPosts(getPosts());
		binding.includePostList.postRecycler.setLayoutManager(new LinearLayoutManager(this));

		// binding spinner subs
		binding.setSubs(getSubs());
		binding.topSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
		{
			protected Adapter init = null;

			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
			{
				if (init != adapterView.getAdapter())
				{
					init = adapterView.getAdapter();
					return;
				}
				selectSub(view);
			}

			@Override
			public void onNothingSelected(AdapterView<?> adapterView)
			{
			}
		});


		// divider
		//	binding.includePostList.postRecycler.addItemDecoration(new DecorationSimpleDivider(getBaseContext()));


		// swipeRefresher
		binding.includePostList.postRefresher.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
		{
			@Override
			public void onRefresh()
			{
				binding.includePostList.fatalError.setVisibility(View.GONE);
				api.requestSubPosts(currentSub(), getPosts());
			}
		});
		binding.includePostList.postRefresher.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark);
		binding.includePostList.postRefresher.setProgressBackgroundColorSchemeResource(R.color.colorBackground);

		binding.includePostList.postRecycler.addOnScrollListener(new RecyclerView.OnScrollListener()
		{
			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy)
			{
				if (dy > 0) //check for scroll down
				{
					if (populatingPosts)
						return;

					LinearLayoutManager layout = (LinearLayoutManager) binding.includePostList.postRecycler.getLayoutManager();
					int visibleItemCount = layout.getChildCount();
					int totalItemCount = layout.getItemCount();
					int pastVisiblesItems = layout.findFirstVisibleItemPosition();
					{
						if (noMorePosts)
							return;

						if ((visibleItemCount + pastVisiblesItems) >= (totalItemCount - 3))
						{
							populatingPosts = true;
							api.requestMoreSubPosts(currentSub(), getPosts());
						}
					}
				}
			}
		});


		// floating button (new message)
		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.setVisibility(View.GONE);
		//		fab.setOnClickListener(new View.OnClickListener()
		//		{
		//			@Override
		//			public void onClick(View view)
		//			{
		//				Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
		//			}
		//		});

		AppUtils.Log("- device is a tablet: " + AppUtils.isTablet(getBaseContext()));
		AppUtils.Log("- device is in landscape mode: " + AppUtils.isLandscape(getBaseContext()));


		if (multiPanel() > 0)
		{
			binding.includePostList.centerPanelContainer.setVisibility(View.VISIBLE);
			if (multiPanel() == 2)
			{
				binding.includePostList.postRefresher.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 1f));
			}
			if (multiPanel() == 3)
			{
				binding.includePostList.postRefresher.getLayoutParams().width = AppUtils.pixelFromDp(getBaseContext(), 400);
				binding.includePostList.rightPanelContainer.setVisibility(View.VISIBLE);
			}
		}
		else
		{
			binding.includePostList.postRefresher
					.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, 1f));
		}

		RefreshTokensService.start(getBaseContext());
	}


	@Override
	protected void onResume()
	{
		super.onResume();


		if (getSubs().getSize() == 0)
		{
			binding.topSpinner.setAlpha(0f);
			binding.topSpinner.setVisibility(View.GONE);
			binding.includePostList.postRefresher.setRefreshing(true);
			api.requestSubList(Core.SOURCE_VOAT, getSubs());
		}


		if (binding.includePostList.postRecycler.getAdapter() != null)
			binding.includePostList.postRecycler.getAdapter().notifyDataSetChanged();


		// set current sub in dropdown list
		final int index = getSubs().getIndex(currentSub());
		if (index > -1)
		{
			AnimUtils.displayView(binding.topSpinner, true, 400);
			binding.topSpinner.post(new Runnable()
			{
				public void run()
				{
					binding.topSpinner.setSelection(index);
				}
			});
		}
	}


	@Override
	public void onApiRequestCompleted(ApiRequest request, boolean isOver)
	{
		if (request.getType() == ApiRequest.REQUEST_TYPE_SUB_LIST)
		{
			if (this.currentSub() == null)
			{
				Sub go = (Sub) getIntent().getSerializableExtra("goToSub");
				if (go != null)
					goToSub(go);
				else
					goToSub(getSubs().getDefault());
			}

			this.refreshSpinner();
			AnimUtils.displayView(binding.topSpinner, true, 400);
		}

		if (isOver)
		{
			binding.includePostList.postRefresher.setRefreshing(false);
			populatingPosts = false;
		}
	}


	@Override
	public void onApiRequestEmpty(int type)
	{
		if (type == ApiRequest.REQUEST_TYPE_SUB_POSTS)
			noMorePosts = true;
	}


	@Override
	public void onApiRequestError(ApiError error)
	{
		super.onApiRequestError(error);

		binding.includePostList.postRefresher.setRefreshing(false);
		if (error.getCode() == ApiError.ERROR_NO_PUBLIC_API || error.getCode() == ApiError.ERROR_INVALID_API)
		{
			binding.includePostList.fatalError.setVisibility(View.VISIBLE);
			binding.includePostList.fatalError.setText(error.getMessage());
		}
	}


	@Override
	protected void goToSub(Sub sub)
	{
		if (sub == null)
			return;

		if (sub.equals(displayedSub))
			return;

		this.nextSub(sub);
		refreshSpinner();

		displayedSub = sub;
		binding.includePostList.postRefresher.setRefreshing(true);
		getPosts().reset();
		api.requestSubPosts(currentSub(), getPosts());
	}


	public void clickMorePost(final View v)
	{
		AppUtils.Log(". clickMorePost");
	}


	public void selectSub(final View v)
	{
		Sub sub = (Sub) v.getTag();
		goToSub(sub);
	}


	public void clickSub(final View v)
	{
		selectSub(v);
	}


	public void clickDetails(View v)
	{
		TextView title = (TextView) v.findViewById(R.id.title);
		if (title == null)
			title = (TextView) ((ViewGroup) v.getParent()).findViewById(R.id.title);

		if (title != null)
			title.setTypeface(null, Typeface.NORMAL);

		Post post = (Post) v.getTag();
		Core.get().setCurrentPost(post);

		if (multiPanel() > 0)
		{
			Bundle arguments = new Bundle();
			FragmentPostDetail fragment = new FragmentPostDetail();
			fragment.setArguments(arguments);

			if (((ViewGroup) findViewById(R.id.center_panel_container)).getChildCount() == 0)
			{
				toggleIsHomeButton = true;
				AnimUtils.animateDrawerToggleIcon(true, drawer, toggle, getSupportActionBar());
			}

			getSupportFragmentManager().beginTransaction().replace(R.id.center_panel_container, fragment).addToBackStack(null).commit();
		}
		else
		{
			Context context = getBaseContext();
			Intent intent = new Intent(context, ActivityPostDetail.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
		}
	}


	public void clickThumb(View v)
	{

		Post post = (Post) v.getTag();
		if (post.getType() != Post.TYPE_LINK)
		{
			clickDetails(v);
			return;
		}

		Core.get().setCurrentPost(post);
		((RoundedImageView) v.findViewById(R.id.thumb)).setBorderColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimaryDark));

		if (multiPanel() > 0)
		{
			Bundle arguments = new Bundle();

			FragmentOpenLink fragment = new FragmentOpenLink();
			fragment.setArguments(arguments);

			int container = R.id.center_panel_container;
			if (multiPanel() == 3)
				container = R.id.right_panel_container;

			if (((ViewGroup) findViewById(container)).getChildCount() == 0)
			{
				toggleIsHomeButton = true;
				AnimUtils.animateDrawerToggleIcon(true, drawer, toggle, getSupportActionBar());
			}
			getSupportFragmentManager().beginTransaction().replace(container, fragment).addToBackStack(null).commit();
		}
		else
		{
			Context context = getBaseContext();
			Intent intent = new Intent(context, ActivityOpenLink.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
		}
	}


	protected void nextSub(Sub sub)
	{
		if (this.stackSubs.contains(sub))
			this.stackSubs.remove(sub);

		if (sub != null && getSubs().getItem(sub) == null)
			getSubs().add(sub);

		this.stackSubs.add(sub);
	}


	protected boolean prevSub()
	{
		if (this.stackSubs.size() <= 1)
			return false;

		this.stackSubs.remove(this.stackSubs.size() - 1);
		return true;
	}


	protected Sub currentSub()
	{
		if (this.stackSubs.size() == 0)
			return null;

		return this.stackSubs.get(this.stackSubs.size() - 1);
	}


	private Subs getSubs()
	{
		return this.subs;
	}


	public Posts getPosts()
	{
		return this.posts;
	}


	@Override
	public void onBackPressed()
	{
		if (drawer != null && drawer.isDrawerOpen(GravityCompat.START))
		{
			drawer.closeDrawer(GravityCompat.START);
		}
		else if (this.prevSub())
		{
			goToSub(this.currentSub());
		}
		else
		{
			super.onBackPressed();
		}
	}


	private void refreshSpinner()
	{
		int index = getSubs().getIndex(currentSub());
		if (index > -1)
			binding.topSpinner.setSelection(index);
	}


	public boolean onOptionsItemSelected(MenuItem item)
	{
		if (toggleIsHomeButton || drawer == null)
			super.onBackPressed();
		else
			drawer.openDrawer(GravityCompat.START);

		return super.onOptionsItemSelected(item);
	}

}