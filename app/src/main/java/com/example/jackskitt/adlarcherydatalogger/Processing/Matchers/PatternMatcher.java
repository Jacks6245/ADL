package com.example.jackskitt.adlarcherydatalogger.Processing.Matchers;

import android.util.Log;

import com.example.jackskitt.adlarcherydatalogger.Math.MathHelper;
import com.example.jackskitt.adlarcherydatalogger.Processing.SimilarityTesters.CorrelationTester;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


/**
 * Created by Jack Skitt on 18/04/2017.
 */

public class PatternMatcher extends EventSearch {
    public double variance     = 0;
    public double mean         = 0;
    public double stdDeviation = 0;
    public double[] samples;

    public PatternMatcher(InputStream templateName) {
        super("PatternMatcher");
        if (templateName != null) {
            readFile(templateName);
            calculateValues();
            similarityTester = new CorrelationTester(this);
        }
    }

    //with this normalisation method, the closer the distance to
    // #the mean the more certain it is
    public void searchForEvent(double toAdd) {
        double r = similarityTester.getSimilarity(toAdd);

        if (r < 1) {//deal with overflow errors

            Log.i("Confidence", testingSequence.sequenceID + " : " + similarityTester.start + " : " + r);

            if (r > highestR) {
                startCountdown = true;
                highestR = r;
                highestRIndex = similarityTester.start;
            }
            if (startCountdown) {
                //5 samples after the last highest, if  nothing is found trigger event for highest value
                if (similarityTester.start > highestRIndex + 5) {
                    super.setEvent(highestRIndex, highestRIndex + lengthOfTemplate, highestR);
                }
            }
        } else {
            Log.i("Confidence ERROR", testingSequence.sequenceID + " : " + similarityTester.start + " : " + r);
        }
    }


    public void remakeTemplate(int start) {
        //add the averages to the model
        for (int i = 0; i < lengthOfTemplate; i++) {
            samples[i] = (samples[i] + getRemovedValue(similarityTester.getTestingSet().get(start + i))) / 2;
        }
        calculateValues();
    }


    //temp ringtone for testing
    public void calculateValues() {
        mean = MathHelper.calculateMean(samples);
        variance = MathHelper.calculateVariance(samples, mean);
        stdDeviation = MathHelper.calculateStdDeviation(samples, mean);

    }

    private void readFile(InputStream file) {
        BufferedReader reader;
        int            i = 0;
        try {
            reader = new BufferedReader(new InputStreamReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("#")) {//loading of patternMatcher settings
                    String[] values = line.split(",");
                    setType(parseType(values[0].substring(1)));
                    lengthOfTemplate = Integer.parseInt(values[1]);
                    samples = new double[lengthOfTemplate];
                    lowThreadhold = Float.parseFloat(values[2]);
                    highThreshold = Float.parseFloat(values[3]);

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
}
