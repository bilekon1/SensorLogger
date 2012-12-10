package cz.uhk.fim.sensorlogger;

import java.text.DecimalFormat;

import cz.uhk.fim.sensorlogger.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class StatsFragment extends DialogFragment{
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		// dialog se statistickými údaji
		Bundle args = getArguments();
		String[] sValues = args.getStringArray("sValues");
		String[] sUnits = args.getStringArray("sUnits");
		float[] min = args.getFloatArray("min");
		float[] max = args.getFloatArray("max");
		float[] avg = args.getFloatArray("avg");
		DecimalFormat df = new DecimalFormat("#.##"); //oøíznutí desetinných míst
		builder.setTitle(R.string.stats);
		String message = args.getString("note")+"\n"+"min; max; avg \n";
		for (int i=0; i<sValues.length; i++){
			message = message+sValues[i]+" "+df.format(min[i])+"; "+df.format(max[i])+"; "+df.format(avg[i])+" "+sUnits[i]+" \n";
		}
		
		
		builder.setMessage(message);
		builder.setNegativeButton(R.string.close, null);
		return builder.create();
    }

}
