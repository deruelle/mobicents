/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mobicents.media.server.impl.clock;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author kulikov
 */
public class TimerImplTest {

    public TimerImplTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getHeartBeat method, of class TimerImpl.
     */
    @Test
    public void testTimer() throws Exception {
        TimerImpl timer = new TimerImpl();
        TestTask t1 = new TestTask();
        TestTask t2 = new TestTask();
        
        ScheduledFuture control1 = timer.synchronize(t1);
        ScheduledFuture control2 = timer.synchronize(t2);
        
        Semaphore semaphore = new Semaphore(0);
        semaphore.tryAcquire(5, TimeUnit.SECONDS);
        
        control1.cancel(true);
        control2.cancel(true);
        
        int c1 = t1.getCountor();
        int c2 = t2.getCountor();
        
        assertEquals(true, c1 > 0);
        assertEquals(true, c2 > 0);
        
        semaphore.tryAcquire(5, TimeUnit.SECONDS);
        assertEquals(true, (t1.getCountor() - c1) <= 1);
        assertEquals(true, (t2.getCountor() - c2) <= 1);
        
    }


    private class TestTask implements Runnable {
        private int countor;
        
        public int getCountor() {
            return countor;
        }
        
        public void run() {
            countor++;
        }
    }
}