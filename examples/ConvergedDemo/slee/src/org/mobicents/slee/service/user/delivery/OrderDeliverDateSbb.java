package org.mobicents.slee.service.user.delivery;

import java.net.URL;
import java.text.ParseException;
import java.util.HashMap;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.sip.ClientTransaction;
import javax.sip.Dialog;
import javax.sip.InvalidArgumentException;
import javax.sip.SipException;
import javax.sip.TransactionUnavailableException;
import javax.sip.address.Address;
import javax.sip.header.CallIdHeader;
import javax.sip.header.Header;
import javax.sip.message.Request;
import javax.slee.ActivityContextInterface;
import javax.slee.ChildRelation;
import javax.slee.CreateException;
import javax.slee.SbbContext;
import javax.slee.UnrecognizedActivityException;

import org.apache.log4j.Logger;
import org.mobicents.seam.Order;
import org.mobicents.slee.resource.media.ratype.Cause;
import org.mobicents.slee.resource.media.ratype.ConnectionEvent;
import org.mobicents.slee.resource.media.ratype.IVRContext;
import org.mobicents.slee.resource.media.ratype.MediaConnection;
import org.mobicents.slee.resource.media.ratype.MediaContextEvent;
import org.mobicents.slee.resource.persistence.ratype.PersistenceResourceAdaptorSbbInterface;
import org.mobicents.slee.resource.tts.ratype.TTSSession;
import org.mobicents.slee.service.callcontrol.CallControlSbbLocalObject;
import org.mobicents.slee.service.common.CommonSbb;
import org.mobicents.slee.service.events.CustomEvent;
import org.mobicents.slee.util.Session;
import org.mobicents.slee.util.SessionAssociation;

public abstract class OrderDeliverDateSbb extends CommonSbb {

	private Logger logger = Logger.getLogger(OrderDeliverDateSbb.class);

	private PersistenceResourceAdaptorSbbInterface persistenceResourceAdaptorSbbInterface = null;

	private String pathToAudioDirectory = null;

	String audioFilePath = null;

	String callerSip = null;

	/** Creates a new instance of UserSbb */
	public OrderDeliverDateSbb() {
		super();
	}

	public void setSbbContext(SbbContext context) {
		super.setSbbContext(context);
		try {

			Context myEnv = (Context) new InitialContext()
					.lookup("java:comp/env");

			audioFilePath = (String) myEnv.lookup("audioFilePath");

			pathToAudioDirectory = "file:"
					+ (String) myEnv.lookup("pathToAudioDirectory");
			callerSip = (String) myEnv.lookup("callerSip");

			persistenceResourceAdaptorSbbInterface = (PersistenceResourceAdaptorSbbInterface) myEnv
					.lookup("slee/resources/pra/0.1/provider");
		} catch (NamingException ne) {
			ne.printStackTrace();
		}
	}

	public void onOrderApproved(CustomEvent event, ActivityContextInterface ac) {
		System.out
				.println("======== OrderDeliverDateSbb ORDER_APPROVED ========");
		makeCall(event, ac);
	}

	public void onOrderProcessed(CustomEvent event, ActivityContextInterface ac) {
		System.out
				.println("======== OrderDeliverDateSbb ORDER_PROCESSED ========");
		makeCall(event, ac);
	}

	private void makeCall(CustomEvent event, ActivityContextInterface ac) {

		// Let us wait for 30 sec as User might be busy with Order Confirmation
		// call
		// TODO: This could be a task in jBPM process flow but for simplicity we
		// will just skip that
		try {
			Thread.sleep(1000 * 30);
		} catch (Exception e) {
			// Ignore
		}

		this.setCustomEvent(event);
		this.setDateAndTime("");

		try {
			// Set the caller address to the address of our call controller
			Address callerAddress = getSipUtils()
					.convertURIToAddress(callerSip);
			callerAddress.setDisplayName(callerSip);

			// Retrieve the callee addresses from the event
			Address calleeAddress = getSipUtils().convertURIToAddress(
					event.getCustomerPhone());

			// Build the INVITE request
			Request request = getSipUtils().buildInvite(callerAddress,
					calleeAddress, null, 1);

			// Create a new transaction based on the generated request
			ClientTransaction ct = getSipProvider().getNewClientTransaction(
					request);

			// Get activity context from factory
			ActivityContextInterface sipACIF = getSipActivityContextInterfaceFactory()
					.getActivityContextInterface(ct);

			Header h = ct.getRequest().getHeader(CallIdHeader.NAME);
			String calleeCallId = ((CallIdHeader) h).getCallId();

			SessionAssociation sa = new SessionAssociation(
					"org.mobicents.slee.service.callcontrol.CallControlSbb$InitialState");

			Session calleeSession = new Session(calleeCallId);
			calleeSession.setSipAddress(calleeAddress);
			calleeSession.setToBeCancelledClientTransaction(ct);

			// The dialog for the client transaction in which the INVITE is sent
			Dialog dialog = ct.getDialog();
			if (dialog != null && logger.isDebugEnabled()) {
				logger
						.debug("Obtained dialog from ClientTransaction : automatic dialog support on");
			}
			if (dialog == null) {
				// Automatic dialog support turned off
				try {
					dialog = getSipProvider().getNewDialog(ct);
					if (logger.isDebugEnabled()) {
						logger
								.debug("Obtained dialog for INVITE request to callee with getNewDialog");
					}
				} catch (Exception e) {
					logger.error("Error getting dialog", e);
				}
			}

			if (logger.isDebugEnabled()) {
				logger
						.debug("Obtained dialog in onThirdPCCTriggerEvent : callId = "
								+ dialog.getCallId().getCallId());
			}

			calleeSession.setDialog(dialog);
			sa.setCalleeSession(calleeSession);

			/**
			 * Actually callerSession is not required for this example and clean
			 * up is needed
			 */
			Session callerSession = new Session();

			// Create a new caller address from caller URI specified in the
			// event (the real caller address) since we need this in the next
			// INVITE.
			callerAddress = getSipUtils().convertURIToAddress(callerSip);
			callerSession.setSipAddress(callerAddress);
			// Since we don't have the client transaction for the caller yet,
			// just set the to be cancelled client transaction to null.
			callerSession.setToBeCancelledClientTransaction(null);
			sa.setCallerSession(callerSession);

			// put the callId for the callee dialog in the cache
			getCacheUtility().put(calleeCallId, sa);

			ChildRelation relation = getCallControlSbbChild();
			// Create child SBB
			CallControlSbbLocalObject child = (CallControlSbbLocalObject) relation
					.create();

			setChildSbbLocalObject(child);

			child.setParent(getSbbContext().getSbbLocalObject());

			// Attach child SBB to the activity context
			sipACIF.attach(child);
			// Send the INVITE request
			ct.sendRequest();

		} catch (ParseException parExc) {
			logger.error("Parse Exception while parsing the callerAddess",
					parExc);
		} catch (InvalidArgumentException invalidArgExcep) {
			logger.error(
					"InvalidArgumentException while building Invite Request",
					invalidArgExcep);
		} catch (TransactionUnavailableException tranUnavExce) {
			logger
					.error(
							"TransactionUnavailableException when trying to getNewClientTransaction",
							tranUnavExce);
		} catch (UnrecognizedActivityException e) {
			// TODO Auto-generated catch block
			logger
					.error(
							"UnrecognizedActivityException when trying to getActivityContextInterface",
							e);
		} catch (CreateException creaExce) {
			logger.error("CreateException while trying to create Child",
					creaExce);
		} catch (SipException sipExec) {
			logger.error("SipException while trying to send INVITE Request",
					sipExec);
		}

	}

	public void onDtmfEvent(ConnectionEvent event, ActivityContextInterface aci) {
		boolean success = false;
		int dtmf = event.getCause();

		String dateAndTime = this.getDateAndTime();

		switch (dtmf) {
		case Cause.DTMF_0:
			dateAndTime = dateAndTime + "0";
			break;
		case Cause.DTMF_1:
			dateAndTime = dateAndTime + "1";
			break;
		case Cause.DTMF_2:
			dateAndTime = dateAndTime + "2";
			break;
		case Cause.DTMF_3:
			dateAndTime = dateAndTime + "3";
			break;
		case Cause.DTMF_4:
			dateAndTime = dateAndTime + "4";
			break;
		case Cause.DTMF_5:
			dateAndTime = dateAndTime + "5";
			break;
		case Cause.DTMF_6:
			dateAndTime = dateAndTime + "6";
			break;
		case Cause.DTMF_7:
			dateAndTime = dateAndTime + "7";
			break;
		case Cause.DTMF_8:
			dateAndTime = dateAndTime + "8";
			break;
		case Cause.DTMF_9:
			dateAndTime = dateAndTime + "9";
			break;
		default:
			System.out.println("Invalid DTMF.");
			break;
		}

		// TODO: Add logic to check if date and time is valid. We assume that
		// use is well educated and will always punch right date and time

		if (dateAndTime.length() == 10) {

			EntityManager mgr = null;
			Order order = null;

			TTSSession ttsSession = getTTSProvider().getNewTTSSession(
					audioFilePath, "kevin16");

			char[] c = dateAndTime.toCharArray();

			StringBuffer stringBuffer = new StringBuffer();
			stringBuffer.append("You have selected delivery date to be ");

			String date = "" + c[0] + c[1];
			int iDate = (new Integer(date)).intValue();
			stringBuffer.append(iDate);

			String month = "" + c[2] + c[3];
			int iMonth = (new Integer(month)).intValue();

			String year = "" + c[4] + c[5];
			int iYear = (new Integer(year)).intValue();

			String hour = "" + c[6] + c[7];
			int iHour = (new Integer(hour)).intValue();

			String min = "" + c[8] + c[9];
			int iMin = (new Integer(min)).intValue();

			switch (iMonth) {
			case 1:
				month = "January";
				break;
			case 2:
				month = "February";
				break;
			case 3:
				month = "March";
				break;
			case 4:
				month = "April";
				break;
			case 5:
				month = "May";
				break;
			case 6:
				month = "June";
				break;
			case 7:
				month = "July";
				break;
			case 8:
				month = "August";
				break;
			case 9:
				month = "September";
				break;
			case 10:
				month = "October";
				break;
			case 11:
				month = "November";
				break;
			case 12:
				month = "December";
				break;
			default:
				System.out.println("Invalid month.");
				break;
			}
			stringBuffer.append(" of ");
			stringBuffer.append(month);
			stringBuffer.append(" ");
			stringBuffer.append(2000 + iYear);
			stringBuffer.append(" at ");
			stringBuffer.append(iHour);
			stringBuffer.append(" hour and ");
			stringBuffer.append(iMin);
			stringBuffer.append(" minute. Thank you. Bye.");

			java.sql.Timestamp timeStamp = new java.sql.Timestamp(
					(iYear + 100), iMonth - 1, iDate, iHour, iMin, 0, 0);

			mgr = this.persistenceResourceAdaptorSbbInterface
					.createEntityManager(new HashMap(), "custom-pu");

			order = (Order) mgr
					.createQuery(
							"select o from Order o where o.orderId = :orderId")
					.setParameter("orderId", this.getCustomEvent().getOrderId())
					.getSingleResult();

			order.setDeliveryDate(timeStamp);

			mgr.flush();
			mgr.close();

			ttsSession.textToAudioFile(stringBuffer.toString());

			MediaConnection connection = event.getConnection();
			IVRContext mediaContext = (IVRContext) connection.getMediaContext();

			try {
				Thread.sleep(500);
			} catch (Exception e) {
				// ignore
			}

			try {

				URL message = new URL("file:" + audioFilePath);
				mediaContext.play(message);
			} catch (Exception ex) {
				logger.error("Unable load void messages", ex);
			}

			success = true;
			this.setSendBye(success);

		} else {
			this.setDateAndTime(dateAndTime);
		}
	}

	public void onPlayerStopped(MediaContextEvent evt,
			ActivityContextInterface aci) {
		logger.debug("onPlayerStopped ");
		if (this.getSendBye()) {
			getChildSbbLocalObject().sendBye();
		}
	}

	public void onPlayerFailed(MediaContextEvent evt,
			ActivityContextInterface aci) {
		logger.error("onPlayerFailed ");

	}

	public void onConnectionConnected(ConnectionEvent evt,
			ActivityContextInterface aci) {
		MediaConnection connection = evt.getConnection();

		IVRContext ivr = (IVRContext) connection.getMediaContext();
		URL announcement = null;
		try {
			announcement = new URL(pathToAudioDirectory
					+ "OrderDeliveryDate.wav");
		} catch (Exception e) {
			logger.error("Could not load announcement message from: "
					+ pathToAudioDirectory + "OrderDeliveryDate.wav");
		}

		ivr.play(announcement);
		try {
			Thread.sleep(1000 * 1);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	// child relation
	public abstract ChildRelation getCallControlSbbChild();

	public abstract void setCustomEvent(CustomEvent customEvent);

	public abstract CustomEvent getCustomEvent();

	public abstract void setSendBye(boolean isBye);

	public abstract boolean getSendBye();

	public abstract void setDateAndTime(String dateAndTime);

	public abstract String getDateAndTime();

	public abstract void setChildSbbLocalObject(
			CallControlSbbLocalObject childSbbLocalObject);

	public abstract CallControlSbbLocalObject getChildSbbLocalObject();
}
