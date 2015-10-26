package com.Fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.model.Graph;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.ScatterChart;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import jxl.biff.drawing.Chart;

/**
 * Created by lhy on 10/24/15.
 */
public class chartFragment extends Fragment {
    public static final String EXTRA_GRAPH = "chartFragment.graph";


    public static chartFragment newInstance(Graph graph) {

        Bundle args = new Bundle();
        args.putSerializable(EXTRA_GRAPH, graph);
        chartFragment fragment = new chartFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // graph = (Graph) getArguments().getSerializable(EXTRA_GRAPH);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       // init();
        //GraphicalView view  = ChartFactory.getScatterChartView(getActivity(), graphSet, graphRender);

        return super.onCreateView(inflater, container, savedInstanceState);
    }


}
