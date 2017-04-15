package com.example.jackskitt.adlarcherydatalogger.Collection;

import com.example.jackskitt.adlarcherydatalogger.Math.Quaternion;
import com.example.jackskitt.adlarcherydatalogger.Math.Vector3;

import java.util.Calendar;

public class Sample {

    public long       time;
    public Vector3    acce;
    public Quaternion quat;
    public Vector3    magn;
    private float scale = 10000;

    public Sample(Vector3 acceleration, Quaternion quaternion, Vector3 compass) {

        this.acce = acceleration;
        this.quat = quaternion;
        this.magn = compass;

        setTimeNow();
    }

    public Sample() {

        Zero();
        setTimeNow();
    }
    public Sample(double qX, double qY, double qZ, double qW, double aX, double aY, double aZ, double mX, double mY, double mZ) {
        setQuaternion(qX / scale, qY / scale, qZ / scale, qW / scale);
        setAcceleration(aX / scale, aY / scale, aZ / scale);
        setCompass(mX / scale, mY / scale, mZ / scale);
        setTimeNow();
    }

    public Sample(double qX, double qY, double qZ, double qW, double aX, double aY, double aZ, double mX, double mY, double mZ, long time) {
        setQuaternion(qX / scale, qY / scale, qZ / scale, qW / scale);
        setAcceleration(aX / scale, aY / scale, aZ / scale);
        setCompass(mX / scale, mY / scale, mZ / scale);
        this.time = time;
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

    public static Sample sclarSubtraction(float theta, Sample a) {
        return new Sample(a.quat.x - theta, a.quat.y - theta, a.quat.z - theta, a.quat.w - theta, a.acce.x - theta,
                a.acce.y - theta, a.acce.z - theta, a.magn.x - theta, a.magn.y - theta, a.magn.z - theta);
    }

    public static Sample scalarAddition(float theta, Sample a) {
        return new Sample(a.quat.x + theta, a.quat.y + theta, a.quat.z + theta, a.quat.w + theta, a.acce.x + theta,
                a.acce.y + theta, a.acce.z + theta, a.magn.x + theta, a.magn.y + theta, a.magn.z + theta);
    }

    public static Sample subtract(Sample a, Sample b) {


        return new Sample(Vector3.subtract(a.acce, b.acce), Quaternion.subtract(a.quat, b.quat), Vector3.subtract(a.magn, b.magn));

    }

    public static Sample add(Sample a, Sample b) {

        return new Sample(Vector3.add(a.acce, b.acce), (Quaternion) Quaternion.add(a.quat, b.quat), Vector3.add(a.magn, b.magn));

    }

    public static Sample multiply(Sample a, Sample b) {
        return new Sample(Vector3.crossProduct(a.acce, b.acce), (Quaternion) Quaternion.multiply(a.quat, b.quat), Vector3.crossProduct(a.magn, b.magn));
    }

    public static Sample divideScalar(Sample a, float theta) {
        return new Sample(a.quat.x / theta, a.quat.y / theta, a.quat.z / theta, a.quat.w / theta, a.acce.x / theta,
                a.acce.y / theta, a.acce.z / theta, a.magn.x / theta, a.magn.y / theta, a.magn.z / theta);
    }

    public static boolean greaterThan(Sample a, Sample b) {
        return Quaternion.greaterThan(a.quat, b.quat) && Vector3.greaterThan(a.acce, b.acce);

    }

    public static boolean lessThan(Sample a, Sample b) {
        return Quaternion.lessThan(a.quat, b.quat) && Vector3.lessThan(a.acce, b.acce);

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

    public void setCompass(double x, double y, double z) {
        magn = new Vector3(x, y, z);
    }

    public void Zero() {
        acce = new Vector3(0, 0, 0);
        quat = new Quaternion(0, 0, 0, 0);

    }

    public String toString() {

        return time + "," + quat.w + "," + quat.x + "," + quat.y + "," + quat.z + "," + acce.x + "," + acce.y + "," + acce.z + "," + acce.magnitude + "," + magn.x + "," + magn.y + "," + magn.z + "\n";

    }
}
