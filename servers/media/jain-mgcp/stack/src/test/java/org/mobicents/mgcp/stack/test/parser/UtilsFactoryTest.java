package org.mobicents.mgcp.stack.test.parser;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mobicents.mgcp.stack.parser.Utils;
import org.mobicents.mgcp.stack.parser.UtilsFactory;

import static org.junit.Assert.*;

public class UtilsFactoryTest {

	@BeforeClass
	public static void setUpClass() throws Exception {
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {

	}

	@After
	public void tearDown() {
	}

	@Test
	public void testEquality() throws Exception {
		UtilsFactory factory = new UtilsFactory(1);
		Utils u1 = factory.allocate();

		assertNotNull(u1);

		factory.deallocate(u1);

		Utils u2 = factory.allocate();
		assertNotNull(u2);

		assertEquals(u1, u2);

		Utils u3 = factory.allocate();
		assertEquals(1, factory.getCount());

		assertNotSame(u2, u3);

		factory.deallocate(u2);
		factory.deallocate(u3);

	}
}
