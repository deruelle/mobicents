package org.mobicents.mgcp.stack.test.notificationrequest;

import jain.protocol.ip.mgcp.JainMgcpCommandEvent;
import jain.protocol.ip.mgcp.JainMgcpEvent;
import jain.protocol.ip.mgcp.JainMgcpResponseEvent;
import jain.protocol.ip.mgcp.message.Constants;
import jain.protocol.ip.mgcp.message.NotificationRequest;
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

	public void sendNotificationRequest() {

		try {
			caProvider.addJainMgcpListener(this);

			EndpointIdentifier endpointID = new EndpointIdentifier("media/trunk/Announcement/enp4", "127.0.0.1:"
					+ mgStack);

			NotificationRequest notificationRequest = new NotificationRequest(this, endpointID, caProvider
					.getUniqueRequestIdentifier());
			notificationRequest.setTransactionHandle(caProvider.getUniqueTransactionHandler());

			caProvider.sendMgcpEvents(new JainMgcpEvent[] { notificationRequest });

			logger.debug(" NotificationRequest command sent for TxId " + notificationRequest.getTransactionHandle());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			NotificationRequestTest.fail("Unexpected Exception");
		}
	}

	public void checkState() {
		NotificationRequestTest.assertTrue("Expect to receive RQNT Response", responseReceived);

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
		case Constants.RESP_NOTIFICATION_REQUEST:
			if (jainmgcpresponseevent.getReturnCode().getValue() == ReturnCode.PROTOCOL_ERROR
					|| jainmgcpresponseevent.getReturnCode().getValue() == ReturnCode.TRANSACTION_EXECUTED_NORMALLY) {
				responseReceived = true;
			}
			break;
		default:
			logger.warn("This RESPONSE is unexpected " + jainmgcpresponseevent);
			NotificationRequestTest.fail("Incorrect response for RQNT command ");
			responseReceived = false;
			break;

		}

	}

}
