/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mobicents.media.server.impl.clock;

import java.io.IOException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mobicents.media.Buffer;
import org.mobicents.media.Format;
import org.mobicents.media.Inlet;
import org.mobicents.media.MediaSink;
import org.mobicents.media.MediaSource;
import org.mobicents.media.server.impl.AbstractSink;
import org.mobicents.media.server.impl.AbstractSource;
import static org.junit.Assert.*;
import org.mobicents.media.server.spi.Connection;
import org.mobicents.media.server.spi.Endpoint;
import org.mobicents.media.server.spi.NotificationListener;
import org.mobicents.media.server.spi.SyncSource;

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
        TestTask t1 = new TestTask("1");
        TestTask t2 = new TestTask("2");
        
        TestSink s1 = new TestSink("1");
        TestSink s2 = new TestSink("2");
        
        t1.connect(s1);
        t2.connect(s2);
        
        t1.setSyncSource(timer);
        t2.setSyncSource(timer);
        
        timer.start();
        
        timer.sync(t1);
        timer.sync(t2);
        
        Semaphore semaphore = new Semaphore(0);
        semaphore.tryAcquire(5, TimeUnit.SECONDS);
        
        timer.unsync(t1);
        timer.unsync(t2);
        
        int c1 = t1.getCountor();
        int c2 = t2.getCountor();
        
        assertEquals(true, c1 > 0);
        assertEquals(true, c2 > 0);
        
        semaphore.tryAcquire(5, TimeUnit.SECONDS);
        assertEquals(true, (t1.getCountor() - c1) <= 1);
        assertEquals(true, (t2.getCountor() - c2) <= 1);
        
        t1.disconnect(s1);
        t2.disconnect(s2);
        
        timer.stop();
    }


    private class TestTask extends AbstractSource {
        private int countor;
        
        public TestTask(String name) {
            super(name);
        }
        
        public int getCountor() {
            return countor;
        }
        
        @Override
        public void evolve(Buffer buffer, long timestamp, long sequenceNumber) {
            System.out.println("tick");
            countor++;
        }

        public Format[] getFormats() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }
    
    private class TestSink extends AbstractSink {

        public TestSink(String name) {
            super(name);
        }
        
        @Override
        public void onMediaTransfer(Buffer buffer) throws IOException {
        }

        public Format[] getFormats() {
            return new Format[0];
        }

        public boolean isAcceptable(Format format) {
            return true;
        }
        
    }
}