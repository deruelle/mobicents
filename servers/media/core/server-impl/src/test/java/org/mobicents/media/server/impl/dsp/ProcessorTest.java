/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mobicents.media.server.impl.dsp;

import java.io.IOException;
import java.util.ArrayList;
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
import org.mobicents.media.format.AudioFormat;
import org.mobicents.media.server.impl.AbstractSink;
import org.mobicents.media.server.impl.AbstractSource;

/**
 *
 * @author Oleg Kulikov
 */
public class ProcessorTest {

    private final static AudioFormat PCMA = new AudioFormat(AudioFormat.ALAW, 8000, 8, 1);
    private final static AudioFormat PCMU = new AudioFormat(AudioFormat.ULAW, 8000, 8, 1);
    private final static AudioFormat LINEAR = new AudioFormat(AudioFormat.LINEAR, 
            8000, 16, 1, AudioFormat.LITTLE_ENDIAN, AudioFormat.SIGNED);
    private final static AudioFormat DTMF = new AudioFormat("telephone-event/8000");

    private Processor dsp;
    public ProcessorTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        dsp = new Processor();
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testFormatsOnInputWithNoConnection() {
        Format[] formats = dsp.getInput().getFormats();
        assertEquals(null, formats);
    }

    
    @Test
    public void testFormatsOnInputWithConnection() {
        TestSink s = new TestSink();
        
        try {
            dsp.getOutput().connect(s);
        } catch (IOException e) {
            fail(e.getMessage());
        }
        
        Format[] formats = dsp.getInput().getFormats();
        assertEquals(true, contains(formats, PCMA));
        assertEquals(true, contains(formats, PCMU));
        assertEquals(true, contains(formats, LINEAR));
        assertEquals(true, contains(formats, DTMF));
    }
    
    @Test
    public void testFormatsOnOutputWithNoConnection() {
        Format[] formats = dsp.getOutput().getFormats();
        assertEquals(null, formats);
    }

    @Test
    public void testFormatsOnOutputWithConnection() {
        TestSource s = new TestSource();
        
        try {
            dsp.getInput().connect(s);
        } catch (IOException e) {
            fail(e.getMessage());
        }
        
        Format[] formats = dsp.getOutput().getFormats();
        assertEquals(true, contains(formats, PCMA));
        assertEquals(true, contains(formats, PCMU));
        assertEquals(true, contains(formats, LINEAR));
        assertEquals(true, contains(formats, DTMF));
    }
    
    private boolean contains(Format[] fmts, Format fmt) {
        for (int i = 0; i < fmts.length; i++) {
            if (fmts[i].matches(fmt)) {
                return true;
            }
        }
        return false;
    }

    private class TestSource extends AbstractSource {

        public void start() {
        }

        public void stop() {
        }

        public Format[] getFormats() {
            return new Format[] {LINEAR, DTMF};
        }
        
    }
    
    private class TestSink extends AbstractSink {

        public Format[] getFormats() {
            return new Format[] {PCMA, PCMU, DTMF};
        }

        public boolean isAcceptable(Format format) {
            return format.matches(PCMA) || format.matches(PCMU) ||
                    format.matches(DTMF);
        }

        public void receive(Buffer buffer) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
}