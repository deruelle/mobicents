/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mobicents.media.server.impl.clock;

import java.util.ArrayList;
import java.util.List;
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
public class TimerTest {

    private final static int TEST_DURATION = 20;
    private final static double ERRORS = 5;
    private int MAX_ERRORS;
    private final static int N = 100;
    private List[] res = new List[N];
    private Timer[] timers = new Timer[N];
    private int count = 0;
    private int errorCount;

    public TimerTest() {
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
        MAX_ERRORS = (int) Math.round(100.0 / ps * ERRORS);

        for (int i = 0; i < N; i++) {
            res[i] = new ArrayList();
        }
    }

    @After
    public void tearDown() {
    }

    @Test
    @SuppressWarnings("static-access")
    public void testTimer() {
        for (int i = 0; i < res.length; i++) {
            TimerTask task = new TimerTask(i);
            timers[i] = new Timer();
            timers[i].setListener(task);
            timers[i].start();
        }

        try {
            Thread.currentThread().sleep(TEST_DURATION * 1000);
        } catch (Exception e) {
            fail("Interrupted");
        }

        for (int i = 0; i < timers.length; i++) {
            timers[i].stop();
        }

        if (count == 0) {
            fail("Didn't run");
        }

        for (int k = 1; k < count - 1; k++) {
            for (int i = 0; i < res.length - 1; i++) {
                try {
                    long t0 = (Long) res[i].get(k);
                    long t1 = (Long) res[i + 1].get(k);

                    long d = Math.abs(t1 - t0);
                    if (d > Quartz.HEART_BEAT) {
                        errorCount++;
                    }
                } catch (NullPointerException e) {
                    errorCount++;
                }
            }
        }

        errorCount = errorCount / N;

        if (errorCount > MAX_ERRORS) {
            fail("Total errors=" + errorCount + ", max allowed=" + MAX_ERRORS);
        }
        System.out.println("Total timer errors: " + errorCount + ", max allowed=" + MAX_ERRORS);
    }

    private class TimerTask implements Runnable {

        private byte[] data = new byte[320];
        private int id;

        public TimerTask(int id) {
            this.id = id;
        }

        public void run() {
            if (id == 0) {
                count++;
            }

            long now = System.currentTimeMillis();
            res[id].add(now);

            for (int i = 0; i < data.length; i++) {
                data[i] = (byte) (1 + data[i] * 2 / 3);
            }
        }
    }
}