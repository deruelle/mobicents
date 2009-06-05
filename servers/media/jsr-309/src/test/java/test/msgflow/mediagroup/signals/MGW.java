package test.msgflow.mediagroup.signals;

import jain.protocol.ip.mgcp.JainMgcpCommandEvent;
import jain.protocol.ip.mgcp.JainMgcpEvent;
import jain.protocol.ip.mgcp.JainMgcpResponseEvent;
import jain.protocol.ip.mgcp.message.Constants;
import jain.protocol.ip.mgcp.message.CreateConnection;
import jain.protocol.ip.mgcp.message.CreateConnectionResponse;
import jain.protocol.ip.mgcp.message.DeleteConnectionResponse;
import jain.protocol.ip.mgcp.message.NotificationRequest;
import jain.protocol.ip.mgcp.message.NotificationRequestResponse;
import jain.protocol.ip.mgcp.message.Notify;
import jain.protocol.ip.mgcp.message.parms.CallIdentifier;
import jain.protocol.ip.mgcp.message.parms.ConnectionDescriptor;
import jain.protocol.ip.mgcp.message.parms.ConnectionIdentifier;
import jain.protocol.ip.mgcp.message.parms.EndpointIdentifier;
import jain.protocol.ip.mgcp.message.parms.EventName;
import jain.protocol.ip.mgcp.message.parms.RequestIdentifier;
import jain.protocol.ip.mgcp.message.parms.RequestedEvent;
import jain.protocol.ip.mgcp.message.parms.ReturnCode;
import jain.protocol.ip.mgcp.pkg.MgcpEvent;

import java.util.TooManyListenersException;

import org.apache.log4j.Logger;
import org.mobicents.mgcp.stack.JainMgcpExtendedListener;
import org.mobicents.mgcp.stack.JainMgcpStackProviderImpl;

/**
 * 
 * @author amit bhayani
 * 
 */
public class MGW implements JainMgcpExtendedListener {

	private static Logger logger = Logger.getLogger(MGW.class);
	private volatile boolean responseSent = false;
	private volatile boolean sendNtfy = true;

	JainMgcpStackProviderImpl mgwProvider;

	public MGW(JainMgcpStackProviderImpl mgwProvider) {
		this.mgwProvider = mgwProvider;
		try {
			this.mgwProvider.addJainMgcpListener(this);
		} catch (TooManyListenersException e) {
			e.printStackTrace();
			SignalDetectorTest.fail("Unexpected Exception");
		}
	}

	public void checkState() {
		SignalDetectorTest.assertTrue("Expect to sent CRCX Response", responseSent);
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
			CreateConnection createConnection = (CreateConnection) jainmgcpcommandevent;

			String identifier = ((CallIdentifier) mgwProvider.getUniqueCallIdentifier()).toString();
			ConnectionIdentifier connectionIdentifier = new ConnectionIdentifier(identifier);

			CreateConnectionResponse responseCRCX = new CreateConnectionResponse(jainmgcpcommandevent.getSource(),
					ReturnCode.Transaction_Executed_Normally, connectionIdentifier);

			responseCRCX.setTransactionHandle(jainmgcpcommandevent.getTransactionHandle());
			try {
				// FIXME: we asume there is wildcard - "any of"
				EndpointIdentifier wildcard = createConnection.getEndpointIdentifier();
				EndpointIdentifier specific = new EndpointIdentifier(wildcard.getLocalEndpointName().replace("$",
						"test-1"), wildcard.getDomainName());
				responseCRCX.setSpecificEndpointIdentifier(specific);
			} catch (Exception e) {
				e.printStackTrace();
			}

			String LOCAL_SDP = "v=0\n" + "m=audio 1234 RTP/AVP  0 \n" + "c=IN IP4 192.168.145.2\n"
					+ "a=rtpmap:0 PCMU/8000\n";

			ConnectionDescriptor localConnectionDescriptor = new ConnectionDescriptor(LOCAL_SDP);
			responseCRCX.setLocalConnectionDescriptor(localConnectionDescriptor);

			EndpointIdentifier secondEndpointId = createConnection.getSecondEndpointIdentifier();
			if (secondEndpointId != null) {
				// We assume its wildcard - "any of"
				EndpointIdentifier secondId = new EndpointIdentifier(secondEndpointId.getLocalEndpointName().replace(
						"$", "test-2"), secondEndpointId.getDomainName());
				responseCRCX.setSecondEndpointIdentifier(secondId);

				String secondIdentifier = ((CallIdentifier) mgwProvider.getUniqueCallIdentifier()).toString();
				ConnectionIdentifier secondcondentifier = new ConnectionIdentifier(secondIdentifier);

				responseCRCX.setSecondConnectionIdentifier(secondcondentifier);
			}
			mgwProvider.sendMgcpEvents(new JainMgcpEvent[] { responseCRCX });

			responseSent = true;

			break;

		case Constants.CMD_DELETE_CONNECTION:
			DeleteConnectionResponse responseDLCX = new DeleteConnectionResponse(jainmgcpcommandevent.getSource(),
					ReturnCode.Transaction_Executed_Normally);
			responseDLCX.setTransactionHandle(jainmgcpcommandevent.getTransactionHandle());
			mgwProvider.sendMgcpEvents(new JainMgcpEvent[] { responseDLCX });
			responseSent = true;
			break;

		case Constants.CMD_NOTIFICATION_REQUEST:
			NotificationRequest notificationRequest = (NotificationRequest) jainmgcpcommandevent;

			NotificationRequestResponse responseRQNT = new NotificationRequestResponse(notificationRequest.getSource(),
					ReturnCode.Transaction_Executed_Normally);
			responseRQNT.setTransactionHandle(notificationRequest.getTransactionHandle());

			mgwProvider.sendMgcpEvents(new JainMgcpEvent[] { responseRQNT });

			EventName[] eventNames = notificationRequest.getSignalRequests();
			RequestedEvent[] requestedEvents = notificationRequest.getRequestedEvents();
			if (eventNames == null && requestedEvents == null) {
				this.sendNtfy = false;
				logger.debug("Received RQNT and signals = 0. This Object = " + this);
				responseSent = true;
			} else {
				logger.debug("Received RQNT and RequestedEvent = " + requestedEvents.length + " This Object = " + this);
				this.sendNtfy = true;
				Runnable tx = new NtfyTx(this, notificationRequest);
				Thread t = new Thread(tx);
				t.start();
			}

			break;
		default:
			logger.warn("This REQUEST is unexpected " + jainmgcpcommandevent);
			break;

		}

	}

	public void processMgcpResponseEvent(JainMgcpResponseEvent jainmgcpresponseevent) {
		logger.info("processMgcpResponseEvent " + jainmgcpresponseevent);

	}

	public boolean getSendNtfy() {
		return this.sendNtfy;
	}

	private class NtfyTx implements Runnable {

		private NotificationRequest notificationRequest = null;
		private MGW mgw = null;

		NtfyTx(MGW mgw, NotificationRequest notificationRequest) {
			this.notificationRequest = notificationRequest;
			this.mgw = mgw;
		}

		public void run() {
			logger.debug("NTFY Tx started and will sleep now for 2 sec ");
			// Let us sleep for 2 secs before sending NTFY
			try {
				Thread.sleep(1000 * 2);
			} catch (InterruptedException e) {
			}

			EndpointIdentifier endpointId = notificationRequest.getEndpointIdentifier();
			RequestIdentifier redId = notificationRequest.getRequestIdentifier();

			RequestedEvent[] requestedEvents = notificationRequest.getRequestedEvents();

			logger.debug("Thread wokeup. Within else block and value of sendNtfy = " + this.mgw.getSendNtfy());
			if (this.mgw.getSendNtfy()) {

				for (RequestedEvent requestedEvent : requestedEvents) {

					EventName eventName = requestedEvent.getEventName();

					// if (eventName.getEventIdentifier().intValue() ==
					// MgcpEvent.MATCH_ANY_DIGIT_WILDCARD) {

					EventName eventDtmf1 = new EventName(eventName.getPackageName(), MgcpEvent.dtmf1, eventName
							.getConnectionIdentifier());

					Notify notify = new Notify(this, endpointId, redId, new EventName[] { eventDtmf1 });
					notify.setTransactionHandle(mgwProvider.getUniqueTransactionHandler());
					mgwProvider.sendMgcpEvents(new JainMgcpEvent[] { notify });
					responseSent = true;
					break;
					// }
				}

			} // if (this.mgw.getSendNtfy())
		}
	}

}