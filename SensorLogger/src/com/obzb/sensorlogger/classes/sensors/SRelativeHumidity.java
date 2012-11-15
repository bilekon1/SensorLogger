package com.obzb.sensorlogger.classes.sensors;

import com.obzb.sensorlogger.MainActivity;
import com.obzb.sensorlogger.R;
import com.obzb.sensorlogger.classes.ISensor;

import android.hardware.Sensor;

public class SRelativeHumidity implements ISensor {
	private Sensor s;
	
	/**
	 * V�choz� konstruktor
	 * @param m SensorManager pou��van� v aplikaci
	 * @param c Kontext aplikace
	 */
	public SRelativeHumidity (){
		this.s = MainActivity.SMANAGER.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
	};

	@Override
	public Sensor getSensor() {
		return s;
	}

	@Override
	public String[][] getValuesNames() {
		String[][] hodnoty = new String[1][2];
		hodnoty[0][0] = MainActivity.CONTEXT.getString(R.string.vHumi);
		hodnoty[0][1] = "%";
		return hodnoty;
	}

	@Override
	public String getNote() {
		return MainActivity.CONTEXT.getString(R.string.nHumi);
	}

}
