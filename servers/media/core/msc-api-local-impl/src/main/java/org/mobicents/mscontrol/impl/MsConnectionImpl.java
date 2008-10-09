/*
 * MsConnectionImpl.java
 *
 * The Simple Media API RA
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
package org.mobicents.mscontrol.impl;

import java.io.IOException;
import java.rmi.server.UID;
import java.util.ArrayList;

import javax.naming.NamingException;
import javax.sdp.SdpException;

import org.apache.log4j.Logger;
import org.mobicents.media.server.spi.Connection;
import org.mobicents.media.server.spi.ConnectionListener;
import org.mobicents.media.server.spi.ConnectionMode;
import org.mobicents.media.server.spi.ConnectionState;
import org.mobicents.media.server.spi.EndpointQuery;
import org.mobicents.media.server.spi.NotificationListener;
import org.mobicents.media.server.spi.ResourceUnavailableException;
import org.mobicents.media.server.spi.TooManyConnectionsException;
import org.mobicents.media.server.spi.events.NotifyEvent;
import org.mobicents.mscontrol.MsConnection;
import org.mobicents.mscontrol.MsConnectionEventCause;
import org.mobicents.mscontrol.MsConnectionEventID;
import org.mobicents.mscontrol.MsConnectionListener;
import org.mobicents.mscontrol.MsConnectionState;
import org.mobicents.mscontrol.MsEndpoint;
import org.mobicents.mscontrol.MsNotificationListener;
import org.mobicents.mscontrol.MsNotifyEvent;
import org.mobicents.mscontrol.MsSession;
import org.mobicents.mscontrol.impl.events.EventParser;

import EDU.oswego.cs.dl.util.concurrent.QueuedExecutor;
import EDU.oswego.cs.dl.util.concurrent.SynchronizedInt;
import EDU.oswego.cs.dl.util.concurrent.ThreadFactory;

/**
 * 
 * @author Oleg Kulikov
 * @author amit bhayani
 */
public class MsConnectionImpl implements MsConnection, ConnectionListener, NotificationListener {

	private String id = (new UID()).toString();
	private MsConnectionState state;
	private String remoteSdp;
	protected MsSessionImpl session;
	private String endpointName;
	protected Connection connection;
	private MsEndpointImpl endpoint;
	protected ArrayList<MsNotificationListener> eventListeners = new ArrayList();
	private transient Logger logger = Logger.getLogger(MsConnectionImpl.class);
	private QueuedExecutor eventQueue = new QueuedExecutor();
	private ThreadFactory threadFactory;
	private EventParser eventParser = new EventParser();

	/**
	 * Creates a new instance of MsConnectionImpl
	 * 
	 * @params session the session object to which this connections belongs.
	 * @param endpointName
	 *            the name of the endpoint.
	 */
	public MsConnectionImpl(MsSessionImpl session, String endpointName) {
		this.session = session;
		this.endpointName = endpointName;

		threadFactory = new ThreadFactory() {

			SecurityManager s = System.getSecurityManager();
			ThreadGroup group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
			final SynchronizedInt i = new SynchronizedInt(0);

			public Thread newThread(final Runnable runnable) {
				return new Thread(group, runnable, "MsConnectionImpl-QueuedExecutor-Thread-" + i.increment());
			}
		};

		eventQueue.setThreadFactory(threadFactory);
		
		setState(MsConnectionState.IDLE, MsConnectionEventCause.NORMAL);

	}

	public String getId() {
		return this.id;
	}

	/**
	 * (Non Java-doc).
	 * 
	 * @see org.mobicents.mscontrol.MsConnection#getSession();
	 */
	public MsSession getSession() {
		return session;
	}

	/**
	 * (Non Java-doc).
	 * 
	 * @see org.mobicents.mscontrol.MsConnection#getLocalDescriptor();
	 */
	public String getLocalDescriptor() {
		return connection != null ? connection.getLocalDescriptor() : null;
	}

	/**
	 * (Non Java-doc).
	 * 
	 * @see org.mobicents.mscontrol.MsConnection#getLocalDescriptor();
	 */
	public String getRemoteDescriptor() {
		return connection != null ? connection.getRemoteDescriptor() : null;
	}

	/**
	 * (Non Java-doc).
	 * 
	 * @see org.mobicents.mscontrol.MsConnection#getEndpoint();
	 */
	public MsEndpoint getEndpoint() {
		return endpoint;
	}

	/**
	 * (Non Java-doc).
	 * 
	 * @see org.mobicents.mscontrol.MsConnection#addConectionListener(MsConnectionListener);
	 */
	public void addConnectionListener(MsConnectionListener listener) {
		session.provider.connectionListeners.add(listener);
	}

	/**
	 * (Non Java-doc).
	 * 
	 * @see org.mobicents.mscontrol.MsConnection#removeConectionListener(MsConnectionListener);
	 */
	public void removeConnectionListener(MsConnectionListener listener) {
		session.provider.connectionListeners.remove(listener);
	}

	/**
	 * (Non Java-doc).
	 * 
	 * @see org.mobicents.mscontrol.MsConnection#modify();
	 */
	public void modify(String localDesc, String remoteDesc) {
		this.remoteSdp = remoteDesc;
		Runnable tx = endpoint == null ? new CreateTx(this) : new ModifyTx(this);
		MsProviderImpl.submit(tx);
	}

	/**
	 * (Non Java-doc).
	 * 
	 * @see org.mobicents.mscontrol.MsConnection#release();
	 */
	public void release() {
		if (endpoint != null) {
			Runnable tx = new DeleteTx();
			MsProviderImpl.submit(tx);
		}
	}

	private synchronized void sendEvent(MsConnectionEventID eventID, MsConnectionEventCause cause, String msg) {
		MsConnectionEventImpl evt = new MsConnectionEventImpl(this, eventID, cause, msg);
		try {
			eventQueue.execute(evt);
		} catch (InterruptedException e) {
		}
	}

	public MsConnectionState getState() {
		return state;
	}

	private void setState(MsConnectionState state, MsConnectionEventCause cause) {
		this.state = state;
		switch (state) {
		case IDLE:
			sendEvent(MsConnectionEventID.CONNECTION_CREATED, cause, null);
			break;
		case HALF_OPEN:
			sendEvent(MsConnectionEventID.CONNECTION_HALF_OPEN, cause, null);
			break;
		case OPEN:
			sendEvent(MsConnectionEventID.CONNECTION_OPEN, cause, null);
			break;
		case FAILED:
			// send event and imidiately trnasit to CLOSED state
			sendEvent(MsConnectionEventID.CONNECTION_FAILED, cause, null);
			setState(MsConnectionState.CLOSED, cause);
			break;
		case CLOSED:
			session.removeConnection(this);
			sendEvent(MsConnectionEventID.CONNECTION_DISCONNECTED, cause, null);
			eventQueue.shutdownAfterProcessingCurrentlyQueuedTasks();
		}
	}

	@Override
	public String toString() {
		return id;
	}

	private class CreateTx implements Runnable {

		private MsConnectionImpl localConnection;

		public CreateTx(MsConnectionImpl localConnection) {
			this.localConnection = localConnection;
		}

		public void run() {
			try {
				endpoint = new MsEndpointImpl(EndpointQuery.lookup(endpointName), session.getProvider());
				endpointName = endpoint.server.getLocalName();

				logger.debug("Media server returns endpoint: " + endpoint.server.getLocalName());

				connection = endpoint.server.createConnection(ConnectionMode.SEND_RECV);
				connection.addListener(localConnection);
				if (remoteSdp != null) {
					connection.setRemoteDescriptor(remoteSdp);
				}
			} catch (NamingException e) {
				setState(MsConnectionState.FAILED, MsConnectionEventCause.ENDPOINT_UNKNOWN);
			} catch (ResourceUnavailableException e) {
				setState(MsConnectionState.FAILED, MsConnectionEventCause.FACILITY_FAILURE);
			} catch (TooManyConnectionsException e) {
				setState(MsConnectionState.FAILED, MsConnectionEventCause.FACILITY_FAILURE);
			} catch (SdpException e) {
				setState(MsConnectionState.FAILED, MsConnectionEventCause.REMOTE_SDP_INVALID);
			} catch (IOException e) {
				setState(MsConnectionState.FAILED, MsConnectionEventCause.FACILITY_FAILURE);
			} catch (Exception e) {
				setState(MsConnectionState.FAILED, MsConnectionEventCause.FACILITY_FAILURE);
			}
		}
	}

	private class ModifyTx implements Runnable {

		private MsConnectionImpl localConnection;

		public ModifyTx(MsConnectionImpl localConnection) {
			this.localConnection = localConnection;
		}

		public void run() {
			if (remoteSdp != null) {
				try {
					connection.setRemoteDescriptor(remoteSdp);
				} catch (SdpException ex) {
					setState(MsConnectionState.FAILED, MsConnectionEventCause.REMOTE_SDP_INVALID);
				} catch (IOException ex) {
					setState(MsConnectionState.FAILED, MsConnectionEventCause.FACILITY_FAILURE);
				} catch (ResourceUnavailableException ex) {
					setState(MsConnectionState.FAILED, MsConnectionEventCause.REMOTE_SDP_INVALID);
				}
			}
		}
	}

	private class DeleteTx implements Runnable {
		public void run() {
			if (connection != null) {
				endpoint.server.deleteConnection(connection.getId());
			}
		}
	}

	public void update(NotifyEvent event) {
		System.out.println("MsConnection: receive event");
		MsNotifyEvent evt = eventParser.parse(this, event);
		for (MsNotificationListener listener : session.provider.eventListeners) {
			System.out.println("MsConnectio nSending event to " + listener);
			listener.update(evt);
		}
	}

	public void onStateChange(Connection connection, ConnectionState oldState) {
		switch (connection.getState()) {
		case NULL:
			setState(MsConnectionState.IDLE, MsConnectionEventCause.NORMAL);
			break;
		case HALF_OPEN:
			setState(MsConnectionState.HALF_OPEN, MsConnectionEventCause.NORMAL);
			break;
		case OPEN:
			setState(MsConnectionState.OPEN, MsConnectionEventCause.NORMAL);
			break;
		case CLOSED:
			setState(MsConnectionState.CLOSED, MsConnectionEventCause.NORMAL);
			break;
		}
	}
}
