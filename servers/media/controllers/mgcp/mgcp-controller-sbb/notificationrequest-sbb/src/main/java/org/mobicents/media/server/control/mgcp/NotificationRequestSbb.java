/*
 * EndpointConfigurationSbb.java
 * Mobicents Media Gateway
 *
 * The source code contained in this file is in in the public domain.
 * It can be used in any project or product without prior permission,
 * license or royalty payments. There is  NO WARRANTY OF ANY KIND,
 * EXPRESS, IMPLIED OR STATUTORY, INCLUDING, WITHOUT LIMITATION,
 * THE IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE,
 * AND DATA ACCURACY.  We do not warrant or make any representations
 * regarding the use of the software or the  results thereof, including
 * but not limited to the correctness, accuracy, reliability or
 * usefulness of the software.
 */
package org.mobicents.media.server.control.mgcp;

import jain.protocol.ip.mgcp.JainMgcpEvent;
import jain.protocol.ip.mgcp.message.NotificationRequest;
import jain.protocol.ip.mgcp.message.NotificationRequestResponse;
import jain.protocol.ip.mgcp.message.Notify;
import jain.protocol.ip.mgcp.message.parms.ConnectionIdentifier;
import jain.protocol.ip.mgcp.message.parms.EndpointIdentifier;
import jain.protocol.ip.mgcp.message.parms.EventName;
import jain.protocol.ip.mgcp.message.parms.NotifiedEntity;
import jain.protocol.ip.mgcp.message.parms.RequestIdentifier;
import jain.protocol.ip.mgcp.message.parms.RequestedEvent;
import jain.protocol.ip.mgcp.message.parms.ReturnCode;
import jain.protocol.ip.mgcp.pkg.MgcpEvent;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.slee.ActivityContextInterface;
import javax.slee.CreateException;
import javax.slee.RolledBackContext;
import javax.slee.Sbb;
import javax.slee.SbbContext;
import javax.slee.SbbLocalObject;
import javax.slee.facilities.ActivityContextNamingFacility;

import net.java.slee.resource.mgcp.JainMgcpProvider;

import org.apache.log4j.Logger;
import org.mobicents.mscontrol.MsConnection;
import org.mobicents.mscontrol.MsConnectionEvent;
import org.mobicents.mscontrol.MsNotifyEvent;
import org.mobicents.mscontrol.MsProvider;
import org.mobicents.mscontrol.events.MsEventAction;
import org.mobicents.mscontrol.events.MsEventFactory;
import org.mobicents.mscontrol.events.MsRequestedEvent;
import org.mobicents.mscontrol.events.MsRequestedSignal;
import org.mobicents.mscontrol.events.ann.MsPlayRequestedSignal;
import org.mobicents.mscontrol.events.pkg.MsAnnouncement;
import org.mobicents.slee.resource.media.ratype.MediaRaActivityContextInterfaceFactory;

/**
 * @author amit bhayani
 */
public abstract class NotificationRequestSbb implements Sbb {

	private SbbContext sbbContext;
	private static final Logger logger = Logger.getLogger(NotificationRequestSbb.class);
	private JainMgcpProvider mgcpProvider;

	private MsProvider msProvider;
	private MediaRaActivityContextInterfaceFactory msActivityFactory;

	private ActivityContextNamingFacility activityContextNamingfacility;

	/**
	 * Creates a new instance of CreateConnectionSbb
	 * 
	 */
	public NotificationRequestSbb() {
	}

	public void setSbbContext(SbbContext sbbContext) {
		this.sbbContext = sbbContext;
		try {
			Context ctx = (Context) new InitialContext()
					.lookup("java:comp/env");

			mgcpProvider = (JainMgcpProvider) ctx
					.lookup("slee/resources/jainmgcp/2.0/provider");

			activityContextNamingfacility = (ActivityContextNamingFacility) ctx
					.lookup("slee/facilities/activitycontextnaming");

			msProvider = (MsProvider) ctx
					.lookup("slee/resources/media/1.0/provider");
			msActivityFactory = (MediaRaActivityContextInterfaceFactory) ctx
					.lookup("slee/resources/media/1.0/acifactory");

		} catch (NamingException ne) {
			logger.warn("Could not set SBB context:" + ne.getMessage());
		}
	}

	public void onNotificationRequest(NotificationRequest notificationRequest,
			ActivityContextInterface aci) {

		int txID = notificationRequest.getTransactionHandle();
		// logger.info("--> RQNT TX ID = " + txID);

		this.setReceivedTransactionID(notificationRequest.getSource());
		this.setRequestIdentifier(notificationRequest.getRequestIdentifier());

		MsConnection msConnection = null;
		ActivityContextInterface mediaACI = null;

		EndpointIdentifier endpointID = notificationRequest
				.getEndpointIdentifier();
		this.setEndpointIdentifier(endpointID);

		NotifiedEntity notifiedEntity = notificationRequest.getNotifiedEntity();
		if (notifiedEntity == null) {
			logger.warn("NotifiedEntity is null, NOTIFY request will fail");
		}

		this.setNotifiedEntity(notifiedEntity);

		EventName[] eventNames = notificationRequest.getSignalRequests();

		RequestedEvent[] requestedEvents = notificationRequest
				.getRequestedEvents();

		this.setRequestedEvents(requestedEvents);

		for (EventName event : eventNames) {

			ConnectionIdentifier connectionIdentifier = event
					.getConnectionIdentifier();

			if (connectionIdentifier != null) {

				// logger.info("The size of activityContextNamingfacility is growing >>> "
				// + ((ActivityContextNamingFacilityImpl)
				// activityContextNamingfacility).getBindings().size());

				String tmpConnectionIdentifier = connectionIdentifier
						.toString();

				mediaACI = activityContextNamingfacility
						.lookup(tmpConnectionIdentifier);

				if (mediaACI == null) {
					logger
							.warn("The MediaActivity doesn't exist for connectionIdentifier = "
									+ tmpConnectionIdentifier);
					
					sendResponse(txID, ReturnCode.Incorrect_Connection_ID);
					return;
				}

				// if (logger.isDebugEnabled()) {
				// logger.debug("Lookeup the ActivityContextInterface = "
				// + mediaACI + " to ConnectionIdentifier = "
				// + tmpConnectionIdentifier);
				// }

				SbbLocalObject sbbObjectLocalObject = sbbContext
						.getSbbLocalObject();
				mediaACI.attach(sbbObjectLocalObject);
				msConnection = (MsConnection) mediaACI.getActivity();
			}

			MgcpEvent mgcpEvent = event.getEventIdentifier();

			switch (mgcpEvent.intValue()) {

			case MgcpEvent.PLAY_AN_ANNOUNCEMENT:

				String announcementUrl = mgcpEvent.getParms();

				if (msConnection != null) {
					String endpoint = msConnection.getEndpoint().getLocalName();

					MsEventFactory eventFactory = msProvider.getEventFactory();

					MsPlayRequestedSignal play = null;
					play = (MsPlayRequestedSignal) eventFactory
							.createRequestedSignal(MsAnnouncement.PLAY);
					play.setURL(announcementUrl);

					MsRequestedEvent onCompleted = null;
					MsRequestedEvent onFailed = null;

					onCompleted = eventFactory
							.createRequestedEvent(MsAnnouncement.COMPLETED);
					onCompleted.setEventAction(MsEventAction.NOTIFY);

					onFailed = eventFactory
							.createRequestedEvent(MsAnnouncement.FAILED);
					onFailed.setEventAction(MsEventAction.NOTIFY);

					MsRequestedSignal[] requestedSignals = new MsRequestedSignal[] { play };
					MsRequestedEvent[] msrequestedEvents = new MsRequestedEvent[] {
							onCompleted, onFailed };

					msConnection.getEndpoint().execute(requestedSignals,
							msrequestedEvents, msConnection);

					// MsSignalGenerator generator =
					// msProvider.getSignalGenerator(endpoint);
					// try {
					// ActivityContextInterface generatorActivity =
					// msActivityFactory
					// .getActivityContextInterface(generator);
					// generatorActivity.attach(sbbContext.getSbbLocalObject());
					// generator.apply(EventID.PLAY, msConnection, new String[]
					// { announcementUrl });
					//
					// } catch (UnrecognizedActivityException e) {
					// e.printStackTrace();
					// }
				}

				break;
			default:
				// TODO : Send error about this Event not supported

				break;

			}

		} // End of for loop

		sendResponse(txID, ReturnCode.Transaction_Executed_Normally);
	}

	public void onAnnouncementComplete(MsNotifyEvent evt,
			ActivityContextInterface aci) {
		if (logger.isDebugEnabled()) {
			logger.debug("onAnnouncementComplete");
		}
		RequestedEvent[] requestedEvents = this.getRequestedEvents();

		for (RequestedEvent requestedEvent : requestedEvents) {

			EventName eventName = requestedEvent.getEventName();

			if (eventName.getEventIdentifier().intValue() == MgcpEvent.REPORT_ON_COMPLETION) {
				sendNotify(new EventName[] { eventName });
			}
		}
	}

	public void onConnectionFailed(MsConnectionEvent evt,
			ActivityContextInterface aci) {
		if (logger.isDebugEnabled()) {
			logger.debug("onConnectionTransactionFailed");
		}
		this.reportFailure();
	}

	public void onAnnouncementFailed(MsNotifyEvent evt,
			ActivityContextInterface aci) {
		if (logger.isDebugEnabled()) {
			logger.debug("onAnnouncementFailed");
		}
		this.reportFailure();
	}

	private void reportFailure() {
		RequestedEvent[] requestedEvents = this.getRequestedEvents();

		for (RequestedEvent requestedEvent : requestedEvents) {

			EventName eventName = requestedEvent.getEventName();

			if (eventName.getEventIdentifier().intValue() == MgcpEvent.REPORT_FAILURE) {
				sendNotify(new EventName[] { eventName });
			}
		}
	}

	private void sendNotify(EventName[] eventNames) {
		Notify notify = new Notify(this.getReceivedTransactionID(), this
				.getEndpointIdentifier(), this.getRequestIdentifier(),
				eventNames);

		notify.setTransactionHandle(mgcpProvider.getUniqueTransactionHandler());
		notify.setNotifiedEntity(this.getNotifiedEntity());

		JainMgcpEvent[] events = { notify };
		mgcpProvider.sendMgcpEvents(events);

	}

	private void sendResponse(int txID, ReturnCode reason) {
		NotificationRequestResponse response = new NotificationRequestResponse(
				this.getReceivedTransactionID(), reason);
		response.setTransactionHandle(txID);
		//logger.info("<-- TX ID = " + txID + ": " + response.getReturnCode());
		mgcpProvider.sendMgcpEvents(new JainMgcpEvent[] { response });
	}

	public void unsetSbbContext() {
	}

	public void sbbCreate() throws CreateException {
	}

	public void sbbPostCreate() throws CreateException {
	}

	public void sbbActivate() {
	}

	public void sbbPassivate() {
	}

	public void sbbLoad() {
	}

	public void sbbStore() {
	}

	public void sbbRemove() {
	}

	public void sbbExceptionThrown(Exception exception, Object object,
			ActivityContextInterface activityContextInterface) {
		logger.error("Runtime exception thrown in RQNT TxId = ");
	}

	public void sbbRolledBack(RolledBackContext rolledBackContext) {
		logger.error("Tx rolled back in RQNT TxId = ");
	}

	public abstract int getTxId();

	public abstract void setTxId(int txId);

	public abstract Object getReceivedTransactionID();

	public abstract void setReceivedTransactionID(Object receivedTransactionID);

	public abstract RequestIdentifier getRequestIdentifier();

	public abstract void setRequestIdentifier(
			RequestIdentifier requestIdentifier);

	public abstract RequestedEvent[] getRequestedEvents();

	public abstract void setRequestedEvents(RequestedEvent[] requestedEvents);

	public abstract void setEndpointIdentifier(
			EndpointIdentifier endpointIdentifier);

	public abstract EndpointIdentifier getEndpointIdentifier();

	public abstract void setNotifiedEntity(NotifiedEntity notifiedEntity);

	public abstract NotifiedEntity getNotifiedEntity();

}
