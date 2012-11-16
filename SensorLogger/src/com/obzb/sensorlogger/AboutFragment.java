package com.obzb.sensorlogger;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class AboutFragment extends DialogFragment{
	@Override
	public Dialog onCreateDialog(Bundle args){
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		// dialog s nápovìdou
		builder.setTitle(R.string.about);
		builder.setMessage(R.string.aboutMes);
		builder.setNegativeButton(R.string.close, null);
		return builder.create();
    }

}
