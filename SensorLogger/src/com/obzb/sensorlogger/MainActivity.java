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

public class MainActivity extends FragmentActivity implements ExportFragment.ExportDialogListener {
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
	private boolean firstrun = true;
	
	// tøídy pro senzory
	public static SensorManager SMANAGER;
	private Sensor sensor;
	private Sensors sensors;
	private ISensor isensor;
	private SensorEventListener slistener;
	private SlLogger logger;
	private Float[] logparam;
	private SlGraph graph;
	private int sParams; //poèet parametrù od senzoru
	private int i=0; //poèítadlo pro graf

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
				txtInfo.setText("");
				txtInfo.append(sensor.getName()+"\n\n");
				txtInfo.append(isensor.getNote());
				
				if (firstrun){
					firstrun = false;
					logger = new SlLogger();
				} else {
					stop();
				}
				
				sParams = isensor.getValuesNames().length;
				logparam = new Float[sParams];
				graph = new SlGraph();
			    lGraph.removeAllViews();  //odstraní pøípadný pøedešlý graf
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
				if (lState == 0) {
					graph.reset();
					lGraph.invalidate();
				}
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
        		
        		stop();
        	}
        });
        
        txtInfo.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				DialogFragment info = new InfoFragment();
				Bundle args = new Bundle();
				args.putString("type", sensors.vratJmenoSenzoru(sensor.getType()));
				args.putString("name", sensor.getName());
				args.putString("vendor", sensor.getVendor());
				args.putFloat("power", sensor.getPower());
				args.putFloat("maxrange", sensor.getMaximumRange());
				args.putFloat("resolution", sensor.getResolution());
				args.putFloat("mindelay", sensor.getMinDelay());
				args.putString("note", isensor.getNote());
				info.setArguments(args);
                info.show(getSupportFragmentManager(), "info");		
			}
		});
        
        
    }
    
    private void stop() {
    	i=0;
		lState = 0;
		if (logger.getValues().size()>=10) {
			 DialogFragment export = new ExportFragment();
             export.show(getSupportFragmentManager(), "export");
		}
		
		txtData.setText("");
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
						logparam[j] = event.values[j];
					};
					
					logger.addValues(logparam);
					
					switch (sParams){
						case 1:
							x = event.values[0];
							graph.pridejHodnotu(i, x);
							break;
						case 2:
							x = event.values[0];
							y = event.values[1];
							graph.pridejHodnotu(i, x, y);
							break;
						case 3:
							x = event.values[0];
							y = event.values[1];
							z = event.values[2];
							graph.pridejHodnotu(i, x, y, z);
					}
					
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
            	DialogFragment about = new AboutFragment();
                about.show(getSupportFragmentManager(), "about");
                return true;
            case R.id.help:
            	DialogFragment help = new HelpFragment();
                help.show(getSupportFragmentManager(), "help");
                return true;
            case R.id.orientation:
                DialogFragment orientation = new OrientationFragment();
                orientation.show(getSupportFragmentManager(), "orientation");
               	return true;
            case R.id.exit:
            	finish();
            	return true;
              
        }
        return false;
    }
    
    
    
    @Override
    protected void onResume() {
    	super.onResume();
    
    }

	@Override
	public void onDialogPositiveClick(ExportFragment dialog) {
		logger.export(sensors.vratJmenoSenzoru(sensor.getType()),isensor.getValuesNames());	
		logger = new SlLogger();
	}

	@Override
	public void onDialogNegativeClick(ExportFragment dialog) {
		logger = new SlLogger();
	}
    
}
