package com.example.jackskitt.adlarcherydatalogger.Profiles;

import android.os.AsyncTask;
import android.widget.Toast;

import com.example.jackskitt.adlarcherydatalogger.Collection.Sequence;
import com.example.jackskitt.adlarcherydatalogger.Collection.SequenceStore;
import com.example.jackskitt.adlarcherydatalogger.UI.MainActivity;

/**
 * Created by Jack Skitt on 15/04/2017.
 */

public class Profile {

    public static Profile  instance;
    public        String   name;
    public        int      numberOfLogs;
    public        Sequence profileCurrentSequence;

    public SequenceStore sequenceStore;

    public Profile(String name) {
        instance = this;
        this.name = name;
        sequenceStore = new SequenceStore();
        profileCurrentSequence = new Sequence();
        profileCurrentSequence.sequenceID = 0;
    }

    public void loadProfile(final String name) {
        final boolean finishedLoadingProfile = false;
        this.name = name;
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                sequenceStore.createStore(name);
                profileCurrentSequence = new Sequence();
                profileCurrentSequence.sequenceID = sequenceStore.allSequences.size();


                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                Toast.makeText(MainActivity.getInstance().getBaseContext(), "Finished Loading Profile", Toast.LENGTH_SHORT).show();
                processProfile();
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


    }

    public void processProfile() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                sequenceStore.createAverageSequence();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                Toast.makeText(MainActivity.getInstance().getBaseContext(), "Finished Loading Profile", Toast.LENGTH_LONG).show();
                averageProcessing();
            }


        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                sequenceStore.createStandardDeviationSequence();

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                Toast.makeText(MainActivity.getInstance().getBaseContext(), "Finished creating stdDev", Toast.LENGTH_SHORT).show();

            }


        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


    }

    private void averageProcessing() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                sequenceStore.getAverageMin();
                sequenceStore.getAverageMax();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                Toast.makeText(MainActivity.getInstance().getBaseContext(), "Finished creating Min/Max sequence", Toast.LENGTH_SHORT).show();
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                sequenceStore.calculateCorrelAndCovar();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                Toast.makeText(MainActivity.getInstance().getBaseContext(), "Profile Load Complete", Toast.LENGTH_LONG).show();
                MainActivity.getInstance().adapter.analysisView.profileLoaded();
                MainActivity.getInstance().adapter.setupView.enableConnectButtons();
                MainActivity.getInstance().adapter.setupView.enableProfileButtons();
                MainActivity.getInstance().adapter.setupView.profileText.setText("Profile Loaded:" + Profile.instance.name);
            }


        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    public void newSequence() {
        profileCurrentSequence = new Sequence();
        profileCurrentSequence.sequenceID = sequenceStore.allSequences.size();
    }


}
