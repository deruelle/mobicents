package org.mobicents.javax.media.mscontrol.networkconnection;

import jain.protocol.ip.mgcp.JainMgcpCommandEvent;
import jain.protocol.ip.mgcp.JainMgcpEvent;
import jain.protocol.ip.mgcp.JainMgcpResponseEvent;
import jain.protocol.ip.mgcp.message.Constants;
import jain.protocol.ip.mgcp.message.CreateConnection;
import jain.protocol.ip.mgcp.message.CreateConnectionResponse;
import jain.protocol.ip.mgcp.message.DeleteConnection;
import jain.protocol.ip.mgcp.message.DeleteConnectionResponse;
import jain.protocol.ip.mgcp.message.ModifyConnection;
import jain.protocol.ip.mgcp.message.ModifyConnectionResponse;
import jain.protocol.ip.mgcp.message.parms.CallIdentifier;
import jain.protocol.ip.mgcp.message.parms.ConflictingParameterException;
import jain.protocol.ip.mgcp.message.parms.ConnectionDescriptor;
import jain.protocol.ip.mgcp.message.parms.ConnectionIdentifier;
import jain.protocol.ip.mgcp.message.parms.ConnectionMode;
import jain.protocol.ip.mgcp.message.parms.EndpointIdentifier;
import jain.protocol.ip.mgcp.message.parms.ReturnCode;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.media.mscontrol.JoinEvent;
import javax.media.mscontrol.Joinable;
import javax.media.mscontrol.MsControlException;
import javax.media.mscontrol.networkconnection.NetworkConnection;
import javax.media.mscontrol.networkconnection.NetworkConnectionConfig;
import javax.media.mscontrol.networkconnection.NetworkConnectionEvent;
import javax.media.mscontrol.networkconnection.NetworkConnectionException;
import javax.media.mscontrol.networkconnection.ResourceNotAvailableException;
import javax.media.mscontrol.resource.Error;
import javax.media.mscontrol.resource.MediaEvent;
import javax.media.mscontrol.resource.MediaEventListener;
import javax.media.mscontrol.resource.Parameters;
import javax.media.mscontrol.resource.Symbol;
import javax.sdp.SdpException;
import javax.sdp.SdpFactory;
import javax.sdp.SdpParseException;
import javax.sdp.SessionDescription;

import org.apache.log4j.Logger;
import org.mobicents.javax.media.mscontrol.AbstractJoinableContainer;
import org.mobicents.javax.media.mscontrol.AudioJoinableStream;
import org.mobicents.javax.media.mscontrol.MediaObjectState;
import org.mobicents.javax.media.mscontrol.MediaSessionImpl;
import org.mobicents.javax.media.mscontrol.resource.ParametersImpl;
import org.mobicents.jsr309.mgcp.MgcpWrapper;
import org.mobicents.jsr309.mgcp.Provider;
import org.mobicents.mgcp.stack.JainMgcpExtendedListener;

/**
 * 
 * @author amit bhayani
 * 
 */
public class NetworkConnectionImpl extends AbstractJoinableContainer implements NetworkConnection {

	public static Logger logger = Logger.getLogger(NetworkConnectionImpl.class);

	private URI uri = null;
	private EndpointIdentifier endpointIdentifier = null;
	private ConnectionIdentifier connectionIdentifier = null;
	private String remoteSessionDescription = null;
	private String localSessionDescription = null;

	private NetworkConnectionException networkConnectionException = null;
	private ResourceNotAvailableException resourceNotAvailableException = null;
	private static final String PR_ENDPOINT_NAME = "/trunk/media/PacketRelay/$";

	private transient SdpFactory sdpFactory = SdpFactory.getInstance();

	protected CopyOnWriteArrayList<MediaEventListener<? extends MediaEvent<?>>> mediaEventListenerList = new CopyOnWriteArrayList<MediaEventListener<? extends MediaEvent<?>>>();

	public NetworkConnectionImpl(MediaSessionImpl mediaSession, MgcpWrapper mgcpWrapper) {
		super(mediaSession, mgcpWrapper, 1, PR_ENDPOINT_NAME);
		try {
			this.uri = new URI(mediaSession.getURI().toString() + "/NetworkConnection." + this.id);
		} catch (URISyntaxException e) {
			// Ignore
		}
	}

	public SessionDescription getLocalSessionDescription() throws NetworkConnectionException {
		if (this.localSessionDescription == null) {
			throw new NetworkConnectionException("No local session description is available");
		}
		SessionDescription sdp = null;
		try {
			sdp = sdpFactory.createSessionDescription(localSessionDescription);
		} catch (SdpParseException e) {
			logger.error(e);
			throw new NetworkConnectionException(e);
		}
		return sdp;
	}

	public SessionDescription getRemoteSessionDescription() throws NetworkConnectionException {
		if (this.remoteSessionDescription == null) {
			throw new NetworkConnectionException("No remote session description is available");
		}
		SessionDescription sdp = null;
		try {
			sdp = sdpFactory.createSessionDescription(remoteSessionDescription);
		} catch (SdpParseException e) {
			logger.error(e);
			throw new NetworkConnectionException(e);
		}
		return sdp;
	}

	public String getRawLocalSessionDescription() throws NetworkConnectionException {
		if (this.localSessionDescription == null) {
			throw new NetworkConnectionException("No local session description is available");
		}
		return this.localSessionDescription;
	}

	public String getRawRemoteSessionDescription() throws NetworkConnectionException {
		if (this.remoteSessionDescription == null) {
			throw new NetworkConnectionException("No remote session description is available");
		}
		return this.remoteSessionDescription;
	}

	public void modify(SessionDescription localSessionDescription, SessionDescription remoteSessionDescription)
			throws SdpException, NetworkConnectionException, ResourceNotAvailableException {
		checkState();
		String localDescr = localSessionDescription != null ? localSessionDescription.toString() : null;
		String remoteDescr = remoteSessionDescription != null ? remoteSessionDescription.toString() : null;
		this.modify(localDescr, remoteDescr);
	}

	public void modify(String localSessionDescription, String remoteSessionDescription) throws SdpException,
			NetworkConnectionException, ResourceNotAvailableException {

		checkState();
		if (localSessionDescription == null && remoteSessionDescription == null) {
			// Do nothing and return as per Spec
			return;
		}
		this.remoteSessionDescription = remoteSessionDescription;

		// resetting the exceptions
		this.networkConnectionException = null;
		this.resourceNotAvailableException = null;

		// Async call
		Runnable tx = this.endpointIdentifier == null ? new CreateTx(this) : new ModifyTx(this);
		Provider.submit(tx);

	}

	public void confirm() throws MsControlException {
		// TODO Auto-generated method stub

	}

	public NetworkConnectionConfig getConfig() {
		// TODO Auto-generated method stub
		return null;
	}

	public <R> R getResource(Class<R> resource) throws MsControlException {
		// TODO Auto-generated method stub
		return null;
	}

	public void triggerRTC(Symbol rtca) {
		// TODO Auto-generated method stub

	}

	public Parameters getParameters(Symbol[] params) {
		// TODO Auto-generated method stub
		return null;
	}

	public URI getURI() {
		return this.uri;
	}

	public void release() {
		checkState();		

		if (this.endpointIdentifier != null) {
			Runnable tx = new DeleteTx(this);
			Provider.submit(tx);
		}

		try {
			Joinable[] joinableArray = this.getJoinees();
			for (Joinable joinable : joinableArray) {
				this.unjoinInitiate(joinable, this);
			}
		} catch (MsControlException e) {
			logger.error("release of NetworkConnection failed ", e);
		}
		
		this.state = MediaObjectState.RELEASED;

	}

	public void setParameters(Parameters params) {
		// TODO Auto-generated method stub

	}

	public void addListener(MediaEventListener<NetworkConnectionEvent> listener) {
		this.mediaEventListenerList.add(listener);

	}

	public void removeListener(MediaEventListener<NetworkConnectionEvent> listener) {
		this.mediaEventListenerList.remove(listener);
	}

	protected void update(NetworkConnectionEvent anEvent) {
		for (MediaEventListener m : mediaEventListenerList) {
			m.onEvent(anEvent);
		}
	}

	private class CreateTx implements Runnable, JainMgcpExtendedListener {
		private NetworkConnectionImpl networkConnectionImpl;
		private int tx = -1;

		public CreateTx(NetworkConnectionImpl networkConnectionImpl) {
			this.networkConnectionImpl = networkConnectionImpl;
		}

		public void run() {
			try {
				this.tx = mgcpWrapper.getUniqueTransactionHandler();
				mgcpWrapper.addListnere(this.tx, this);
				CallIdentifier callId = mediaSession.getCallIdentifier();
				EndpointIdentifier endpointID = new EndpointIdentifier(endpoint, mgcpWrapper.getPeerIp() + ":"
						+ mgcpWrapper.getPeerPort());

				CreateConnection createConnection = new CreateConnection(this, callId, endpointID,
						ConnectionMode.SendRecv);
				if (remoteSessionDescription != null) {
					createConnection.setRemoteConnectionDescriptor(new ConnectionDescriptor(remoteSessionDescription));
				}

				createConnection.setTransactionHandle(tx);
				mgcpWrapper.sendMgcpEvents(new JainMgcpEvent[] { createConnection });

			} catch (ConflictingParameterException e) {
				e.printStackTrace();
			}
		}

		public void transactionEnded(int arg0) {
			if (logger.isDebugEnabled()) {
				logger.debug("Successfully completed Tx = " + arg0);
			}
		}

		public void transactionRxTimedOut(JainMgcpCommandEvent arg0) {
		}

		public void transactionTxTimedOut(JainMgcpCommandEvent jainMgcpCommandEvent) {
			logger.error("No response from MGW. Tx timed out for MGCP Tx " + this.tx + " For Command sent "
					+ jainMgcpCommandEvent.toString());
			mgcpWrapper.removeListener(jainMgcpCommandEvent.getTransactionHandle());
			NetworkConnectionEvent networkConnectionEvent = new NetworkConnectionEventImpl(this.networkConnectionImpl,
					NetworkConnection.e_ResourceNotAvailable, Error.e_System, "No response from MGW for modify");
			update(networkConnectionEvent);
		}

		public void processMgcpCommandEvent(JainMgcpCommandEvent arg0) {
		}

		public void processMgcpResponseEvent(JainMgcpResponseEvent jainmgcpresponseevent) {

			// TODO : Depending on Response we get fire corresponding JSR 309
			// events here

			switch (jainmgcpresponseevent.getObjectIdentifier()) {
			case Constants.RESP_CREATE_CONNECTION:
				processCreateConnectionResponse((CreateConnectionResponse) jainmgcpresponseevent);
				break;
			default:
				mgcpWrapper.removeListener(jainmgcpresponseevent.getTransactionHandle());
				logger.warn(" This RESPONSE is unexpected " + jainmgcpresponseevent);
				NetworkConnectionEvent networkConnectionEvent = new NetworkConnectionEventImpl(
						this.networkConnectionImpl, NetworkConnection.e_ResourceNotAvailable, Error.e_System,
						"modify failed. Look at logs " + jainmgcpresponseevent.getReturnCode().getComment());
				update(networkConnectionEvent);
				break;

			}
		}

		private void processCreateConnectionResponse(CreateConnectionResponse responseEvent) {
			logger.debug(" processCreateConnectionResponse() ");
			NetworkConnectionEvent networkConnectionEvent = null;
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
				connectionIdentifier = responseEvent.getConnectionIdentifier();
				endpointIdentifier = responseEvent.getSpecificEndpointIdentifier();
				endpoint = endpointIdentifier.getLocalEndpointName();

				if (logger.isDebugEnabled()) {
					logger.debug(" TRANSACTION_EXECUTED_NORMALLY for connectionIdentifier = " + connectionIdentifier
							+ " endpointID = " + endpointIdentifier);
				}
				localSessionDescription = responseEvent.getLocalConnectionDescriptor().toString();

				if (audioJoinableStream == null) {
					audioJoinableStream = new AudioJoinableStream(this.networkConnectionImpl);
				}
				networkConnectionEvent = new NetworkConnectionEventImpl(this.networkConnectionImpl,
						NetworkConnection.ev_Modify);
				update(networkConnectionEvent);

				break;
			case ReturnCode.ENDPOINT_INSUFFICIENT_RESOURCES:
				mgcpWrapper.removeListener(responseEvent.getTransactionHandle());
				networkConnectionEvent = new NetworkConnectionEventImpl(this.networkConnectionImpl,
						NetworkConnection.e_ResourceNotAvailable, Error.e_ResourceUnavailable, returnCode.getComment());
				update(networkConnectionEvent);
				break;
			default:
				logger.error(" SOMETHING IS BROKEN = " + responseEvent);
				mgcpWrapper.removeListener(responseEvent.getTransactionHandle());
				networkConnectionEvent = new NetworkConnectionEventImpl(this.networkConnectionImpl,
						NetworkConnection.e_ResourceNotAvailable, Error.e_System, returnCode.getComment());
				update(networkConnectionEvent);
				break;

			}

		}

	}

	private class ModifyTx implements Runnable, JainMgcpExtendedListener {
		private NetworkConnectionImpl networkConnectionImpl;
		private int tx = -1;

		public ModifyTx(NetworkConnectionImpl networkConnectionImpl) {
			this.networkConnectionImpl = networkConnectionImpl;
		}

		public void run() {

			try {
				this.tx = mgcpWrapper.getUniqueTransactionHandler();
				mgcpWrapper.addListnere(this.tx, this);

				CallIdentifier callId = mediaSession.getCallIdentifier();
				ModifyConnection modifyConnection = new ModifyConnection(this, callId, endpointIdentifier,
						connectionIdentifier);

				if (remoteSessionDescription != null) {
					modifyConnection.setRemoteConnectionDescriptor(new ConnectionDescriptor(remoteSessionDescription));
				}

				modifyConnection.setTransactionHandle(tx);
				mgcpWrapper.sendMgcpEvents(new JainMgcpEvent[] { modifyConnection });
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public void transactionEnded(int arg0) {
			// TODO Auto-generated method stub

		}

		public void transactionRxTimedOut(JainMgcpCommandEvent arg0) {
			// TODO Auto-generated method stub

		}

		public void transactionTxTimedOut(JainMgcpCommandEvent jainMgcpCommandEvent) {
			logger.error("No response from MGW. Tx timed out for MGCP Tx " + this.tx + " For Command sent "
					+ jainMgcpCommandEvent.toString());
			mgcpWrapper.removeListener(jainMgcpCommandEvent.getTransactionHandle());
			NetworkConnectionEvent networkConnectionEvent = new NetworkConnectionEventImpl(this.networkConnectionImpl,
					NetworkConnection.e_ResourceNotAvailable, Error.e_System, "No response from MGW for modify");
			update(networkConnectionEvent);

		}

		public void processMgcpCommandEvent(JainMgcpCommandEvent arg0) {
			// TODO Auto-generated method stub

		}

		public void processMgcpResponseEvent(JainMgcpResponseEvent jainmgcpresponseevent) {

			// TODO : Depending on Response we get fire corresponding JSR 309
			// events here

			switch (jainmgcpresponseevent.getObjectIdentifier()) {

			case Constants.RESP_MODIFY_CONNECTION:
				processMofiyConnectionResponse((ModifyConnectionResponse) jainmgcpresponseevent);
				break;
			default:
				mgcpWrapper.removeListener(jainmgcpresponseevent.getTransactionHandle());
				logger.warn(" This RESPONSE is unexpected " + jainmgcpresponseevent);
				NetworkConnectionEvent networkConnectionEvent = new NetworkConnectionEventImpl(
						this.networkConnectionImpl, NetworkConnection.e_ResourceNotAvailable, Error.e_System,
						"modify failed. Look at logs ");
				update(networkConnectionEvent);
				break;

			}
		}

		private void processMofiyConnectionResponse(ModifyConnectionResponse responseEvent) {
			logger.debug(" processMofiyConnectionResponse() ");
			NetworkConnectionEvent networkConnectionEvent = null;
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
				if (logger.isDebugEnabled()) {
					logger.debug(" MDCX TRANSACTION_EXECUTED_NORMALLY for connectionIdentifier = "
							+ connectionIdentifier + "endpointID = " + endpointIdentifier);
				}

				if (responseEvent.getLocalConnectionDescriptor() != null) {
					localSessionDescription = responseEvent.getLocalConnectionDescriptor().toString();
				}
				networkConnectionEvent = new NetworkConnectionEventImpl(this.networkConnectionImpl,
						NetworkConnection.ev_Modify);
				update(networkConnectionEvent);

				break;
			case ReturnCode.ENDPOINT_INSUFFICIENT_RESOURCES:
				mgcpWrapper.removeListener(responseEvent.getTransactionHandle());
				networkConnectionEvent = new NetworkConnectionEventImpl(this.networkConnectionImpl,
						NetworkConnection.e_ResourceNotAvailable, Error.e_ResourceUnavailable, returnCode.getComment());
				update(networkConnectionEvent);

				break;
			default:
				mgcpWrapper.removeListener(responseEvent.getTransactionHandle());
				logger.error(" SOMETHING IS BROKEN = " + responseEvent);
				networkConnectionEvent = new NetworkConnectionEventImpl(this.networkConnectionImpl,
						NetworkConnection.e_ResourceNotAvailable, Error.e_System, returnCode.getComment());
				update(networkConnectionEvent);

				break;

			}

		}
	}

	private class DeleteTx implements Runnable, JainMgcpExtendedListener {

		private NetworkConnectionImpl networkConnectionImpl;
		private int tx = -1;

		public DeleteTx(NetworkConnectionImpl networkConnectionImpl) {
			this.networkConnectionImpl = networkConnectionImpl;
		}

		public void run() {
			try {
				this.tx = mgcpWrapper.getUniqueTransactionHandler();
				// TODO : Do we need to fire event for DLCX?
				mgcpWrapper.addListnere(this.tx, this);

				CallIdentifier callId = mediaSession.getCallIdentifier();
				DeleteConnection deleteConnection = new DeleteConnection(this, callId, endpointIdentifier,
						connectionIdentifier);

				deleteConnection.setTransactionHandle(tx);
				mgcpWrapper.sendMgcpEvents(new JainMgcpEvent[] { deleteConnection });
			} catch (Exception e) {
				logger.error(e);
			}
		}

		public void transactionEnded(int arg0) {
			// TODO Auto-generated method stub

		}

		public void transactionRxTimedOut(JainMgcpCommandEvent arg0) {
			// TODO Auto-generated method stub

		}

		public void transactionTxTimedOut(JainMgcpCommandEvent arg0) {
			// TODO Auto-generated method stub

		}

		public void processMgcpCommandEvent(JainMgcpCommandEvent arg0) {
			// TODO Auto-generated method stub

		}

		public void processMgcpResponseEvent(JainMgcpResponseEvent response) {
			if (response.getTransactionHandle() != this.tx) {
				return;
			}

			switch (response.getObjectIdentifier()) {

			case Constants.RESP_DELETE_CONNECTION:
				processDeleteConnectionResponse((DeleteConnectionResponse) response);
				break;
			default:
				mgcpWrapper.removeListener(response.getTransactionHandle());
				logger.warn(" DLCX of Netwrok connction failed RESPONSE is unexpected " + response);
				// NetworkConnectionEvent networkConnectionEvent = new
				// NetworkConnectionEventImpl(
				// this.networkConnectionImpl,
				// NetworkConnection.e_ResourceNotAvailable, Error.e_System,
				// "Delete failed. Look at logs ");
				// update(networkConnectionEvent);
				break;

			}
		}

		private void processDeleteConnectionResponse(DeleteConnectionResponse responseEvent) {
			ReturnCode returnCode = responseEvent.getReturnCode();
			JoinEvent joinEvent = null;
			switch (returnCode.getValue()) {
			case ReturnCode.TRANSACTION_BEING_EXECUTED:
				// do nothing
				if (logger.isDebugEnabled()) {
					logger.debug("Transaction " + responseEvent.getTransactionHandle()
							+ "is being executed. Response received = " + responseEvent);
				}
				break;
			case ReturnCode.TRANSACTION_EXECUTED_NORMALLY:
				if (logger.isDebugEnabled()) {
					logger.debug("DLCX executed successfully for Tx = " + responseEvent.getTransactionHandle());
				}
				break;
			default:
				logger.error(" SOMETHING IS BROKEN = " + responseEvent);
				break;
			}

		}

	}

	public Parameters createParameters() {
		return new ParametersImpl();
	}

	@Override
	protected void resetContainer() {
		// App didn't call NC.modify() yet. We can still reuse this NC object
		if (this.endpointIdentifier == null) {
			this.audioJoinableStream = null;
			this.endpoint = PR_ENDPOINT_NAME;
		}
	}

	protected void checkState() {
		if (this.state.equals(MediaObjectState.RELEASED)) {
			throw new IllegalStateException("State of container " + this.getURI() + " is released");
		}
	}

	@Override
	protected void joined(ConnectionIdentifier thisConnId, ConnectionIdentifier otherConnId) {
		// TODO Do we want to preserve the connectionId of 2nd Connection of PR?
	}

	@Override
	protected void unjoined(ConnectionIdentifier thisConnId, ConnectionIdentifier otherConnId) {
		// TODO any further cleaning action?
	}

	@Override
	protected MediaObjectState getState() {
		return this.state;
	}

}
