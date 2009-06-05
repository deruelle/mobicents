package org.mobicents.javax.media.mscontrol.mediagroup;

import jain.protocol.ip.mgcp.JainMgcpCommandEvent;
import jain.protocol.ip.mgcp.JainMgcpEvent;
import jain.protocol.ip.mgcp.JainMgcpResponseEvent;
import jain.protocol.ip.mgcp.message.Constants;
import jain.protocol.ip.mgcp.message.NotificationRequest;
import jain.protocol.ip.mgcp.message.NotificationRequestResponse;
import jain.protocol.ip.mgcp.message.Notify;
import jain.protocol.ip.mgcp.message.NotifyResponse;
import jain.protocol.ip.mgcp.message.parms.ConnectionIdentifier;
import jain.protocol.ip.mgcp.message.parms.EndpointIdentifier;
import jain.protocol.ip.mgcp.message.parms.EventName;
import jain.protocol.ip.mgcp.message.parms.RequestIdentifier;
import jain.protocol.ip.mgcp.message.parms.RequestedAction;
import jain.protocol.ip.mgcp.message.parms.RequestedEvent;
import jain.protocol.ip.mgcp.message.parms.ReturnCode;
import jain.protocol.ip.mgcp.pkg.MgcpEvent;

import java.net.URI;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.media.mscontrol.MediaSession;
import javax.media.mscontrol.MsControlException;
import javax.media.mscontrol.mediagroup.MediaGroup;
import javax.media.mscontrol.mediagroup.Recorder;
import javax.media.mscontrol.mediagroup.RecorderEvent;
import javax.media.mscontrol.resource.Error;
import javax.media.mscontrol.resource.MediaEvent;
import javax.media.mscontrol.resource.MediaEventListener;
import javax.media.mscontrol.resource.Parameters;
import javax.media.mscontrol.resource.RTC;

import org.apache.log4j.Logger;
import org.mobicents.jain.protocol.ip.mgcp.pkg.AUMgcpEvent;
import org.mobicents.jain.protocol.ip.mgcp.pkg.AUPackage;
import org.mobicents.javax.media.mscontrol.MediaObjectState;
import org.mobicents.javax.media.mscontrol.MediaSessionImpl;
import org.mobicents.jsr309.mgcp.MgcpWrapper;
import org.mobicents.jsr309.mgcp.Provider;
import org.mobicents.mgcp.stack.JainMgcpExtendedListener;

/**
 * 
 * @author amit bhayani
 * 
 */
public class RecorderImpl implements Recorder {

	private static Logger logger = Logger.getLogger(RecorderImpl.class);

	protected MediaGroupImpl mediaGroup = null;
	protected CopyOnWriteArrayList<MediaEventListener<? extends MediaEvent<?>>> mediaEventListenerList = new CopyOnWriteArrayList<MediaEventListener<? extends MediaEvent<?>>>();

	protected MediaSessionImpl mediaSession = null;
	protected MgcpWrapper mgcpWrapper = null;

	protected RequestIdentifier reqId = null;

	protected RecorderState state = RecorderState.IDLE;

	public RecorderImpl(MediaGroupImpl mediaGroup, MgcpWrapper mgcpWrapper) {
		this.mediaGroup = mediaGroup;
		this.mediaSession = (MediaSessionImpl) mediaGroup.getMediaSession();
		this.mgcpWrapper = mgcpWrapper;
	}

	public void record(URI streamID, RTC[] rtc, Parameters optargs) throws MsControlException {
		if (MediaObjectState.JOINED.equals(this.mediaGroup.getState())) {
			if (this.state == RecorderState.ACTIVE) {
				throw new MsControlException(this.mediaGroup.getURI() + " Recorder already ACTIVE");
			} else {
				Runnable tx = new StartTx(this, streamID);
				Provider.submit(tx);
			}
			this.state = RecorderState.ACTIVE;
		} else {
			throw new MsControlException(this.mediaGroup.getURI() + " Container is not joined to any other container");
		}
	}

	public MediaGroup getContainer() {
		return this.mediaGroup;
	}

	public boolean stop() {
		if (this.state == RecorderState.ACTIVE) {
			Runnable tx = new StopTx(this);
			Provider.submit(tx);
			return true;
		}
		return false;
	}

	public void addListener(MediaEventListener<RecorderEvent> listener) {
		this.mediaEventListenerList.add(listener);
	}

	public MediaSession getMediaSession() {
		return this.mediaSession;
	}

	public void removeListener(MediaEventListener<RecorderEvent> listener) {
		this.mediaEventListenerList.remove(listener);
	}

	protected void update(RecorderEvent anEvent) {
		for (MediaEventListener m : mediaEventListenerList) {
			m.onEvent(anEvent);
		}
	}

	private class StopTx implements Runnable, JainMgcpExtendedListener {
		private int tx = -1;
		private RecorderImpl recorder = null;

		StopTx(RecorderImpl recorder) {
			this.recorder = recorder;
		}

		public void run() {
			try {
				this.tx = mgcpWrapper.getUniqueTransactionHandler();
				mgcpWrapper.addListnere(this.tx, this);
				mgcpWrapper.addListnere(reqId, this);

				EndpointIdentifier endpointID = new EndpointIdentifier(mediaGroup.getEndpoint(), mgcpWrapper
						.getPeerIp()
						+ ":" + mgcpWrapper.getPeerPort());
				NotificationRequest notificationRequest = new NotificationRequest(this, endpointID, reqId);
				notificationRequest.setNotifiedEntity(mgcpWrapper.getDefaultNotifiedEntity());
				notificationRequest.setTransactionHandle(this.tx);
				mgcpWrapper.sendMgcpEvents(new JainMgcpEvent[] { notificationRequest });

			} catch (Exception e) {
				logger.error(e);
				RecorderEventImpl event = new RecorderEventImpl(this.recorder, Recorder.ev_Record, Error.e_Unknown,
						"Error while sending RQNt " + e.getMessage());
				update(event);
			}
		}

		public void transactionEnded(int arg0) {
			// TODO Auto-generated method stub

		}

		public void transactionRxTimedOut(JainMgcpCommandEvent cmdEvent) {
			logger.error("No response from MGW. Tx timed out for RQNT Tx " + this.tx + " For Command sent "
					+ cmdEvent.toString());
			mgcpWrapper.removeListener(cmdEvent.getTransactionHandle());
			mgcpWrapper.removeListener(reqId);

			RecorderEventImpl event = new RecorderEventImpl(this.recorder, Recorder.ev_Record, Error.e_Unknown,
					"No response from MGW for RQNT");
			update(event);
		}

		public void transactionTxTimedOut(JainMgcpCommandEvent arg0) {
			// TODO Auto-generated method stub

		}

		public void processMgcpCommandEvent(JainMgcpCommandEvent arg0) {
			// TODO Auto-generated method stub

		}

		public void processMgcpResponseEvent(JainMgcpResponseEvent respEvent) {
			switch (respEvent.getObjectIdentifier()) {
			case Constants.RESP_NOTIFICATION_REQUEST:
				processReqNotificationResponse((NotificationRequestResponse) respEvent);
				break;
			default:
				mgcpWrapper.removeListener(respEvent.getTransactionHandle());
				mgcpWrapper.removeListener(reqId);
				logger.warn(" This RESPONSE is unexpected " + respEvent);

				RecorderEventImpl event = new RecorderEventImpl(this.recorder, Recorder.ev_Record, Error.e_Unknown,
						"RQNT Failed.  Look at logs " + respEvent.getReturnCode().getComment());
				update(event);
				break;

			}
		}

		private void processReqNotificationResponse(NotificationRequestResponse responseEvent) {
			RecorderEventImpl event = null;
			ReturnCode returnCode = responseEvent.getReturnCode();

			switch (returnCode.getValue()) {
			case ReturnCode.TRANSACTION_BEING_EXECUTED:
				// do nothing
				if (logger.isDebugEnabled()) {
					logger.debug("Transaction " + this.tx + "is being executed. Response received = " + responseEvent);
				}
				break;
			case ReturnCode.TRANSACTION_EXECUTED_NORMALLY:
				mgcpWrapper.removeListener(responseEvent.getTransactionHandle());
				mgcpWrapper.removeListener(reqId);
				state = RecorderState.IDLE;
				event = new RecorderEventImpl(this.recorder, Recorder.ev_Record, Recorder.q_Stop, null, -1);
				update(event);

				break;
			default:
				logger.error(" SOMETHING IS BROKEN = " + responseEvent);
				mgcpWrapper.removeListener(responseEvent.getTransactionHandle());
				mgcpWrapper.removeListener(reqId);

				event = new RecorderEventImpl(this.recorder, Recorder.ev_Record, Error.e_Unknown,
						"RQNT Failed.  Look at logs " + responseEvent.getReturnCode().getComment());
				update(event);
				break;

			}
		}

	}

	private class StartTx implements Runnable, JainMgcpExtendedListener {
		private int tx = -1;

		private Recorder recorder = null;
		private String file = null;

		StartTx(Recorder recorder, URI uri) {
			this.recorder = recorder;
			this.file = uri.toString();
		}

		public void run() {
			this.tx = mgcpWrapper.getUniqueTransactionHandler();
			try {
				mgcpWrapper.addListnere(this.tx, this);

				EndpointIdentifier endpointID = new EndpointIdentifier(mediaGroup.getEndpoint(), mgcpWrapper
						.getPeerIp()
						+ ":" + mgcpWrapper.getPeerPort());

				reqId = mgcpWrapper.getUniqueRequestIdentifier();
				mgcpWrapper.addListnere(reqId, this);

				NotificationRequest notificationRequest = new NotificationRequest(this, endpointID, reqId);
				notificationRequest.setNotifiedEntity(mgcpWrapper.getDefaultNotifiedEntity());
				ConnectionIdentifier connId = mediaGroup.thisConnId;

				EventName signalRequest = new EventName(AUPackage.AU, AUMgcpEvent.aupr.withParm(this.file), null);

				notificationRequest.setSignalRequests(new EventName[] { signalRequest });

				RequestedAction[] actions = new RequestedAction[] { RequestedAction.NotifyImmediately };

				RequestedEvent[] requestedEvents = {
						new RequestedEvent(new EventName(AUPackage.AU, AUMgcpEvent.auoc, connId), actions),
						new RequestedEvent(new EventName(AUPackage.AU, AUMgcpEvent.auof, connId), actions) };

				//TODO : These are not supported yet on MMS 
				//notificationRequest.setRequestedEvents(requestedEvents);
				notificationRequest.setTransactionHandle(this.tx);

				mgcpWrapper.sendMgcpEvents(new JainMgcpEvent[] { notificationRequest });

			} catch (Exception e) {
				logger.error(e);
				RecorderEventImpl event = new RecorderEventImpl(this.recorder, Recorder.ev_Record, Error.e_Unknown,
						"Error while sending RQNt " + e.getMessage());
				update(event);
			}

		}

		public void transactionEnded(int arg0) {
			if (logger.isDebugEnabled()) {
				logger.debug("Successfully completed Tx = " + arg0);
			}
		}

		public void transactionRxTimedOut(JainMgcpCommandEvent arg0) {
			if (logger.isDebugEnabled()) {
				logger.debug("Couldn't send the Tx = " + arg0);
			}
		}

		public void transactionTxTimedOut(JainMgcpCommandEvent cmdEvent) {
			logger.error("No response from MGW. Tx timed out for RQNT Tx " + this.tx + " For Command sent "
					+ cmdEvent.toString());
			mgcpWrapper.removeListener(cmdEvent.getTransactionHandle());
			mgcpWrapper.removeListener(reqId);

			RecorderEventImpl event = new RecorderEventImpl(this.recorder, Recorder.ev_Record, Error.e_Unknown,
					"No response from MGW for RQNT");
			update(event);
		}

		public void processMgcpCommandEvent(JainMgcpCommandEvent command) {
			logger.debug(" The NTFY received " + command.toString());
			Notify notify = (Notify) command;
			EventName[] observedEvents = notify.getObservedEvents();
			RecorderEvent event = null;
			for (EventName observedEvent : observedEvents) {
				switch (observedEvent.getEventIdentifier().intValue()) {
				case AUMgcpEvent.OPERATION_COMPLETE:
					mgcpWrapper.removeListener(notify.getRequestIdentifier());
					state = RecorderState.IDLE;

					MgcpEvent mgcpEvent = observedEvent.getEventIdentifier();
					String params = mgcpEvent.getParms();

					event = new RecorderEventImpl(this.recorder, Recorder.ev_Record, Recorder.q_Standard, null,
							getInterval(params));

					update(event);
					break;

				case AUMgcpEvent.OPERATION_FAIL:
					mgcpWrapper.removeListener(notify.getRequestIdentifier());
					event = new RecorderEventImpl(this.recorder, Recorder.ev_Record, Error.e_Unknown,
							"Recorder failed on server ");
					update(event);
					break;
				}
			}
			NotifyResponse response = new NotifyResponse(notify.getSource(), ReturnCode.Transaction_Executed_Normally);
			response.setTransactionHandle(notify.getTransactionHandle());

			mgcpWrapper.sendMgcpEvents(new JainMgcpEvent[] { response });
		}

		private int getInterval(String params) {
			// Let us remove all SP and HTAB
			// params.replaceAll("[\\x20]", "");
			// params.replaceAll("[\\x20]", "");
			int inter = -1;
			if (params != null) {
				char[] cArr = params.toCharArray();
				int start = 0;
				int end = 0;
				for (int i = 0; i < cArr.length - 1; i++) {
					if (cArr[i] == 'i' && cArr[i + 1] == 'v') {
						start = i;
						break;
					}
				}// end of for

				boolean decoded = true;
				int count = start + 3;
				String interval = "";
				while (decoded && (count < cArr.length)) {
					char ch = cArr[count];
					if (ch >= '0' && ch <= '9') {
						count++;
						interval = interval + ch;
					} else {
						decoded = false;
					}
				}// while
				try {
					inter = Integer.parseInt(interval);
				} catch (NumberFormatException e) {
					logger.error("Parsing of interval failed ", e);
				}
			}

			return inter;
		}

		public void processMgcpResponseEvent(JainMgcpResponseEvent respEvent) {
			switch (respEvent.getObjectIdentifier()) {
			case Constants.RESP_NOTIFICATION_REQUEST:
				processReqNotificationResponse((NotificationRequestResponse) respEvent);
				break;
			default:
				mgcpWrapper.removeListener(respEvent.getTransactionHandle());
				mgcpWrapper.removeListener(reqId);
				logger.warn(" This RESPONSE is unexpected " + respEvent);

				RecorderEventImpl event = new RecorderEventImpl(this.recorder, Recorder.ev_Record, Error.e_Unknown,
						"RQNT Failed.  Look at logs " + respEvent.getReturnCode().getComment());
				update(event);
				break;

			}
		}

		private void processReqNotificationResponse(NotificationRequestResponse responseEvent) {
			RecorderEvent event = null;
			ReturnCode returnCode = responseEvent.getReturnCode();

			switch (returnCode.getValue()) {
			case ReturnCode.TRANSACTION_BEING_EXECUTED:
				// do nothing
				if (logger.isDebugEnabled()) {
					logger.debug("Transaction " + this.tx + "is being executed. Response received = " + responseEvent);
				}
				break;
			case ReturnCode.TRANSACTION_EXECUTED_NORMALLY:
				mgcpWrapper.removeListener(responseEvent.getTransactionHandle());

				break;
			case ReturnCode.ENDPOINT_INSUFFICIENT_RESOURCES:
				mgcpWrapper.removeListener(responseEvent.getTransactionHandle());
				mgcpWrapper.removeListener(reqId);

				event = new RecorderEventImpl(this.recorder, Recorder.ev_Record, Error.e_ResourceUnavailable,
						"RQNT Failed.  Look at logs " + responseEvent.getReturnCode().getComment());
				update(event);
				break;
			default:
				logger.error(" SOMETHING IS BROKEN = " + responseEvent);
				mgcpWrapper.removeListener(responseEvent.getTransactionHandle());
				mgcpWrapper.removeListener(reqId);

				event = new RecorderEventImpl(this.recorder, Recorder.ev_Record, Error.e_Unknown,
						"RQNT Failed.  Look at logs " + responseEvent.getReturnCode().getComment());

				update(event);
				break;

			}
		}
	}

}
