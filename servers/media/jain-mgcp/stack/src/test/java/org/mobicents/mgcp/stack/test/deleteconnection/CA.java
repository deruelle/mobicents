package org.mobicents.mgcp.stack.test.deleteconnection;

import jain.protocol.ip.mgcp.JainMgcpCommandEvent;
import jain.protocol.ip.mgcp.JainMgcpEvent;
import jain.protocol.ip.mgcp.JainMgcpResponseEvent;
import jain.protocol.ip.mgcp.message.Constants;
import jain.protocol.ip.mgcp.message.DeleteConnection;
import jain.protocol.ip.mgcp.message.parms.ConnectionIdentifier;
import jain.protocol.ip.mgcp.message.parms.EndpointIdentifier;
import jain.protocol.ip.mgcp.message.parms.ReturnCode;

import org.apache.log4j.Logger;
import org.mobicents.mgcp.stack.JainMgcpExtendedListener;
import org.mobicents.mgcp.stack.JainMgcpStackProviderImpl;

public class CA implements JainMgcpExtendedListener {

	private static Logger logger = Logger.getLogger(CA.class);

	private JainMgcpStackProviderImpl caProvider;
	private int mgStack = 0;
	private boolean responseReceived = false;

	public CA(JainMgcpStackProviderImpl caProvider, JainMgcpStackProviderImpl mgwProvider) {
		this.caProvider = caProvider;
		mgStack = mgwProvider.getJainMgcpStack().getPort();
	}

	public void sendDeleteConnection() {

		try {
			caProvider.addJainMgcpListener(this);

			EndpointIdentifier endpointID = new EndpointIdentifier("media/trunk/Announcement/enp14", "127.0.0.1:"
					+ mgStack);

			ConnectionIdentifier connectionIdentifier = new ConnectionIdentifier((caProvider.getUniqueCallIdentifier())
					.toString());

			DeleteConnection deleteConnection = new DeleteConnection(this, endpointID);
			deleteConnection.setConnectionIdentifier(connectionIdentifier);
			deleteConnection.setTransactionHandle(caProvider.getUniqueTransactionHandler());

			caProvider.sendMgcpEvents(new JainMgcpEvent[] { deleteConnection });

			logger.debug(" DeleteConnection command sent for TxId " + deleteConnection.getTransactionHandle()
					+ " and ConnectionIdentifier " + connectionIdentifier);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			DeleteConnectionTest.fail("Unexpected Exception");
		}
	}

	public void checkState() {
		DeleteConnectionTest.assertTrue("Expect to receive DLCX Response", responseReceived);

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
		case Constants.RESP_DELETE_CONNECTION:
			if (jainmgcpresponseevent.getReturnCode().getValue() == ReturnCode.UNKNOWN_CALL_ID
					|| jainmgcpresponseevent.getReturnCode().getValue() == ReturnCode.TRANSACTION_EXECUTED_NORMALLY) {
				responseReceived = true;
			}
			break;
		default:
			logger.warn("This RESPONSE is unexpected " + jainmgcpresponseevent);
			DeleteConnectionTest.fail("Incorrect response for DLCX command ");
			break;

		}

	}

}
