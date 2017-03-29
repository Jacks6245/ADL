package com.example.jackskitt.adlarcherydatalogger.Sensors;

import java.util.ArrayList;

import com.example.jackskitt.adlarcherydatalogger.Collection.Sample;
import com.example.jackskitt.adlarcherydatalogger.Math.MathHelper;
import com.example.jackskitt.adlarcherydatalogger.Math.Vector3;
import com.example.jackskitt.adlarcherydatalogger.Sensors.*;
import com.example.jackskitt.adlarcherydatalogger.Math.Averager;

public class SensorCollector {
///this class mainly deals with the calibration and graphing storage, storing averagers...

    Sensor sensorPointer;

    public Vector3 cal = new Vector3();
    public Averager data;

    private int AVERGE_COUNT = 10;

    public SensorCollector(Sensor sensor) {
        sensorPointer = sensor;

        data = new Averager(AVERGE_COUNT);

    }


}
