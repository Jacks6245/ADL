package com.example.jackskitt.adlarcherydatalogger.Math;

import android.util.Log;

import java.nio.ByteBuffer;

public class MathHelper {
    public static final int AX_HI_POSITION   = 0;
    public static final int AX_LO_POSITION   = 1;
    public static final int AY_HI_POSITION   = 2;
    public static final int AY_LO_POSITION   = 3;
    public static final int AZ_HI_POSITION   = 4;
    public static final int AZ_LO_POSITION   = 5;
    public static final int T0_SEQ0_POSITION = 6;
    public static final int MX_HI_POSITION   = 7;
    public static final int MX_LO_POSITION   = 8;
    public static final int MY_HI_POSITION   = 9;
    public static final int MY_LO_POSITION   = 10;
    public static final int MZ_HI_POSITION   = 11;
    public static final int MZ_LO_POSITION   = 12;
    public static final int GX_HI_POSITION   = 13;
    public static final int GX_LO_POSITION   = 14;
    public static final int GY_HI_POSITION   = 15;
    public static final int GY_LO_POSITION   = 16;
    public static final int GZ_HI_POSITION   = 17;
    public static final int GZ_LO_POSITION   = 18;
    public static final int T1_SEQ1_POSITION = 19;


    public static int getSequence(byte[] data, int pointer) {
        byte[] seq = {data[pointer]};
        return seq[0] & 0xff;
    }

    public static long getTimeDifference(long timeA, long timeB) {

        return timeB - timeA;
    }

    public static int getDataFromBytesAsSInt(byte[] data, int hi, int lo) {
        byte[] value = {data[hi], data[lo]};
        return ConvertTwoByteValueToSInt(value);
    }

    public static int ConvertTwoByteValueToSInt(byte[] b) {
        int i = 0;
        if (b.length != 2) {
            return i;
        }
        i = (((b[0] & 0xff) << 8) | (b[1] & 0xff));
        if (i > 32767) i -= 65536;
        return i;
    }

    public static double calculateMean(double[] set) {
        double total = 0;
        for (double value : set) {
            total += value;
        }
        return (total / set.length);
    }

}
