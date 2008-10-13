/*
 * Mobicents Media Gateway
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
package org.mobicents.media.server.impl;

import java.util.ArrayList;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.log4j.Logger;
import org.jboss.util.id.UID;
import org.mobicents.media.server.local.management.ConnectionLocalManagement;
import org.mobicents.media.server.spi.Connection;
import org.mobicents.media.server.spi.ConnectionListener;
import org.mobicents.media.server.spi.ConnectionMode;
import org.mobicents.media.server.spi.ConnectionState;
import org.mobicents.media.server.spi.Endpoint;
import org.mobicents.media.server.spi.NotificationListener;
import org.mobicents.media.server.spi.ResourceUnavailableException;
import org.mobicents.media.server.spi.events.NotifyEvent;
import org.mobicents.media.server.spi.events.RequestedEvent;

/**
 * 
 * @author Oleg Kulikov
 */
public abstract class BaseConnection implements Connection, NotificationListener , ConnectionLocalManagement{

	protected String id;
	protected BaseEndpoint endpoint;
	protected String endpointName;
	protected ConnectionMode mode;
	protected ConnectionState state = ConnectionState.NULL;
	private int lifeTime = 30;
	protected Multiplexer mux;
	protected Demultiplexer demux;
	private TimerTask closeTask;
	private boolean timerStarted = false;
	private ReentrantLock stateLock = new ReentrantLock();
	private ArrayList<ConnectionListener> listeners = new ArrayList();
	private ArrayList<RequestedEvent> eventListeners = new ArrayList();
	private ExecutorService eventQueue = Executors.newSingleThreadExecutor(new BaseConnection.ThreadFactoryImpl());
	protected transient Logger logger = Logger.getLogger(BaseConnection.class);
	private long connectionCreationTime = System.currentTimeMillis();
	private long packets;

	private class CloseConnectionTask extends TimerTask {

		public void run() {
			logger.info("Connection timer expired, Disconnecting");
			timerStarted = false;
			endpoint.deleteConnection(id);

			eventQueue.shutdown();
		}
	}

	public BaseConnection(Endpoint endpoint, ConnectionMode mode, ConnectionListener initial) throws ResourceUnavailableException {
		// FIXME: baranowb: this is not required in base - as this is the
		// "atomic operation"
		// try {
		this.id = genID();
		this.mode = mode;

		this.endpoint = (BaseEndpoint) endpoint;
		this.endpointName = endpoint.getLocalName();

		mux = new Multiplexer();
		demux = new Demultiplexer(this.endpoint.getSupportedFormats());

		this.addListener(initial);
		setLifeTime(lifeTime);
		setState(ConnectionState.NULL);
		// } catch (InterruptedException e) {
		//	
		// e.printStackTrace();
		// throw new ResourceUnavailableException("Failed to acquire lock,
		// possibly server is shutting down?");
		// } finally {
		// this.releaseState();
		// }
	}

	/**
	 * (Non-Javadoc).
	 * 
	 * @see org.mobicents.media.server.spi.Connection#getId();
	 */
	public String getId() {
		return id;
	}

	/**
	 * Gets the current state of this connection.
	 * 
	 * @return current state;
	 */
	public ConnectionState getState() {
		return this.state;
	}

	/**
	 * Non Java-doc).
	 * 
	 * @see org.mobicents.media.server.spi.Connection#getLifeTime().
	 */
	public int getLifeTime() {
		return lifeTime;
	}

	/**
	 * Non Java-doc).
	 * 
	 * @see org.mobicents.media.server.spi.Connection#setLifeTime(int).
	 */
	public void setLifeTime(int lifeTime) {
		this.lifeTime = lifeTime;
		if (timerStarted) {
			closeTask.cancel();
			BaseEndpoint.connectionTimer.purge();
			timerStarted = false;
		}
		this.closeTask = new CloseConnectionTask();
		BaseEndpoint.connectionTimer.schedule(closeTask, 60 * lifeTime * 1000);
		timerStarted = true;
	}

	/**
	 * Generates unique identifier for this connection.
	 * 
	 * @return hex view of the unique integer.
	 */
	private String genID() {
		return (new UID()).toString();
	}

	/**
	 * (Non-Javadoc).
	 * 
	 * @see org.mobicents.media.server.spi.Connection#getMode();
	 */
	public ConnectionMode getMode() {
		return mode;
	}

	/**
	 * (Non-Javadoc).
	 * 
	 * @see org.mobicents.media.server.spi.Connection#setMode(int);
	 */
	public void setMode(ConnectionMode mode) {
		this.mode = mode;
		// @todo rebuilt send/recv streams.
	}

	/**
	 * (Non-Javadoc).
	 * 
	 * @see org.mobicents.media.server.spi.Connection#getEndpoint(int);
	 */
	public Endpoint getEndpoint() {
		return endpoint;
	}

	public Multiplexer getMux() {
		return mux;
	}

	public Demultiplexer getDemux() {
		return demux;
	}

	/**
	 * Adds listener for the specified event.
	 * 
	 * @param requestedEvent
	 *            the event to detect or null to diable detection.
	 */
	public void detect(RequestedEvent requestedEvent) {
		synchronized (eventListeners) {
			if (requestedEvent != null) {
				eventListeners.add(requestedEvent);
			} else {
				eventListeners.clear();
			}
		}
	}

	/**
	 * (Non-Javadoc).
	 * 
	 * @see org.mobicents.media.server.spi.Connection#addListener(ConnectionListener)
	 */
	public void addListener(ConnectionListener listener) {
		listeners.add(listener);
	}

	/**
	 * (Non-Javadoc).
	 * 
	 * @see org.mobicents.media.server.spi.Connection#removeListener(ConnectionListener)
	 */
	public void removeListener(ConnectionListener listener) {
		listeners.remove(listener);
	}

	protected void setState(ConnectionState newState) {
		
		if(newState==this.state)
		{
			return;
		}
		ConnectionState oldState = this.state;
		this.state = newState;
		
		
		for (ConnectionListener cl : listeners) {
			cl.onStateChange(this, oldState);
		}

	}

	public void update(NotifyEvent event) {
		System.out.println("Event=" + event.getEventID().getFQN());
		synchronized (eventListeners) {
			for (RequestedEvent request : this.eventListeners) {
				if (request.getID().equals(event.getEventID())) {
					System.out.println("Sending event=" + event.getEventID() + " to " + request.getHandler());
					eventQueue.submit(new EventSender(request.getHandler(), event));
				}
			}
		}
	}

	/**
	 * Used for test cases only.
	 * 
	 * @return true if life time started.
	 */
	protected boolean getLifeTimeTimerState() {
		return timerStarted;
	}

	/**
	 * Releases all resources requested by this connection.
	 * 
	 * @throws InterruptedException
	 */
	public void close() {
		if (timerStarted) {
			closeTask.cancel();
			timerStarted = false;
			BaseEndpoint.connectionTimer.purge();
		}

		this.setState(ConnectionState.CLOSED);
		eventQueue.shutdown();
	}

	public void lockState() throws InterruptedException {
		this.stateLock.lock();
	}

	public void releaseState() {
		this.stateLock.unlock();
	}

	private static class ThreadFactoryImpl implements ThreadFactory {

		final ThreadGroup group;
		static final AtomicInteger baseConnectionPoolNumber = new AtomicInteger(1);
		final AtomicInteger threadNumber = new AtomicInteger(1);
		final String namePrefix;

		ThreadFactoryImpl() {
			SecurityManager s = System.getSecurityManager();
			group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
			namePrefix = "BaseConnection-SingleThreadExecutor-" + baseConnectionPoolNumber.getAndIncrement()
					+ "thread-";
		}

		public Thread newThread(Runnable r) {
			Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
			if (t.isDaemon())
				t.setDaemon(false);
			if (t.getPriority() != Thread.NORM_PRIORITY)
				t.setPriority(Thread.NORM_PRIORITY);
			return t;
		}

	}
	
	
	public long getConnectionCreationTime() throws IllegalArgumentException {
		
		return connectionCreationTime;
	}

	public long getNumberOfPackets() throws IllegalArgumentException {
		
		return packets;
	}


	
}
