package com.obzb.sensorlogger.classes.sensors;

import com.obzb.sensorlogger.MainActivity;
import com.obzb.sensorlogger.R;
import com.obzb.sensorlogger.classes.ISensor;

import android.hardware.Sensor;

public class STemperature implements ISensor {
	private Sensor s;
	
	public STemperature (){
		this.s = MainActivity.SMANAGER.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
	};

	@Override
	public Sensor getSensor() {
		return s;
	}

	@Override
	public String[][] getValuesNames() {
		String[][] hodnoty = new String[1][2];
		hodnoty[0][0] = MainActivity.CONTEXT.getString(R.string.vTemp);
		hodnoty[0][1] = "°C";
		return hodnoty;
	}

	@Override
	public String getNote() {
		return MainActivity.CONTEXT.getString(R.string.nTemp);
	}

}
