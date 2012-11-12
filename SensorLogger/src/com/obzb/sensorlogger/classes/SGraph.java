package com.obzb.sensorlogger.classes;

import android.content.Context;
import android.graphics.Color;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.LegendAlign;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphViewSeries.GraphViewStyle;
import com.jjoe64.graphview.LineGraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.obzb.sensorlogger.MainActivity;

public class SGraph {
	private Context c;
	private GraphViewSeries graphSer1;
	private GraphViewSeries graphSer2;
	private GraphViewSeries graphSer3;
	private GraphViewData[] graphData;
	private int sParams;
	
	public SGraph () {
		this.c = MainActivity.CONTEXT;
		graphData = new GraphViewData[] {  
			      new GraphViewData(0, 0)};  
	}
	
	//Graf
    public GraphView kresliGraf(int sParams, String[][] names){
    	this.sParams = sParams;
    	GraphView graphView = new LineGraphView(  
    	      c // context  
    	      , "Sensor Log" // heading  
    	);  
    	switch (sParams){
    	case 1: 
    		graphSer1 = new GraphViewSeries(names[0][0], new GraphViewStyle(Color.BLUE, 2), graphData);
    		graphView.addSeries(graphSer1);
    		break;
    	case 2:
    		graphSer1 = new GraphViewSeries(names[0][0], new GraphViewStyle(Color.BLUE, 2), graphData);
    		graphView.addSeries(graphSer1);
    		graphSer2 = new GraphViewSeries(names[1][0], new GraphViewStyle(Color.GREEN, 2), graphData);
    		graphView.addSeries(graphSer2);
    		break;
    	case 3:
    		graphSer1 = new GraphViewSeries(names[0][0], new GraphViewStyle(Color.BLUE, 2), graphData);
    		graphView.addSeries(graphSer1);
    		graphSer2 = new GraphViewSeries(names[1][0], new GraphViewStyle(Color.GREEN, 2), graphData);
    		graphView.addSeries(graphSer2);
    		graphSer3 = new GraphViewSeries(names[2][0], new GraphViewStyle(Color.RED, 2), graphData);
    		graphView.addSeries(graphSer3);
    		break;
    	} 
    	graphView.setViewPort(0, 100);
    	//graphView.setScrollable(true);
    	graphView.setScalable(true);
    	graphView.setShowLegend(true);
    	graphView.setLegendAlign(LegendAlign.BOTTOM); 
    	  
    	return graphView;
    }
    
    public void pridejHodnotu(float x, float y1){
    	graphSer1.appendData(new GraphViewData(x, y1), true);	
    }
    
    public void reset(){
    	switch (sParams){
    	case 1:
    		graphSer1.resetData(graphData);
    		break;
    	case 2:
    		graphSer1.resetData(graphData);
    		graphSer2.resetData(graphData);
    		break;
    	case 3:
    		graphSer1.resetData(graphData);
    		graphSer2.resetData(graphData);
    		graphSer3.resetData(graphData);
    		break;
    	}
    }
    
    public void pridejHodnotu(float x, float y1, float y2){
    	graphSer1.appendData(new GraphViewData(x, y1), true);
    	graphSer2.appendData(new GraphViewData(x, y2), true);
    }
    
    public void pridejHodnotu(float x, float y1, float y2, float y3){
    	graphSer1.appendData(new GraphViewData(x, y1), true);
    	graphSer2.appendData(new GraphViewData(x, y2), true);
    	graphSer3.appendData(new GraphViewData(x, y3), true);
    }

}
