package com.example.jackskitt.adlarcherydatalogger.Collection;

import android.os.Environment;
import android.util.Log;

import com.example.jackskitt.adlarcherydatalogger.Adapters.ProfileListValue;
import com.example.jackskitt.adlarcherydatalogger.Profiles.Profile;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Hashtable;

public class FileManager {

    public final static String defaultDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/ADL_Logs/";

    //TODO:  update this for android

    private static void doesDirectoryExist() {
        File tempFile = new File(defaultDirectory);
        if (tempFile.exists()) {
            tempFile.mkdir();
        }
    }

    public static void saveSamples(Sequence sequence) {
        StringBuilder sb = new StringBuilder();
        doesDirectoryExist();
        sb.append("$" + Profile.instance.name + "," + sequence.sequenceID + "\n");
        for (SampleStorage s : sequence.sequenceData) {
            if (s.getSamples().size() > 0) {
                sb.append(encodeSensor(s));
            }
        }
        saveToFile(sequence, sb);
    }

    private static void saveToFile(Sequence sequence, StringBuilder sb) {
        try {
            File file = new File(defaultDirectory, makeProfileFileName(Profile.instance.name, Profile.instance.profileCurrentSequence.sequenceID));
            if (!file.exists()) {
                file.createNewFile();
            }

            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(sb.toString());
            writer.close();
            sequence.saved = true;
        } catch (IOException e) {
            Log.e("IO", e.getMessage());
        }
    }

    private static String encodeSensor(SampleStorage s) {
        StringBuilder builder = new StringBuilder();
        // builds a header for the s ensor, having it's id, port number and
        // length
        builder.append("#" + s.sensorRef.mBluetoothGatt.getDevice().getName() + "," + s.sensorRef.id + "," + s.sensorRef.mBluetoothGatt.getDevice() + "," + s.getTimeDifference() + "\n");
        if (s.getMarkers() != null)
            for (Marker a : s.getMarkers()) {
                builder.append("?" + a.startTime + "," + a.endTime + "," + a.note + "\n");
            }
        if (s.getSamples() != null) {
            for (Sample a : s.getSamples()) {
                builder.append(a.toString());
            }

        }

        return builder.toString();
    }

    //TODO: need to add profile name saving
    public static Sequence readFile(File file) {
        BufferedReader reader;
        try {
            if (Profile.instance != null) {
                reader = new BufferedReader(new FileReader(file));

                String line;

                Sequence tempSequence = new Sequence();
                int      sensorIndex  = -1;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("$")) {
                        String[] values = line.split(",");

                        tempSequence.sequenceID = Integer.parseInt(values[1]);
                    } else if (line.startsWith("#")) {
                        String[] values = line.split(",");
                        sensorIndex++;
                        tempSequence.sequenceData[sensorIndex].sensorName = values[0].substring(1);
                        tempSequence.sequenceData[sensorIndex].sensorID = Integer.parseInt(values[1]);// cuts
                        tempSequence.sequenceData[sensorIndex].sensorAddress = values[2];
                        tempSequence.sequenceData[sensorIndex].lengthOfSample = Long.parseLong(values[3]);
                    } else if (line.startsWith("?")) {// markers
                        String[] values = line.split(",");
                        tempSequence.sequenceData[sensorIndex].getMarkers().add(filterMarkers(line));
                    } else {
                        tempSequence.sequenceData[sensorIndex].addSample(filterResults(line));
                    }
                }
                return tempSequence;
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

    public static File[] findAllFilesForUser(String name) {
        ArrayList<File> outputFiles = new ArrayList<>();

        File folder = new File(defaultDirectory);
        if (!folder.exists()) {
            folder.mkdir();
        }
        File[] listOfFiles = folder.listFiles();
        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                if (file.isFile()) {

                    if (file.getName().contains(name)) {
                        outputFiles.add(file);
                    }

                }
            }
        }
        File[] arrayOutput = new File[outputFiles.size()];
        arrayOutput = outputFiles.toArray(arrayOutput);
        return arrayOutput;

    }

    public static Collection<ProfileListValue> findAllProfiles() {
        Hashtable<String, ProfileListValue> files = new Hashtable<>();


        File folder = new File(defaultDirectory);
        if (!folder.exists()) {
            folder.mkdir();
        }
        File[] listOfFiles = folder.listFiles();
        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    String profileName = getProfileName(file.getName());

                    if (!files.containsKey(profileName)) {
                        files.put(profileName, new ProfileListValue(profileName, 1));
                    } else {
                        files.get(profileName).count++;
                    }

                }
            }
        }
        return files.values();

    }

    private static String getProfileName(String fileName) {
        return fileName.split("_")[0];
    }

    public static String makeProfileFileName(String profileName, int sequenceId) {
        SimpleDateFormat sdf       = new SimpleDateFormat("yyyy-MM-dd-HHmmss");
        String           timestamp = sdf.format(Calendar.getInstance().getTime());

        return profileName + "_" + sequenceId + "_" + timestamp + ".csv";
    }
}
