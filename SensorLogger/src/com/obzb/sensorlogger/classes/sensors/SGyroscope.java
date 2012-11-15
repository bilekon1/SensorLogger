package com.obzb.sensorlogger.classes.sensors;

import com.obzb.sensorlogger.MainActivity;
import com.obzb.sensorlogger.R;
import com.obzb.sensorlogger.classes.ISensor;

import android.hardware.Sensor;

public class SGyroscope implements ISensor {
	private Sensor s;
	

	public SGyroscope (){
		this.s = MainActivity.SMANAGER.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
	};

	@Override
	public Sensor getSensor() {
		return s;
	}

	@Override
	public String[][] getValuesNames() {
		String[][] hodnoty = new String[3][2];
		hodnoty[0][0] = "x";
		hodnoty[0][1] = "rad/s";
		hodnoty[1][0] = "y";
		hodnoty[1][1] = "rad/s";
		hodnoty[2][0] = "z";
		hodnoty[2][1] = "rad/s";
		return hodnoty;
	}

	@Override
	public String getNote() {
		return MainActivity.CONTEXT.getString(R.string.nGyro);
	}

}
