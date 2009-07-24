package org.mobicents.media.server.impl.rtp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mobicents.media.Format;
import org.mobicents.media.format.AudioFormat;
import org.mobicents.media.server.impl.clock.TimerImpl;
import org.mobicents.media.server.spi.Timer;

/**
 * 
 * @author amit bhayani
 * 
 */
public class RtpFactoryTest {

    private static final int HEART_BEAT = 20;
    private static final AudioFormat PCMA = new AudioFormat(AudioFormat.ALAW, 8000, 8, 1);
    private static final AudioFormat PCMU = new AudioFormat(AudioFormat.ULAW, 8000, 8, 1);
    private static Map<Integer, Format> formatMap = new HashMap<Integer, Format>();
    

    static {
        formatMap.put(0, PCMU);
        formatMap.put(8, PCMA);
    }
    private RtpFactory factory = null;

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
        factory = new RtpFactory();
        factory.setFormatMap(formatMap);

        Timer timer = new TimerImpl();
        timer.setHeartBeat(HEART_BEAT);

        factory.setTimer(timer);
        factory.setJitter(80);
        factory.setBindAddress("127.0.0.1");
        factory.setLocalPort(9201);
        factory.start();
    }

    @After
    public void tearDown() {
        // Dont close the Factory as it will stop the RtpSocket.readerThread and
        // RtpSocketTest will be screwed
        // factory.stop();
    }

    @Test
    public void getRTPSocketTest() throws Exception {
        RtpSocket rtpSocket = factory.getRTPSocket();
        int port = rtpSocket.getLocalPort();
        assertEquals(9201, port);
        assertEquals(80, rtpSocket.getJitter());

        HashMap<Integer, Format> format = rtpSocket.getRtpMap();

        assertEquals(2, format.size());

        format.remove(0);

        rtpSocket.setRtpMap(format);

        assertEquals(1, rtpSocket.getRtpMap().size());

        rtpSocket.release();

        RtpSocket rtpSocket2 = factory.getRTPSocket();
        assertEquals(2, rtpSocket2.getRtpMap().size());

    }
}
