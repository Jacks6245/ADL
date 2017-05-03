package com.example.jackskitt.adlarcherydatalogger.Adapters;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.jackskitt.adlarcherydatalogger.R;

import java.util.ArrayList;

/**
 * Created by Jack Skitt on 11/04/2017.
 */
//adapter for the bluetooth device selection list
public class BluetoothSensorsListAdapter extends BaseAdapter {
    public  TextView                   deviceName;
    public  TextView                   deviceConnected;
    private ArrayList<BluetoothDevice> sensorDevices;
    private LayoutInflater             listItemInflater;
    private Context                    context;


    public BluetoothSensorsListAdapter(Context context) {
        super();
        this.context = context;
        sensorDevices = new ArrayList<>();
        listItemInflater = LayoutInflater.from(context);

    }

    public void clearList() {
        sensorDevices.clear();
    }

    public void addDevice(BluetoothDevice toAdd) {
        //adds a device to the sensor List
        if (!sensorDevices.contains(toAdd)) {
            sensorDevices.add(toAdd);

        }
    }

    public int getPositionOfDevice(BluetoothDevice toFind) {
        return sensorDevices.lastIndexOf(toFind);
    }

    public BluetoothDevice getDevice(int i) {
        return sensorDevices.get(i);
    }

    @Override
    public int getCount() {
        return sensorDevices.size();
    }

    @Override
    public Object getItem(int position) {
        return sensorDevices.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = listItemInflater.inflate(R.layout.list_view_item, null);
            //defines a custom list item that lists the device name and
            deviceName = (TextView) convertView.findViewById(R.id.deviceName);
            deviceConnected = (TextView) convertView.findViewById(R.id.connectedText);
        }

        //Assigns a new device based on the selected device
        BluetoothDevice device = sensorDevices.get(position);
        deviceName.setText(device.getName());
        deviceConnected.setText(device.getAddress());

        return convertView;
    }
}
