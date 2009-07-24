/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mobicents.media.server;


import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

import org.mobicents.media.server.impl.clock.TimerImpl;
import org.mobicents.media.server.impl.resource.test.TesterSinkFactory;
import org.mobicents.media.server.impl.resource.test.TesterSourceFactory;
import org.mobicents.media.server.impl.resource.test.TransmissionTester;
import org.mobicents.media.server.resource.ChannelFactory;
import org.mobicents.media.server.spi.Connection;
import org.mobicents.media.server.spi.ConnectionMode;
import org.mobicents.media.server.spi.Timer;

/**
 *
 * @author kulikov
 */
public class LocalConnectionTest {

    private Timer timer;
    private EndpointImpl sender;
    private EndpointImpl receiver;
    
    private TransmissionTester tester;
    
    private TesterSinkFactory sinkFactory;
    private TesterSourceFactory sourceFactory;
    
    private ChannelFactory channelFactory;
    
    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
        timer = new TimerImpl();
        tester = new TransmissionTester(timer);
        
        sourceFactory = new TesterSourceFactory(tester);
        sinkFactory = new TesterSinkFactory(tester);
        
        channelFactory = new ChannelFactory();
        channelFactory.start();
        
        sender = new EndpointImpl("test/announcement/sender");
        sender.setTimer(timer);
        
        sender.setSourceFactory(sourceFactory);
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
        
        tester.start();
        
        receiver.deleteConnection(rxConnection.getId());
        sender.deleteConnection(txConnection.getId());

        assertTrue(tester.getMessage(), tester.isPassed());
    }


}