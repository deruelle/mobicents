package org.mobicents.mgcp.stack.test.deleteconnection;

import org.apache.log4j.Logger;
import org.mobicents.mgcp.stack.test.MessageFlowHarness;


public class DeleteConnectionTest extends MessageFlowHarness {

	private static Logger logger = Logger.getLogger("mgcp.test");

	private CA ca;
	private MGW mgw;

	public DeleteConnectionTest() {
		super("DeleteConnectionTest");
	}

	public void setUp() {
		try {
			super.setUp();

			ca = new CA(caProvider, mgProvider);
			mgw = new MGW(mgProvider);

		} catch (Exception ex) {
			ex.printStackTrace();
			fail("Unexpected Exception");
		}
	}

	public void testDeleteConnection() {
		this.ca.sendDeleteConnection();
		waitForMessage();
	}

	public void tearDown() {

		this.ca.checkState();
		this.mgw.checkState();
		logTestCompleted();

	}


	public static void assertTrue(String diagnostic, boolean cond) {
		if (cond) {
			assertTrue(cond);
		} else {
			fail(diagnostic);
		}

	}
}
