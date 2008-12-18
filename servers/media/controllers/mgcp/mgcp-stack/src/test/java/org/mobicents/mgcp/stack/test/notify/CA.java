package org.mobicents.mgcp.stack.test.notify;

import jain.protocol.ip.mgcp.JainMgcpCommandEvent;
import jain.protocol.ip.mgcp.JainMgcpEvent;
import jain.protocol.ip.mgcp.JainMgcpResponseEvent;
import jain.protocol.ip.mgcp.message.Constants;
import jain.protocol.ip.mgcp.message.Notify;
import jain.protocol.ip.mgcp.message.parms.ConnectionIdentifier;
import jain.protocol.ip.mgcp.message.parms.EndpointIdentifier;
import jain.protocol.ip.mgcp.message.parms.EventName;
import jain.protocol.ip.mgcp.message.parms.NotifiedEntity;
import jain.protocol.ip.mgcp.pkg.MgcpEvent;
import jain.protocol.ip.mgcp.pkg.PackageName;

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

	public void sendNotify() {

		try {
			caProvider.addJainMgcpListener(this);

			EndpointIdentifier endpointID = new EndpointIdentifier("media/trunk/Announcement/enp-1", "127.0.0.1:"
					+ mgStack);

			Notify notify = new Notify(this, endpointID, caProvider.getUniqueRequestIdentifier(),
					new EventName[] { new EventName(PackageName.Announcement, MgcpEvent.oc, new ConnectionIdentifier(
							"1")) });
			notify.setTransactionHandle(caProvider.getUniqueTransactionHandler());

			// TODO We are forced to set the NotifiedEntity, but this should
			// happen automatically. Fix this in MGCP Stack
			NotifiedEntity notifiedEntity = new NotifiedEntity("127.0.0.1", "127.0.0.1", mgStack);
			notify.setNotifiedEntity(notifiedEntity);

			caProvider.sendMgcpEvents(new JainMgcpEvent[] { notify });

			logger.debug(" Notify command sent for TxId " + notify.getTransactionHandle());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			NotifyTest.fail("Unexpected Exception");
		}
	}

	public void checkState() {
		NotifyTest.assertTrue("Expect to receive NTFY Response", responseReceived);

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
		case Constants.RESP_NOTIFY:
			responseReceived = true;
			break;
		default:
			logger.warn("This RESPONSE is unexpected " + jainmgcpresponseevent);
			responseReceived = false;
			break;

		}

	}

}
