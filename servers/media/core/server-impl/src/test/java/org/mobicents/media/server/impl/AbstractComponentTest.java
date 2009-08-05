/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mobicents.media.server.impl;

import java.io.IOException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.mobicents.media.Buffer;
import org.mobicents.media.Format;
import org.mobicents.media.Inlet;
import org.mobicents.media.MediaSink;
import org.mobicents.media.MediaSource;
import org.mobicents.media.Outlet;

/**
 *
 * @author kulikov
 */
public class AbstractComponentTest {

    private TestSource src = new TestSource("source");
    private TestSource2 src2 = new TestSource2("source2");
    private TestSink sink = new TestSink("sink");
    private TestSink2 sink2 = new TestSink2("sink2");

    private Inlet inlet = new InletImpl("test-inlet");
    private Outlet outlet = new OutletImpl("test-outlet");
    
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
    public void testSource2SinkDirectConnectDisconnect() {
        src.connect(sink);
        assertEquals(true, src.isConnected());
        assertEquals(true, sink.isConnected());

        src.disconnect(sink);
        assertEquals(false, src.isConnected());
        assertEquals(false, sink.isConnected());
    }

    @Test
    public void testSource2OneSinkConnectDisconnect() {
        src.connect(sink);
        assertEquals(true, src.isConnected());
        assertEquals(true, sink.isConnected());

        sink.disconnect(src);
        assertEquals(false, src.isConnected());
        assertEquals(false, sink.isConnected());
    }

    @Test
    public void testSourceSet2SinkDirectConnectDisconnect() {
        src2.connect(sink);
        assertEquals(true, sink.isConnected());
        assertEquals(1, src2.getActiveSourceCount());

        src2.disconnect(sink);
        assertEquals(0, src2.getActiveSourceCount());
        assertEquals(false, sink.isConnected());

        sink.connect(src2);
        assertEquals(1, src2.getActiveSourceCount());
        assertEquals(true, sink.isConnected());

        sink.disconnect(src2);
        assertEquals(0, src2.getActiveSourceCount());
        assertEquals(false, sink.isConnected());
    }

    @Test
    public void testSourceSet2SinkCrosswayConnectDisconnect() {
        src2.connect(sink);
        assertEquals(1, src2.getActiveSourceCount());
        assertEquals(true, sink.isConnected());

        sink.disconnect(src2);
        assertEquals(0, src2.getActiveSourceCount());
        assertEquals(false, sink.isConnected());

        sink.connect(src2);
        assertEquals(1, src2.getActiveSourceCount());
        assertEquals(true, sink.isConnected());

        src2.disconnect(sink);
        assertEquals(0, src2.getActiveSourceCount());
        assertEquals(false, sink.isConnected());
    }
    
    @Test
    public void testSource2SinkSetDirectConnectDisconnect2() {
        src.connect(sink2);
        assertEquals(true, src.isConnected());
        assertEquals(1, sink2.getActiveSinkCount());

        src.disconnect(sink2);
        assertEquals(0, sink2.getActiveSinkCount());
        assertEquals(false, sink.isConnected());

        sink2.connect(src);
        assertEquals(true, src.isConnected());
        assertEquals(1, sink2.getActiveSinkCount());

        sink2.disconnect(src);
        assertEquals(false, src.isConnected());
        assertEquals(0, sink2.getActiveSinkCount());
    }

    @Test
    public void testSource2SinkSetCrosswayConnectDisconnect2() {
        src.connect(sink2);
        assertEquals(true, src.isConnected());
        assertEquals(1, sink2.getActiveSinkCount());

        sink2.disconnect(src);
        assertEquals(0, sink2.getActiveSinkCount());
        assertEquals(false, sink.isConnected());

        sink2.connect(src);
        assertEquals(true, src.isConnected());
        assertEquals(1, sink2.getActiveSinkCount());

        src.disconnect(sink2);
        assertEquals(false, src.isConnected());
        assertEquals(0, sink2.getActiveSinkCount());
    }


    @Test
    public void testSource2InletDirectConnectDisconnect2() {
        src.connect(inlet);
        assertEquals(true, src.isConnected());
        assertEquals(true, inlet.getInput().isConnected());

        src.disconnect(inlet);
        assertEquals(false, inlet.getInput().isConnected());
        assertEquals(false, sink.isConnected());

        inlet.connect(src);
        assertEquals(true, src.isConnected());
        assertEquals(true, inlet.getInput().isConnected());

        inlet.disconnect(src);
        assertEquals(false, src.isConnected());
        assertEquals(false, inlet.getInput().isConnected());
    }

    @Test
    public void testSource2InletCrosswayConnectDisconnect2() {
        src.connect(inlet);
        assertEquals(true, src.isConnected());
        assertEquals(true, inlet.getInput().isConnected());

        inlet.disconnect(src);
        assertEquals(false, inlet.getInput().isConnected());
        assertEquals(false, sink.isConnected());

        inlet.connect(src);
        assertEquals(true, src.isConnected());
        assertEquals(true, inlet.getInput().isConnected());

        src.disconnect(inlet);
        assertEquals(false, src.isConnected());
        assertEquals(false, inlet.getInput().isConnected());
    }

    @Test
    public void testSink2OutletDirectConnectDisconnect2() {
        sink.connect(outlet);
        assertEquals(true, sink.isConnected());
        assertEquals(true, outlet.getOutput().isConnected());

        sink.disconnect(outlet);
        assertEquals(false, sink.isConnected());
        assertEquals(false, outlet.getOutput().isConnected());

        outlet.connect(sink);
        assertEquals(true, sink.isConnected());
        assertEquals(true, outlet.getOutput().isConnected());

        outlet.disconnect(sink);
        assertEquals(false, sink.isConnected());
        assertEquals(false, outlet.getOutput().isConnected());
    }

    @Test
    public void testSink2OutletCrosswayConnectDisconnect2() {
        sink.connect(outlet);
        assertEquals(true, sink.isConnected());
        assertEquals(true, outlet.getOutput().isConnected());

        outlet.disconnect(sink);
        assertEquals(false, sink.isConnected());
        assertEquals(false, outlet.getOutput().isConnected());

        outlet.connect(sink);
        assertEquals(true, sink.isConnected());
        assertEquals(true, outlet.getOutput().isConnected());

        sink.disconnect(outlet);
        assertEquals(false, sink.isConnected());
        assertEquals(false, outlet.getOutput().isConnected());
    }
    
    private class TestSource extends AbstractSource {

        public TestSource(String name) {
            super(name);
        }

        public Format[] getFormats() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public String getOtherPartyName() {
            return this.otherParty.getName();
        }

        @Override
        public void evolve(Buffer buffer, long sequenceNumber) {
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

        public String getOtherPartyName() {
            return this.otherParty.getName();
        }

        @Override
        public void onMediaTransfer(Buffer buffer) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    private class TestSource2 extends AbstractSourceSet {

        public TestSource2(String name) {
            super(name);
        }

        private class InnerSource extends AbstractSource {

            public InnerSource(String name) {
                super(name);
            }

            public Format[] getFormats() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            public String getOtherPartyName() {
                return this.otherParty.getName();
            }

            @Override
            public void evolve(Buffer buffer, long sequenceNumber) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        }

        @Override
        public AbstractSource createSource(MediaSink otherParty) {
            return new InnerSource("test");
        }

        public void start() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void stop() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void evolve(Buffer buffer, long sequenceNumber) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public Format[] getFormats() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void destroySource(AbstractSource source) {
        }
    }

    private class TestSink2 extends AbstractSinkSet {

        public TestSink2(String name) {
            super(name);
        }

        private class LocalSink extends AbstractSink {

            public LocalSink(String name) {
                super(name);
            }

            public Format[] getFormats() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            public boolean isAcceptable(Format format) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void onMediaTransfer(Buffer buffer) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        }

        @Override
        public AbstractSink createSink(MediaSource otherParty) {
            return new LocalSink("inner.sink");
        }

        public void start() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void stop() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void onMediaTransfer(Buffer buffer) throws IOException {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public Format[] getFormats() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public boolean isAcceptable(Format format) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void destroySink(AbstractSink sink) {
        }
    }

    private class OutletImpl extends AbstractOutlet {

        private InnerSource source = new InnerSource("inner.source");
        
        public OutletImpl(String name) {
            super(name);
        }

        public void start() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void stop() {
        }

        public MediaSource getOutput() {
            return source;
        }

        private class InnerSource extends AbstractSource {

            public InnerSource(String name) {
                super(name);
            }

            public Format[] getFormats() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            public String getOtherPartyName() {
                return this.otherParty.getName();
            }

            @Override
            public void evolve(Buffer buffer, long sequenceNumber) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        }
    }
    
    private class InletImpl extends AbstractInlet {

        private LocalSink sink = new LocalSink("test");
        
        public InletImpl(String name) {
            super(name);
        }
        
        public void start() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void stop() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public MediaSink getInput() {
            return sink;
        }

        private class LocalSink extends AbstractSink {

            public LocalSink(String name) {
                super(name);
            }

            public Format[] getFormats() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            public boolean isAcceptable(Format format) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void onMediaTransfer(Buffer buffer) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        }
        
    }
}