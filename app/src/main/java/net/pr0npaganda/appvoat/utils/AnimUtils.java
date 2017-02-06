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


package net.pr0npaganda.appvoat.utils;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.View;
import android.view.ViewGroup;


public class AnimUtils
{
	public static void displayView(final View view, final boolean display, int duration)
	{
		displayView(view, display, duration, false);
	}


	public static void displayView(final View view, final boolean display, int duration, final boolean removeOnEnd)
	{
		if (view == null)
			return;

		float alphaEnd = 1f;
		if (!display)
			alphaEnd = 0f;
		else
			view.setVisibility(View.VISIBLE);

		if (view.getAlpha() == alphaEnd)
			return;

		if (display)
		{
			view.setAlpha(0f);
			view.setVisibility(View.VISIBLE);
		}

		ValueAnimator animator = ValueAnimator.ofFloat(view.getAlpha(), alphaEnd);
		animator.setDuration(duration);
		animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
		{
			@Override
			public void onAnimationUpdate(ValueAnimator animation)
			{
				float alpha = (float) animation.getAnimatedValue();
				view.setAlpha(alpha);
			}
		});

		animator.addListener(new Animator.AnimatorListener()
		{
			@Override
			public void onAnimationStart(Animator animator)
			{
			}


			@Override
			public void onAnimationEnd(Animator animation)
			{
				if (!display && removeOnEnd)
					view.setVisibility(View.GONE);
			}


			@Override
			public void onAnimationCancel(Animator animator)
			{
			}


			@Override
			public void onAnimationRepeat(Animator animator)
			{
			}

		});

		animator.start();
	}




	public static void alphaView(final View view, float alphaEnd, int duration)
	{
		if (view == null)
			return;

		if (view.getAlpha() == alphaEnd)
			return;

		ValueAnimator animator = ValueAnimator.ofFloat(view.getAlpha(), alphaEnd);
		animator.setDuration(duration);
		animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
		{
			@Override
			public void onAnimationUpdate(ValueAnimator animation)
			{
				float alpha = (float) animation.getAnimatedValue();
				view.setAlpha(alpha);
			}
		});

		animator.start();
	}


	private static void displayViewGroup(final ViewGroup view)
	{
		float alphaEnd = 1f;

		ValueAnimator animator = ValueAnimator.ofFloat(view.getAlpha(), alphaEnd);
		animator.setDuration(500);
		animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
		{

			@Override
			public void onAnimationUpdate(ValueAnimator animation)
			{
				float alpha = (float) animation.getAnimatedValue();
				view.setAlpha(alpha);
			}
		});

		animator.start();
	}


	public static void animateDrawerToggleIcon(boolean display,
	                                           final DrawerLayout drawer,
	                                           final ActionBarDrawerToggle toggle,
	                                           final ActionBar actionBar)
	{

		ValueAnimator anim;
		if (display)
			anim = ValueAnimator.ofFloat(0, 1);
		else
			anim = ValueAnimator.ofFloat(1, 0);

		anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
		{

			@Override
			public void onAnimationUpdate(ValueAnimator valueAnimator)
			{
				float slideOffset = (Float) valueAnimator.getAnimatedValue();
				toggle.onDrawerSlide(drawer, slideOffset);
			}
		});

		anim.addListener(new Animator.AnimatorListener()
		{
			@Override
			public void onAnimationStart(Animator animator)
			{
			}


			@Override
			public void onAnimationEnd(Animator animation)
			{
				toggle.setDrawerIndicatorEnabled(false);
				actionBar.setDisplayShowHomeEnabled(true);
				actionBar.setHomeButtonEnabled(true);
			}


			@Override
			public void onAnimationCancel(Animator animator)
			{
			}


			@Override
			public void onAnimationRepeat(Animator animator)
			{
			}

		});

		//anim.setInterpolator(new DecelerateInterpolator());
		anim.setDuration(500);
		anim.start();

	}

}
