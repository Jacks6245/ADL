package com.example.jackskitt.adlarcherydatalogger.Processing;

/**
 * Created by Jack Skitt on 21/04/2017.
 */

public class DeviationDifference extends SimilarityTester {

    private int dataIncrement = 0;

    public DeviationDifference(EventSearch searcher) {
        super(searcher);
    }

    @Override
    public double getSimilarity(double addedValue) {
        super.getSimilarity(addedValue);
        dataIncrement++;

        if (dataIncrement == 3) {
            dataIncrement = 0;
            return stdDeviation;
        }
        return 0;
    }


}

