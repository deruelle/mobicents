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
    private RtpSocket rtpSocket = new RtpSocketImpl();
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
        jitterBuffer = new JitterBuffer( jitter, period);
        int ps = 1000 / Quartz.HEART_BEAT;
        MAX_ERRORS = (int) Math.round(100.0 / ps * ERRORS);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testFirstRead() {
    	Buffer one = new Buffer();
    	one.setData( "One".getBytes());
    	one.setSequenceNumber(1);
    	one.setTimeStamp(0);
    	one.setSequenceNumber(1L);
    	
    	Buffer two = new Buffer();
    	two.setData( "Two".getBytes());
    	two.setSequenceNumber(2);
    	two.setTimeStamp(160);
    	two.setSequenceNumber(1L);
    	
    	Buffer three = new Buffer();
    	three.setData( "Three".getBytes());
    	three.setSequenceNumber(3);
    	three.setTimeStamp(320);
    	three.setSequenceNumber(1L);
    	
    	Buffer four = new Buffer();
    	three.setData( "Four".getBytes());
    	three.setSequenceNumber(4);
    	three.setTimeStamp(480);
    	three.setSequenceNumber(1L);    	
    	

        jitterBuffer.write(one);
        Buffer buffer = jitterBuffer.read();
        if (buffer != null) {
            fail("Buffer is new and not full yet. But it allow to read packets");
        }

        jitterBuffer.write(two);
        buffer = jitterBuffer.read();
        if (buffer == null) {
            fail("Buffer is new and not full yet. But it allow to read packets");
        }

        jitterBuffer.write(three);
        buffer = jitterBuffer.read();
        if (buffer == null) {
            fail("Buffer is new and full but it doesn't allow to read packets");
        }

        buffer = jitterBuffer.read();
        if (buffer == null) {
            fail("Buffer is new and not empty but it doesn't allow to read packets");
        }
        jitterBuffer.write(four);

    }

    @Test
    public void testOverflow() {
    	
    	Buffer one = new Buffer();
    	one.setData( "One".getBytes());
    	one.setSequenceNumber(1);
    	one.setTimeStamp(0);
    	one.setLength("One".getBytes().length);
    	
    	Buffer two = new Buffer();
    	two.setData( "Two".getBytes());
    	two.setSequenceNumber(2);
    	two.setTimeStamp(160);
    	two.setLength("Two".getBytes().length);
    	
    	Buffer three = new Buffer();
    	three.setData( "Three".getBytes());
    	three.setSequenceNumber(3);
    	three.setTimeStamp(320);
    	three.setLength("Three".getBytes().length);
    	
    	Buffer four = new Buffer();
    	three.setData( "Four".getBytes());
    	three.setSequenceNumber(4);
    	three.setTimeStamp(480);
    	three.setLength("Four".getBytes().length);  


        jitterBuffer.write(one);
        jitterBuffer.write(two);
        jitterBuffer.write(three);
        jitterBuffer.write(four);
        jitterBuffer.write(four);

        Buffer buffer = jitterBuffer.read();
        String s = new String((byte[]) buffer.getData(), buffer.getOffset(), buffer.getLength());
        
        System.out.println(buffer.getData());
        
        assertEquals("Two", s);

    }

    @Test
    @SuppressWarnings("static-access")
    public void testReadWrite() {
        jitterBuffer = new JitterBuffer(jitter, period);

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
//            RtpPacket p = new RtpPacket((byte) 8, seq, seq, 1L,
//                    new Integer(seq).toString().getBytes());
            
            
           	Buffer b = new Buffer();
           	byte[] bytes = new Integer(seq).toString().getBytes();
           	
        	b.setData( bytes);
        	b.setSequenceNumber(seq);
        	b.setTimeStamp(seq);
        	b.setLength(bytes.length);            
            
            jitterBuffer.write(b);
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
