/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mobicents.media.server.impl.resource.prelay;

import static org.junit.Assert.assertEquals;

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
import org.mobicents.media.server.impl.resource.DemuxFactory;
import org.mobicents.media.server.impl.resource.audio.RecorderFactory;
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

/**
 * 
 * @author kulikov
 */
public class DtmfTransitionTest {

    private Timer timer;
    private Hashtable<String, RtpFactory> rtpFactories1, rtpFactories2;

    private int localPort1 = 9201;
    private int localPort2 = 9202;
    
    private EndpointImpl sender,  receiver;
    private EndpointImpl packetRelayEnp;
    
    private PacketRelaySourceFactory prSourceFactory;
    private PacketRelaySinkFactory prSinkFactory;
    private ChannelFactory prChannelFactory;
    
//    private ChannelFactory senderChannelFactory;
//    private ChannelFactory receiverChannelFactory;
    
    private Semaphore semaphore;

    private DspFactory dspFactory;
    private RtpFactory rtpFactory1, rtpFactory2;
    private EncoderFactory encoderFactory;
    private DecoderFactory decoderFactory;
    private ArrayList list;

    public DtmfTransitionTest() {
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
        timer = new TimerImpl();

        this.setupRTP();
        this.setupDSP();
        
        this.setupPacketRelay();
        
        this.setupSender();
        this.setupReceiver();        
    }

    @After
    public void tearDown() {
    }

    private void setupRTP() throws Exception {
        semaphore = new Semaphore(0);
        
        HashMap<Integer, Format> rtpmap = new HashMap();
        //rtpmap.put(0, AVProfile.PCMA);
        rtpmap.put(97, AVProfile.SPEEX);
        rtpmap.put(101, AVProfile.DTMF);

        rtpFactory1 = new RtpFactory();
        rtpFactory1.setBindAddress("localhost");
        rtpFactory1.setLocalPort(localPort1);
        rtpFactory1.setTimer(timer);
        rtpFactory1.setFormatMap(rtpmap);
        rtpFactory1.start();

        rtpFactory2 = new RtpFactory();
        rtpFactory2.setBindAddress("localhost");
        rtpFactory2.setLocalPort(localPort2);
        rtpFactory2.setTimer(timer);
        rtpFactory2.setFormatMap(rtpmap);
        rtpFactory2.start();
        
        
        rtpFactories1 = new Hashtable();
        rtpFactories1.put("audio", rtpFactory1);

        rtpFactories2 = new Hashtable();
        rtpFactories2.put("audio", rtpFactory2);

    }

    private void setupDSP() throws Exception {
        // preparing g711: ALaw encoder, ULAW decoder
        encoderFactory = new EncoderFactory();
        decoderFactory = new DecoderFactory();

        org.mobicents.media.server.impl.dsp.audio.speex.EncoderFactory spexEncfact = new org.mobicents.media.server.impl.dsp.audio.speex.EncoderFactory();
        org.mobicents.media.server.impl.dsp.audio.speex.DecoderFactory spexDecfact = new org.mobicents.media.server.impl.dsp.audio.speex.DecoderFactory();

        // group codecs into list
        ArrayList codecs = new ArrayList();
        codecs.add(encoderFactory);
        codecs.add(decoderFactory);
        codecs.add(spexEncfact);
        codecs.add(spexDecfact);

        // creating dsp factory with g711 encoder/decoder
        dspFactory = new DspFactory();
        dspFactory.setName("dsp");
        dspFactory.setCodecFactories(codecs);
    }
    
    private void setupSender() throws Exception {
        // configuring sender
        sender = new EndpointImpl("/pr/test/sender");
        sender.setTimer(timer);
        
        // creating transparent channels
        ChannelFactory senderChannelFactory = new ChannelFactory();
        senderChannelFactory.start();
        sender.setTxChannelFactory(senderChannelFactory);

        // creating source
        TestSourceFactory genFactory = new TestSourceFactory();
        genFactory.setName("test-source");

        sender.setSourceFactory(genFactory);
        sender.setRtpFactory(rtpFactories1);
        sender.start();
    }
    
    private void setupReceiver() throws Exception {
        TestSinkFactory detectorFactory = new TestSinkFactory();
        detectorFactory.setName("test-sink");

        DemuxFactory demuxFactory = new DemuxFactory("demux");

        // creating component list
        ArrayList componentsIVR = new ArrayList();
        componentsIVR.add(detectorFactory);
        componentsIVR.add(demuxFactory);
        componentsIVR.add(dspFactory);

        // define pipes

        // Exhaust for Rx channel

        PipeFactory p11 = new PipeFactory();
        p11.setInlet(null);
        p11.setOutlet("dsp");

        PipeFactory p13 = new PipeFactory();
        p13.setInlet("dsp");
        p13.setOutlet("demux");
        
        PipeFactory p12 = new PipeFactory();
        p12.setInlet("demux");
        p12.setOutlet("test-sink");


        PipeFactory p14 = new PipeFactory();
        p14.setInlet("demux");
        p14.setOutlet(null);

        ArrayList pipes = new ArrayList();
        pipes.add(p11);
        pipes.add(p12);
        pipes.add(p13);
        pipes.add(p14);

        ChannelFactory rxChannFact = new ChannelFactory();
        rxChannFact.start();

        rxChannFact.setComponents(componentsIVR);
        rxChannFact.setPipes(pipes);

        // Create RecorderFactory - sink for endpoint
        RecorderFactory recFact = new RecorderFactory();
        recFact.setName("RecorderFactory");

        receiver = new EndpointImpl("/pr/test/receiver");
        receiver.setSinkFactory(recFact);

        receiver.setTimer(timer);
        receiver.setRxChannelFactory(rxChannFact);

        // start IVREndpoint
        receiver.start();
    }
    
    private void setupPacketRelay() throws Exception {
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
        packetRelayEnp.setRtpFactory(rtpFactories2);

        // strating packet relay endpoint
        packetRelayEnp.start();
    }
    /**
     * Test of getSink method, of class Bridge.
     */
    // @Test
    public void testSimpleTransmission() throws Exception {
        Connection txConnection = sender.createLocalConnection(ConnectionMode.SEND_RECV);
        Connection rxConnection = receiver.createLocalConnection(ConnectionMode.SEND_RECV);

        Connection rxC = packetRelayEnp.createLocalConnection(ConnectionMode.RECV_ONLY);
        Connection txC = packetRelayEnp.createLocalConnection(ConnectionMode.SEND_ONLY);

        rxC.setOtherParty(txConnection);
        txC.setOtherParty(rxConnection);

        MediaSource gen1 = (MediaSource) sender.getComponent("test-source");
        gen1.start();

        semaphore.tryAcquire(10, TimeUnit.SECONDS);

        gen1.stop();
        assertEquals(true, !list.isEmpty());

        receiver.deleteAllConnections();
        sender.deleteAllConnections();

        packetRelayEnp.deleteAllConnections();

    }

    private void runRtpTransmission() throws Exception {
        Connection txConnection = sender.createConnection(ConnectionMode.SEND_ONLY);
        Connection rxConnection = receiver.createLocalConnection(ConnectionMode.RECV_ONLY);

        Connection rxC = packetRelayEnp.createConnection(ConnectionMode.RECV_ONLY);
        Connection txC = packetRelayEnp.createLocalConnection(ConnectionMode.SEND_ONLY);


        rxC.setRemoteDescriptor(txConnection.getLocalDescriptor());
        txConnection.setRemoteDescriptor(rxC.getLocalDescriptor());
        
        txC.setOtherParty(rxConnection);

        MediaSource gen1 = (MediaSource) sender.getComponent("test-source");
        gen1.start();

        semaphore.tryAcquire(2, TimeUnit.SECONDS);
        assertEquals(true, !list.isEmpty());

        gen1.stop();

        receiver.deleteAllConnections();
        sender.deleteAllConnections();

        packetRelayEnp.deleteAllConnections();
    }

    @Test
    public void testRtpTransmission() throws Exception {
        for (int i = 0; i < 10; i++) {
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
            // worker = timer.synchronize(this);
            run();
            run();
            run();
            run();
        }

        public void stop() {
            // worker.cancel(true);
        }

        public Format[] getFormats() {
            return new Format[]{AVProfile.DTMF, AVProfile.SPEEX};
        }

        public void run() {
            Buffer buffer = new Buffer();
            buffer.setSequenceNumber(seq++);
            buffer.setDuration(20);
            buffer.setTimeStamp(seq * 20);
            buffer.setFormat(AVProfile.DTMF);
            buffer.setData(new byte[320]);
            buffer.setLength(4);
            buffer.setOffset(0);
            if (otherParty != null) {
                System.out.println("Sending to " + otherParty);
                try {
                    otherParty.receive(buffer);
                } catch (Exception e) {
                }
            }
        }

        @Override
        public void connect(MediaSink otherParty) {
            super.connect(otherParty);
        }

        @Override
        public void disconnect(MediaSink otherParty) {
            super.disconnect(otherParty);
        }

        @Override
        public void evolve(Buffer buffer, long sequenceNumber) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    private class TestSink extends AbstractSink {

        public TestSink(String name) {
            super(name);
        }

        public Format[] getFormats() {
            return new Format[]{AVProfile.DTMF, AVProfile.SPEEX, AVProfile.PCMA};
        }

        public boolean isAcceptable(Format format) {
//			System.out.println("testSing format = "+ format);
            return format.matches(AVProfile.DTMF);
        }

        public void receive(Buffer buffer) {
            list.add(buffer);
        }

        @Override
        public void onMediaTransfer(Buffer buffer) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
}