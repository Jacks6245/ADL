package com.example.jackskitt.adlarcherydatalogger.Processing;

import com.example.jackskitt.adlarcherydatalogger.Collection.Sample;

import java.util.ArrayList;

/**
 * Created by Jack Skitt on 18/04/2017.
 */

public class Correlation extends SimilarityTester {
    private double covariance = 0;

    public Correlation(PatternMatcher patternMatcher) {
        super(patternMatcher);

    }

    //this class works out the pearson correlation of the cururent incoming data versus the data templates, it first finds the minimum data
    //this data normalizes the data so that we are matching the shape rather than power levels, it then calculates mean and standard de


    @Override
    public double getSimilarity(double addedValue) {
        super.getSimilarity(addedValue);


        if (canCount) {
            covariance = calculateCovariance();
            return calcuateCorrelation();
        } else {
            return 0;
        }
    }

    public double calculateDistance() {
        ArrayList<Sample> testSetRef     = getTestingSet();
        PatternMatcher    patternMatcher = (PatternMatcher) this.patternMatcher;
        double            total          = 0;

        for (int i = 0; i < patternMatcher.lengthOfTemplate; i++) {
            double currentVal = patternMatcher.getRemovedValue(testSetRef.get((i + (start))));


            double normalVal = normalizeMean(currentVal);
            total += Math.pow(patternMatcher.samples[i] - normalVal, 2);
        }

        return Math.sqrt(total);
    }

    private double calcuateCorrelation() {
        PatternMatcher patternMatcher = (PatternMatcher) this.patternMatcher;
        if (stdDeviation != 0) {
            return covariance / (stdDeviation * patternMatcher.stdDeviation);
        } else {
            return 0;
        }
    }

    //not sure if works anymore
    @Override
    public void reset() {
        super.reset();
        covariance = 0;
    }

    //TODO: ignore everything before firest samble
    private double calculateCovariance() {
        ArrayList<Sample> testSetRef     = getTestingSet();
        double            total          = 0;
        PatternMatcher    patternMatcher = (PatternMatcher) this.patternMatcher;
        //allows me to handle the data if there's not enough for a full curve.

        for (int i = 0; i < patternMatcher.lengthOfTemplate; i++) {
            double normValue = (normalizeMean(patternMatcher.getRemovedValue(testSetRef.get(i + (start - 1))))) - mean;
            total += (patternMatcher.samples[i] - patternMatcher.mean) * normValue;
        }
        //calculate R of the Cross Correlation equation
        return total / (patternMatcher.lengthOfTemplate);
    }


    //change  back too testing only


}
