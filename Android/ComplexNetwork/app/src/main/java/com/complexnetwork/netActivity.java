package com.complexnetwork;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.tool.chartTool;
import com.tool.xlsTool;
import com.model.Graph;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;

import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.io.IOException;

import jxl.read.biff.BiffException;

public class netActivity extends AppCompatActivity {
    private Graph graph;
    private String graphTitle;
    private XYMultipleSeriesDataset graphSet = new XYMultipleSeriesDataset();
    private XYMultipleSeriesRenderer graphRender = new XYMultipleSeriesRenderer();
    //private XYSeries graphSeries = new XYSeries();
    private static final String TAG = "netActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net);
        try {
            xlsTool tool = new xlsTool(getResources().openRawResource(R.raw.data));
            String[][] data = tool.readSheet(1);
            graph = new Graph(data);
            Graph tmp = graph;
            tmp.computeCoreness();
            graph.setCoreness(tmp.getCoreness());

            Log.v(TAG, String.valueOf(graph.getCoef()));
            Log.v(TAG, String.valueOf(graph.getAvgLength()));
            Log.v(TAG, String.valueOf(graph.getCoreness()));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (BiffException e) {
            e.printStackTrace();
        }

        init();
        Intent intent = ChartFactory.getScatterChartIntent(this, graphSet, graphRender);
        startActivity(intent);
    }
    public void init(){
        double[] delta = graph.getDelta();
        chartTool  chart = new chartTool();
        graphSet = chart.getGraphSet(delta);
        double Xmax = delta.length+5;
        double Ymax = delta[0];
        for(int i = 0; i < delta.length; i++){
            if(delta[i] > Ymax)
                Ymax = delta[i];
        }

        // set x/y title
        graphRender = new chartTool().getRenderer(Xmax, Ymax);

    }

}
