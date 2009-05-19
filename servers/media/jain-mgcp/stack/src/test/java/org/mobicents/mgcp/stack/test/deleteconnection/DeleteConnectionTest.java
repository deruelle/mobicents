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
		} catch (Exception ex) {
			ex.printStackTrace();
			fail("Unexpected Exception");
		}
	}

	public void testDeleteConnection() {
		ca = new CA(caProvider, mgProvider);
		mgw = new MGW(mgProvider);
		
		this.ca.sendDeleteConnection();
		waitForMessage();
	}
	
	public void testDeleteConnectionFailure() {
		ca = new CA(caProvider, mgProvider);
		mgw = new MGW(mgProvider, true);
		
		this.ca.sendDeleteConnection();
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
