package org.mobicents.javax.media.mscontrol.mediagroup.signals;

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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.media.mscontrol.MediaErr;
import javax.media.mscontrol.MediaEventListener;
import javax.media.mscontrol.MediaSession;
import javax.media.mscontrol.MsControlException;
import javax.media.mscontrol.Parameter;
import javax.media.mscontrol.Parameters;
import javax.media.mscontrol.mediagroup.MediaGroup;
import javax.media.mscontrol.mediagroup.signals.SignalDetector;
import javax.media.mscontrol.mediagroup.signals.SignalDetectorEvent;
import javax.media.mscontrol.resource.RTC;
import javax.media.mscontrol.resource.enums.ParameterEnum;

import org.apache.log4j.Logger;
import org.mobicents.javax.media.mscontrol.MediaConfigImpl;
import org.mobicents.javax.media.mscontrol.MediaObjectState;
import org.mobicents.javax.media.mscontrol.MediaSessionImpl;
import org.mobicents.javax.media.mscontrol.mediagroup.MediaGroupImpl;
import org.mobicents.jsr309.mgcp.MgcpWrapper;
import org.mobicents.jsr309.mgcp.Provider;
import org.mobicents.mgcp.stack.JainMgcpExtendedListener;

/**
 * 
 * @author amit bhayani
 * 
 */
public class SignalDetectorImpl implements SignalDetector {

	private static Logger logger = Logger.getLogger(SignalDetectorImpl.class);
	protected CopyOnWriteArrayList<MediaEventListener<SignalDetectorEvent>> mediaEventListenerList = new CopyOnWriteArrayList<MediaEventListener<SignalDetectorEvent>>();
	protected MediaGroupImpl mediaGroup = null;
	protected MediaSessionImpl mediaSession = null;
	protected MgcpWrapper mgcpWrapper = null;
	protected volatile RequestIdentifier reqId = null;

	private MediaConfigImpl config = null;
	private List<EventName> eveNames = new ArrayList<EventName>();

	// TODO : Not really caring about State as of now
	protected volatile SignalDetectorState state = SignalDetectorState.IDLE;

	// TODO : Buffer needs to be implemented
	private List<String> buffer = null;

	public SignalDetectorImpl(MediaGroupImpl mediaGroup, MgcpWrapper mgcpWrapper, MediaConfigImpl config) {
		this.mediaGroup = mediaGroup;
		this.mgcpWrapper = mgcpWrapper;
		this.config = config;
		this.mediaSession = (MediaSessionImpl) mediaGroup.getMediaSession();

		this.buffer = new ArrayList<String>();
	}

	public void flushBuffer() throws MsControlException {
		this.buffer.clear();
	}

	public void receiveSignals(int numSignals, Parameter[] patterns, RTC[] rtc, Parameters optargs)
			throws MsControlException {

		if (MediaObjectState.JOINED.equals(this.mediaGroup.getState())) {
			Runnable tx = new StartTx(this, patterns, optargs);
			Provider.submit(tx);
			this.state = SignalDetectorState.DETECTING;
		} else {
			throw new MsControlException(this.mediaGroup.getURI() + " Container is not joined to any other container");
		}
	}

	public MediaGroup getContainer() {
		return this.mediaGroup;
	}

	public boolean stop() {
		if (this.state == SignalDetectorState.DETECTING) {
			Runnable tx = new StopTx(this);
			Provider.submit(tx);
			return true;
		}
		return false;
	}

	public void addListener(MediaEventListener<SignalDetectorEvent> listener) {
		this.mediaEventListenerList.add(listener);
	}

	public MediaSession getMediaSession() {
		return this.mediaSession;
	}

	public void removeListener(MediaEventListener<SignalDetectorEvent> listener) {
		this.mediaEventListenerList.remove(listener);
	}

	protected void update(SignalDetectorEvent anEvent) {
		for (MediaEventListener<SignalDetectorEvent> m : mediaEventListenerList) {
			m.onEvent(anEvent);
		}
	}

	private class StopTx implements Runnable, JainMgcpExtendedListener {
		private int tx = -1;

		private SignalDetectorImpl detector = null;

		StopTx(SignalDetectorImpl detector) {
			this.detector = detector;
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
			state = SignalDetectorState.IDLE;
		}

		public void processMgcpCommandEvent(JainMgcpCommandEvent jainmgcpcommandevent) {
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
				state = SignalDetectorState.IDLE;
				logger.warn(" This RESPONSE is unexpected " + respEvent);
				break;

			}
		}

		private void processReqNotificationResponse(NotificationRequestResponse responseEvent) {

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
				state = SignalDetectorState.IDLE;
				break;
			default:
				logger.error(" SOMETHING IS BROKEN = " + responseEvent);
				mgcpWrapper.removeListener(responseEvent.getTransactionHandle());
				mgcpWrapper.removeListener(reqId);
				state = SignalDetectorState.IDLE;
				break;

			}
		}

	}

	private class StartTx implements Runnable, JainMgcpExtendedListener {
		/*
		 * NOTE : Assuming that reg-ex is similar to given in MGCP RFC
		 */

		private int tx = -1;

		private SignalDetectorImpl detector = null;
		private Parameter[] patterns = null;
		private Parameters optargs = null;

		String[] regExp = null;
		String digitMap = null;

		String digitDetected = "";

		StartTx(SignalDetectorImpl detector, Parameter[] patterns, Parameters optargs) {
			this.detector = detector;
			this.patterns = patterns;
			this.optargs = optargs;
			boolean first = true;
			if (optargs != null) {

				regExp = new String[31];

				for (Parameter p : optargs.keySet()) {
					ParameterEnum pE = (ParameterEnum) p;
					String regExtmp = (String) optargs.get(p);

					// NOTE : Assuming that reg-ex is similar to given in MGCP
					// RFC
					regExtmp = regExtmp.replaceAll("x", "\\\\d");
					regExtmp = regExtmp.replaceAll("\\*", "\\\\*");
					regExtmp = regExtmp.replaceAll("\\.", "{0,}");

					switch (pE) {
					case SD_PATTERN_0:
						regExp[0] = regExtmp;
						if (logger.isDebugEnabled()) {
							logger.debug("The Reg Exp for SD_PATTERN_0 = " + regExtmp);
						}
						break;
					case SD_PATTERN_1:
						regExp[1] = regExtmp;
						if (logger.isDebugEnabled()) {
							logger.debug("The Reg Exp for SD_PATTERN_1 = " + regExtmp);
						}
						break;
					case SD_PATTERN_2:
						regExp[2] = regExtmp;
						if (logger.isDebugEnabled()) {
							logger.debug("The Reg Exp for SD_PATTERN_2 = " + regExtmp);
						}
						break;
					case SD_PATTERN_3:
						regExp[3] = regExtmp;
						if (logger.isDebugEnabled()) {
							logger.debug("The Reg Exp for SD_PATTERN_3 = " + regExtmp);
						}
						break;
					case SD_PATTERN_4:
						regExp[4] = regExtmp;
						if (logger.isDebugEnabled()) {
							logger.debug("The Reg Exp for SD_PATTERN_4 = " + regExtmp);
						}
						break;
					case SD_PATTERN_5:
						regExp[5] = regExtmp;
						if (logger.isDebugEnabled()) {
							logger.debug("The Reg Exp for SD_PATTERN_5 = " + regExtmp);
						}
						break;
					case SD_PATTERN_6:
						regExp[6] = regExtmp;
						if (logger.isDebugEnabled()) {
							logger.debug("The Reg Exp for SD_PATTERN_6 = " + regExtmp);
						}
						break;
					case SD_PATTERN_7:
						regExp[7] = regExtmp;
						if (logger.isDebugEnabled()) {
							logger.debug("The Reg Exp for SD_PATTERN_7 = " + regExtmp);
						}
						break;
					case SD_PATTERN_8:
						regExp[8] = regExtmp;
						if (logger.isDebugEnabled()) {
							logger.debug("The Reg Exp for SD_PATTERN_8 = " + regExtmp);
						}
						break;
					case SD_PATTERN_9:
						regExp[9] = regExtmp;
						if (logger.isDebugEnabled()) {
							logger.debug("The Reg Exp for SD_PATTERN_9 = " + regExtmp);
						}
						break;
					case SD_PATTERN_10:
						regExp[10] = regExtmp;
						if (logger.isDebugEnabled()) {
							logger.debug("The Reg Exp for SD_PATTERN_10 = " + regExtmp);
						}
						break;
					case SD_PATTERN_11:
						regExp[11] = regExtmp;
						if (logger.isDebugEnabled()) {
							logger.debug("The Reg Exp for SD_PATTERN_11 = " + regExtmp);
						}
						break;
					case SD_PATTERN_12:
						regExp[12] = regExtmp;
						if (logger.isDebugEnabled()) {
							logger.debug("The Reg Exp for SD_PATTERN_12 = " + regExtmp);
						}
						break;
					case SD_PATTERN_13:
						regExp[13] = regExtmp;
						if (logger.isDebugEnabled()) {
							logger.debug("The Reg Exp for SD_PATTERN_13 = " + regExtmp);
						}
						break;
					case SD_PATTERN_14:
						regExp[14] = regExtmp;
						if (logger.isDebugEnabled()) {
							logger.debug("The Reg Exp for SD_PATTERN_14 = " + regExtmp);
						}
						break;
					case SD_PATTERN_15:
						regExp[15] = regExtmp;
						if (logger.isDebugEnabled()) {
							logger.debug("The Reg Exp for SD_PATTERN_15 = " + regExtmp);
						}
						break;
					case SD_PATTERN_16:
						regExp[16] = regExtmp;
						if (logger.isDebugEnabled()) {
							logger.debug("The Reg Exp for SD_PATTERN_16 = " + regExtmp);
						}
						break;
					case SD_PATTERN_17:
						regExp[17] = regExtmp;
						if (logger.isDebugEnabled()) {
							logger.debug("The Reg Exp for SD_PATTERN_17 = " + regExtmp);
						}
						break;
					case SD_PATTERN_18:
						regExp[18] = regExtmp;
						if (logger.isDebugEnabled()) {
							logger.debug("The Reg Exp for SD_PATTERN_18 = " + regExtmp);
						}
						break;
					case SD_PATTERN_19:
						regExp[19] = regExtmp;
						if (logger.isDebugEnabled()) {
							logger.debug("The Reg Exp for SD_PATTERN_19 = " + regExtmp);
						}
						break;
					case SD_PATTERN_20:
						regExp[20] = regExtmp;
						if (logger.isDebugEnabled()) {
							logger.debug("The Reg Exp for SD_PATTERN_20 = " + regExtmp);
						}
						break;
					case SD_PATTERN_21:
						regExp[21] = regExtmp;
						if (logger.isDebugEnabled()) {
							logger.debug("The Reg Exp for SD_PATTERN_21 = " + regExtmp);
						}
						break;
					case SD_PATTERN_22:
						regExp[22] = regExtmp;
						if (logger.isDebugEnabled()) {
							logger.debug("The Reg Exp for SD_PATTERN_22 = " + regExtmp);
						}
						break;
					case SD_PATTERN_23:
						regExp[23] = regExtmp;
						if (logger.isDebugEnabled()) {
							logger.debug("The Reg Exp for SD_PATTERN_23 = " + regExtmp);
						}
						break;
					case SD_PATTERN_24:
						regExp[24] = regExtmp;
						if (logger.isDebugEnabled()) {
							logger.debug("The Reg Exp for SD_PATTERN_24 = " + regExtmp);
						}
						break;
					case SD_PATTERN_25:
						regExp[25] = regExtmp;
						if (logger.isDebugEnabled()) {
							logger.debug("The Reg Exp for SD_PATTERN_25 = " + regExtmp);
						}
						break;
					case SD_PATTERN_26:
						regExp[26] = regExtmp;
						if (logger.isDebugEnabled()) {
							logger.debug("The Reg Exp for SD_PATTERN_26 = " + regExtmp);
						}
						break;
					case SD_PATTERN_27:
						regExp[27] = regExtmp;
						if (logger.isDebugEnabled()) {
							logger.debug("The Reg Exp for SD_PATTERN_27 = " + regExtmp);
						}
						break;
					case SD_PATTERN_28:
						regExp[28] = regExtmp;
						if (logger.isDebugEnabled()) {
							logger.debug("The Reg Exp for SD_PATTERN_28 = " + regExtmp);
						}
						break;
					case SD_PATTERN_29:
						regExp[29] = regExtmp;
						if (logger.isDebugEnabled()) {
							logger.debug("The Reg Exp for SD_PATTERN_29 = " + regExtmp);
						}
						break;
					case SD_PATTERN_30:
						regExp[30] = regExtmp;
						if (logger.isDebugEnabled()) {
							logger.debug("The Reg Exp for SD_PATTERN_30 = " + regExtmp);
						}
						break;
					case SD_PATTERN_31:
						regExp[31] = regExtmp;
						if (logger.isDebugEnabled()) {
							logger.debug("The Reg Exp for SD_PATTERN_31 = " + regExtmp);
						}
						break;

					}

					if (first) {
						first = false;
						digitMap = regExtmp;

					} else {
						digitMap = "|" + regExtmp;
					}
				}
				if (logger.isDebugEnabled()) {
					logger.debug("DigitMap formed is " + digitMap);
				}
			}

			if (digitMap == null) {
				digitMap = "[0-9, A,B,C,D,*,#]";
			}

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

				RequestedAction[] dtmfActions = new RequestedAction[] { RequestedAction.NotifyImmediately };
				// notificationRequest.setDigitMap(new DigitMap(digitMap));

				for (SignalDetectorEventDetectorFactory detfact : config.getSigDeteEveDetFactList()) {
					eveNames.add(detfact.generateMgcpEvent(null, connId));
				}

				RequestedEvent[] requestedEvents = new RequestedEvent[eveNames.size()];
				for (int i = 0; i < requestedEvents.length; i++) {
					requestedEvents[i] = new RequestedEvent(eveNames.get(i), dtmfActions);
				}

				eveNames.clear();
				
				notificationRequest.setRequestedEvents(requestedEvents);
				notificationRequest.setTransactionHandle(this.tx);

				mgcpWrapper.sendMgcpEvents(new JainMgcpEvent[] { notificationRequest });

			} catch (Exception e) {
				logger.error(e);
				state = SignalDetectorState.IDLE;
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
			SignalDetectorEventImpl event = new SignalDetectorEventImpl(this.detector,
					SignalDetectorEvent.SIGNAL_DETECTED, false, MediaErr.UNKNOWN_ERROR, "No response from MGW for RQNT");
			update(event);
		}

		public void processMgcpCommandEvent(JainMgcpCommandEvent command) {
			if (logger.isDebugEnabled()) {
				logger.debug(" The NTFY received " + command.toString());
			}
			Notify notify = (Notify) command;

			NotifyResponse response = new NotifyResponse(notify.getSource(), ReturnCode.Transaction_Executed_Normally);
			response.setTransactionHandle(notify.getTransactionHandle());

			mgcpWrapper.sendMgcpEvents(new JainMgcpEvent[] { response });

			// TODO : For now we de-register for every NTFY command
			mgcpWrapper.removeListener(notify.getRequestIdentifier());

			EventName[] observedEvents = notify.getObservedEvents();
			SignalDetectorEventImpl event = null;
			for (EventName observedEvent : observedEvents) {
				for (SignalDetectorEventDetectorFactory detfact : config.getSigDeteEveDetFactList()) {
					if ((detfact.getPkgName().compareTo(observedEvent.getPackageName().toString()) == 0)
							&& (detfact.getEventName().compareTo(observedEvent.getEventIdentifier().getName()) == 0)) {

						switch (observedEvent.getEventIdentifier().intValue()) {
						case MgcpEvent.DTMF_0:
							digitDetected = digitDetected + 0;
							break;
						case MgcpEvent.DTMF_1:
							digitDetected = digitDetected + 1;
							break;
						case MgcpEvent.DTMF_2:
							digitDetected = digitDetected + 2;
							break;
						case MgcpEvent.DTMF_3:
							digitDetected = digitDetected + 3;
							break;
						case MgcpEvent.DTMF_4:
							digitDetected = digitDetected + 4;
							break;
						case MgcpEvent.DTMF_5:
							digitDetected = digitDetected + 5;
							break;
						case MgcpEvent.DTMF_6:
							digitDetected = digitDetected + 6;
							break;
						case MgcpEvent.DTMF_7:
							digitDetected = digitDetected + 7;
							break;
						case MgcpEvent.DTMF_8:
							digitDetected = digitDetected + 8;
							break;
						case MgcpEvent.DTMF_9:
							digitDetected = digitDetected + 9;
							break;
						case MgcpEvent.DTMF_A:
							digitDetected = digitDetected + "A";
							break;
						case MgcpEvent.DTMF_B:
							digitDetected = digitDetected + "B";
							break;
						case MgcpEvent.DTMF_C:
							digitDetected = digitDetected + "C";
							break;
						case MgcpEvent.DTMF_D:
							digitDetected = digitDetected + "D";
							break;
						case MgcpEvent.DTMF_HASH:
							digitDetected = digitDetected + "#";
							break;
						case MgcpEvent.DTMF_STAR:
							digitDetected = digitDetected + "*";
							break;

						default:
							// TODO : ObservedEvent could be not DTMF. Need to take care
							// latter
							logger.error("Detected unexpected MGCP Event "
									+ observedEvent.getEventIdentifier().toString());
							break;

						}

						event = (SignalDetectorEventImpl) detfact.generateMediaEvent();
						event.setDetector(detector);
						event.setSuccessful(true);
						event.setSignal(digitDetected);
						update(event);

					}
				}
			}

			// TODO : Need to implement the patterns
			// int count = -1;
			// for (String s : regExp) {
			// count++;
			// if (s!=null && digitDetected.matches(s)) {
			// break;
			// }
			// }
			//
			// if (count > -1) {
			// event = new SignalDetectorEventImpl(this.detector,
			// SignalDetector.ev_Pattern[count], digitDetected,
			// count, SignalDetector.q_Pattern[count], null);
			// } else {

			// }

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

				SignalDetectorEventImpl event = new SignalDetectorEventImpl(this.detector,
						SignalDetectorEvent.SIGNAL_DETECTED, false, MediaErr.UNKNOWN_ERROR,
						"RQNT Failed.  Look at logs " + respEvent.getReturnCode().getComment());
				update(event);
				break;
			}
		}

		private void processReqNotificationResponse(NotificationRequestResponse responseEvent) {
			SignalDetectorEvent event = null;
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

				event = new SignalDetectorEventImpl(this.detector, SignalDetectorEvent.SIGNAL_DETECTED, false,
						MediaErr.RESOURCE_UNAVAILABLE, "RQNT Failed.  Look at logs "
								+ responseEvent.getReturnCode().getComment());

				update(event);
				break;
			default:
				logger.error(" SOMETHING IS BROKEN = " + responseEvent);
				mgcpWrapper.removeListener(responseEvent.getTransactionHandle());
				mgcpWrapper.removeListener(reqId);

				event = new SignalDetectorEventImpl(this.detector, SignalDetectorEvent.SIGNAL_DETECTED, false,
						MediaErr.UNKNOWN_ERROR, "RQNT Failed.  Look at logs "
								+ responseEvent.getReturnCode().getComment());

				update(event);
				break;

			}
		}
	}

}
