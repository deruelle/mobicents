package org.mobicents.media.server.impl.rtp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 
 * @author baranowb
 * 
 */
public class RtpHeaderTest {

    public static final int _HEADER_LENGTH = 12;
    public static final int _TEST_HEADER_LENGTH = 25;
    public static final int _OVERLAP = _TEST_HEADER_LENGTH - _HEADER_LENGTH;

    @Test
    public void testAppend() {
        boolean marker = false;
        byte payloadType = 9;
        int seqNumber = 13717;
        int timestamp = 1234897;
        long ssrc = 1438967189;

        byte[] bin = new byte[12];
        bin[0] = (byte) (0x80);

        bin[1] = marker ? (byte) (payloadType | 0x80) : (byte) (payloadType & 0x7f);
        bin[2] = ((byte) ((seqNumber & 0xFF00) >> 8));
        bin[3] = ((byte) (seqNumber & 0x00FF));

        bin[4] = ((byte) ((timestamp & 0xFF000000) >> 24));
        bin[5] = ((byte) ((timestamp & 0x00FF0000) >> 16));
        bin[6] = ((byte) ((timestamp & 0x0000FF00) >> 8));
        bin[7] = ((byte) ((timestamp & 0x000000FF)));

        bin[8] = ((byte) ((ssrc & 0xFF000000) >> 24));
        bin[9] = ((byte) ((ssrc & 0x00FF0000) >> 16));
        bin[10] = ((byte) ((ssrc & 0x0000FF00) >> 8));
        bin[11] = ((byte) ((ssrc & 0x000000FF)));

        RtpHeader h2 = new RtpHeader();
        int i = 0;
        while (!h2.isFilled()) {
            h2.append(bin, i++, 1);
        }
        
        assertEquals(marker, h2.getMarker());
        assertEquals(payloadType, h2.getPayloadType());
        assertEquals(seqNumber, h2.getSeqNumber());
        assertEquals(timestamp, h2.getTimestamp());
        assertEquals(ssrc, h2.getSsrc());
    }

    @Test
    public void testFullParse() {
        // There was a bug, nwe have to cover it :)
        RtpHeader header = new RtpHeader();

        //Values we want

        boolean marker = false;
        byte payloadType = 9;
        int seqNumber = 13717;
        int timestamp = 1234897;
        long ssrc = 1438967189;


        byte[] bin = new byte[12];
        bin[0] = (byte) (0x80);

        bin[1] = marker ? (byte) (payloadType | 0x80) : (byte) (payloadType & 0x7f);
        bin[2] = ((byte) ((seqNumber & 0xFF00) >> 8));
        bin[3] = ((byte) (seqNumber & 0x00FF));

        bin[4] = ((byte) ((timestamp & 0xFF000000) >> 24));
        bin[5] = ((byte) ((timestamp & 0x00FF0000) >> 16));
        bin[6] = ((byte) ((timestamp & 0x0000FF00) >> 8));
        bin[7] = ((byte) ((timestamp & 0x000000FF)));

        bin[8] = ((byte) ((ssrc & 0xFF000000) >> 24));
        bin[9] = ((byte) ((ssrc & 0x00FF0000) >> 16));
        bin[10] = ((byte) ((ssrc & 0x0000FF00) >> 8));
        bin[11] = ((byte) ((ssrc & 0x000000FF)));

        try {
            header.init(bin);
            assertEquals("Payload value is not correct, on full init.", payloadType, header.getPayloadType());
            assertEquals("Seq value is not correct, on full init.", seqNumber, header.getSeqNumber());
            assertEquals("timestamp value is not correct, on full init.", timestamp, header.getTimestamp());
            assertEquals("Ssrc value is not correct, on full init.", ssrc, header.getSsrc());
            assertTrue("Binary representation is null.", header.toByteArray() != null);
            assertTrue("Failed to match binary representation.", Arrays.equals(bin, header.toByteArray()));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Failed to parse due exception.");
        }

    }

    @Test
    public void testInitFromJavaPrimitivesVsBin() {
        RtpHeader header = new RtpHeader();

        //Values we want

        boolean marker = false;
        byte payloadType = 9;
        int seqNumber = 13717;
        int timestamp = 1234897;
        long ssrc = 1438967189;


        byte[] bin = new byte[12];
        bin[0] = (byte) (0x80);

        bin[1] = marker ? (byte) (payloadType | 0x80) : (byte) (payloadType & 0x7f);
        bin[2] = ((byte) ((seqNumber & 0xFF00) >> 8));
        bin[3] = ((byte) (seqNumber & 0x00FF));

        bin[4] = ((byte) ((timestamp & 0xFF000000) >> 24));
        bin[5] = ((byte) ((timestamp & 0x00FF0000) >> 16));
        bin[6] = ((byte) ((timestamp & 0x0000FF00) >> 8));
        bin[7] = ((byte) ((timestamp & 0x000000FF)));

        bin[8] = ((byte) ((ssrc & 0xFF000000) >> 24));
        bin[9] = ((byte) ((ssrc & 0x00FF0000) >> 16));
        bin[10] = ((byte) ((ssrc & 0x0000FF00) >> 8));
        bin[11] = ((byte) ((ssrc & 0x000000FF)));

        try {
            header.init(payloadType, seqNumber, timestamp, ssrc);
            assertEquals("Payload value is not correct, on primitives init.", payloadType, header.getPayloadType());
            assertEquals("Seq value is not correct, on primitives init.", seqNumber, header.getSeqNumber());
            assertEquals("timestamp value is not correct, on primitives init.", timestamp, header.getTimestamp());
            assertEquals("Ssrc value is not correct, on primitives init.", ssrc, header.getSsrc());
            assertTrue("Binary representation is null.", header.toByteArray() != null);
            assertTrue("Failed to match binary representation.", Arrays.equals(bin, header.toByteArray()));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Failed to parse due exception.");
        }


    }


    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }
}
