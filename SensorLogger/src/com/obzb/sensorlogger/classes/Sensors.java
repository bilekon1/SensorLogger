package com.obzb.sensorlogger.classes;

import java.util.List;

import com.obzb.sensorlogger.MainActivity;
import com.obzb.sensorlogger.R;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.util.SparseArray;

public class Sensors {
	
	private SparseArray<String> senzory = new SparseArray<String>(); //lepší než hashmap údajnì
	private SensorManager mSensorManager;
	
	
	public Sensors (){
		this.mSensorManager = MainActivity.SMANAGER;
		Context c = MainActivity.CONTEXT;
		senzory.put(1, c.getString(R.string.accel));
		senzory.put(9, c.getString(R.string.grav));
		senzory.put(4, c.getString(R.string.gyro));
		senzory.put(5, c.getString(R.string.light));
		senzory.put(10, c.getString(R.string.line));
		senzory.put(2, c.getString(R.string.magn));
		senzory.put(6, c.getString(R.string.press));
		senzory.put(8, c.getString(R.string.prox));
		senzory.put(11, c.getString(R.string.rvec));
		senzory.put(7, c.getString(R.string.temp));
	};
	
	public List<Sensor> getSensorsList() {
		return mSensorManager.getSensorList(Sensor.TYPE_ALL);
	}
	
	public boolean checkSensor (int type){
		if (mSensorManager.getSensorList(type).size() == 0) return false;
		return true;
	}

	public String vratJmenoSenzoru(int typ) {
		return senzory.get(typ);
	}

	public ISensor getISensor(int i) {
		switch (i){
			case 1 : return new SAccelerometer();
		}
		return null;
	}

}
