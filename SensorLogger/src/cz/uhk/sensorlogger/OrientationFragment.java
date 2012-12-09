package cz.uhk.sensorlogger;

import cz.uhk.sensorlogger.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class OrientationFragment extends DialogFragment{
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		// dialog pøi zmìnì orientace
		builder.setTitle(R.string.orientation);
		builder.setMessage(R.string.orientationMes);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int id) {
		        int orient = getResources().getConfiguration().orientation;
		        
		        if (orient == 1) { //portrait
		        	 dialog.dismiss();
		        	getActivity().setRequestedOrientation(0);
		        } else {
		        	 dialog.dismiss();
		        	getActivity().setRequestedOrientation(1);
		        }
		    }});
		builder.setNegativeButton(R.string.cancel, null);
		return builder.create();
    }

}
