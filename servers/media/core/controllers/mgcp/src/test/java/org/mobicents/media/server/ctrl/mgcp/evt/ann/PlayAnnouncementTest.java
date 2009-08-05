/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mobicents.media.server.ctrl.mgcp.evt.ann;

import jain.protocol.ip.mgcp.message.parms.NotifiedEntity;
import jain.protocol.ip.mgcp.message.parms.RequestIdentifier;
import java.io.IOException;
import java.net.URL;
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
import org.mobicents.media.server.ConnectionFactory;
import org.mobicents.media.server.EndpointImpl;
import org.mobicents.media.server.ctrl.mgcp.MgcpController;
import static org.junit.Assert.*;
import org.mobicents.media.server.ctrl.mgcp.Request;
import org.mobicents.media.server.impl.AbstractSink;
import org.mobicents.media.server.impl.clock.TimerImpl;
import org.mobicents.media.server.impl.resource.audio.AudioPlayerFactory;
import org.mobicents.media.server.impl.rtp.sdp.AVProfile;
import org.mobicents.media.server.resource.ChannelFactory;
import org.mobicents.media.server.spi.Connection;
import org.mobicents.media.server.spi.ConnectionMode;
import org.mobicents.media.server.spi.Endpoint;
import org.mobicents.media.server.spi.Timer;

/**
 *
 * @author kulikov
 */
public class PlayAnnouncementTest {

    private Timer timer;
    private EndpointImpl sender;
    private EndpointImpl receiver;
    
    private AudioPlayerFactory playerFactory;
    private TestSinkFactory sinkFactory;
    
    private ChannelFactory channelFactory;
    
    private Semaphore semaphore;
    private boolean res;
    private int count;
    
    private MgcpController controller;
    
    public PlayAnnouncementTest() {
    }

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
        
                ConnectionFactory connectionFactory = new ConnectionFactory();
                connectionFactory.setRxChannelFactory(channelFactory);
                connectionFactory.setTxChannelFactory(channelFactory);
        
        sender.setSourceFactory(playerFactory);
        sender.setConnectionFactory(connectionFactory);
        
        sender.start();
        
        receiver = new EndpointImpl("test/announcement/receiver");
        receiver.setTimer(timer);
        
        receiver.setSinkFactory(sinkFactory);
        receiver.setConnectionFactory(connectionFactory);
        
        receiver.start();        
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of doVerify method, of class PlayAnnouncement.
     */
    @Test
    public void testSignal() throws Exception {
        
        System.out.println("======1");
        Connection rxConnection = receiver.createLocalConnection(ConnectionMode.RECV_ONLY);
        System.out.println("======2");
        Connection txConnection = sender.createLocalConnection(ConnectionMode.SEND_ONLY);
        
        txConnection.setOtherParty(rxConnection);
        
        URL url = PlayAnnouncementTest.class.getClassLoader().getResource(
		 "addf8-Alaw-GW.wav");
        String s = url.toExternalForm();
        RequestIdentifier id = new RequestIdentifier("1");
        NotifiedEntity ne = new NotifiedEntity("localhost");
        PlayAnnouncementFactory factory = new PlayAnnouncementFactory();
        factory.setResourceName("audio.player");
        PlayAnnouncement signal = (PlayAnnouncement) factory.getInstance(controller, s);
        
        Request request = new Request(controller, id, null, sender, ne);
        
        signal.doVerify(sender);
        signal.start(request);
        
        System.out.println("Started");
        semaphore.tryAcquire(10, TimeUnit.SECONDS);
        assertEquals(150, count);
        
        receiver.deleteConnection(rxConnection.getId());
        sender.deleteConnection(txConnection.getId());
        
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
        public void onMediaTransfer(Buffer buffer) throws IOException {
            throw new UnsupportedOperationException("Not supported yet.");
        }
        
    }
    
}