package com.obzb.sensorlogger.classes;

import com.obzb.sensorlogger.MainActivity;
import com.obzb.sensorlogger.R;

import android.hardware.Sensor;

public class SAccelerometer implements ISensor {
	private Sensor s;
	
	/**
	 * Výchozí konstruktor
	 * @param m SensorManager používaný v aplikaci
	 * @param c Kontext aplikace
	 */
	public SAccelerometer (){
		this.s = MainActivity.SMANAGER.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
	};

	@Override
	public Sensor getSensor() {
		return s;
	}

	@Override
	public String[][] getValuesNames() {
		String[][] hodnoty = new String[3][2];
		hodnoty[0][0] = "x";
		hodnoty[0][1] = "ms2";
		hodnoty[1][0] = "y";
		hodnoty[1][1] = "ms2";
		hodnoty[2][0] = "z";
		hodnoty[2][1] = "ms2";
		return hodnoty;
	}

	@Override
	public String getNote() {
		return MainActivity.CONTEXT.getString(R.string.nAccel);
	}

}
