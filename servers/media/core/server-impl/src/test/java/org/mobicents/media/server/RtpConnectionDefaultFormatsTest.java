/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mobicents.media.server;

import java.net.URL;
import java.util.HashMap;
import static org.junit.Assert.assertEquals;

import java.util.Hashtable;
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
import org.mobicents.media.format.AudioFormat;
import org.mobicents.media.server.impl.AbstractSink;
import org.mobicents.media.server.impl.clock.TimerImpl;
import org.mobicents.media.server.impl.resource.audio.AudioPlayerFactory;
import org.mobicents.media.server.impl.rtp.RtpFactory;
import org.mobicents.media.server.impl.rtp.sdp.AVProfile;
import org.mobicents.media.server.resource.ChannelFactory;
import org.mobicents.media.server.spi.Connection;
import org.mobicents.media.server.spi.ConnectionMode;
import org.mobicents.media.server.spi.Endpoint;
import org.mobicents.media.server.spi.NotificationListener;
import org.mobicents.media.server.spi.Timer;
import org.mobicents.media.server.spi.events.NotifyEvent;
import org.mobicents.media.server.spi.resource.AudioPlayer;

/**
 *
 * @author kulikov
 * @author baranowb
 */
public class RtpConnectionDefaultFormatsTest {

    private Timer timer;
    
    private int localPort1 = 9201;
    private int localPort2 = 9202;
    
    private EndpointImpl sender;
    private EndpointImpl receiver;
    
    private AudioPlayerFactory playerFactory;
    private TestSinkFactory sinkFactory;
    
    private ChannelFactory channelFactory;
    private RtpFactory rtpFactory1, rtpFactory2;
    
    private Semaphore semaphore;
    private int count;
    
    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
        semaphore = new Semaphore(0);
        
        timer = new TimerImpl();
        
        HashMap<Integer, Format> rtpmap = new HashMap();
        rtpmap.put(0, new AudioFormat(AudioFormat.ALAW, 8000, 8, 1));
        rtpmap.put(8,  new AudioFormat(AudioFormat.ULAW, 8000, 8, 1));
        

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
        
        
        Hashtable<String, RtpFactory> rtpFactories1 = new Hashtable();
        rtpFactories1.put("audio", rtpFactory1);

        Hashtable<String, RtpFactory> rtpFactories2 = new Hashtable();
        rtpFactories2.put("audio", rtpFactory2);
        
        playerFactory = new AudioPlayerFactory();
        playerFactory.setName("audio.player");
        
        sinkFactory = new TestSinkFactory();
        
        channelFactory = new ChannelFactory();
        channelFactory.start();
        
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setRxChannelFactory(channelFactory);
        connectionFactory.setTxChannelFactory(channelFactory);
        
        sender = new EndpointImpl("test/announcement/sender");
        sender.setTimer(timer);
        
        sender.setRtpFactory(rtpFactories1);
        sender.setSourceFactory(playerFactory);
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
    }

    /**
     * Test of getLocalName method, of class EndpointImpl.
     */
    @Test
    public void testTransmission() throws Exception {
        Connection rxConnection = receiver.createConnection(ConnectionMode.RECV_ONLY);
        Connection txConnection = sender.createConnection(ConnectionMode.SEND_ONLY);
        
        txConnection.setRemoteDescriptor(rxConnection.getLocalDescriptor());
        rxConnection.setRemoteDescriptor(txConnection.getLocalDescriptor());
        
        Component c = sender.getComponent("audio.player");
            AudioPlayer player = (AudioPlayer)c;
        URL url = RtpConnectionDefaultFormatsTest.class.getClassLoader().getResource(
		 "org/mobicents/media/server/impl/addf8-Alaw-GW.wav");
        player.setURL(url.toExternalForm());
        player.addListener(new PlayerListener());
        player.start();

        semaphore.tryAcquire(10, TimeUnit.SECONDS);
        boolean res = Math.abs(150-count) < 10;
        assertEquals(true, res);
        
        assertEquals(true, receiver.isInUse());
        assertEquals(true, sender.isInUse());
        
        receiver.deleteConnection(rxConnection.getId());
        sender.deleteConnection(txConnection.getId());
        
        assertEquals(false, receiver.isInUse());
        assertEquals(false, sender.isInUse());
    }

    private class PlayerListener implements NotificationListener {

        public void update(NotifyEvent event) {
            if (event.getEventID() == NotifyEvent.COMPLETED) {
                semaphore.release();
            }
        }
        
    }
    
    private class TestSinkFactory implements ComponentFactory {

        public Component newInstance(Endpoint endpoint) {
            return new TestSink("Tester[detector]");
        }
        
    }

    private class TestSink extends AbstractSink {

        public TestSink(String name) {
            super(name);
        }
        
        public Format[] getFormats() {
            return new Format[] {AVProfile.PCMA};
        }

        public boolean isAcceptable(Format format) {
            return true;
        }

        public void receive(Buffer buffer) {
            count++;
        }

        @Override
        public void onMediaTransfer(Buffer buffer) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
        
    }
}