package org.mobicents.javax.media.mscontrol.resource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import javax.media.mscontrol.mediagroup.CoderConstants;
import javax.media.mscontrol.mediagroup.Recorder;
import javax.media.mscontrol.resource.Parameter;
import javax.media.mscontrol.resource.Parameters;
import javax.media.mscontrol.resource.Resource;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

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
		parameters.put(Recorder.p_AudioCoder, CoderConstants.v_MuLawPCM_64k);
	}

	@After
	public void tearDown() {
		parameters.clear();
		obj = null;
	}

	@Test
	public void testParameters() {
		assertTrue(parameters.containsKey(Recorder.p_AudioCoder));
		assertTrue(parameters.containsValue(CoderConstants.v_MuLawPCM_64k));
		
		Set<java.util.Map.Entry<Parameter, Object>> set = parameters.entrySet();
		assertNotNull(set);
		assertEquals(1, set.size());
		
		Object objTemp = parameters.get(Recorder.p_AudioCoder);
		assertEquals(CoderConstants.v_MuLawPCM_64k, objTemp);
		
		assertEquals(false, parameters.isEmpty());
		
		assertNotNull(parameters.remove(Recorder.p_AudioCoder));

	}
}
