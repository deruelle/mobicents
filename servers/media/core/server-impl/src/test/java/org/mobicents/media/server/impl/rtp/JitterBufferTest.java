/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mobicents.media.server.impl.rtp;

import java.util.ArrayList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.mobicents.media.Buffer;
import org.mobicents.media.server.impl.AbstractWorkDataGatherer;
import org.mobicents.media.server.impl.clock.Quartz;
import org.mobicents.media.server.impl.clock.Timer;
import org.mobicents.media.server.impl.clock.TimerTask;

/**
 *
 * @author Oleg Kulikov
 */
public class JitterBufferTest {

    private final static int TEST_DURATION = 20;
    private final static double ERRORS = 5;
    private int MAX_ERRORS;
    
    private int period = 20;
    private int jitter = 40;
    private RtpSocket rtpSocket;// = new RtpSocketImpl(period, jitter);
    private JitterBuffer jitterBuffer;
    private ArrayList packets = new ArrayList();

    private int errorCount;
    
    public JitterBufferTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        jitterBuffer = new JitterBuffer(jitter, period);
        int ps = 1000 / Quartz.HEART_BEAT;
        MAX_ERRORS = (int) Math.round(100.0 / ps * ERRORS);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testFirstRead() {
        Buffer b1 = new Buffer();
        b1.setSequenceNumber(1);
        
        Buffer b2 = new Buffer();
        b2.setSequenceNumber(2);
        
        Buffer b3 = new Buffer();
        b3.setSequenceNumber(3);
        
        Buffer b4 = new Buffer();
        b4.setSequenceNumber(4);

        
        jitterBuffer.write(b1);
        Buffer buffer = jitterBuffer.read();
        if (buffer != null) {
            fail("Buffer is new and not full yet. But it allow to read packets");
        }

        jitterBuffer.write(b2);
        buffer = jitterBuffer.read();
        if (buffer == null) {
            fail("Buffer is new and not full yet. But it allow to read packets");
        }

        jitterBuffer.write(b3);
        buffer = jitterBuffer.read();
        if (buffer == null) {
            fail("Buffer is new and full but it doesn't allow to read packets");
        }

        buffer = jitterBuffer.read();
        if (buffer == null) {
            fail("Buffer is new and not empty but it doesn't allow to read packets");
        }
        jitterBuffer.write(b4);

    }

    @Test
    public void testOverflow() {
        Buffer b1 = new Buffer();
        b1.setData(1);
        
        Buffer b2 = new Buffer();
        b2.setData(2);
        
        Buffer b3 = new Buffer();
        b3.setData(3);
        
        Buffer b4 = new Buffer();
        b4.setData(4);

        jitterBuffer.write(b1);
        jitterBuffer.write(b2);
        jitterBuffer.write(b3);
        jitterBuffer.write(b4);
        jitterBuffer.write(b1);

        Buffer buffer = jitterBuffer.read();
        assertEquals(1, buffer.getData());
        
        buffer = jitterBuffer.read();
        assertEquals(2, buffer.getData());
        
        buffer = jitterBuffer.read();
        assertEquals(3, buffer.getData());
        
        buffer = jitterBuffer.read();
        assertEquals(1, buffer.getData());
        
    }

}
