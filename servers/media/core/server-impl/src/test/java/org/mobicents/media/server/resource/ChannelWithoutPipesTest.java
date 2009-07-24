/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mobicents.media.server.resource;

import java.util.ArrayList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.mobicents.media.Buffer;
import org.mobicents.media.Format;
import org.mobicents.media.server.EndpointImpl;
import org.mobicents.media.server.impl.clock.TimerImpl;
import org.mobicents.media.server.impl.resource.test.TransmissionTester;
import org.mobicents.media.server.spi.Endpoint;

/**
 *
 * @author kulikov
 */
public class ChannelWithoutPipesTest {

    public final Format FORMAT = new Format("test");
    private Endpoint endpoint;
    private ChannelFactory channelFactory = new ChannelFactory();
    private ArrayList<Buffer> list = new ArrayList();
    private TransmissionTester tester;
    
//    private Semaphore semaphore = new Semaphore(0);

    public ChannelWithoutPipesTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
        TimerImpl timer = new TimerImpl();
        endpoint = new EndpointImpl();
        list.clear();

        channelFactory = new ChannelFactory();
        channelFactory.start();

        ArrayList components = new ArrayList();
        ArrayList pipes = new ArrayList();

        channelFactory.setComponents(components);
        channelFactory.setPipes(pipes);
        
        tester = new TransmissionTester(timer);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testConnect1() throws Exception {
        Channel channel = channelFactory.newInstance(endpoint);

        channel.connect(tester.getDetector());
        channel.connect(tester.getGenerator());
        
        tester.start();
    }

    @Test
    public void testConnect2() throws Exception {
        Channel channel = channelFactory.newInstance(endpoint);

        channel.connect(tester.getGenerator());
        channel.connect(tester.getDetector());
        
        tester.start();
    }


    @Test
    public void testInputFormats() throws Exception {
        Channel channel = channelFactory.newInstance(endpoint);

        Format[] f = channel.getInputFormats();
        assertEquals(0, f.length);
                
        channel.connect(tester.getDetector());
        
        f = channel.getInputFormats();
        assertEquals(1, f.length);
        assertEquals(true, f[0].matches(tester.getDetector().getFormats()[0]));
        
        channel.disconnect(tester.getDetector());

        assertEquals(false, tester.getDetector().isConnected());
        
        f = channel.getInputFormats();
        assertEquals(0, f.length);
    }

    @Test
    public void testOutputFormats() throws Exception {
        Channel channel = channelFactory.newInstance(endpoint);

        Format[] f = channel.getOutputFormats();
        assertEquals(0, f.length);
                
        channel.connect(tester.getGenerator());
        
        f = channel.getOutputFormats();
        assertEquals(1, f.length);
        assertEquals(true, f[0].matches(tester.getGenerator().getFormats()[0]));
        
        channel.disconnect(tester.getGenerator());

        assertEquals(false, tester.getGenerator().isConnected());
        
        f = channel.getOutputFormats();
        assertEquals(0, f.length);
    }
    
    @Test
    public void testChannelConnect1() throws Exception {
        Channel channel1 = channelFactory.newInstance(endpoint);
        Channel channel2 = channelFactory.newInstance(endpoint);

        channel1.connect(tester.getGenerator());
        channel2.connect(tester.getDetector());

        channel1.connect(channel2);

        tester.start();
    }

    @Test
    public void testChannelConnect2() throws Exception {
        Channel channel1 = channelFactory.newInstance(endpoint);
        Channel channel2 = channelFactory.newInstance(endpoint);

        channel1.connect(channel2);
        
        channel1.connect(tester.getGenerator());
        channel2.connect(tester.getDetector());


        tester.start();
    }

    

}