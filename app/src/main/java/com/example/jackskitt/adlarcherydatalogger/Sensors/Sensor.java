package com.example.jackskitt.adlarcherydatalogger.Sensors;


import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;

import android.os.IBinder;

import android.util.Log;


import com.example.jackskitt.adlarcherydatalogger.Collection.Sample;
import com.example.jackskitt.adlarcherydatalogger.Collection.SampleStorage;
import com.example.jackskitt.adlarcherydatalogger.Math.MathHelper;

import java.util.UUID;

public class Sensor extends Service {

    private final static String TAG = Sensor.class.getSimpleName();

    public final static String INTENT_ACTION_GATT_CONNECTED = "INTENT_ACTION_GATT_CONNECTED";
    public final static String INTENT_ACTION_GATT_DISCONNECTED = "INTENT_ACTION_GATT_DISCONNECTED";
    public final static String INTENT_ACTION_GATT_SERVICES_DISCOVERED = "INTENT_ACTION_GATT_SERVICES_DISCOVERED";
    public final static String INTENT_ACTION_DATA_AVAILABLE = "INTENT_ACTION_DATA_AVAILABLE";
    public final static String INTENT_ACTION_EXTRA_DATA = "INTENT_ACTION_EXTRA_DATA";
    public final static String INTENT_DEVICE_DOES_NOT_SUPPORT_UART = "INTENT_DEVICE_DOES_NOT_SUPPORT_UART";

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;

    private boolean DEBUG = false;

    public SensorCollector graphStorage;

    public SampleStorage sampleStore;

    private BluetoothGatt mBluetoothGatt;

    private int mConnectionState = STATE_DISCONNECTED;
    private String mBluetoothDeviceAddress;
    public int id;
//needs replacing with the charts api
    //  public DrawCanvas accCanvas;
    //  public DrawCanvas quatCanvas;


    public boolean collectData;

    public boolean drawnCanvas = false;

    public class LocalBinder extends Binder {
        Sensor getService() {
            return Sensor.this;
        }
    }

    private final IBinder mBinder = new LocalBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        close();
        return super.onUnbind(intent);
    }

    public Sensor() {
        graphStorage = new SensorCollector(this);
        sampleStore = new SampleStorage(this);

        // accCanvas = new DrawCanvas();
        //  quatCanvas = new DrawCanvas();
    }


    public void processSample(byte[] sampleBytes) {
        if (collectData) {
            System.out.println("Process values");
            if (sampleBytes != null) {

                Sample sample = getSample(sampleBytes);

                //Transform3D q = new Transform3D();
                // point representing the acceleration
                // sets the acceleration in world coordinates
                //    q.setRotation(sample.quat);
                //  q.transform(sample.acce);

                if (SensorStore.getInstance().saveData) {
                    // save the sample
                    sampleStore.addSample(sample);
                }
                // further process the sample
                graphStorage.data.addSample(sample);

            }
        }
    }

    private Sample getSample(byte[] buffer) {

        float qw = MathHelper.toInt(buffer[1], buffer[0]);
        float qx = MathHelper.toInt(buffer[3], buffer[2]);
        float qy = MathHelper.toInt(buffer[5], buffer[4]);
        float qz = MathHelper.toInt(buffer[7], buffer[6]);

        float Ax = MathHelper.toInt(buffer[9], buffer[8]);
        float Ay = MathHelper.toInt(buffer[11], buffer[10]);
        float Az = MathHelper.toInt(buffer[13], buffer[12]);

        float mx = MathHelper.toInt(buffer[15], buffer[14]);
        float my = MathHelper.toInt(buffer[17], buffer[16]);
        float mz = MathHelper.toInt(buffer[19], buffer[20]);
        return new Sample(qx, qy, qz, qw, Ax, Ay, Az, mx, my, mz);

    }

    public boolean connect(final String deviceAddress) {
        if (DEBUG) Log.d(TAG, "connect");
        if (SensorStore.getInstance().mBluetoothAdapter == null || deviceAddress == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address");
            return false;
        }

        if (mBluetoothDeviceAddress != null && deviceAddress.equals(mBluetoothDeviceAddress) && mBluetoothGatt != null) {
            if (DEBUG) Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection");
            if (mBluetoothGatt.connect()) {
                mConnectionState = STATE_CONNECTING;
                return true;
            } else {
                return false;
            }
        }

        final BluetoothDevice device = SensorStore.getInstance().mBluetoothAdapter.getRemoteDevice(deviceAddress);
        if (device == null) {
            Log.w(TAG, "Unable to connect, device not found");
            return false;
        }

        mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
        if (DEBUG) Log.d(TAG, "Trying to create a new connection");
        mBluetoothDeviceAddress = deviceAddress;
        mConnectionState = STATE_CONNECTING;

        return true;
    }

    //sensor connector stuff
        /* Closes the gatt client when device is not needed for use anymore */
    public void close() {
        if (mBluetoothGatt == null) return;
        mBluetoothDeviceAddress = null;
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    /* Disconnects a connection or cancels a pending connection, needs to go in the sensor */
    public void disconnect() {
        if (SensorStore.getInstance().mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.disconnect();
    }

    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String intentAction;
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.i(TAG, "mGattCallback - Connected to GATT server");
                mConnectionState = STATE_CONNECTED;
                intentAction = INTENT_ACTION_GATT_CONNECTED;
                broadcastUpdate(intentAction);
                if (mBluetoothGatt != null) {
                    Log.i(TAG, "mGattCallback - Starting service discovery: " + mBluetoothGatt.discoverServices());
                }
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.i(TAG, "mGattCallback - Disconnected from GATT server");
                mConnectionState = STATE_DISCONNECTED;
                intentAction = INTENT_ACTION_GATT_DISCONNECTED;
                broadcastUpdate(intentAction);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (DEBUG) Log.d(TAG, "onServicesDiscovered");
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (mBluetoothGatt != null) {
                    Log.d(TAG, "discovered: " + gatt.getServices());
                }
                broadcastUpdate(INTENT_ACTION_GATT_SERVICES_DISCOVERED);
            } else {
                Log.w(TAG, "onServicesDiscovered: " + status);
            }
            //Auto-enable notifications from device:
            //setCharacteristicNotificationNRFUartTX(true);
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (DEBUG) Log.d(TAG, "onCharacteristicRead");
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(INTENT_ACTION_DATA_AVAILABLE, characteristic);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            if (DEBUG) Log.d(TAG, "incoming data");
            //parse and write to file
            final byte[] rawSensorData = characteristic.getValue();
            processSample(rawSensorData);
            //FIXME: writing to file seems to cause ART to suspend all threads multiple times

        }
    };


    private void broadcastUpdate(final String intentAction) {
        final Intent intent = new Intent(intentAction);
        sendBroadcast(intent);
    }

    private void broadcastUpdate(final String intentAction, final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(intentAction);

        if (SensorStore.isNRFUartReadCharacteristic(characteristic)) {
            byte[] data = characteristic.getValue();
            intent.putExtra(INTENT_ACTION_EXTRA_DATA, data);
        }
        sendBroadcast(intent);
    }


    public void setCharacteristicNotificationNRFUartTX(boolean enabled) {
        Log.i(TAG, "setCharacteristicNotificationNRFUartTX enabled: " + enabled);

        BluetoothGattService nRFUartService = mBluetoothGatt.getService(SensorStore.getInstance().NRF_UART_SERVICE_UUID);
        if (nRFUartService == null) {
            Log.w(TAG, "nRFUartService not found");
            broadcastUpdate(INTENT_DEVICE_DOES_NOT_SUPPORT_UART);
            return;
        }
        BluetoothGattCharacteristic nRFUartTXCharacteristic = nRFUartService.getCharacteristic(SensorStore.getInstance().NRF_UART_TX_UUID);
        if (nRFUartTXCharacteristic == null) {
            Log.w(TAG, "nRFUartTXCharacteristic not found");
            broadcastUpdate(INTENT_DEVICE_DOES_NOT_SUPPORT_UART);
            return;
        }
        BluetoothGattDescriptor descriptor = nRFUartTXCharacteristic.getDescriptor(SensorStore.getInstance().CLIENT_CHARACTERISTIC_CONFIGURATION_DESCRIPTOR);
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

        return mBluetoothDeviceAddress;
    }
}
