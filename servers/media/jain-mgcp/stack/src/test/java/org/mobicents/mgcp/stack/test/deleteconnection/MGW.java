package org.mobicents.mgcp.stack.test.deleteconnection;

import jain.protocol.ip.mgcp.JainMgcpCommandEvent;
import jain.protocol.ip.mgcp.JainMgcpEvent;
import jain.protocol.ip.mgcp.JainMgcpResponseEvent;
import jain.protocol.ip.mgcp.message.Constants;
import jain.protocol.ip.mgcp.message.DeleteConnectionResponse;
import jain.protocol.ip.mgcp.message.parms.ReturnCode;

import java.util.TooManyListenersException;

import org.apache.log4j.Logger;
import org.mobicents.mgcp.stack.JainMgcpExtendedListener;
import org.mobicents.mgcp.stack.JainMgcpStackProviderImpl;

public class MGW implements JainMgcpExtendedListener {

	private static Logger logger = Logger.getLogger(MGW.class);
	private boolean responseSent = false;

	JainMgcpStackProviderImpl mgwProvider;
	private boolean sendFailedResponse = false;

	public MGW(JainMgcpStackProviderImpl mgwProvider, boolean sendFailedResponse) {
		this(mgwProvider);
		this.sendFailedResponse = sendFailedResponse;
	}

	public MGW(JainMgcpStackProviderImpl mgwProvider) {
		this.mgwProvider = mgwProvider;
		try {
			this.mgwProvider.addJainMgcpListener(this);
		} catch (TooManyListenersException e) {
			e.printStackTrace();
			DeleteConnectionTest.fail("Unexpected Exception");
		}
	}

	public void checkState() {
		DeleteConnectionTest.assertTrue("Expect to sent DLCX Response", responseSent);
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

		switch (jainmgcpcommandevent.getObjectIdentifier()) {
		case Constants.CMD_DELETE_CONNECTION:

			DeleteConnectionResponse response = null;
			if (this.sendFailedResponse) {
				response = new DeleteConnectionResponse(jainmgcpcommandevent.getSource(), ReturnCode.Unknown_Call_ID);
				response.setTransactionHandle(jainmgcpcommandevent.getTransactionHandle());
			} else {
				response = new DeleteConnectionResponse(jainmgcpcommandevent.getSource(),
						ReturnCode.Transaction_Executed_Normally);

				response.setTransactionHandle(jainmgcpcommandevent.getTransactionHandle());
			}
			mgwProvider.sendMgcpEvents(new JainMgcpEvent[] { response });

			responseSent = true;

			break;
		default:
			logger.warn("This REQUEST is unexpected " + jainmgcpcommandevent);
			break;

		}

	}

	public void processMgcpResponseEvent(JainMgcpResponseEvent jainmgcpresponseevent) {
		logger.info("processMgcpResponseEvent " + jainmgcpresponseevent);

	}

}
