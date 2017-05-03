package com.example.jackskitt.adlarcherydatalogger.Collection;

import com.example.jackskitt.adlarcherydatalogger.Math.Vector3;

import java.util.Calendar;

//a sammple is the base data structure that is used for a piece of sensor data, it contains 10 values the time, acceleration, rotation and compass readings from the sensor
public class Sample {

    private static float scale = 10000;
    public long    time;
    public Vector3 acce;
    public Vector3 rot;
    public Vector3 magn;

    public Sample(Vector3 acceleration, Vector3 rotation, Vector3 compass) {

        this.acce = acceleration;
        this.rot = rotation;
        this.magn = compass;

        setTimeNow();
    }

    public Sample() {

        Zero();
        setTimeNow();
    }

    public Sample(double qX, double qY, double qZ, double aX, double aY, double aZ, double mX, double mY, double mZ) {
        setRotation(qX / scale, qY / scale, qZ / scale);
        setAcceleration(aX / scale, aY / scale, aZ / scale);
        setCompass(mX / scale, mY / scale, mZ / scale);
        setTimeNow();
    }

    //the extra boolean is used to apply the scale values or not
    public Sample(double qX, double qY, double qZ, double aX, double aY, double aZ, double mX, double mY, double mZ, boolean scale) {
        setRotation(qX, qY, qZ);
        setAcceleration(aX, aY, aZ);
        setCompass(mX, mY, mZ);
        setTimeNow();
    }

    public Sample(double qX, double qY, double qZ, double aX, double aY, double aZ, double mX, double mY, double mZ, long time) {
        setRotation(qX / scale, qY / scale, qZ / scale);
        setAcceleration(aX / scale, aY / scale, aZ / scale);
        setCompass(mX / scale, mY / scale, mZ / scale);
        this.time = time;
    }

    public Sample(String[] values) {
        Zero();
        time = Long.parseLong(values[0]);
        //we should only have 8 values so this is all we care about
        for (int i = 1; i < 4; i++) {
            float val = Float.parseFloat(values[i]);
            rot.setValueByNumber(i - 1, val);

        }
        for (int i = 4; i < 7; i++) {
            float val = Float.parseFloat(values[i]);
            acce.setValueByNumber(i - 4, val);

        }
        for (int i = 7; i < 10; i++) {
            float val = Float.parseFloat(values[i]);
            magn.setValueByNumber(i - 7, val);

        }

    }

    public static Sample sclarSubtraction(float theta, Sample a) {
        return new Sample(a.rot.x - theta, a.rot.y - theta, a.rot.z - theta, a.acce.x - theta,
                a.acce.y - theta, a.acce.z - theta, a.magn.x - theta, a.magn.y - theta, a.magn.z - theta, true);
    }

    public static Sample scalarAddition(float theta, Sample a) {
        return new Sample(a.rot.x + theta, a.rot.y + theta, a.rot.z + theta, a.acce.x + theta,
                a.acce.y + theta, a.acce.z + theta, a.magn.x + theta, a.magn.y + theta, a.magn.z + theta, true);
    }

    public static Sample subtract(Sample a, Sample b) {


        return new Sample(Vector3.subtract(a.acce, b.acce), Vector3.subtract(a.rot, b.rot), Vector3.subtract(a.magn, b.magn));

    }

    public static Sample add(Sample a, Sample b) {

        return new Sample(Vector3.add(a.acce, b.acce), Vector3.add(a.rot, b.rot), Vector3.add(a.magn, b.magn));

    }

    public static Sample multiply(Sample a, Sample b) {
        return new Sample(Vector3.crossProduct(a.acce, b.acce), Vector3.crossProduct(a.rot, b.rot), Vector3.crossProduct(a.magn, b.magn));
    }

    public static Sample divideScalar(Sample a, float theta) {
        return new Sample(a.rot.x / theta, a.rot.y / theta, a.rot.z / theta, a.acce.x / theta,
                a.acce.y / theta, a.acce.z / theta, a.magn.x / theta, a.magn.y / theta, a.magn.z / theta, true);
    }

    public static Sample multiplyScalar(Sample a, float theta) {
        return new Sample(a.rot.x * theta, a.rot.y * theta, a.rot.z * theta, a.acce.x * theta,
                a.acce.y * theta, a.acce.z * theta, a.magn.x * theta, a.magn.y * theta, a.magn.z * theta, true);
    }


    public static Sample sqrt(Sample a) {
        return new Sample(Math.sqrt(a.rot.x), Math.sqrt(a.rot.y), Math.sqrt(a.rot.z), Math.sqrt(a.acce.x),
                Math.sqrt(a.acce.y), Math.sqrt(a.acce.z), Math.sqrt(a.magn.x), Math.sqrt(a.magn.y), Math.sqrt(a.magn.z), true);
    }


    public static boolean greaterThan(Sample a, Sample b) {
        return Vector3.greaterThan(a.rot, b.rot) && Vector3.greaterThan(a.acce, b.acce);

    }

    public static boolean lessThan(Sample a, Sample b) {
        return Vector3.lessThan(a.rot, b.rot) && Vector3.lessThan(a.acce, b.acce);

    }

    public void setTimeNow() {
        time = Calendar.getInstance().getTimeInMillis();
    }

    public void setRotation(double x, double y, double z) {
        rot = new Vector3(x, y, z);
    }

    public void setAcceleration(double x, double y, double z) {
        acce = new Vector3(x, y, z);
    }

    public void setCompass(double x, double y, double z) {
        magn = new Vector3(x, y, z);
    }

    public void Zero() {
        acce = new Vector3(0, 0, 0);
        rot = new Vector3(0, 0, 0);
        magn = new Vector3(0, 0, 0);
    }

    //allows returning of a value based on a index on 0-9
    public double getValueFromIndex(int index) {
        if (index / 3 == 0) {
            return rot.getValueByNumber(index - ((index / 3) * 3));
        }
        if (index / 3 == 1) {
            return acce.getValueByNumber(index - (((index) / 3) * 3));
        }
        if (index / 3 == 2) {
            return magn.getValueByNumber(index - ((index / 3) * 3));
        }
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        Sample no = (Sample) o;
        return (this.acce.equals(no.acce) && this.rot.equals(no.rot) && this.magn.equals(no.magn));
    }

    @Override
    public String toString() {

        return time + "," + rot.x + "," + rot.y + "," + rot.z + "," + acce.x + "," + acce.y + "," + acce.z + "," + magn.x + "," + magn.y + "," + magn.z + "\n";

    }
}
