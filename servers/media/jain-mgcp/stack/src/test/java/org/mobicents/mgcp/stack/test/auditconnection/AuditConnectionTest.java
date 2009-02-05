package org.mobicents.mgcp.stack.test.auditconnection;

import org.apache.log4j.Logger;
import org.mobicents.mgcp.stack.test.MessageFlowHarness;
import org.mobicents.mgcp.stack.test.auditconnection.CA;
import org.mobicents.mgcp.stack.test.auditconnection.MGW;

public class AuditConnectionTest extends MessageFlowHarness {

	private static Logger logger = Logger.getLogger("mgcp.test");

	private CA ca;
	private MGW mgw;

	public AuditConnectionTest() {
		super("AuditConnectionTest");
	}

	public void setUp() {
		try {
			super.setUp();

			ca = new CA(caProvider, mgProvider);
			
			mgw = new MGW(mgProvider,caProvider.getJainMgcpStack().getPort());

		} catch (Exception ex) {
			ex.printStackTrace();
			fail("Unexpected Exception");
		}
	}

	public void testAuditConnection() {
		this.ca.sendAuditConnection();
//		try {
//			Thread.sleep(1000 * 60 * 5);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
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