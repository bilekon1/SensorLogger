package com.obzb.sensorlogger;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class HelpFragment extends DialogFragment{
	@Override
	public Dialog onCreateDialog(Bundle args){
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		// dialog s informacemi o senzoru
		builder.setTitle(R.string.help);
		builder.setMessage(R.string.helpMes);
		builder.setNegativeButton(R.string.close, null);
		return builder.create();
    }

}
