/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mobicents.media.server.impl.resource.echo;

import org.mobicents.media.server.*;
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
public class EchoTest {

    private Timer timer;
    private EndpointImpl transmittor;
    private EndpointImpl echo;
    
    private AudioPlayerFactory playerFactory;
    private TestSinkFactory sinkFactory;
    
    private EchoSinkFactory echoSinkFactory;
    private EchoSourceFactory echoSourceFactory;
    
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
        
        echoSinkFactory = new EchoSinkFactory();
        echoSinkFactory.setName("echo.sink");
        
        echoSourceFactory = new EchoSourceFactory();
        echoSourceFactory.setName("echo.source");
        
        channelFactory = new ChannelFactory();
        channelFactory.start();
        
        transmittor = new EndpointImpl("test/announcement/sender");
        transmittor.setTimer(timer);
        
        transmittor.setSourceFactory(playerFactory);
        transmittor.setSinkFactory(sinkFactory);
        
        transmittor.setTxChannelFactory(channelFactory);
        transmittor.setRxChannelFactory(channelFactory);
        
        transmittor.start();
        
        echo = new EndpointImpl("test/announcement/receiver");
        echo.setTimer(timer);
        
        echo.setSourceFactory(echoSourceFactory);
        echo.setSinkFactory(echoSinkFactory);
        
        echo.setRxChannelFactory(channelFactory);
        echo.setTxChannelFactory(channelFactory);
        
        echo.start();        
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getLocalName method, of class EndpointImpl.
     */
    @Test
    public void testTransmission() throws Exception {
        Connection connection1 = echo.createLocalConnection(ConnectionMode.SEND_RECV);
        Connection connection2 = transmittor.createLocalConnection(ConnectionMode.SEND_RECV);
        
        connection2.setOtherParty(connection1);
        
        Component c = transmittor.getComponent("audio.player");
            AudioPlayer player = (AudioPlayer)c;
        URL url = EchoTest.class.getClassLoader().getResource(
		 "org/mobicents/media/server/impl/addf8-Alaw-GW.wav");
        player.setURL(url.toExternalForm());
        player.addListener(new PlayerListener());
        player.start();

        semaphore.tryAcquire(10, TimeUnit.SECONDS);
        assertEquals(150, count);
        
        assertEquals(true, echo.isInUse());
        assertEquals(true, transmittor.isInUse());
        
        echo.deleteConnection(connection1.getId());
        transmittor.deleteConnection(connection2.getId());
        
        assertEquals(false, echo.isInUse());
        assertEquals(false, transmittor.isInUse());
        
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

        @Override
        public void onMediaTransfer(Buffer buffer) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
        
    }
}