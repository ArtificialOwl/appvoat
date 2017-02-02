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
import android.content.res.TypedArray;
import android.databinding.BindingAdapter;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.pr0npaganda.appvoat.R;
import net.pr0npaganda.appvoat.model.Comment;
import net.pr0npaganda.appvoat.utils.AppUtils;

import static net.pr0npaganda.appvoat.utils.AnimUtils.displayView;


public class CommentBindingAdapter
{
	@BindingAdapter ({"bind:displayCommentMore"})
	public static void displayLoading(final FrameLayout view, final Comment comment)
	{
		LinearLayout layout_more = (LinearLayout) view.findViewById(R.id.comment_more);
		final LinearLayout layout_comment = (LinearLayout) view.findViewById(R.id.layout_comment);
		final LinearLayout comment_options = (LinearLayout) view.findViewById(R.id.comment_options);

		if (comment.getType() != Comment.COMMENT_LOAD_MORE_COMMENTS)
		{
			if (comment.getType() != Comment.COMMENT_LOAD_MORE_SUBCOMMENTS)
			{

				//
				// long Click
				View.OnLongClickListener longClick = new View.OnLongClickListener()
				{
					//	private FrameLayout parent = view;


					@Override
					public boolean onLongClick(View arg0)
					{
						resetAllCommentsOptions(view);

						if (comment_options.getAlpha() == 0f)
						{
							displayView(layout_comment, false, 300);
							displayView(comment_options, true, 300);
						}
						else
						{
							displayView(layout_comment, true, 300);
							displayView(comment_options, false, 300, true);
						}
						return true;
					}
				};

				view.findViewById(R.id.comment_content).setOnLongClickListener(longClick);
				//view.findViewById(R.id.comment_options).setOnLongClickListener(longClick);
				layout_comment.setOnLongClickListener(longClick);

				LinearLayout listing = (LinearLayout) view.findViewById(R.id.comment_options);
				for (int i = 0; i < listing.getChildCount(); i++)
				{
					listing.getChildAt(i).setOnLongClickListener(longClick);
				}
			}

			layout_more.setVisibility(View.GONE);
			layout_comment.setVisibility(View.VISIBLE);
			return;
		}

		layout_more.setVisibility(View.VISIBLE);
		layout_comment.setVisibility(View.GONE);
		view.findViewById(R.id.comment_infos).setVisibility(View.GONE);
	}


	@BindingAdapter ({"bind:commentMarge"})
	public static void fixMargin(final LinearLayout view, final Comment comment)
	{
		int level = comment.getLevel() - 1;

		Context context = view.getContext();

		// on recupere les couleurs pour le background
		TypedArray ta = view.getResources().obtainTypedArray(R.array.colorMarge);
		int[] colors = new int[ta.length()];
		for (int i = 0; i < ta.length(); i++)
			colors[i] = ta.getColor(i, 0);
		ta.recycle();

		// marge
		int width_pixel = 8;
		int width = AppUtils.pixelFromDp(context, width_pixel);
		int height = AppUtils.pixelFromDp(context, 1);
		int marge = AppUtils.pixelFromDp(context, (level - 1) * width_pixel);

		// on rajoute le divider
		LayerDrawable layer = (LayerDrawable) ContextCompat.getDrawable(context, R.drawable.leftside_comment);
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
		{
			GradientDrawable divider = (GradientDrawable) layer.findDrawableByLayerId(R.id.bottom_divider);
			TypedValue typedValue = new TypedValue();
			Resources.Theme theme = context.getTheme();
			theme.resolveAttribute(R.attr.colorDivider, typedValue, true);
			divider.setStroke(height, typedValue.data);
		}

		// on rajoute le stroke avec la bonne couleur au shape et on assigne le background
		GradientDrawable shape = (GradientDrawable) layer.findDrawableByLayerId(R.id.leftside_shapeitem);
		int colLevel = (level - 1);
		while (colLevel >= colors.length)
			colLevel = colLevel - 9;

		if (level > 0)
			shape.setStroke(width, colors[colLevel]);

		if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN)
			view.setBackgroundDrawable(layer);
		else
			view.setBackground(layer);
		//		}

		// on rajoute la marge
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
		                                                               FrameLayout.LayoutParams.MATCH_PARENT);
		params.setMargins(Math.round(marge), 0, 0, 0);
		view.setLayoutParams(params);

		// load more ?
		if (comment.getType() == Comment.COMMENT_LOAD_MORE_SUBCOMMENTS)
		{
			TextView tv = (TextView) view.findViewById(R.id.subcomment_more);
			tv.setText(String.format(tv.getText().toString(), comment.getChildRemaining()));
			tv.setVisibility(View.VISIBLE);
			view.findViewById(R.id.comment_show).setVisibility(View.GONE);
			view.findViewById(R.id.comment_infos).setVisibility(View.GONE);
			view.findViewById(R.id.comment_content).setVisibility(View.GONE);
		}
		else if (comment.getType() == Comment.COMMENT_DISPLAY_COMMENTS)
		{
			// Dans le cas d'un lien show/hide
			//			GradientDrawable drawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{ContextCompat.getColor(context, R.color.colorBackground), ContextCompat.getColor(context, R.color.colorPrimaryDark)});

			TextView tv = (TextView) view.findViewById(R.id.comment_show);

			tv.setVisibility(View.VISIBLE);
			tv.setText(String.format(tv.getText().toString(), comment.getChildRemaining()));
			view.findViewById(R.id.subcomment_more).setVisibility(View.GONE);
			view.findViewById(R.id.comment_infos).setVisibility(View.GONE);
			view.findViewById(R.id.comment_content).setVisibility(View.GONE);
		}
		else
		{
			view.findViewById(R.id.comment_show).setVisibility(View.GONE);
			view.findViewById(R.id.subcomment_more).setVisibility(View.GONE);
			view.findViewById(R.id.comment_infos).setVisibility(View.VISIBLE);
			view.findViewById(R.id.comment_content).setVisibility(View.VISIBLE);
		}
	}


	public static void resetAllCommentsOptions(final FrameLayout parent)
	{
		RecyclerView listing = (RecyclerView) parent.getParent();
		for (int i = 0; i < listing.getChildCount(); i++)
		{
			LinearLayout layout_comment = (LinearLayout) listing.getChildAt(i).findViewById(R.id.layout_comment);
			if (((Comment) layout_comment.getTag()).getType() != Comment.COMMENT_LOAD_MORE_COMMENTS)
			{
				displayView(listing.getChildAt(i).findViewById(R.id.layout_comment), true, 400);
				displayView(listing.getChildAt(i).findViewById(R.id.comment_options), false, 400, true);
			}
		}

	}
}