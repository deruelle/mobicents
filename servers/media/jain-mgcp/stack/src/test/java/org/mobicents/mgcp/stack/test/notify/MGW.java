package org.mobicents.mgcp.stack.test.notify;

import jain.protocol.ip.mgcp.JainMgcpCommandEvent;
import jain.protocol.ip.mgcp.JainMgcpEvent;
import jain.protocol.ip.mgcp.JainMgcpResponseEvent;
import jain.protocol.ip.mgcp.message.Constants;
import jain.protocol.ip.mgcp.message.NotifyResponse;
import jain.protocol.ip.mgcp.message.parms.ReturnCode;

import java.util.TooManyListenersException;

import org.apache.log4j.Logger;
import org.mobicents.mgcp.stack.JainMgcpExtendedListener;
import org.mobicents.mgcp.stack.JainMgcpStackProviderImpl;

public class MGW implements JainMgcpExtendedListener {

	private static Logger logger = Logger.getLogger(MGW.class);
	private boolean responseSent = false;

	JainMgcpStackProviderImpl mgwProvider;

	public MGW(JainMgcpStackProviderImpl mgwProvider) {
		this.mgwProvider = mgwProvider;
		try {
			this.mgwProvider.addJainMgcpListener(this);
		} catch (TooManyListenersException e) {
			e.printStackTrace();
			NotifyTest.fail("Unexpected Exception");
		}
	}

	public void checkState() {
		NotifyTest.assertTrue("Expect to sent NTFY Response", responseSent);
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
		case Constants.CMD_NOTIFY:			

			NotifyResponse response = new NotifyResponse(jainmgcpcommandevent.getSource(),
					ReturnCode.Transaction_Executed_Normally);
			response.setTransactionHandle(jainmgcpcommandevent.getTransactionHandle());
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
