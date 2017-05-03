package com.example.jackskitt.adlarcherydatalogger.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.jackskitt.adlarcherydatalogger.Collection.FileManager;
import com.example.jackskitt.adlarcherydatalogger.Profiles.Profile;
import com.example.jackskitt.adlarcherydatalogger.R;

import java.io.File;
import java.lang.reflect.Array;
import java.security.PublicKey;
import java.util.ArrayList;

/**
 * Created by Jack Skitt on 11/04/2017.
 */

public class ProfileLoadAdapter extends BaseAdapter {
    public TextView profileName;
    public TextView noFiles;
    private ProfileListValue[] profileList = new ProfileListValue[]{};
    private LayoutInflater listItemInflater;
    private Context        context;


    public ProfileLoadAdapter(Context context) {
        super();
        this.context = context;

        listItemInflater = LayoutInflater.from(context);
        //gets all the profiles from the file store
        profileList = FileManager.findAllProfiles().toArray(profileList);

    }

    public String getProfileName(int position) {
        return profileList[position].name;
    }

    public ProfileListValue getStringData(int position) {
        return profileList[position];
    }

    @Override
    public int getCount() {
        return profileList.length;
    }

    @Override
    public Object getItem(int position) {
        return profileList[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//load the ProfileListValues from the saved list
        if (convertView == null) {
            convertView = listItemInflater.inflate(R.layout.list_view_item, null);
            profileName = (TextView) convertView.findViewById(R.id.deviceName);
            noFiles = (TextView) convertView.findViewById(R.id.connectedText);
        }
        if (profileList.length != 0) {
            ProfileListValue device = profileList[position];
            profileName.setText(device.name);
            noFiles.setText("Number of recordings: " + device.count);
        }
        return convertView;
    }


}
