package com.example.jackskitt.adlarcherydatalogger.Math;

import java.lang.reflect.Array;

import com.example.jackskitt.adlarcherydatalogger.Collection.Sample;
import com.example.jackskitt.adlarcherydatalogger.Sensors.SensorCollector;

/**
 * class to keep track of a data sequence and provide an average of up to the
 * latest n terms
 *
 * @author nealsnooke
 */
public class Averager {

    private Sample[] samples;
    private int nSamples = 0;
    private Sample lastAverage = new Sample();
    private Sample currentAverage = new Sample();
    private Sample maxDiff = new Sample(); // the maximum absolute change


    public Averager(int maxSamples) {
        samples = new Sample[maxSamples];
        nSamples = maxSamples;
    }

    /**
     * @param sample
     */
    public void addSample(Sample sample) {
        // System.out.println("addSample"+currentAverage);
        // make space for new sample - improve with a cyclic structure later
        for (int i = nSamples - 1; i > 0; i--) {
            samples[i] = samples[i - 1];
        }

        samples[0] = sample;
        lastAverage = currentAverage;
        // System.out.println("LA: "+lastAverage);
        currentAverage.Zero();
        if (!(samples[1] == null)) {
            maxDiff = Sample.multiply(maxDiff, Sample.scalarAddition(0.9f, Sample.subtract(sample, samples[1])));
        } else {
            maxDiff = Sample.multiply(maxDiff, Sample.scalarAddition(0.9f, Sample.subtract(sample, new Sample())));
        }
    }

    /**
     * magnitude of sample difference in averaging buffer
     */
    public Sample sampleVariation() {
        Sample min = samples[0];
        Sample max = samples[0];
        ;

        for (int i = 0; i < nSamples; i++) {
            if (Sample.greaterThan(samples[i], max)) {
                max = samples[i];
            }
            if (Sample.lessThan(samples[i], min)) {
                min = samples[i];
            }
        }

        return Sample.subtract(max, min);
    }

    public Sample getMaxDiff() {
        return maxDiff;
    }

    /**
     *
     * @param sample
     *
     *            public void integrateSample(double sample){
     *            //System.out.println("addSample"+currentAverage); // make
     *            space for new sample - improve with a cyclic structure later
     *            for (int i=nSamples-1; i>0; i--){ samples[i] = samples[i-1]; }
     *
     *            samples[0] = sample; lastAverage = currentAverage;
     *            //System.out.println("LA: "+lastAverage); currentAverage = 0;
     *            }
     */

    /**
     * @param
     */
    public void reset() {
        // System.out.println("addSample"+currentAverage);
        for (int i = nSamples - 1; i >= 0; i--) {
            samples[i].Zero();
        }

        lastAverage.Zero();
        ;
        currentAverage.Zero();
        ;
    }

    /**
     * @param n
     */
    public Sample getAverage(int n) {
        // System.out.println("get Ave");
        Sample total = new Sample();
        for (int i = 0; i < n; i++) {
            total = Sample.add(total, samples[i]);
            // System.out.println(i+" "+samples[i]);
        }

        currentAverage = Sample.divideScalar(total, (float) n);
        // System.out.println("Current Ave: "+currentAverage);
        return currentAverage;
    }


    public Sample getAverage() {
        return getAverage(nSamples);
    }


    public Sample getLastAverage() {
        // System.out.println("get Last Ave");
        return lastAverage;
    }

    /**
     * @return
     */
    public Sample getCurrentSample() {
        return samples[0];
    }

    /**
     * @return
     */
    public Sample getLastSample() {
        if (samples[1] == null) {
            return new Sample();
        }
        return samples[1];
    }

}
