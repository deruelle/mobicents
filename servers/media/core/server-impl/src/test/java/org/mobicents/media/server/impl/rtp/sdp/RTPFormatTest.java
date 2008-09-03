/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mobicents.media.server.impl.rtp.sdp;

import java.util.HashMap;
import javax.sdp.SdpFactory;
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
public class RTPFormatTest {

    private SdpFactory sdpFactory = SdpFactory.getInstance();
    
    public RTPFormatTest() {
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
     * Test of getFormats method, of class RTPFormat.
     */
    @Test
    public void testPCMU() throws Exception {
        RTPAudioFormat pcmu = AVProfile.PCMU;
        
        String sdp = 
                "v=0\n" +
        "o=MediaServer 5334424 5334424 IN IP4 192.168.1.2\n" +
        "s=session\n" +
        "c=IN IP4 192.168.1.2\n" +
        "t=0 0\n" +
        "m=audio 64535 RTP/AVP 0 8\n" +
        "a=rtpmap:0 pcmu/8000\n" +
        "a=rtpmap:8 pcma/8000";

        SessionDescription sd = sdpFactory.createSessionDescription(sdp);
        HashMap result = RTPFormat.getFormats(sd);
        
        System.out.println("RES=" + result);
        
        Format fmt = (Format) result.get(0);
        if (!fmt.matches(pcmu)) {
            fail("PCMU expected");
        }
    }

    @Test
    public void testDefaultPCMU() throws Exception {
        RTPAudioFormat pcmu = AVProfile.PCMU;
        
        String sdp = 
                "v=0\n" +
        "o=MediaServer 5334424 5334424 IN IP4 192.168.1.2\n" +
        "s=session\n" +
        "c=IN IP4 192.168.1.2\n" +
        "t=0 0\n" +
        "m=audio 64535 RTP/AVP 0 8\n" +
        "a=rtpmap:8 pcma/8000";

        SessionDescription sd = sdpFactory.createSessionDescription(sdp);
        HashMap result = RTPFormat.getFormats(sd);
        
        System.out.println("RES=" + result);
        
        Format fmt = (Format) result.get(0);
        if (!fmt.matches(pcmu)) {
            fail("PCMU expected");
        }
    }
    
    @Test
    public void testPCMA() throws Exception {
        RTPAudioFormat pcma = AVProfile.PCMA;
        
        String sdp = 
                "v=0\n" +
        "o=MediaServer 5334424 5334424 IN IP4 192.168.1.2\n" +
        "s=session\n" +
        "c=IN IP4 192.168.1.2\n" +
        "t=0 0\n" +
        "m=audio 64535 RTP/AVP 0 8\n" +
        "a=rtpmap:0 pcmu/8000\n" +
        "a=rtpmap:8 pcma/8000";

        SessionDescription sd = sdpFactory.createSessionDescription(sdp);
        HashMap result = RTPFormat.getFormats(sd);
        
        Format fmt = (Format) result.get(8);
        if (!fmt.matches(pcma)) {
            fail("PCMA expected");
        }
    }

    @Test
    public void testDefaultPCMA() throws Exception {
        RTPAudioFormat pcmu = AVProfile.PCMU;
        
        String sdp = 
                "v=0\n" +
        "o=MediaServer 5334424 5334424 IN IP4 192.168.1.2\n" +
        "s=session\n" +
        "c=IN IP4 192.168.1.2\n" +
        "t=0 0\n" +
        "m=audio 64535 RTP/AVP 0 8\n" +
        "a=rtpmap:0 pcmu/8000";

        SessionDescription sd = sdpFactory.createSessionDescription(sdp);
        HashMap result = RTPFormat.getFormats(sd);
        
        System.out.println("RES=" + result);
        
        Format fmt = (Format) result.get(0);
        if (!fmt.matches(pcmu)) {
            fail("PCMU expected");
        }
    }
    
}