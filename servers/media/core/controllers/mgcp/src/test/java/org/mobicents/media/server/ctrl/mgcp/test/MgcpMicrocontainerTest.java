package org.mobicents.media.server.ctrl.mgcp.test;

import java.net.InetAddress;

import org.jboss.test.kernel.junit.MicrocontainerTest;
import org.mobicents.media.server.ctrl.mgcp.MgcpController;
import org.mobicents.mgcp.stack.JainMgcpStackImpl;
import org.mobicents.mgcp.stack.JainMgcpStackProviderImpl;


public abstract class MgcpMicrocontainerTest extends MicrocontainerTest
{
	protected static long TRANSACTION_TIMES_OUT_FOR = 31000;

	protected static long STACKS_START_FOR = 1000;

	protected static long STACKS_SHUT_DOWN_FOR = 500;

	// timeout values depend on pc
	protected static long MESSAGES_ARRIVE_FOR = 3000;

	protected static long RETRANSMISSION_TRANSACTION_TIMES_OUT_FOR = 5000;
	
	protected static final String LOCAL_ADDRESS = "127.0.0.1";

	protected static final int CA_PORT = 2724;


	protected InetAddress caIPAddress = null;
	
	protected JainMgcpStackImpl caStack = null;
	protected JainMgcpStackProviderImpl caProvider = null;
	
	protected MgcpController mgcpServerStack = null;

	public MgcpMicrocontainerTest(String name) {
		super(name);
	}
	   
	protected void setUp() throws Exception {
		super.setUp();
		caIPAddress = InetAddress.getByName(LOCAL_ADDRESS);
		caStack = new JainMgcpStackImpl(caIPAddress, CA_PORT);
		caProvider = (JainMgcpStackProviderImpl) caStack.createProvider();
		
		mgcpServerStack = (MgcpController) getBean("MgcpController");
		assertNotNull(mgcpServerStack);		
	}
	
	public void tearDown() throws java.lang.Exception {

		System.out.println("CLOSE THE STACK");

		if (caStack != null) {
			caStack.close();
			caStack = null;
		}
		
		if(mgcpServerStack != null){
			mgcpServerStack.stop();
			mgcpServerStack = null;
		}


		// Wait for stack threads to release resources (e.g. port)
		sleep(STACKS_SHUT_DOWN_FOR);
	}

	protected static void waitForTimeout() {
		sleep(TRANSACTION_TIMES_OUT_FOR);
	}

	protected static void waitForRetransmissionTimeout() {
		sleep(RETRANSMISSION_TRANSACTION_TIMES_OUT_FOR);
	}

	public static void waitForMessage() {
		sleep(MESSAGES_ARRIVE_FOR);
	}	
	
	protected static void sleep(long sleepFor) {
		try {
			Thread.sleep(sleepFor);
		} catch (InterruptedException ex) {
			// Ignore
		}
	}
}
