package com.example.jackskitt.adlarcherydatalogger.Collection;

import com.example.jackskitt.adlarcherydatalogger.Processing.TemplateStore;
import com.example.jackskitt.adlarcherydatalogger.Sensors.Sensor;
import com.example.jackskitt.adlarcherydatalogger.UI.MainActivity;

public class Sequence {


    //A sequence is a store of multiple senors
    public SampleStorage[] sequenceData;

    public Event drawEvent;
    public Event shotEvent;
    public int   sequenceID;
    public  boolean saved             = false;
    private boolean bowDrawFound      = false;
    private boolean bowShotFound      = false;
    private boolean gloveReleaseFound = false;

    public Sequence() {

        sequenceData = new SampleStorage[2];
        populateSequenceData();
    }

    private void populateSequenceData() {
        for (Sensor s : MainActivity.getInstance().store.sensors) {
            sequenceData[s.id] = new SampleStorage(s);
        }
    }

    public void addSample(int i, Sample value) {

        sequenceData[i].saveSample(value);
        if (i == 0) {
            TemplateStore.instance.checkTemplates(value);
        }

    }

    public int getSizeOfSet() {
        int total = 0;
        for (SampleStorage s : sequenceData) {
            total += s.getSamples().size();
        }
        return total;
    }

    public boolean isBowDrawFound() {
        return bowDrawFound;
    }

    public void setBowDrawFound(int start, int end, double r) {
        drawEvent = new Event(start, end, r);
        this.bowDrawFound = true;
    }

    public boolean isBowShotFound() {
        return bowShotFound;
    }

    public void setBowShotFound(int start, int end, double r) {
        //TODO: detect start and end here
        shotEvent = new Event(start, end, r);
        this.bowShotFound = true;
    }

    public boolean isGloveReleaseFound() {
        return gloveReleaseFound;
    }

    public void setGloveReleaseFound(int start, int end, double r) {
        this.gloveReleaseFound = true;
    }

}
