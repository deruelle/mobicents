/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mobicents.media.server.impl.dsp;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.mobicents.media.Buffer;
import org.mobicents.media.Format;
import org.mobicents.media.format.AudioFormat;
import org.mobicents.media.server.impl.AbstractSink;

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
    private final static AudioFormat SPEEX = new AudioFormat(AudioFormat.SPEEX, 8000, 8, 1);
    private final static AudioFormat G729 = new AudioFormat(AudioFormat.G729, 8000, 8, 1);
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
    public void testFormatsNoConnection() {
        Format[] formats = dsp.getInput().getFormats();
        assertEquals(null, formats);
    }

    @Test
    public void testFormatsWithConnection() {
        TestSink s = new TestSink();
        dsp.getOutput().connect(s);

        Format[] formats = dsp.getInput().getFormats();

        assertEquals(6, formats.length);
        
        assertEquals(true, contains(formats, PCMA));
        assertEquals(true, contains(formats, PCMU));
        assertEquals(true, contains(formats, SPEEX));
        assertEquals(true, contains(formats, G729));
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

    private class TestSink extends AbstractSink {

        public Format[] getFormats() {
            return new Format[]{LINEAR, DTMF};
        }

        public boolean isAcceptable(Format format) {
            return format.matches(LINEAR) || format.matches(DTMF);
        }

        public void receive(Buffer buffer) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
}