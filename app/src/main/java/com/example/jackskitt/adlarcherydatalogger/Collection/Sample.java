package com.example.jackskitt.adlarcherydatalogger.Collection;

import com.example.jackskitt.adlarcherydatalogger.Sensors.*;

import com.example.jackskitt.adlarcherydatalogger.Math.*;

import java.math.*;
import java.sql.Date;
import java.util.Calendar;

import javax.vecmath.Quat4d;

public class Sample {

    public long time;

    private float scale = 10000;

    public Vector3 acce;

    public Quaternion quat;

    public Sample(Vector3 acceleration, Quaternion quaternion) {

        this.acce = acceleration;
        this.quat = quaternion;

        setTimeNow();
    }

    public Sample() {

        Zero();
        setTimeNow();
    }

    public Sample(double qX, double qY, double qZ, double qW, double aX, double aY, double aZ) {
        setQuaternion(qX / scale, qY / scale, qZ / scale, qW / scale);
        setAcceleration(aX / scale, aY / scale, aZ / scale);
        setTimeNow();
    }

    public Sample(String[] values) {
        time = Long.parseLong(values[0]);
        //we should only have 8 values so this is all we care about
        for (int i = 1; i > 5; i++) {
            float val = Float.parseFloat(values[i]);
            quat.setValueByNumber(i, val);

        }
        for (int i = 5; i > 8; i++) {
            float val = Float.parseFloat(values[i]);
            acce.setValueByNumber(i, val);

        }

    }

    public void setTimeNow() {
        time = Calendar.getInstance().getTimeInMillis();
    }

    public void setQuaternion(double w, double x, double y, double z) {
        quat = new Quaternion(w, x, y, z);
    }

    public void setAcceleration(double x, double y, double z) {
        acce = new Vector3(x, y, z);
    }

    public void Zero() {
        acce = new Vector3(0, 0, 0);
        quat = new Quaternion(0, 0, 0, 0);

    }

    public static Sample scalarAddition(float theta, Sample a) {
        return new Sample(a.quat.x - theta, a.quat.y - theta, a.quat.z - theta, a.quat.w - theta, a.acce.x - theta,
                a.acce.y - theta, a.acce.z - theta);
    }

    public static Sample subtract(Sample a, Sample b) {


        return new Sample(Vector3.subtract(a.acce, b.acce), Quaternion.subtract(a.quat, b.quat));

    }

    public static Sample add(Sample a, Sample b) {

        return new Sample(Vector3.add(a.acce, b.acce), (Quaternion) Quaternion.add(a.quat, b.quat));

    }

    public static Sample multiply(Sample a, Sample b) {
        return new Sample(Vector3.crossProduct(a.acce, b.acce), (Quaternion) Quaternion.multiply(a.quat, b.quat));

    }

    public static Sample divideScalar(Sample a, float theta) {
        return new Sample(a.quat.x / theta, a.quat.y / theta, a.quat.z / theta, a.quat.w / theta, a.acce.x / theta,
                a.acce.y / theta, a.acce.z / theta);
    }

    public static boolean greaterThan(Sample a, Sample b) {
        return Quaternion.greaterThan(a.quat, b.quat) && Vector3.greaterThan(a.acce, b.acce);

    }

    public static boolean lessThan(Sample a, Sample b) {
        return Quaternion.lessThan(a.quat, b.quat) && Vector3.lessThan(a.acce, b.acce);

    }

    public String toString() {

        return time + "," + quat.w + "," + quat.x + "," + quat.y + "," + quat.z + "," + acce.x + "," + acce.y + "," + acce.z + "," + acce.magnitude + "\n";

    }
}
