/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mobicents.media.server.resource;

import java.util.HashMap;
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
import org.mobicents.media.server.spi.Endpoint;

/**
 *
 * @author kulikov
 */
public class ChannelTest {

    private HashMap<String, MediaSink> sinks = new HashMap();
    private HashMap<String, MediaSource> sources = new HashMap();
    private HashMap<String, Inlet> inlets = new HashMap();
    private HashMap<String, Outlet> outlets = new HashMap();
    
    private Endpoint endpoint;
    private ChannelFactory channelFactory;
    
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

    private class TestSource extends AbstractSource {

        public TestSource(String name) {
            super(name);
        }
        
        public void start() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void stop() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public Format[] getFormats() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
        
    }
    
    private class TestSink extends AbstractSink {

        public TestSink(String name) {
            super(name);
        }
        
        public Format[] getFormats() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public boolean isAcceptable(Format format) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void receive(Buffer buffer) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
        
    }
}