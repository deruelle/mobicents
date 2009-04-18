/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mobicents.media.server.impl.rtp.sdp;

import java.util.Collection;
import java.util.Iterator;
import javax.sdp.Attribute;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author kulikov
 */
public class RTPAudioFormatTest {

    public RTPAudioFormatTest() {
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

    private Attribute getAttribute(Collection<Attribute> attributes, int idx) {
        Iterator<Attribute> it = attributes.iterator();
        for (int i = 0; i < idx; i++) {
            it.next();
        }
        return it.next();
    }
    /**
     * Test of encode method, of class RTPAudioFormat.
     */
    @Test
    public void testEncodePCMA() throws Exception {
        Collection<Attribute> attributes = AVProfile.PCMA.encode();
        assertEquals(1, attributes.size());
        Attribute rtpmap = getAttribute(attributes, 0);
        assertEquals("rtpmap",rtpmap.getName());
        assertEquals("8 pcma/8000",rtpmap.getValue());
    }

    @Test
    public void testEncodePCMU() throws Exception {
        Collection<Attribute> attributes = AVProfile.PCMU.encode();
        assertEquals(1, attributes.size());
        Attribute rtpmap = getAttribute(attributes, 0);
        assertEquals("rtpmap",rtpmap.getName());
        assertEquals("0 pcmu/8000",rtpmap.getValue());
    }

    @Test
    public void testEncodeGSM() throws Exception {
        Collection<Attribute> attributes = AVProfile.GSM.encode();
        assertEquals(1, attributes.size());
        Attribute rtpmap = getAttribute(attributes, 0);
        assertEquals("rtpmap",rtpmap.getName());
        assertEquals("3 gsm/8000",rtpmap.getValue());
    }
    
    @Test
    public void testEncodeSpeex() throws Exception {
        Collection<Attribute> attributes = AVProfile.SPEEX.encode();
        assertEquals(1, attributes.size());
        Attribute rtpmap = getAttribute(attributes, 0);
        assertEquals("rtpmap",rtpmap.getName());
        assertEquals("97 speex/8000",rtpmap.getValue());
    }
    
    @Test
    public void testEncodeL16Stereo() throws Exception {
        Collection<Attribute> attributes = AVProfile.L16_STEREO.encode();
        assertEquals(1, attributes.size());
        Attribute rtpmap = getAttribute(attributes, 0);
        assertEquals("rtpmap",rtpmap.getName());
        assertEquals("10 l16/44100/2",rtpmap.getValue());
    }

    @Test
    public void testEncodeL16Mono() throws Exception {
        Collection<Attribute> attributes = AVProfile.L16_MONO.encode();
        assertEquals(1, attributes.size());
        Attribute rtpmap = getAttribute(attributes, 0);
        assertEquals("rtpmap",rtpmap.getName());
        assertEquals("11 l16/44100",rtpmap.getValue());
    }
    
    @Test
    public void testEncodeG729() throws Exception {
        Collection<Attribute> attributes = AVProfile.G729.encode();
        assertEquals(2, attributes.size());
        Attribute rtpmap = getAttribute(attributes, 0);
        assertEquals("rtpmap",rtpmap.getName());
        assertEquals("18 g729/8000",rtpmap.getValue());
        
        Attribute fmtp = getAttribute(attributes, 1);
        assertEquals("fmtp",fmtp.getName());
        assertEquals("18 annex=b",fmtp.getValue());
    }

    @Test
    public void testEncodeDTMF() throws Exception {
        Collection<Attribute> attributes = AVProfile.DTMF.encode();
        assertEquals(2, attributes.size());
        Attribute rtpmap = getAttribute(attributes, 0);
        assertEquals("rtpmap",rtpmap.getName());
        assertEquals("101 telephone-event/8000",rtpmap.getValue());
        
        Attribute fmtp = getAttribute(attributes, 1);
        assertEquals("fmtp",fmtp.getName());
        assertEquals("101 0-15",fmtp.getValue());
    }
    
}