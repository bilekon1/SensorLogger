package com.obzb.sensorlogger.classes.sensors;

import com.obzb.sensorlogger.MainActivity;
import com.obzb.sensorlogger.R;
import com.obzb.sensorlogger.classes.ISensor;

import android.hardware.Sensor;

public class SPressure implements ISensor {
	private Sensor s;
	

	public SPressure (){
		this.s = MainActivity.SMANAGER.getDefaultSensor(Sensor.TYPE_PRESSURE);
	};

	@Override
	public Sensor getSensor() {
		return s;
	}

	@Override
	public String[][] getValuesNames() {
		String[][] hodnoty = new String[1][2];
		hodnoty[0][0] = MainActivity.CONTEXT.getString(R.string.vPress);
		hodnoty[0][1] = "hPa";
		return hodnoty;
	}

	@Override
	public String getNote() {
		return MainActivity.CONTEXT.getString(R.string.nPress);
	}

}
