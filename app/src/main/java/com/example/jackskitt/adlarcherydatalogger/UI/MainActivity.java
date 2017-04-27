package com.example.jackskitt.adlarcherydatalogger.UI;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import com.example.jackskitt.adlarcherydatalogger.Adapters.TabViewAdapter;
import com.example.jackskitt.adlarcherydatalogger.Processing.TemplateStore;
import com.example.jackskitt.adlarcherydatalogger.R;
import com.example.jackskitt.adlarcherydatalogger.Sensors.SensorStore;

public class MainActivity extends FragmentActivity {
    public static  Context        context;
    private static MainActivity   instance;
    public         SensorStore    store;
    public         TabViewAdapter adapter;
    boolean mBound = false;
    private ViewPager viewPage;
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            SensorStore.LocalBinder binder = (SensorStore.LocalBinder) service;
            store = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    public static synchronized MainActivity getInstance() {
        return instance;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        setContentView(R.layout.main_activity);
        context = getBaseContext();
        // Get the ViewPager and set it's PagerAdapter so that it can display items
        viewPage = (ViewPager) findViewById(R.id.pager);
        adapter = new TabViewAdapter(getFragmentManager(), MainActivity.this);
        viewPage.setAdapter(adapter);

        this.bindService(new Intent(getApplicationContext(), SensorStore.class), mConnection, Context.BIND_AUTO_CREATE);
        //create the templateStore;
        new TemplateStore();
        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab);
        tabLayout.setupWithViewPager(viewPage);
        tabLayout.setNestedScrollingEnabled(true);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        this.stopService(new Intent(getBaseContext(), SensorStore.class));

    }
}
