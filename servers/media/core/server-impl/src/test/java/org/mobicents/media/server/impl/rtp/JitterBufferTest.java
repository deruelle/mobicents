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
    private RtpSocket rtpSocket = new RtpSocketImpl(period, jitter);
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
        jitterBuffer = new JitterBuffer(rtpSocket, jitter, period,new AbstractWorkDataGatherer(){});
        int ps = 1000 / Quartz.HEART_BEAT;
        MAX_ERRORS = (int) Math.round(100.0 / ps * ERRORS);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testFirstRead() {
        RtpPacket p1 = new RtpPacket((byte) 8, 1, 0, 1L, "One".getBytes());
        RtpPacket p2 = new RtpPacket((byte) 8, 2, 160, 1L, "Two".getBytes());
        RtpPacket p3 = new RtpPacket((byte) 8, 3, 320, 1L, "Three".getBytes());
        RtpPacket p4 = new RtpPacket((byte) 8, 4, 480, 1L, "Four".getBytes());

        jitterBuffer.write(p1);
        Buffer buffer = jitterBuffer.read();
        if (buffer != null) {
            fail("Buffer is new and not full yet. But it allow to read packets");
        }

        jitterBuffer.write(p2);
        buffer = jitterBuffer.read();
        if (buffer == null) {
            fail("Buffer is new and not full yet. But it allow to read packets");
        }

        jitterBuffer.write(p3);
        buffer = jitterBuffer.read();
        if (buffer == null) {
            fail("Buffer is new and full but it doesn't allow to read packets");
        }

        buffer = jitterBuffer.read();
        if (buffer == null) {
            fail("Buffer is new and not empty but it doesn't allow to read packets");
        }
        jitterBuffer.write(p4);

    }

    @Test
    public void testOverflow() {
        RtpPacket p1 = new RtpPacket((byte) 8, 1, 0, 1L, "One".getBytes());
        RtpPacket p2 = new RtpPacket((byte) 8, 2, 160, 1L, "Two".getBytes());
        RtpPacket p3 = new RtpPacket((byte) 8, 3, 320, 1L, "Three".getBytes());
        RtpPacket p4 = new RtpPacket((byte) 8, 4, 480, 1L, "Four".getBytes());

        jitterBuffer.write(p1);
        jitterBuffer.write(p2);
        jitterBuffer.write(p3);
        jitterBuffer.write(p4);
        jitterBuffer.write(p4);

        Buffer buffer = jitterBuffer.read();
        String s = new String((byte[]) buffer.getData(), buffer.getOffset(), buffer.getLength());

        assertEquals("Two", s);

    }

    @Test
    @SuppressWarnings("static-access")
    public void testReadWrite() {
        jitterBuffer = new JitterBuffer(rtpSocket, jitter, period,new AbstractWorkDataGatherer(){});

        Timer r_timer = new Timer();
        r_timer.setListener(new Receiver());
        r_timer.start();

        Timer s_timer = new Timer();
        s_timer.setListener(new Sender());
        s_timer.start();


        try {
            Thread.currentThread().sleep(TEST_DURATION * 1000);
        } catch (Exception e) {
            fail("Interrupted");
        }
        
        s_timer.stop();
        r_timer.stop();
        
        int k = 0;
        for (int i = 0; i < packets.size(); i++) {
            if (packets.get(i) != null) {
                k = i;
                break;
            }
        }
        
        long exp = 1;
        for (int i = k; i < packets.size(); i++) {
            Buffer buffer = (Buffer) packets.get(i);
            if (exp != buffer.getSequenceNumber()) {
                exp = buffer.getSequenceNumber() + 1;
                errorCount++;
            } else {
                exp++;
            }
        }
        
        if (errorCount > MAX_ERRORS) {
            fail("Too many errors: " + errorCount + ", max=" + MAX_ERRORS);
        }
        System.out.println("Total errors: " + errorCount + ", max=" + MAX_ERRORS);
    }

    public class Sender implements TimerTask {

        private int seq = 0;

        @SuppressWarnings("static-access")
        public void run() {
            RtpPacket p = new RtpPacket((byte) 8, seq, seq, 1L,
                    new Integer(seq).toString().getBytes());
            jitterBuffer.write(p);
            seq++;
        }

        public void started() {
        }

        public void ended() {
        }
    }

    public class Receiver implements TimerTask {
        
        @SuppressWarnings("static-access")
        public void run() {
            Buffer buffer = jitterBuffer.read();
            if (buffer != null) {
                packets.add(buffer);
            }
        }

        public void started() {
        }

        public void ended() {
        }
    }
}
