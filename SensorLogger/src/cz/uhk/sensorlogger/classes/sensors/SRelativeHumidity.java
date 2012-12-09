package cz.uhk.sensorlogger.classes.sensors;

import cz.uhk.sensorlogger.R;

import cz.uhk.sensorlogger.MainActivity;
import cz.uhk.sensorlogger.classes.ISensor;

import android.hardware.Sensor;

public class SRelativeHumidity implements ISensor {
	private Sensor s;
	
	/**
	 * Výchozí konstruktor
	 * @param m SensorManager používaný v aplikaci
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
