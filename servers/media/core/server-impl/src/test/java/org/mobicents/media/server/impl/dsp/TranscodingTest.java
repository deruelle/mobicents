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
import org.mobicents.media.server.impl.AbstractSource;

/**
 *
 * @author Oleg Kulikov
 */
public class TranscodingTest {

    private final static AudioFormat PCMA = new AudioFormat(AudioFormat.ALAW, 8000, 8, 1);
    private final static AudioFormat PCMU = new AudioFormat(AudioFormat.ULAW, 8000, 8, 1);
    private final static AudioFormat SPEEX = new AudioFormat(AudioFormat.SPEEX, 8000, 8, 1);
    private final static AudioFormat G729 = new AudioFormat(AudioFormat.G729, 8000, 8, 1);
    private final static AudioFormat LINEAR = new AudioFormat(AudioFormat.LINEAR, 
            8000, 16, 1, AudioFormat.LITTLE_ENDIAN, AudioFormat.SIGNED);
    private final static AudioFormat DTMF = new AudioFormat("telephone-event/8000");

    private Processor dsp;
    
    public TranscodingTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        dsp = new Processor("TranscodingTest");
    }

    @After
    public void tearDown() {
    }

    private void testTranscode(Format[] f1, Format f2[], String codecMap) {
        TestSink r = new TestSink(f2);        
        dsp.getOutput().connect(r);
        
        String map = dsp.showCodecMap();
        System.out.println(map);
        
        String[] resLines = map.split("\n");        
        String[] expLines = codecMap.split("\n");
        
        assertEquals(expLines.length, resLines.length);
        for (int i = 0; i < expLines.length; i++) {
            assertEquals(expLines[i], resLines[i]);
        }
        
    }
    
    @Test
    public void testLinear2PCMA() {
    /*    Format[] send = new Format[]{LINEAR, DTMF};
        Format[] recv = new Format[]{PCMA,DTMF};
        
        String map = LINEAR.toString().toLowerCase() + "-->" + 
                "org.mobicents.media.server.impl.dsp.audio.g711.alaw.Encoder"; 
        testTranscode(send, recv, map);  
     */ 
    }

    //@Test
    public void testLinear() {
        Format[] send = new Format[]{PCMA, DTMF};
        Format[] recv = new Format[]{LINEAR,DTMF};
        
        String map = 
                PCMA.toString().toLowerCase() + "-->" + 
                "org.mobicents.media.server.impl.dsp.audio.g711.alaw.Decoder\n";
        testTranscode(send, recv, map);        
    }

//    @Test
    public void testLinear2PCMU() {
        Format[] send = new Format[]{LINEAR, DTMF};
        Format[] recv = new Format[]{PCMU,DTMF};
        
        String map = LINEAR.toString().toLowerCase() + "-->" + 
                "org.mobicents.media.server.impl.dsp.audio.g711.ulaw.Encoder"; 
        testTranscode(send, recv, map);  
    }

//    @Test
    public void testPCMU2Linear() {
        Format[] send = new Format[]{PCMU, DTMF};
        Format[] recv = new Format[]{LINEAR,DTMF};
        
        String map = PCMU.toString().toLowerCase() + "-->" + 
                "org.mobicents.media.server.impl.dsp.audio.g711.ulaw.Decoder"; 
        testTranscode(send, recv, map);        
    }
    
//    @Test
    public void testLinear2Speex() {
        Format[] send = new Format[]{LINEAR, DTMF};
        Format[] recv = new Format[]{SPEEX,DTMF};
        
        String map = LINEAR.toString().toLowerCase() + "-->" + 
                "org.mobicents.media.server.impl.dsp.audio.speex.Encoder"; 
        testTranscode(send, recv, map);  
    }

//    @Test
    public void testSpeex2Linear() {
        Format[] send = new Format[]{SPEEX, DTMF};
        Format[] recv = new Format[]{LINEAR,DTMF};
        
        String map = SPEEX.toString().toLowerCase() + "-->" + 
                "org.mobicents.media.server.impl.dsp.audio.speex.Decoder"; 
        testTranscode(send, recv, map);        
    }

//    @Test
    public void testLinear2G729() {
        Format[] send = new Format[]{LINEAR, DTMF};
        Format[] recv = new Format[]{G729,DTMF};
        
        String map = LINEAR.toString().toLowerCase() + "-->" + 
                "org.mobicents.media.server.impl.dsp.audio.g729.Encoder"; 
        testTranscode(send, recv, map);  
    }

//    @Test
    public void testG7292Linear() {
        Format[] send = new Format[]{G729, DTMF};
        Format[] recv = new Format[]{LINEAR,DTMF};
        
        String map = G729.toString().toLowerCase() + "-->" + 
                "org.mobicents.media.server.impl.dsp.audio.g729.Decoder"; 
        testTranscode(send, recv, map);        
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

        private Format[] f;
        
        public TestSink(Format[] f) {
        	super("TranscodingTest.TestSink");
            this.f= f;
        }
        
        public Format[] getFormats() {
            return f;
        }

        public boolean isAcceptable(Format format) {
            return contains(f, format);                   
        }

        public void receive(Buffer buffer) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
}
