package com.obzb.sensorlogger.classes.sensors;

import com.obzb.sensorlogger.MainActivity;
import com.obzb.sensorlogger.R;
import com.obzb.sensorlogger.classes.ISensor;

import android.hardware.Sensor;

public class SLight implements ISensor {
	private Sensor s;
	
	
	public SLight (){
		this.s = MainActivity.SMANAGER.getDefaultSensor(Sensor.TYPE_LIGHT);
	};

	@Override
	public Sensor getSensor() {
		return s;
	}

	@Override
	public String[][] getValuesNames() {
		String[][] hodnoty = new String[1][2];
		hodnoty[0][0] = MainActivity.CONTEXT.getString(R.string.vLight);
		hodnoty[0][1] = "lux";
		return hodnoty;
	}

	@Override
	public String getNote() {
		return MainActivity.CONTEXT.getString(R.string.nLight);
	}

}
