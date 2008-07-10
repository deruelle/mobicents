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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.mobicents.mscontrol.MsCallbackHandler;
import org.mobicents.mscontrol.MsConnection;
import org.mobicents.mscontrol.MsConnectionListener;
import org.mobicents.mscontrol.MsLinkListener;
import org.mobicents.mscontrol.MsProvider;
import org.mobicents.mscontrol.MsResourceListener;
import org.mobicents.mscontrol.MsSession;
import org.mobicents.mscontrol.MsSessionListener;
import org.mobicents.mscontrol.MsSignalDetector;
import org.mobicents.mscontrol.MsSignalGenerator;

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

	private ArrayList<MsSession> calls = new ArrayList<MsSession>();

	private MsCallbackHandler callbackHandler = null;

	/** Creates a new instance of MsProviderImpl */
	public MsProviderImpl() {
	}

	public MsSession createSession() {
		MsSession call = new MsSessionImpl(this);
		calls.add(call);

		if (callbackHandler != null) {
			callbackHandler.handle(call);
		}

		call.setSessionStateIdle();
		return call;
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

	public List<MsSessionListener> getSessionListeners() {
		return this.sessionListeners;
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

	public List<MsConnectionListener> getConnectionListeners() {
		return this.connectionListeners;
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

	public MsSignalGenerator getSignalGenerator(String endpointName) {
		MsSignalGenerator msSignalGenerator = new MsSignalGeneratorImpl(this, endpointName);
		if (callbackHandler != null) {
			callbackHandler.handle(msSignalGenerator);
		}
		return msSignalGenerator;
	}

	public MsSignalDetector getSignalDetector(String endpointName) {
		MsSignalDetector signalDetectror = new MsSignalDetectorImpl(this, endpointName);
		if (callbackHandler != null) {
			callbackHandler.handle(signalDetectror);
		}
		return signalDetectror;
	}

	public MsConnection getMsConnection(String msConnectionId) {

		for (MsSession e : calls) {
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
		for (MsSession e : calls) {
			for (MsConnection c : e.getConnections()) {
				if (c.getEndpoint().equals(endpointName)) {
					msConnectionList.add(c);
				}
			}
		}
		return msConnectionList;
	}

	public List<MsResourceListener> getResourceListeners() {
		return resourceListeners;
	}

	public List<MsLinkListener> getLinkListeners() {
		return this.linkListeners;
	}

	public MsCallbackHandler getCallbackHandler() {
		return this.callbackHandler;
	}

	public void setCallbackHandler(MsCallbackHandler callbackHandler) {
		this.callbackHandler = callbackHandler;

	}

}
