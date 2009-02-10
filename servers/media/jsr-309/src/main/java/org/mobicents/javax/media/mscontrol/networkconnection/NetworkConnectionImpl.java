package org.mobicents.javax.media.mscontrol.networkconnection;

import jain.protocol.ip.mgcp.JainMgcpCommandEvent;
import jain.protocol.ip.mgcp.JainMgcpEvent;
import jain.protocol.ip.mgcp.JainMgcpResponseEvent;
import jain.protocol.ip.mgcp.message.CreateConnection;
import jain.protocol.ip.mgcp.message.parms.CallIdentifier;
import jain.protocol.ip.mgcp.message.parms.ConflictingParameterException;
import jain.protocol.ip.mgcp.message.parms.ConnectionDescriptor;
import jain.protocol.ip.mgcp.message.parms.ConnectionMode;
import jain.protocol.ip.mgcp.message.parms.EndpointIdentifier;

import java.io.Serializable;
import java.net.URI;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.media.mscontrol.Joinable;
import javax.media.mscontrol.JoinableStream;
import javax.media.mscontrol.MediaSession;
import javax.media.mscontrol.MscontrolException;
import javax.media.mscontrol.StatusEventListener;
import javax.media.mscontrol.JoinableStream.StreamType;
import javax.media.mscontrol.networkconnection.NetworkConnection;
import javax.media.mscontrol.networkconnection.NetworkConnectionConfig;
import javax.media.mscontrol.networkconnection.NetworkConnectionEvent;
import javax.media.mscontrol.networkconnection.NetworkConnectionException;
import javax.media.mscontrol.networkconnection.ResourceNotAvailableException;
import javax.media.mscontrol.resource.MediaEvent;
import javax.media.mscontrol.resource.MediaEventListener;
import javax.media.mscontrol.resource.Parameters;
import javax.media.mscontrol.resource.Symbol;
import javax.sdp.SdpException;
import javax.sdp.SessionDescription;

import org.mobicents.javax.media.mscontrol.MediaSessionFactoryImpl;
import org.mobicents.javax.media.mscontrol.MediaSessionImpl;
import org.mobicents.jsr309.mgcp.JainMgcpExtendedListenerImpl;
import org.mobicents.jsr309.mgcp.Provider;
import org.mobicents.mgcp.stack.JainMgcpExtendedListener;
import org.mobicents.mgcp.stack.JainMgcpStackProviderImpl;

public class NetworkConnectionImpl implements NetworkConnection,
		JainMgcpExtendedListener {

	private MediaSessionImpl mediaSession = null;
	protected JainMgcpStackProviderImpl jainMgcpStackProviderImpl;
	protected JainMgcpExtendedListenerImpl jainMgcpListenerImpl;
	protected int tx = -1;
	private String localEndpointName = null;
	private String remoteSessionDescription = null;
	private String localSessionDescription = null;
	private static final String PR_ENDPOINT_NAME = "/trunk/media/PacketRelay/$";

	protected CopyOnWriteArrayList<MediaEventListener<? extends MediaEvent<?>>> mediaEventListenerList = new CopyOnWriteArrayList<MediaEventListener<? extends MediaEvent<?>>>();

	public NetworkConnectionImpl(MediaSessionImpl mediaSession,
			JainMgcpStackProviderImpl jainMgcpStackProviderImpl,
			JainMgcpExtendedListenerImpl jainMgcpListenerImpl) {
		this.mediaSession = mediaSession;
		this.jainMgcpStackProviderImpl = jainMgcpStackProviderImpl;
		this.jainMgcpListenerImpl = jainMgcpListenerImpl;
	}

	public SessionDescription getLocalSessionDescription()
			throws NetworkConnectionException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getRawLocalSessionDescription()
			throws NetworkConnectionException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getRawRemoteSessionDescription()
			throws NetworkConnectionException {
		// TODO Auto-generated method stub
		return null;
	}

	public SessionDescription getRemoteSessionDescription()
			throws NetworkConnectionException {
		// TODO Auto-generated method stub
		return null;
	}

	public void modify(SessionDescription localSessionDescription,
			SessionDescription remoteSessionDescription) throws SdpException,
			NetworkConnectionException, ResourceNotAvailableException {
		// TODO Auto-generated method stub

	}

	public void modify(String localSessionDescription,
			String remoteSessionDescription) throws SdpException,
			NetworkConnectionException, ResourceNotAvailableException {
		this.remoteSessionDescription = remoteSessionDescription;
		this.localSessionDescription = localSessionDescription;

		Runnable tx = this.localEndpointName == null ? new CreateTx(this)
				: new ModifyTx(this);

		// TODO assuming that modify is async opertaion. Can it be sync too?
		Provider.submit(tx);
	}

	public JoinableStream getJoinableStream(StreamType value)
			throws MscontrolException {
		// TODO Auto-generated method stub
		return null;
	}

	public JoinableStream[] getJoinableStreams() throws MscontrolException {
		// TODO Auto-generated method stub
		return null;
	}

	public Joinable[] getJoinees() throws MscontrolException {
		// TODO Auto-generated method stub
		return null;
	}

	public Joinable[] getJoinees(Direction direction) throws MscontrolException {
		// TODO Auto-generated method stub
		return null;
	}

	public void join(Direction direction, Joinable other)
			throws MscontrolException {
		// TODO Auto-generated method stub

	}

	public void joinInitiate(Direction direction, Joinable other,
			Serializable context) throws MscontrolException {
		// TODO Auto-generated method stub

	}

	public void unjoin(Joinable other) throws MscontrolException {
		// TODO Auto-generated method stub

	}

	public void unjoinInitiate(Joinable other, Serializable context)
			throws MscontrolException {
		// TODO Auto-generated method stub

	}

	public void addListener(StatusEventListener listener) {
		// TODO Auto-generated method stub

	}

	public MediaSession getMediaSession() {
		return this.mediaSession;
	}

	public void removeListener(StatusEventListener listener) {
		// TODO Auto-generated method stub

	}

	public void confirm() throws MscontrolException {
		// TODO Auto-generated method stub

	}

	public NetworkConnectionConfig getConfig() {
		// TODO Auto-generated method stub
		return null;
	}

	public <R> R getResource(Class<R> resource) throws MscontrolException {
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
		// TODO Auto-generated method stub
		return null;
	}

	public void release() {
		// TODO Auto-generated method stub

	}

	public void setParameters(Parameters params) {
		// TODO Auto-generated method stub

	}

	public void addListener(MediaEventListener<NetworkConnectionEvent> listener) {
		this.mediaEventListenerList.add(listener);

	}

	public void removeListener(
			MediaEventListener<NetworkConnectionEvent> listener) {
		this.mediaEventListenerList.remove(listener);
	}

	/***************************************************************************
	 * JainMgcpExtendedListener method calls
	 **************************************************************************/

	public void processMgcpCommandEvent(
			JainMgcpCommandEvent jainmgcpcommandevent) {
		if (jainmgcpcommandevent.getEndpointIdentifier().getLocalEndpointName()
				.equals(this.localEndpointName)) {

		}
	}

	public void processMgcpResponseEvent(
			JainMgcpResponseEvent jainmgcpresponseevent) {
		if (jainmgcpresponseevent.getTransactionHandle() != this.tx) {
			return;
		}
		// TODO : Depending on Response we get fire corresponding JSR 309 events
		// here
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

	private class CreateTx implements Runnable {
		private NetworkConnectionImpl networkConnectionImpl;

		public CreateTx(NetworkConnectionImpl networkConnectionImpl) {
			this.networkConnectionImpl = networkConnectionImpl;
		}

		public void run() {
			try {
				jainMgcpListenerImpl
						.addJainMgcpListenerImpl(this.networkConnectionImpl);

				CallIdentifier callId = mediaSession.getCallIdentifier();
				tx = jainMgcpStackProviderImpl.getUniqueTransactionHandler();

				EndpointIdentifier endpointID = new EndpointIdentifier(
						PR_ENDPOINT_NAME,
						MediaSessionFactoryImpl.mgcpStackPeerIp + ":"
								+ MediaSessionFactoryImpl.mgcpStackPeerPort);

				CreateConnection createConnection = new CreateConnection(this,
						callId, endpointID, ConnectionMode.SendRecv);
				if (remoteSessionDescription != null) {
					createConnection
							.setRemoteConnectionDescriptor(new ConnectionDescriptor(
									remoteSessionDescription));
				}

				createConnection.setTransactionHandle(tx);
				jainMgcpStackProviderImpl
						.sendMgcpEvents(new JainMgcpEvent[] { createConnection });

			} catch (ConflictingParameterException e) {
				e.printStackTrace();
			}
		}

	}

	private class ModifyTx implements Runnable {
		private NetworkConnectionImpl networkConnectionImpl;

		public ModifyTx(NetworkConnectionImpl networkConnectionImpl) {
			this.networkConnectionImpl = networkConnectionImpl;
		}

		public void run() {

		}
	}

}
