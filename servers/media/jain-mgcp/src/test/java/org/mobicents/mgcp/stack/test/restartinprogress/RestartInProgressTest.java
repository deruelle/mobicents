package org.mobicents.mgcp.stack.test.restartinprogress;

import org.apache.log4j.Logger;
import org.mobicents.mgcp.stack.test.MessageFlowHarness;

public class RestartInProgressTest extends MessageFlowHarness {

	private static Logger logger = Logger.getLogger("mgcp.test");

	private CA ca;
	private MGW mgw;

	public RestartInProgressTest() {
		super("RestartInProgressTest");
	}

	public void setUp() {
		try {
			super.setUp();

			ca = new CA(caProvider, mgProvider);

			mgw = new MGW(mgProvider, caProvider.getJainMgcpStack().getPort());

		} catch (Exception ex) {
			ex.printStackTrace();
			fail("Unexpected Exception");
		}
	}

	public void testAuditConnection() {
		this.mgw.sendRestartInProgress();
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