package cz.uhk.fim.sensorlogger.classes;

import android.hardware.Sensor;

public interface ISensor {
	
	public Sensor getSensor();

	/**
	 * Vrací pole s názvy hodnot senzoru a jednotkami
	 * @return názvy a jednotky
	 */
	public String[][] getValuesNames();
	/**
	 * Vrací poznámku k senzoru
	 * @return poznámka
	 */
	public String getNote();
	

}
