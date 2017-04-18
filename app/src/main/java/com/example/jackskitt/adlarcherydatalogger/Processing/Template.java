package com.example.jackskitt.adlarcherydatalogger.Processing;

import com.example.jackskitt.adlarcherydatalogger.Collection.FileManager;
import com.example.jackskitt.adlarcherydatalogger.Math.MathHelper;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by Jack Skitt on 18/04/2017.
 */

public class Template {
    public int lengthOfTemplate;
    public int    sensorLookupId = 0;
    public double variance       = 0;
    public  double       mean;
    public  double[]     samples;
    private float        highThreshold;
    private float        lowThreadhold;
    private TemplateType type;

    public Template(String templateName) {
        readFile(FileManager.defaultDirectory + templateName);
        calculateValues();
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
        calculateVariance();
    }


    private void calculateVariance() {
        for (double s : samples) {
            variance += (s - mean) * (s - mean);
        }
    }

    private void readFile(String file) {
        BufferedReader reader;
        int            i = 0;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("#")) {//loading of template settings
                    String[] values = line.split(",");
                    type = parseType(values[0].substring(1));
                    lengthOfTemplate = Integer.parseInt(values[1]);
                    samples = new double[lengthOfTemplate];
                    highThreshold = Float.parseFloat(values[2]);
                    lowThreadhold = Float.parseFloat(values[3]);
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

    public enum TemplateType {
        BOW_SHOT,
        BOW_DRAW,
        GLOVE_RELEASE,
        OTHER
    }
}
