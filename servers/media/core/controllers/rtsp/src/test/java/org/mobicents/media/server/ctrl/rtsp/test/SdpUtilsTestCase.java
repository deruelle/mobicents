package org.mobicents.media.server.ctrl.rtsp.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.net.URL;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mobicents.media.server.ctrl.rtsp.SdpUtils;

public class SdpUtilsTestCase {

	private String rawSdp = "v=0\r\n" + "o=4855 13760799956958020 13760799956958020" + " IN IP4 16.16.230.57\r\n"
			+ "s=mysession session\r\n" + "p=+46 8 52018010\r\n" + "c=IN IP4 16.16.230.57\r\n" + "t=0 0\r\n"
			+ "m=audio 6022 RTP/AVP 8 4 18\r\n" + "a=rtpmap:8 PCMA/8000/1\r\n" + "a=rtpmap:4 G723/8000\r\n"
			+ "a=rtpmap:18 G729A/8000\r\n" + "a=ptime:20\r\n";
	private SdpUtils sdpUtils = null;
	private static final String ipAddress = "192.168.0.100";
	private static final int port = 9000;

	public SdpUtilsTestCase() {
	}

	@BeforeClass
	public static void setUpClass() throws Exception {
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		sdpUtils = new SdpUtils();
	}

	@After
	public void tearDown() {
	}

	@Test
	public void testALAW_sdp() throws Exception {
		URL url = SdpUtilsTestCase.class.getClassLoader().getResource(
				"org/mobicents/media/server/ctrl/mgcp/test/ann/8kulaw.wav");
		String path = url.getPath();

		assertNotNull(path);

		File f = new File(path);

		String sdp = sdpUtils.getSdp(f, ipAddress, port, "rtsp://127.0.0.1:1554/addf8-Alaw-GW.wav");

		System.out.println(sdp);

		assertNotNull(sdp);
	}

	@Test
	public void testGetAudioPort() throws Exception {		
		int port = sdpUtils.getAudioPort(rawSdp);
		assertEquals(6022, port);
	}

}
