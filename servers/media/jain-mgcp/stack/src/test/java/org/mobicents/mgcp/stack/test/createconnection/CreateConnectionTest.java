package org.mobicents.mgcp.stack.test.createconnection;

import org.apache.log4j.Logger;
import org.mobicents.mgcp.stack.test.MessageFlowHarness;

public class CreateConnectionTest extends MessageFlowHarness {

	private static Logger logger = Logger.getLogger("mgcp.test");

	private CA ca;
	private MGW mgw;

	public CreateConnectionTest() {
		super("CreateConnectionTest");
	}

	public void setUp() {
		try {
			super.setUp();
		} catch (Exception ex) {
			ex.printStackTrace();
			fail("Unexpected Exception");
		}
	}

	public void testCreateConnection() {
		ca = new CA(caProvider, mgProvider);
		mgw = new MGW(mgProvider);
		
		this.ca.sendCreateConnection();
		waitForMessage();
	}
	
	public void testCreateConnectionFailure() {
		
		ca = new CA(caProvider, mgProvider);
		mgw = new MGW(mgProvider, true);
		
		this.ca.sendCreateConnection();
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
