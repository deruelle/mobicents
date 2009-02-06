package org.mobicents.mgcp.stack.test.transactionretransmisson;

import jain.protocol.ip.mgcp.JainMgcpCommandEvent;
import jain.protocol.ip.mgcp.JainMgcpEvent;
import jain.protocol.ip.mgcp.JainMgcpResponseEvent;
import jain.protocol.ip.mgcp.message.Constants;
import jain.protocol.ip.mgcp.message.CreateConnection;
import jain.protocol.ip.mgcp.message.CreateConnectionResponse;
import jain.protocol.ip.mgcp.message.DeleteConnectionResponse;
import jain.protocol.ip.mgcp.message.ModifyConnectionResponse;
import jain.protocol.ip.mgcp.message.NotificationRequestResponse;
import jain.protocol.ip.mgcp.message.NotifyResponse;
import jain.protocol.ip.mgcp.message.parms.CallIdentifier;
import jain.protocol.ip.mgcp.message.parms.ConnectionIdentifier;
import jain.protocol.ip.mgcp.message.parms.EndpointIdentifier;
import jain.protocol.ip.mgcp.message.parms.ReturnCode;

import java.util.TooManyListenersException;

import org.apache.log4j.Logger;
import org.mobicents.mgcp.stack.JainMgcpExtendedListener;
import org.mobicents.mgcp.stack.JainMgcpStackProviderImpl;

public class MGW implements JainMgcpExtendedListener {

	private static Logger logger = Logger.getLogger(MGW.class);
	private boolean finalResponseSent = false;
	private boolean provisionalResponseSent = false;
	private String command;

	JainMgcpStackProviderImpl mgwProvider;

	public MGW(JainMgcpStackProviderImpl mgwProvider) {
		this.mgwProvider = mgwProvider;

		try {
			this.mgwProvider.addJainMgcpListener(this);
		} catch (TooManyListenersException e) {
			e.printStackTrace();
			TxRetransmissionTest.fail("Unexpected Exception");
		}
	}

	public void checkState() {
		TxRetransmissionTest.assertTrue("Expect to sent Provisional " + command
				+ " Response", provisionalResponseSent);
		TxRetransmissionTest.assertTrue("Expect to sent Final " + command
				+ " Response", finalResponseSent);
	}

	public void transactionEnded(int handle) {
		logger.info("transactionEnded " + handle);

	}

	public void transactionRxTimedOut(JainMgcpCommandEvent jainMgcpCommandEvent) {
		logger.info("transactionRxTimedOut " + jainMgcpCommandEvent);

	}

	public void transactionTxTimedOut(JainMgcpCommandEvent jainMgcpCommandEvent) {
		logger.info("transactionTxTimedOut " + jainMgcpCommandEvent);

	}

	public void processMgcpCommandEvent(
			JainMgcpCommandEvent jainmgcpcommandevent) {
		logger.info("processMgcpCommandEvent \n" + jainmgcpcommandevent);

		switch (jainmgcpcommandevent.getObjectIdentifier()) {
		case Constants.CMD_CREATE_CONNECTION:

			String identifier = ((CallIdentifier) mgwProvider
					.getUniqueCallIdentifier()).toString();
			ConnectionIdentifier connectionIdentifier = new ConnectionIdentifier(
					identifier);

			CreateConnectionResponse createConnectionResponse = new CreateConnectionResponse(
					jainmgcpcommandevent.getSource(),
					ReturnCode.Transaction_Executed_Normally,
					connectionIdentifier);

			createConnectionResponse.setTransactionHandle(jainmgcpcommandevent
					.getTransactionHandle());
			CreateConnection cc = (CreateConnection) jainmgcpcommandevent;
			EndpointIdentifier wildcard = cc.getEndpointIdentifier();
			String endpointName = wildcard.getLocalEndpointName();
			if (endpointName.indexOf("$") != -1) {
				endpointName = endpointName.substring(0, endpointName
						.indexOf("$"));
			}
			EndpointIdentifier specific = new EndpointIdentifier(endpointName
					+ "test-1", wildcard.getDomainName());
			createConnectionResponse.setSpecificEndpointIdentifier(specific);
			// Let us sleep for 4.5 Sec which will fire the CRCX command again
			// from CA.
			sleep();

			mgwProvider
					.sendMgcpEvents(new JainMgcpEvent[] { createConnectionResponse });

			finalResponseSent = true;

			break;

		case Constants.CMD_DELETE_CONNECTION:

			DeleteConnectionResponse deleteConnectionResponse = new DeleteConnectionResponse(
					jainmgcpcommandevent.getSource(),
					ReturnCode.Transaction_Executed_Normally);

			deleteConnectionResponse.setTransactionHandle(jainmgcpcommandevent
					.getTransactionHandle());
			sleep();
			mgwProvider
					.sendMgcpEvents(new JainMgcpEvent[] { deleteConnectionResponse });

			finalResponseSent = true;

			break;

		case Constants.CMD_MODIFY_CONNECTION:

			ModifyConnectionResponse modifyConnectionResponse = new ModifyConnectionResponse(
					jainmgcpcommandevent.getSource(),
					ReturnCode.Transaction_Executed_Normally);
			modifyConnectionResponse.setTransactionHandle(jainmgcpcommandevent
					.getTransactionHandle());
			sleep();
			mgwProvider
					.sendMgcpEvents(new JainMgcpEvent[] { modifyConnectionResponse });
			finalResponseSent = true;
			break;

		case Constants.CMD_NOTIFICATION_REQUEST:

			NotificationRequestResponse notificationRequestResponse = new NotificationRequestResponse(
					jainmgcpcommandevent.getSource(),
					ReturnCode.Transaction_Executed_Normally);
			notificationRequestResponse
					.setTransactionHandle(jainmgcpcommandevent
							.getTransactionHandle());
			sleep();
			mgwProvider
					.sendMgcpEvents(new JainMgcpEvent[] { notificationRequestResponse });
			finalResponseSent = true;
			break;

		case Constants.CMD_NOTIFY:

			NotifyResponse notifyResponse = new NotifyResponse(
					jainmgcpcommandevent.getSource(),
					ReturnCode.Transaction_Executed_Normally);
			notifyResponse.setTransactionHandle(jainmgcpcommandevent
					.getTransactionHandle());
			sleep();
			mgwProvider.sendMgcpEvents(new JainMgcpEvent[] { notifyResponse });
			finalResponseSent = true;
			break;

		default:
			logger.warn("This REQUEST is unexpected " + jainmgcpcommandevent);
			break;

		}

	}

	public void processMgcpResponseEvent(
			JainMgcpResponseEvent jainmgcpresponseevent) {
		logger.info("processMgcpResponseEvent " + jainmgcpresponseevent);

	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	private void sleep() {
		try {
			Thread.sleep(4500);
			// Assuming that stack must have sent provisional response. The
			// listener is never called for re-transmission of command
			provisionalResponseSent = true;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
