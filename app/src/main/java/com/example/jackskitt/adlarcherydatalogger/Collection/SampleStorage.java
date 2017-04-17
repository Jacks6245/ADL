package com.example.jackskitt.adlarcherydatalogger.Collection;

import com.example.jackskitt.adlarcherydatalogger.Sensors.Sensor;

import java.util.ArrayList;

public class SampleStorage {

    public ArrayList<Marker> getMarkers() {
        return markers;
    }

    private ArrayList<Marker> markers = new ArrayList<Marker>();
    public Sensor sensorRef;
    public String sensorName;
    public String sensorAddress;
    public int    sensorID;
    public long   lengthOfSample;
    public int    listSize;
    public  int               sizeOfDataset = 0;
    private ArrayList<Sample> samples       = new ArrayList<Sample>();

    public SampleStorage(Sensor sensor) {
        // TODO Auto-generated constructor stub
        this.sensorRef = sensor;
    }

    public ArrayList<Sample> getSamples() {
        return samples;
    }

    public void addSample(Sample toAdd) {
        if (toAdd != null) {
            samples.add(toAdd);
            listSize = samples.size();
        }
    }

    public long getTimeDifference() {
        if (samples.size() > 0) {
            lengthOfSample = samples.get(samples.size() - 1).time - samples.get(0).time;
        }
        return lengthOfSample;

    }

    public long getLocalTime(long sensorTime) {

        return sensorTime - samples.get(0).time;
    }

    public void clear() {
        markers.clear();
        lengthOfSample = 0;
        listSize = 0;
        samples.clear();
    }
}
