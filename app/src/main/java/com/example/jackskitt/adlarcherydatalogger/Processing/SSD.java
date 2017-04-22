package com.example.jackskitt.adlarcherydatalogger.Processing;

import com.example.jackskitt.adlarcherydatalogger.Collection.Sample;

import java.util.ArrayList;

/**
 * Created by Jack Skitt on 21/04/2017.
 */

public class SSD extends SimilarityTester {


    public SSD(PatternMatcher eventSearcher) {

        super(eventSearcher);


    }

    @Override
    public double getSimilarity(double addedValue) {
        super.getSimilarity(addedValue);
        if (canCount) {
            //   covariance = calculateCovariance();
            return (1 / (1 + calculateDistance())) / stdDeviation;
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

        return Math.sqrt(total);
    }
}
