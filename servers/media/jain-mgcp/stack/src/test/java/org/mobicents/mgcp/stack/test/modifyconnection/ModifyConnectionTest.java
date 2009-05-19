package org.mobicents.mgcp.stack.test.modifyconnection;

import org.apache.log4j.Logger;
import org.mobicents.mgcp.stack.test.MessageFlowHarness;

public class ModifyConnectionTest extends MessageFlowHarness {

	private static Logger logger = Logger.getLogger("mgcp.test");

	private CA ca;
	private MGW mgw;

	public ModifyConnectionTest() {
		super("ModifyConnectionTest");
	}

	public void setUp() {
		try {
			super.setUp();

		} catch (Exception ex) {
			ex.printStackTrace();
			fail("Unexpected Exception");
		}
	}

	public void testModifyConnection() {
		ca = new CA(caProvider, mgProvider);
		mgw = new MGW(mgProvider);
		this.ca.sendModifyConnection();
		waitForMessage();
	}

	public void testModifyConnectionFailure() {
		ca = new CA(caProvider, mgProvider);
		mgw = new MGW(mgProvider, true);
		this.ca.sendModifyConnection();
		waitForMessage();
	}

	public void tearDown() {
		try {
			super.tearDown();
		} catch (Exception ex) {

		}

		this.ca.checkState();
		this.mgw.checkState();
		logTestCompleted();

	}

}
