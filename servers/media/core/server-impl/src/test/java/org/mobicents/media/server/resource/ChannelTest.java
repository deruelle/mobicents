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
import org.mobicents.media.ComponentFactory;
import org.mobicents.media.Format;
import org.mobicents.media.Inlet;
import org.mobicents.media.MediaSink;
import org.mobicents.media.MediaSource;
import org.mobicents.media.Outlet;
import org.mobicents.media.server.EndpointImpl;
import org.mobicents.media.server.impl.AbstractSink;
import org.mobicents.media.server.impl.AbstractSource;
import org.mobicents.media.server.impl.BaseComponent;
import org.mobicents.media.server.impl.clock.TimerImpl;
import org.mobicents.media.server.spi.Connection;
import org.mobicents.media.server.spi.Endpoint;
import org.mobicents.media.server.spi.NotificationListener;
import org.mobicents.media.server.spi.Timer;

/**
 *
 * @author kulikov
 */
public class ChannelTest {

    public final Format FORMAT = new Format("test");
    
    private Endpoint endpoint;
    private ArrayList<Buffer> list = new ArrayList();
    private Semaphore semaphore = new Semaphore(0);
    private boolean res = false;
    private boolean retransmit = false;
    
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
        list.clear();        
    }

    @After
    public void tearDown() {
    }


    @Test
    public void testPipeTransmission() throws Exception {
        TestSinkFactory sinkFactory = new TestSinkFactory("test-sink");
        TestSourceFactory sourceFactory = new TestSourceFactory("test-source");
        TestGatewayFactory gateway = new TestGatewayFactory("test-in-out-let");
        
        ArrayList components = new ArrayList();
        components.add(gateway);

        PipeFactory p1 = new PipeFactory();
        p1.setInlet(null);
        p1.setOutlet("test-in-out-let");

        PipeFactory p2 = new PipeFactory();
        p2.setInlet("test-in-out-let");
        p2.setOutlet(null);
        
        ArrayList pipes = new ArrayList();
        pipes.add(p1);
        pipes.add(p2);
        
        ChannelFactory channelFactory = new ChannelFactory();
        channelFactory.start();
        
        channelFactory.setComponents(components);
        channelFactory.setPipes(pipes);
        
        Channel channel = channelFactory.newInstance(endpoint);

        TestSink sink = (TestSink) sinkFactory.newInstance(endpoint);
        TestSource source = (TestSource) sourceFactory.newInstance(endpoint);
        
        Format[] f = channel.connect(sink);
        assertEquals(1, f.length);
        assertEquals(true, FORMAT.matches(f[0]));
        
        f = channel.connect(source);        
        assertEquals(1, f.length);
        assertEquals(true, FORMAT.matches(f[0]));
        
        source.start();
        semaphore.tryAcquire(10, TimeUnit.SECONDS);
        
        assertEquals(true, checkData());
        assertEquals(true, retransmit);
    }

    @Test
    public void testDirectTransmission() throws Exception {
        TestSink sink = new TestSink("test-sink");
        TestSource source = new TestSource("test-source");
        
        ChannelFactory channelFactory = new ChannelFactory();
        channelFactory.start();
        
        ArrayList components = new ArrayList();
        ArrayList pipes = new ArrayList();
        
        channelFactory.setComponents(components);
        channelFactory.setPipes(pipes);
        
        Channel channel = channelFactory.newInstance(endpoint);
        
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
        }
        return result;
    }
    
    private class TestSourceFactory implements ComponentFactory {
        
        private String name;
        
        public TestSourceFactory(String name) {
            this.name = name;
        }
        
        public Component newInstance(Endpoint endpoint) {
            return new TestSource(name);
        }
        
    }

    private class TestSinkFactory implements ComponentFactory {
        
        private String name;
        
        public TestSinkFactory(String name) {
            this.name = name;
        }
        
        public Component newInstance(Endpoint endpoint) {
            return new TestSink(name);
        }
        
    }

    private class TestGatewayFactory implements ComponentFactory {
        
        private String name;
        
        public TestGatewayFactory(String name) {
            this.name = name;
        }
        
        public Component newInstance(Endpoint endpoint) {
            return new TestGateway(name);
        }
        
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
    
    private class TestGateway extends BaseComponent implements Inlet, Outlet {
    
        private TestInput input;
        private TestOutput output;
        
        private class TestInput extends AbstractSink implements MediaSink {

            public TestInput(String name) {
                super(name);
            }
            
            public Format[] getFormats() {
                return new Format[]{FORMAT};
            }

            public boolean isAcceptable(Format format) {
                return true;
            }

            public void receive(Buffer buffer) {
                output.send(buffer);
                retransmit = true;
            }

            
        }
        
        private class TestOutput extends AbstractSource implements MediaSource {

            public TestOutput(String name) {
                super(name);
            }
            
            public void start() {
            }

            public void stop() {
            }

            public Format[] getFormats() {
                return new Format[]{FORMAT};
            }
            
            public void send(Buffer buffer) {
                this.otherParty.receive(buffer);
            }
        }
        
        public TestGateway(String name) {
            super(name);
                input = new TestInput("input." + name);
                output = new TestOutput("output." + name);
        }
        
        public MediaSink getInput() {
            return input;
        }


        public MediaSource getOutput() {
            return output;
        }
        
    }
}