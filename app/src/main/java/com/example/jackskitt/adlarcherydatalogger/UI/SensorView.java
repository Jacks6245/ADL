package com.example.jackskitt.adlarcherydatalogger.UI;

import android.app.Fragment;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.jackskitt.adlarcherydatalogger.Collection.FileManager;
import com.example.jackskitt.adlarcherydatalogger.Profiles.Profile;
import com.example.jackskitt.adlarcherydatalogger.R;
import com.example.jackskitt.adlarcherydatalogger.Sensors.Sensor;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

/**
 * Created by Jack Skitt on 04/04/2017.
 */

public class SensorView extends Fragment implements OnChartValueSelectedListener {

    public static int[] colours = {Color.rgb(250, 104, 104), Color.rgb(117, 201, 69), Color.rgb(60, 123, 219)};
    public Sensor viewingSensor;
    public Sensor.CHART_TYPE chartType = Sensor.CHART_TYPE.ACCELERATION;

    public  TextView     title;
    public  ToggleButton recordToggle;
    public  ToggleButton averageToggle;
    public  Button       resetButton;
    private RadioGroup   chartGroup;

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
                        chartType = Sensor.CHART_TYPE.ACCELERATION;
                        title.setText("Acceleration");

                        break;
                    case R.id.rotButton:
                        chartType = Sensor.CHART_TYPE.ROTATION;
                        title.setText("Rotation");
                        break;
                    case R.id.magButton:
                        chartType = Sensor.CHART_TYPE.COMPASS;
                        title.setText("Compass");
                        break;
                }
                viewingSensor.currentType = chartType;
            }
        });

        chartGroup.performClick();

        //sensors are null so crashes
        viewingSensor.chartViewReference = this;
        viewingSensor.charts[0] = (LineChart) view.findViewById(R.id.chart1);
        viewingSensor.charts[1] = (LineChart) view.findViewById(R.id.chart2);
        viewingSensor.charts[2] = (LineChart) view.findViewById(R.id.chart3);

        recordToggle = (ToggleButton) view.findViewById(R.id.recordToggle);
        averageToggle = (ToggleButton) view.findViewById(R.id.averageToggle);

        resetButton = (Button) view.findViewById(R.id.resetButton);

        recordToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (Profile.instance != null) {

                    MainActivity.getInstance().store.setCollectData(isChecked);
                    //if the recording has  changed from on to off
                    if (!isChecked) {
                        if (Profile.instance != null) {
                            //async task to save the  store
                            new AsyncTask<Void, Void, Void>() {
                                @Override
                                protected Void doInBackground(Void... params) {
                                    if (Profile.instance.profileCurrentSequence.getSizeOfSet() > 0) {
                                        FileManager.saveSamples(Profile.instance.profileCurrentSequence);
                                        Profile.instance.newSequence();
                                    }
                                    return null;
                                }
                            }.execute();
                        }
                    }
                } else {
                    Toast.makeText(getActivity(), "You must load or create a profile first", Toast.LENGTH_LONG).show();
                }
            }
        });

        averageToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                } else {
                    // The toggle is disabled
                }
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewingSensor != null) {
                    viewingSensor.restartCharts();
                }
            }
        });


        for (int i = 0; i < 3; i++) {
            setupChart(viewingSensor.charts[i]);
            viewingSensor.charts[i].setBackgroundColor(colours[i]);
            viewingSensor.charts[i].setGridBackgroundColor(colours[i]);
        }

        return view;
    }

    private void setupChart(LineChart chart) {


        chart.setOnChartValueSelectedListener(this);

        chart.setDrawGridBackground(true);

        chart.getDescription().setEnabled(false);

        chart.getLegend().setEnabled(false);

        chart.setDrawBorders(false);

        chart.getAxisLeft().setDrawZeroLine(true);

        chart.getAxisLeft().setDrawAxisLine(false);

        chart.getXAxis().setDrawAxisLine(true);

        chart.getXAxis().setDrawGridLines(false);

        chart.getAxisRight().setEnabled(false);
        chart.getXAxis().setEnabled(false);
        chart.setTouchEnabled(true);

        chart.setBorderColor(Color.WHITE);

        chart.setNoDataTextColor(Color.WHITE);

        chart.setAutoScaleMinMaxEnabled(false);

        chart.setDragEnabled(true);

        chart.setScaleEnabled(true);
        chart.getAxisLeft().setGridColor(Color.WHITE);
        chart.getAxisLeft().setTextColor(Color.WHITE);
        chart.getAxisLeft().setZeroLineColor(Color.BLACK);
        chart.getAxisLeft().setAxisMinimum(-3f);
        chart.getAxisLeft().setAxisMaximum(3f);
        chart.setPinchZoom(true);

        LineData data = new LineData();
        data.setValueTextColor(Color.WHITE);

        // add empty data
        chart.setData(data);


    }


    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }


}
