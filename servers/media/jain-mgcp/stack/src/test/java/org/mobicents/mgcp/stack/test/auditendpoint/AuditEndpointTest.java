package org.mobicents.mgcp.stack.test.auditendpoint;

import org.apache.log4j.Logger;
import org.mobicents.mgcp.stack.test.MessageFlowHarness;

public class AuditEndpointTest extends MessageFlowHarness {

	private static Logger logger = Logger.getLogger("mgcp.test");

	private CA ca;
	private MGW mgw;

	public AuditEndpointTest() {
		super("AuditEndpointTest");
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

	public void testAuditEndpoint() {
		this.ca.sendAuditEndpoint();
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