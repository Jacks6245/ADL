package com.example.jackskitt.adlarcherydatalogger.Collection;

import android.support.annotation.NonNull;

import com.example.jackskitt.adlarcherydatalogger.Processing.FeatureSelectorStore;
import com.example.jackskitt.adlarcherydatalogger.Profiles.Profile;
import com.example.jackskitt.adlarcherydatalogger.Sensors.Sensor;
import com.example.jackskitt.adlarcherydatalogger.UI.MainActivity;

import java.util.ArrayList;

//A sequence respresents a shot sequence, it has the storage for both the sensors in and contains references to each different type of event.
public class Sequence implements Comparable<Sequence> {


    public static int desiredLength = 500;
    //A sequence is a store of multiple senors
    public SampleStorage[]  sequenceData;
    public ArrayList<Event> drawEvent;
    public ArrayList<Event> shotEvent;
    public ArrayList<Event> splitEvent;
    public int              sequenceID;

    public double bowCovariance    = 0;
    public double gloveCovariance  = 0;
    public double bowCorrelation   = 0;
    public double gloveCorrelation = 0;

    public boolean processed = false;
    public int     aimTime   = 0;
    public String date;
    private boolean bowDrawFound      = false;
    private boolean bowShotFound      = false;
    private boolean gloveReleaseFound = false;

    public Sequence() {

        sequenceData = new SampleStorage[2];
        populateSequenceData();
        drawEvent = new ArrayList<>();
        shotEvent = new ArrayList<>();
        splitEvent = new ArrayList<>();

    }

    private void populateSequenceData() {
        if (MainActivity.getInstance() != null) {
            for (Sensor s : MainActivity.getInstance().store.sensors) {
                sequenceData[s.id] = new SampleStorage(s);
            }
        }
    }

    public void addSample(int index, Sample value) {
        sequenceData[index].saveSample(value);
    }

    public void removeShotFlag() {
        //removed the last shot event added to the sequence
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

    //adds an event to the draw event array, sets the flags and resets the search template
    public void setBowDrawFound(int start, int end, double r) {
        drawEvent.add(new Event(start, end, r));
        bowDrawFound = true;
        FeatureSelectorStore.instance.resetTemplateEvent(0);

    }

    public void resetEventFlags() {
        bowDrawFound = false;
        bowShotFound = false;
    }

    public void resetSequence() {
        populateSequenceData();

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

    //saves the samples by creating a file, encoding the header the loops throuhg each SampleStore converting  it to a string and writes to it.
    public void saveSamples() {
        StringBuilder sb = new StringBuilder();
        FileManager.doesDirectoryExist();
        sb.append("$" + Profile.instance.name + "," + sequenceID + "," + processed + "\n");
        for (SampleStorage s : sequenceData) {
            if (s.getSamples().size() > 0) {
                sb.append(encodeSensor(s));//encodes the current sensor
            }
        }
        FileManager.saveToFile(this, sb);//saves the string builderr to a file
    }

    //runs the sequence through the template matchers this is performed incrementally
    public void processSequence() {
        processed = true;
        for (int i = 0; i < sequenceData.length; i++) {
            if (i == 0) {//only check the bow sensor at the moment
                for (int j = 0; j < sequenceData[0].getSamples().size(); j++) {
                    FeatureSelectorStore.instance.checkTemplates(sequenceData[0].getSamples().get(j), this);
                }
                splitSequence();
            }
        }
    }

    //encoding of the sensors, loops through all the samples adding them to the sequence builder
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

    //splits the sequence up into multiple sequences based on the split events which are calculated from the template matchers
    public void splitSequence() {
        for (Event split : splitEvent) {
            //the new sequence
            Sequence toReturn = new Sequence();
            toReturn.sequenceID = Profile.instance.sequenceStore.allSequences.size() + 2;
            toReturn.bowDrawFound = true;
            toReturn.bowShotFound = true;
            toReturn.processed = true;
            //splits both the SampleStores at the same positions
            for (int i = 0; i < sequenceData.length; i++) {
                if (sequenceData.length > 0) {
                    int start = split.startTime;
                    int end   = split.endTime;
                    //checks the sample size has enough data in it if not it adds blank data
                    if (split.startTime > sequenceData[i].getSamples().size() - 1) {
                        start = 0;
                        toReturn.sequenceData[i] = toReturn.sequenceData[i];
                        toReturn.sequenceData[i].blankSamples();

                        end = sequenceData[i].getSamples().size() - 1;
                        int missingEnd = (split.endTime - FeatureSelectorStore.releaseRecordTime);//end of sequence
                        int x          = (FeatureSelectorStore.releaseRecordTime - (sequenceData[i].getSamples().size() - missingEnd)) - split.startTime;
                        endPadding(i, x);
                    }

                    //splits the main sequence and assigns the output to the temp sequence
                    toReturn.sequenceData[i] = sequenceData[i].split(start, end);
                    if (i == 0) {
                        //calculates the aim time for this sequence
                        toReturn.calculateAimTime();
                    }
                    //adds zero padding at the end of the aim period, this is to get each sample synced up with each other
                    toReturn.zeroAimPadding(i);
                }
                //adds padding to the end to make sure each sample is the sample length
                endPadding(i, Sequence.desiredLength);
            }
            //adds the temp sequence to the profile
            Profile.instance.sequenceStore.allSequences.add(toReturn);

            //save the new sequence to a file
            toReturn.saveSamples();

        }
        //updates the average, min/max and standard deviation sequences for the analysis screen
        Profile.instance.processProfile();
        if (Profile.instance.sequenceStore.allSequences.size() == splitEvent.size()) {
            MainActivity.getInstance().adapter.analysisView.profileLoaded();
        } else {
            MainActivity.getInstance().adapter.analysisView.updateStatsGraphs();
        }
    }

    //copies the sample at the end of the sequence the amount of times is required to reach the desired length
    public void endPadding(int index, int amount) {
        Sample dataToAdd;
        if (sequenceData[index].getSamples().size() > 0) {
            dataToAdd = sequenceData[index].getSamples().get(sequenceData[index].getSamples().size() - 1);
        } else {
            dataToAdd = new Sample();
        }

        for (int i = 0; i < amount; i++) {
            sequenceData[index].getSamples().add(dataToAdd);
        }
    }


    //calculates the aim time of the sequence based on the start of the stot-the end of the draw
    private void calculateAimTime() {
        int startOfAim = FeatureSelectorStore.instance.eventSearchers[1].lengthOfTemplate;//the starting of our aim is at the end of the template
        int endOfAim   = sequenceData[0].getSamples().size() - FeatureSelectorStore.releaseRecordTime;//get the end minus the extra data  added to the end

        aimTime = endOfAim - startOfAim;
    }

    //adds padded data to make the values add up
    //is already split up easier to do it on the reduced data
    public void zeroAimPadding(int index) {
        int sizeOfSet    = sequenceData[index].getSamples().size();
        int lengthToAdd  = desiredLength - (sizeOfSet);
        int paddingIndex = sizeOfSet - (FeatureSelectorStore.releaseRecordTime + 10);
        if (paddingIndex <= 0) {
            paddingIndex = 0;
        }
        if (sizeOfSet > 0) {
            Sample paddingValue = sequenceData[index].getSamples().get(paddingIndex);
            for (int i = 0; i < lengthToAdd; i++) {
                sequenceData[index].getSamples().add(i + paddingIndex, paddingValue);
            }
        }
    }


    @Override
    public int compareTo(@NonNull Sequence another) {
        return (this.sequenceID < another.sequenceID ? -1 : this.sequenceID == another.sequenceID ? 0 : 1);
    }
}
