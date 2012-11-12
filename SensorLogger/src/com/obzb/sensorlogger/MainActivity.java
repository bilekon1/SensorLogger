package com.obzb.sensorlogger;

import java.util.ArrayList;


import com.obzb.sensorlogger.classes.ISensor;
import com.obzb.sensorlogger.classes.SGraph;
import com.obzb.sensorlogger.classes.SLogger;
import com.obzb.sensorlogger.classes.Sensors;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.util.SparseIntArray;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends Activity{
	public static Context CONTEXT;
	//GUI prvky
	TextView txtInfo;
	TextView txtData;
	Spinner spSensors;
	LinearLayout lGraph;
	ArrayList<String> lSensors = new ArrayList<String>();
	SparseIntArray priSenzory = new SparseIntArray(); //pøiøazuje id typu senzoru k jeho poøadí ve spinneru, sparseIntArray má být výkonnìjší než HashMap
	ImageButton bPlay;
	ImageButton bPause;
	ImageButton bStop;
	
	private int lState = 0; //stav logování, 0 vypnuto, 1 pauza, 2 bìží
	
	// tøídy pro senzory
	public static SensorManager SMANAGER;
	private Sensor sensor;
	private Sensors sensors;
	private ISensor isensor;
	private SensorEventListener slistener;
	private SLogger logger;
	private SGraph graph;
	private int sParams; //poèet parametrù od senzoru
	private int i=0; //poèítadlo pro graf
	
	//private final Handler mHandler = new Handler(); //zajištuje refresh grafu - nezajištuje
	//private Runnable mTimer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lGraph = (LinearLayout) findViewById(R.id.lGraph);
        CONTEXT = this;
        SMANAGER = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensors = new Sensors();
        vytvorSlistener();
        //GUI inicializace
        txtInfo = (TextView) findViewById(R.id.txtInfo);
        txtData = (TextView) findViewById(R.id.txtData);
        spSensors = (Spinner)findViewById(R.id.spSensors);
        bPlay = (ImageButton) findViewById(R.id.bPlay);
        bPause = (ImageButton) findViewById(R.id.bPause);
        bStop = (ImageButton) findViewById(R.id.bStop);
        
        naplnSpinner();
        ArrayAdapter<String> arSensors = new ArrayAdapter<String>(this, R.layout.row, lSensors);
        spSensors.setAdapter(arSensors);
        
        spSensors.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int pozice, long arg3) {
				SMANAGER.unregisterListener(slistener);
				isensor = sensors.getISensor(priSenzory.get(pozice));
				sensor = isensor.getSensor();
				txtInfo.append(getString(R.string.name)+sensor.getName()+"\n");
				txtInfo.append(getString(R.string.vendor)+sensor.getVendor()+"\n");
				txtInfo.append(getString(R.string.power)+sensor.getPower()+" mA\n");
				txtInfo.append(getString(R.string.maxrange)+sensor.getMaximumRange()+"\n");
				txtInfo.append(getString(R.string.resolution)+sensor.getResolution()+"\n");
				txtInfo.append(getString(R.string.mindelay)+sensor.getMinDelay()+" ms\n");
				txtInfo.append(isensor.getNote());
				
				sParams = isensor.getValuesNames().length;
				logger = new SLogger();
				graph = new SGraph();
			    lGraph.removeAllViews();  //odstraní pøípadný pøedešlý graf
			    lGraph.addView(graph.kresliGraf(sParams, isensor.getValuesNames()));  
				SMANAGER.registerListener(slistener, sensor, SensorManager.SENSOR_DELAY_UI);
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				txtInfo.setText("Nic nevybráno");
			}
		});
        
        bPlay.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				lState = 2;
				
			}
		});
        
        bPause.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				lState = 1;
				
			}
		});
        bStop.setOnClickListener(new View.OnClickListener() {
	
        	@Override
        	public void onClick(View v) {
        		lState = 0;
        		i=0;
        		graph.reset();
        		lGraph.invalidate();
		
        	}
        });
        
        
    }
    
    private void vytvorSlistener() {
		slistener = new SensorEventListener() {
			float x;
			float y;
			float z;
			@Override
			public void onSensorChanged(SensorEvent event) {
				String[][] sValues = isensor.getValuesNames();
				if (lState == 2){
					txtData.setText("");
					for (int j=0; j<sParams; j++){//cyklus kdy to bude postupnì apendovat øádky dle poètu parametrù
						txtData.append(sValues[j][0]+" "+String.valueOf(event.values[j])+" "+sValues[j][1]+"\n");
					};
					
					//logger.addValues(event.values[0]);
					
					x = event.values[0];
					y = event.values[1];
					z = event.values[2];
					/*mTimer = new Runnable() {    //dìsnì zasekává aplikaci
						@Override
						public void run() {
							if ( (i % 400) == 0) graph.reset();
							graph.pridejHodnotu(i, x, y, z);
							mHandler.postDelayed(this, 2000);
							lGraph.invalidate();
						}
					};
					mHandler.postDelayed(mTimer, 2000);
					i++;*/
					graph.pridejHodnotu(i, x, y, z);
					
					lGraph.invalidate();
					i++;
				}
			}
			
			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
				
				
			}
		};
		
	}

	/**
     * Plní spinner existujícími senzory a vytváøí hashmapu k identifikaci položek
     */
    private void naplnSpinner() { 
		int pozice = 0;
		if (sensors.checkSensor(1)) {
			lSensors.add(sensors.vratJmenoSenzoru(1));
			priSenzory.put(pozice, 1);
			pozice++;};
		if (sensors.checkSensor(9)) {
			lSensors.add(sensors.vratJmenoSenzoru(9));
			priSenzory.put(pozice, 9);
			pozice++;};
		if (sensors.checkSensor(4)) {
			lSensors.add(sensors.vratJmenoSenzoru(4));
			priSenzory.put(pozice, 4);
			pozice++;};
		if (sensors.checkSensor(5)) {
			lSensors.add(sensors.vratJmenoSenzoru(5));
			priSenzory.put(pozice, 5);
			pozice++;};
		if (sensors.checkSensor(10)) {
			lSensors.add(sensors.vratJmenoSenzoru(10));
			priSenzory.put(pozice, 10);
			pozice++;};
		if (sensors.checkSensor(2)) {
			lSensors.add(sensors.vratJmenoSenzoru(2));
			priSenzory.put(pozice, 2);
			pozice++;};
		if (sensors.checkSensor(6)) {
			lSensors.add(sensors.vratJmenoSenzoru(6));
			priSenzory.put(pozice, 6);
			pozice++;};
		if (sensors.checkSensor(8)) {
			lSensors.add(sensors.vratJmenoSenzoru(8));
			priSenzory.put(pozice, 8);
			pozice++;};
		if (sensors.checkSensor(11)) {
			lSensors.add(sensors.vratJmenoSenzoru(11));
			priSenzory.put(pozice, 11);
			pozice++;};
	}
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    
    }
    
}
