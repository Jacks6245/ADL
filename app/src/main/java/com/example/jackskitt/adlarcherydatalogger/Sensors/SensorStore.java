package com.example.jackskitt.adlarcherydatalogger.Sensors;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;

import com.example.jackskitt.adlarcherydatalogger.Collection.SampleStorage;
import com.example.jackskitt.adlarcherydatalogger.Collection.Sequence;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class SensorStore {


    //these are the UDIDs used by the firmware
    public final static UUID DEVICE_INFORMATION_SERVICE_UUID = UUID.fromString("0000180A-0000-1000-8000-00805F9B43FB");
    public final static UUID MANUFACTURER_NAME_UUID = UUID.fromString("00002A00-0000-1000-8000-00805F9B43FB");

    public final static UUID BATTERY_SERVICE_UUID = UUID.fromString("0000180F-0000-1000-8000-00805F9B43FB");
    public final static UUID BATTERY_LEVEL_UUID = UUID.fromString("00002A19-0000-1000-8000-00805F9B43FB");

    public final static UUID NRF_UART_SERVICE_UUID = UUID.fromString("6E400001-B5A3-F393-E0A9-E50E24DCCA9E");
    public final static UUID NRF_UART_RX_UUID = UUID.fromString("6E400002-B5A3-F393-E0A9-E50E24DCCA9E"); //write without response
    public final static UUID NRF_UART_TX_UUID = UUID.fromString("6E400003-B5A3-F393-E0A9-E50E24DCCA9E"); //notify

    public final static UUID DFU_SERVICE = UUID.fromString("00001530-1212-EFDE-1523-785FEABCD123");

    //universal for Notify or Indicate types of characteristics
    public final static UUID CLIENT_CHARACTERISTIC_CONFIGURATION_DESCRIPTOR = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    // open accessibility for tests
    public List<Sensor> sensorStorage = new ArrayList<Sensor>();
    public List<String> outgoingPorts = new ArrayList<String>();

    public int numSensors;

    public boolean hasSensors = false;

    public boolean saveData = false;

    private static SensorStore instance;

    public BluetoothManager mBluetoothManager;
    public BluetoothAdapter mBluetoothAdapter;


    public static SensorStore getInstance() {
        if (instance == null) {
            instance = new SensorStore();
        }
        return instance;
    }

    public SensorStore() {
        // perform an initial search
        createBlankSensors();
    }

    public void createBlankSensors() {

        for (int i = 0; i < 2; i++) {
            Sensor tempSensor = new Sensor();
            tempSensor.id = i;
            sensorStorage.add(tempSensor);
        }
    }

    public Sensor getSensorByName(String name) {
        for (Sensor s : sensorStorage) {

            if (s.getName().equals(name)) {
                return s;
            }
        }
        return null;
    }

    public boolean connectToSensor(String sensor) {
        Sensor tempSensor = new Sensor();
        tempSensor.collectData = true;
        if (tempSensor.connect(sensor)) {


            tempSensor.id = numSensors;
            sensorStorage.add(tempSensor);
            numSensors++;

            return true;
        } else {
            System.out.println("ERROR: Sensor cannot be added.");
            return false;
        }

    }

    // method to filter out the names of the outgoing ports, this makes the
    // assumption that the only things connected to the com ports are the
    // sensors
    public void findOutgoingPorts() {

    }

    public void clearLog() {
        if (hasSensors) {
            for (SampleStorage storage : Sequence.getInstance().sequenceData) {
                storage.getSamples().clear();// wipes the storage
            }
        }
    }

    public static boolean isDeviceInformationService(final BluetoothGattService service) {
        return service != null && SensorStore.getInstance().DEVICE_INFORMATION_SERVICE_UUID.equals(service.getUuid());
    }

    public static boolean isManufacturerNameCharacteristic(final BluetoothGattCharacteristic characteristic) {
        return characteristic != null && SensorStore.getInstance().MANUFACTURER_NAME_UUID.equals(characteristic.getUuid());
    }

    public static boolean isBatteryService(final BluetoothGattService service) {
        return service != null && SensorStore.getInstance().BATTERY_SERVICE_UUID.equals(service.getUuid());
    }

    public static boolean isBatteryLevelCharacteristic(final BluetoothGattCharacteristic characteristic) {
        return characteristic != null && SensorStore.getInstance().BATTERY_LEVEL_UUID.equals(characteristic.getUuid());
    }

    public static boolean isNRFUartService(final BluetoothGattService service) {
        return service != null && SensorStore.getInstance().NRF_UART_SERVICE_UUID.equals(service.getUuid());
    }

    public static boolean isNRFUartWriteCharacteristic(final BluetoothGattCharacteristic characteristic) {
        return characteristic != null && SensorStore.getInstance().NRF_UART_RX_UUID.equals(characteristic.getUuid());
    }

    public static boolean isNRFUartReadCharacteristic(final BluetoothGattCharacteristic characteristic) {
        return characteristic != null && SensorStore.getInstance().NRF_UART_TX_UUID.equals(characteristic.getUuid());
    }
}
