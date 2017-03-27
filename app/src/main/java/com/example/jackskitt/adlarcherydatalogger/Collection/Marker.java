package com.example.jackskitt.adlarcherydatalogger.Collection;

public class Marker {

    public long startTime;
    public long endTime;
    public String note;

    //TO:DO need to add a time frame variable as well as the interface to create them.
    public Marker(String[] parsing) {
        startTime = Long.parseLong(parsing[0]);
        endTime = Long.parseLong(parsing[1]);
        note = parsing[2];
    }

    public Marker(long start, long end, String note) {
        startTime = start;
        endTime = end;
        this.note = note;
    }
}
