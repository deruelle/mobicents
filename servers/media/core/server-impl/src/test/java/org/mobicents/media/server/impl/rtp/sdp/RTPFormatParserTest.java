/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mobicents.media.server.impl.rtp.sdp;

import java.util.HashMap;
import javax.sdp.SdpException;
import javax.sdp.SdpFactory;
import javax.sdp.SdpParseException;
import javax.sdp.SessionDescription;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mobicents.media.Format;
import static org.junit.Assert.*;

/**
 *
 * @author Oleg Kulikov
 */
public class RTPFormatParserTest {

    private SdpFactory sdpFactory = SdpFactory.getInstance();
    private final String sdp = 
                "v=0\n" +
        "o=MediaServer 5334424 5334424 IN IP4 192.168.1.2\n" +
        "s=session\n" +
        "c=IN IP4 192.168.1.2\n" +
        "t=0 0\n" +
        "m=audio 64535 RTP/AVP 0 8 3 97 18 101\n" +
        "a=rtpmap:0 pcmu/8000\n" +
        "a=rtpmap:8 pcma/8000\n" + 
        "a=rtpmap:3 gsm/8000\n" + 
        "a=rtpmap:97 speex/8000\n" + 
        "a=rtpmap:18 g729/8000\n" + 
        "a=rtpmap:10 L16/44100/2\n" + 
        "a=rtpmap:11 L16/44100\n" + 
        "a=rtpmap:101 telephone-event/8000\n" + 
        "a=fmtp: 101 0-15\n" +
        "a=fmtp: 18 annex=b\n" + 
        "m=video 64537 RTP/AVP 31\n" +
        "a=rtpmap: 31 h261/90000";

    private final String defaultSdp = 
                "v=0\n" +
        "o=MediaServer 5334424 5334424 IN IP4 192.168.1.2\n" +
        "s=session\n" +
        "c=IN IP4 192.168.1.2\n" +
        "t=0 0\n" +
        "m=audio 64535 RTP/AVP 0 8 3 97 10 11 101 18\n" +
        "m=video 64537 RTP/AVP 31";
    
    public RTPFormatParserTest() {
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

    private Format getAudioFormat(String sdp, int pt) throws SdpParseException, SdpException {
        SessionDescription sd = sdpFactory.createSessionDescription(sdp);
        HashMap<Integer, Format> formats  =RTPFormatParser.getFormats(sd, "audio");
        return formats.get(pt);
    }
    private Format getVideoFormat(String sdp, int pt) throws SdpParseException, SdpException {
        SessionDescription sd = sdpFactory.createSessionDescription(sdp);
        HashMap<Integer, Format> formats  =RTPFormatParser.getFormats(sd, "video");
        return formats.get(pt);
    }
    /**
     * Test of getFormats method, of class RTPFormat.
     */
    @Test
    public void testPCMU() throws Exception {
        Format fmt =getAudioFormat(sdp, 0);
        if (!fmt.equals(AVProfile.PCMU)) {
            fail("PCMU expected");
        }
    }

    @Test
    public void testDefaultPCMU() throws Exception {
        Format fmt =getAudioFormat(defaultSdp, 0);
        if (!fmt.equals(AVProfile.PCMU)) {
            fail("PCMU expected");
        }
    }
    
    @Test
    public void testPCMA() throws Exception {
        Format fmt =getAudioFormat(sdp, 8);
        if (!fmt.equals(AVProfile.PCMA)) {
            fail("PCMA expected");
        }
    }

    @Test
    public void testDefaultPCMA() throws Exception {
        Format fmt =getAudioFormat(defaultSdp, 8);
        if (!fmt.equals(AVProfile.PCMA)) {
            fail("PCMA expected");
        }
    }

    @Test
    public void testSpeex() throws Exception {
        Format fmt =getAudioFormat(sdp, 97);
        if (!fmt.equals(AVProfile.SPEEX)) {
            fail("Speex expected");
        }
    }

    @Test
    public void testDefaultSpeex() throws Exception {
        Format fmt =getAudioFormat(defaultSdp, 97);
        if (!fmt.equals(AVProfile.SPEEX)) {
            fail("Speex expected");
        }
    }

    @Test
    public void testGsm() throws Exception {
        Format fmt =getAudioFormat(sdp, 3);
        if (!fmt.equals(AVProfile.GSM)) {
            fail("Gsm expected");
        }
    }

    @Test
    public void testDefaultGsm() throws Exception {
        Format fmt =getAudioFormat(defaultSdp, 3);
        if (!fmt.equals(AVProfile.GSM)) {
            fail("Gsm expected");
        }
    }

    @Test
    public void testG729() throws Exception {
        Format fmt =getAudioFormat(sdp, 18);
        if (!fmt.equals(AVProfile.G729)) {
            fail("G729 expected");
        }
    }
    
    @Test
    public void testDefaultG729() throws Exception {
        Format fmt =getAudioFormat(defaultSdp, 18);
        if (!fmt.equals(AVProfile.G729)) {
            fail("G729 expected");
        }
    }
    
    @Test
    public void testH261() throws Exception {
        Format fmt =getVideoFormat(sdp, 31);
        if (!fmt.equals(AVProfile.H261)) {
            fail("H261 expected");
        }
    }

    @Test
    public void testDtmf() throws Exception {
        Format fmt =getAudioFormat(sdp, 101);
        if (!fmt.equals(AVProfile.DTMF)) {
            fail("DTMF expected, but was " + fmt);
        }
    }

    @Test
    public void testL16Stereo() throws Exception {
        Format fmt =getAudioFormat(sdp, 10);
        if (!fmt.equals(AVProfile.L16_STEREO)) {
            fail("L16 stereo expected, but was " + fmt);
        }
    }

    @Test
    public void testDefaultL16Stereo() throws Exception {
        Format fmt =getAudioFormat(defaultSdp, 10);
        if (!fmt.equals(AVProfile.L16_STEREO)) {
            fail("L16 stereo expected, but was " + fmt);
        }
    }

    @Test
    public void testL16Mono() throws Exception {
        Format fmt =getAudioFormat(sdp, 11);
        if (!fmt.equals(AVProfile.L16_MONO)) {
            fail("L16 stereo expected, but was " + fmt);
        }
    }

    @Test
    public void testDefaultL16Mono() throws Exception {
        Format fmt =getAudioFormat(defaultSdp, 11);
        if (!fmt.equals(AVProfile.L16_MONO)) {
            fail("L16 stereo expected, but was " + fmt);
        }
    }
    
    @Test
    public void testDefaultH261() throws Exception {
        Format fmt =getVideoFormat(defaultSdp, 31);
        if (!fmt.equals(AVProfile.H261)) {
            fail("H261 expected");
        }
    }
    
}