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
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import net.pr0npaganda.appvoat.api.Api;
import net.pr0npaganda.appvoat.api.ApiError;
import net.pr0npaganda.appvoat.api.ApiRequest;
import net.pr0npaganda.appvoat.databinding.OauthBinding;
import net.pr0npaganda.appvoat.interfaces.ApiRequestListener;
import net.pr0npaganda.appvoat.utils.AppUtils;


public class FragmentOAuth extends Fragment implements ApiRequestListener
{
	//	private boolean mTwoPane;
	private Api api;

	private Core core = null;
	private OauthBinding binding;

	//	private LinearLayoutManager layoutManager;
	//	private boolean populatingComments = false;


	public FragmentOAuth()
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
			//			toolbar.setTitle(core.getCurrentPost().getSub().getName());
		}

		//		if (getActivity().findViewById(R.id.center_panel_container) != null)
		//			mTwoPane = true;

	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// binding
		binding = DataBindingUtil.inflate(inflater, R.layout.oauth, container, false);
		View rootView = binding.getRoot();

		binding.webviewOauth.setWebViewClient(new WebViewClient()
		{
			@SuppressWarnings ("deprecation")
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url)
			{
				return returnedUrl(url);
			}


			@TargetApi (Build.VERSION_CODES.N)
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request)
			{
				return returnedUrl(request.getUrl().toString());
			}


			private boolean returnedUrl(String url)
			{
				String[] splited = url.split("\\?code=");
				if (splited.length != 2)
					return false;

				String code = splited[1];
				getToken(code);

				return true;
			}

		});

		Snackbar.make(getActivity().findViewById(R.id.drawer_layout), "Redirecting you to Voat.co for authentication", Snackbar.LENGTH_LONG)
				.setAction("Action", null).show();
		binding.webviewOauth.loadUrl(api.getAuthorizeUrl(Core.SOURCE_VOAT));

		return rootView;
	}


	public void getToken(String code)
	{
		Snackbar.make(getActivity().findViewById(R.id.drawer_layout), "Retreiving Token from Voat.co", Snackbar.LENGTH_LONG).setAction(
				"Action",
				null).show();

		api.requestToken(Core.SOURCE_VOAT, code);
	}


	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);

		//		api.requestComments(core.getCurrentPost());
		//		PostsDatabase.setPostAsRead(core.getCurrentPost(), Post.TYPE_TEXT);
	}


	@Override
	public void onApiRequestCompleted(ApiRequest request, boolean isOver)
	{
		if (request.getType() == ApiRequest.REQUEST_TYPE_TOKEN)
		{
			Snackbar.make(getActivity().findViewById(R.id.drawer_layout), "Authentication completed", Snackbar.LENGTH_LONG).setAction(
					"Action",
					null).show();

			Context context = getActivity().getBaseContext();
			Intent intent = new Intent(context, ActivityPostList.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
		}
		//		AnimUtils.displayView(binding.progressBar, false, 500);
		//		populatingComments = false;
	}


	@Override
	public void onApiRequestEmpty(int type)
	{
	}


	@Override
	public void onApiRequestError(ApiError error)
	{
		AppUtils.Log("auth error: " + error.getMessage());
		Snackbar.make(getActivity().findViewById(R.id.drawer_layout), "Error while querying Voat server", Snackbar.LENGTH_LONG).setAction(
				"Action",
				null).show();
	}

}
