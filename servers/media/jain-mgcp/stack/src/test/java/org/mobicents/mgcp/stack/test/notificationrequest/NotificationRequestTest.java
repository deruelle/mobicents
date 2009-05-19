package org.mobicents.mgcp.stack.test.notificationrequest;

import org.apache.log4j.Logger;
import org.mobicents.mgcp.stack.test.MessageFlowHarness;

public class NotificationRequestTest extends MessageFlowHarness {

	private static Logger logger = Logger.getLogger("mgcp.test");

	private CA ca;
	private MGW mgw;

	public NotificationRequestTest() {
		super("NotificationRequestTest");
	}

	public void setUp() {
		try {
			super.setUp();

		} catch (Exception ex) {
			ex.printStackTrace();
			fail("Unexpected Exception");
		}
	}

	public void testNotificationRequest() {

		ca = new CA(caProvider, mgProvider);
		mgw = new MGW(mgProvider);
		this.ca.sendNotificationRequest();
		waitForMessage();
	}

	public void testNotificationRequestFailure() {

		ca = new CA(caProvider, mgProvider);
		mgw = new MGW(mgProvider, true);
		this.ca.sendNotificationRequest();
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
