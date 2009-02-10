package org.mobicents.javax.media.mscontrol.resource;

import static org.junit.Assert.assertTrue;

import java.util.Set;

import javax.media.mscontrol.resource.Parameters;
import javax.media.mscontrol.resource.ResourceConstants;
import javax.media.mscontrol.resource.Symbol;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

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
		obj = new Object();
		parameters.put(ResourceConstants.q_RTC, obj);
	}

	@After
	public void tearDown() {
		parameters.clear();
		obj = null;
	}

	@Test
	public void testParameters() {
		assertTrue(parameters.containsKey(ResourceConstants.q_RTC));
		assertTrue(parameters.containsValue(obj));
		
		Set<java.util.Map.Entry<Symbol, Object>> set = parameters.entrySet();
		assertNotNull(set);
		assertEquals(1, set.size());
		
		Object objTemp = parameters.get(ResourceConstants.q_RTC);
		assertEquals(obj, objTemp);
		
		assertEquals(false, parameters.isEmpty());
		
		assertNotNull(parameters.remove(ResourceConstants.q_RTC));

	}
}
