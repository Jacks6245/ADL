package com.example.jackskitt.adlarcherydatalogger.Collection;

import com.example.jackskitt.adlarcherydatalogger.Math.MathHelper;
import com.example.jackskitt.adlarcherydatalogger.Processing.TemplateStore;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
//the sequenceStore contains a list of all the current sequences for the profile and claculates all the averages, min-max and standard deviation sequences

public class SequenceStore {

    public ArrayList<Sequence> allSequences = new ArrayList<Sequence>();

    public Sequence average;// average of all the samples
    public Sequence averageMax;// tolerence for displaying it
    public Sequence averageMin;
    public Sequence stdDeviation;

    public Sample[] deviationAverage;
    public double   averageAimTime;

    private float max = 1;// tolerence max and min
    private float min = 1;

    private int numSensors = 2;

    public SequenceStore() {
        deviationAverage = new Sample[]{new Sample(), new Sample()};

    }

    //loads all the sequences from files for a user and sorts them in acsending order
    public void createStore(String user) {
        getAllLogs(user);
        Collections.sort(allSequences);

    }

    //creates the sequence for standard deviationn
    public void createStandardDeviationSequence() {
        if (allSequences.isEmpty()) {
            System.out.print("Samples Empty");
        } else {


            for (int j = 0; j < allSequences.get(0).sequenceData.length; j++) {
                stdDeviation = new Sequence();
                int length = allSequences.get(0).sequenceData[j].getSamples().size();
                //this will iterate through every sample, might be able to improve by using the previous average calculations
                for (int i = 0; i < length; i++) {
                    //creates the average sample
                    Sample averageSample = new Sample();
                    for (Sequence seq : allSequences) {
                        //total thesample up
                        if (seq.sequenceData[j].getSamples().size() == length) {
                            averageSample = Sample.add(averageSample, seq.sequenceData[j].getSamples().get(i));
                        }
                    }

                    averageSample = Sample.divideScalar(averageSample, allSequences.size());
                    //creates the standard deviation
                    Sample stdDevSample = new Sample();
                    for (Sequence stdseq : allSequences) {
                        //total thesample up
                        if (stdseq.sequenceData[j].getSamples().size() == length) {
                            Sample tempSq = Sample.multiply(Sample.subtract(stdseq.sequenceData[j].getSamples().get(i), averageSample),
                                    Sample.subtract(stdseq.sequenceData[j].getSamples().get(i), averageSample));
                            stdDevSample = Sample.add(stdDevSample, tempSq);
                        }

                    }
                    stdDevSample = Sample.divideScalar(stdDevSample, (allSequences.size() - 1));
                    stdDevSample = Sample.sqrt(stdDevSample);
                    stdDeviation.addSample(j, Sample.sqrt(stdDevSample));
                    deviationAverage[j] = Sample.add(deviationAverage[j], stdDevSample);
                }
                deviationAverage[j] = Sample.divideScalar(deviationAverage[j], Sequence.desiredLength);
            }
        }
    }

    public void calculateCorrelAndCovar() {
        double[][] tempAverageListGlove = new double[6][Sequence.desiredLength];
        double[][] tempAverageList      = new double[6][Sequence.desiredLength];
        boolean    averagesGot          = false;

        for (Sequence s : allSequences) {
            double[] tempList = new double[Sequence.desiredLength];


            double[] tempListGlove = new double[Sequence.desiredLength];

//gets the
            double bowCorrelationTotal   = 0;
            double bowCovarienceTotal    = 0;
            double gloveCoVarienceTotal  = 0;
            double gloveCorrelationTotal = 0;

            for (int i = 0; i < 6; i++) {

                for (int j = 0; j < Sequence.desiredLength; j++) {
//gets the average f or this data sample
                    if (!averagesGot) {
                        tempAverageList[i][j] = average.sequenceData[0].getSamples().get(j).getValueFromIndex(i);
                        if (s.sequenceData[1].getSamples().size() != 0) {
                            tempAverageListGlove[i][j] = average.sequenceData[1].getSamples().get(j).getValueFromIndex(i);
                        } else {
                            tempAverageListGlove[i][j] = 0;
                        }

                    }
                    //gets the data from the current sequence
                    if (s.sequenceData[0].getSamples().size() != 0) {
                        tempList[j] = s.sequenceData[0].getSamples().get(j).getValueFromIndex(i);
                    } else {
                        tempList[j] = 0;
                    }
                    if (s.sequenceData[1].getSamples().size() != 0) {
                        tempListGlove[j] = s.sequenceData[1].getSamples().get(j).getValueFromIndex(i);
                    } else {
                        tempListGlove[j] = 0;
                    }
                }
                //calculated covariance from math helper class
                double bowCovarience   = MathHelper.calculateCovariance(tempList, tempAverageList[i]);
                double gloveCovarience = MathHelper.calculateCovariance(tempListGlove, tempAverageListGlove[i]);
                //calcualting the correlation for the current sample vs the average
                bowCorrelationTotal += MathHelper.calcuateCorrelation(bowCovarience, tempList, tempAverageList[i]);
                gloveCorrelationTotal += MathHelper.calcuateCorrelation(gloveCovarience, tempListGlove, tempAverageListGlove[i]);

                bowCovarienceTotal += bowCovarience;
                gloveCoVarienceTotal += gloveCovarience;
            }
            //gets the average of each
            averagesGot = true;
            s.bowCovariance = bowCovarienceTotal / 6;
            s.gloveCovariance = gloveCoVarienceTotal / 6;
            s.bowCorrelation = bowCorrelationTotal / 6;
            s.gloveCorrelation = gloveCorrelationTotal / 6;
        }

    }

    public void getAllLogs(String user) {
        //gets all the files for the user and processes them into sequences, also calcaulated the average a im time
        File[] fileNames = FileManager.findAllFilesForUser(user);
        int    numSeq    = 0;
        for (File file : fileNames) {
            Sequence tempSeq;
            TemplateStore.instance.resetTemplate(0);
            tempSeq = FileManager.readFile(file);
            if (tempSeq.sequenceData[1].equals(null)) {
                numSensors = 1;
            }
            averageAimTime += tempSeq.aimTime;
            allSequences.add(tempSeq);
        }

        //allSequences.get(0).processSequence();

        averageAimTime /= allSequences.size();

        System.out.println("Sequences printed: " + numSeq);
    }

    //gets the average of all the sequences to create a average value
    public boolean createAverageSequence() {
        if (allSequences.isEmpty()) {
            System.out.print("Samples Empty");
            return false;
        }

        average = new Sequence();

        for (int j = 0; j < allSequences.get(0).sequenceData.length; j++) {
            int length = allSequences.get(0).sequenceData[j].getSamples().size();
            //this will iterate through every sample
            for (int i = 0; i < length; i++) {
                Sample averageSample = new Sample();
                for (Sequence seq : allSequences) {
                    //total thesample up
                    if (seq.sequenceData[j].getSamples().size() == length) {
                        averageSample = Sample.add(averageSample, seq.sequenceData[j].getSamples().get(i));
                    }
                }

                averageSample = Sample.divideScalar(averageSample, allSequences.size());
                average.addSample(j, averageSample);

            }
        }
        //use this to update the model
        //average.saveSamples();
        return true;
    }

    //gets the min and max ranges by adding a pre defined range to the average
    public void getAverageMax() {
        averageMax = new Sequence();
        for (int n = 0; n < numSensors; n++) {
            for (Sample s : average.sequenceData[n].getSamples()) {
                s = Sample.scalarAddition(max, s);
                averageMax.sequenceData[n].getSamples().add(s);
            }
        }
    }

    public void getAverageMin() {
        averageMin = new Sequence();
        for (int n = 0; n < numSensors; n++) {
            for (Sample s : average.sequenceData[n].getSamples()) {
                s = Sample.sclarSubtraction(min, s);
                averageMin.sequenceData[n].getSamples().add(s);
            }
        }
    }
}
