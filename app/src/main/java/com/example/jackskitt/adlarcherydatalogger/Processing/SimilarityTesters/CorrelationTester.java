package com.example.jackskitt.adlarcherydatalogger.Processing.SimilarityTesters;

import com.example.jackskitt.adlarcherydatalogger.Collection.Sample;
import com.example.jackskitt.adlarcherydatalogger.Math.MathHelper;
import com.example.jackskitt.adlarcherydatalogger.Processing.Matchers.PatternMatcher;

import java.util.ArrayList;

/**
 * Created by Jack Skitt on 18/04/2017.
 */

public class CorrelationTester extends SimilarityTester {
    private double covariance = 0;

    public CorrelationTester(PatternMatcher patternMatcher) {
        super(patternMatcher);
    }

    //this class works out the pearson similarityTester of the cururent incoming data versus the data templates, it first finds the minimum data
    //this data normalizes the data so that we are matching the shape rather than power levels, it then calculates mean and standard de


    public double getSimilarity(double addedValue) {
        this.addedValue = addedValue;
        updateValues();
        updateCalculations();
        if (canCount) {
            covariance = calculateCovariance();
            return calcuateCorrelation();
        } else {
            return 0;
        }
    }

    private double calcuateCorrelation() {
        PatternMatcher patternMatcher = (PatternMatcher) this.patternMatcher;
        if (stdDeviation != 0) {
            double x = patternMatcher.stdDeviation * stdDeviation;
            return covariance / x;
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
            double normValue = (normalizeMean(patternMatcher.getRemovedValue(testSetRef.get(i + (start)))));
            total += (patternMatcher.samples[i] - patternMatcher.mean) * normValue;
        }
        //calculate R of the Cross CorrelationTester equation
        return total / patternMatcher.lengthOfTemplate;
    }


    //change  back too testing only


}
