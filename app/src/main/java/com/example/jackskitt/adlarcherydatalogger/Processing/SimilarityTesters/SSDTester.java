package com.example.jackskitt.adlarcherydatalogger.Processing.SimilarityTesters;

import com.example.jackskitt.adlarcherydatalogger.Collection.Sample;
import com.example.jackskitt.adlarcherydatalogger.Processing.Matchers.PatternMatcher;

import java.util.ArrayList;

/**
 * Created by Jack Skitt on 21/04/2017.
 */

public class SSDTester extends SimilarityTester {


    public SSDTester(PatternMatcher eventSearcher) {

        super(eventSearcher);


    }
    public double getSimilarity(double addedValue) {
        this.addedValue = addedValue;
        updateValues();
        updateCalculations();
        if (canCount) {
            //   covariance = calculateCovariance();
            return (1 - (1 / calculateDistance()));
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
            total += Math.pow((patternMatcher.samples[i]) - normalVal, 2);
        }

        return total;
    }
}
