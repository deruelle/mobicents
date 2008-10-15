/*
 * MsProviderImpl.java
 *
 * The Simple Media Server Control API
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

import EDU.oswego.cs.dl.util.concurrent.QueuedExecutor;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.mobicents.mscontrol.MsConnection;
import org.mobicents.mscontrol.MsConnectionListener;
import org.mobicents.mscontrol.MsLinkListener;
import org.mobicents.mscontrol.MsNotificationListener;
import org.mobicents.mscontrol.MsProvider;
import org.mobicents.mscontrol.MsResourceListener;
import org.mobicents.mscontrol.MsSession;
import org.mobicents.mscontrol.MsSessionListener;
import org.mobicents.mscontrol.MsSignalDetector;
import org.mobicents.mscontrol.MsSignalGenerator;
import org.mobicents.mscontrol.events.MsEventFactory;

/**
 * 
 * @author Oleg Kulikov
 * @author amit.bhayani
 * 
 */
public class MsProviderImpl implements MsProvider, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2166483960453025777L;
	protected ArrayList<MsSessionListener> sessionListeners = new ArrayList<MsSessionListener>();
	protected ArrayList<MsConnectionListener> connectionListeners = new ArrayList<MsConnectionListener>();
	protected ArrayList<MsResourceListener> resourceListeners = new ArrayList<MsResourceListener>();
	protected ArrayList<MsLinkListener> linkListeners = new ArrayList<MsLinkListener>();
	protected ArrayList<MsNotificationListener> eventListeners = new ArrayList();
	protected ArrayList<MsSession> sessions = new ArrayList<MsSession>();
	protected static ExecutorService pool = Executors.newFixedThreadPool(5, new ThreadFactoryImpl());
        private static QueuedExecutor eventQueue = new QueuedExecutor();
	/** Creates a new instance of MsProviderImpl */
	public MsProviderImpl() {
		
	}

	public MsSession createSession() {
		MsSession call = new MsSessionImpl(this);
		sessions.add(call);

		return call;
	}

	public MsEventFactory getEventFactory() {
		return new MsEventFactoryImpl();
	}

	/**
	 * (Non Java-doc).
	 * 
	 * @see org.mobicents.mscontrol.MsProvider#addSessionListener(MsSessionListener).
	 */
	public void addSessionListener(MsSessionListener listener) {
		sessionListeners.add(listener);
	}

	/**
	 * (Non Java-doc).
	 * 
	 * @see org.mobicents.mscontrol.MsProvider#removeSessionListener(MsSessionListener).
	 */
	public void removeSessionListener(MsSessionListener listener) {
		sessionListeners.remove(listener);
	}

	public void addResourceListener(MsResourceListener listener) {
		resourceListeners.add(listener);
	}

	public void removeResourceListener(MsResourceListener listener) {
		resourceListeners.remove(listener);
	}

	public void addConnectionListener(MsConnectionListener listener) {
		connectionListeners.add(listener);
	}

	public void removeConnectionListener(MsConnectionListener listener) {
		connectionListeners.remove(listener);
	}

	/**
	 * Add a termination listener to all terminations.
	 * 
	 * @param MsLinkListener
	 *            object that receives the specified events.
	 */
	public void addLinkListener(MsLinkListener listener) {
		linkListeners.add(listener);
	}

	/**
	 * Removes termination listener
	 * 
	 * @param MsLinkListener
	 *            object that receives the specified events.
	 */
	public void removeLinkListener(MsLinkListener listener) {
		linkListeners.remove(listener);
	}

	public void addNotificationListener(MsNotificationListener listener) {
		eventListeners.add(listener);
	}

	public void removeNotificationListener(MsNotificationListener listener) {
		eventListeners.remove(listener);
	}

	public MsSignalGenerator getSignalGenerator(String endpointName) {
		// MsSignalGenerator msSignalGenerator = new MsSignalGeneratorImpl(this,
		// endpointName);
		return null;// msSignalGenerator;
	}

	public MsSignalDetector getSignalDetector(String endpointName) {
		// MsSignalDetector signalDetectror = new MsSignalDetectorImpl(this,
		// endpointName);
		return null;// signalDetectror;
	}

	public MsConnection getMsConnection(String msConnectionId) {

		for (MsSession e : sessions) {
			for (MsConnection c : e.getConnections()) {
				if (c.getId().equals(msConnectionId)) {
					return c;
				}
			}
		}
		return null;
	}

	public List<MsConnection> getMsConnections(String endpointName) {
		List<MsConnection> msConnectionList = new ArrayList<MsConnection>();
		for (MsSession e : sessions) {
			for (MsConnection c : e.getConnections()) {
				if (c.getEndpoint().getLocalName().equals(endpointName)) {
					msConnectionList.add(c);
				}
			}
		}
		return msConnectionList;
	}

	protected static synchronized void submit(Runnable task) {
		pool.submit(task);
	}

        protected static synchronized void sendEvent(Runnable event) {
            try {
                eventQueue.execute(event);
            } catch (InterruptedException e) {
            }
        }
        
	static class ThreadFactoryImpl implements ThreadFactory {

		final ThreadGroup group;
		static final AtomicInteger msProviderPoolNumber = new AtomicInteger(1);
		final AtomicInteger threadNumber = new AtomicInteger(1);
		final String namePrefix;

		ThreadFactoryImpl() {
			SecurityManager s = System.getSecurityManager();
			group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
			namePrefix = "MsProviderImpl-FixedThreadPool-" + msProviderPoolNumber.getAndIncrement() + "-thread-";
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
}
