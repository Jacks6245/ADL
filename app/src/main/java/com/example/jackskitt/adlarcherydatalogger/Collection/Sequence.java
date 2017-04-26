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
    public ArrayList<Event> shotEvent;
    public ArrayList<Event> splitEvent;
    public int              sequenceID;
    public  boolean processed         = false;
    public  int     aimTime           = 0;
    private boolean bowDrawFound      = false;
    private boolean bowShotFound      = false;
    private boolean gloveReleaseFound = false;
    private int     desiredLength     = 500;

    public Sequence() {

        sequenceData = new SampleStorage[2];
        populateSequenceData();
        drawEvent = new ArrayList<>();
        shotEvent = new ArrayList<>();
        splitEvent = new ArrayList<>();

    }

    private void populateSequenceData() {
        for (Sensor s : MainActivity.getInstance().store.sensors) {
            sequenceData[s.id] = new SampleStorage(s);
        }
    }

    public void addSample(int i, Sample value) {

        sequenceData[i].saveSample(value);
    }

    public void removeShotFlag() {
        bowShotFound = false;
        shotEvent.remove(shotEvent.size() - 1);
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
        bowDrawFound = true;
        TemplateStore.instance.resetTemplateEvent(0);

    }

    public void resetEventFlags() {
        bowDrawFound = false;
        bowShotFound = false;
    }

    public boolean isBowShotFound() {
        return bowShotFound;
    }

    public void setBowShotFound(int start, int end, double r) {
        //TODO: detect start and end here
        shotEvent.add(new Event(start, end, r));
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
        sb.append("$" + Profile.instance.name + "," + sequenceID + "," + processed + "\n");
        for (SampleStorage s : sequenceData) {
            if (s.getSamples().size() > 0) {
                sb.append(encodeSensor(s));
            }
        }
        FileManager.saveToFile(this, sb);
    }

    public void processSequence() {
        for (int i = 0; i < sequenceData.length; i++) {
            if (i == 0) {//only check the bow sensor at the moment
                for (int j = 0; j < sequenceData[0].getSamples().size(); j++) {

                    TemplateStore.instance.checkTemplates(sequenceData[0].getSamples().get(j), this);
                }
            }
        }
        Profile.instance.newSequence();
    }

    //test loading of sequences goes here
    private String encodeSensor(SampleStorage s) {
        StringBuilder builder = new StringBuilder();
        // builds a header for the sensor, having it's id, port number and
        // length
        builder.append("#" + s.sensorRef.sensorName + "," + s.sensorRef.id + "," + s.sensorRef.sensorAddress + "," + s.getTimeDifference() + "," + aimTime + "\n");
        if (s.getEvents() != null)
            for (Event a : s.getEvents()) {
                builder.append("?" + a.startTime + "," + a.endTime + "," + a.probability + "," + a.eventType.name() + "\n");
            }
        if (s.getSamples() != null) {
            for (Sample a : s.getSamples()) {
                builder.append(a.toString());
            }
        }
        return builder.toString();
    }

    public void splitSequence() {
        for (Event split : splitEvent) {
            Sequence toReturn = new Sequence();
            toReturn.sequenceID = Profile.instance.sequenceStore.allSequences.size();
            toReturn.bowDrawFound = true;
            toReturn.bowShotFound = true;
            toReturn.processed = true;

            for (int i = 0; i < sequenceData.length; i++) {
                int start = split.startTime;
                int end   = split.endTime;
                if (split.startTime > sequenceData[i].getSamples().size() - 1) {
                    start = 0;
                    toReturn.sequenceData[i] = toReturn.sequenceData[i];
                    toReturn.sequenceData[i].blankSamples();
                }
                if (split.endTime > sequenceData[i].getSamples().size() - 1) {
                    end = sequenceData[i].getSamples().size() - 1;
                    int missingEnd = (split.endTime - TemplateStore.releaseRecordTime);//end of sequence
                    int x          = (TemplateStore.releaseRecordTime - (sequenceData[i].getSamples().size() - missingEnd)) - split.startTime;
                    endPadding(i, x);
                }


                toReturn.sequenceData[i] = sequenceData[i].split(start, end);
                if (i == 0) {
                    toReturn.calculateAimTime();
                }
                toReturn.zeroAimPadding(i);
            }
            Profile.instance.sequenceStore.allSequences.add(toReturn);

            //save the new sequence to a file
            toReturn.saveSamples();
        }
    }

    private void endPadding(int index, int amount) {
        Sample dataToAdd = sequenceData[index].getSamples().get(sequenceData[index].getSamples().size() - 1);

        for (int i = 0; i < amount; i++) {
            sequenceData[index].getSamples().add(dataToAdd);
        }
    }

    private void calculateAimTime() {
        int startOfAim = TemplateStore.instance.patternMatchers[1].lengthOfTemplate;//the starting of our aim is at the end of the template
        int endOfAim   = sequenceData[0].getSamples().size() - TemplateStore.releaseRecordTime;//get the end minus the extra data  added to the end

        aimTime = endOfAim - startOfAim;

    }

    //is already split up easier to do it on the reduced data
    public void zeroAimPadding(int index) {
        int sizeOfSet    = sequenceData[index].getSamples().size();
        int lengthToAdd  = desiredLength - (sizeOfSet);
        int paddingIndex = sizeOfSet - (TemplateStore.releaseRecordTime + 10);
        if (paddingIndex < 0) {
            paddingIndex = 0;
        }
        Sample paddingValue = sequenceData[index].getSamples().get(paddingIndex);
        for (int i = 0; i < lengthToAdd; i++) {
            sequenceData[index].getSamples().add(i + paddingIndex, paddingValue);
        }
    }

}
