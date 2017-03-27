package com.example.jackskitt.adlarcherydatalogger.Collection;

import java.util.ArrayList;

import Sensors.Sensor;

public class SampleStorage {

    private ArrayList<Sample> samples = new ArrayList<Sample>();

    public ArrayList<Marker> markers = new ArrayList<Marker>();

    public Sensor sensorRef;

    public String sensorName;

    public int sensorID;

    public long lengthOfSample;

    public int listSize;

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
        lengthOfSample = samples.get(samples.size() - 1).time - samples.get(0).time;
        return lengthOfSample;

    }

    public long getLocalTime(long sensorTime) {

        return sensorTime - samples.get(0).time;
    }
}
