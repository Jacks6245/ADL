package com.example.jackskitt.adlarcherydatalogger.Math;

import javax.vecmath.Point3d;

public class Vector3 extends Point3d {

    public float magnitude;


    public Vector3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        magnitude();
    }

    public Vector3() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }

    public static Vector3 add(Vector3 base, Vector3 toAdd) {
        return new Vector3(base.x + toAdd.x, base.y + toAdd.y, base.z + toAdd.z);
    }

    public static Vector3 scalar(Vector3 base, float scalar) {
        return new Vector3(base.x * scalar, base.y * scalar, base.z * scalar);

    }

    public static Vector3 crossProduct(Vector3 a, Vector3 b) {
        return new Vector3(a.x * b.x, a.y * b.y, a.z * b.z);
    }

    public static Vector3 subtract(Vector3 a, Vector3 b) {
        return new Vector3(a.x - b.x, a.y - b.y, a.z - b.z);
    }

    public static boolean greaterThan(Vector3 a, Vector3 b) {
        return a.x > b.x && a.y > b.y && a.z > b.z;
    }

    public static boolean lessThan(Vector3 a, Vector3 b) {
        return a.x < b.x && a.y < b.y && a.z < b.z;
    }

    private void magnitude() {
        magnitude = (float) Math.sqrt((this.x * this.x) + (this.y * this.y) + (this.z * this.z));

    }

    public double getValueByNumber(int i) {
        switch (i) {
            case 0:
                return x;
            case 1:
                return y;
            case 2:
                return z;
        }
        return 0;
    }

    public void setValueByNumber(int i, double value) {
        switch (i) {
            case 0:
                x = value;
                break;
            case 1:
                y = value;
                break;
            case 2:
                z = value;
                break;

        }
    }
}
