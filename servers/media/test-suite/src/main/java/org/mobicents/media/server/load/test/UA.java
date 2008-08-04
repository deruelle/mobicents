package org.mobicents.media.server.load.test;

import jain.protocol.ip.mgcp.CreateProviderException;
import jain.protocol.ip.mgcp.DeleteProviderException;
import jain.protocol.ip.mgcp.JainMgcpCommandEvent;
import jain.protocol.ip.mgcp.JainMgcpEvent;
import org.mobicents.mgcp.stack.JainMgcpExtendedListener;
import jain.protocol.ip.mgcp.JainMgcpProvider;
import jain.protocol.ip.mgcp.JainMgcpResponseEvent;
import jain.protocol.ip.mgcp.message.Constants;
import jain.protocol.ip.mgcp.message.CreateConnection;
import jain.protocol.ip.mgcp.message.CreateConnectionResponse;
import jain.protocol.ip.mgcp.message.DeleteConnection;
import jain.protocol.ip.mgcp.message.DeleteConnectionResponse;
import jain.protocol.ip.mgcp.message.parms.CallIdentifier;
import jain.protocol.ip.mgcp.message.parms.ConflictingParameterException;
import jain.protocol.ip.mgcp.message.parms.ConnectionDescriptor;
import jain.protocol.ip.mgcp.message.parms.ConnectionIdentifier;
import jain.protocol.ip.mgcp.message.parms.ConnectionMode;
import jain.protocol.ip.mgcp.message.parms.EndpointIdentifier;
import jain.protocol.ip.mgcp.message.parms.ReturnCode;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.Properties;
import java.util.TooManyListenersException;

import javax.sdp.SdpException;
import javax.sdp.SdpFactory;
import javax.sdp.SessionDescription;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.mobicents.media.server.impl.common.ConnectionState;
import org.mobicents.media.server.impl.common.MediaResourceType;
import org.mobicents.media.server.impl.common.events.EventID;
import org.mobicents.media.server.impl.ivr.IVREndpointImpl;
import org.mobicents.media.server.impl.rtp.RtpSocketAdaptor;
import org.mobicents.media.server.impl.sdp.AVProfile;
import org.mobicents.media.server.spi.Connection;
import org.mobicents.media.server.spi.ConnectionListener;
import org.mobicents.media.server.spi.Endpoint;
import org.mobicents.media.server.spi.NotificationListener;
import org.mobicents.media.server.spi.ResourceUnavailableException;
import org.mobicents.media.server.spi.TooManyConnectionsException;
import org.mobicents.media.server.spi.UnknownMediaResourceException;
import org.mobicents.media.server.spi.UnknownSignalException;
import org.mobicents.media.server.spi.events.NotifyEvent;
import org.mobicents.mgcp.stack.JainMgcpStackImpl;

public class UA implements Runnable {

	private Logger logger = Logger.getLogger(UA.class);

	private String name = "null";

	private int UACount = 0;

	RtpSocketAdaptor rtpSocket = null;

	private Endpoint uaEndpoint;

	private Connection uaConnection;

	private String sendSdp = null;

	private JainMgcpStackImpl stack = null;
	private JainMgcpProvider provider = null;

	private EndpointIdentifier endpointID = null;
	private ConnectionIdentifier connectionIdentifier = null;

	private boolean endOfMediaEventFired = false;

	public UA(int UACount) {
		this.UACount = UACount;
		this.name = "UA" + UACount;
	}

	public void run() {

		logger.info("Starting the UA = " + this.name);

		try {
			uaEndpoint = new IVREndpointImpl("test/client/" + this.UACount);
			uaEndpoint.addFormat(AVProfile.PCMA.getPayload(), AVProfile.PCMA);
			uaEndpoint.addFormat(AVProfile.PCMU.getPayload(), AVProfile.PCMU);
			uaEndpoint.setJitter(60);
			uaEndpoint.setPacketizationPeriod(20);
			uaEndpoint.setBindAddress(InetAddress.getByName(EchoLoadTest.EndpointBindAddress));
			uaEndpoint.setPortRange((EchoLoadTest.EndpointLowPortNumber + UACount) + "-"
					+ EchoLoadTest.EndpointHighPortNumber);

			Properties dtmfConfig = new Properties();
			dtmfConfig.setProperty("detector.mode", EchoLoadTest.EndpointDTMFDetector);

			uaEndpoint.setDefaultConfig(MediaResourceType.DTMF_DETECTOR, dtmfConfig);
			uaEndpoint.configure(MediaResourceType.DTMF_DETECTOR, dtmfConfig);

			uaConnection = uaEndpoint.createConnection(org.mobicents.media.server.impl.common.ConnectionMode.SEND_RECV);
			uaConnection.addListener(new UAConnectionListener(uaConnection));

			sendSdp = uaConnection.getLocalDescriptor();

			logger.debug("sendSdp = " + sendSdp);

			stack = new JainMgcpStackImpl(2728 + this.UACount);
			provider = stack.createProvider();

			JainMgcpListnerImpl listenerImpl = new JainMgcpListnerImpl();
			provider.addJainMgcpListener(listenerImpl);

			CallIdentifier callID = new CallIdentifier("1");

			endpointID = new EndpointIdentifier("media/test/trunk/Loopback/" + this.UACount,
					EchoLoadTest.JBossBindAddress + ":" + EchoLoadTest.ServerMGCPStackPort);

			CreateConnection createConnection = new CreateConnection("", callID, endpointID, ConnectionMode.SendRecv);

			createConnection.setRemoteConnectionDescriptor(new ConnectionDescriptor(sendSdp));

			createConnection.setTransactionHandle(1);

			provider.sendMgcpEvents(new JainMgcpEvent[] { createConnection });

			logger.debug("******* CreateConnection command sent ***********");

		} catch (CreateProviderException cpex) {
			logger.error("CreateProviderException ", cpex);
		} catch (TooManyListenersException tmlex) {
			logger.error("TooManyListenersException ", tmlex);
		} catch (ConflictingParameterException e) {
			logger.error("ConflictingParameterException ", e);
		} catch (UnknownHostException e) {
			logger.error("UnknownHostException ", e);
		} catch (UnknownMediaResourceException e) {
			logger.error("UnknownMediaResourceException ", e);
		} catch (TooManyConnectionsException e) {
			logger.error("TooManyConnectionsException ", e);
		} catch (ResourceUnavailableException e) {
			logger.error("ResourceUnavailableException ", e);
		}

	}

	class UAConnectionListener implements ConnectionListener, NotificationListener {

		private Logger logger = Logger.getLogger(UAConnectionListener.class);

		private Connection connection;

		public UAConnectionListener(Connection connection) {
			this.connection = connection;
		}

		public void onStateChange(Connection connection, ConnectionState oldState) {
			logger.debug("onStateChange: New Connection state = " + connection.getState() + " Old Connection State = "
					+ oldState);

			String RECORDER_FILE = "recordedfile" + UACount + ".wav";

			String[] params = new String[] { EchoLoadTest.AudioFileToPlay, RECORDER_FILE };
			if (connection.getState() == ConnectionState.OPEN) {

				logger.debug("Start the play and record ");
				try {
					connection.getEndpoint().configure(MediaResourceType.AUDIO_SINK, connection, null);
					connection.getEndpoint().configure(MediaResourceType.AUDIO_SOURCE, connection, null);
					connection.getEndpoint().play(EventID.PLAY_RECORD, params, connection.getId(), this, false, true);
				} catch (UnknownSignalException e) {
					e.printStackTrace();
				} catch (UnknownMediaResourceException e) {
					e.printStackTrace();
				}
			}
		}

		public void update(NotifyEvent event) {
			logger.debug("update: " + event.getID());
			if (event.getID() == EventID.COMPLETE && !endOfMediaEventFired) {

				endOfMediaEventFired = true;
				logger.info("EventID.COMPLETE Announcement Completes here now send DELETE MGCPCommand" + event);
				sendDeleteConnectionMGCPRequest();

			} else if (event.getID() == EventID.FAIL && !endOfMediaEventFired) {
				endOfMediaEventFired = true;
				logger.warn("EventID.FAIL Something went wrong send DELETE MGCPCommand" + event);
				sendDeleteConnectionMGCPRequest();

			}
		}

		private void sendDeleteConnectionMGCPRequest() {
			DeleteConnection deleteConnection = new DeleteConnection(this, endpointID);
			deleteConnection.setConnectionIdentifier(connectionIdentifier);
			deleteConnection.setTransactionHandle(2);

			provider.sendMgcpEvents(new JainMgcpEvent[] { deleteConnection });
		}
	}

	class JainMgcpListnerImpl implements JainMgcpExtendedListener {

		public void processMgcpCommandEvent(JainMgcpCommandEvent arg0) {
			logger.debug("processMgcpCommandEvent = " + arg0);

		}

		private void processCreateConnectionResponse(CreateConnectionResponse responseEvent) {
			logger.debug("processCreateConnectionResponse() ");
			ReturnCode returnCode = responseEvent.getReturnCode();

			SdpFactory sdpFactory = SdpFactory.getInstance();

			switch (returnCode.getValue()) {
			case ReturnCode.TRANSACTION_EXECUTED_NORMALLY:
				logger.debug("TRANSACTION_EXECUTED_NORMALLY = " + responseEvent);

				ConnectionDescriptor connectionDescriptor = ((CreateConnectionResponse) responseEvent)
						.getLocalConnectionDescriptor();

				connectionIdentifier = ((CreateConnectionResponse) responseEvent).getConnectionIdentifier();

				String descriptor = connectionDescriptor.toString();
				try {
					SessionDescription remoteSDP = sdpFactory.createSessionDescription(descriptor);

					logger.debug("remoteSDP = " + remoteSDP.toString());

					uaConnection.setRemoteDescriptor(remoteSDP.toString());

				} catch (SdpException sdp) {
					logger.error("SdpException", sdp);
				} catch (MalformedURLException e) {
					logger.error("MalformedURLException", e);
				} catch (IOException e) {
					logger.error("IOException", e);
				}

				break;
			default:
				logger.error("SOMETHING IS BROKEN = " + responseEvent);
				cleanUp();
				break;

			}

		}

		private void processDeleteConnectionResponse(DeleteConnectionResponse responseEvent) {
			logger.debug("Connection deleted at server, do the clean up here");
			cleanUp();

		}

		private void cleanUp() {
			// Cleaning

			uaEndpoint.deleteAllConnections();
			logger.info("All Connections deleted for endpoint " + uaEndpoint.getLocalName());

			// TODO : stack.deleteProvider(provider) does nothing :(
			try {
				stack.deleteProvider(provider);
				stack.close();
			} catch (DeleteProviderException e) {
				logger.error("DeleteProviderException ", e);
			}
			logger.info("MGCP Stack also down");
		}

		public void processMgcpResponseEvent(JainMgcpResponseEvent responseEvent) {
			logger.debug("processMgcpResponseEvent = " + responseEvent);
			switch (responseEvent.getObjectIdentifier()) {
			case Constants.RESP_CREATE_CONNECTION:
				processCreateConnectionResponse((CreateConnectionResponse) responseEvent);
				break;
			case Constants.RESP_DELETE_CONNECTION:
				processDeleteConnectionResponse((DeleteConnectionResponse) responseEvent);
				break;

			}

		}

		public void transactionEnded(int handle) {
			System.out.println("transactionEnded for handle = "+handle);
			
		}

		public void transactionRxTimedOut(JainMgcpCommandEvent command) {
			System.out.println("Request not able to send");
			System.out.println("transactionRxTimedOut for JainMgcpCommandEvent = "+command);
			System.out.println("Clean the MGCP Stack");
			
		}

		public void transactionTxTimedOut(JainMgcpCommandEvent command) {
			System.out.println("transactionTxTimedOut for JainMgcpCommandEvent = "+command);
			
		}

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

		UA ua = new UA(1);
		ua.run();
	}
}
