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
	
	public SlLogger (){
		values = new ArrayList<float[]>();
	}
	
	
	public void addValues(float[] a){
		values.add(a);
	}


	public ArrayList<float[]> getValues() {
		return values;
	}
	
	public String export(String filename, String[][] params){ //pøidat parametry na vytvoøení záhlaví
		String file;
		try {
			Calendar now = Calendar.getInstance();
			file = filename+"_"+now.get(Calendar.YEAR)+String.valueOf(now.get(Calendar.MONTH)+1)+now.get(Calendar.DAY_OF_MONTH)+"_"+now.get(Calendar.HOUR_OF_DAY)+now.get(Calendar.MINUTE)+".csv";
			File soubor = new File(Environment.getExternalStorageDirectory()+"/"+file);
			soubor.createNewFile();
			BufferedWriter vystup = new BufferedWriter(new FileWriter(soubor));
			//záhlaví
			vystup.append("line;");
			for (int i=0; i<params.length; i++){
				vystup.append(String.valueOf(params[i][0])+"["+String.valueOf(params[i][1])+"];");
			}
			vystup.append("\r\n");
			//výpis hodnot
			int j=0;
			for (float[] param:values) {
				vystup.append(j+";");
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
