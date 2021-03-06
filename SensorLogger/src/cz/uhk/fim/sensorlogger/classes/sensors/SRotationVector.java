package cz.uhk.fim.sensorlogger.classes.sensors;

import cz.uhk.fim.sensorlogger.MainActivity;
import cz.uhk.fim.sensorlogger.classes.ISensor;
import cz.uhk.fim.sensorlogger.R;


import android.hardware.Sensor;

public class SRotationVector implements ISensor {
	private Sensor s;
	
	/**
	 * Výchozí konstruktor
	 * @param m SensorManager používaný v aplikaci
	 * @param c Kontext aplikace
	 */
	public SRotationVector (){
		this.s = MainActivity.SMANAGER.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
	};

	@Override
	public Sensor getSensor() {
		return s;
	}

	@Override
	public String[][] getValuesNames() {
		String[][] hodnoty = new String[3][2];
		hodnoty[0][0] = "z*sin(θ/2)";
		hodnoty[0][1] = "";
		hodnoty[1][0] = "y*sin(θ/2)";
		hodnoty[1][1] = "";
		hodnoty[2][0] = "z*sin(θ/2)";
		hodnoty[2][1] = "";
		return hodnoty;
	}

	@Override
	public String getNote() {
		return MainActivity.CONTEXT.getString(R.string.nRotV);
	}

}
