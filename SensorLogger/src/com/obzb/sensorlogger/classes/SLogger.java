package com.obzb.sensorlogger.classes;

import java.util.ArrayList;

public class SLogger {
	private ArrayList<Float> values;
	
	public SLogger (){
		values = new ArrayList<Float>();
	}
	
	
	public void addValues(float a){
		values.add(a);
	}


	public ArrayList<Float> getValues() {
		return values;
	}

}
