package org.mobicents.mgcp.stack.test.endpointconfiguration;

import jain.protocol.ip.mgcp.JainMgcpCommandEvent;
import jain.protocol.ip.mgcp.JainMgcpEvent;
import jain.protocol.ip.mgcp.JainMgcpResponseEvent;
import jain.protocol.ip.mgcp.message.Constants;
import jain.protocol.ip.mgcp.message.EndpointConfiguration;
import jain.protocol.ip.mgcp.message.parms.BearerInformation;
import jain.protocol.ip.mgcp.message.parms.CallIdentifier;
import jain.protocol.ip.mgcp.message.parms.EndpointIdentifier;

import org.apache.log4j.Logger;
import org.mobicents.mgcp.stack.JainMgcpExtendedListener;
import org.mobicents.mgcp.stack.JainMgcpStackProviderImpl;
import org.mobicents.mgcp.stack.test.createconnection.CreateConnectionTest;

public class CA implements JainMgcpExtendedListener {

	private static Logger logger = Logger.getLogger(CA.class);

	private JainMgcpStackProviderImpl caProvider;
	private int mgStack = 0;
	private boolean responseReceived = false;

	public CA(JainMgcpStackProviderImpl caProvider, JainMgcpStackProviderImpl mgwProvider) {
		this.caProvider = caProvider;
		mgStack = mgwProvider.getJainMgcpStack().getPort();
	}

	public void sendEndpointConfiguration() {

		try {
			caProvider.addJainMgcpListener(this);

			CallIdentifier callID = caProvider.getUniqueCallIdentifier();

			EndpointIdentifier endpointID = new EndpointIdentifier("media/trunk/Announcement/", "127.0.0.1:" + mgStack);
			EndpointConfiguration endpointConfiguration = new EndpointConfiguration(this, endpointID, BearerInformation.EncMethod_A_Law );

			endpointConfiguration.setTransactionHandle(caProvider.getUniqueTransactionHandler());

			caProvider.sendMgcpEvents(new JainMgcpEvent[] { endpointConfiguration });

			logger.debug(" EndpointConfiguration command sent for TxId " + endpointConfiguration.getTransactionHandle()
					+ " and CallId " + callID);
		} catch (Exception e) {			
			e.printStackTrace();
			EndpointConfigurationTest.fail("Unexpected Exception");
		}
	}

	public void checkState() {
		CreateConnectionTest.assertTrue("Expect to receive EPCF Response", responseReceived);

	}

	public void transactionEnded(int handle) {
		logger.info("transactionEnded " + handle);

	}

	public void transactionRxTimedOut(JainMgcpCommandEvent command) {
		logger.info("transactionRxTimedOut " + command);

	}

	public void transactionTxTimedOut(JainMgcpCommandEvent command) {
		logger.info("transactionTxTimedOut " + command);

	}

	public void processMgcpCommandEvent(JainMgcpCommandEvent jainmgcpcommandevent) {
		logger.info("processMgcpCommandEvent " + jainmgcpcommandevent);
	}

	public void processMgcpResponseEvent(JainMgcpResponseEvent jainmgcpresponseevent) {
		logger.debug("processMgcpResponseEvent = " + jainmgcpresponseevent);
		switch (jainmgcpresponseevent.getObjectIdentifier()) {
		case Constants.RESP_ENDPOINT_CONFIGURATION:
			responseReceived = true;			
			break;
		default:
			logger.warn("This RESPONSE is unexpected " + jainmgcpresponseevent);
			break;

		}

	}

}
