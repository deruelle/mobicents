package org.mobicents.media.server.impl.rtp;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mobicents.media.Buffer;
import org.mobicents.media.Format;
import org.mobicents.media.format.AudioFormat;
import static org.junit.Assert.*;

/**
 * 
 * @author amit bhayani
 * 
 */
public class JitterBufferTest {

    private AudioFormat PCMA = new AudioFormat(AudioFormat.ALAW, 8000, 8, 1);
    private int period = 20;
    private int jitter = 40;
    private JitterBuffer jitterBuffer;
    private BufferFactory bufferFactory = new BufferFactory(10, "ReceiverBuffer");
    private ArrayList packets = new ArrayList();
    private final long ssrc = System.currentTimeMillis();

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        HashMap<Integer, Format> rtpMap = new HashMap();
        rtpMap.put(8, PCMA);
        jitterBuffer = new JitterBuffer(jitter, period, rtpMap);
    }

    @After
    public void tearDown() {
    }

    private byte[] createBuffer(int seq) {
        RtpHeader header = new RtpHeader();
        header.init(false, (byte) 8, (int) seq, (int) (seq * 160), ssrc);
        byte[] h = header.toByteArray(); 
        
        byte[] res = new byte[172];
        System.arraycopy(h, 0, res, 0, 12);
        return res;
    }

    @Test
    public void testAccuracy() {

        /***********************************************************************
         * Fill Buffer 1
         **********************************************************************/
        jitterBuffer.write(createBuffer(1), 0, 172);
        Buffer buff = jitterBuffer.read();        
        assertEquals("Jitter Buffer not full yet", null, buff);
        
        jitterBuffer.write(createBuffer(2), 0, 172);
        buff = jitterBuffer.read();        
        assertEquals("Jitter Buffer not full yet", null, buff);
        
        jitterBuffer.write(createBuffer(3), 0, 172);
        buff = jitterBuffer.read();        
        assertTrue("Failed to match binary representation.", buff != null);

    }

    @Test
    public void testAsyncWritting() {
        byte[] b1 = createBuffer(1);
        byte[] b2 = createBuffer(2);
        byte[] b3 = createBuffer(3);
        
        write(jitterBuffer, b1);
        Buffer buff = jitterBuffer.read();        
        assertEquals("Jitter Buffer not full yet", null, buff);
        
        write(jitterBuffer, b2);
        buff = jitterBuffer.read();        
        assertEquals("Jitter Buffer not full yet", null, buff);
        
        write(jitterBuffer, b3);
        buff = jitterBuffer.read();        
        assertTrue("Failed to match binary representation.", buff != null);
        
        assertEquals("Seq number problem", 0, buff.getSequenceNumber());
    }
    
    private void write(JitterBuffer jb, byte[] data) {
        int offset = 0;
        int remainder = 172;
        
        while (remainder > 0) {
            int len = Math.min(10, remainder);
            jb.write(data, offset, len);
            remainder -= len;
            offset += len;
        }
    }
}
