/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mobicents.media.server.impl.resource;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.mobicents.media.Buffer;
import org.mobicents.media.Format;
import org.mobicents.media.Utils;
import org.mobicents.media.format.AudioFormat;
import org.mobicents.media.server.EndpointImpl;
import org.mobicents.media.server.impl.AbstractSource;
import org.mobicents.media.server.impl.clock.TimerImpl;

/**
 *
 * @author kulikov
 */
public class MultiplexerTest {

    private Multiplexer mux;
    public MultiplexerTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        TimerImpl timer = new TimerImpl();
        EndpointImpl endpoint = new EndpointImpl();
        endpoint.setTimer(timer);
        mux = new Multiplexer("test");
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testOutputFormats() {
        Format[] supported = mux.getFormats();
        assertEquals(0, supported.length);

        mux.connect(new Source1(""));
        
        supported = mux.getOutput().getFormats();
        assertEquals(1, supported.length);
        
        Format[] formats = new Format[] {new AudioFormat("f1")};
        assertEquals(true, Utils.checkFormats(supported,formats ));
        
        mux.connect(new Source2(""));
        supported = mux.getOutput().getFormats();
        assertEquals(2, supported.length);
        formats = new Format[] {new AudioFormat("f1"), new AudioFormat("f2")};
        assertEquals(true, Utils.checkFormats(supported,formats ));
    }
    
    private class Source1 extends AbstractSource {

        private Format f = new AudioFormat("f1");
        
        public Source1(String name) {
            super(name);
        }
        
        public Format[] getFormats() {
            return new Format[]{f};
        }

        public boolean isAcceptable(Format format) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void receive(Buffer buffer) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void start() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void stop() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
        
    }
    
    private class Source2 extends AbstractSource {

        private Format f = new AudioFormat("f2");
        
        public Source2(String name) {
            super(name);
        }
        
        public Format[] getFormats() {
            return new Format[]{f};
        }

        public boolean isAcceptable(Format format) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void receive(Buffer buffer) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void start() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void stop() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
        
    }
    
}