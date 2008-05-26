/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mobicents.media.server.impl.codec.locator.test;

import junit.framework.TestCase;

import org.mobicents.media.server.impl.jmf.dsp.Codec;
import org.mobicents.media.server.impl.jmf.dsp.CodecLocator;

/**
 *
 * @author Oleg Kulikov
 */
public class CodecLocatorTest extends TestCase {
    
    public CodecLocatorTest(String testName) {
        super(testName);
    }            

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test of getCodec method, of class CodecLocator.
     */
    public void testALawEncoder() {
        Codec encoder = new org.mobicents.media.server.impl.jmf.dsp.audio.alaw.Encoder();
        
        Codec codec = CodecLocator.getCodec(Codec.LINEAR_AUDIO, Codec.PCMA);
        if (codec == null) {
            fail("Code not found");
        }
        
        if (!(codec.getClass().getName().equals(encoder.getClass().getName()))) {
            fail("Unexpected codec");
        }
    }

    public void testALawDecoder() {
        Codec decoder = new org.mobicents.media.server.impl.jmf.dsp.audio.alaw.Decoder();
        
        Codec codec = CodecLocator.getCodec(Codec.PCMA, Codec.LINEAR_AUDIO);
        if (codec == null) {
            fail("Code not found");
        }
        
        if (!(codec.getClass().getName().equals(decoder.getClass().getName()))) {
            fail("Unexpected codec");
        }
    }

    public void testULawEncoder() {
        Codec encoder = new org.mobicents.media.server.impl.jmf.dsp.audio.ulaw.Encoder();
        
        Codec codec = CodecLocator.getCodec(Codec.LINEAR_AUDIO, Codec.PCMU);
        if (codec == null) {
            fail("Code not found");
        }
        
        if (!(codec.getClass().getName().equals(encoder.getClass().getName()))) {
            fail("Unexpected codec");
        }
    }

    public void testULawDecoder() {
        Codec decoder = new org.mobicents.media.server.impl.jmf.dsp.audio.ulaw.Decoder();
        
        Codec codec = CodecLocator.getCodec(Codec.PCMU, Codec.LINEAR_AUDIO);
        if (codec == null) {
            fail("Code not found");
        }
        
        if (!(codec.getClass().getName().equals(decoder.getClass().getName()))) {
            fail("Unexpected codec");
        }
    }
    
}
