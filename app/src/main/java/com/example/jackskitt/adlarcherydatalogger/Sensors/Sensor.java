package com.example.jackskitt.adlarcherydatalogger.Sensors;

import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortTimeoutException;
import main.DrawCanvas;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.media.j3d.Transform3D;

import Collection.Sample;
import Collection.SampleStorage;
import Math.MathHelper;

public class Sensor {

    private SerialPort serialPort;

    private String name;

    public SensorCollector graphStorage;

    public SampleStorage sampleStore;

    public int id;

    public DrawCanvas accCanvas;
    public DrawCanvas quatCanvas;


    public boolean collectData;

    public boolean drawnCanvas = false;

    public Sensor() {
        graphStorage = new SensorCollector(this);
        sampleStore = new SampleStorage(this);

        accCanvas = new DrawCanvas();
        quatCanvas = new DrawCanvas();
    }

    public boolean connectToPort(String port) {
        name = port;
        serialPort = new SerialPort(port); // breadboard
        try {

            if (serialPort.openPort()) {// Open serial port

                serialPort.setParams(57600, 8, 1, 0, true, false);// Set params.
                // usb mode

                System.out.println("Connected to Bluetooth serial port " + port + "");
                // purgePort();
                return true;
            } else {
                return false;
            }
        } catch (SerialPortException ex) {
            ex.printStackTrace();
            System.out.println("Failed to open Bluetooth serial port  " + port + "");
            return false;
        }
    }

    public void closePort() {
        if (serialPort.isOpened()) {
            purgePort();
            try {
                serialPort.closePort();
            } catch (SerialPortException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    public byte[] commsIntValues(char prefix, int bytecount) {
        byte[] tempByteStore = portReadBytes(prefix, bytecount);
        // save a copy in the sensors data store, this will be the main place to
        // save from
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {

            if (tempByteStore != null) {
                outputStream.write(tempByteStore);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return tempByteStore;
    }

    public byte[] readData(int amount) {

        try {
            if (serialPort.isOpened()) {
                return serialPort.readBytes(amount, 10000);
            }
        } catch (SerialPortException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SerialPortTimeoutException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            try {
                serialPort.closePort();
            } catch (SerialPortException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

        }
        return new byte[1];

    }

    public byte[] portReadBytes(char prefix, int bytecount) {

        byte buffer[] = new byte[30];
        buffer[0] = 0;

        if (collectData) {
            while (buffer[0] != prefix) {
                buffer = readData(1);

            }
            // read one sequence of data
            buffer = readData(bytecount);
        }
        return buffer;
    }

    public String getName() {

        return name;
    }

    public void processSample() {
        if (collectData) {
            byte buffer[] = commsIntValues('6', 14); // 6 is the prefix added to
            // bluetooth messages

            System.out.println("Process values");
            if (buffer != null) {

                Sample sample = getSample(buffer);

                Transform3D q = new Transform3D();
                // point representing the acceleration
                // sets the acceleration in world coordinates
                q.setRotation(sample.quat);
                q.transform(sample.acce);

                if (SensorStore.getInstance().saveData) {
                    // save the sample
                    sampleStore.addSample(sample);
                }
                // further process the sample
                graphStorage.data.addSample(sample);

            }
        }
    }

    private Sample getSample(byte[] buffer) {

        float qw = MathHelper.toInt(buffer[1], buffer[0]);
        float qx = MathHelper.toInt(buffer[3], buffer[2]);
        float qy = MathHelper.toInt(buffer[5], buffer[4]);
        float qz = MathHelper.toInt(buffer[7], buffer[6]);

        float Ax = MathHelper.toInt(buffer[9], buffer[8]);
        float Ay = MathHelper.toInt(buffer[11], buffer[10]);
        float Az = MathHelper.toInt(buffer[13], buffer[12]);

        return new Sample(qx, qy, qz, qw, Ax, Ay, Az);

    }

    private void purgePort() {
        try {
            if (serialPort.isOpened()) {
                serialPort.purgePort(SerialPort.PURGE_RXCLEAR | SerialPort.PURGE_TXCLEAR);
            }
        } catch (SerialPortException e) {
            e.printStackTrace();
            System.out.println("Failed to purge serial port");
            System.exit(1);
        }
    }
}
