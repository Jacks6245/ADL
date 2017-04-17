package com.example.jackskitt.adlarcherydatalogger.Profiles;

import android.os.AsyncTask;

import com.example.jackskitt.adlarcherydatalogger.Collection.Sequence;
import com.example.jackskitt.adlarcherydatalogger.Collection.SequenceStore;

/**
 * Created by Jack Skitt on 15/04/2017.
 */

public class Profile {

    public String name;

    public int numberOfLogs;

    public static Profile instance;

    public Sequence profileCurrentSequence;

    public SequenceStore sequenceStore;

    public Profile(String name) {
        instance = this;
        this.name = name;
        sequenceStore = new SequenceStore();
        profileCurrentSequence = new Sequence();
        profileCurrentSequence.sequenceID = 0;
    }

    public void loadProfile(final String name) {

        this.name = name;
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                sequenceStore.createStore(name);
                profileCurrentSequence = new Sequence();
                profileCurrentSequence.sequenceID = sequenceStore.allSequences.size();

                return null;
            }
        }.execute();
    }

    public void newSequence() {
        sequenceStore.allSequences.add(profileCurrentSequence);
        profileCurrentSequence = new Sequence();
        profileCurrentSequence.sequenceID = sequenceStore.allSequences.size();
    }


}
