package com.example.jackskitt.adlarcherydatalogger.Sensors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JLabel;

import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;
import jssc.SerialPortTimeoutException;
import main.Logger;
import main.QuatGrapher;

public class SensorStore {
    // open accessibility for tests
    public List<Sensor> sensorStorage = new ArrayList<Sensor>();
    public List<String> outgoingPorts = new ArrayList<String>();

    public int numSensors;

    public boolean hasSensors = false;

    public boolean saveData = false;

    private static SensorStore instance;

    private String usbPortName = "COM6";

    public SensorStore() {
        // perform an initial search
        searchForSensors();
    }

    public void searchForSensors() {

        for (Sensor sensor : sensorStorage) {
            if (sensor != null) {
                sensor.collectData = false;
                sensor.closePort();
            }
        }

        sensorStorage.clear();
        outgoingPorts.clear();
        numSensors = 0;

        System.out.println("Searching for Sensors...");
        findOutgoingPorts();
        if (outgoingPorts.isEmpty()) {
            System.out.println("Sorry - couldn't find any sensors");
            hasSensors = false;
        }
    }

    // Loops through all the sensors and connects to them while increasing the
    // number of sensors for error checking
    private boolean connectSensors() {
        for (String outPorts : outgoingPorts) {
            Sensor tempSensor = new Sensor();
            tempSensor.collectData = true;
            if (tempSensor.connectToPort(outPorts)) {
                numSensors++;

                tempSensor.id = numSensors;
                sensorStorage.add(tempSensor);
                QuatGrapher.getInstance().chart.addNewDrawingCanvas(tempSensor);
                return true;
            } else {
                System.out.println("ERROR: Sensor cannot be added.");
                return false;
            }
        }
        return false;
    }

    public Sensor getSensorByName(String name) {
        for (Sensor s : sensorStorage) {

            if (s.getName().equals(name)) {
                return s;
            }
        }
        return null;
    }

    public boolean connectToSensor(String sensor) {
        Sensor tempSensor = new Sensor();
        tempSensor.collectData = true;
        if (tempSensor.connectToPort(sensor)) {


            tempSensor.id = numSensors;
            sensorStorage.add(tempSensor);

            Logger.getInstance().addConnectedSensor(sensor);
            numSensors++;
            return true;
        } else {
            System.out.println("ERROR: Sensor cannot be added.");
            return false;
        }

    }

    // method to filter out the names of the outgoing ports, this makes the
    // assumption that the only things connected to the com ports are the
    // sensors
    public void findOutgoingPorts() {
        List<String> portNames = new ArrayList<String>(Arrays.asList(SerialPortList.getPortNames()));

        if (!portNames.contains(usbPortName)) {
            portNames.add(usbPortName);
        }

        for (String port : portNames) {
            try {
                SerialPort serialPort = new SerialPort(port);
                serialPort.openPort();// Open serial port

                serialPort.setParams(57600, 8, 1, 0, true, false);

                if (!serialPort.isDSR() && !(port.equals(usbPortName))) {
                    serialPort.closePort();
                    continue;
                }
                outgoingPorts.add(port);
                hasSensors = true;
                serialPort.closePort();
            } catch (SerialPortException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public void clearLog() {
        if (hasSensors) {
            for (Sensor sensor : sensorStorage) {
                sensor.sampleStore.getSamples().clear();// wipes the storage
            }
        }
    }

    public static SensorStore getInstance() {
        if (instance == null) {
            instance = new SensorStore();
        }
        return instance;
    }

}
