package com.example.jackskitt.adlarcherydatalogger.Processing.SimilarityTesters;

import com.example.jackskitt.adlarcherydatalogger.Processing.Matchers.EventSearch;

/**
 * Created by Jack Skitt on 23/04/2017.
 */

public class DifferenceTester extends SimilarityTester {
    double lastValue = 0;

    public DifferenceTester(EventSearch patternMatcher) {
        super(patternMatcher);
        canRemove = true;//to allow to start counting the start
        canCount = true;
    }

    public void reset() {
        super.reset();
        canRemove = true;
        canCount = true;
    }

    @Override
    public double getSimilarity(double addedValue) {


        updateStart();
        if (count != 0 && count <= getTestingSet().size() - 1) {//gets the range of 1..n-1
            double valueToReturn;

            if (addedValue > lastValue) {//always allows for a positive value
                valueToReturn = addedValue - lastValue;
            } else {
                valueToReturn = lastValue - addedValue;
            }


            lastValue = addedValue;
            return valueToReturn;
        }
        lastValue = addedValue;
        count++;

        return 0;
    }
}
