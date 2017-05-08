package com.example.jackskitt.adlarcherydatalogger;

import com.example.jackskitt.adlarcherydatalogger.Collection.Sample;
import com.example.jackskitt.adlarcherydatalogger.Collection.SampleStorage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.Collections;


public class SampleStoreTests {
    SampleStorage a    = new SampleStorage();
    Sample[]      test = new Sample[]{new Sample(0, 0, 0, 0, 0, 0, 0, 0, 0, true),
            new Sample(1, 1, 1, 1, 1, 1, 1, 1, 1, true),
            new Sample(2, 2, 2, 2, 2, 2, 2, 2, 2, true),
            new Sample(3, 3, 3, 3, 3, 3, 3, 3, 3, true),
            new Sample(4, 4, 4, 4, 4, 4, 4, 4, 4, true)};

    public SampleStoreTests() {
        Collections.addAll(a.getSamples(), test);
    }

    @Test
    public void testSplitCount() {
        SampleStorage b = a.split(1, 3);
        assertEquals(3, b.getSamples().size());

    }

    @Test
    public void testFirstValue() {
        SampleStorage b = a.split(1, 3);
        assertEquals(new Sample(1, 1, 1, 1, 1, 1, 1, 1, 1, true), b.getSamples().get(0));

    }

    @Test
    public void testLastValue() {
        SampleStorage b = a.split(1, 3);
        assertEquals(new Sample(3, 3, 3, 3, 3, 3, 3, 3, 3, true), b.getSamples().get(2));

    }

}
