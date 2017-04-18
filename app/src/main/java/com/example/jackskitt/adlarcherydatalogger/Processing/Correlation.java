package com.example.jackskitt.adlarcherydatalogger.Processing;

import com.example.jackskitt.adlarcherydatalogger.Collection.Sample;
import com.example.jackskitt.adlarcherydatalogger.Profiles.Profile;

import java.util.ArrayList;

/**
 * Created by Jack Skitt on 18/04/2017.
 */

public class Correlation {
    private Template template;

    private int count;
    private double oldMean = 0;
    private double mean    = 0;


    private double stdDeviation = 0;
    private double variance     = 0;


    public double calculateCorrelation(double addedValue) {
        mean = calculateRunningMean(addedValue);
        stdDeviation = calculateRunningDeviation(addedValue);
        return xCorrelation();
    }

    private double xCorrelation() {
        ArrayList<Sample> testSetRef = getTestingSet();
        double            x          = 0;
        int               a;
        int               limit;

        //allows me to handle the data if there's not enough for a full curve.
        if (testSetRef.size() > template.lengthOfTemplate) {
            a = testSetRef.size() - template.lengthOfTemplate;
            limit = template.lengthOfTemplate;
        } else {
            a = 0;
            limit = testSetRef.size();
        }
        for (int i = a; i < limit; i++) {
            x += (template.samples[i] - template.mean) * (testSetRef.get(i).quat.x - mean);
        }
        //calculate R of the Cross Correlation equation
        return x / stdDeviation;
    }

    private double calculateRunningDeviation(double addedValue) {
        //calculated running variance
        if (count > 0) {
            variance = variance + (addedValue - oldMean) * (addedValue - mean);
        }
        //returns the new standard deviation based on the old mean count
        if (count > 1) {
            double newVariance = Math.sqrt(variance / (count - 1));
            return Math.sqrt(template.variance * newVariance);
        }
        return 0;
    }

    //this is a more efficent method to calculate the mean,
    //this means that it doesn't need to sum all the data each time
    private double calculateRunningMean(double addedValue) {
        oldMean = mean;
        //need to make sure count is increased before this method is called
        if (count < template.lengthOfTemplate) {
            return mean - ((addedValue - mean) / count);
        }

        return mean - (((getRemovedValue() - addedValue) - mean) / count);
    }

    private ArrayList<Sample> getTestingSet() {
        return Profile.instance.profileCurrentSequence.sequenceData[template.sensorLookupId].getSamples();

    }

    //gets the most recent data for the template match
    private double getRemovedValue() {
        Sample sampleRef = getTestingSet().get(getTestingSet().size() - template.lengthOfTemplate);
        if (template.getType() == Template.TemplateType.BOW_DRAW || template.getType() == Template.TemplateType.BOW_SHOT) {
            return sampleRef.quat.x;
        }
        return sampleRef.acce.x;
    }

}
