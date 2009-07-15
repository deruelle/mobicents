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

import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.media.mscontrol.MediaErr;
import javax.media.mscontrol.MediaEventListener;
import javax.media.mscontrol.MediaSession;
import javax.media.mscontrol.MsControlException;
import javax.media.mscontrol.Parameters;
import javax.media.mscontrol.Value;
import javax.media.mscontrol.mediagroup.MediaGroup;
import javax.media.mscontrol.mediagroup.Player;
import javax.media.mscontrol.mediagroup.PlayerEvent;
import javax.media.mscontrol.resource.RTC;
import javax.media.mscontrol.resource.ResourceEvent;

import org.apache.log4j.Logger;
import org.mobicents.javax.media.mscontrol.DefaultEventGeneratorFactory;
import org.mobicents.javax.media.mscontrol.MediaConfigImpl;
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
	protected CopyOnWriteArrayList<MediaEventListener<PlayerEvent>> mediaEventListenerList = new CopyOnWriteArrayList<MediaEventListener<PlayerEvent>>();

	protected MediaSessionImpl mediaSession = null;
	protected MgcpWrapper mgcpWrapper = null;
	private MediaConfigImpl config = null;

	protected volatile RequestIdentifier reqId = null;

	protected volatile PlayerState state = PlayerState.IDLE;

	private volatile LinkedList<Runnable> txList = new LinkedList<Runnable>();

	private List<EventName> eveNames = new ArrayList<EventName>();

	protected PlayerImpl(MediaGroupImpl mediaGroup, MgcpWrapper mgcpWrapper, MediaConfigImpl config)
			throws MsControlException {
		this.mediaGroup = mediaGroup;
		this.mediaSession = (MediaSessionImpl) mediaGroup.getMediaSession();
		this.mgcpWrapper = mgcpWrapper;
		this.config = config;

	}

	private void updateState() {
		if (txList.size() == 0) {
			this.state = PlayerState.IDLE;
		} else {
			this.state = PlayerState.ACTIVE;
		}
	}

	private void executeNextTx() {
		Runnable nextTx = txList.poll();
		if (nextTx != null) {
			this.state = PlayerState.ACTIVE;
			Provider.submit(nextTx);
		}
	}

	// Player methods
	public void play(URI[] uris, RTC[] arg1, Parameters params) throws MsControlException {
		for (URI uri : uris) {
			this.play(uri, arg1, params);
		}

	}

	public void play(URI uri, RTC[] arg1, Parameters params) throws MsControlException {
		if (MediaObjectState.JOINED.equals(this.mediaGroup.getState())) {
			if (this.state == PlayerState.ACTIVE) {
				Object obj = null;
				if (params != null && (obj = params.get(BEHAVIOUR_IF_BUSY)) != null) {
					Value action = (Value) obj;
					if (action == Player.QUEUE_IF_BUSY) {
						Runnable tx = new StartTx(this, uri);
						txList.add(tx);
					} else if (action == Player.STOP_IF_BUSY) {
						txList.clear();
						Runnable tx = new StartTx(this, uri);
						txList.add(tx);

						// Stop the Player first
						Runnable tx1 = new StopTx(this);
						Provider.submit(tx1);
					} else if (action == Player.FAIL_IF_BUSY) {
						throw new MsControlException("Player is busy");
					} else {
						logger
								.error("The Value "
										+ action
										+ " is not recognized for Parameter p_IfBusy. It has to be one of Player.v_Queue, Player.v_Stop or Player.v_Fail");
					}
				} else {
					logger
							.warn("The Player is busy and no Parameter BEHAVIOUR_IF_BUSY passed to take necessary action");
				}
			} else {
				Runnable tx = new StartTx(this, uri);
				Provider.submit(tx);
			}
			this.state = PlayerState.ACTIVE;
		} else {
			throw new MsControlException(this.mediaGroup.getURI() + " Container is not joined to any other container");
		}
	}

	// Resource Methods
	public MediaGroup getContainer() {
		return this.mediaGroup;
	}

	public boolean stop() {
		txList.clear();
		if (this.state == PlayerState.ACTIVE) {
			Runnable tx = new StopTx(this);
			Provider.submit(tx);
			return true;
		}
		return false;
	}

	// MediaEventNotifier methods
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
		for (MediaEventListener<PlayerEvent> m : mediaEventListenerList) {
			m.onEvent(anEvent);
		}
	}

	private class StopTx implements Runnable, JainMgcpExtendedListener {
		private int tx = -1;
		private PlayerImpl player = null;

		StopTx(PlayerImpl player) {
			this.player = player;
		}

		// TODO : This will stop all the active Signals and Event Detection! We
		// just want Player to Stop
		public void run() {
			try {
				this.tx = mgcpWrapper.getUniqueTransactionHandler();
				mgcpWrapper.addListnere(this.tx, this);
				mgcpWrapper.addListnere(reqId, this);

				EndpointIdentifier endpointID = new EndpointIdentifier(mediaGroup.getEndpoint(), mgcpWrapper
						.getPeerIp()
						+ ":" + mgcpWrapper.getPeerPort());
				NotificationRequest notificationRequest = new NotificationRequest(this, endpointID, reqId);

				notificationRequest.setTransactionHandle(this.tx);
				notificationRequest.setNotifiedEntity(mgcpWrapper.getDefaultNotifiedEntity());
				mgcpWrapper.sendMgcpEvents(new JainMgcpEvent[] { notificationRequest });

			} catch (Exception e) {
				logger.error(e);
				PlayerEventImpl event = new PlayerEventImpl(this.player, PlayerEvent.PLAY_COMPLETED, false,
						MediaErr.UNKNOWN_ERROR, "Error " + e.getMessage());
				update(event);
				executeNextTx();
			}
		}

		public void transactionEnded(int arg0) {
			// TODO Auto-generated method stub

		}

		public void transactionRxTimedOut(JainMgcpCommandEvent cmdEvent) {

		}

		public void transactionTxTimedOut(JainMgcpCommandEvent cmdEvent) {
			logger.error("No response from MGW. Tx timed out for RQNT Tx " + this.tx + " For Command sent "
					+ cmdEvent.toString());
			mgcpWrapper.removeListener(cmdEvent.getTransactionHandle());
			mgcpWrapper.removeListener(reqId);
			PlayerEventImpl event = new PlayerEventImpl(this.player, PlayerEvent.PLAY_COMPLETED, false,
					MediaErr.UNKNOWN_ERROR, "No response from MGW for RQNT");
			update(event);
			executeNextTx();
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

				updateState();

				PlayerEventImpl event = new PlayerEventImpl(this.player, PlayerEvent.PLAY_COMPLETED, false,
						MediaErr.UNKNOWN_ERROR, "RQNT Failed.  Look at logs " + respEvent.getReturnCode().getComment());

				update(event);
				executeNextTx();
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
				mgcpWrapper.removeListener(reqId);

				updateState();

				event = new PlayerEventImpl(this.player, PlayerEvent.PLAY_COMPLETED, true, ResourceEvent.STOPPED, null);
				update(event);
				executeNextTx();

				break;
			default:
				logger.error(" SOMETHING IS BROKEN = " + responseEvent);
				mgcpWrapper.removeListener(responseEvent.getTransactionHandle());
				mgcpWrapper.removeListener(reqId);

				updateState();

				event = new PlayerEventImpl(this.player, PlayerEvent.PLAY_COMPLETED, false, MediaErr.UNKNOWN_ERROR,
						"RQNT Failed.  Look at logs " + responseEvent.getReturnCode().getComment());
				update(event);
				executeNextTx();
				break;

			}
		}

	}

	private class StartTx implements Runnable, JainMgcpExtendedListener {
		private int tx = -1;

		private PlayerImpl player = null;
		private URI file = null;

		StartTx(PlayerImpl player, URI file) {
			this.player = player;
			this.file = file;
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

				EventName[] signalRequests = null;

				for (DefaultEventGeneratorFactory genfact : config.getPlayerGeneFactList()) {
					if (genfact.getEventName().compareTo("ann") == 0) {
						String filePath = file.toString();
						eveNames.add(genfact.generateMgcpEvent(filePath, connId));
					} else {
						eveNames.add(genfact.generateMgcpEvent(null, connId));
					}
				}
				signalRequests = new EventName[eveNames.size()];
				eveNames.toArray(signalRequests);

				eveNames.clear();

				notificationRequest.setSignalRequests(signalRequests);

				// Request Event
				RequestedAction[] actions = new RequestedAction[] { RequestedAction.NotifyImmediately };
				for (PlayerEventDetectorFactory detfact : config.getPlayerDetFactList()) {
					eveNames.add(detfact.generateMgcpEvent(null, connId));
				}

				RequestedEvent[] requestedEvents = new RequestedEvent[eveNames.size()];
				for (int i = 0; i < requestedEvents.length; i++) {
					requestedEvents[i] = new RequestedEvent(eveNames.get(i), actions);
				}
				
				eveNames.clear();

				notificationRequest.setRequestedEvents(requestedEvents);
				notificationRequest.setTransactionHandle(this.tx);
				notificationRequest.setNotifiedEntity(mgcpWrapper.getDefaultNotifiedEntity());

				mgcpWrapper.sendMgcpEvents(new JainMgcpEvent[] { notificationRequest });

			} catch (Exception e) {
				logger.error(e);
				updateState();
				PlayerEventImpl event = new PlayerEventImpl(this.player, PlayerEvent.PLAY_COMPLETED, false,
						MediaErr.UNKNOWN_ERROR, "Error while sending RQNt " + e.getMessage());
				update(event);
				executeNextTx();
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

			updateState();

			PlayerEventImpl event = new PlayerEventImpl(this.player, PlayerEvent.PLAY_COMPLETED, false,
					MediaErr.UNKNOWN_ERROR, "No response from MGW for RQNT");
			update(event);
			executeNextTx();
		}

		public void processMgcpCommandEvent(JainMgcpCommandEvent command) {
			logger.debug(" The NTFY received " + command.toString());

			PlayerEventImpl event = null;

			switch (command.getObjectIdentifier()) {
			case Constants.CMD_NOTIFY:

				Notify notify = (Notify) command;
				EventName[] observedEvents = notify.getObservedEvents();

				mgcpWrapper.removeListener(notify.getRequestIdentifier());

				updateState();

				for (EventName observedEvent : observedEvents) {
					for (PlayerEventDetectorFactory detfact : config.getPlayerDetFactList()) {
						if ((detfact.getPkgName().compareTo(observedEvent.getPackageName().toString()) == 0)
								&& (detfact.getEventName().compareTo(observedEvent.getEventIdentifier().getName()) == 0)) {

							event = (PlayerEventImpl) detfact.generateMediaEvent();
							event.setPlayer(this.player);
							event.setSuccessful(true);
							event.setQualifier(ResourceEvent.STANDARD_COMPLETION);
							
							update(event);
							executeNextTx();

						}
					}
				}
				NotifyResponse response = new NotifyResponse(notify.getSource(),
						ReturnCode.Transaction_Executed_Normally);
				response.setTransactionHandle(notify.getTransactionHandle());

				mgcpWrapper.sendMgcpEvents(new JainMgcpEvent[] { response });
				break;

			default:
				logger.error("Expected NTFY cmd. Received " + command);
				updateState();
				event = new PlayerEventImpl(this.player, PlayerEvent.PLAY_COMPLETED, false, MediaErr.UNKNOWN_ERROR,
						"Player failed on Server");
				update(event);
				executeNextTx();
				break;
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
				updateState();

				logger.warn(" This RESPONSE is unexpected " + respEvent);

				PlayerEventImpl event = new PlayerEventImpl(this.player, PlayerEvent.PLAY_COMPLETED, false,
						MediaErr.UNKNOWN_ERROR, "RQNT Failed.  Look at logs " + respEvent.getReturnCode().getComment());

				update(event);
				executeNextTx();
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

				updateState();

				event = new PlayerEventImpl(this.player, PlayerEvent.PLAY_COMPLETED, false,
						MediaErr.RESOURCE_UNAVAILABLE, "RQNT Failed.  Look at logs "
								+ responseEvent.getReturnCode().getComment());
				update(event);
				executeNextTx();
				break;
			default:
				logger.error(" SOMETHING IS BROKEN = " + responseEvent);
				mgcpWrapper.removeListener(responseEvent.getTransactionHandle());
				mgcpWrapper.removeListener(reqId);
				updateState();

				event = new PlayerEventImpl(this.player, PlayerEvent.PLAY_COMPLETED, false, MediaErr.UNKNOWN_ERROR,
						"RQNT Failed.  Look at logs " + responseEvent.getReturnCode().getComment());

				update(event);
				executeNextTx();
				break;

			}
		}
	}

}
