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

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.VideoView;

import com.ortiz.touch.TouchImageView;

import net.pr0npaganda.appvoat.db.PostsDatabase;
import net.pr0npaganda.appvoat.model.OpenLink;
import net.pr0npaganda.appvoat.model.Post;
import net.pr0npaganda.appvoat.utils.AnimUtils;
import net.pr0npaganda.appvoat.utils.AppUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class FragmentOpenLink extends Fragment
{
	private final static int CONFIG_DISPLAYVIEW_ANIM_DURATION        = 300;
	private final static int CONFIG_DISPLAYPROGRESSBAR_ANIM_DURATION = 800;

	private Core     core = null;
	private OpenLink link = null;

	private Toolbar        toolbar      = null;
	private String         toolbarTitle = "";
	private ProgressBar    progressBar  = null;
	private WebView        webView      = null;
	private TouchImageView imageView    = null;
	private VideoView      videoView    = null;

	private FrameLayout videoViewWrapper = null;
	private MediaController mediaController;

	private ValueAnimator progressBarAnimator = null;
	private float         progressBarAlpha    = 0;


	public FragmentOpenLink()
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
		link = (OpenLink) getArguments().getSerializable("link");

		Activity activity = this.getActivity();
		Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
		if (toolbar != null)
		{
			//			toolbar.setTitle(core.getCurrentPost().getDomain());
		}

	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(R.layout.open_link, container, false);

		webView = (WebView) rootView.findViewById(R.id.webView);
		imageView = (TouchImageView) rootView.findViewById(R.id.imageView);
		videoView = (VideoView) rootView.findViewById(R.id.videoView);
		videoViewWrapper = (FrameLayout) rootView.findViewById(R.id.videoViewWrapper);

		progressBar = (ProgressBar) rootView.findViewById(R.id.progress);
		progressBar.setProgress(0);
		progressBar.setMax(100);

		return rootView;
	}


	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);

		// DEV DEV
		//	String url = "http://www.perdu.com";
		//getUrl = "https://gfycat.com/LinedHalfCavy"; // zippy - WORKING
		//getUrl = "https://i.imgur.com/FX8UsLH.gifv"; // convert gif -> mp4 - WORKING
		//getUrl = "https://gfycat.com/EmptyUnacceptableCaracal"; // giant - WORKING but not using giant
		//url = "https://upload.wikimedia.org/wikipedia/commons/7/7e/2014_Cape_Verde._Sal._Gata.jpg"; // big picture
		//getUrl = "https://thumbs.gfycat.com/LinedHalfCavy-mobile.mp4";
		//getUrl = "https://imgur.com/bI3ChDR"; // we keep the imgur website, cause ...
		//getUrl = "https://imgur.com/a/0IQzl"; // gallery on imgur
		//getUrl = "https://media-cdn.tripadvisor.com/media/photo-s/01/00/c9/22/beach-at-santa-maria.jpg"; // small picture

		//post.setUrl("https://thumbs.gfycat.com/LinedHalfCavy-mobile.mp4");
		//		post.setUrl("https://gfycat.com/HandyHorribleJerboa");
		//post.setUrl("https://upload.wikimedia.org/wikipedia/commons/7/7e/2014_Cape_Verde._Sal._Gata.jpg");
		//	Post post = new Post(new Sub(Core.SOURCE_VOAT, "test"), 1547234);
		//	post.setUrl(url);
		// END DEV

		OpenLink openLink;
		if (link != null)
			openLink = link;
		else
			openLink = core.getCurrentPost().getOpenLink();

		AppUtils.Log("Link : " + openLink.getUrl());

		String type = "";
		String mimetype = AppUtils.getMimeType(openLink.getUrl());
		if (mimetype != null)
		{
			String[] types = mimetype.split("/");
			if (types.length > 0)
				type = types[0];
		}

		if (mimetype == null)
			displayUnknown(openLink);
		else if (mimetype.equalsIgnoreCase("text/html"))
			displayHtml(openLink);
		else if (mimetype.equalsIgnoreCase("image/gif"))
			displayHtml(openLink);
		else
			displayUnknown(openLink);

		//openLink.resetUrl();
		PostsDatabase.setPostAsRead(core.getCurrentPost(), core.getCurrentAccount(), Post.TYPE_LINK);
	}


	private void displayUnknown(OpenLink openLink)
	{
		if (openLink.parseUrl())
		{
			cacheLink(openLink);
			return;
		}

		String mimetype = AppUtils.getMimeType(openLink.getUrl());

		if (mimetype == null)
		{
			displayHtml(openLink);
			return;
		}

		if (mimetype.toLowerCase().startsWith("image/gif"))
		{
			openLink.setUrlContentType(OpenLink.DISPLAY_AS_VIDEO);
			cacheLink(openLink);
			return;
		}

		if (mimetype.toLowerCase().startsWith("image"))
		{
			openLink.setUrlContentType(OpenLink.DISPLAY_AS_IMAGE);
			cacheLink(openLink);
			return;
		}

		if (mimetype.toLowerCase().startsWith("video"))
		{
			openLink.setUrlContentType(OpenLink.DISPLAY_AS_VIDEO);
			cacheLink(openLink);
			return;
		}

		displayHtml(openLink);
	}


	private void displayHtml(OpenLink openLink)
	{
		webView.setVisibility(View.VISIBLE);

		webView.getSettings().setJavaScriptEnabled(true);
		webView.setWebViewClient(new WebViewClient());

		displayProgressBar(true);
		webView.setWebChromeClient(new WebChromeClient()
		{
			@Override
			public void onProgressChanged(WebView view, int progress)
			{
				progressBar.setProgress(progress);
				if (progress == 100)
					displayProgressBar(false);
			}
		});

		webView.setInitialScale(100);

		webView.requestFocusFromTouch();

		webView.getSettings().setBuiltInZoomControls(true);
		webView.getSettings().setDisplayZoomControls(false);
		webView.getSettings().setSupportZoom(true);
		webView.getSettings().setUseWideViewPort(true);

		webView.loadUrl(openLink.getUrl());
	}


	private boolean displayImage(OpenLink openLink)
	{
		String url;
		if (openLink.isCached())
			url = openLink.getCacheFilename(getContext());
		else
			url = openLink.getUrl();

		AppUtils.Log("displayImage " + url);

		imageView.setImageBitmap(AppUtils.resizeBitmap(BitmapFactory.decodeFile(url), 2000, 2000));
		AnimUtils.displayView(imageView, true, CONFIG_DISPLAYVIEW_ANIM_DURATION);

		return true;
	}


	private boolean displayVideo(OpenLink openLink)
	{
		AppUtils.Log("displayVideo " + openLink.getUrl());

		String url;
		if (openLink.isCached())
		{
			url = openLink.getCacheFilename(getContext());
		}
		else
			url = openLink.getUrl();

		videoView.setVisibility(View.VISIBLE);
		videoView.setVideoURI(Uri.parse(url));
		//		AppAnims.displayView(videoView, true, 400);

		//		mediaController = new MediaController(getContext());
		//		videoView.setMediaController(mediaController);
		//		mediaController.setAnchorView(videoViewWrapper);

		videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
		{
			public void onCompletion(MediaPlayer mp)
			{
				videoView.start();
			}
		});

		videoView.setOnTouchListener(new View.OnTouchListener()
		{
			private long timedFirstTap = 0;


			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				if (event.getAction() == MotionEvent.ACTION_DOWN)
				{
					if ((System.currentTimeMillis() - timedFirstTap) < 250L)
					{
						videoView.seekTo(0);
						//videoView.start();
						videoView.pause();
						return false;
					}

					if (videoView.isPlaying())
						videoView.pause();
					else
						videoView.start();

					timedFirstTap = System.currentTimeMillis();
				}

				return false;
			}
		});

		videoView.start();
		return true;
	}


	private void displayProgressBar(final boolean display)
	{
		float alphaEnd = 1f;
		if (!display)
			alphaEnd = 0f;

		if (progressBarAnimator != null)
		{
			progressBarAnimator.cancel();
			progressBarAnimator = null;
		}

		progressBarAnimator = ValueAnimator.ofFloat(progressBarAlpha, alphaEnd);
		progressBarAnimator.setDuration(CONFIG_DISPLAYPROGRESSBAR_ANIM_DURATION);
		progressBarAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
		{

			@Override
			public void onAnimationUpdate(ValueAnimator animation)
			{
				progressBarAlpha = (float) animation.getAnimatedValue();
				progressBar.setAlpha(progressBarAlpha);
			}
		});

		progressBarAnimator.addListener(new Animator.AnimatorListener()
		{
			private boolean canceled = false;


			@Override
			public void onAnimationStart(Animator animation)
			{
			}


			@Override
			public void onAnimationEnd(Animator animation)
			{
				//				if (!canceled)
				//					progressBar.setVisibility(View.GONE);
			}


			@Override
			public void onAnimationCancel(Animator animation)
			{
				canceled = true;
			}


			@Override
			public void onAnimationRepeat(Animator animation)
			{
			}
		});

		progressBarAnimator.start();
	}


	private void cacheLink(OpenLink openLink)
	{
		//		if (post.getType() != Post.TYPE_LINK)
		//			return;

		final CacheLinkTask downloadTask = new CacheLinkTask(getContext(), openLink);
		downloadTask.execute();
	}


	private void CachedLink(OpenLink openLink)
	{
		if (openLink.isCached())
		{
			switch (openLink.getUrlContentType())
			{
				case OpenLink.DISPLAY_AS_IMAGE:
					displayImage(openLink);
					break;

				case OpenLink.DISPLAY_AS_VIDEO:
					displayVideo(openLink);
					break;
			}
		}
		else
			displayHtml(openLink);
	}


	private class CacheLinkTask extends AsyncTask<Void, Integer, String>
	{
		private Context context;
		private OpenLink openLink = null;
		//		private PowerManager.WakeLock mWakeLock;


		public CacheLinkTask(Context context, OpenLink openLink)
		{
			this.context = context;
			this.openLink = openLink;
		}


		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();

			displayProgressBar(true);
			//	PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
			//	mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getName());
			//	mWakeLock.acquire();
		}


		@Override
		protected String doInBackground(Void... values)
		{
			InputStream input = null;
			OutputStream output = null;
			HttpURLConnection connection = null;
			try
			{
				while (true)
				{
					String urlString = openLink.getUrl();

					if (openLink.getModifiedUrl().size() > 0)
					{
						urlString = openLink.getModifiedUrlNext();
						AppUtils.Log("Modified Url: " + urlString);
						if (urlString == null)
							break;
					}

					URL url = new URL(urlString);
					connection = (HttpURLConnection) url.openConnection();
					connection.connect();

					if (connection.getResponseCode() != HttpURLConnection.HTTP_OK)
					{
						if (openLink.getModifiedUrl().size() == 0)
							return "Server returned HTTP " + connection.getResponseCode() + " " + connection.getResponseMessage();
						else
							continue;
					}

					int fileLength = connection.getContentLength();

					// download the file
					input = connection.getInputStream();

					new File(openLink.getCacheDir(context)).mkdirs();
					AppUtils.Log("Temp File: " + openLink.getCacheFilename(context));

					File temp = new File(openLink.getCacheFilename(context));
					output = new FileOutputStream(temp);

					byte data[] = new byte[4096];
					long total = 0;
					int count;
					while ((count = input.read(data)) != -1)
					{
						if (isCancelled())
						{
							input.close();
							return null;
						}
						total += count;

						if (fileLength > 0)
							publishProgress((int) (total * 100 / fileLength));
						output.write(data, 0, count);
					}

					if (openLink.getUrlContentType() == OpenLink.DISPLAY_AS_IMAGE)
					{
						openLink.cached(true);
						break;
					}

					if (openLink.getUrlContentType() == OpenLink.DISPLAY_AS_VIDEO)
					{
						try
						{
							MediaPlayer mp = MediaPlayer.create(context, Uri.parse(openLink.getCacheFilename(context)));
							mp.release();

							openLink.cached(true);
							break;
						}
						catch (Exception e)
						{
						}

						continue;
					}

					if (openLink.getModifiedUrl().size() == 0)
						break;
				}
			}
			catch (Exception e)
			{
				return e.toString();
			}
			finally
			{
				try
				{
					if (output != null)
						output.close();
					if (input != null)
						input.close();
				}
				catch (IOException ignored)
				{
				}

				if (connection != null)
					connection.disconnect();
			}

			return null;
		}


		@Override
		protected void onProgressUpdate(Integer... progress)
		{
			super.onProgressUpdate(progress);
			progressBar.setProgress(progress[0]);
		}


		@Override
		protected void onPostExecute(String result)
		{
			//mWakeLock.release();

			displayProgressBar(false);
			CachedLink(openLink);
		}
	}
}
