package com.obzb.sensorlogger.classes;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import android.os.Environment;


public class SlLogger {
	private ArrayList<Float[]> values;
	
	public SlLogger (){
		values = new ArrayList<Float[]>();
	}
	
	
	public void addValues(Float[] a){
		values.add(a);
	}


	public ArrayList<Float[]> getValues() {
		return values;
	}
	
	public boolean export(String filename, String[][] params){ //pøidat parametry na vytvoøení záhlaví
		boolean uspech = false;
		try {
			Calendar now = Calendar.getInstance();
			File soubor = new File(Environment.getExternalStorageDirectory()+"/"+filename+"_"+now.get(Calendar.YEAR)+now.get(Calendar.MONTH)+now.get(Calendar.DAY_OF_MONTH)+"_"+now.get(Calendar.HOUR_OF_DAY)+now.get(Calendar.MINUTE)+".csv");
			soubor.createNewFile();
			BufferedWriter vystup = new BufferedWriter(new FileWriter(soubor));
			//záhlaví
			vystup.append("line;");
			for (int i=0; i<params.length; i++){
				vystup.append(String.valueOf(params[i][0])+";");
			}
			vystup.append("\r\n");
			//výpis hodnot
			int j=0;
			for (Float[] param:values) {
				vystup.append(j+";");
				for (int i=0; i<param.length; i++){
					vystup.append(String.valueOf(param[i])+";");
				}
				vystup.append("\r\n");
				j++;
			}
			uspech = true; 
			vystup.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		
		return uspech;
	}

}
