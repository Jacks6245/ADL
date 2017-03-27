package com.example.jackskitt.adlarcherydatalogger.Math;

import java.nio.ByteBuffer;

public class MathHelper {

    public static long getTimeDifference(long timeA, long timeB) {

        return timeB - timeA;
    }

    public static int toInt(byte hb, byte lb) {
        ByteBuffer bb = ByteBuffer.wrap(new byte[]{hb, lb});
        return bb.getShort(); // Implicitly widened to an int per JVM spec.
    }
}
