package com.example.jackskitt.adlarcherydatalogger.Collection;

import com.example.jackskitt.adlarcherydatalogger.Processing.TemplateStore;
import com.example.jackskitt.adlarcherydatalogger.Sensors.Sensor;

import java.util.ArrayList;

public class SampleStorage {

    public Sensor sensorRef;
    public String sensorName;
    public String sensorAddress;
    public int    sensorID;
    public long   lengthOfSample;
    public int    listSize;
    public  int               sizeOfDataset = 0;
    private ArrayList<Event>  events        = new ArrayList<Event>();
    private ArrayList<Sample> samples       = new ArrayList<Sample>();

    public SampleStorage(Sensor sensor) {
        // TODO Auto-generated constructor stub
        this.sensorRef = sensor;
    }

    public ArrayList<Event> getEvents() {
        return events;
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
        SampleStorage toReturn = new SampleStorage(this.sensorRef);
        toReturn.sensorAddress = this.sensorAddress;
        toReturn.sensorID = this.sensorID;
        toReturn.sensorName = this.sensorName;
        toReturn.samples = new ArrayList<>();
        if (samples.size() > end) {
            for (int i = start; i < end; i++) {
                toReturn.samples.add(samples.get(i));
            }
        } else {
            toReturn.samples = new ArrayList<>();
        }
        return toReturn;
    }

    public void blankSamples() {
        samples = new ArrayList<>();
    }

    public double[] splitIntoArray(int index, int start, int end) {
        double[] toReturn = new double[end - start];
        if (samples.size() > end) {
            for (int i = 0; i < (end - start); i++) {
                toReturn[i] = TemplateStore.instance.getSearcher(index).getRemovedValue(samples.get((start + i) + 1));
            }
        }
        return toReturn;
    }

    public void clear() {
        events.clear();
        lengthOfSample = 0;
        listSize = 0;
        samples.clear();
    }
}
