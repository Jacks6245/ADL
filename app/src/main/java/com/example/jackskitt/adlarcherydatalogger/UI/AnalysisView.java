package com.example.jackskitt.adlarcherydatalogger.UI;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.jackskitt.adlarcherydatalogger.Collection.Sample;
import com.example.jackskitt.adlarcherydatalogger.Collection.SampleStorage;
import com.example.jackskitt.adlarcherydatalogger.Collection.Sequence;
import com.example.jackskitt.adlarcherydatalogger.Profiles.Profile;
import com.example.jackskitt.adlarcherydatalogger.R;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;

/**
 * Created by Jack Skitt on 21/04/2017.
 */

public class AnalysisView extends Fragment implements OnChartValueSelectedListener {


    //5..8 average, min, max
    // 9 stddev
    public static int[] colours       = {Color.rgb(255, 252, 61), Color.CYAN, Color.rgb(255, 79, 249), Color.rgb(250, 104, 104), Color.rgb(117, 201, 69), Color.rgb(60, 123, 219)};
    public        int   selectedIndex = 0;
    private CheckBox[]   displayBoxes;
    private Switch       sensorSwitch;
    private Button       prev;
    private Button       next;
    private ToggleButton minMaxToggle;
    private ToggleButton averageToggle;

    private ToggleButton            stdDeviationToggle;
    private TextView                idText;
    private TextView                dateText;
    private TextView                aimTimeText;
    private TextView                correlText;
    private TextView                covarText;
    private TextView                stdDevAccText;
    private TextView                stdDevRotText;
    private TextView                averageAimTime;
    private TextView                currentSequenceText;
    private LineChart               displayChart;
    private ArrayList<ILineDataSet> bowDataSet; //0..5 movement data
    private ArrayList<ILineDataSet> gloveDataSet; //0..5 movement data

    private ArrayList<ILineDataSet> averageSets;
    private ArrayList<ILineDataSet> minMaxSets;
    private ArrayList<ILineDataSet> stdDevSets;
    private String[]  labels           = new String[]{"AccX", "AccY", "AccZ", "RotX", "RotY", "RotZ"};
    private int       visibleCount     = 0;
    private int       lastVisibleCount = 0;
    private boolean[] enabledGraphs    = new boolean[6];

    private int normalBackColor = Color.rgb(105, 105, 105);
    private int minMaxColour    = Color.argb(150, 153, 221, 255);


    private boolean oneDataSet  = false;
    private int     sensorIndex = 0;

    public AnalysisView() {
        bowDataSet = new ArrayList<>();
        gloveDataSet = new ArrayList<>();

        averageSets = new ArrayList<>();
        minMaxSets = new ArrayList<>();
        stdDevSets = new ArrayList<>();

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_analysis, container, false);

        displayBoxes = new CheckBox[6];

        loadControls(view);

        setupChart();

        if (Profile.instance != null) {
            setCheckBoxListeners();
            setupButtonListeners();

            currentSequenceText.setText(formatCurrentSequenceString(selectedIndex));
            //checkboxupdating
            updateTextValues(Profile.instance.sequenceStore.allSequences.get(selectedIndex));

        }
        displayChart.setData(new LineData(bowDataSet));
        displayChart.notifyDataSetChanged();

        return view;
    }

    private void setupButtonListeners() {
        final int sequenceSize = Profile.instance.sequenceStore.allSequences.size() - 1;
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedIndex > 0) {
                    selectedIndex--;
                }
                prev.setEnabled(selectedIndex != 0);
                next.setEnabled(selectedIndex != sequenceSize);
                currentSequenceText.setText(formatCurrentSequenceString(selectedIndex));
                loadNewSequence(selectedIndex);
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (selectedIndex < sequenceSize) {
                    selectedIndex++;
                }
                prev.setEnabled(selectedIndex != 0);
                next.setEnabled(selectedIndex != sequenceSize);
                currentSequenceText.setText(formatCurrentSequenceString(selectedIndex));
                loadNewSequence(selectedIndex);
            }
        });
        sensorSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sensorIndex = 1;
                    sensorSwitch.setText("Glove: ");
                } else {
                    sensorIndex = 0;
                    sensorSwitch.setText("Bow: ");
                }
                loadNewSequence(selectedIndex);
            }
        });
    }

    private void loadNewSequence(int index) {
        for (int i = 0; i < 6; i++) {
            bowDataSet.set(i, getAxisSet(Profile.instance.sequenceStore.allSequences.get(index).sequenceData[sensorIndex], i));
        }
        updateTextValues(Profile.instance.sequenceStore.allSequences.get(selectedIndex));
        updateChartAnimate();
    }

    public void profileLoaded() {
        processSequenceChartSet(Profile.instance.sequenceStore.allSequences.get(0), 0);
        updateStatsGraphs();
    }

    public void updateStatsGraphs() {
        processAverageLines();
        processMinMax();
        processStdDev();
    }

    private Entry processSample(Sample s, int sampleIndex) {
        return new Entry(0, (float) s.getValueFromIndex(sampleIndex));
    }

    private void processSequenceChartSet(Sequence s, int graphType) {

        for (int i = 0; i < 6; i++) {
            LineDataSet set = getAxisSet(s.sequenceData[sensorIndex], i);
            if (graphType == 0) {
                bowDataSet.add(set);
            } else if (graphType == 1) {
                set = setAverageDataSettings(set);
                averageSets.add(set);
            } else if (graphType == 2) {
                set = setMinDataSettings(set);
                minMaxSets.add(set);
            } else if (graphType == 3) {
                set = setMaxDataSettings(set);
                minMaxSets.add(set);
            } else if (graphType == 4) {
                set = setStdDevSettings(set);
                stdDevSets.add(set);
            }
        }
    }

    private void updateTextValues(Sequence s) {
        if (aimTimeText != null) {
            //formats it for ms or seconds


            aimTimeText.setText(aimTimeFormat(s.aimTime));
            idText.setText("" + s.sequenceID);
            dateText.setText(s.date);
            if (sensorIndex == 0) {
                covarText.setText("" + s.bowCovariance);
                correlText.setText("" + s.bowCorrelation);
            } else {
                covarText.setText("" + s.gloveCovariance);
                correlText.setText("" + s.gloveCorrelation);
            }
            averageAimTime.setText(aimTimeFormat(Profile.instance.sequenceStore.averageAimTime));
            stdDevAccText.setText(formatAccelerationString(Profile.instance.sequenceStore.deviationAverage[sensorIndex]));
            stdDevRotText.setText(formatRotationString(Profile.instance.sequenceStore.deviationAverage[sensorIndex]));
        }
    }

    private String formatAccelerationString(Sample s) {
        return "Acce: X: " + String.format("%.3f", s.acce.x) + " | Y: " + String.format("%.3f", s.acce.y) + " | Z: " + String.format("%.3f", s.acce.z) + " |";
    }

    private String formatRotationString(Sample s) {
        return "Rot:  X: " + String.format("%.3f", s.rot.x) + " | Y: " + String.format("%.3f", s.rot.y) + " | Z: " + String.format("%.3f", s.rot.z) + " |";
    }

    private String aimTimeFormat(double aimTime) {
        String timeScale = (aimTime * 10) + "ms";
        if (aimTime > 100) {
            float floataimTime = ((float) (aimTime)) / 100;
            timeScale = floataimTime + "s";
        }
        return timeScale;
    }

    private LineDataSet getAxisSet(SampleStorage sampleStorage, int axisToGet) {

        ArrayList<Entry> axisSet = new ArrayList<>();
//loads all the samples in a sequence into an arrow of ArrayList<Entry> for adding to a dataset
        for (int i = 0; i < sampleStorage.getSamples().size(); i++) {
            Entry sampleEntries = processSample(sampleStorage.getSamples().get(i), axisToGet);
            sampleEntries.setX(i);
            axisSet.add(sampleEntries);

        }


        LineDataSet set = new LineDataSet(axisSet, labels[axisToGet]);
        set.setLineWidth(2.5f);
        set.setDrawCircles(false);
        set.setColor(colours[axisToGet]);
        set.setVisible(enabledGraphs[axisToGet]);
        return set;
    }

    private LineDataSet setAverageDataSettings(LineDataSet set) {
        set.setColor(Color.WHITE);


        return set;
    }

    private LineDataSet setMinDataSettings(LineDataSet set) {
        set.setColor(Color.WHITE);
        set.setFillAlpha(255);
        set.setDrawFilled(true);
        set.setFillColor(Color.rgb(105, 105, 105));

        set.setFillFormatter(new IFillFormatter() {
            @Override
            public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                return displayChart.getAxisLeft().getAxisMinimum();
            }
        });
        return set;
    }

    private LineDataSet setMaxDataSettings(LineDataSet set) {
        set.setColor(Color.WHITE);
        set.setFillAlpha(255);
        set.setDrawFilled(true);
        set.setFillColor(Color.rgb(105, 105, 105));
        set.setFillFormatter(new IFillFormatter() {
            @Override
            public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                return displayChart.getAxisLeft().getAxisMaximum();
            }
        });
        return set;
    }

    private LineDataSet setStdDevSettings(LineDataSet set) {
        set.setColor(Color.WHITE);
        return set;
    }

    private void processAverageLines() {
        if (Profile.instance.sequenceStore.allSequences.size() > 1) {
            processSequenceChartSet(Profile.instance.sequenceStore.average, 1);
            bowDataSet.add(6, averageSets.get(0));

        }
    }

    private void processMinMax() {
        processSequenceChartSet(Profile.instance.sequenceStore.averageMin, 2);
        processSequenceChartSet(Profile.instance.sequenceStore.averageMax, 3);
        bowDataSet.add(7, minMaxSets.get(0));
        bowDataSet.add(8, minMaxSets.get(6));
    }

    private void processStdDev() {
        processSequenceChartSet(Profile.instance.sequenceStore.stdDeviation, 4);
        bowDataSet.add(9, stdDevSets.get(0));
    }

    private void updateChart() {

        displayChart.notifyDataSetChanged();

        displayChart.getData().notifyDataChanged();
        if (visibleCount == 1 && lastVisibleCount == 0) {
            displayChart.animateY(1000, Easing.EasingOption.EaseInOutSine);

        } else {
            displayChart.invalidate();
        }

    }

    private void updateChartAnimate() {
        displayChart.notifyDataSetChanged();

        displayChart.getData().notifyDataChanged();
        displayChart.animateX(1000, Easing.EasingOption.Linear);

    }

    private void updateVisibileLineCount(boolean plus) {
        lastVisibleCount = visibleCount;
        if (plus) {
            visibleCount++;
        } else {
            visibleCount--;

        }
    }

    private void clearVisible(int i) {
        for (int j = 0; j < 6; j++) {
            if (j != i) {
                //turn off all the other ones
                changeAxisVisibility(j, false);
            } else {
                //don't update checker box the one we want
                updateVisibileLineCount(false);
                enabledGraphs[i] = false;
                bowDataSet.get(i).setVisible(false);
            }
        }
    }

    private void setCheckBoxListeners() {
        for (int i = 0; i < displayBoxes.length; i++) {
            final int finalI = i;
            displayBoxes[i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked != enabledGraphs[finalI]) {

                        updateChartDataSets(finalI, isChecked);
                        updateChart();
                    }
                }
            });
        }

        averageToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    bowDataSet.get(6).setVisible(false);
                } else {
                    stdDeviationToggle.setChecked(false);
                    updateChartDataSets(0, true);
                }

                updateChart();
            }
        });
        minMaxToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // updateAverageChartsVisible(2);
                if (!isChecked) {
                    displayChart.setGridBackgroundColor(normalBackColor);
                    bowDataSet.get(8).setVisible(false);
                    bowDataSet.get(7).setVisible(false);

                } else {
                    stdDeviationToggle.setChecked(false);
                    displayChart.setGridBackgroundColor(minMaxColour);
                    updateChartDataSets(0, true);
                }
                updateChart();
            }
        });

        stdDeviationToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    bowDataSet.get(9).setVisible(false);
                    clearVisible(9);
                } else {
                    updateChartDataSets(0, true);
                }

                updateChart();
            }
        });
    }

    private void updateAverageChartsVisible(int indexToGet) {
        //is the user turns on the average chart, iterate through the enabled charts list
        //enabling the relevant average graph

        //chart selection 1 = average, 2 = min, 3 = max, 4 = standard Deviation
        if (bowDataSet.size() - 1 < 6) {
            bowDataSet.add(6, averageSets.get(indexToGet));

        } else {
            bowDataSet.set(6, averageSets.get(indexToGet));
        }
        changeAxisVisibility(indexToGet, true);
        bowDataSet.get(6).setVisible(true);

    }

    private void updateMinMaxChartsVisible(int indexToGet) {
        //is the user turns on the average chart, iterate through the enabled charts list
        //enabling the relevant average graph

        //chart selection 1 = average, 2 = min, 3 = max, 4 = standard Deviation
        if (bowDataSet.size() - 1 < 8) {
            bowDataSet.add(7, minMaxSets.get(indexToGet));
            bowDataSet.add(8, minMaxSets.get(indexToGet + 6));

        } else {
            bowDataSet.set(7, minMaxSets.get(indexToGet));
            bowDataSet.set(8, minMaxSets.get(indexToGet + 6));

        }
        changeAxisVisibility(indexToGet, true);
        bowDataSet.get(7).setVisible(true);
        bowDataSet.get(8).setVisible(true);


    }

    private void changeAxisVisibility(int i, boolean visible) {

        updateVisibileLineCount(visible);
        enabledGraphs[i] = visible;
        bowDataSet.get(i).setVisible(visible);
        displayBoxes[i].setChecked(visible);

    }

    private void updateChartDataSets(int i, boolean visible) {
        if (i < 6) {
            if (averageToggle.isChecked() || minMaxToggle.isChecked()) {
                manageAverageLine(i);
            } else if (stdDeviationToggle.isChecked()) {
                manageStdDeviationLine(i);
            } else {
                changeAxisVisibility(i, visible);
            }
        }
    }

    private void manageAverageLine(int i) {

        for (int j = 0; j < 6; j++) {
            if (enabledGraphs[j]) {
                changeAxisVisibility(j, false);
            }

        }
        if (averageToggle.isChecked()) {
            updateAverageChartsVisible(i);

        }
        if (minMaxToggle.isChecked()) {
            updateMinMaxChartsVisible(i);
        }
    }

    public void manageStdDeviationLine(int i) {
        clearVisible(i);
        if (averageToggle.isChecked())
            averageToggle.setChecked(false);
        if (minMaxToggle.isChecked())
            minMaxToggle.setChecked(false);

        displayBoxes[i].setChecked(true);
        bowDataSet.set(9, stdDevSets.get(i));
        bowDataSet.get(9).setVisible(true);

    }

    private void loadControls(View view) {

        displayBoxes[0] = (CheckBox) view.findViewById(R.id.rotXSelection);
        displayBoxes[1] = (CheckBox) view.findViewById(R.id.rotYSelection);
        displayBoxes[2] = (CheckBox) view.findViewById(R.id.rotZSelection);

        displayBoxes[3] = (CheckBox) view.findViewById(R.id.accXSelection);
        displayBoxes[4] = (CheckBox) view.findViewById(R.id.accYSelection);
        displayBoxes[5] = (CheckBox) view.findViewById(R.id.accZSelection);

        sensorSwitch = (Switch) view.findViewById(R.id.sensorSwitch);

        prev = (Button) view.findViewById(R.id.prevButton);
        next = (Button) view.findViewById(R.id.nextButton);

        minMaxToggle = (ToggleButton) view.findViewById(R.id.minmaxToggle);
        averageToggle = (ToggleButton) view.findViewById(R.id.averageToggle);
        stdDeviationToggle = (ToggleButton) view.findViewById(R.id.stdDeviationToggle);

        currentSequenceText = (TextView) view.findViewById(R.id.currentSequenceText);
        idText = (TextView) view.findViewById(R.id.idEntry);
        dateText = (TextView) view.findViewById(R.id.textDate);
        aimTimeText = (TextView) view.findViewById(R.id.aimEntry);
        correlText = (TextView) view.findViewById(R.id.correlationText);
        covarText = (TextView) view.findViewById(R.id.coVarText);
        stdDevAccText = (TextView) view.findViewById(R.id.stdDevAccText);
        stdDevRotText = (TextView) view.findViewById(R.id.stdDevRotText);
        averageAimTime = (TextView) view.findViewById(R.id.averageAimTimeText);

        displayChart = (LineChart) view.findViewById(R.id.chartView);
    }

    public void setupChart() {
        displayChart.setOnChartValueSelectedListener(this);

        displayChart.setDrawGridBackground(true);
        displayChart.setGridBackgroundColor(Color.rgb(105, 105, 105));
        displayChart.setBackgroundColor(Color.rgb(74, 74, 74));
        displayChart.getDescription().setEnabled(false);

        displayChart.getLegend().setEnabled(false);

        displayChart.setDrawBorders(false);

        displayChart.getAxisLeft().setDrawZeroLine(true);

        displayChart.getAxisLeft().setDrawAxisLine(false);

        displayChart.getXAxis().setDrawAxisLine(true);

        displayChart.getXAxis().setDrawGridLines(false);

        displayChart.getAxisRight().setEnabled(false);
        displayChart.getXAxis().setEnabled(false);
        displayChart.setTouchEnabled(true);

        displayChart.setBorderColor(Color.BLACK);

        displayChart.setNoDataTextColor(Color.BLACK);

        displayChart.setAutoScaleMinMaxEnabled(true);

        displayChart.setDragEnabled(true);

        displayChart.setHighlightPerDragEnabled(false);
        displayChart.setHighlightPerTapEnabled(false);
        displayChart.setScaleEnabled(true);
        displayChart.getAxisLeft().setGridColor(Color.rgb(192, 192, 192));
        displayChart.getAxisLeft().setTextColor(Color.WHITE);
        displayChart.getAxisLeft().setZeroLineColor(Color.BLACK);

        displayChart.setPinchZoom(true);

    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    private String formatCurrentSequenceString(int currentIndex) {
        return (currentIndex + 1) + "/" + Profile.instance.sequenceStore.allSequences.size();
    }

    @Override
    public void onNothingSelected() {

    }
}
