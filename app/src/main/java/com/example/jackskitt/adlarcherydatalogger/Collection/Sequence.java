package com.example.jackskitt.adlarcherydatalogger.Collection;

import com.example.jackskitt.adlarcherydatalogger.Processing.TemplateStore;
import com.example.jackskitt.adlarcherydatalogger.Profiles.Profile;
import com.example.jackskitt.adlarcherydatalogger.Sensors.Sensor;
import com.example.jackskitt.adlarcherydatalogger.UI.MainActivity;

import java.util.ArrayList;

public class Sequence {


    //A sequence is a store of multiple senors
    public SampleStorage[] sequenceData;

    public ArrayList<Event> drawEvent;
    public Event            shotEvent;
    public int              sequenceID;
    public  boolean saved             = false;
    private boolean bowDrawFound      = false;
    private boolean bowShotFound      = false;
    private boolean gloveReleaseFound = false;

    public Sequence() {

        sequenceData = new SampleStorage[2];
        populateSequenceData();
        drawEvent = new ArrayList<>();
    }

    private void populateSequenceData() {
        for (Sensor s : MainActivity.getInstance().store.sensors) {
            sequenceData[s.id] = new SampleStorage(s);
        }
    }

    public void addSample(int i, Sample value) {

        sequenceData[i].saveSample(value);
        TemplateStore.instance.checkTemplates(value, this);
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
        drawEvent.add(new Event(start, end, r));
        TemplateStore.instance.resetTemplateEvent(0);

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

    public void saveSamples() {
        StringBuilder sb = new StringBuilder();
        FileManager.doesDirectoryExist();
        sb.append("$" + Profile.instance.name + "," + sequenceID + "\n");
        for (SampleStorage s : sequenceData) {
            if (s.getSamples().size() > 0) {
                sb.append(encodeSensor(s));
            }
        }
        FileManager.saveToFile(this, sb);
    }

    private String encodeSensor(SampleStorage s) {
        StringBuilder builder = new StringBuilder();
        // builds a header for the s ensor, having it's id, port number and
        // length
        builder.append("#" + s.sensorRef.mBluetoothGatt.getDevice().getName() + "," + s.sensorRef.id + "," + s.sensorRef.mBluetoothGatt.getDevice() + "," + s.getTimeDifference() + "\n");
        if (s.getEvents() != null)
            for (Event a : s.getEvents()) {
                builder.append("?" + a.startTime + "," + a.endTime + "," + a.probability + "," + a.eventType.name() + "\n");
            }
        if (s.getSamples() != null) {
            for (Sample a : s.getSamples()) {
                TemplateStore.instance.checkTemplates(a, this);
                builder.append(a.toString());
            }

        }

        return builder.toString();
    }

    public Sequence splitSequence(int start, int end) {
        Sequence toReturn = this;
        toReturn.sequenceID = Profile.instance.sequenceStore.allSequences.size();
        for (int i = 0; i < sequenceData.length; i++) {

            toReturn.sequenceData[i] = sequenceData[i].split(start, end);
        }
        return toReturn;
    }

}
