/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mobicents.media.server.resource;

import java.util.ArrayList;
import java.util.concurrent.ScheduledFuture;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.mobicents.media.Buffer;
import org.mobicents.media.Format;
import org.mobicents.media.MediaSink;
import org.mobicents.media.MediaSource;
import org.mobicents.media.server.EndpointImpl;
import org.mobicents.media.server.impl.AbstractSink;
import org.mobicents.media.server.impl.AbstractSource;
import org.mobicents.media.server.impl.clock.TimerImpl;
import org.mobicents.media.server.spi.Endpoint;
import org.mobicents.media.server.spi.Timer;

/**
 *
 * @author kulikov
 */
public class ChannelWithoutPipesTest {

    public final Format FORMAT = new Format("test");
    private Endpoint endpoint;
    private TestSink sink = new TestSink("test-sink");
    private TestSource source = new TestSource("test-source");
    private ChannelFactory channelFactory = new ChannelFactory();
    private ArrayList<Buffer> list = new ArrayList();
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
        endpoint = new EndpointImpl();
        list.clear();

        sink = new TestSink("test-sink");
        source = new TestSource("test-source");

        channelFactory = new ChannelFactory();
        channelFactory.start();

        ArrayList components = new ArrayList();
        ArrayList pipes = new ArrayList();

        channelFactory.setComponents(components);
        channelFactory.setPipes(pipes);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testConnect1() throws Exception {
        Channel channel = channelFactory.newInstance(endpoint);

        Format[] f = channel.connect(sink);
        assertEquals(0, f.length);
        
        f = channel.connect(source);
        assertEquals(1, f.length);
        assertEquals(true, f[0].matches(FORMAT));
        
        assertEquals(true, sink.isConnected());
        assertEquals(true, source.isConnected());

        source.start();        
        assertEquals(10, list.size());

        channel.disconnect(sink);
        channel.disconnect(source);

        assertEquals(false, sink.isConnected());
        assertEquals(false, source.isConnected());
    }

    @Test
    public void testConnect2() throws Exception {
        Channel channel = channelFactory.newInstance(endpoint);

        Format[] f = channel.connect(source);
        assertEquals(0, f.length);
        
        f = channel.connect(sink);
        assertEquals(1, f.length);
        assertEquals(true, f[0].matches(FORMAT));

        assertEquals(true, sink.isConnected());
        assertEquals(true, source.isConnected());

        source.start();        
        assertEquals(10, list.size());
        
        channel.disconnect(sink);
        channel.disconnect(source);

        assertEquals(false, sink.isConnected());
        assertEquals(false, source.isConnected());
    }

    @Test
    public void testConnect3() throws Exception {
        Channel channel = channelFactory.newInstance(endpoint);

        Format[] f = channel.connect(source);
        assertEquals(0, f.length);
        
        f = channel.connect(sink);
        assertEquals(1, f.length);
        assertEquals(true, f[0].matches(FORMAT));

        assertEquals(true, sink.isConnected());
        assertEquals(true, source.isConnected());

        source.start();        
        assertEquals(10, list.size());
        
        channel.disconnect(source);
        channel.disconnect(sink);

        assertEquals(false, sink.isConnected());
        assertEquals(false, source.isConnected());
    }

    @Test
    public void testConnect4() throws Exception {
        Channel channel = channelFactory.newInstance(endpoint);

        Format[] f = channel.connect(sink);
        assertEquals(0, f.length);
        
        f = channel.connect(source);
        assertEquals(1, f.length);
        assertEquals(true, f[0].matches(FORMAT));

        assertEquals(true, sink.isConnected());
        assertEquals(true, source.isConnected());

        source.start();        
        assertEquals(10, list.size());
        
        channel.disconnect(source);
        channel.disconnect(sink);

        assertEquals(false, sink.isConnected());
        assertEquals(false, source.isConnected());
    }

    @Test
    public void testInputFormats() throws Exception {
        Channel channel = channelFactory.newInstance(endpoint);

        Format[] f = channel.getInputFormats();
        assertEquals(0, f.length);
                
        channel.connect(sink);
        
        f = channel.getInputFormats();
        assertEquals(1, f.length);
        assertEquals(true, f[0].matches(FORMAT));
        
        channel.disconnect(sink);

        assertEquals(false, sink.isConnected());
        
        f = channel.getInputFormats();
        assertEquals(0, f.length);
    }

    @Test
    public void testOutputFormats() throws Exception {
        Channel channel = channelFactory.newInstance(endpoint);

        Format[] f = channel.getOutputFormats();
        assertEquals(0, f.length);
                
        channel.connect(source);
        
        f = channel.getOutputFormats();
        assertEquals(1, f.length);
        assertEquals(true, f[0].matches(FORMAT));
        
        channel.disconnect(source);

        assertEquals(false, source.isConnected());
        
        f = channel.getOutputFormats();
        assertEquals(0, f.length);
    }
    
    @Test
    public void testChannelConnect1() throws Exception {
        Channel channel1 = channelFactory.newInstance(endpoint);
        Channel channel2 = channelFactory.newInstance(endpoint);

        channel1.connect(source);
        channel2.connect(sink);

        channel1.connect(channel2);

        assertEquals(true, sink.isConnected());
        assertEquals(true, source.isConnected());

        source.start();        
        assertEquals(10, list.size());
        
        
        channel1.disconnect(channel2);
        
        list.clear();
        source.start();        

        //channels are disconected, no transssitions
        assertEquals(true, list.isEmpty());
        
        assertEquals(true, sink.isConnected());
        assertEquals(true, source.isConnected());

        channel1.disconnect(source);
        channel2.disconnect(sink);

        assertEquals(false, sink.isConnected());
        assertEquals(false, source.isConnected());
    }

    @Test
    public void testChannelConnect2() throws Exception {
        Channel channel1 = channelFactory.newInstance(endpoint);
        Channel channel2 = channelFactory.newInstance(endpoint);

        channel1.connect(channel2);

        channel2.connect(sink);
        channel1.connect(source);


        assertEquals(true, sink.isConnected());
        assertEquals(true, source.isConnected());

        source.start();        
        assertEquals(10, list.size());
        
        channel1.disconnect(channel2);
        list.clear();
        source.start();        
        

        assertEquals(true, sink.isConnected());
        assertEquals(true, source.isConnected());

        //channels are disconected, no transssitions
        assertEquals(true, list.isEmpty());
        
        channel1.disconnect(source);
        channel2.disconnect(sink);

        assertEquals(false, sink.isConnected());
        assertEquals(false, source.isConnected());
    }

    @Test
    public void testChannelConnect3() throws Exception {
        Channel channel1 = channelFactory.newInstance(endpoint);
        Channel channel2 = channelFactory.newInstance(endpoint);

        channel1.connect(channel2);

        channel2.connect(sink);
        channel1.connect(source);

        assertEquals(true, sink.isConnected());
        assertEquals(true, source.isConnected());

        source.start();        
        assertEquals(10, list.size());
        
        
        channel1.disconnect(source);
        channel2.disconnect(sink);

        list.clear();
        source.start();        
        
        //channels are disconected, no transssitions
        assertEquals(true, list.isEmpty());
        
        channel1.disconnect(channel2);

        assertEquals(false, sink.isConnected());
        assertEquals(false, source.isConnected());
    }

    @Test
    public void testChannelConnect4() throws Exception {
        Channel channel1 = channelFactory.newInstance(endpoint);
        Channel channel2 = channelFactory.newInstance(endpoint);

        channel1.connect(channel2);

        channel2.connect(sink);
        channel1.connect(source);


        assertEquals(true, sink.isConnected());
        assertEquals(true, source.isConnected());

        source.start();        
        assertEquals(10, list.size());
        
        
        channel1.disconnect(channel2);

        list.clear();
        source.start();        
        
        assertEquals(true, sink.isConnected());
        assertEquals(true, source.isConnected());

        //channels are disconected, no transssitions
        assertEquals(true, list.isEmpty());
        
        channel1.disconnect(source);
        channel2.disconnect(sink);

        assertEquals(false, sink.isConnected());
        assertEquals(false, source.isConnected());
    }

    @Test
    public void testChannelConnect5() throws Exception {
        Channel channel1 = channelFactory.newInstance(endpoint);
        Channel channel2 = channelFactory.newInstance(endpoint);

        channel1.connect(channel2);

        channel2.connect(sink);
        channel1.connect(source);


        assertEquals(true, sink.isConnected());
        assertEquals(true, source.isConnected());

        source.start();        
        assertEquals(10, list.size());
        
        //disconnect rx/tx channel is opposite direction
        channel2.disconnect(channel1);

        list.clear();
        source.start();        
        
        assertEquals(true, sink.isConnected());
        assertEquals(true, source.isConnected());

        //channels are disconected, no transssitions
        assertEquals(true, list.isEmpty());
        
        channel1.disconnect(source);
        channel2.disconnect(sink);

        assertEquals(false, sink.isConnected());
        assertEquals(false, source.isConnected());
    }
    
    private class TestSource extends AbstractSource implements Runnable {

        private Timer timer = new TimerImpl();
        private ScheduledFuture task;
        private int count;

        public TestSource(String name) {
            super(name);
        }

        public void start() {
            for (int i = 0; i < 10; i++) {
                run();
            }
        }

        public void stop() {
            task.cancel(true);
        }

        public MediaSink getOtherParty() {
            return otherParty;
        }

        public Format[] getFormats() {
            return new Format[]{FORMAT};
        }

        public void run() {
            Buffer buffer = new Buffer();
            buffer.setFormat(FORMAT);
            buffer.setTimeStamp(count * timer.getHeartBeat());
            buffer.setDuration(timer.getHeartBeat());
            buffer.setData(Integer.toString(count).getBytes());
            buffer.setSequenceNumber(count++);
            
            if (this.otherParty != null) {
                otherParty.receive(buffer);
            }
        }
    }

    private class TestSink extends AbstractSink {

        public TestSink(String name) {
            super(name);
        }

        public Format[] getFormats() {
            return new Format[]{FORMAT};
        }

        public boolean isAcceptable(Format format) {
            return true;
        }

        public void receive(Buffer buffer) {
            list.add(buffer);
        }

        public MediaSource getOtherParty() {
            return otherParty;
        }
    }

}