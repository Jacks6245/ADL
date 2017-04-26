package com.example.jackskitt.adlarcherydatalogger.Processing.SimilarityTesters;

import com.example.jackskitt.adlarcherydatalogger.Collection.Sample;
import com.example.jackskitt.adlarcherydatalogger.Processing.Matchers.EventSearch;

import java.util.ArrayList;

/**
 * Created by Jack Skitt on 21/04/2017.
 */

public abstract class SimilarityTester {
    public    int    start        = 0;
    public    int    end          = 0;
    public    double stdDeviation = 0;
    public    double mean         = 0;
    public    int    searchStart  = 0;
    protected double addedValue   = 0;
    protected int    count        = 0;
    protected double oldMean      = 0;
    protected double S            = 0;
    protected double variance     = 0;

    protected EventSearch patternMatcher;
    protected boolean canRemove = false;
    protected boolean canCount  = false;

    public SimilarityTester(EventSearch patternMatcher) {
        this.patternMatcher = patternMatcher;
    }

    public void reset() {
        oldMean = 0;
        mean = 0;
        stdDeviation = 0;
        variance = 0;
        start = 0;
        count = 0;
        canCount = false;
        canRemove = false;
        S = 0;
    }

    public abstract double getSimilarity(double addedValue);

    public void updateValues() {

        if (canCount) {//allows this to be called one iteration after the counting has started
            canRemove = true;
        }
        if (count < patternMatcher.lengthOfTemplate) {
            count++;
        }
        if (count == patternMatcher.lengthOfTemplate) {

            canCount = true;

        }
        updateStart();
    }

    public void updateStart() {
        if (start == getTestingSet().size() - patternMatcher.lengthOfTemplate) {
            start = getTestingSet().size() - patternMatcher.lengthOfTemplate;
        } else if (canRemove) {
            start++;
        } else {
            start = searchStart;
        }
        end = start + patternMatcher.lengthOfTemplate;
    }

    public void updateCalculations() {
        mean = calculateRunningMean();
        stdDeviation = calculateRunningDeviation();

    }


    public ArrayList<Sample> getTestingSet() {
        return patternMatcher.testingSequence.sequenceData[patternMatcher.sensorLookupId].getSamples();

    }

    private double calculateRunningDeviation() {
        //calculated running variance


        if (canRemove) {
            //can possibly improve  this with difference calulation, need testing
            S -= (getValueAtStartToRemove() - oldMean) * (getValueAtStartToRemove() - mean);
        }
        double delta  = addedValue - oldMean;
        double delta2 = addedValue - (mean);

        S += delta * delta2;

        //returns the new standard deviation based on the old mean count
        if (count > 1) {
            variance = S / (count - 1);
            return Math.sqrt(variance);
        } else {
            variance = 0;
            return 0;
        }
    }

    //this is a more efficent method to calculate the mean,
    //this means that it doesn't need to sum all the data each time
    private double calculateRunningMean() {
        oldMean = mean;
        //need to make sure count is increased before this method is called
        if (canRemove) {

            //can possibly improve  this with difference calulation, need testing
            mean = ((mean * count) - getValueAtStartToRemove()) / (count - 1);//removed a value from the mean
        }
        return mean + ((addedValue - mean) / count);
    }

    private double getValueAtStartToRemove() {
        return patternMatcher.getRemovedValue(getTestingSet().get((start - 1)));
    }

    public double normalizeMean(double value) {
        return ((value - mean));
    }

}
