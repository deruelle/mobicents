package test.msgflow;

import java.net.InetAddress;
import java.util.Properties;

import javax.media.mscontrol.MsControlFactory;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.mobicents.jsr309.mgcp.MgcpStackFactory;
import org.mobicents.mgcp.stack.JainMgcpStackImpl;
import org.mobicents.mgcp.stack.JainMgcpStackProviderImpl;

public abstract class MessageFlowHarness extends TestCase {

	protected Logger logger = null;

	public MessageFlowHarness() {
	}

	public MessageFlowHarness(String name) {
		super(name);

	}

	protected static long TRANSACTION_TIMES_OUT_FOR = 31000;

	protected static long STACKS_START_FOR = 1000;

	protected static long STACKS_SHUT_DOWN_FOR = 500;

	// timeout values depend on pc
	protected static long MESSAGES_ARRIVE_FOR = 3000;

	protected static long RETRANSMISSION_TRANSACTION_TIMES_OUT_FOR = 5000;

	protected static final String LOCAL_ADDRESS = "127.0.0.1";

	protected static final int MGW_PORT = 2427;

	protected static final int CA_PORT = 2727;
	
	protected boolean testPassed = false;
	
	protected static final String TEST_SEPARATOR = "----------------";

	protected InetAddress mgwIPAddress = null;

	protected JainMgcpStackImpl mgwStack = null;
	protected JainMgcpStackProviderImpl mgwProvider = null;

	protected MsControlFactory msControlFactory = null;
	protected Properties property = null;

	protected void setUp() throws Exception {
		logger.info(TEST_SEPARATOR + this.getName() + " Started" + TEST_SEPARATOR);		
		super.setUp();
		testPassed = false;
		mgwIPAddress = InetAddress.getByName(LOCAL_ADDRESS);
		mgwStack = new JainMgcpStackImpl(mgwIPAddress, MGW_PORT);
		mgwProvider = (JainMgcpStackProviderImpl) mgwStack.createProvider();

		property = new Properties();
		property.put(MgcpStackFactory.MGCP_STACK_NAME, "TestStack");
		property.put(MgcpStackFactory.MGCP_PEER_IP, LOCAL_ADDRESS);
		property.put(MgcpStackFactory.MGCP_PEER_PORT, mgwStack.getPort());

		property.put(MgcpStackFactory.MGCP_STACK_IP, LOCAL_ADDRESS);
		property.put(MgcpStackFactory.MGCP_STACK_PORT, CA_PORT);

		javax.media.mscontrol.spi.Driver driver = new org.mobicents.javax.media.mscontrol.spi.DriverImpl();
		msControlFactory = driver.getFactory(property);

		assertNotNull(msControlFactory);
	}

	public void tearDown() throws java.lang.Exception {

		logger.debug("CLOSE THE STACK");

		if (mgwStack != null) {
			mgwStack.close();
			mgwStack = null;
		}

		MgcpStackFactory mgcpStackFactory = MgcpStackFactory.getInstance();
		mgcpStackFactory.clearMgcpStackProvider(property);

		// Wait for stack threads to release resources (e.g. port)
		sleep(STACKS_SHUT_DOWN_FOR);
		logger.info(TEST_SEPARATOR + this.getName() + " Completed" + TEST_SEPARATOR);
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
