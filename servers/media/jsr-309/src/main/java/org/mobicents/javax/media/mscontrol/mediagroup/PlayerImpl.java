package org.mobicents.javax.media.mscontrol.mediagroup;

import jain.protocol.ip.mgcp.JainMgcpCommandEvent;
import jain.protocol.ip.mgcp.JainMgcpEvent;
import jain.protocol.ip.mgcp.JainMgcpResponseEvent;
import jain.protocol.ip.mgcp.message.Constants;
import jain.protocol.ip.mgcp.message.NotificationRequest;
import jain.protocol.ip.mgcp.message.NotificationRequestResponse;
import jain.protocol.ip.mgcp.message.Notify;
import jain.protocol.ip.mgcp.message.parms.ConnectionIdentifier;
import jain.protocol.ip.mgcp.message.parms.EndpointIdentifier;
import jain.protocol.ip.mgcp.message.parms.EventName;
import jain.protocol.ip.mgcp.message.parms.RequestIdentifier;
import jain.protocol.ip.mgcp.message.parms.RequestedAction;
import jain.protocol.ip.mgcp.message.parms.RequestedEvent;
import jain.protocol.ip.mgcp.message.parms.ReturnCode;
import jain.protocol.ip.mgcp.pkg.MgcpEvent;
import jain.protocol.ip.mgcp.pkg.PackageName;

import java.net.URI;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.media.mscontrol.MediaSession;
import javax.media.mscontrol.MsControlException;
import javax.media.mscontrol.mediagroup.MediaGroup;
import javax.media.mscontrol.mediagroup.Player;
import javax.media.mscontrol.mediagroup.PlayerEvent;
import javax.media.mscontrol.resource.Error;
import javax.media.mscontrol.resource.MediaEvent;
import javax.media.mscontrol.resource.MediaEventListener;
import javax.media.mscontrol.resource.Parameters;
import javax.media.mscontrol.resource.RTC;

import org.apache.log4j.Logger;
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
public class PlayerImpl implements Player {

	private static Logger logger = Logger.getLogger(PlayerImpl.class);
	protected MediaGroupImpl mediaGroup = null;
	protected CopyOnWriteArrayList<MediaEventListener<? extends MediaEvent<?>>> mediaEventListenerList = new CopyOnWriteArrayList<MediaEventListener<? extends MediaEvent<?>>>();

	protected MediaSessionImpl mediaSession = null;
	protected MgcpWrapper mgcpWrapper = null;

	protected RequestIdentifier reqId = null;

	protected PlayerState state = PlayerState.IDLE;

	protected PlayerImpl(MediaGroupImpl mediaGroup, MgcpWrapper mgcpWrapper) throws MsControlException {
		this.mediaGroup = mediaGroup;
		this.mediaSession = (MediaSessionImpl) mediaGroup.getMediaSession();
		this.mgcpWrapper = mgcpWrapper;
	}

	public void play(URI[] uris, RTC[] arg1, Parameters arg2) throws MsControlException {

		if (MediaObjectState.JOINED.equals(this.mediaGroup.getState())) {
			Runnable tx = new StartTx(this, uris);
			Provider.submit(tx);
			state = PlayerState.ACTIVE;
		} else {
			throw new MsControlException(this.mediaGroup.getURI() + " Container is not joined to any other container");
		}
	}

	public void play(URI arg0, RTC[] arg1, Parameters arg2) throws MsControlException {
		this.play(new URI[] { arg0 }, arg1, arg2);
	}

	public MediaGroup getContainer() {
		return this.mediaGroup;
	}

	public void stop() {

		if (this.state == PlayerState.ACTIVE) {
			Runnable tx = new StopTx(this);
			Provider.submit(tx);
		}
		state = PlayerState.IDLE;
	}

	public void addListener(MediaEventListener<PlayerEvent> listener) {
		this.mediaEventListenerList.add(listener);
	}

	public void removeListener(MediaEventListener<PlayerEvent> listener) {
		this.mediaEventListenerList.remove(listener);
	}

	public MediaSession getMediaSession() {
		return this.mediaGroup.getMediaSession();
	}

	protected void update(PlayerEvent anEvent) {
		for (MediaEventListener m : mediaEventListenerList) {
			m.onEvent(anEvent);
		}
	}

	private class StopTx implements Runnable {
		private int tx = -1;
		private PlayerImpl player = null;

		StopTx(PlayerImpl player) {
			this.player = player;
		}

		public void run() {
			try {
				this.tx = mgcpWrapper.getUniqueTransactionHandler();
				// TODO do we really care to listen for any event (NTFY) from
				// MMS for this?
				EndpointIdentifier endpointID = new EndpointIdentifier(mediaGroup.getEndpoint(), mgcpWrapper
						.getPeerIp()
						+ ":" + mgcpWrapper.getPeerPort());
				NotificationRequest notificationRequest = new NotificationRequest(this, endpointID, reqId);
				notificationRequest.setSignalRequests(new EventName[] {});
				notificationRequest.setRequestedEvents(new RequestedEvent[] {});
				mgcpWrapper.sendMgcpEvents(new JainMgcpEvent[] { notificationRequest });
				notificationRequest.setTransactionHandle(this.tx);
			} catch (Exception e) {
				logger.error(e);
			}
		}

	}

	private class StartTx implements Runnable, JainMgcpExtendedListener {
		private int tx = -1;

		private PlayerImpl player = null;
		private URI[] files = null;

		private int annCompleted = 0;

		StartTx(PlayerImpl player, URI[] files) {
			this.player = player;
			this.files = files;
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
				ConnectionIdentifier connId = mediaGroup.thisConnId;

				EventName[] signalRequests = new EventName[files.length];
				int count = 0;
				for (URI uri : files) {
					String filePath = uri.toString();
					signalRequests[count] = new EventName(PackageName.Announcement, MgcpEvent.ann.withParm(filePath),
							connId);
					count++;
				}

				notificationRequest.setSignalRequests(signalRequests);

				RequestedAction[] actions = new RequestedAction[] { RequestedAction.NotifyImmediately };

				RequestedEvent[] requestedEvents = {
						new RequestedEvent(new EventName(PackageName.Announcement, MgcpEvent.oc, connId), actions),
						new RequestedEvent(new EventName(PackageName.Announcement, MgcpEvent.of, connId), actions) };

				notificationRequest.setRequestedEvents(requestedEvents);
				notificationRequest.setTransactionHandle(this.tx);

				mgcpWrapper.sendMgcpEvents(new JainMgcpEvent[] { notificationRequest });

			} catch (Exception e) {
				logger.error(e);
				PlayerEventImpl event = new PlayerEventImpl(this.player, Player.ev_PlayComplete, Error.e_System,
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
			PlayerEventImpl event = new PlayerEventImpl(this.player, Player.ev_PlayComplete, Error.e_System,
					"No response from MGW for RQNT");
			update(event);
		}

		public void processMgcpCommandEvent(JainMgcpCommandEvent command) {
			logger.debug(" The NTFY received " + command.toString());
			Notify notify = (Notify) command;
			EventName[] observedEvents = notify.getObservedEvents();
			PlayerEvent event = null;
			for (EventName observedEvent : observedEvents) {
				this.annCompleted++;
				switch (observedEvent.getEventIdentifier().intValue()) {
				case MgcpEvent.REPORT_ON_COMPLETION:
					if (this.annCompleted == files.length) {
						mgcpWrapper.removeListener(notify.getRequestIdentifier());

						state = PlayerState.IDLE;
						event = new PlayerEventImpl(this.player, Player.ev_PlayComplete);
						update(event);
					}
					break;

				case MgcpEvent.REPORT_FAILURE:
					if (this.annCompleted != files.length) {
						// TODO Stop the further playing of all file

					}
					mgcpWrapper.removeListener(notify.getRequestIdentifier());
					event = new PlayerEventImpl(this.player, Player.ev_PlayComplete, Error.e_System,
							"Player failed on Server");
					update(event);
					break;
				}

				NotificationRequestResponse response = new NotificationRequestResponse(notify.getSource(),
						ReturnCode.Transaction_Executed_Normally);
				response.setTransactionHandle(notify.getTransactionHandle());

				mgcpWrapper.sendMgcpEvents(new JainMgcpEvent[] { response });
			}
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

				PlayerEventImpl event = new PlayerEventImpl(this.player, Player.ev_PlayComplete, Error.e_System,
						"RQNT Failed.  Look at logs " + respEvent.getReturnCode().getComment());

				update(event);
				break;

			}
		}

		private void processReqNotificationResponse(NotificationRequestResponse responseEvent) {
			PlayerEvent event = null;
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

				event = new PlayerEventImpl(this.player, Player.ev_PlayComplete, Error.e_ResourceUnavailable,
						"RQNT Failed.  Look at logs " + responseEvent.getReturnCode().getComment());
				update(event);
				break;
			default:
				logger.error(" SOMETHING IS BROKEN = " + responseEvent);
				mgcpWrapper.removeListener(responseEvent.getTransactionHandle());
				mgcpWrapper.removeListener(reqId);

				event = new PlayerEventImpl(this.player, Player.ev_PlayComplete, Error.e_System,
						"RQNT Failed.  Look at logs " + responseEvent.getReturnCode().getComment());

				update(event);

				break;

			}
		}
	}

}
