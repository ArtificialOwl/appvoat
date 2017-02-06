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


package net.pr0npaganda.appvoat.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.databinding.BindingAdapter;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.text.method.LinkMovementMethod;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.makeramen.roundedimageview.RoundedImageView;

import net.pr0npaganda.appvoat.Core;
import net.pr0npaganda.appvoat.R;
import net.pr0npaganda.appvoat.model.Post;
import net.pr0npaganda.appvoat.utils.AnimUtils;
import net.pr0npaganda.appvoat.utils.AppUtils;


public class PostBindingAdapter
{
	@BindingAdapter ({"bind:displayPostMore"})
	public static void displayLoading(final LinearLayout view, final Post post)
	{
		LinearLayout layout_more = (LinearLayout) view.findViewById(R.id.post_more);
		RelativeLayout layout_point = (RelativeLayout) view.findViewById(R.id.layout_point);
		RelativeLayout layout_infos = (RelativeLayout) view.findViewById(R.id.layout_infos);
		RelativeLayout layout_right = (RelativeLayout) view.findViewById(R.id.layout_right);

		if (post.getType() != Post.TYPE_MORE)
		{
			layout_more.setVisibility(View.GONE);
			layout_point.setVisibility(View.VISIBLE);
			layout_infos.setVisibility(View.VISIBLE);
			layout_right.setVisibility(View.VISIBLE);
			return;
		}

		layout_more.setVisibility(View.VISIBLE);
		layout_point.setVisibility(View.GONE);
		layout_infos.setVisibility(View.GONE);
		layout_right.setVisibility(View.GONE);
	}


	@BindingAdapter ({"bind:displayVoting"})
	public static void displayPostVoting(final LinearLayout view, final Post post)
	{
		if (Core.get().getCurrentAccount() == null)
		{
			view.findViewById(R.id.post_upvote).setVisibility(View.GONE);
			view.findViewById(R.id.post_downvote).setVisibility(View.GONE);
			return;
		}

		view.findViewById(R.id.post_upvote).setVisibility(View.VISIBLE);
		view.findViewById(R.id.post_downvote).setVisibility(View.VISIBLE);

		//Context context = view.getContext();

		ImageView image_upvote = (ImageView) view.findViewById(R.id.image_upvote);
		ImageView image_downvote = (ImageView) view.findViewById(R.id.image_downvote);
		if (post.getVote() == 1)
		{
			image_upvote.setImageResource(R.mipmap.upvoat_sel);
			AnimUtils.alphaView(image_upvote, 0.8f, 400);
			image_downvote.setImageResource(R.mipmap.downvoat);
			AnimUtils.alphaView(image_downvote, 0.2f, 400);
		}
		else if (post.getVote() == -1)
		{
			image_upvote.setImageResource(R.mipmap.upvoat);
			AnimUtils.alphaView(image_upvote, 0.2f, 400);
			image_downvote.setImageResource(R.mipmap.downvoat_sel);
			AnimUtils.alphaView(image_downvote, 0.8f, 400);
		}
		else
		{
			image_upvote.setImageResource(R.mipmap.upvoat);
			AnimUtils.alphaView(image_upvote, 0.2f, 400);
			image_downvote.setImageResource(R.mipmap.downvoat);
			AnimUtils.alphaView(image_downvote, 0.2f, 400);
		}
	}


	@BindingAdapter ({"bind:displayPostDivider"})
	public static void displayPostDivider(final LinearLayout view, final Post post)
	{
		Context context = view.getContext();

		int height = AppUtils.pixelFromDp(context, 1);

		// on rajoute le divider
		LayerDrawable layer = (LayerDrawable) ContextCompat.getDrawable(context, R.drawable.post_divider);
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
		{
			GradientDrawable divider = (GradientDrawable) layer.findDrawableByLayerId(R.id.bottom_divider);
			TypedValue typedValue = new TypedValue();
			Resources.Theme theme = context.getTheme();
			theme.resolveAttribute(R.attr.colorDivider, typedValue, true);
			divider.setStroke(height, typedValue.data);
		}

		if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN)
			view.setBackgroundDrawable(layer);
		else
			view.setBackground(layer);
	}


	@BindingAdapter ({"bind:displayIfPostRead"})
	public static void displayIfPostRead(TextView view, Post post)
	{
		if (post.isRead())
			view.setTypeface(null, Typeface.NORMAL);
		else
			view.setTypeface(null, Typeface.BOLD);
	}


	@BindingAdapter ({"bind:clickableSpan"})
	public static void makeItClickable(TextView view, String empty)
	{
		view.setMovementMethod(LinkMovementMethod.getInstance());
	}


	@BindingAdapter ({"bind:showLinkIcon"})
	public static void showLinkIcon(final View view, Post post)
	{
		view.setAlpha(0);
		if (post.getType() != Post.TYPE_LINK || post.getThumbUrl() != "")
			return;

		AnimUtils.displayView(view, true, 500);
	}


	@BindingAdapter ({"bind:displayPostOptions"})
	public static void displayPostOptions(final LinearLayout view, Post post)
	{
		if (Core.get().getCurrentAccount() == null)
		{
			view.findViewById(R.id.post_upvoat).setVisibility(View.GONE);
			view.findViewById(R.id.post_downvoat).setVisibility(View.GONE);
			view.findViewById(R.id.post_comment).setVisibility(View.GONE);
		}
		else
		{
			view.findViewById(R.id.post_upvoat).setVisibility(View.VISIBLE);
			view.findViewById(R.id.post_downvoat).setVisibility(View.VISIBLE);
			view.findViewById(R.id.post_comment).setVisibility(View.VISIBLE);
		}

		ImageView image_upvoat = (ImageView) view.findViewById(R.id.post_upvoat_image);
		ImageView image_downvoat = (ImageView) view.findViewById(R.id.post_downvoat_image);
		if (post.getVote() == 1)
		{
			image_upvoat.setImageResource(R.mipmap.upvoat_sel);
			image_downvoat.setImageResource(R.mipmap.downvoat);
		}
		else if (post.getVote() == -1)
		{
			image_upvoat.setImageResource(R.mipmap.upvoat);
			image_downvoat.setImageResource(R.mipmap.downvoat_sel);
		}
		else
		{
			image_upvoat.setImageResource(R.mipmap.upvoat);
			image_downvoat.setImageResource(R.mipmap.downvoat);
		}
	}


	@BindingAdapter ({"bind:showPostThumb", "bind:showPostThumbColorNew", "bind:showPostThumbColorOpened"})
	public static void showPostThumb(final RoundedImageView imageView, Post post, int colorNew, int colorOpened)
	{
		String url = post.getThumbUrl();
		if (url == null)
			return;

		//		((ViewGroup) imageView.getParent()).setAlpha(0f);

		if (!url.equals(""))
		{
			if (!post.isLinkOpened())
				imageView.setBorderColor(colorNew);
			else
				imageView.setBorderColor(colorOpened);

			((ViewGroup) imageView.getParent()).setVisibility(View.VISIBLE);

			Glide.with(imageView.getContext()).load(url).asBitmap().into(new SimpleTarget<Bitmap>()
			{
				@Override
				public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation)
				{
					imageView.setImageBitmap(bitmap);
					//					((ViewGroup) imageView.getParent()).setAlpha(1f);
					((ViewGroup) imageView.getParent()).setVisibility(View.VISIBLE);
					AnimUtils.displayView((ViewGroup) imageView.getParent(), true, 500);
				}
			});
		}
		else
			((ViewGroup) imageView.getParent()).setVisibility(View.GONE);
	}

}