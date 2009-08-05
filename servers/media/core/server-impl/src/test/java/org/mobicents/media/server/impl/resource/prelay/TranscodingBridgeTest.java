/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mobicents.media.server.impl.resource.prelay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.concurrent.Semaphore;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mobicents.media.Component;
import org.mobicents.media.ComponentFactory;
import org.mobicents.media.Format;
import org.mobicents.media.server.ConnectionFactory;
import org.mobicents.media.server.EndpointImpl;
import org.mobicents.media.server.impl.clock.TimerImpl;
import org.mobicents.media.server.impl.dsp.DspFactory;
import org.mobicents.media.server.impl.dsp.audio.g711.alaw.DecoderFactory;
import org.mobicents.media.server.impl.dsp.audio.g711.alaw.EncoderFactory;
import org.mobicents.media.server.impl.resource.Proxy;
import org.mobicents.media.server.impl.resource.ProxySinkFactory;
import org.mobicents.media.server.impl.resource.ProxySourceFactory;
import org.mobicents.media.server.impl.resource.fft.AnalyzerFactory;
import org.mobicents.media.server.impl.resource.test.SineGeneratorFactory;
import org.mobicents.media.server.impl.resource.test.TransmissionTester2;
import org.mobicents.media.server.impl.rtp.RtpFactory;
import org.mobicents.media.server.impl.rtp.sdp.AVProfile;
import org.mobicents.media.server.resource.ChannelFactory;
import org.mobicents.media.server.resource.PipeFactory;
import org.mobicents.media.server.spi.Connection;
import org.mobicents.media.server.spi.ConnectionMode;
import org.mobicents.media.server.spi.Endpoint;
import org.mobicents.media.server.spi.Timer;
import static org.junit.Assert.*;

/**
 * 
 * @author kulikov
 */
public class TranscodingBridgeTest {

    private int localPort1 = 9201;
    private int localPort2 = 9202;
    
    private final static int FREQ_ERROR = 5;
    private int MAX_ERRORS = 3;
    private final static int[] FREQ = new int[]{50, 250};
    private Timer timer;
    private EndpointImpl testerEndpoint,  echoEndpoint;
    private EndpointImpl packetRelayEnp;
    private SineGeneratorFactory g1,  g2;
    private AnalyzerFactory a1,  a2;
    private ArrayList<double[]> s1,  s2;
    
    private BridgeFactory packetRelayFactory;
    private ChannelFactory prChannelFactory;
    private ChannelFactory channelFactory;
    private Semaphore semaphore;
    private boolean res;
    private DspFactory dspFactory;
    private RtpFactory rtpFactory1, rtpFactory2;
    private EncoderFactory encoderFactory;
    private DecoderFactory decoderFactory;
    private ArrayList list;

    private TransmissionTester2 tester;
    private Proxy proxy;
    
    private ConnectionFactory connectionFactory;
    
    public TranscodingBridgeTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {

        // creating timer
        timer = new TimerImpl();
        tester = new TransmissionTester2(timer);
        
        HashMap<Integer, Format> rtpmap = new HashMap();
        rtpmap.put(0, AVProfile.PCMA);

        rtpFactory1 = new RtpFactory();
        rtpFactory1.setBindAddress("localhost");
        rtpFactory1.setLocalPort(localPort1);
        rtpFactory1.setTimer(timer);
        rtpFactory1.setFormatMap(rtpmap);
        rtpFactory1.start();


        Hashtable<String, RtpFactory> rtpFactories1 = new Hashtable();
        rtpFactories1.put("audio", rtpFactory1);

        // preparing g711: ALaw encoder, decoder
        encoderFactory = new EncoderFactory();
        decoderFactory = new DecoderFactory();

        // group codecs into list
        ArrayList codecs = new ArrayList();
        codecs.add(encoderFactory);
        codecs.add(decoderFactory);

        // creating dsp factory with g711 encoder/decoder
        dspFactory = new DspFactory();
        dspFactory.setName("dsp");
        dspFactory.setCodecFactories(codecs);

        // creating component list
        ArrayList components = new ArrayList();
        components.add(dspFactory);

        // define pipes
        PipeFactory p1 = new PipeFactory();
        p1.setInlet(null);
        p1.setOutlet("dsp");

        PipeFactory p2 = new PipeFactory();
        p2.setInlet("dsp");
        p2.setOutlet(null);

        ArrayList pipes = new ArrayList();
        pipes.add(p1);
        pipes.add(p2);

        // preparing channel factory
        prChannelFactory = new ChannelFactory();
        prChannelFactory.start();

        prChannelFactory.setComponents(components);
        prChannelFactory.setPipes(pipes);

        ConnectionFactory prConnectionFactory = new ConnectionFactory();
        prConnectionFactory.setRxChannelFactory(prChannelFactory);
        prConnectionFactory.setTxChannelFactory(prChannelFactory);
        
        // configuring Packet relay endpoint
        packetRelayFactory = new BridgeFactory();
        packetRelayFactory.setName("Packet-Relay");

        packetRelayEnp = new EndpointImpl("/pr/test/cnf");
        packetRelayEnp.setGroupFactory(packetRelayFactory);

        packetRelayEnp.setTimer(timer);
        packetRelayEnp.setConnectionFactory(prConnectionFactory);
        packetRelayEnp.setRtpFactory(rtpFactories1);

        // strating packet relay endpoint
        packetRelayEnp.start();

        // creating transparent channels
        channelFactory = new ChannelFactory();
        channelFactory.start();

        connectionFactory = new ConnectionFactory();
        connectionFactory.setRxChannelFactory(channelFactory);
        connectionFactory.setTxChannelFactory(channelFactory);
        
        setupTester();
        setupEcho();
    }

    @After
    public void tearDown() {
        rtpFactory1.stop();
        rtpFactory2.stop();
    }

    private void setupTester() throws Exception {
        tester = new TransmissionTester2(timer);

        TestSourceFactory genFactory = new TestSourceFactory(tester);
        TestSinkFactory detFactory = new TestSinkFactory(tester);

        // configuring sender
        testerEndpoint = new EndpointImpl("/pr/test/sender");
        testerEndpoint.setTimer(timer);
        testerEndpoint.setConnectionFactory(connectionFactory);
        testerEndpoint.setSourceFactory(genFactory);
        testerEndpoint.setSinkFactory(detFactory);
        testerEndpoint.start();
    }
    
    private void setupEcho() throws Exception {
        proxy = new Proxy("proxy");
        proxy.setFormat(new Format[]{AVProfile.PCMA});
        
        proxy.getInput().start();
        proxy.getOutput().start();
        
        ProxySourceFactory sourceFactory = new ProxySourceFactory(proxy);
        ProxySinkFactory sinkFactory = new ProxySinkFactory(proxy);
        
        HashMap<Integer, Format> rtpmap = new HashMap();
        rtpmap.put(0, AVProfile.PCMA);
        
        rtpFactory2 = new RtpFactory();
        rtpFactory2.setBindAddress("localhost");
        rtpFactory2.setLocalPort(localPort2);
        rtpFactory2.setTimer(timer);
        rtpFactory2.setFormatMap(rtpmap);
        rtpFactory2.start();

        Hashtable<String, RtpFactory> rtpFactories2 = new Hashtable();
        rtpFactories2.put("audio", rtpFactory2);
        
        echoEndpoint = new EndpointImpl("/pr/test/echo");
        echoEndpoint.setTimer(timer);
        echoEndpoint.setConnectionFactory(connectionFactory);
        echoEndpoint.setSinkFactory(sinkFactory);
        echoEndpoint.setSourceFactory(sourceFactory);
        echoEndpoint.setRtpFactory(rtpFactories2);
        echoEndpoint.start();
    }
    /**
     * Test of getSink method, of class Bridge.
     */
    //@Test
    public void testSimpleTransmission() throws Exception {
        Connection txConnection = testerEndpoint.createLocalConnection(ConnectionMode.SEND_RECV);
        Connection rxConnection = echoEndpoint.createLocalConnection(ConnectionMode.SEND_RECV);

        Connection rxC = packetRelayEnp.createLocalConnection(ConnectionMode.SEND_RECV);
        Connection txC = packetRelayEnp.createLocalConnection(ConnectionMode.SEND_RECV);

        rxC.setOtherParty(txConnection);
        txC.setOtherParty(rxConnection);

        tester.start();
        
        echoEndpoint.deleteAllConnections();
        testerEndpoint.deleteAllConnections();

        packetRelayEnp.deleteAllConnections();
        
        assertTrue(tester.getMessage(), tester.isPassed());
    }

    private void runRtpTransmission() throws Exception {
        Connection txConnection = testerEndpoint.createLocalConnection(ConnectionMode.SEND_RECV);

        Connection rxC = packetRelayEnp.createLocalConnection(ConnectionMode.SEND_RECV);
        rxC.setOtherParty(txConnection);
        
        Connection rxConnection = echoEndpoint.createConnection(ConnectionMode.SEND_RECV);
        Connection txC = packetRelayEnp.createConnection(ConnectionMode.SEND_RECV);

        txC.setRemoteDescriptor(rxConnection.getLocalDescriptor());
        rxConnection.setRemoteDescriptor(txC.getLocalDescriptor());

        tester.start();
        
        echoEndpoint.deleteAllConnections();
        testerEndpoint.deleteAllConnections();

        packetRelayEnp.deleteAllConnections();
        assertTrue(tester.getMessage(), tester.isPassed());        
    }

    @Test
    public void testRtpTransmission() throws Exception {
        for (int i = 0; i < 3; i++) {
            runRtpTransmission();
        }
    }

    private class TestSourceFactory implements ComponentFactory {

        private TransmissionTester2 tester;
        
        public TestSourceFactory(TransmissionTester2 tester) {
            this.tester = tester;
        }
        public String getName() {
            return tester.getGenerator().getName();
        }

        public Component newInstance(Endpoint endpoint) {
            return tester.getGenerator();
        }
    }

    private class TestSinkFactory implements ComponentFactory {

        private TransmissionTester2 tester;
        
        public TestSinkFactory(TransmissionTester2 tester) {
            this.tester = tester;
        }

        public String getName() {
            return tester.getDetector().getName();
        }


        public Component newInstance(Endpoint endpoint) {
            return tester.getDetector();
        }
    }

}