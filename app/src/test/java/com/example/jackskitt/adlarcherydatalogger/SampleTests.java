package com.example.jackskitt.adlarcherydatalogger;

import com.example.jackskitt.adlarcherydatalogger.Collection.Sample;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class SampleTests {
    Sample a = new Sample(2, 3, 1, 2, 3, 4, 1, 1, 1, true);
    Sample b = new Sample(0, 0, 0, 1, 2, 1, 1, 1, 1, true);

    @Test
    public void addSamples() throws Exception {
        assertEquals(new Sample(2.0d, 3.0d, 1.0d, 3.0, 5.0d, 5.0d, 2.0d, 2.0d, 2.0d, true), Sample.add(a, b));
    }

    @Test
    public void testSubtractSamples() throws Exception {

        assertEquals(new Sample(2, 3, 1, 1, 1, 3, 0, 0, 0, true), Sample.subtract(a, b));
    }

    @Test
    public void testMultipleSamples() throws Exception {

        assertEquals(new Sample(0, 0, 0, 2, 6, 4, 1, 1, 1, true), Sample.multiply(a, b));
    }

    @Test
    public void testAddScalar() throws Exception {
        assertEquals(new Sample(5, 6, 4, 5, 6, 7, 4, 4, 4, true), Sample.scalarAddition(3, a));
    }

    @Test
    public void testMiniusScalar() throws Exception {
        assertEquals(new Sample(-1, 0, -2, -1, 0, 1, -2, -2, -2, true), Sample.sclarSubtraction(3, a));
    }

    @Test
    public void testMultiplyScalar() throws Exception {
        assertEquals(new Sample(6, 9, 3, 6, 9, 12, 3, 3, 3, true), Sample.multiplyScalar(a, 3));
    }

    @Test
    public void testMultipleScalar() throws Exception {
        Sample c = new Sample(9, 9, 9, 9, 9, 9, 9, 9, 9, true);
        assertEquals(new Sample(3, 3, 3, 3, 3, 3, 3, 3, 3, true), Sample.divideScalar(c, 3));
    }

    @Test
    public void testGetIndex1() throws Exception {
        assertTrue(a.acce.x == a.getValueFromIndex(3));
    }

    @Test
    public void testGetIndex2() throws Exception {
        assertTrue(a.acce.y == a.getValueFromIndex(4));
    }

    @Test
    public void testGetIndex3() throws Exception {
        assertTrue(a.acce.z == a.getValueFromIndex(5));
    }

    @Test
    public void testGetIndex4() throws Exception {
        assertTrue(a.rot.x == a.getValueFromIndex(0));
    }

    @Test
    public void testGetIndex5() throws Exception {
        assertTrue(a.rot.y == a.getValueFromIndex(1));
    }

    @Test
    public void testGetIndex6() throws Exception {
        assertTrue(a.rot.z == a.getValueFromIndex(2));
    }

    @Test
    public void testGetIndex7() throws Exception {
        assertTrue(a.magn.x == a.getValueFromIndex(6));
    }

    @Test
    public void testGetIndex8() throws Exception {
        assertTrue(a.magn.y == a.getValueFromIndex(7));
    }

    @Test
    public void testGetIndex9() throws Exception {
        assertTrue(a.magn.z == a.getValueFromIndex(8));
    }
}