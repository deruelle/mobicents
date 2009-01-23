package org.mobicents.mgcp.stack.test.auditendpoint;

import jain.protocol.ip.mgcp.JainMgcpCommandEvent;
import jain.protocol.ip.mgcp.JainMgcpEvent;
import jain.protocol.ip.mgcp.JainMgcpResponseEvent;
import jain.protocol.ip.mgcp.message.AuditEndpoint;
import jain.protocol.ip.mgcp.message.Constants;
import jain.protocol.ip.mgcp.message.parms.EndpointIdentifier;
import jain.protocol.ip.mgcp.message.parms.InfoCode;

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

	public void sendAuditEndpoint() {

		try {
			caProvider.addJainMgcpListener(this);

			EndpointIdentifier endpointID = new EndpointIdentifier("media/trunk/Announcement/enp-1", "127.0.0.1:"
					+ mgStack);
			
			AuditEndpoint auditEndpoint = new AuditEndpoint(this, endpointID, new InfoCode[]{InfoCode.Capabilities});

			auditEndpoint.setTransactionHandle(caProvider.getUniqueTransactionHandler());

			caProvider.sendMgcpEvents(new JainMgcpEvent[] { auditEndpoint });

			logger.debug(" AuditEndpoint command sent for TxId " + auditEndpoint.getTransactionHandle());
		} catch (Exception e) {			
			e.printStackTrace();
			AuditEndpointTest.fail("Unexpected Exception");
		}
	}

	public void checkState() {
		AuditEndpointTest.assertTrue("Expect to receive AUEP Response", responseReceived);

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
		case Constants.RESP_AUDIT_ENDPOINT :
			responseReceived = true;
			break;
		default:
			logger.warn("This RESPONSE is unexpected " + jainmgcpresponseevent);
			break;

		}

	}

}
