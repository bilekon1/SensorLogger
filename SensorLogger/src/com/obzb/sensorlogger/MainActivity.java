package com.obzb.sensorlogger;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import android.widget.Toast;

import com.obzb.sensorlogger.classes.DbEngine;
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
	private boolean firstrun = true; //aplikace byla teprv spuštìna, øeší vytváøení loggeru pøi spuštìní
	private static boolean firstvalue = true;; //první hodnoda logu - inicializace statistických hodnot
	
	// tøídy pro senzory
	public static SensorManager SMANAGER;
	private Sensor sensor;
	private Sensors sensors;
	private ISensor isensor;
	private SensorEventListener slistener;
	private SlLogger logger;
	private float[] logparam;
	private SlGraph graph;
	private int sParams; //poèet parametrù od senzoru
	private int i=0; //poèítadlo pro graf
	private float[] min;  //statistické údaje
	private float[] max;
	private float[] avg;
	
	private DbEngine dbe;
	/* odchytává zprávy z vlákna zpracovávajícícho zvuk na dB */
    private Handler mHandle;
  
	
	

    @SuppressLint("HandlerLeak")  //pøedpokládám že u mainactivity která bìží v jedné instanci by problém nastat nemìl
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
        
        mHandle = new Handler()
        {
           @Override
           public void handleMessage(Message msg)
           {	
        	   if (lState == 2){
        		   loggujMic(msg.getData().getFloat("db"));
    			}          
           };
        };
        
        naplnSpinner();
        ArrayAdapter<String> arSensors = new ArrayAdapter<String>(this, R.layout.row, lSensors);
        spSensors.setAdapter(arSensors);
        
        spSensors.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int pozice, long arg3) {
				SMANAGER.unregisterListener(slistener);
				if (dbe != null) dbe.stopDb();
				dbe = null;
				
				txtInfo.setText("");
				txtData.setText("");
				
				if (priSenzory.get(pozice)!= 100) {
					txtInfo.setClickable(true); 
					isensor = sensors.getISensor(priSenzory.get(pozice));
					sensor = isensor.getSensor();
					sParams = isensor.getValuesNames().length;
					txtInfo.append(sensor.getName()+"\n\n");
					txtInfo.append(isensor.getNote());
				} else {
					txtInfo.setClickable(false); //u mikrofonu není co zobrazit
					txtInfo.append(getString(R.string.mic)+"\n\n");
					txtInfo.append(getString(R.string.nMic));
					sParams = 1;
				}			
				
				if (firstrun){
					firstrun = false;
					logger = new SlLogger();
				} else {
					stop();
				}
								
				//logparam = new float[sParams];
				graph = new SlGraph();
			    lGraph.removeAllViews();  //odstraní pøípadný pøedešlý graf
			    if (priSenzory.get(pozice)!= 100) {
			    	lGraph.addView(graph.kresliGraf(sParams, isensor.getValuesNames()));  
			    	SMANAGER.registerListener(slistener, sensor, SensorManager.SENSOR_DELAY_UI);
			    } else {
			    	String[][] mic = new String[1][2];
			    	mic[0][0] = getString(R.string.vMic);
			    	mic[0][1] = "dB";
			    	lGraph.addView(graph.kresliGraf(1, mic)); 
			    	dbe = new DbEngine(mHandle);
			    	dbe.start();
			    	dbe.startDb();
			    }
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
        
        txtData.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				DialogFragment stats = new StatsFragment();
				Bundle args = new Bundle();
				if (dbe == null) {
					String[] sValues = new String[sParams];
					String[] sUnits = new String[sParams];
					for (int k = 0; k<sParams; k++) {
						sValues[k] = isensor.getValuesNames()[k][0];
					}
					for (int k = 0; k<sParams; k++) {
						sUnits[k] = isensor.getValuesNames()[k][1];
					}
					args.putStringArray("sValues", sValues);
					args.putStringArray("sUnits", sUnits);
					args.putString("note", isensor.getNote()); 
				} else {
					String[] sValues = new String[1];
					String[] sUnits = new String[1];
					sValues[0] =  getString(R.string.vMic);
					sUnits[0] = "dB";
					args.putStringArray("sValues", sValues);
					args.putStringArray("sUnits", sUnits);
					args.putString("note", getString(R.string.nMic));
				}
				args.putFloatArray("min", min);
				args.putFloatArray("max", max);
				args.putFloatArray("avg", avg);
				stats.setArguments(args);
                stats.show(getSupportFragmentManager(), "stats");		
			}
		});
        
        
    }
   
    private void loggujMic(float db){
    	i++;
		if (firstvalue){
			min = new float [1];
			avg =new float[1];
			max =new float[1];
			min[0] = db;
			max[0] = db;
			avg[0] = db;
			firstvalue = false;
		}
		logparam = new float[sParams];
		txtData.setText(CONTEXT.getString(R.string.vMic)+" "+String.valueOf(db)+" dB");
		logparam[0] = db;
		if (min[0] > db) min[0] = db;
		if (max[0] < db) max[0] = db;
		avg[0]=(avg[0]*(i-1)+db)/i;
		
		logAndGraph();
    }
    
    private void stop() {
    	i=0;
		lState = 0;
		if (logger.getValues().size()>=10) {
			 DialogFragment export = new ExportFragment();
             export.show(getSupportFragmentManager(), "export");
		}
		
		//txtData.setText("");
		firstvalue = true;
	}
    
    
    private void vytvorSlistener() {
		slistener = new SensorEventListener() {
			
			String[][] sValues;
			
			@Override
			public void onSensorChanged(SensorEvent event) { 
				if (lState == 2){
					i++;
					if (firstvalue){
						sValues = isensor.getValuesNames();
						min =new float[sParams];
						avg =new float[sParams];
						max =new float[sParams];
						for (int j=0; j<sParams; j++){//nastaví statistická pole
							min[j] = event.values[j];
							max[j] = event.values[j];
							avg[j] = event.values[j];
						};
						firstvalue = false;
					}
					logparam = new float[sParams];
					txtData.setText("");
					for (int j=0; j<sParams; j++){//cyklus kdy to bude postupnì apendovat øádky dle poètu parametrù
						txtData.append(sValues[j][0]+" "+String.valueOf(event.values[j])+" "+sValues[j][1]+"\n");
						logparam[j] = event.values[j];
						if (min[j] > event.values[j]) min[j] = event.values[j];
						if (max[j] < event.values[j]) max[j] = event.values[j];
						avg[j]=(avg[j]*(i-1)+event.values[j])/i;
					};			
					logAndGraph();
				}
			}
			
			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {		
			}
		};
		
	}
    
    /**
     * Zapisuje data do grafu a logu
     */
    private void logAndGraph() {
		float x;
		float y;
		float z;
		logger.addValues(logparam);
		switch (sParams){
			case 1:
				x = logparam[0];
				graph.pridejHodnotu(i, x);
				break;
			case 2:
				x = logparam[0];
				y = logparam[1];
				graph.pridejHodnotu(i, x, y);
				break;
			case 3:
				x = logparam[0];
				y = logparam[1];
				z = logparam[2];
				graph.pridejHodnotu(i, x, y, z);
		}		
		lGraph.invalidate();
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
		lSensors.add(getString(R.string.mic));
		priSenzory.put(pozice, 100);
	}
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
    	if (dbe != null){
    		dbe = new DbEngine(mHandle);
	    	dbe.start();
	    	dbe.startDb();
    	} else {
    		SMANAGER.registerListener(slistener, sensor, SensorManager.SENSOR_DELAY_UI);
    	}
    }

	@Override
	public void onDialogPositiveClick(ExportFragment dialog) {
		String file;
		if (dbe == null) {
			file = logger.export(sensors.vratJmenoSenzoru(sensor.getType()),isensor.getValuesNames());
		} else {
			String[][] vn = new String[1][2];
			vn[0][0] =  getString(R.string.vMic);
			vn[0][1] = "dB";
			file = logger.export(getString(R.string.mic), vn);
		}
		Toast toast;
		if (file == null) {
			toast = Toast.makeText(this, getString(R.string.exportFail), Toast.LENGTH_LONG);
		} else {
			toast = Toast.makeText(this, getString(R.string.exported)+" "+file, Toast.LENGTH_LONG);
		}
		toast.show();
		logger = new SlLogger();
	}

	@Override
	public void onDialogNegativeClick(ExportFragment dialog) {
		logger = new SlLogger();
		Toast toast = Toast.makeText(this, getString(R.string.cleared), Toast.LENGTH_LONG);
		toast.show();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if (dbe != null) {
			dbe.stopDb();
		}
		else {
			SMANAGER.unregisterListener(slistener);
		}
	}
    
}
