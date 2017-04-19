package com.example.jackskitt.adlarcherydatalogger.Processing;

import com.example.jackskitt.adlarcherydatalogger.Collection.Sample;
import com.example.jackskitt.adlarcherydatalogger.Profiles.Profile;

import java.util.ArrayList;

/**
 * Created by Jack Skitt on 18/04/2017.
 */

public class Correlation {

    public int start = 0;
    public int end   = 0;
    private Template template;
    private int    count        = 0;
    private double oldMean      = 0;
    private double mean         = 0;
    private double stdDeviation = 0;
    private double variance     = 0;
    private double newMean      = 0;
    private double S            = 0;
    private double denom        = 0;
    private double covariance   = 0;
    private double min          = 0;

    //this class works out the pearson correlation of the cururent incoming data versus the data templates, it first finds the minimum data
    //this data normalizes the data so that we are matching the shape rather than power levels, it then calculates mean and standard de
    public Correlation(Template template) {
        this.template = template;
    }

    public double getCorrelation(double addedValue) {
        if (addedValue < min) {
            min = addedValue;
        }

        start = getStartOfCollectionData();
        end = getStartOfCollectionData() + template.lengthOfTemplate;
        if (count < template.lengthOfTemplate) {
            count++;
        }
        mean = calculateRunningMean(addedValue);
        stdDeviation = calculateRunningDeviation(addedValue);
        if (getTestingSet().size() >= template.lengthOfTemplate) {
            covariance = calculateCovariance();
            return calcuateCorrelation();
        } else {
            return 0;
        }
    }

    private double calcuateCorrelation() {

        return covariance / (stdDeviation * template.stdDeviation);
    }

    //TODO: ignore everything before firest samble
    private double calculateCovariance() {
        ArrayList<Sample> testSetRef = getTestingSet();
        double            total      = 0;

        //allows me to handle the data if there's not enough for a full curve.

        for (int i = 0; i < template.lengthOfTemplate; i++) {
            total += (template.samples[i] - template.mean) * ((getRemovedValue(testSetRef.get(i + start)) - min) - (mean - min));
        }
        //calculate R of the Cross Correlation equation
        return total / (template.lengthOfTemplate);
    }

    private double calculateRunningDeviation(double addedValue) {
        //calculated running variance
        double delta = addedValue - oldMean;

        double delta2 = addedValue - mean;

        S += delta * delta2;

        //returns the new standard deviation based on the old mean count
        if (count > 2) {
            variance = S / (count - 1);
            return Math.sqrt(variance);
        } else {
            variance = 0;
            return 0;
        }
    }

    //this is a more efficent method to calculate the mean,
    //this means that it doesn't need to sum all the data each time
    private double calculateRunningMean(double addedValue) {
        oldMean = mean;
        //need to make sure count is increased before this method is called
        if (getTestingSet().size() > template.lengthOfTemplate) {

            //can possibly improve  this with difference calulation, need testing
            mean = ((mean * count) - getValueAtStart()) / (count - 1);//removed a value from the mean
            return mean + ((addedValue - mean) / count);//add a new one
        }
        return mean + ((addedValue - mean) / count);


    }

    private double getValueAtStart() {
        return getRemovedValue(getTestingSet().get(start));
    }

    //change  back too testing only
    public ArrayList<Sample> getTestingSet() {
        return Profile.instance.profileCurrentSequence.sequenceData[template.sensorLookupId].getSamples();

    }

    public int getStartOfCollectionData() {
        if (getTestingSet().size() > template.lengthOfTemplate) {
            return (getTestingSet().size() - template.lengthOfTemplate) - 1;
        } else {
            return 0;
        }
    }

    //gets the most recent data for the template match
    public double getRemovedValue(Sample value) {
        if (template.getType() == Template.TemplateType.BOW_DRAW || template.getType() == Template.TemplateType.BOW_SHOT) {
            return value.acce.x;
        }
        return value.acce.x;
    }

}
