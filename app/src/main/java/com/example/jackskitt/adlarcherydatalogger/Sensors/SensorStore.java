package com.example.jackskitt.adlarcherydatalogger.Sensors;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.example.jackskitt.adlarcherydatalogger.Processing.TemplateStore;
import com.example.jackskitt.adlarcherydatalogger.UI.MainActivity;

import java.util.UUID;


public class SensorStore extends Service {
    //these are the UDIDs used by the firmware
    public final static UUID DEVICE_INFORMATION_SERVICE_UUID = UUID.fromString("0000180A-0000-1000-8000-00805F9B43FB");

    ;
    public final static UUID              MANUFACTURER_NAME_UUID                         = UUID.fromString("00002A00-0000-1000-8000-00805F9B43FB");
    public final static UUID              BATTERY_SERVICE_UUID                           = UUID.fromString("0000180F-0000-1000-8000-00805F9B43FB");
    public final static UUID              BATTERY_LEVEL_UUID                             = UUID.fromString("00002A19-0000-1000-8000-00805F9B43FB");
    public final static UUID              NRF_UART_SERVICE_UUID                          = UUID.fromString("6E400001-B5A3-F393-E0A9-E50E24DCCA9E");
    public final static UUID              NRF_UART_RX_UUID                               = UUID.fromString("6E400002-B5A3-F393-E0A9-E50E24DCCA9E"); //write without response
    public final static UUID              NRF_UART_TX_UUID                               = UUID.fromString("6E400003-B5A3-F393-E0A9-E50E24DCCA9E"); //notify
    public final static UUID              DFU_SERVICE                                    = UUID.fromString("00001530-1212-EFDE-1523-785FEABCD123");
    //universal for Notify or Indicate types of characteristics
    public final static UUID              CLIENT_CHARACTERISTIC_CONFIGURATION_DESCRIPTOR = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    private final       IBinder           mBinder                                        = new LocalBinder();
    // open accessibility for tests
    public              Sensor[]          sensors                                        = new Sensor[2];
    private final       BroadcastReceiver mGattStatusReceiver                            = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final int sensorIndex = intent.getIntExtra("sensor", 0);

            switch (intent.getAction()) {
                case Sensor.INTENT_ACTION_GATT_CONNECTED:
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            sensors[sensorIndex].mBluetoothGatt.discoverServices();
                        }
                    }).start();

                    break;
                case Sensor.INTENT_ACTION_GATT_DISCONNECTED:
                    //TODO: do something  here
                    break;

                case Sensor.INTENT_ACTION_GATT_SERVICES_DISCOVERED:
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            sensors[sensorIndex].setCharacteristicNotificationNRFUartTX(true);
                        }
                    }).start();
                    break;
                case Sensor.INTENT_ACTION_DATA_AVAILABLE:


                    break;
                case Sensor.INTENT_ACTION_DATA_DISPLAY:

                    break;
            }

        }
    };
    public int              numSensors;
    private boolean collectData = false;

    public static boolean isDevceInformationService(final BluetoothGattService service) {
        return service != null && SensorStore.DEVICE_INFORMATION_SERVICE_UUID.equals(service.getUuid());
    }

    public static boolean isManufacturerNameCharacteristic(final BluetoothGattCharacteristic characteristic) {
        return characteristic != null && SensorStore.MANUFACTURER_NAME_UUID.equals(characteristic.getUuid());
    }

    public static boolean isBatteryService(final BluetoothGattService service) {
        return service != null && SensorStore.BATTERY_SERVICE_UUID.equals(service.getUuid());
    }

    public static boolean isBatteryLevelCharacteristic(final BluetoothGattCharacteristic characteristic) {
        return characteristic != null && SensorStore.BATTERY_LEVEL_UUID.equals(characteristic.getUuid());
    }

    public static boolean isNRFUartService(final BluetoothGattService service) {
        return service != null && SensorStore.NRF_UART_SERVICE_UUID.equals(service.getUuid());
    }

    public static boolean isNRFUartWriteCharacteristic(final BluetoothGattCharacteristic characteristic) {
        return characteristic != null && SensorStore.NRF_UART_RX_UUID.equals(characteristic.getUuid());
    }

    public static boolean isNRFUartReadCharacteristic(final BluetoothGattCharacteristic characteristic) {
        return characteristic != null && SensorStore.NRF_UART_TX_UUID.equals(characteristic.getUuid());
    }

    private static IntentFilter gattActionIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Sensor.INTENT_ACTION_DATA_AVAILABLE);
        intentFilter.addAction(Sensor.INTENT_ACTION_EXTRA_DATA);
        intentFilter.addAction(Sensor.INTENT_ACTION_GATT_CONNECTED);
        intentFilter.addAction(Sensor.INTENT_ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(Sensor.INTENT_ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(Sensor.INTENT_DEVICE_DOES_NOT_SUPPORT_UART);
        intentFilter.addAction(Sensor.INTENT_ACTION_DATA_DISPLAY);
        return intentFilter;
    }

    public void setCollectData(boolean collectData) {
        TemplateStore.instance.resetTemplate(0);
        for (Sensor s : sensors) {
            s.chartViewReference.recordToggle.setChecked(collectData);
            s.collectData = collectData;
        }

        this.collectData = collectData;

    }

    public void createBlankSensors() {
        sensors[0] = new Sensor(getApplicationContext());
        sensors[0].id = 0;

        sensors[1] = new Sensor(getApplicationContext());
        sensors[1].id = 1;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        MainActivity.getInstance().store = this;
        createBlankSensors();

    }
    // method to filter out the names of the outgoing ports, this makes the
    // assumption that the only things connected to the com ports are the
    // sensors

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        registerReceiver(mGattStatusReceiver, gattActionIntentFilter());

        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        for (Sensor s : sensors) {
            s.disconnect();
        }
        unregisterReceiver(mGattStatusReceiver);
        disconnectAllSensors();
        return super.onUnbind(intent);
    }

    private void disconnectAllSensors() {
        for (Sensor s : sensors) {
            s.disconnect();
        }
    }

    //TODO: needs to be in a seperate thread possibly aync task
    public void connectToSensor(final BluetoothDevice address, final SENSORTYPE toConnectTo) {


        //TODO:  new thread here
        Thread connectionThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Sensor remoteSensor = new Sensor(getApplicationContext());
                if (toConnectTo == SENSORTYPE.BOW) {
                    remoteSensor = sensors[0];
                } else if (toConnectTo == SENSORTYPE.GLOVE) {
                    remoteSensor = sensors[1];
                }
                if (remoteSensor.connectSensorGATT(address)) {

                    remoteSensor.id = numSensors;

                    Log.i(address.getAddress(), "CONNECTED");

                    numSensors++;

                }
            }
        });
        connectionThread.start();


    }

    public enum SENSORTYPE {
        BOW,
        GLOVE
    }

    public class LocalBinder extends Binder {
        public SensorStore getService() {
            return SensorStore.this;
        }
    }


}
