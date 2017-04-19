package com.example.jackskitt.adlarcherydatalogger.Processing;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;

import com.example.jackskitt.adlarcherydatalogger.Collection.Sample;
import com.example.jackskitt.adlarcherydatalogger.Math.MathHelper;
import com.example.jackskitt.adlarcherydatalogger.Profiles.Profile;
import com.example.jackskitt.adlarcherydatalogger.UI.MainActivity;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


/**
 * Created by Jack Skitt on 18/04/2017.
 */

public class Template {
    public int lengthOfTemplate;
    public int    sensorLookupId = 0;
    public double variance       = 0;
    public double mean           = 0;
    public double stdDeviation   = 0;
    public double denom          = 0;
    public double[]    samples;
    public Correlation correlation;
    private double  highestR       = 0;
    private int     highestRIndex  = 0;
    private boolean startCountdown = false;


    public enum TemplateType {
        BOW_SHOT,
        BOW_DRAW,
        GLOVE_RELEASE,
        OTHER
    }


    private int countDownCorrelations = 0;
    private float        highThreshold;
    private float        lowThreadhold;
    private TemplateType type;

    public Template(InputStream templateName) {
        readFile(templateName);
        calculateValues();
        correlation = new Correlation(this);
    }

    public void getCorrelation(double toAdd) {
        double r = correlation.getCorrelation(toAdd);


        if (r > highestR) {

            startCountdown = true;
            highestR = r;
            highestRIndex = correlation.start;

        }
        if (startCountdown) {
            //5 samples after the last highest, if  nothing is found trigger event for highest value
            if (correlation.start > highestRIndex + 5) {
                setEvent(highestRIndex, highestRIndex + lengthOfTemplate, highestRIndex);
                if (highestR > highThreshold) {
                    remakeTemplate(highestRIndex);
                }
            }
        }
    }

    public void remakeTemplate(int start) {
        //add the averages to the model
        for (int i = 0; i < lengthOfTemplate; i++) {
            samples[i] = (samples[i] + getRemovedValue(correlation.getTestingSet().get(start + i))) / 2;
        }
        calculateValues();
    }

    //temp ringtone for testing
    public void setEvent(int start, int end, double confidence) {
        try {
            Uri      notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r            = RingtoneManager.getRingtone(MainActivity.getInstance().getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
        switch (type) {
            case BOW_DRAW:
                Profile.instance.profileCurrentSequence.setBowDrawFound(start, end, confidence);
                break;
            case BOW_SHOT:
                Profile.instance.profileCurrentSequence.setBowShotFound(start, end, confidence);
                break;
            case GLOVE_RELEASE:
                Profile.instance.profileCurrentSequence.setGloveReleaseFound(start, end, confidence);
                break;
        }


    }

    public TemplateType getType() {
        return type;
    }

    public void setType(TemplateType type) {
        if (type == TemplateType.BOW_DRAW || type == TemplateType.BOW_SHOT) {
            sensorLookupId = 0;
        } else {
            sensorLookupId = 1;
        }
        this.type = type;
    }

    public void calculateValues() {
        mean = MathHelper.calculateMean(samples);
        variance = MathHelper.calculateVariance(samples, mean);
        stdDeviation = MathHelper.calculateStdDeviation(samples, mean);
        for (double s : samples) {
            calculateDenom(s);
        }
    }

    private void calculateDenom(double addedValue) {

        denom += (addedValue - mean) * (addedValue - mean);
    }

    private void readFile(InputStream file) {
        BufferedReader reader;
        int            i = 0;
        try {
            reader = new BufferedReader(new InputStreamReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("#")) {//loading of template settings
                    String[] values = line.split(",");
                    setType(parseType(values[0].substring(1)));
                    lengthOfTemplate = Integer.parseInt(values[1]);
                    samples = new double[lengthOfTemplate];
                    highThreshold = Float.parseFloat(values[2]);
                    lowThreadhold = Float.parseFloat(values[3]);
                    highestR = lowThreadhold;
                } else {
                    double value = Double.parseDouble(line);

                    samples[i] = value;
                    i++;

                }
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NumberFormatException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block=-
            e.printStackTrace();
        }

    }

    private TemplateType parseType(String type) {
        switch (type) {
            case "BOW_SHOT":
                return TemplateType.BOW_SHOT;
            case "BOW_DRAW":
                return TemplateType.BOW_DRAW;
            case "GLOVE_RELEASE":
                return TemplateType.GLOVE_RELEASE;
            default:
                return TemplateType.OTHER;
        }
    }

    //gets the most recent data for the template match
    public double getRemovedValue(Sample value) {
        if (getType() == Template.TemplateType.BOW_DRAW || getType() == Template.TemplateType.BOW_SHOT) {
            return value.acce.x;
        }
        return value.acce.x;
    }
}
