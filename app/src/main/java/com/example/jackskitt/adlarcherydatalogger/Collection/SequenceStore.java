package com.example.jackskitt.adlarcherydatalogger.Collection;

import com.example.jackskitt.adlarcherydatalogger.Adapters.ProfileListValue;

import java.io.File;
import java.util.ArrayList;

public class SequenceStore {

    public ArrayList<Sequence> allSequences = new ArrayList<Sequence>();

    public Sequence average;// average of all the samples
    public Sequence averageMax;// tolerence for displaying it
    public Sequence averageMin;

    private float max = 10;// tolerence max and min
    private float min = 10;

    private int numSensors = 2;

    public SequenceStore() {


    }

    public void createStore(String user) {
        getAllLogs(user);
        // createAverageSequence();
        //getAverageMax();
        //getAverageMin();

    }
    public void getAllLogs(String user) {
        File[] fileNames = FileManager.findAllFilesForUser(user);
        int    numSeq    = 0;
        for (File file : fileNames) {
            Sequence tempSeq = new Sequence();
            allSequences.add(tempSeq);
            FileManager.readFile(file);
            if (tempSeq.sequenceData[1].equals(null)) {
                numSensors = 1;
            }
            //   allSequences.add(tempSeq);
        }

        System.out.println("Sequences printed: " + numSeq);
    }

    public boolean createAverageSequence() {
        if (allSequences.isEmpty()) {
            System.out.print("Samples Empty");
            return false;
        }

        Sequence shortest = getShortestSequence();
        for (int n = 0; n < numSensors; n++) {
            for (int i = 0; i < shortest.sequenceData[n].listSize; i++) {

                Sample averageSample = new Sample();

                for (int o = 0; o < allSequences.size(); o++) {
                    averageSample = Sample.add(averageSample, allSequences.get(o).sequenceData[n].getSamples().get(i));
                }
                averageSample = Sample.divideScalar(averageSample, allSequences.size());
                average.addSample(n, averageSample);

            }
        }

        return true;
    }

    private void getAverageMax() {
        for (int n = 0; n < numSensors; n++) {
            for (Sample s : average.sequenceData[n].getSamples()) {
                s = Sample.scalarAddition(max, s);
                averageMax.sequenceData[n].getSamples().add(s);
            }
        }
    }

    private void getAverageMin() {
        for (int n = 0; n < numSensors; n++) {
            for (Sample s : average.sequenceData[n].getSamples()) {
                s = Sample.scalarAddition(min, s);
                averageMin.sequenceData[n].getSamples().add(s);
            }
        }
    }

    public Sequence getShortestSequence() {
        int shortest = 0;
        for (int i = 0; i < allSequences.size(); i++) {
            //they should both be the same length in the sensor array
            if (allSequences.get(i).sequenceData[0].listSize < allSequences.get(shortest).sequenceData[0].listSize) {
                shortest = i;
            }


        }
        return allSequences.get(shortest);
    }

    public Sequence getShortestTimeSequence() {
        int shortest = 0;
        for (int i = 0; i < allSequences.size(); i++) {
            if (allSequences.get(i).sequenceData[0].lengthOfSample < allSequences
                    .get(shortest).sequenceData[0].lengthOfSample) {
                shortest = i;
            }

        }
        return allSequences.get(shortest);
    }
}
