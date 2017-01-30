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


package net.pr0npaganda.appvoat.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.View;
import android.widget.EditText;

import net.pr0npaganda.appvoat.ActivityBase;
import net.pr0npaganda.appvoat.R;


public class SimpleEntryDialogFragment extends AppCompatDialogFragment
{

	private String   tag;
	private String   title;
	private EditText editText;


	public SimpleEntryDialogFragment()
	{
	}


	public static SimpleEntryDialogFragment newInstance(String tag, String title)
	{
		SimpleEntryDialogFragment frag = new SimpleEntryDialogFragment();

		Bundle args = new Bundle();
		args.putString("tag", tag);
		args.putString("title", title);
		frag.setArguments(args);

		return frag;
	}


	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		Bundle args = getArguments();
		this.tag = args.getString("tag", "");
		this.title = args.getString("title", "");
	}


	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(this.title);

		View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_simpleentry, null);
		editText = (EditText) view.findViewById(R.id.edit_value);
		builder.setView(view).setPositiveButton("Go", new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				String value = editText.getText().toString();

				ActivityBase callingActivity = (ActivityBase) getActivity();
				callingActivity.onSimpleEntryDialogValue(tag, value);
				dialog.dismiss();
			}
		});

		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dismiss();
			}
		});

		return builder.create();
	}

}