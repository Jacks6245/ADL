package com.example.jackskitt.adlarcherydatalogger.Collection;

import com.example.jackskitt.adlarcherydatalogger.Processing.Template;

public class Event {

    public int                   startTime;
    public int                   endTime;
    public double                probability;
    public Template.TemplateType eventType;

    //TO:DO need to add a time frame variable as well as the interface to create them.
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
