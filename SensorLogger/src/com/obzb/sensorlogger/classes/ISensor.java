package com.obzb.sensorlogger.classes;

import android.hardware.Sensor;

public interface ISensor {
	
	public Sensor getSensor();

	/**
	 * Vrac� pole s n�zvy hodnot senzoru a jednotkami
	 * @return n�zvy a jednotky
	 */
	public String[][] getValuesNames();
	/**
	 * Vrac� pozn�mku k senzoru
	 * @return pozn�mka
	 */
	public String getNote();
	

}
