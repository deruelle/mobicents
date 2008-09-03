package org.mobicents.media.server.impl.common.events;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class EventIDTest {

	public EventIDTest() {

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

	@Test	
	public void testToString() {
		EventID complete = EventID.getEvent("org.mobicents.media.ann.COMPLETE");
		assertEquals(EventID.COMPLETE, complete);
		
		EventID play = EventID.getEvent("org.mobicents.media.ann.PLAY");
		assertEquals(EventID.PLAY, play);
		
		EventID fail = EventID.getEvent("org.mobicents.media.ann.FAIL");
		assertEquals(EventID.FAIL, fail);
		
		EventID playRecord = EventID.getEvent("org.mobicents.media.au.PLAY_RECORD");
		assertEquals(EventID.PLAY_RECORD, playRecord);		
		
		EventID promptAndCollect = EventID.getEvent("org.mobicents.media.au.PROMPT_AND_COLLECT");
		assertEquals(EventID.PROMPT_AND_COLLECT, promptAndCollect);				
		
		EventID dtmf = EventID.getEvent("org.mobicents.media.dtmf.DTMF");
		assertEquals(EventID.DTMF, dtmf);						
		
		EventID testSpectra = EventID.getEvent("org.mobicents.media.test.TEST_SPECTRA");
		assertEquals(EventID.TEST_SPECTRA, testSpectra);								
		
		EventID testSine = EventID.getEvent("org.mobicents.media.test.TEST_SINE");
		assertEquals(EventID.TEST_SINE, testSine);										

	}

}
