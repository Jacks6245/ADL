package com.example.jackskitt.adlarcherydatalogger.Adapters;

import android.content.Context;

import android.app.Fragment;
import android.app.FragmentManager;

import android.support.v13.app.FragmentPagerAdapter;

import com.example.jackskitt.adlarcherydatalogger.UI.MainActivity;
import com.example.jackskitt.adlarcherydatalogger.UI.SensorView;

/**
 * Created by Jack Skitt on 03/04/2017.
 */

public class TabViewAdapter extends FragmentPagerAdapter {
    private String[] tabNames = {"Setup", "Bow Sensor", "Glove Sensor", "Average", "Analysis"};

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

                return new SensorView();

            case 1:
                //chart 1

                return new SensorView();
            case 2:
                //chart 2

                return new SensorView();
            case 3:
                //average tap

                return new SensorView();
            case 4:
                //analysis

                return new SensorView();
            default:
                break;
        }
        return null;
    }

    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 5;
    }

    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabNames[position];
    }
}
