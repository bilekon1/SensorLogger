package cz.uhk.fim.sensorlogger;

import cz.uhk.fim.sensorlogger.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class ExportFragment extends DialogFragment{
	public interface ExportDialogListener {
        public void onDialogPositiveClick(ExportFragment dialog);
        public void onDialogNegativeClick(ExportFragment dialog);
    }
	
	ExportDialogListener mListener;
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the ExportDialogListener so we can send events to the host
            mListener = (ExportDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement ExportDialogListener");
        }
    }
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		// dialog pøi zmìnì orientace
		builder.setTitle(R.string.export);
		builder.setMessage(R.string.exportMes);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {	
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// Send the positive button event back to the host activity
                mListener.onDialogPositiveClick(ExportFragment.this);
			}
		});
		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {	
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// Send the positive button event back to the host activity
                mListener.onDialogNegativeClick(ExportFragment.this);
			}
		});
		return builder.create();
    }

}
