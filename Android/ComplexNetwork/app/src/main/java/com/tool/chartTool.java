package com.tool;

import android.graphics.Color;

import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

/**
 * Created by lhy on 10/24/15.
 */
public class chartTool {
    private static XYMultipleSeriesRenderer graphRender = new XYMultipleSeriesRenderer();
    private XYMultipleSeriesDataset graphSet = new XYMultipleSeriesDataset();
    public XYMultipleSeriesDataset getGraphSet(double[] delta){
        XYSeries graphSeries = new XYSeries("xxx");
        for(int i = 0; i < delta.length; i++){
            graphSeries.add(i, delta[i]);
        }
        graphSet.addSeries(graphSeries);
        return graphSet;
    }
    public XYMultipleSeriesRenderer getRenderer(double Xmax, double Ymax){
        graphRender.setXTitle("x");
        graphRender.setYTitle("y");


        graphRender.setXAxisMin(0);
        graphRender.setXAxisMax(Xmax);

        graphRender.setYAxisMin(0);

        graphRender.setYAxisMax(Ymax);

        //graphRender.setZoomButtonsVisible(true);
        graphRender.setZoomEnabled(false);
        graphRender.setAntialiasing(true);
        graphRender.setAxesColor(Color.DKGRAY);
        graphRender.setLabelsColor(Color.LTGRAY);


        XYSeriesRenderer r = new XYSeriesRenderer();
        r.setColor(Color.BLACK);
        r.setPointStyle(PointStyle.SQUARE);
        r.setFillBelowLine(true);
        r.setFillBelowLineColor(Color.WHITE);
        r.setFillPoints(true);
        graphRender.addSeriesRenderer(r);

        graphRender.setAxisTitleTextSize(16);
        graphRender.setLabelsTextSize(15);
        graphRender.setChartTitleTextSize(20);
        graphRender.setLegendTextSize(15);
        graphRender.setYLabels(10);
        graphRender.setXLabels((int) (Xmax/6));
        graphRender.setPanEnabled(false, false);
        return graphRender;
    }
}
