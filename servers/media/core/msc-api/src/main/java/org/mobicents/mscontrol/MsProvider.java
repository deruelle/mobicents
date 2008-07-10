/*
 * MsProvider.java
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

package org.mobicents.mscontrol;

import java.util.List;

/**
 * This is the provider class which is used to create the MsSession,
 * MsSignalGenerator and MsSignalDetector. Through out the Application life
 * cycle MsProvider remains the same. Application that are interested in
 * listening MsSessionEvent, MsConnectionEvent, MsLinkEvent and MsNotifyEvent
 * calls the respective addlistener methods passing instance of class that
 * implements respective listeners.
 * 
 * Use MsPeer.getProvider() to get the instance of MsProvider
 * 
 * @author Oleg Kulikov
 * @author amit bhayani
 */
public interface MsProvider {
	/**
	 * Add a session listener to all (feature and current) within the domain of
	 * this provider.
	 * 
	 * @param listener
	 *            object that receives the specified events
	 */
	public void addSessionListener(MsSessionListener listener);

	/**
	 * Removes a listener that was previously registered.
	 * 
	 * @param listener
	 *            Listener object.
	 */
	public void removeSessionListener(MsSessionListener listener);

	/**
	 * Returns the list of MsSessionListners registered with MsProvider
	 * 
	 * @return
	 */
	public List<MsSessionListener> getSessionListeners();

	/**
	 * Add a connection listener to all connections under this MsProvider.
	 * 
	 * @param MsConnectionListener
	 *            object that receives the specified events.
	 */
	public void addConnectionListener(MsConnectionListener connectionListener);

	/**
	 * Returns the List of MsConnectionListener registered with MsProvider
	 * 
	 * @return
	 */
	public List<MsConnectionListener> getConnectionListeners();

	/**
	 * Add a resource listener to all resources under this MsProvider.
	 * 
	 * @param MsResourceListener
	 *            object that receives the specified events.
	 */
	public void addResourceListener(MsResourceListener listener);

	/**
	 * Returns List of MsResourceListener registered with MsProvider
	 * 
	 * @return
	 */
	public List<MsResourceListener> getResourceListeners();

	/**
	 * Add a link listener to all terminations.
	 * 
	 * @param MsLinkListener
	 *            object that receives the specified events.
	 */
	public void addLinkListener(MsLinkListener listener);

	/**
	 * Removes link listener
	 * 
	 * @param MsLinkListener
	 *            object that receives the specified events.
	 */
	public void removeLinkListener(MsLinkListener listener);

	/**
	 * Returns the List of MsLinkListener registered with MsProvider
	 * 
	 * @return
	 */
	public List<MsLinkListener> getLinkListeners();

	/**
	 * Creates a new instance of the session with no links.
	 * 
	 * @return MsSession object.
	 */
	public MsSession createSession();

	/**
	 * Creates a new instance of MsSignalGenerator for given EndpointName
	 * 
	 * @param endpointName
	 * @return
	 */
	public MsSignalGenerator getSignalGenerator(String endpointName);

	/**
	 * Creates a new instance of SignalDetector for given EndpointName
	 * 
	 * @param endpointName
	 * @return
	 */
	public MsSignalDetector getSignalDetector(String endpointName);

	/**
	 * Get the MsConnection for the msConnectionId passed.
	 * 
	 * @param msConnectionId
	 * @return
	 */
	public MsConnection getMsConnection(String msConnectionId);

	/**
	 * Gets List of MsConnection object for given endpointName
	 * 
	 * @param endpointName
	 * @return
	 */
	public List<MsConnection> getMsConnections(String endpointName);

	/**
	 * An application ( that implements MsCallbackHandler ) using MsProvider can
	 * be called synchronously by setting CallbackHandler here. If the
	 * application is not interested in callback than it may not set it and
	 * CallbackHandler will be null;
	 * 
	 * @param callbackHandler
	 */
	public void setCallbackHandler(MsCallbackHandler callbackHandler);

	/**
	 * Get the CallbackHandler implementation
	 * @return
	 */
	public MsCallbackHandler getCallbackHandler();

}
