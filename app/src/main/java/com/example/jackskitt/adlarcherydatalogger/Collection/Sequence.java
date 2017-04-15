package com.example.jackskitt.adlarcherydatalogger.Collection;

public class Sequence {

    private static Sequence instance;

    //A sequence is a store of multiple senors
    public SampleStorage[] sequenceData = new SampleStorage[2];

    public String userName;
    public int    sequenceID;

    public Sequence() {

    }

    public static Sequence getInstance() {
        if (instance == null) {
            instance = new Sequence();
        }
        return instance;
    }
}
