package org.mobicents.media.server.impl.rtp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 
 * @author amit bhayani
 * 
 */
public class JitterBufferTest {

    private int period = 20;
    private int jitter = 40;
    private JitterBuffer jitterBuffer;

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        jitterBuffer = new JitterBuffer(jitter, period);
    }

    @After
    public void tearDown() {
    }

    private RtpPacket createBuffer(int seq) {
        return new RtpPacket((byte)0, seq, seq * 160, 1, new byte[160]);
    }

    @Test
    public void testAccuracy() {
        jitterBuffer.write(createBuffer(1));
        RtpPacket packet = jitterBuffer.read();
        assertEquals("Jitter Buffer not full yet", null, packet);

        jitterBuffer.write(createBuffer(2));
        packet = jitterBuffer.read();
        assertEquals("Jitter Buffer not full yet", null, packet);
        
        jitterBuffer.write(createBuffer(3));

        //buffer is filled and ready for output
        packet = jitterBuffer.read();
        check(packet, 1);
        
        packet = jitterBuffer.read();
        check(packet, 2);
        
        packet = jitterBuffer.read();
        check(packet, 3);
        
        //here buffer is empty
        packet = jitterBuffer.read();
        assertEquals("Jitter Buffer not full yet", null, packet);
    }
    
    private void check(RtpPacket packet, int seq) {
        assertTrue("Failed to match binary representation.", packet != null);
        assertTrue("Expected seq = " + seq, packet.getSeqNumber() == seq);
        assertTrue("Expected timestamp=" + (seq*160), packet.getTimestamp() == (seq*160));
    }
}
