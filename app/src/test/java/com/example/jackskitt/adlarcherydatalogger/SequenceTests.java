package com.example.jackskitt.adlarcherydatalogger;

import com.example.jackskitt.adlarcherydatalogger.Collection.Event;
import com.example.jackskitt.adlarcherydatalogger.Collection.Sample;
import com.example.jackskitt.adlarcherydatalogger.Collection.SampleStorage;
import com.example.jackskitt.adlarcherydatalogger.Collection.Sequence;
import com.example.jackskitt.adlarcherydatalogger.Processing.TemplateStore;
import com.example.jackskitt.adlarcherydatalogger.Profiles.Profile;

import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.*;

/**
 * Created by Jack Skitt on 30/04/2017.
 */

public class SequenceTests {
    Profile       p  = new Profile("test");
    TemplateStore ts = new TemplateStore();


    Sequence testSequence = new Sequence();

    public SequenceTests() {
        SampleStorage a = new SampleStorage();
        Sample[] test = new Sample[]{new Sample(0, 0, 0, 0, 0, 0, 0, 0, 0, true),
                new Sample(1, 1, 1, 1, 1, 1, 1, 1, 1, true),
                new Sample(2, 2, 2, 2, 2, 2, 2, 2, 2, true),
                new Sample(3, 3, 3, 3, 3, 3, 3, 3, 3, true),
                new Sample(4, 4, 4, 4, 4, 4, 4, 4, 4, true)};

        SampleStorage b = new SampleStorage();
        Sample[] testb = new Sample[]{new Sample(0, 0, 0, 0, 0, 0, 0, 0, 0, true),
                new Sample(3, 1, 1, 1, 1, 1, 1, 1, 1, true),
                new Sample(4, 2, 2, 2, 2, 2, 2, 2, 2, true),
                new Sample(5, 3, 3, 3, 3, 3, 3, 3, 3, true),
                new Sample(6, 4, 4, 4, 4, 4, 4, 4, 4, true)};
        Collections.addAll(a.getSamples(), test);
        Collections.addAll(b.getSamples(), testb);
        testSequence.sequenceData[0] = a;
        testSequence.sequenceData[1] = b;
    }

    @Test
    public void padding() throws Exception {
        testSequence.endPadding(0, 10);
        assertEquals(15, testSequence.sequenceData[0].getSamples().size());
    }


    @Test
    public void splitTest() throws Exception {
        testSequence.splitEvent.add(new Event(0, 2, 0.2));
        testSequence.splitEvent.add(new Event(3, 5, 0.2));
        testSequence.splitSequence();
        assertEquals(15, 2);
    }

}
