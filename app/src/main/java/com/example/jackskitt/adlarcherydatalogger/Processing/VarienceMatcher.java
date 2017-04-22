package com.example.jackskitt.adlarcherydatalogger.Processing;

/**
 * Created by Jack Skitt on 22/04/2017.
 */

public class VarienceMatcher extends EventSearch {
    public VarienceMatcher(int length, double lowThreshold, double highThreshold) {
        correlation = new DeviationDifference(this);
        setType(TemplateType.BOW_SHOT);
    }

    @Override
    public void searchForEvent(double toAdd) {
        super.searchForEvent(toAdd);

        if (correlation.getSimilarity(toAdd) > lowThreadhold) {
            setEvent(highestRIndex, highestRIndex + lengthOfTemplate, highestR);

        }

    }
}
