package com.example.jackskitt.adlarcherydatalogger.Processing.SimilarityTesters;

import com.example.jackskitt.adlarcherydatalogger.Processing.Matchers.EventSearch;

/**
 * Created by Jack Skitt on 21/04/2017.
 */

public class DeviationTester extends SimilarityTester {

    private int dataIncrement    = 0;
    private int maxVarienceCount = 3;

    public DeviationTester(EventSearch searcher) {
        super(searcher);
    }

    @Override
    public double getSimilarity(double addedValue) {
        this.addedValue = addedValue;
        updateValues();
        updateCalculations();
        dataIncrement++;

        if (dataIncrement == maxVarienceCount) {
            dataIncrement = 0;
            return stdDeviation;
        }
        return 0;
    }


}

