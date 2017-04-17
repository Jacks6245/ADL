package com.example.jackskitt.adlarcherydatalogger.Sensors;


import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.jackskitt.adlarcherydatalogger.Collection.Sample;
import com.example.jackskitt.adlarcherydatalogger.Collection.Sequence;
import com.example.jackskitt.adlarcherydatalogger.Math.MathHelper;
import com.example.jackskitt.adlarcherydatalogger.Profiles.Profile;
import com.example.jackskitt.adlarcherydatalogger.UI.SensorView;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

public class Sensor {

    public final static String INTENT_ACTION_GATT_CONNECTED           = "INTENT_ACTION_GATT_CONNECTED";
    public final static String INTENT_ACTION_GATT_DISCONNECTED        = "INTENT_ACTION_GATT_DISCONNECTED";
    public final static String INTENT_ACTION_GATT_SERVICES_DISCOVERED = "INTENT_ACTION_GATT_SERVICES_DISCOVERED";
    public final static String INTENT_ACTION_DATA_AVAILABLE           = "INTENT_ACTION_DATA_AVAILABLE";
    public final static String INTENT_ACTION_EXTRA_DATA               = "INTENT_ACTION_EXTRA_DATA";
    public final static String INTENT_DEVICE_DOES_NOT_SUPPORT_UART    = "INTENT_DEVICE_DOES_NOT_SUPPORT_UART";
    public final static String INTENT_ACTION_DATA_DISPLAY             = "INTENT_ACTION_DATA_DISPLAY";

    private final static String TAG = "com.example.jackskitt.adlarcherydatalogger.";

    public boolean collectData = false;
    public int        id;
    public SensorView chartViewReference;
    public LineChart[] charts = new LineChart[3];
    public BluetoothDevice device;
    public BluetoothGatt   mBluetoothGatt;
    public CHART_TYPE currentType = CHART_TYPE.ACCELERATION;

    private LocalBroadcastManager managerRef;
    private CONNECTED_STATE connectionState = CONNECTED_STATE.DISCONNECTED;
    private Context superContext;
    private boolean DEBUG = false;
    //

    public final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        final Intent blueIntent = new Intent();

        @Override

        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {

            if (newState == BluetoothProfile.STATE_CONNECTED) {

                connectionState = CONNECTED_STATE.CONNECTED;
                blueIntent.setAction(INTENT_ACTION_GATT_CONNECTED);
                blueIntent.putExtra("sensor", id);
                superContext.sendBroadcast(blueIntent);
                //       mBluetoothGatt.discoverServices();

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {

                connectionState = CONNECTED_STATE.DISCONNECTED;
                blueIntent.setAction(INTENT_ACTION_GATT_DISCONNECTED);
                blueIntent.putExtra("sensor", id);
                superContext.sendBroadcast(blueIntent);
            }
        }

        //need to send a notification to the SensorStore once services have been discovered
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {

            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (mBluetoothGatt != null) {
                    Log.d(this.getClass().getSimpleName(), "discovered: " + gatt.getServices());
                }
                blueIntent.setAction(INTENT_ACTION_GATT_SERVICES_DISCOVERED);
                blueIntent.putExtra("sensor", id);
                superContext.sendBroadcast(blueIntent);

                //     setCharacteristicNotificationNRFUartTX(true);
            } else {
                Log.w(this.getClass().getSimpleName(), "onServicesDiscovered: " + status);
            }
            //Auto-enable notifications from device:

        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (DEBUG) Log.d(this.getClass().getSimpleName(), "onCharacteristicRead");
            if (status == BluetoothGatt.GATT_SUCCESS) {
                blueIntent.setAction(INTENT_ACTION_EXTRA_DATA);
                blueIntent.putExtra("sensor", id);
                superContext.sendBroadcast(blueIntent);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
            if (DEBUG) Log.d(this.getClass().getSimpleName(), "incoming data");
            //parse and write to file

            processSample(characteristic.getValue());


            updateCharts();


            //  superContext.sendBroadcast(blueIntent);


        }
    };

    public Sensor(Context storeContext) {
        this.superContext = storeContext;

    }

    public void restartCharts() {
        for (LineChart chart : charts) {
            chart.setData(new LineData());
        }
        if (Profile.instance != null)
            Profile.instance.profileCurrentSequence.sequenceData[id].clear();
    }

    //TODO: this needs to be in the sensor thread
    public void processSample(byte[] sampleData) {

            if (sampleData != null) {

                Sample sample = getSample(sampleData);

                //Transform3D q = new Transform3D();
                // point representing the acceleration
                // sets the acceleration in world coordinates
                //    q.setRotation(sample.quat);
                //  q.transform(sample.acce);

                //       if (true) {
                // save the sample
                //         Sequence.getInstance().sequenceData[id].addSample(sample);
                //   }

                if (Profile.instance != null && collectData) {
                    Profile.instance.profileCurrentSequence.sequenceData[id].addSample(sample);
                }
                addSampleToChart(sample);



            }

    }

    public void addSampleToChart(Sample sampleToAdd) {
        for (int i = 0; i < 3; i++) {
            double value = 0;
            if (currentType == CHART_TYPE.ACCELERATION) {
                value = sampleToAdd.acce.getValueByNumber(i);
            } else if (currentType == CHART_TYPE.ROTATION) {
                value = sampleToAdd.quat.getValueByNumber(i + 1);//need to fix this
            } else if (currentType == CHART_TYPE.COMPASS) {
                value = sampleToAdd.magn.getValueByNumber(i);
            }

            addEntryToChart(charts[i], (float) value);

        }
    }

    private void updateCharts() {
        for (int i = 0; i < 3; i++) {

            charts[i].getData().notifyDataChanged();
            charts[i].notifyDataSetChanged();

            // limit the number of visible entries
            // mChart.setVisibleYRange(30, AxisDependency.LEFT);

            // move to the latest entry
            charts[i].moveViewToX(charts[i].getData().getEntryCount());
            charts[i].setVisibleXRangeMaximum(300);
            charts[i].setVisibleYRange(-3.5f, 3.5f, YAxis.AxisDependency.LEFT);
        }
    }

    public Context getSuperContext() {
        return superContext;
    }

    //this needs to be in the UI Thread, possibly moved to a different class
    private void addEntryToChart(LineChart chart, float dataToAdd) {

        LineData data = chart.getData();

            ILineDataSet set = data.getDataSetByIndex(0);
            // set.addEntry(...); // can be called as well

            if (set == null) {
                set = createSet();
                data.addDataSet(set);
            }

            data.addEntry(new Entry(set.getEntryCount(), dataToAdd), 0);


        // let the chart know it's data has change
    }

    private LineDataSet createSet() {

        LineDataSet set = new LineDataSet(null, "sensorData");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(Color.WHITE);
        set.setDrawCircles(false);
        set.setLineWidth(2f);
        set.setFillAlpha(65);
        set.setFillColor(Color.WHITE);
        set.setHighLightColor(Color.rgb(244, 117, 117));
        set.setValueTextColor(Color.WHITE);
        set.setValueTextSize(9f);
        set.setDrawValues(false);

        return set;
    }

    private Sample getSample(byte[] buffer) {

        float Ax = MathHelper.getDataFromBytesAsSInt(buffer, MathHelper.AX_HI_POSITION, MathHelper.AX_LO_POSITION);
        float Ay = MathHelper.getDataFromBytesAsSInt(buffer, MathHelper.AY_HI_POSITION, MathHelper.AY_LO_POSITION);
        float Az = MathHelper.getDataFromBytesAsSInt(buffer, MathHelper.AZ_HI_POSITION, MathHelper.AZ_LO_POSITION);

        float mx = MathHelper.getDataFromBytesAsSInt(buffer, MathHelper.MX_HI_POSITION, MathHelper.MX_LO_POSITION);
        float my = MathHelper.getDataFromBytesAsSInt(buffer, MathHelper.MY_HI_POSITION, MathHelper.MY_LO_POSITION);
        float mz = MathHelper.getDataFromBytesAsSInt(buffer, MathHelper.MZ_HI_POSITION, MathHelper.MZ_LO_POSITION);


        float qx = MathHelper.getDataFromBytesAsSInt(buffer, MathHelper.GX_HI_POSITION, MathHelper.GX_LO_POSITION);
        float qy = MathHelper.getDataFromBytesAsSInt(buffer, MathHelper.GY_HI_POSITION, MathHelper.GY_LO_POSITION);
        float qz = MathHelper.getDataFromBytesAsSInt(buffer, MathHelper.GZ_HI_POSITION, MathHelper.GZ_LO_POSITION);

        long time = MathHelper.getSequence(buffer, MathHelper.T0_SEQ0_POSITION);
        return new Sample(qx, qy, qz, Ax, Ay, Az, mx, my, mz, time);

    }

    //sensor connector stuff
        /* Closes the gatt client when device is not needed for use anymore */

    /* Disconnects a connection or cancels a pending connection, needs to go in the sensor */


    public void setCharacteristicNotificationNRFUartTX(boolean enabled) {
        Log.i(this.getClass().getSimpleName(), "setCharacteristicNotificationNRFUartTX enabled: " + enabled);
        final Intent         blueIntent     = new Intent();
        BluetoothGattService nRFUartService = mBluetoothGatt.getService(SensorStore.NRF_UART_SERVICE_UUID);
        if (nRFUartService == null) {
            Log.w(this.getClass().getSimpleName(), "nRFUartService not found");
            blueIntent.setAction(TAG + INTENT_DEVICE_DOES_NOT_SUPPORT_UART);
            blueIntent.putExtra("sensor", id);
            superContext.sendBroadcast(blueIntent);
            return;
        }
        BluetoothGattCharacteristic nRFUartTXCharacteristic = nRFUartService.getCharacteristic(SensorStore.NRF_UART_TX_UUID);
        if (nRFUartTXCharacteristic == null) {
            Log.w(this.getClass().getSimpleName(), "nRFUartTXCharacteristic not found");
            blueIntent.setAction(TAG + INTENT_DEVICE_DOES_NOT_SUPPORT_UART);
            blueIntent.putExtra("sensor", id);
            superContext.sendBroadcast(blueIntent);
            return;
        }
        BluetoothGattDescriptor descriptor = nRFUartTXCharacteristic.getDescriptor(SensorStore.CLIENT_CHARACTERISTIC_CONFIGURATION_DESCRIPTOR);
        if (enabled) {
            mBluetoothGatt.setCharacteristicNotification(nRFUartTXCharacteristic, true);
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        } else {
            mBluetoothGatt.setCharacteristicNotification(nRFUartTXCharacteristic, false);
            descriptor.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
        }
        mBluetoothGatt.writeDescriptor(descriptor);
    }

    public String getName() {

        return device.getName();
    }

    public void disconnect() {
        if (mBluetoothGatt == null) {
            return;
        }

        mBluetoothGatt.disconnect();
        close();
    }

    public boolean connectSensorGATT(BluetoothDevice device) {
        if (device == null) {
            //noSensor
            return false;
        }

        if (device != null && device.equals(this.device) && mBluetoothGatt != null) {
            mBluetoothGatt.connect();

            return true;
        }

        mBluetoothGatt = device.connectGatt(superContext, false, mGattCallback);
        return true;
    }

    public void close() {
        if (mBluetoothGatt == null) return;
        device = null;
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    public enum CHART_TYPE {
        ACCELERATION,
        ROTATION,
        COMPASS
    }


    public enum CONNECTED_STATE {
        DISCONNECTED,
        CONNECTING,
        CONNECTED;
    }


}
