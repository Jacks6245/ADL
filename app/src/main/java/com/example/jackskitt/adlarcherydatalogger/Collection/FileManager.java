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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Hashtable;

public class FileManager {

    public final static String defaultDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/ADL_Logs/";

    //TODO:  update this for android
//checks if the default directory exists, and if not it creates it
    public static void doesDirectoryExist() {
        File tempFile = new File(defaultDirectory);
        if (tempFile.exists()) {
            tempFile.mkdir();
        }
    }

    //first creates the file name form the profile  name and  it's sequence_ID. then wries the sequence to the file.
    public static void saveToFile(Sequence sequence, StringBuilder sb) {
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

    //Reads the sequence file into a new  sequence which is then saved into the  profiles sequence store.

    //TODO: need to add profile name saving
    public static Sequence readFile(File file) {
        BufferedReader reader;
        try {
            if (Profile.instance != null) {
                reader = new BufferedReader(new FileReader(file));

                String line;
                //temp change bacck
                Sequence tempSequence = new Sequence();
                tempSequence.date = getProfileDate(file.getName());
                TemplateStore.instance.resetForNewSequence();
                int sensorIndex = -1;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("$")) {
                        //gets the file header that identifies the sequence

                        String[] values = line.split(",");

                        tempSequence.sequenceID = Integer.parseInt(values[1]);

                    } else if (line.startsWith("#")) {
                        ////gets the header information from the sensor and loads it into the sensor
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
                        //filters the sequence ID into
                        tempSequence.addSample(sensorIndex, filterResults(line));
                    }
                }

                //tempSequence.splitSequence();
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

    //splits the CSV data
    public static Sample filterResults(String line) {
        String values[] = line.split(",");
        return new Sample(values);
    }

    private static Event filterMarkers(String line) {
        String values[] = line.split(",");
        values[0] = values[0].substring(1);
        return new Event(values);
    }

    //returns a list of all the files that the name matches that of the orifuke that is being looked for
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

                    if (getProfileName(file.getName()).equals(name)) {
                        outputFiles.add(file);
                    }

                }
            }
        }
        File[] arrayOutput = new File[outputFiles.size()];
        arrayOutput = outputFiles.toArray(arrayOutput);
        return arrayOutput;

    }
//returns the list of profiles for the ProfileList and formats them into the ProfileListValue object

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

    private static String getProfileDate(String fileName) {
        String           fileDate    = fileName.split("_")[2];
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd-HHmmss");
        Date             tempDate    = null;
        try {
            tempDate = inputFormat.parse(fileDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy");
        return outputFormat.format(tempDate);
    }

    public static String makeProfileFileName(String profileName, int sequenceId) {
        SimpleDateFormat sdf       = new SimpleDateFormat("yyyy-MM-dd-HHmmss");
        String           timestamp = sdf.format(Calendar.getInstance().getTime());

        return profileName + "_" + sequenceId + "_" + timestamp + ".csv";
    }
}
