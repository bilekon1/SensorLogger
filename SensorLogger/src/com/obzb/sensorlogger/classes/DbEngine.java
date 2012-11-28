package com.obzb.sensorlogger.classes;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
//http://code.google.com/p/splmeter/
public class DbEngine extends Thread {
	private Handler h;
	private AudioRecord rec;
	private static final int BUFFER = 400;
	public volatile boolean run = false;
	
	public DbEngine (Handler h) {
		this.h = h;
	}
	
	public void run(){
		 try
	      {	      
			android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
	         rec = new AudioRecord(MediaRecorder.AudioSource.MIC,
	               8000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, 8000);
	         
	         rec.startRecording();
	         short[] tempBuffer = new short[BUFFER];
	         
	         while (run){
	        	 double splValue = 0.0;
	        	 double rmsValue = 0.0;

	        	 for (int i = 0; i < BUFFER - 1; i++)
	        	 {
	        		 tempBuffer[i] = 0;
	        	 }

	        	 rec.read(tempBuffer, 0, BUFFER);

	        	 for (int i = 0; i < BUFFER - 1; i++)
	        	 {
	        		 rmsValue += tempBuffer[i] * tempBuffer[i];      
	        	 }
	        	 rmsValue = rmsValue / BUFFER;
	        	 rmsValue = Math.sqrt(rmsValue);

	        	 splValue = 20 * Math.log10(rmsValue);
	        	 //splValue = splValue;
	        	 //splValue = round(splValue, 2);

	        	 Bundle args = new Bundle();
	        	 args.putFloat("db", (float)splValue);
	        	 Message msg = Message.obtain();
	        	 msg.setData(args);
	        	 h.sendMessage(msg); 
	        	 Thread.sleep(50);
	         }
	         
	      }
	      catch (Exception e)
	      {
	         
	      }
	}
	
	public void stopDb(){
		run = false;
		rec.stop();   //tohle by zbouralo aplikaci kdyby hrozil opìtovný start
	}
	
	public void startDb(){
		run = true;
	};

}
