/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mobicents.media.server.impl.resource.prelay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mobicents.media.Buffer;
import org.mobicents.media.Component;
import org.mobicents.media.ComponentFactory;
import org.mobicents.media.Format;
import org.mobicents.media.MediaSink;
import org.mobicents.media.MediaSource;
import org.mobicents.media.server.EndpointImpl;
import org.mobicents.media.server.impl.AbstractSink;
import org.mobicents.media.server.impl.AbstractSource;
import org.mobicents.media.server.impl.clock.TimerImpl;
import org.mobicents.media.server.impl.dsp.DspFactory;
import org.mobicents.media.server.impl.dsp.audio.g711.alaw.DecoderFactory;
import org.mobicents.media.server.impl.dsp.audio.g711.alaw.EncoderFactory;
import org.mobicents.media.server.impl.resource.fft.AnalyzerFactory;
import org.mobicents.media.server.impl.resource.test.SineGeneratorFactory;
import org.mobicents.media.server.impl.rtp.RtpFactory;
import org.mobicents.media.server.impl.rtp.sdp.AVProfile;
import org.mobicents.media.server.resource.ChannelFactory;
import org.mobicents.media.server.resource.PipeFactory;
import org.mobicents.media.server.spi.Connection;
import org.mobicents.media.server.spi.ConnectionMode;
import org.mobicents.media.server.spi.Endpoint;
import org.mobicents.media.server.spi.Timer;
import org.mobicents.media.server.spi.dsp.Codec;
import static org.junit.Assert.*;

/**
 * 
 * @author kulikov
 */
public class TranscodingBridgeTest {

    private final static int FREQ_ERROR = 5;
    private int MAX_ERRORS = 3;
    private final static int[] FREQ = new int[]{50, 250};
    private Timer timer;
    private EndpointImpl sender,  receiver;
    private EndpointImpl packetRelayEnp;
    private SineGeneratorFactory g1,  g2;
    private AnalyzerFactory a1,  a2;
    private ArrayList<double[]> s1,  s2;
    private PacketRelaySourceFactory prSourceFactory;
    private PacketRelaySinkFactory prSinkFactory;
    private ChannelFactory prChannelFactory;
    private ChannelFactory channelFactory;
    private Semaphore semaphore;
    private boolean res;
    private DspFactory dspFactory;
    private RtpFactory rtpFactory;
    private EncoderFactory encoderFactory;
    private DecoderFactory decoderFactory;
    private ArrayList list;

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
        list = new ArrayList();

        semaphore = new Semaphore(0);
        res = false;

        // creating timer
        timer = new TimerImpl();

        HashMap<Integer, Format> rtpmap = new HashMap();
        rtpmap.put(0, AVProfile.PCMA);

        rtpFactory = new RtpFactory();
        rtpFactory.setBindAddress("localhost");
        rtpFactory.setPortRange("1024-65535");
        rtpFactory.setJitter(60);
        rtpFactory.setTimer(timer);
        rtpFactory.setFormatMap(rtpmap);

        Hashtable<String, RtpFactory> rtpFactories = new Hashtable();
        rtpFactories.put("audio", rtpFactory);

        // preparing g711: ALaw encoder, ULAW decoder
        encoderFactory = new EncoderFactory();
        decoderFactory = new DecoderFactory();

        // group codecs into list
        ArrayList list = new ArrayList();
        list.add(encoderFactory);
        list.add(decoderFactory);

        // creating dsp factory with g711 encoder/decoder
        dspFactory = new DspFactory();
        dspFactory.setName("dsp");
        dspFactory.setCodecFactories(list);

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

        // configuring Packet relay endpoint
        prSourceFactory = new PacketRelaySourceFactory();
        prSourceFactory.setName("pr-source");

        prSinkFactory = new PacketRelaySinkFactory();
        prSinkFactory.setName("pr-sink");

        packetRelayEnp = new EndpointImpl("/pr/test/cnf");
        packetRelayEnp.setSourceFactory(prSourceFactory);
        packetRelayEnp.setSinkFactory(prSinkFactory);

        packetRelayEnp.setTimer(timer);
        packetRelayEnp.setTxChannelFactory(prChannelFactory);
        packetRelayEnp.setRxChannelFactory(prChannelFactory);
        packetRelayEnp.setRtpFactory(rtpFactories);

        // strating packet relay endpoint
        packetRelayEnp.start();

        // creating transparent channels
        channelFactory = new ChannelFactory();
        channelFactory.start();

        // creating source
        TestSourceFactory genFactory = new TestSourceFactory();
        genFactory.setName("test-source");

        // configuring sender
        sender = new EndpointImpl("/pr/test/sender");
        sender.setTimer(timer);
        sender.setTxChannelFactory(channelFactory);
        sender.setRxChannelFactory(channelFactory);
        sender.setSourceFactory(genFactory);
        sender.start();

        TestSinkFactory detFactory = new TestSinkFactory();
        detFactory.setName("test-sink");
        // configuring receiver
        receiver = new EndpointImpl("/pr/test/sender");
        receiver.setTimer(timer);
        receiver.setTxChannelFactory(channelFactory);
        receiver.setRxChannelFactory(channelFactory);
        receiver.setSinkFactory(detFactory);
        receiver.setRtpFactory(rtpFactories);
        receiver.start();
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getSink method, of class Bridge.
     */
//	@Test
    public void testSimpleTransmission() throws Exception {
        Connection txConnection = sender.createLocalConnection(ConnectionMode.SEND_RECV);
        Connection rxConnection = receiver.createLocalConnection(ConnectionMode.SEND_RECV);

        Connection rxC = packetRelayEnp.createLocalConnection(ConnectionMode.RECV_ONLY);
        Connection txC = packetRelayEnp.createLocalConnection(ConnectionMode.SEND_ONLY);

        rxC.setOtherParty(txConnection);
        txC.setOtherParty(rxConnection);

        MediaSource gen1 = (MediaSource) sender.getComponent("test-source");
        gen1.start();

        semaphore.tryAcquire(5, TimeUnit.SECONDS);
        gen1.stop();
        semaphore.tryAcquire(5, TimeUnit.SECONDS);
        
        assertEquals(true, !list.isEmpty());

        receiver.deleteAllConnections();
        sender.deleteAllConnections();

        packetRelayEnp.deleteAllConnections();

    }

    private void runRtpTransmission() throws Exception {
        Connection txConnection = sender.createLocalConnection(ConnectionMode.SEND_ONLY);
        Connection rxConnection = receiver.createConnection(ConnectionMode.RECV_ONLY);

        Connection rxC = packetRelayEnp.createLocalConnection(ConnectionMode.RECV_ONLY);
        Connection txC = packetRelayEnp.createConnection(ConnectionMode.SEND_ONLY);

        rxC.setOtherParty(txConnection);
        txC.setRemoteDescriptor(rxConnection.getLocalDescriptor());
        rxConnection.setRemoteDescriptor(txC.getLocalDescriptor());

        MediaSource gen1 = (MediaSource) sender.getComponent("test-source");
        gen1.start();

        semaphore.tryAcquire(5, TimeUnit.SECONDS);
        assertEquals(true, !list.isEmpty());

        gen1.stop();

        list.clear();
        
        semaphore.tryAcquire(5, TimeUnit.SECONDS);
        assertEquals(true, !list.isEmpty());
        
        receiver.deleteAllConnections();
        sender.deleteAllConnections();

        packetRelayEnp.deleteAllConnections();
    }

    @Test
    public void testRtpTransmission() throws Exception {
        for (int i = 0; i < 3; i++) {
            list.clear();
            runRtpTransmission();
            assertEquals(false, list.isEmpty());
            System.out.println("Pass...." + i);
        }
    }

    private class TestSourceFactory implements ComponentFactory {

        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Component newInstance(Endpoint endpoint) {
            return new TestSource(name, endpoint.getTimer());
        }
    }

    private class TestSinkFactory implements ComponentFactory {

        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Component newInstance(Endpoint endpoint) {
            return new TestSink(name);
        }
    }

    private class TestSource extends AbstractSource implements Runnable {

        private int seq;
        private Timer timer;
        private ScheduledFuture worker;

        public TestSource(String name, Timer timer) {
            super(name);
            this.timer = timer;
        }

        public void start() {
            worker = timer.synchronize(this);
        }

        public void stop() {
            worker.cancel(true);
        }

        public Format[] getFormats() {
            return new Format[]{Codec.LINEAR_AUDIO};
        }

        public void run() {
            Buffer buffer = new Buffer();
            buffer.setSequenceNumber(seq++);
            buffer.setDuration(20);
            buffer.setTimeStamp(seq * 20);
            buffer.setFormat(Codec.LINEAR_AUDIO);
            buffer.setData(new byte[320]);
            buffer.setLength(320);
            buffer.setOffset(0);
            otherParty.receive(buffer);
        }

        @Override
        public void connect(MediaSink otherParty) {
            super.connect(otherParty);
        }

        @Override
        public void disconnect(MediaSink otherParty) {
            super.disconnect(otherParty);
        }
    }

    private class TestSink extends AbstractSink {

        public TestSink(String name) {
            super(name);
        }

        public Format[] getFormats() {
            return new Format[]{Codec.PCMA};
        }

        public boolean isAcceptable(Format format) {
            return format.matches(Codec.PCMA);
        }

        public void receive(Buffer buffer) {
            list.add(buffer);
        }
    }
}