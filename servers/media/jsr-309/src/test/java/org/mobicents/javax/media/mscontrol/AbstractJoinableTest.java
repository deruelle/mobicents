package org.mobicents.javax.media.mscontrol;

import javax.media.mscontrol.MsControlException;
import javax.media.mscontrol.Joinable.Direction;

import junit.framework.TestCase;

public class AbstractJoinableTest extends TestCase {

	public AbstractJoinableTest() {
		super();
	}

	public AbstractJoinableTest(String name) {
		super(name);
	}

	public void setUp() {

	}

	public void tearDown() throws Exception {

	}

	public void testAbstarctJoinable() {
		TestAbstractJoinable j1 = new TestAbstractJoinable();
		TestAbstractJoinable j2 = new TestAbstractJoinable();
		TestAbstractJoinable j3 = new TestAbstractJoinable();

		// J1 Sends to J2
		try {
			j1.join(Direction.SEND, j2);
			assertEquals(Direction.RECV, j2.getDirection());
		} catch (MsControlException e) {
			// Un-expected
			e.printStackTrace();
			fail("The join operation failed ");
		}

		// J2 SEND_RECV from J3 which should fail
		try {
			j2.join(Direction.DUPLEX, j3);
			fail("J2 already conneted to J1 hance fails");
		} catch (MsControlException e) {
			// Expected
		}

		try {
			j1.unjoin(j2);
		} catch (MsControlException e) {
			// un-expected
			e.printStackTrace();
			fail();
		}

		// Now J2 can join J3
		try {
			j2.join(Direction.DUPLEX, j3);
			assertEquals(Direction.DUPLEX, j3.getDirection());
		} catch (MsControlException e) {
			// Un-expected
			e.printStackTrace();
			fail("Joining J2 failed with J3");
		}

	}

	private class TestAbstractJoinable extends AbstractJoinable {

	}

}
