/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mobicents.media.server.impl;

import java.util.HashMap;
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

/**
 *
 * @author kulikov
 */
public class AbstractComponentTest {

   TestSource src = new TestSource("source");
   TestSource2 src2 = new TestSource2("source2");
   TestSink sink = new TestSink("sink");
   TestSink2 sink2 = new TestSink2("sink2");
    
    public AbstractComponentTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of connect method, of class AbstractSink.
     */
    @Test
    public void testOne2OneDirectConnectDisconnect() {
        src.connect(sink);
        assertEquals(true, src.isConnected());
        assertEquals(true, sink.isConnected());
        
        src.disconnect(sink);
        assertEquals(false, src.isConnected());
        assertEquals(false, sink.isConnected());
    }

    @Test
    public void testOne2OneCrosswayConnectDisconnect() {
        src.connect(sink);
        assertEquals(true, src.isConnected());
        assertEquals(true, sink.isConnected());
        
        sink.disconnect(src);
        assertEquals(false, src.isConnected());
        assertEquals(false, sink.isConnected());
    }

    @Test
    public void testOne2ManyDirectConnectDisconnect() {
        src2.connect(sink);
        assertEquals(true, src2.isConnected());
        assertEquals(true, sink.isConnected());
//        assertEquals("source2.local", sink.getOtherPartyName());
//        assertEquals("sink", src2.getOtherPartyName(sink.getId()));
        
        src2.disconnect(sink);
        assertEquals(false, src2.isConnected());
        assertEquals(false, sink.isConnected());
        
        sink.connect(src2);
        assertEquals(true, src2.isConnected());
        assertEquals(true, sink.isConnected());
//        assertEquals("source2.local", sink.getOtherPartyName());
//        assertEquals("sink", src2.getOtherPartyName(sink.getId()));
        
        sink.disconnect(src2);
        assertEquals(false, src2.isConnected());
        assertEquals(false, sink.isConnected());
    }

    @Test
    public void testOne2ManyDirectConnectDisconnect2() {
        src.connect(sink2);
        assertEquals(true, src.isConnected());
        assertEquals(true, sink2.isConnected());
//        assertEquals("source", sink2.getOtherPartyName(src.getId()));
//        assertEquals("sink2.local", src.getOtherPartyName());
        
        src.disconnect(sink2);
        assertEquals(false, src2.isConnected());
        assertEquals(false, sink.isConnected());
        
        sink2.connect(src);
        assertEquals(true, src.isConnected());
        assertEquals(true, sink2.isConnected());
//        assertEquals("source", sink2.getOtherPartyName(src.getId()));
//        assertEquals("sink2.local", src.getOtherPartyName());
        
        sink2.disconnect(src);
        assertEquals(false, src.isConnected());
        assertEquals(false, sink2.isConnected());
    }

    @Test
    public void testOne2ManyCrosswayConnectDisconnect2() {
        src.connect(sink2);
        assertEquals(true, src.isConnected());
        assertEquals(true, sink2.isConnected());
//        assertEquals("source", sink2.getOtherPartyName(src.getId()));
//        assertEquals("sink2.local", src.getOtherPartyName());
        
        sink2.disconnect(src);
        assertEquals(false, src2.isConnected());
        assertEquals(false, sink.isConnected());
        
        sink2.connect(src);
        assertEquals(true, src.isConnected());
        assertEquals(true, sink2.isConnected());
//        assertEquals("source", sink2.getOtherPartyName(src.getId()));
//        assertEquals("sink2.local", src.getOtherPartyName());
        
        src.disconnect(sink2);
        assertEquals(false, src.isConnected());
        assertEquals(false, sink2.isConnected());
    }
    
    @Test
    public void testOne2ManyCrosswayConnectDisconnect() {
        src2.connect(sink);
        assertEquals(true, src2.isConnected());
        assertEquals(true, sink.isConnected());
//        assertEquals("source2.local", sink.getOtherPartyName());
//        assertEquals("sink", src2.getOtherPartyName(sink.getId()));
        
        sink.disconnect(src2);
        assertEquals(false, src2.isConnected());
        assertEquals(false, sink.isConnected());
        
        sink.connect(src2);
        assertEquals(true, src2.isConnected());
        assertEquals(true, sink.isConnected());
//        assertEquals("source2.local", sink.getOtherPartyName());
//        assertEquals("sink", src2.getOtherPartyName(sink.getId()));
        
        src2.disconnect(sink);
        assertEquals(false, src2.isConnected());
        assertEquals(false, sink.isConnected());
    }

    @Test
    public void testMany2ManyDirectConnectDisconnect() {
        src2.connect(sink2);
        assertEquals(true, src2.isConnected());
        assertEquals(true, sink2.isConnected());
//        assertEquals("source2.local", sink2.getOtherPartyName("sink2.local"));
//        assertEquals("sink2.local", src2.getOtherPartyName("source2.local"));
        
        src2.disconnect(sink2);
        assertEquals(false, src2.isConnected());
        assertEquals(false, sink2.isConnected());
        
        sink2.connect(src2);
        assertEquals(true, src2.isConnected());
        assertEquals(true, sink2.isConnected());
        
//        assertEquals("source2.local", sink2.getOtherPartyName(src2.getId()));
//        assertEquals("sink2.local", src2.getOtherPartyName(sink2.getId()));
        
        sink2.disconnect(src2);
        assertEquals(false, src2.isConnected());
        assertEquals(false, sink2.isConnected());
    }

    @Test
    public void testMany2ManyCrosswayConnectDisconnect() {
        src2.connect(sink2);
        assertEquals(true, src2.isConnected());
        assertEquals(true, sink2.isConnected());
//        assertEquals("source2.local", sink2.getOtherPartyName("sink2.local"));
//        assertEquals("sink2.local", src2.getOtherPartyName("source2.local"));
        
        sink2.disconnect(src2);
        assertEquals(false, src2.isConnected());
        assertEquals(false, sink2.isConnected());
        
        sink2.connect(src2);
        assertEquals(true, src2.isConnected());
        assertEquals(true, sink2.isConnected());
        
//        assertEquals("source2.local", sink2.getOtherPartyName(src2.getId()));
//        assertEquals("sink2.local", src2.getOtherPartyName(sink2.getId()));
        
        src2.disconnect(sink2);
        assertEquals(false, src2.isConnected());
        assertEquals(false, sink2.isConnected());
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
        
        public String getOtherPartyName() {
            return this.otherParty.getName();
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
        
        public String getOtherPartyName() {
            return this.otherParty.getName();
        }
    }
    
    private class TestSource2 extends AbstractSource {

        private HashMap<String, InnerSource> locals = new HashMap();
        
        public TestSource2(String name) {
            super(name);
        }
        
        private String getIdentifier() {
            return getId();
        }
        
        @Override
        public boolean isConnected() {
            return !locals.isEmpty();
        }
        
        @Override
        public void connect(MediaSink sink) {
            InnerSource local = new InnerSource(getName() + ".local");
            locals.put(sink.getId(), local);
            local.connect(sink);
        }

        @Override
        public void disconnect(MediaSink sink) {
            InnerSource local = locals.remove(sink.getId());
            local.disconnect(sink);
        }
        
        public String getOtherPartyName(String id) {
            return locals.get(id).getOtherPartyName();
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
        
        private class InnerSource extends AbstractSource {

            public InnerSource(String name) {
                super(name);
            }
            
            @Override
            public String getId() {
                return getIdentifier();
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
            
            public String getOtherPartyName() {
                return this.otherParty.getName();
            }
        }
    }
    
    private class TestSink2 extends AbstractSink  {

        private HashMap<String, LocalSink> locals = new HashMap();
        
        public TestSink2(String name) {
            super(name);
        }
        
        private String getIdentifier() {
            return getId();
        }
        
        @Override
        public void connect(MediaSource source) {
            LocalSink l = new LocalSink(getName() + ".local");
            locals.put(source.getId(), l);
            l.connect(source);
        }

        @Override
        public void disconnect(MediaSource source) {
            LocalSink l = locals.remove(source.getId());
            l.disconnect(source);
        }
        
        @Override
        public boolean isConnected() {
            return !locals.isEmpty();
        }
        
        public String getOtherPartyName(String id) {
            return locals.get(id).getOtherPartyName();
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
        
        private class LocalSink extends AbstractSink {

            public LocalSink(String name) {
                super(name);
            }
            
            @Override
            public String getId() {
                return getIdentifier();
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
            
            public String getOtherPartyName() {
                return this.otherParty.getName();
            }
            
        }
    }
}