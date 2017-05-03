package com.example.jackskitt.adlarcherydatalogger.Collection;

import com.example.jackskitt.adlarcherydatalogger.Processing.Matchers.PatternMatcher;

public class Event {
    //an event specified a particular event in the data stream, e.g a bow shot or a bow draw
    public int                         startTime;
    public int                         endTime;
    public double                      probability;
    public PatternMatcher.TemplateType eventType;

    //First overload allows for adding from a file
    //TODO: need to add a time frame variable as well as the interface to create them.
    public Event(String[] parsing) {
        startTime = Integer.parseInt(parsing[0]);
        endTime = Integer.parseInt(parsing[1]);
        probability = Double.parseDouble(parsing[2]);
    }

    public Event(int start, int end, double probability) {
        startTime = start;
        endTime = end;
        this.probability = probability;
    }
}
