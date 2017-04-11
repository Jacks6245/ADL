package com.example.jackskitt.adlarcherydatalogger.UI;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.jackskitt.adlarcherydatalogger.R;
import com.example.jackskitt.adlarcherydatalogger.Sensors.Sensor;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

/**
 * Created by Jack Skitt on 04/04/2017.
 */

public class SensorView extends Fragment implements OnChartValueSelectedListener {

    public Sensor viewingSensor;

    private enum Selection {
        ACCELERATION,
        ROTATION,
        COMPASS
    }

    public Selection chartType = Selection.ACCELERATION;

    public TextView title;

    private RadioGroup chartGroup;

    public static int[] colours = {Color.rgb(250, 104, 104), Color.rgb(117, 201, 69), Color.rgb(60, 123, 219)};



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sensors, container, false);

        chartGroup = (RadioGroup) view.findViewById(R.id.chartRadio);

        title = (TextView) view.findViewById(R.id.chartTitle);
//Allows the user to change the chart type
        chartGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {

                    case R.id.accButton:
                        chartType = Selection.ACCELERATION;
                        title.setText("Acceleration");

                        break;
                    case R.id.rotButton:
                        chartType = Selection.ROTATION;
                        title.setText("Rotation");
                        break;
                    case R.id.magButton:
                        chartType = Selection.COMPASS;
                        title.setText("Compass");
                        break;
                }
            }
        });

        chartGroup.performClick();

        //sensors are null so crashes
        viewingSensor.charts[0] = (LineChart) view.findViewById(R.id.chart1);
        viewingSensor.charts[1] = (LineChart) view.findViewById(R.id.chart2);
        viewingSensor.charts[2] = (LineChart) view.findViewById(R.id.chart3);

        for (int i = 0; i < 3; i++) {
            setupChart(viewingSensor.charts[i]);
            viewingSensor.charts[i].setBackgroundColor(colours[i]);
        }

        return view;
    }

    private void setupChart(LineChart chart) {
        chart.setOnChartValueSelectedListener(this);

        chart.setDrawGridBackground(true);

        chart.getDescription().setEnabled(true);

        chart.setDrawBorders(true);

        chart.getAxisLeft().setDrawZeroLine(true);

        chart.getAxisLeft().setDrawAxisLine(true);

        chart.getXAxis().setDrawAxisLine(true);

        chart.getXAxis().setDrawGridLines(true);

        chart.setTouchEnabled(true);

        chart.setDragEnabled(true);

        chart.setScaleEnabled(true);

        chart.setPinchZoom(true);

    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }


}
