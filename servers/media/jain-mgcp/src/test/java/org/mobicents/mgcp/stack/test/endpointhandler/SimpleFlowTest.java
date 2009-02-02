package org.mobicents.mgcp.stack.test.endpointhandler;

import org.apache.log4j.Logger;
import org.mobicents.mgcp.stack.test.MessageFlowHarness;


public class SimpleFlowTest extends MessageFlowHarness {

	private static Logger logger = Logger.getLogger("mgcp.test");

	private CA ca;
	private MGW mgw;

	public SimpleFlowTest() {
		super("SimpleFlowTest");
	}

	public void setUp() {
		try {
			super.setUp();

			ca = new CA(caProvider, mgProvider,super.caIPAddress,super.CA_PORT);
			mgw = new MGW(mgProvider,super.mgIPAddress,super.MGW_PORT,super.CA_PORT);

		} catch (Exception ex) {
			ex.printStackTrace();
			fail("Unexpected Exception");
		}
	}

	public void testCRCX_NR_DLCX_Flow() {
		
		
		try{
		this.ca.sendCRCX();
		waitForRetransmissionTimeout();
		
		sleep(45000);
		
		}finally
		{
			ca.checkState();
			mgw.checkState();
		}
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
