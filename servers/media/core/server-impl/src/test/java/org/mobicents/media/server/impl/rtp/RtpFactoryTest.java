package org.mobicents.media.server.impl.rtp;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mobicents.media.Format;
import org.mobicents.media.format.AudioFormat;

/**
 * 
 * @author amit bhayani
 *
 */
public class RtpFactoryTest {
	
	private static final AudioFormat PCMA = new AudioFormat(AudioFormat.ALAW, 8000, 8, 1);
	private static final AudioFormat PCMU = new AudioFormat(AudioFormat.ULAW, 8000, 8, 1);
	
	private static Map<Integer, Format> formatMap = new HashMap<Integer, Format>();
	static{
		formatMap.put(0, PCMU);
		formatMap.put(8, PCMA);
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
	public void tearDown(){
		
	}
	
	@Test
	public void getRTPSocketTest(){
		RtpFactory factory = new RtpFactory();
		factory.setFormatMap(formatMap);
		
		
		
	}

}
