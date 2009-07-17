package org.mobicents.mgcp.stack.test.notify;

import org.apache.log4j.Logger;
import org.mobicents.mgcp.stack.test.MessageFlowHarness;

public class NotifyTest extends MessageFlowHarness {

	private static Logger logger = Logger.getLogger("mgcp.test");

	private CA ca;
	private MGW mgw;

	public NotifyTest() {
		super("NotifyTest");
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

	public void testNotify() {
		this.ca.sendNotify();
		waitForMessage();
		waitForMessage();
		waitForMessage();
		waitForMessage();
		waitForMessage();
		waitForMessage();
		waitForMessage();waitForMessage();waitForMessage();waitForMessage();
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
