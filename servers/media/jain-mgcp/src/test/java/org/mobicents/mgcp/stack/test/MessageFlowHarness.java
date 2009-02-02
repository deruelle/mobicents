package org.mobicents.mgcp.stack.test;

import java.net.InetAddress;

import org.apache.log4j.Logger;
import org.mobicents.mgcp.stack.JainMgcpStackImpl;
import org.mobicents.mgcp.stack.JainMgcpStackProviderImpl;

public abstract class MessageFlowHarness extends TestHarness {

	private static Logger logger = Logger.getLogger("mgcp.test");

	protected static long TRANSACTION_TIMES_OUT_FOR = 31000;

	protected static long STACKS_START_FOR = 1000;

	protected static long STACKS_SHUT_DOWN_FOR = 500;

	// timeout values depend on pc
	protected static long MESSAGES_ARRIVE_FOR = 3000;

	protected static long RETRANSMISSION_TRANSACTION_TIMES_OUT_FOR = 5000;

	protected InetAddress caIPAddress = null;
	protected InetAddress mgIPAddress = null;

	protected JainMgcpStackImpl caStack = null;
	protected JainMgcpStackImpl mgStack = null;

	protected JainMgcpStackProviderImpl caProvider = null;
	protected JainMgcpStackProviderImpl mgProvider = null;

	public MessageFlowHarness(String name) {
		super(name);
	}

	public void setUp() throws java.lang.Exception {
		caIPAddress = InetAddress.getByName(LOCAL_ADDRESS);
		mgIPAddress = InetAddress.getByName(LOCAL_ADDRESS);

		caStack = new JainMgcpStackImpl(caIPAddress, CA_PORT);
		mgStack = new JainMgcpStackImpl(mgIPAddress, MGW_PORT);

		caProvider = (JainMgcpStackProviderImpl) caStack.createProvider();
		mgProvider = (JainMgcpStackProviderImpl) mgStack.createProvider();

	}

	public void tearDown() throws java.lang.Exception {

		System.out.println("CLOSE THE STACK");

		if (caStack != null) {
			caStack.close();
			caStack = null;
		}

		if (mgStack != null) {
			mgStack.close();
			mgStack = null;
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
