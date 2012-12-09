package cz.uhk.sensorlogger.classes.sensors;

import cz.uhk.sensorlogger.R;

import cz.uhk.sensorlogger.MainActivity;
import cz.uhk.sensorlogger.classes.ISensor;

import android.hardware.Sensor;

public class SGravity implements ISensor {
	private Sensor s;

	public SGravity (){
		this.s = MainActivity.SMANAGER.getDefaultSensor(Sensor.TYPE_GRAVITY);
	};

	@Override
	public Sensor getSensor() {
		return s;
	}

	@Override
	public String[][] getValuesNames() {
		String[][] hodnoty = new String[3][2];
		hodnoty[0][0] = "x";
		hodnoty[0][1] = "m/s^2";
		hodnoty[1][0] = "y";
		hodnoty[1][1] = "m/s^2";
		hodnoty[2][0] = "z";
		hodnoty[2][1] = "m/s^2";
		return hodnoty;
	}

	@Override
	public String getNote() {
		return MainActivity.CONTEXT.getString(R.string.nGravity);
	}

}
