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
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.pr0npaganda.appvoat.R;
import net.pr0npaganda.appvoat.model.Comment;
import net.pr0npaganda.appvoat.utils.AppUtils;


public class CommentBindingAdapter
{
	@BindingAdapter ({"bind:displayCommentMore"})
	public static void displayLoading(final LinearLayout view, final Comment comment)
	{
		RelativeLayout layout_more = (RelativeLayout) view.findViewById(R.id.comment_more);
		LinearLayout layout_comment = (LinearLayout) view.findViewById(R.id.layout_comment);

		if (comment.getType() != Comment.COMMENT_LOAD_MORE_COMMENTS)
		{
			layout_more.setVisibility(View.GONE);
			layout_comment.setVisibility(View.VISIBLE);
			return;
		}

		layout_more.setVisibility(View.VISIBLE);
		layout_comment.setVisibility(View.GONE);
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
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
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

}