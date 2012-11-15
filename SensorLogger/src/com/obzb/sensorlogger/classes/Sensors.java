package com.obzb.sensorlogger.classes;

import java.util.List;

import com.obzb.sensorlogger.MainActivity;
import com.obzb.sensorlogger.R;
import com.obzb.sensorlogger.classes.sensors.SAccelerometer;
import com.obzb.sensorlogger.classes.sensors.SGravity;
import com.obzb.sensorlogger.classes.sensors.SGyroscope;
import com.obzb.sensorlogger.classes.sensors.SLight;
import com.obzb.sensorlogger.classes.sensors.SLinearAcceleration;
import com.obzb.sensorlogger.classes.sensors.SMagneticField;
import com.obzb.sensorlogger.classes.sensors.SPressure;
import com.obzb.sensorlogger.classes.sensors.SProximity;
import com.obzb.sensorlogger.classes.sensors.SRelativeHumidity;
import com.obzb.sensorlogger.classes.sensors.SRotationVector;
import com.obzb.sensorlogger.classes.sensors.STemperature;

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
		senzory.put(13, c.getString(R.string.temp));
		senzory.put(12, c.getString(R.string.humidity));
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
			case 9 : return new SGravity();
			case 4 : return new SGyroscope();
			case 5 : return new SLight();
			case 10 : return new SLinearAcceleration();
			case 2 : return new SMagneticField();
			case 6 : return new SPressure();
			case 8 : return new SProximity();
			case 11 : return new SRotationVector();
			case 13 : return new STemperature();
			case 12 : return new SRelativeHumidity();
		}
		return null;
	}

}
