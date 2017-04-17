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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.jackskitt.adlarcherydatalogger.Adapters.BluetoothSensorsListAdapter;
import com.example.jackskitt.adlarcherydatalogger.Adapters.ProfileLoadAdapter;
import com.example.jackskitt.adlarcherydatalogger.Profiles.Profile;
import com.example.jackskitt.adlarcherydatalogger.R;
import com.example.jackskitt.adlarcherydatalogger.Sensors.SensorStore;

import java.util.ArrayList;

/**
 * Created by Jack Skitt on 04/04/2017.
 */

public class SetupView extends Fragment {

    private ListView                    bluetoothList;
    private ListView                    profileList;
    private BluetoothSensorsListAdapter listAdapter;
    private ProfileLoadAdapter          profileAdapter;
    private BluetoothLeScanner          bluetoothScanner;
    private ScanSettings                settings;
    private BluetoothDevice             selectedDevice;

    private Button bowConnect;
    private Button gloveConnect;
    private Button bowDisconnect;
    private Button gloveDisconnect;
    private Button scanButton;

    private Button newProfileButton;
    private Button loadProfileButton;

    private EditText newProfileEntry;

    private TextView bowConnectionText;

    private TextView gloveConnectionText;
    private TextView profileText;
    private String   selectedProfile;
    private Handler  threadHandler;
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

        threadHandler = new Handler();


        bowConnect = (Button) view.findViewById(R.id.bowSensorConnect);
        gloveConnect = (Button) view.findViewById(R.id.gloveSensor);
        bowDisconnect = (Button) view.findViewById(R.id.bowSensorDisconnect);
        gloveDisconnect = (Button) view.findViewById(R.id.gloveDisconnect);

        scanButton = (Button) view.findViewById(R.id.scanbutton);
        newProfileButton = (Button) view.findViewById(R.id.buttonNewProfile);
        loadProfileButton = (Button) view.findViewById(R.id.buttonLoadProfile);

        bowConnectionText = (TextView) view.findViewById(R.id.bowSensorText);
        gloveConnectionText = (TextView) view.findViewById(R.id.gloveSensorText);
        profileText = (TextView) view.findViewById(R.id.profileText);
        bluetoothList = (ListView) view.findViewById(R.id.sensorsList);

        newProfileEntry = (EditText) view.findViewById(R.id.profileInput);


        profileList = (ListView) view.findViewById(R.id.profileList);
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


        bowDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.getInstance().store.sensors[0].disconnect();
                bowConnectionText.setText("Bow Sensor: ");
            }
        });

        gloveDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.getInstance().store.sensors[1].disconnect();
                gloveConnectionText.setText("Bow Sensor: ");
            }
        });


        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scan();
            }
        });

        listAdapter = new BluetoothSensorsListAdapter(getActivity());

        bluetoothList.setAdapter(listAdapter);

        bluetoothList.setOnItemClickListener(new AdapterView.OnItemClickListener() {


            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedDevice = listAdapter.getDevice(position);

            }
        });

        profileAdapter = new ProfileLoadAdapter(getActivity());
        profileList.setAdapter(profileAdapter);
        profileList.setOnItemClickListener(new AdapterView.OnItemClickListener() {


            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedProfile = profileAdapter.getProfileName(position);
                loadProfileButton.setEnabled(true);

            }
        });
        newProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Profile(newProfileEntry.getText().toString());
                profileText.setText("Profile: " + Profile.instance.name);
            }
        });
        loadProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedProfile != null) {

                    Profile tempProfile = new Profile(selectedProfile);
                    tempProfile.loadProfile(selectedProfile);
                    profileText.setText("Profile: " + Profile.instance.name);
                }
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
