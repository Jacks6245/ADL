package com.example.jackskitt.adlarcherydatalogger.Collection;

import com.example.jackskitt.adlarcherydatalogger.Sensors.Sensor;
import com.example.jackskitt.adlarcherydatalogger.UI.MainActivity;

public class Sequence {


    //A sequence is a store of multiple senors
    public SampleStorage[] sequenceData;


    public int sequenceID;

    public boolean saved = false;

    public Sequence() {

        sequenceData = new SampleStorage[2];
        populateSequenceData();
    }

    private void populateSequenceData() {
        for (Sensor s : MainActivity.getInstance().store.sensors) {
            sequenceData[s.id] = new SampleStorage(s);
        }
    }

    public int getSizeOfSet() {
        int total = 0;
        for (SampleStorage s : sequenceData) {
            total += s.getSamples().size();
        }
        return total;
    }


}
