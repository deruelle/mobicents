package org.mobicents.javax.media.mscontrol.resource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import javax.media.mscontrol.Parameter;
import javax.media.mscontrol.Parameters;
import javax.media.mscontrol.mediagroup.CodecConstants;
import javax.media.mscontrol.mediagroup.Recorder;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mobicents.javax.media.mscontrol.ParametersImpl;

/**
 * 
 * @author amit bhayani
 *
 */
public class ParametersImplTest {
	Parameters parameters = null;
	Object obj = null;

	public ParametersImplTest() {

	}

	@BeforeClass
	public static void setUpClass() throws Exception {
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
	}

	@Before
	public void setUp() {
		parameters = new ParametersImpl();
		parameters.put(Recorder.AUDIO_CODEC, CodecConstants.MULAW_PCM_64K);
	}

	@After
	public void tearDown() {
		parameters.clear();
		obj = null;
	}

	@Test
	public void testParameters() {
		assertTrue(parameters.containsKey(Recorder.AUDIO_CODEC));
		assertTrue(parameters.containsValue(CodecConstants.MULAW_PCM_64K));
		
		Set<java.util.Map.Entry<Parameter, Object>> set = parameters.entrySet();
		assertNotNull(set);
		assertEquals(1, set.size());
		
		Object objTemp = parameters.get(Recorder.AUDIO_CODEC);
		assertEquals(CodecConstants.MULAW_PCM_64K, objTemp);
		
		assertEquals(false, parameters.isEmpty());
		
		assertNotNull(parameters.remove(Recorder.AUDIO_CODEC));

	}
}
