package com.example.jackskitt.adlarcherydatalogger.Collection;

import android.os.Environment;
import android.util.Log;

import com.example.jackskitt.adlarcherydatalogger.Adapters.ProfileListValue;
import com.example.jackskitt.adlarcherydatalogger.Processing.TemplateStore;
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

    static void doesDirectoryExist() {
        File tempFile = new File(defaultDirectory);
        if (tempFile.exists()) {
            tempFile.mkdir();
        }
    }


    static void saveToFile(Sequence sequence, StringBuilder sb) {
        try {
            File file = new File(defaultDirectory, makeProfileFileName(Profile.instance.name, sequence.sequenceID));
            if (!file.exists()) {
                file.createNewFile();
            }

            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(sb.toString());
            writer.close();
            sequence.processed = true;
        } catch (IOException e) {
            Log.e("IO", e.getMessage());
        }
    }



    //TODO: need to add profile name saving
    public static Sequence readFile(File file) {
        BufferedReader reader;
        try {
            if (Profile.instance != null) {
                reader = new BufferedReader(new FileReader(file));

                String line;
//temp change bacck
                Sequence tempSequence = Profile.instance.sequenceStore.allSequences.get(Profile.instance.sequenceStore.allSequences.size() - 1);
                TemplateStore.instance.resetForNewSequence();
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
                        tempSequence.sequenceData[sensorIndex].sizeOfDataset = Integer.parseInt(values[3]);
                        tempSequence.aimTime = Integer.parseInt(values[4]);

                    } else if (line.startsWith("?")) {// markers
                        String[] values = line.split(",");
                        tempSequence.sequenceData[sensorIndex].getEvents().add(filterMarkers(line));
                    } else {
                        tempSequence.addSample(sensorIndex, filterResults(line));
                    }
                }

                tempSequence.splitSequence();
                TemplateStore.instance.resetForNewSequence();

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

    public static Sample filterResults(String line) {
        String values[] = line.split(",");
        return new Sample(values);
    }

    private static Event filterMarkers(String line) {
        String values[] = line.split(",");
        values[0] = values[0].substring(1);
        return new Event(values);
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
