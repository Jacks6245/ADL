package com.example.jackskitt.adlarcherydatalogger.Collection;

import com.example.jackskitt.adlarcherydatalogger.Sensors.Sensor;
import com.example.jackskitt.adlarcherydatalogger.Sensors.SensorStore;
import com.example.jackskitt.adlarcherydatalogger.UI.MainActivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class FileManager {
    //TODO:  update this for android
    public static void SaveSamples(String name, int sessionID) {
        StringBuilder sb = new StringBuilder();

        sb.append(name + ",\t" + sessionID + "\n");
        sb.append(encodeSensor(MainActivity.getInstance().store.sensors[0]));
        sb.append(encodeSensor(MainActivity.getInstance().store.sensors[1]));


        try {
            Date             date       = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
            File file = new File(
                    "./data/LOG_" + dateFormat.format(date) + "_" + System.currentTimeMillis() % 1000 + ".csv");
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(sb.toString());
            writer.close();

        } catch (IOException e) {

        }
    }

    public static String encodeSensor(Sensor s) {
        StringBuilder builder = new StringBuilder();
        // builds a header for the s ensor, having it's id, port number and
        // length
        builder.append("#" + s.id + "," + s.getName() + "," + Sequence.getInstance().sequenceData[s.id].getTimeDifference() + "\n");
        for (Marker a : Sequence.getInstance().sequenceData[s.id].markers) {
            builder.append("?" + a.startTime + "," + a.endTime + "," + a.note + "\n");
        }
        for (Sample a : Sequence.getInstance().sequenceData[s.id].getSamples()) {
            builder.append(Sequence.getInstance().sequenceData[s.id].getLocalTime(a.time) + "," + a.toString());
        }


        return builder.toString();
    }

    public static Sequence readFile(String name) {
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(name));

            String   line         = null;
            Sequence tempSequence = new Sequence();
            int      sensorIndex  = -1;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("@")) {
                    String[] values = line.split(",");
                    tempSequence.userName = values[0].substring(1);
                    tempSequence.sequenceID = Integer.parseInt(values[1]);
                } else if (line.startsWith("#")) {
                    String[] values = line.split(",");
                    sensorIndex++;
                    tempSequence.sequenceData[sensorIndex].sensorID = Integer.parseInt(values[0].substring(1));
                    tempSequence.sequenceData[sensorIndex].sensorName = values[1];// cuts
                    // off
                    // the
                    // identifer

                    tempSequence.sequenceData[sensorIndex].lengthOfSample = Long.parseLong(values[2]);
                } else if (line.startsWith("?")) {// markers
                    String[] values = line.split(",");
                    tempSequence.sequenceData[sensorIndex].markers.add(filterMarkers(line));
                } else {
                    tempSequence.sequenceData[sensorIndex].addSample(filterResults(line));
                }
            }
            return tempSequence;
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

        return null;
    }

    private static Sample filterResults(String line) {
        String values[] = line.split(",");
        return new Sample(values);
    }

    private static Marker filterMarkers(String line) {
        String values[] = line.split(",");
        values[0] = values[0].substring(1);
        return new Marker(values);
    }

    public static ArrayList<String> findAllFilesForUser(String user, String directory) {
        ArrayList<String> names = new ArrayList<String>();

        File   folder      = new File("./" + directory);
        File[] listOfFiles = folder.listFiles();

        for (File file : listOfFiles) {
            if (file.isFile()) {
                if (file.getName().contains(user)) {
                    names.add(file.getName());
                }
            } else if (file.isDirectory()) {
                names.addAll(findAllFilesForUser(user, file.getPath()));
            }
        }
        return names;
    }
}
