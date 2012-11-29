package com.obzb.sensorlogger.classes;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import android.os.Environment;


public class SlLogger {
	private ArrayList<float[]> values;
	private ArrayList<Long> times;
	
	public SlLogger (){
		values = new ArrayList<float[]>();
		times = new ArrayList<Long>();
	}
	
	
	public void addValues(float[] a){
		values.add(a);
		times.add(System.currentTimeMillis());
	}


	public ArrayList<float[]> getValues() {
		return values;
	}
	
	public String export(String filename, String[][] params){ //p�idat parametry na vytvo�en� z�hlav�
		long first = times.get(0); //reset �asu aby to �lo od 0
		for (int l=0; l < times.size(); l++ ) {
			long time = times.get(l);
			time = time - first;
			times.set(l,time);	
		}
		
		String file;
		try {
			Calendar now = Calendar.getInstance();
			file = filename+"_"+now.get(Calendar.YEAR)+String.valueOf(now.get(Calendar.MONTH)+1)+now.get(Calendar.DAY_OF_MONTH)+"_"+now.get(Calendar.HOUR_OF_DAY)+now.get(Calendar.MINUTE)+".csv";
			File soubor = new File(Environment.getExternalStorageDirectory()+"/"+file);
			soubor.createNewFile();
			BufferedWriter vystup = new BufferedWriter(new FileWriter(soubor));
			//z�hlav�
			vystup.append("time;");
			for (int i=0; i<params.length; i++){
				vystup.append(String.valueOf(params[i][0])+"["+String.valueOf(params[i][1])+"];");
			}
			vystup.append("\r\n");
			//v�pis hodnot
			int j=0;
			for (float[] param:values) {
				vystup.append(times.get(j)+";");
				for (int i=0; i<param.length; i++){
					vystup.append(String.valueOf(param[i])+";");
				}
				vystup.append("\r\n");
				j++;
			}
			vystup.close();
			} catch (IOException e) {
				e.printStackTrace();
				file = null;
			}
		
		return file;
	}

}
