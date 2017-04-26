package com.example.jackskitt.adlarcherydatalogger.Processing.Matchers;

import com.example.jackskitt.adlarcherydatalogger.Processing.SimilarityTesters.DifferenceTester;

/**
 * Created by Jack Skitt on 23/04/2017.
 */

public class DifferenceMatcher extends EventSearch {
    public DifferenceMatcher(float lowThreshold, float highThreshold, TemplateType type) {
        super("Difference_Matcher");

        similarityTester = new DifferenceTester(this);

        this.lowThreadhold = lowThreshold;
        this.highThreshold = highThreshold;
        setType(type);

    }

    @Override
    public void searchForEvent(double toAdd) {
        double r = similarityTester.getSimilarity(toAdd);
        if (r > lowThreadhold) {
            super.setEvent(similarityTester.start, similarityTester.end, r);

        }
    }
}
