package com.example.jackskitt.adlarcherydatalogger.UI;

import android.app.FragmentTransaction;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;


import android.support.v4.view.ViewPager;
import android.os.Bundle;

import com.example.jackskitt.adlarcherydatalogger.Adapters.TabViewAdapter;
import com.example.jackskitt.adlarcherydatalogger.R;

public class MainActivity extends FragmentActivity {
    private ViewPager viewPage;
    private TabViewAdapter adapter;
    private TabLayout tabBar;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        viewPage = (ViewPager) findViewById(R.id.pager);
        viewPage.setAdapter(new TabViewAdapter(getFragmentManager(),
                MainActivity.this));

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab);
        tabLayout.setupWithViewPager(viewPage);

    }


}
