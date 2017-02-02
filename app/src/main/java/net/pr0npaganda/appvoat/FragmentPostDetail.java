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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.pr0npaganda.appvoat.api.Api;
import net.pr0npaganda.appvoat.api.ApiError;
import net.pr0npaganda.appvoat.api.ApiRequest;
import net.pr0npaganda.appvoat.databinding.PostDetailBinding;
import net.pr0npaganda.appvoat.db.PostsDatabase;
import net.pr0npaganda.appvoat.interfaces.ApiRequestListener;
import net.pr0npaganda.appvoat.model.Post;
import net.pr0npaganda.appvoat.utils.AnimUtils;


public class FragmentPostDetail extends Fragment implements ApiRequestListener
{
	private Api api;

	private Core core = null;
	private PostDetailBinding binding;

	private LinearLayoutManager layoutManager;
	private boolean populatingComments = false;


	public FragmentPostDetail()
	{
	}


	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		if (!getArguments().containsKey("core"))
		{
			Context context = getContext();
			Intent intent = new Intent(context, ActivityPostList.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
			return;
		}

		core = (Core) getArguments().getSerializable("core");
		api = new Api(getContext(), core, this);

		Activity activity = this.getActivity();
		Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
		if (toolbar != null)
		{
		}

	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// binding
		binding = DataBindingUtil.inflate(inflater, R.layout.post_detail, container, false);
		View rootView = binding.getRoot();

		binding.setPost(core.getCurrentPost());
		binding.setComments(core.getCurrentPost().getComments());
		binding.setFragment(this);

		layoutManager = new LinearLayoutManager(inflater.getContext());
		binding.detailComments.setLayoutManager(layoutManager);
		binding.detailComments.setNestedScrollingEnabled(false);

		binding.detailScroll.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener()
		{
			@Override
			public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY)
			{
				if (populatingComments)
					return;

				View view = v.getChildAt(v.getChildCount() - 1);
				int diff = (view.getBottom() - (v.getHeight() + v.getScrollY()));
				if (core.getCurrentPost().getMoreComment() != null && diff < 1000)
				{
					populatingComments = true;
					api.requestMoreComment(core.getCurrentPost().getMoreComment());
				}
			}
		});
		binding.detailScroll.setSmoothScrollingEnabled(true);

		// divider
		//		binding.detailComments.addItemDecoration(new DecorationSimpleDivider(inflater.getContext()));

		// swipe refresher
		//		binding.postRefresher.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark);
		//		binding.postRefresher.setProgressBackgroundColorSchemeResource(R.color.colorBackground);
		//		//	swipeRefresher = (SwipeRefreshLayout) findViewById(R.id.post_refresher);
		//		binding.postRefresher.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
		//		{
		//			@Override
		//			public void onRefresh()
		//			{
		//		//		api.requestComments(core.getCurrentPost());
		//			}
		//		});

		return rootView;
	}


	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);

		api.requestComments(core.getCurrentPost());
		PostsDatabase.setPostAsRead(core.getCurrentPost(), core.getCurrentAccount(), Post.TYPE_TEXT);
	}


	@Override
	public void onApiRequestCompleted(ApiRequest request, boolean isOver)
	{
		AnimUtils.displayView(binding.progressBar, false, 500);
		populatingComments = false;
	}


	@Override
	public void onApiRequestEmpty(int type)
	{
	}


	@Override
	public void onApiRequestError(ApiError error)
	{
		AnimUtils.displayView(binding.progressBar, false, 500);
		Snackbar.make(getActivity().findViewById(R.id.drawer_layout), "Error while querying Voat server", Snackbar.LENGTH_LONG).setAction(
				"Action",
				null).show();
	}


	public void clickThumb(View v)
	{
		if (((ActivityBase) getActivity()).multiPanel() > 0)
		{
			Bundle arguments = new Bundle();
			arguments.putSerializable("core", (Core) core.clone());

			FragmentOpenLink fragment = new FragmentOpenLink();
			fragment.setArguments(arguments);

			int container = R.id.center_panel_container;
			if (((ActivityBase) getActivity()).multiPanel() == 3)
				container = R.id.right_panel_container;

			getActivity().getSupportFragmentManager().beginTransaction().replace(container, fragment).addToBackStack(null).
					commit();
		}
		else
		{
			Context context = getContext();
			Intent intent = new Intent(context, ActivityOpenLink.class);
			intent.putExtra("core", (Core) core.clone());
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
		}
	}

}
