package com.example.jackskitt.adlarcherydatalogger;

import com.example.jackskitt.adlarcherydatalogger.Collection.Sample;
import com.example.jackskitt.adlarcherydatalogger.Collection.SampleStorage;
import com.example.jackskitt.adlarcherydatalogger.Collection.Sequence;
import com.example.jackskitt.adlarcherydatalogger.Math.MathHelper;
import com.example.jackskitt.adlarcherydatalogger.Processing.Matchers.DifferenceMatcher;
import com.example.jackskitt.adlarcherydatalogger.Processing.Matchers.EventSearch;
import com.example.jackskitt.adlarcherydatalogger.Processing.SimilarityTesters.DifferenceTester;

import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertEquals;

/**
 * Created by Jack Skitt on 30/04/2017.
 */

public class MathTester {
    double[] test   = new double[]{4, 3, 1, 2, 4, 5, 6};
    double[] correl = new double[]{1, 2, 4, 5, 2, 1, 3};

    public MathTester() {
    }

    @Test
    public void runningMeanTest() throws Exception {


        assertEquals(String.format("%.3f", 3.571428), String.format("%.3f", MathHelper.calculateMean(test)));
    }

    @Test
    public void stdDev() throws Exception {


        assertEquals(String.format("%.3f", 1.718249386), String.format("%.3f", MathHelper.calculateStdDeviation(test)));
    }

    @Test
    public void correl() throws Exception {
        assertEquals(String.format("%.3f", -0.5957539048), String.format("%.3f", MathHelper.calcuateCorrelation(test, correl)));
    }

    @Test
    public void covar() throws Exception {
        assertEquals(String.format("%.3f", -1.326530612), String.format("%.3f", MathHelper.calculateCovariance(test, correl)));
    }
}
