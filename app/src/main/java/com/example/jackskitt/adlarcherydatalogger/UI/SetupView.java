package com.example.jackskitt.adlarcherydatalogger.UI;

import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.jackskitt.adlarcherydatalogger.Adapters.BluetoothSensorsListAdapter;
import com.example.jackskitt.adlarcherydatalogger.R;
import com.example.jackskitt.adlarcherydatalogger.Sensors.SensorStore;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by Jack Skitt on 04/04/2017.
 */

public class SetupView extends Fragment {
    private ListView                    bluetoothList;
    private BluetoothSensorsListAdapter listAdapter;
    private BluetoothLeScanner          bluetoothScanner;
    private ScanSettings                settings;
    private BluetoothDevice             selectedDevice;

    private Button bowConnect;
    private Button gloveConnect;
    private Button scanButton;

    private TextView bowConnectionText;

    private TextView gloveConnectionText;

    private Handler threadHandler;

    private boolean      isScanning       = false;
    private ScanCallback scanningCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            listAdapter.addDevice(result.getDevice());
            listAdapter.notifyDataSetChanged();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setup, container, false);
        setupBluetooth();

        bowConnect = (Button) view.findViewById(R.id.bowSensorConnect);
        gloveConnect = (Button) view.findViewById(R.id.gloveSensor);
        scanButton = (Button) view.findViewById(R.id.scanbutton);

        bowConnectionText = (TextView) view.findViewById(R.id.bowSensorText);
        gloveConnectionText = (TextView) view.findViewById(R.id.gloveSensorText);

        bowConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedDevice != null) {
                    MainActivity.getInstance().store.connectToSensor(selectedDevice, SensorStore.SENSORTYPE.BOW);
                    int toDisable = listAdapter.getPositionOfDevice(selectedDevice);
                    bluetoothList.getChildAt(toDisable).setEnabled(false);
                    bowConnectionText.setText("Bow Sensor: " + selectedDevice.getAddress());


                }
            }
        });
        gloveConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedDevice != null) {
                    MainActivity.getInstance().store.connectToSensor(selectedDevice, SensorStore.SENSORTYPE.GLOVE);
                    int toDisable = listAdapter.getPositionOfDevice(selectedDevice);
                    bluetoothList.getChildAt(toDisable).setEnabled(false);

                    gloveConnectionText.setText("Glove Sensor: " + selectedDevice.getAddress());

                }
            }
        });
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scan();
            }
        });


        bluetoothList = (ListView) view.findViewById(R.id.sensorsList);
        listAdapter = new BluetoothSensorsListAdapter(getActivity());

        bluetoothList.setAdapter(listAdapter);

        threadHandler = new Handler();

        bluetoothList.setOnItemClickListener(new AdapterView.OnItemClickListener() {


            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedDevice = listAdapter.getDevice(position);

            }
        });
        scan();
        return view;

    }

    private void scan() {
        threadHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

                isScanning = false;
                bluetoothScanner.stopScan(scanningCallback);
            }
        }, 10000);
        bluetoothScanner.startScan(new ArrayList<ScanFilter>(), settings, scanningCallback);
        isScanning = true;
    }

    private void setupBluetooth() {
        MainActivity.getInstance().store.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (MainActivity.getInstance().store.bluetoothAdapter == null) {
            //No Bluetooth  support
        }
        //enable bluetooth is disables
        if (!MainActivity.getInstance().store.bluetoothAdapter.isEnabled()) {
            Intent enableBluetoothSetting = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetoothSetting, 0);
        }
        bluetoothScanner = MainActivity.getInstance().store.bluetoothAdapter.getBluetoothLeScanner();
        settings = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_BALANCED).build();

    }

}
