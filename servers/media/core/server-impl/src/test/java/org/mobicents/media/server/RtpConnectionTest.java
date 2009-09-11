/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mobicents.media.server;

import java.util.HashMap;
import static org.junit.Assert.*;

import java.util.Hashtable;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mobicents.media.Format;
import org.mobicents.media.server.impl.clock.TimerImpl;
import org.mobicents.media.server.impl.resource.test.TesterSinkFactory;
import org.mobicents.media.server.impl.resource.test.TesterSourceFactory;
import org.mobicents.media.server.impl.resource.test.TransmissionTester;
import org.mobicents.media.server.impl.rtp.RtpFactory;
import org.mobicents.media.server.impl.rtp.sdp.AVProfile;
import org.mobicents.media.server.resource.ChannelFactory;
import org.mobicents.media.server.spi.Connection;
import org.mobicents.media.server.spi.ConnectionMode;
import org.mobicents.media.server.spi.Timer;
import org.mobicents.media.server.spi.dsp.Codec;

/**
 *
 * @author kulikov
 */
public class RtpConnectionTest {

    private Timer timer;
    private EndpointImpl sender;
    private EndpointImpl receiver;
    private int localPort1 = 9201;
    private int localPort2 = 9202;
    private ChannelFactory channelFactory;
    private RtpFactory rtpFactory1,  rtpFactory2;

    private TransmissionTester tester;
    private TesterSourceFactory sourceFactory;
    private TesterSinkFactory sinkFactory;
    
    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
        timer = new TimerImpl();
        timer.start();
        
        tester = new TransmissionTester(timer);
        
        sourceFactory = new TesterSourceFactory(tester);
        sinkFactory = new TesterSinkFactory(tester);
        
        HashMap<Integer, Format> rtpmap = new HashMap();
        rtpmap.put(8, AVProfile.PCMU);
        rtpmap.put(0, AVProfile.PCMA);
        rtpmap.put(1, Codec.LINEAR_AUDIO);

        rtpFactory1 = new RtpFactory();
        rtpFactory1.setBindAddress("127.0.0.1");
        rtpFactory1.setLocalPort(localPort1);
        rtpFactory1.setTimer(timer);
        rtpFactory1.setFormatMap(rtpmap);
        rtpFactory1.start();

        rtpFactory2 = new RtpFactory();
        rtpFactory2.setBindAddress("127.0.0.1");
        rtpFactory2.setLocalPort(localPort2);
        rtpFactory2.setTimer(timer);
        rtpFactory2.setFormatMap(rtpmap);
        rtpFactory2.start();


        Hashtable<String, RtpFactory> rtpFactories1 = new Hashtable();
        rtpFactories1.put("audio", rtpFactory1);

        Hashtable<String, RtpFactory> rtpFactories2 = new Hashtable();
        rtpFactories2.put("audio", rtpFactory2);

        channelFactory = new ChannelFactory();
        channelFactory.start();

        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setRxChannelFactory(channelFactory);
        connectionFactory.setTxChannelFactory(channelFactory);
        
        sender = new EndpointImpl("test/announcement/sender");
        sender.setTimer(timer);

        sender.setRtpFactory(rtpFactories1);
        sender.setSourceFactory(sourceFactory);
        sender.setConnectionFactory(connectionFactory);

        sender.start();

        receiver = new EndpointImpl("test/announcement/receiver");
        receiver.setTimer(timer);

        receiver.setRtpFactory(rtpFactories2);
        receiver.setSinkFactory(sinkFactory);
        receiver.setConnectionFactory(connectionFactory);

        receiver.start();
    }

    @After
    public void tearDown() {
        timer.stop();
    }

    /**
     * Test of getLocalName method, of class EndpointImpl.
     */
    @Test
    public void testTransmission() throws Exception {
        Connection rxConnection = receiver.createConnection(ConnectionMode.RECV_ONLY);
        Connection txConnection = sender.createConnection(ConnectionMode.SEND_ONLY);

        txConnection.setRemoteDescriptor(rxConnection.getLocalDescriptor());
        String sdp = txConnection.getLocalDescriptor();
        rxConnection.setRemoteDescriptor(sdp);

        tester.start();
        assertTrue(tester.getMessage(), tester.isPassed());
    }

}