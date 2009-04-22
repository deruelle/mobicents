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
import org.mobicents.media.Buffer;
import static org.junit.Assert.*;
import org.mobicents.media.Format;
import org.mobicents.media.Utils;
import org.mobicents.media.format.AudioFormat;
import org.mobicents.media.server.impl.AbstractSource;

/**
 *
 * @author kulikov
 */
public class DemultiplexerTest {

    private final static Format f1 = new AudioFormat("f1");
    private final static Format f2 = new AudioFormat("f2");
    
    private final static Format[] formats = new Format[]{f1, f2};
    
    private Demultiplexer demux;
    
    public DemultiplexerTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        demux = new Demultiplexer("test");
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getFormats method, of class Demultiplexer.
     */
    @Test
    public void testInputFormats() {
        Format[] supported = demux.getInput().getFormats();
        assertEquals(0, supported.length);
    }

    @Test
    public void testOutputFormats() {
        Format[] supported = demux.getFormats();
        assertEquals(0, supported.length);
        
        demux.getInput().connect(new Source1(""));
        supported = demux.getFormats();
        
        assertEquals(2, supported.length);
        assertEquals(true, Utils.checkFormats(formats, supported));
    }
    
    /**
     * Test of connect method, of class Demultiplexer.
     */
    @Test
    public void testConnect() {
    }

    /**
     * Test of disconnect method, of class Demultiplexer.
     */
    @Test
    public void testDisconnect() {
    }


    private class Source1 extends AbstractSource {

        public Source1(String name) {
            super(name);
        }
        
        public Format[] getFormats() {
            return new Format[]{f1, f2};
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