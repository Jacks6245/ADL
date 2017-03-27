package com.example.jackskitt.adlarcherydatalogger.Sensors;

/**
 * Created by Heli on 12.11.2015.
 */
class AistinBlueBoard {

    private static final String TAG = "AistinBlue";

    //Kionix type data ver. 0.15
    private static final int AX_HI_POSITION = 0;
    private static final int AX_LO_POSITION = 1;
    private static final int AY_HI_POSITION = 2;
    private static final int AY_LO_POSITION = 3;
    private static final int AZ_HI_POSITION = 4;
    private static final int AZ_LO_POSITION = 5;
    private static final int T0_SEQ0_POSITION = 6;
    private static final int MX_HI_POSITION = 7;
    private static final int MX_LO_POSITION = 8;
    private static final int MY_HI_POSITION = 9;
    private static final int MY_LO_POSITION = 10;
    private static final int MZ_HI_POSITION = 11;
    private static final int MZ_LO_POSITION = 12;
    private static final int GX_HI_POSITION = 13;
    private static final int GX_LO_POSITION = 14;
    private static final int GY_HI_POSITION = 15;
    private static final int GY_LO_POSITION = 16;
    private static final int GZ_HI_POSITION = 17;
    private static final int GZ_LO_POSITION = 18;
    private static final int T1_SEQ1_POSITION = 19;

    private static final String ACC_MAG_SENSOR_ID = "KMX62";
    private static final String GYR_SENSOR_ID = "KXG03";

    private static final String CSV_SEPARATOR = ";";
    private static final String LOG_FORMAT_TYPE_1 = "dataID" + CSV_SEPARATOR +
            "accX" + CSV_SEPARATOR + "accY" + CSV_SEPARATOR + "accZ" + CSV_SEPARATOR +
            "magX" + CSV_SEPARATOR + "magY" + CSV_SEPARATOR + "magZ" + CSV_SEPARATOR +
            "gyrX" + CSV_SEPARATOR + "gyrY" + CSV_SEPARATOR + "gyrZ";
    public static final String LOG_FORMAT_TYPE_2 = "sequenceID" + CSV_SEPARATOR + LOG_FORMAT_TYPE_1;
    public static final String LOG_FORMAT_TYPE_3 = "timestamp(ms)" + CSV_SEPARATOR + "sensorID" + CSV_SEPARATOR + "data";
    public static final String LOG_FORMAT_TYPE_4 = "sequence" + CSV_SEPARATOR + "sensorID" + CSV_SEPARATOR + "data";
    public static final String DATA_LOG_FORMAT = LOG_FORMAT_TYPE_4;

    public static int[] sequenceNumbersArray = new int[255];

    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    /* Converts an arbitrary array of bytes to a single hex string */
    public static String ConvertByteArrayToHexString(byte[] data) {
        char[] hexData = new char[data.length * 2];
        for (int j = 0; j < data.length; j++) {
            int v = data[j] & 0xFF;
            hexData[j * 2] = HEX_ARRAY[v >>> 4];
            hexData[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexData);
    }

    /* Converts an array of two bytes to a signed integer */
    private static int ConvertTwoByteValueToSInt(byte[] b) {
        int i = 0;
        if (b.length != 2) {
            return i;
        }
        i = (((b[0] & 0xff) << 8) | (b[1] & 0xff));
        if (i > 32767) i -= 65536;
        return i;
    }

    public static int getTimestampAsMs(byte[] data, int pointer) {
        byte[] ts = {data[pointer]};
        return ((ts[0] & 0xff) * 1000) / 1024;
    }

    private static int getSequence(byte[] data, int pointer) {
        byte[] seq = {data[pointer]};
        return seq[0] & 0xff;
    }

    private static int getDataFromBytesAsSInt(byte[] data, int hi, int lo) {
        byte[] value = {data[hi], data[lo]};
        return ConvertTwoByteValueToSInt(value);
    }

    public static String ParseMessageToSignedIntStringCSV(byte[] data) {
        if (data.length != 20) {
            return null;
        }
        //String register = getRegisterIDAsString(data);
        return getSequence(data, T0_SEQ0_POSITION) + CSV_SEPARATOR + ACC_MAG_SENSOR_ID + CSV_SEPARATOR
                + getDataFromBytesAsSInt(data, AX_HI_POSITION, AX_LO_POSITION) + CSV_SEPARATOR
                + getDataFromBytesAsSInt(data, AY_HI_POSITION, AY_LO_POSITION) + CSV_SEPARATOR
                + getDataFromBytesAsSInt(data, AZ_HI_POSITION, AZ_LO_POSITION) + CSV_SEPARATOR
                + getDataFromBytesAsSInt(data, MX_HI_POSITION, MX_LO_POSITION) + CSV_SEPARATOR
                + getDataFromBytesAsSInt(data, MY_HI_POSITION, MY_LO_POSITION) + CSV_SEPARATOR
                + getDataFromBytesAsSInt(data, MZ_HI_POSITION, MZ_LO_POSITION) + "\n"

                + getSequence(data, T1_SEQ1_POSITION) + CSV_SEPARATOR + GYR_SENSOR_ID + CSV_SEPARATOR
                + getDataFromBytesAsSInt(data, GX_HI_POSITION, GX_LO_POSITION) + CSV_SEPARATOR
                + getDataFromBytesAsSInt(data, GY_HI_POSITION, GY_LO_POSITION) + CSV_SEPARATOR
                + getDataFromBytesAsSInt(data, GZ_HI_POSITION, GZ_LO_POSITION);
    }
}
