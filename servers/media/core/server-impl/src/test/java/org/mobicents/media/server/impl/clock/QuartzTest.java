/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mobicents.media.server.impl.clock;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Oleg Kulikov
 */
public class QuartzTest {

    private final static int TEST_DURATION = 20;
    private final static double DELAYS = 3;
    
    private int MAX_ERRORS;
        
    public QuartzTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        int ps = 1000 / Quartz.HEART_BEAT;        
        MAX_ERRORS = (int) Math.round(100.0/ps*DELAYS);
    }

    @After
    public void tearDown() {
    }

    @Test
    @SuppressWarnings("static-access")
    public void testHeartBeat() {
        Quartz quartz = new Quartz();
        quartz.testMode = true;
        Timer t = new TestTimer();
        quartz.addTimer(t);
        
        try {
            Thread.currentThread().sleep(TEST_DURATION * 1000);
        } catch (Exception e) {
        }
        
        quartz.removeTimer(t);
        if (quartz.errors > MAX_ERRORS) {
            fail("Too many delay delays: " + quartz.errors);
        }
        System.out.println("total delays=" + quartz.errors);
    }

    private class TestTimer extends Timer {

        @Override
        public void heartBeat() {
        }
    }
}