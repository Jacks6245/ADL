package com.example.jackskitt.adlarcherydatalogger.Processing.Matchers;

import com.example.jackskitt.adlarcherydatalogger.Processing.SimilarityTesters.DeviationTester;

/**
 * Created by Jack Skitt on 22/04/2017.
 */

public class VarienceMatcher extends EventSearch {
    public VarienceMatcher(int length, float lowThreshold, float highThreshold, TemplateType type) {
        super("VarienceMatcher");
        similarityTester = new DeviationTester(this);
        this.lengthOfTemplate = length;
        this.lowThreadhold = lowThreshold;
        this.highThreshold = highThreshold;
        setType(type);
    }


    public void searchForEvent(double toAdd) {
        double r = similarityTester.getSimilarity(toAdd);
        if (r > lowThreadhold) {
            super.setEvent(similarityTester.start, similarityTester.end, r);

        }

    }
}
