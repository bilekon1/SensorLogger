package com.obzb.sensorlogger;

import java.util.ArrayList;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.SparseIntArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.obzb.sensorlogger.classes.ISensor;
import com.obzb.sensorlogger.classes.SlGraph;
import com.obzb.sensorlogger.classes.SlLogger;
import com.obzb.sensorlogger.classes.Sensors;

public class MainActivity extends FragmentActivity {
	public static Context CONTEXT;
	//GUI prvky
	TextView txtInfo;
	TextView txtData;
	Spinner spSensors;
	LinearLayout lGraph;
	ArrayList<String> lSensors = new ArrayList<String>();
	SparseIntArray priSenzory = new SparseIntArray(); //p�i�azuje id typu senzoru k jeho po�ad� ve spinneru, sparseIntArray m� b�t v�konn�j�� ne� HashMap
	ImageButton bPlay;
	ImageButton bPause;
	ImageButton bStop;
	
	private int lState = 0; //stav logov�n�, 0 vypnuto, 1 pauza, 2 b��
	
	// t��dy pro senzory
	public static SensorManager SMANAGER;
	private Sensor sensor;
	private Sensors sensors;
	private ISensor isensor;
	private SensorEventListener slistener;
	private SlLogger logger;
	private Float[] logparam;
	private SlGraph graph;
	private int sParams; //po�et parametr� od senzoru
	private int i=0; //po��tadlo pro graf
	
	//private final Handler mHandler = new Handler(); //zaji�tuje refresh grafu - nezaji�tuje
	//private Runnable mTimer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_main);
        
        CONTEXT = this;
        SMANAGER = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensors = new Sensors();
        vytvorSlistener();
        //GUI inicializace
        lGraph = (LinearLayout) findViewById(R.id.lGraph);
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
				logparam = new Float[sParams];
				logger = new SlLogger();
				graph = new SlGraph();
			    lGraph.removeAllViews();  //odstran� p��padn� p�ede�l� graf
			    lGraph.addView(graph.kresliGraf(sParams, isensor.getValuesNames()));  
				SMANAGER.registerListener(slistener, sensor, SensorManager.SENSOR_DELAY_UI);
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				
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
        		logger.export(sensors.vratJmenoSenzoru(sensor.getType()),isensor.getValuesNames());
        		logger = new SlLogger();
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
					for (int j=0; j<sParams; j++){//cyklus kdy to bude postupn� apendovat ��dky dle po�tu parametr�
						txtData.append(sValues[j][0]+" "+String.valueOf(event.values[j])+" "+sValues[j][1]+"\n");
						logparam[j] = event.values[j];
					};
					
					logger.addValues(logparam);
					
					x = event.values[0];
					y = event.values[1];
					z = event.values[2];
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
     * Pln� spinner existuj�c�mi senzory a vytv��� hashmapu k identifikaci polo�ek
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
		if (sensors.checkSensor(12)) {
			lSensors.add(sensors.vratJmenoSenzoru(12));
			priSenzory.put(pozice, 12);
			pozice++;};
		if (sensors.checkSensor(7)) {
			lSensors.add(sensors.vratJmenoSenzoru(7));
			priSenzory.put(pozice, 7);
			pozice++;};
	}
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.about:
            	
                return true;
            case R.id.help:
               
                return true;
            case R.id.orientation:
                DialogFragment orientation = new OrientationFragment();
                orientation.show(getSupportFragmentManager(), "orientation");
               	return true;
        }
        return false;
    }
    
    
    
    @Override
    protected void onResume() {
    	super.onResume();
    
    }
    
}
