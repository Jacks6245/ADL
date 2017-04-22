package com.example.jackskitt.adlarcherydatalogger.Collection;

import com.example.jackskitt.adlarcherydatalogger.Sensors.Sensor;

import java.util.ArrayList;

public class SampleStorage {

    public ArrayList<Event> getEvents() {
        return events;
    }

    private ArrayList<Event> events = new ArrayList<Event>();
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

    public void saveSample(Sample toAdd) {
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

    public SampleStorage split(int start, int end) {
        SampleStorage toReturn = this;

        toReturn.samples = (ArrayList<Sample>) samples.subList(start, end);
        return toReturn;
    }
    public void clear() {
        events.clear();
        lengthOfSample = 0;
        listSize = 0;
        samples.clear();
    }
}
