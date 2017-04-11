package com.example.jackskitt.adlarcherydatalogger.Adapters;

import android.content.Context;

import android.app.Fragment;
import android.app.FragmentManager;

import android.support.v13.app.FragmentPagerAdapter;

import com.example.jackskitt.adlarcherydatalogger.Sensors.SensorStore;
import com.example.jackskitt.adlarcherydatalogger.UI.MainActivity;
import com.example.jackskitt.adlarcherydatalogger.UI.SensorView;
import com.example.jackskitt.adlarcherydatalogger.UI.SetupView;

/**
 * Created by Jack Skitt on 03/04/2017.
 */

public class TabViewAdapter extends FragmentPagerAdapter {
    private String[] tabNames = {"Setup", "Bow Sensor", "Glove Sensor", "Analysis"};

    private Context context;

    public TabViewAdapter(FragmentManager fm, MainActivity mainActivity) {
        super(fm);
        this.context = mainActivity;
    }

    @Override
    public Fragment getItem(int index) {
        switch (index) {
            case 0:
                //Setup Activity

                return new SetupView();

            case 1:
                //chart 1
                SensorView tempView = new SensorView();
                tempView.viewingSensor = SensorStore.getInstance().sensorStorage.get(0);

                return tempView;
            case 2:
                //chart 2
                SensorView tempView2 = new SensorView();
                tempView2.viewingSensor = SensorStore.getInstance().sensorStorage.get(1);
                return tempView2;
            case 3:
                //analysis


                return new SetupView();

            default:
                break;
        }
        return null;
    }

    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 4;
    }

    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabNames[position];
    }
}
