package org.mobicents.media.server.load.test;

import jain.protocol.ip.mgcp.JainMgcpCommandEvent;
import jain.protocol.ip.mgcp.JainMgcpEvent;
import jain.protocol.ip.mgcp.JainMgcpResponseEvent;
import jain.protocol.ip.mgcp.message.Constants;
import jain.protocol.ip.mgcp.message.CreateConnection;
import jain.protocol.ip.mgcp.message.CreateConnectionResponse;
import jain.protocol.ip.mgcp.message.DeleteConnection;
import jain.protocol.ip.mgcp.message.DeleteConnectionResponse;
import jain.protocol.ip.mgcp.message.NotificationRequest;
import jain.protocol.ip.mgcp.message.Notify;
import jain.protocol.ip.mgcp.message.NotifyResponse;
import jain.protocol.ip.mgcp.message.parms.CallIdentifier;
import jain.protocol.ip.mgcp.message.parms.ConflictingParameterException;
import jain.protocol.ip.mgcp.message.parms.ConnectionDescriptor;
import jain.protocol.ip.mgcp.message.parms.ConnectionIdentifier;
import jain.protocol.ip.mgcp.message.parms.ConnectionMode;
import jain.protocol.ip.mgcp.message.parms.EndpointIdentifier;
import jain.protocol.ip.mgcp.message.parms.EventName;
import jain.protocol.ip.mgcp.message.parms.NotifiedEntity;
import jain.protocol.ip.mgcp.message.parms.RequestedAction;
import jain.protocol.ip.mgcp.message.parms.RequestedEvent;
import jain.protocol.ip.mgcp.message.parms.ReturnCode;
import jain.protocol.ip.mgcp.pkg.MgcpEvent;
import jain.protocol.ip.mgcp.pkg.PackageName;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.TooManyListenersException;
import java.util.Vector;

import javax.sdp.Attribute;
import javax.sdp.MediaDescription;
import javax.sdp.SdpException;
import javax.sdp.SdpFactory;
import javax.sdp.SessionDescription;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.mobicents.media.server.impl.rtp.sdp.AVProfile;
import org.mobicents.media.server.impl.rtp.sdp.RTPAudioFormat;
import org.mobicents.mgcp.stack.JainMgcpExtendedListener;
import org.mobicents.mgcp.stack.JainMgcpStackProviderImpl;

public class AnnouncementUA implements Runnable {

	private Logger logger = Logger.getLogger(AnnouncementUA.class);

	private boolean taskCompleted = false;

	private String name = "null";

	private int UACount = 0;
	private InetAddress clientMachineIPAddress;

	private String jbossBindAddress;
	private int serverMGCPStackPort = 0;

	private JainMgcpStackProviderImpl provider = null;

	private EndpointIdentifier endpointID = null;
	private ConnectionIdentifier connectionIdentifier = null;

	private EchoLoadTest echoLoadTest;

	private JainMgcpListnerImpl listenerImpl = null;

	private boolean openRTP = false;

	private boolean oc = true;
	
	private RTPAudioFormat format;

	public AnnouncementUA(int UACount, InetAddress clientMachineIPAddress, String jbossBindAddress,
			int serverMGCPStackPort, JainMgcpStackProviderImpl provider, EchoLoadTest echoLoadTest, RTPAudioFormat format) {
		this.UACount = UACount;
		this.name = "AnnouncementUA" + UACount;

		this.clientMachineIPAddress = clientMachineIPAddress;
		this.jbossBindAddress = jbossBindAddress;
		this.serverMGCPStackPort = serverMGCPStackPort;

		this.provider = provider;

		this.echoLoadTest = echoLoadTest;
		this.format = format;
	}

	public void run() {
		logger.info(this.name + "Starting the AnnouncementUA = " + this.name);
		oc = true;

		try {
			this.setTaskCompleted(false);

			listenerImpl = new JainMgcpListnerImpl();
			provider.addJainMgcpListener(listenerImpl);

			CallIdentifier callID = provider.getUniqueCallIdentifier();

			endpointID = new EndpointIdentifier("media/trunk/Announcement/$", jbossBindAddress + ":"
					+ serverMGCPStackPort);

			CreateConnection createConnection = new CreateConnection(this, callID, endpointID, ConnectionMode.SendRecv);

			createConnection.setRemoteConnectionDescriptor(new ConnectionDescriptor(getLocalDescriptor(this.UACount)));

			createConnection.setTransactionHandle(provider.getUniqueTransactionHandler());

			provider.sendMgcpEvents(new JainMgcpEvent[] { createConnection });

			logger.debug(this.name + " CreateConnection command sent for TxId "
					+ createConnection.getTransactionHandle() + " and CallId " + callID);

			// Now let us go to sleep unless the task is completed successfully
			// or with errors

			while (!isTaskCompleted()) {

				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			logger.debug(this.name + " Completed ");
		} catch (TooManyListenersException tmlex) {
			logger.error("TooManyListenersException ", tmlex);
		} catch (ConflictingParameterException e) {
			logger.error("ConflictingParameterException ", e);
		}

	}

	public boolean isTaskCompleted() {
		return taskCompleted;
	}

	public void setTaskCompleted(boolean taskCompleted) {
		this.taskCompleted = taskCompleted;
	}

	public boolean isOpenRTP() {
		return openRTP;
	}

	public void setOpenRTP(boolean openRTP) {
		this.openRTP = openRTP;
	}

	class JainMgcpListnerImpl implements JainMgcpExtendedListener {

		public void processMgcpCommandEvent(JainMgcpCommandEvent command) {
			logger.debug("processMgcpCommandEvent = " + command);
			switch (command.getObjectIdentifier()) {
			case Constants.CMD_NOTIFY:
				logger.info(name + " Announcement Completed Successfully");
				// echoLoadTest.addTaskCompletedSuccessfully();

				// Lets test if this is OC or OF

				Notify notify = (Notify) command;
				EventName[] eventNames = notify.getObservedEvents();
				for (EventName eventName : eventNames) {
					if (eventName.getEventIdentifier().intValue() == MgcpEvent.of.intValue()) {
						logger.warn(name + " Announcement Failed");
						oc = false;
					}
				}

				// Send NotifyResponse
				NotifyResponse notifyResponse = new NotifyResponse(command.getSource(),
						ReturnCode.Transaction_Executed_Normally);

				notifyResponse.setTransactionHandle(command.getTransactionHandle());

				provider.sendMgcpEvents(new JainMgcpEvent[] { notifyResponse });

				// cleanUp();

				// Send DeleteConnection
				sendDeleteConnectionMGCPRequest();

				// sendNotificationRequestMGCPRequest();
				break;

			default:
				logger.warn(name + " Unexpected COMMAND " + command);
				// echoLoadTest.addTaskCompletedFailure();
				// cleanUp();
				break;
			}

		}

		private void processCreateConnectionResponse(CreateConnectionResponse responseEvent) {
			logger.debug(name + " processCreateConnectionResponse() ");
			ReturnCode returnCode = responseEvent.getReturnCode();

			endpointID = responseEvent.getSpecificEndpointIdentifier();

			switch (returnCode.getValue()) {
			case ReturnCode.TRANSACTION_EXECUTED_NORMALLY:
				connectionIdentifier = responseEvent.getConnectionIdentifier();
				logger.debug(name + " TRANSACTION_EXECUTED_NORMALLY for connectionIdentifier = " + connectionIdentifier
						+ "endpointID = " + endpointID);

				// Before Sending Notification lets Open RTP Session to Play
				// Audio in case

				sendNotificationRequestMGCPRequest();

				break;
			default:
				logger.error(name + " SOMETHING IS BROKEN = " + responseEvent);
				echoLoadTest.addTaskCompletedFailure();
				cleanUp();
				break;

			}

		}

		private void processDeleteConnectionResponse(DeleteConnectionResponse responseEvent) {
			logger.debug(name + " Connection deleted at server, do the clean up here");
			if (oc) {
				echoLoadTest.addTaskCompletedSuccessfully();
			} else {
				echoLoadTest.addTaskCompletedFailure();
			}
			cleanUp();

		}

		public void processMgcpResponseEvent(JainMgcpResponseEvent responseEvent) {
			logger.debug(name + " processMgcpResponseEvent = " + responseEvent);
			switch (responseEvent.getObjectIdentifier()) {
			case Constants.RESP_CREATE_CONNECTION:

				// Let us sleep here for few secs
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				processCreateConnectionResponse((CreateConnectionResponse) responseEvent);
				break;
			case Constants.RESP_DELETE_CONNECTION:
				processDeleteConnectionResponse((DeleteConnectionResponse) responseEvent);
				break;

			case Constants.RESP_NOTIFICATION_REQUEST:
				logger.info(name + " Announcement Started");
				break;

			default:
				logger.warn(name + " This RESPONSE is unexpected " + responseEvent);
				break;

			}

		}

		public void transactionEnded(int handle) {
			logger.debug(name + " transactionEnded for handle = " + handle);

		}

		public void transactionRxTimedOut(JainMgcpCommandEvent command) {
			logger.debug(name + " Request not able to send");
			logger.debug("transactionRxTimedOut for JainMgcpCommandEvent = " + command);
			logger.debug("Clean the MGCP Stack");
			echoLoadTest.addTaskCompletedFailure();
			cleanUp();

		}

		public void transactionTxTimedOut(JainMgcpCommandEvent command) {
			logger.debug(name + " transactionTxTimedOut for JainMgcpCommandEvent = " + command);

			echoLoadTest.addTaskCompletedFailure();

			if (connectionIdentifier != null) {
				sendDeleteConnectionMGCPRequest();
			} else {
				cleanUp();
			}

			// This is failure condition

		}

		private void sendDeleteConnectionMGCPRequest() {
			DeleteConnection deleteConnection = new DeleteConnection(this, endpointID);
			//deleteConnection.setConnectionIdentifier(connectionIdentifier);
			deleteConnection.setTransactionHandle(provider.getUniqueTransactionHandler());

			provider.sendMgcpEvents(new JainMgcpEvent[] { deleteConnection });
		}

		private void sendNotificationRequestMGCPRequest() {

			String HELLO_WORLD = "http://" + jbossBindAddress + ":8080/mms-load-test/audio/result" + UACount + ".wav";

			NotificationRequest notificationRequest = new NotificationRequest(this, endpointID, provider
					.getUniqueRequestIdentifier());

			EventName[] signalRequests = { new EventName(PackageName.Announcement, MgcpEvent.ann.withParm(HELLO_WORLD),
					connectionIdentifier) };
			notificationRequest.setSignalRequests(signalRequests);

			RequestedAction[] actions = new RequestedAction[] { RequestedAction.NotifyImmediately };

			RequestedEvent[] requestedEvents = {
					new RequestedEvent(new EventName(PackageName.Announcement, MgcpEvent.oc, connectionIdentifier),
							actions),
					new RequestedEvent(new EventName(PackageName.Announcement, MgcpEvent.of, connectionIdentifier),
							actions) };

			notificationRequest.setRequestedEvents(requestedEvents);
			notificationRequest.setTransactionHandle(provider.getUniqueTransactionHandler());

			NotifiedEntity notifiedEntity = new NotifiedEntity(clientMachineIPAddress.getHostName(),
					clientMachineIPAddress.getHostName(), provider.getJainMgcpStack().getPort());
			notificationRequest.setNotifiedEntity(notifiedEntity);

			provider.sendMgcpEvents(new JainMgcpEvent[] { notificationRequest });

			logger.info(name + " NotificationRequest sent");
		}

	}// End of Private Class JainMgcpListnerImpl

	private void cleanUp() {
		// Cleaning
		logger.debug(this.name + " cleanUp()");
		provider.removeJainMgcpListener(listenerImpl);
		listenerImpl = null;
		setTaskCompleted(true);
	}

	private String getLocalDescriptor(int port) {

		String userName = "MediaServerLoadTest";
		long sessionID = UACount;
		long sessionVersion = sessionID;

		String networkType = javax.sdp.Connection.IN;
		String addressType = javax.sdp.Connection.IP4;
		String address = clientMachineIPAddress.getHostAddress();

		// TODO : Port should be assigned
		int audioPort = 7999 + port;

		SessionDescription localSDP = null;
		SdpFactory sdpFactory = SdpFactory.getInstance();

		try {
			localSDP = sdpFactory.createSessionDescription();
			localSDP.setVersion(sdpFactory.createVersion(0));
			localSDP.setOrigin(sdpFactory.createOrigin(userName, sessionID, sessionVersion, networkType, addressType,
					address));
			localSDP.setSessionName(sdpFactory.createSessionName("session"));
			localSDP.setConnection(sdpFactory.createConnection(networkType, addressType, address));

			Vector<MediaDescription> descriptions = new Vector<MediaDescription>();

			// encode formats
			HashMap fmts = new HashMap();
			fmts.put(AVProfile.getPayload(this.format), this.format);
			

			Object[] payloads = getPayloads(fmts).toArray();

			int[] formats = new int[payloads.length];
			for (int i = 0; i < formats.length; i++) {
				formats[i] = ((Integer) payloads[i]).intValue();
			}

			// generate media descriptor
			MediaDescription md = sdpFactory.createMediaDescription("audio", audioPort, 1, "RTP/AVP", formats);

			boolean g729 = false;
			// set attributes for formats
			Vector<Attribute> attributes = new Vector<Attribute>();
			for (int i = 0; i < formats.length; i++) {
				RTPAudioFormat format = (RTPAudioFormat) fmts.get(new Integer(formats[i]));
				attributes.add(sdpFactory.createAttribute("rtpmap", format.toSdp()));
				if (format.getEncoding().contains("g729"))
					g729 = true;
			}

			if (g729)
				attributes.add(sdpFactory.createAttribute("fmtp", "18 annexb=no"));

			// generate descriptor
			md.setAttributes(attributes);
			descriptions.add(md);

			localSDP.setMediaDescriptions(descriptions);
		} catch (SdpException e) {
			logger.error("Could not create descriptor", e);
		}
		logger.debug(" SDP = "+localSDP.toString());
		return localSDP.toString();
	}

	private Collection getPayloads(HashMap fmts) {
		Object[] payloads = fmts.keySet().toArray();

		ArrayList list = new ArrayList();
		for (int i = 0; i < payloads.length; i++) {
			list.add(payloads[i]);
		}

		Collections.sort(list);
		return list;
	}

	public static void main(String args[]) {

		try {
			String logj = args[0];
			PropertyConfigurator.configure(logj);

		} catch (ArrayIndexOutOfBoundsException aoex) {
			aoex.printStackTrace();
			// User is not passing any Log4j file. Let us set at least
			// BasicConfigurator
			BasicConfigurator.configure();
		}

		InetAddress clientMachineIPAddress = null;
		try {
			clientMachineIPAddress = InetAddress.getByName("127.0.0.1");
		} catch (UnknownHostException e) {		
			e.printStackTrace();
		}
		AnnouncementUA ua = new AnnouncementUA(1, clientMachineIPAddress, "127.0.0.1", 2729, null, null, AVProfile.PCMU);

		System.out.println("getLocalDescriptor = " + ua.getLocalDescriptor(1));
		// ua.run();
	}

}
