package com.github.mitschi;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ExampleTest {

    private Example example;

    @Before
    public void setUp() {
        this.example = new Example();
    }

    @Test
    public void exampleTest() {
        int result = example.multByAddition(4, 5);
        assertEquals(20,result);
    }
}
