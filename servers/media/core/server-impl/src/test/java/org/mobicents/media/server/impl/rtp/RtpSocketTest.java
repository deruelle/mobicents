package org.mobicents.media.server.impl.rtp;

import static org.junit.Assert.assertTrue;

import java.net.InetAddress;
import java.util.HashMap;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mobicents.media.Format;
import org.mobicents.media.server.impl.clock.TimerImpl;
import org.mobicents.media.server.impl.resource.test.TransmissionTester;
import org.mobicents.media.server.spi.dsp.Codec;

/**
 * 
 * @author amit bhayani
 * 
 */
public class RtpSocketTest {

    private final static int HEART_BEAT = 20;
    private final static int jitter = 60;
    private RtpFactory rtpFactory1 = null;
    private RtpFactory rtpFactory2 = null;
    private RtpSocket serverSocket;
    private RtpSocket clientSocket;
    private InetAddress localAddress;
    private int localPort1 = 9201;
    private int localPort2 = 9202;
    private TimerImpl timer = null;
    
    private TransmissionTester tester;
    
    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
        timer = new TimerImpl();
        timer.setHeartBeat(HEART_BEAT);
        timer.start();
        tester = new TransmissionTester(timer);
        
        HashMap<Integer, Format> rtpMap = new HashMap();
        rtpMap.put(8, Codec.LINEAR_AUDIO);

        rtpFactory1 = new RtpFactory();
        rtpFactory1.setJitter(jitter);
        rtpFactory1.setBindAddress("localhost");
        rtpFactory1.setLocalPort(localPort1);
        rtpFactory1.setTimer(timer);
        rtpFactory1.setFormatMap(rtpMap);
        rtpFactory1.start();

        rtpFactory2 = new RtpFactory();
        rtpFactory2.setJitter(jitter);
        rtpFactory2.setBindAddress("localhost");
        rtpFactory2.setLocalPort(localPort2);
        rtpFactory2.setTimer(timer);
        rtpFactory2.setFormatMap(rtpMap);
        rtpFactory2.start();

        try {
            localAddress = InetAddress.getByName("localhost");
        } catch (Exception e) {
        }


        serverSocket = rtpFactory1.getRTPSocket();
        clientSocket = rtpFactory2.getRTPSocket();
    }

    @After
    public void tearDown() {
        rtpFactory1.stop();
        rtpFactory2.stop();
        timer.stop();
    }

    @Test
    @SuppressWarnings("static-access")
    public void testTransmission() throws Exception {
        serverSocket.setPeer(localAddress, localPort2);
        clientSocket.setPeer(localAddress, localPort1);

        serverSocket.getReceiveStream().connect(tester.getDetector());
        serverSocket.getReceiveStream().start();

        clientSocket.getSendStream().connect(tester.getGenerator());
        clientSocket.getSendStream().start();
        
        tester.start();
        
        serverSocket.release();
        clientSocket.release();

        
        assertTrue(tester.getMessage(), tester.isPassed());
    }

}
