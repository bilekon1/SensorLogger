package com.obzb.sensorlogger;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class InfoFragment extends DialogFragment{
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		// dialog s informacemi o aplikaci
		Bundle args = getArguments();
		builder.setTitle(args.getString("type"));
		String message = getString(R.string.name)+args.getString("name")+"\n"+
		getString(R.string.vendor)+args.getString("vendor")+"\n"+
		getString(R.string.power)+args.getFloat("power")+" mA\n"+
		getString(R.string.maxrange)+args.getFloat("maxrange")+"\n"+
		getString(R.string.resolution)+args.getFloat("resolution")+"\n"+
		getString(R.string.mindelay)+args.getFloat("mindelay")+"ms \n"+
		args.getString("note")+"\n";
		builder.setMessage(message);
		builder.setNegativeButton(R.string.close, null);
		return builder.create();
    }

}
