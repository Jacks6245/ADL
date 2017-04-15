package com.example.jackskitt.adlarcherydatalogger.Math;

import android.graphics.Color;

import javax.vecmath.Quat4d;

public class Quaternion extends Quat4d {
    public static int[] colours = {Color.GREEN, Color.RED, Color.WHITE, Color.BLUE};

    public Quaternion(double w, double x, double y, double z) {
        // TODO Auto-generated constructor stub
        this.w = w;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static Quaternion subtract(Quaternion a, Quaternion b) {
        return new Quaternion(a.w - b.w, a.x - b.x, a.y - b.y, a.z - b.z);
    }

    public static Quaternion add(Quaternion a, Quaternion b) {
        return new Quaternion(a.w + b.w, a.x + b.x, a.y + b.y, a.z + b.z);
    }

    public static Quaternion multiply(Quaternion a, Quaternion b) {
        return new Quaternion(a.w * b.w, a.x * b.x, a.y * b.y, a.z * b.z);
    }

    public static Quaternion divideScalar(Quaternion a, float theta) {
        return new Quaternion(a.w / theta, a.x / theta, a.y / theta, a.z / theta);
    }

    public static boolean greaterThan(Quat4d a, Quat4d b) {
        return a.w > b.w && a.x > b.x && a.y > b.y && a.z > b.z;
    }

    public static boolean lessThan(Quat4d a, Quat4d b) {
        return a.w < b.w && a.x < b.x && a.y < b.y && a.z < b.z;
    }

    public double getValueByNumber(int i) {
        switch (i) {
            case 0:
                return w;
            case 1:
                return x;
            case 2:
                return y;
            case 3:
                return z;
        }
        return 0;
    }

    public void setValueByNumber(int i, float value) {
        switch (i) {
            case 0:
                w = value;
                break;
            case 1:
                x = value;
                break;
            case 2:
                y = value;
                break;
            case 4:
                z = value;
                break;
        }
    }
}
