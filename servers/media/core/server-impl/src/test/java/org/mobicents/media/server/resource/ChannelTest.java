/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mobicents.media.server.resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.mobicents.media.Buffer;
import org.mobicents.media.Component;
import org.mobicents.media.Format;
import org.mobicents.media.Inlet;
import org.mobicents.media.MediaSink;
import org.mobicents.media.MediaSource;
import org.mobicents.media.Outlet;
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
public class ChannelTest {

    public final Format FORMAT = new Format("test");
    
    private HashMap<String, MediaSink> sinks = new HashMap();
    private HashMap<String, MediaSource> sources = new HashMap();
    private HashMap<String, Inlet> inlets = new HashMap();
    private HashMap<String, Outlet> outlets = new HashMap();
    
    private Endpoint endpoint;
    private ChannelFactory channelFactory;
    private ArrayList<Buffer> list = new ArrayList();
    private Semaphore semaphore = new Semaphore(0);
    private boolean res = false;
    
    public ChannelTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        endpoint = new EndpointImpl();
/*        ArrayList components = new ArrayList();
        
        ChannelFactory channelFactory = new ChannelFactory();
        channelFactory.setComponents(components);
*/        
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testGetComponent() {
        TestSource source = new TestSource("test-source");
        source.setResourceType(1);
        
        TestSink sink = new TestSink("test-sink");
        sink.setResourceType(2);
        
        sources.put(source.getName(), source);
        sinks.put(sink.getName(), sink);
        
        Channel channel = new Channel(sources, sinks, inlets, outlets);

        Component c = channel.getComponent(1);        
        assertEquals(c.getResourceType(), 1);
        
        c = channel.getComponent(2);        
        assertEquals(c.getResourceType(), 2);
    }

    @Test
    public void testDerectTransmission() throws Exception {
        Channel channel = new Channel(sources, sinks, inlets, outlets);
        TestSink sink = new TestSink("test-sink");
        TestSource source = new TestSource("test-source");
        
        channel.connect(sink);
        channel.connect(source);
        
        source.start();
        semaphore.tryAcquire(10, TimeUnit.SECONDS);
        
        assertEquals(true, checkData());
    }
    
    private boolean checkData() {
        boolean result = true;
        long seq = 0;
        for (Buffer buffer : list) {
                result &= (seq == buffer.getSequenceNumber());
            seq++;
            System.out.println("SEQ=" + seq + ", RES=" + result);
        }
        return result;
    }
    
    private class TestSource extends AbstractSource implements Runnable {
                
        private Timer timer = new TimerImpl();
        private ScheduledFuture task;
        private int count;
        
       
        public TestSource(String name) {
            super(name);
        }
        
        public void start() {
            task = timer.synchronize(this);
        }

        public void stop() {
            task.cancel(true);
            semaphore.release();
        }

        public Format[] getFormats() {
            return new Format[] {FORMAT};
        }
        
        public void run() {
            if (count == 10) {
                stop();
            } else {
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
        
    }
}