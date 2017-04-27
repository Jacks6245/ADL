package com.example.jackskitt.adlarcherydatalogger.Collection;

import android.os.AsyncTask;

import com.example.jackskitt.adlarcherydatalogger.Math.MathHelper;
import com.example.jackskitt.adlarcherydatalogger.Processing.TemplateStore;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class SequenceStore {

    public ArrayList<Sequence> allSequences = new ArrayList<Sequence>();

    public Sequence average;// average of all the samples
    public Sequence averageMax;// tolerence for displaying it
    public Sequence averageMin;
    public Sequence stdDeviation;

    public Sample deviationAverage;
    public double averageAimTime;

    private float max = 1;// tolerence max and min
    private float min = 1;

    private int numSensors = 2;

    public SequenceStore() {
        deviationAverage = new Sample();

    }

    public void createStore(String user) {
        getAllLogs(user);
        Collections.sort(allSequences);

        createAverageSequence();
        createStandardDeviationSequence();
        getAverageMax();
        getAverageMin();
        calculateCorrelAndCovar();

    }

    private void createStandardDeviationSequence() {
        if (allSequences.isEmpty()) {
            System.out.print("Samples Empty");
        } else {
            stdDeviation = new Sequence();

            for (int j = 0; j < allSequences.get(0).sequenceData.length; j++) {
                //this will iterate through every sample
                for (int i = 0; i < allSequences.get(0).sequenceData[0].getSamples().size(); i++) {
                    Sample averageSample = new Sample();
                    for (Sequence seq : allSequences) {
                        //total thesample up
                        averageSample = Sample.add(averageSample, seq.sequenceData[j].getSamples().get(i));
                    }

                    averageSample = Sample.divideScalar(averageSample, allSequences.size());
                    Sample stdDevSample = new Sample();
                    for (Sequence stdseq : allSequences) {
                        //total thesample up
                        Sample tempSq = Sample.multiply(Sample.subtract(stdseq.sequenceData[j].getSamples().get(i), averageSample),
                                Sample.subtract(stdseq.sequenceData[j].getSamples().get(i), averageSample));
                        stdDevSample = Sample.add(stdDevSample, tempSq);

                    }
                    stdDevSample = Sample.divideScalar(stdDevSample, (allSequences.size() - 1));
                    stdDevSample = Sample.sqrt(stdDevSample);
                    stdDeviation.addSample(j, Sample.sqrt(stdDevSample));
                    deviationAverage = Sample.add(deviationAverage, stdDevSample);
                }
                deviationAverage = Sample.divideScalar(deviationAverage, Sequence.desiredLength);
            }
        }
    }

    private void calculateCorrelAndCovar() {
        double[][] tempAverageListGlove = new double[6][Sequence.desiredLength];
        double[][] tempAverageList      = new double[6][Sequence.desiredLength];
        boolean    averagesGot          = false;

        for (Sequence s : allSequences) {
            double[] tempList = new double[Sequence.desiredLength];


            double[] tempListGlove = new double[Sequence.desiredLength];


            double bowCorrelationTotal   = 0;
            double bowCovarienceTotal    = 0;
            double gloveCoVarienceTotal  = 0;
            double gloveCorrelationTotal = 0;

            for (int i = 0; i < 6; i++) {

                for (int j = 0; j < Sequence.desiredLength; j++) {
                    if (!averagesGot) {
                        tempAverageList[i][j] = average.sequenceData[0].getSamples().get(j).getValueFromIndex(i);
                        tempAverageListGlove[i][j] = average.sequenceData[1].getSamples().get(j).getValueFromIndex(i);

                    }

                    tempList[j] = s.sequenceData[0].getSamples().get(j).getValueFromIndex(i);
                    tempListGlove[j] = s.sequenceData[1].getSamples().get(j).getValueFromIndex(i);

                }
                double bowCovarience   = MathHelper.calculateCovariance(tempList, tempAverageList[i]);
                double gloveCovarience = MathHelper.calculateCovariance(tempList, tempAverageList[i]);

                bowCorrelationTotal += MathHelper.calcuateCorrelation(bowCovarience, Sequence.desiredLength);
                gloveCorrelationTotal += MathHelper.calcuateCorrelation(gloveCovarience, Sequence.desiredLength);

                bowCovarienceTotal += bowCovarience;
                gloveCoVarienceTotal += gloveCovarience;
            }
            averagesGot = true;
            s.bowCovariance = bowCovarienceTotal / 6;
            s.gloveCovariance = gloveCoVarienceTotal / 6;
            s.bowCorrelation = bowCorrelationTotal / 6;
            s.gloveCorrelation = gloveCorrelationTotal / 6;
        }
    }

    public void getAllLogs(String user) {
        File[] fileNames = FileManager.findAllFilesForUser(user);
        int    numSeq    = 0;
        for (File file : fileNames) {
            Sequence tempSeq;
            TemplateStore.instance.resetTemplate(0);
            tempSeq = FileManager.readFile(file);
            if (tempSeq.sequenceData[1].equals(null)) {
                numSensors = 1;
            }
            averageAimTime = tempSeq.aimTime;
            allSequences.add(tempSeq);
        }
        averageAimTime /= allSequences.size();

        System.out.println("Sequences printed: " + numSeq);
    }

    public boolean createAverageSequence() {
        if (allSequences.isEmpty()) {
            System.out.print("Samples Empty");
            return false;
        }
        average = new Sequence();

        for (int j = 0; j < allSequences.get(0).sequenceData.length; j++) {
            //this will iterate through every sample
            for (int i = 0; i < allSequences.get(0).sequenceData[0].getSamples().size(); i++) {
                Sample averageSample = new Sample();
                for (Sequence seq : allSequences) {
                    //total thesample up
                    averageSample = Sample.add(averageSample, seq.sequenceData[j].getSamples().get(i));
                }

                averageSample = Sample.divideScalar(averageSample, allSequences.size());
                average.addSample(j, averageSample);

            }
        }
        return true;
    }

    private void getAverageMax() {
        averageMax = new Sequence();
        for (int n = 0; n < numSensors; n++) {
            for (Sample s : average.sequenceData[n].getSamples()) {
                s = Sample.scalarAddition(max, s);
                averageMax.sequenceData[n].getSamples().add(s);
            }
        }
    }

    private void getAverageMin() {
        averageMin = new Sequence();
        for (int n = 0; n < numSensors; n++) {
            for (Sample s : average.sequenceData[n].getSamples()) {
                s = Sample.sclarSubtraction(min, s);
                averageMin.sequenceData[n].getSamples().add(s);
            }
        }
    }


}
