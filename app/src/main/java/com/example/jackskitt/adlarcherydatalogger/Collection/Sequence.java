package com.example.jackskitt.adlarcherydatalogger.Collection;

import java.util.ArrayList;

public class Sequence {

    private static Sequence instance;

    //A sequence is a store of multiple senors
    public SampleStorage[] sequenceData = new SampleStorage[2];

    public String userName;
    public int sequenceID;

    public Sequence() {

    }

    public static Sequence getInstance() {
        if (instance.equals(null)) {
            instance = new Sequence();
        }
        return instance;
    }
}
