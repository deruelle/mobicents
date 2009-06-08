/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mobicents.media.server;

import java.net.URL;
import static org.junit.Assert.assertEquals;

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
import org.mobicents.media.server.impl.AbstractSink;
import org.mobicents.media.server.impl.AbstractSource;
import org.mobicents.media.server.impl.clock.TimerImpl;
import org.mobicents.media.server.impl.resource.audio.AudioPlayerEvent;
import org.mobicents.media.server.impl.resource.audio.AudioPlayerFactory;
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
 */
public class LocalConnectionTest {

    private Timer timer;
    private EndpointImpl sender;
    private EndpointImpl receiver;
    
    private AudioPlayerFactory playerFactory;
    private TestSinkFactory sinkFactory;
    
    private ChannelFactory channelFactory;
    
    private Semaphore semaphore;
    private boolean res;
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
        res = false;
        
        timer = new TimerImpl();
        
        playerFactory = new AudioPlayerFactory();
        playerFactory.setName("audio.player");
        
        sinkFactory = new TestSinkFactory();
        
        channelFactory = new ChannelFactory();
        channelFactory.start();
        
        sender = new EndpointImpl("test/announcement/sender");
        sender.setTimer(timer);
        
        sender.setSourceFactory(playerFactory);
        sender.setTxChannelFactory(channelFactory);
        
        sender.start();
        
        receiver = new EndpointImpl("test/announcement/receiver");
        receiver.setTimer(timer);
        
        receiver.setSinkFactory(sinkFactory);
        receiver.setRxChannelFactory(channelFactory);
        
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
        Connection rxConnection = receiver.createLocalConnection(ConnectionMode.RECV_ONLY);
        Connection txConnection = sender.createLocalConnection(ConnectionMode.SEND_ONLY);
        
        txConnection.setOtherParty(rxConnection);
        
        Component c = sender.getComponent("audio.player");
        assertEquals(true, ((AbstractSource)c).isConnected());
        
        AudioPlayer player = (AudioPlayer)c;
        URL url = LocalConnectionTest.class.getClassLoader().getResource(
		 "org/mobicents/media/server/impl/addf8-Alaw-GW.wav");
        player.setURL(url.toExternalForm());
        player.addListener(new PlayerListener());
        player.start();

        semaphore.tryAcquire(10, TimeUnit.SECONDS);
        assertEquals(150, count);
        
        assertEquals(true, receiver.isInUse());
        assertEquals(true, sender.isInUse());
        
        receiver.deleteConnection(rxConnection.getId());
        sender.deleteConnection(txConnection.getId());
        
        assertEquals(false, receiver.isInUse());
        assertEquals(false, sender.isInUse());
        
        assertEquals(false, ((AbstractSource)c).isConnected());
    }

    private class PlayerListener implements NotificationListener {

        public void update(NotifyEvent event) {
            if (event.getEventID() == AudioPlayerEvent.END_OF_MEDIA) {
                semaphore.release();
            }
        }
        
    }
    
    private class TestSinkFactory implements ComponentFactory {

        public Component newInstance(Endpoint endpoint) {
            return new TestSink("test-sink");
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
        
    }
}