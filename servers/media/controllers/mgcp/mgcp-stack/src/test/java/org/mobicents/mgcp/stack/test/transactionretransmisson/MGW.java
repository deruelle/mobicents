package org.mobicents.mgcp.stack.test.transactionretransmisson;

import java.util.TooManyListenersException;

import jain.protocol.ip.mgcp.JainMgcpCommandEvent;
import jain.protocol.ip.mgcp.JainMgcpEvent;
import jain.protocol.ip.mgcp.JainMgcpResponseEvent;
import jain.protocol.ip.mgcp.message.Constants;
import jain.protocol.ip.mgcp.message.CreateConnectionResponse;
import jain.protocol.ip.mgcp.message.parms.CallIdentifier;
import jain.protocol.ip.mgcp.message.parms.ConnectionIdentifier;
import jain.protocol.ip.mgcp.message.parms.ReturnCode;

import org.apache.log4j.Logger;
import org.mobicents.mgcp.stack.JainMgcpExtendedListener;
import org.mobicents.mgcp.stack.JainMgcpStackProviderImpl;

public class MGW implements JainMgcpExtendedListener {

	private static Logger logger = Logger.getLogger(MGW.class);
	private boolean finalResponseSent = false;
	private boolean provisionalResponseSent = false;

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
		TxRetransmissionTest.assertTrue("Expect to sent Provisional CRCX Response", provisionalResponseSent);
		TxRetransmissionTest.assertTrue("Expect to sent Final CRCX Response", finalResponseSent);
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
		case Constants.CMD_CREATE_CONNECTION:

			String identifier = ((CallIdentifier) mgwProvider.getUniqueCallIdentifier()).toString();
			ConnectionIdentifier connectionIdentifier = new ConnectionIdentifier(identifier);

			CreateConnectionResponse response = new CreateConnectionResponse(jainmgcpcommandevent.getSource(),
					ReturnCode.Transaction_Executed_Normally, connectionIdentifier);

			response.setTransactionHandle(jainmgcpcommandevent.getTransactionHandle());

			// Let us sleep for 4.5 Sec which will fire the CRCX command again
			// from CA.

			try {
				Thread.sleep(4500);
				// Assuming that stack must have sent provisional response
				provisionalResponseSent = true;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			mgwProvider.sendMgcpEvents(new JainMgcpEvent[] { response });

			finalResponseSent = true;

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
