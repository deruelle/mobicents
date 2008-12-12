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

import java.io.Serializable;
import java.util.List;

import org.mobicents.mscontrol.events.MsEventFactory;
import org.mobicents.mscontrol.events.MsRequestedEvent;

/**
 * This is the provider class which is used to create the {@link MsSession}.
 * Through out the Application life cycle <code>MsProvider</code> remains the
 * same. Application that are interested in listening {@link MsSessionEvent},
 * {@link MsConnectionEvent}, {@link MsLinkEvent} and {@link MsNotifyEvent}
 * calls the respective addlistener methods passing instance of class that
 * implements respective listeners.
 * 
 * Use <code>MsPeer.getProvider()</code> to get the instance of MsProvider
 * 
 * @author Oleg Kulikov
 * @author amit bhayani
 */
public interface MsProvider extends Serializable {

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
	 * Add a notification listener to all (feature and current) within the
	 * domain of this provider.
	 * 
	 * @param listener
	 */
	public void addNotificationListener(MsNotificationListener listener);

	/**
	 * Remove MsNotificationListener
	 * 
	 * @param listener
	 */
	public void removeNotificationListener(MsNotificationListener listener);

	/**
	 * Add a connection listener to all connections under this MsProvider.
	 * 
	 * @param listener
	 *            object that receives the specified events.
	 */
	public void addConnectionListener(MsConnectionListener listener);

	/**
	 * Removes connection listener
	 * 
	 * @param listener
	 *            this listener is removed
	 */
	public void removeConnectionListener(MsConnectionListener listener);

	/**
	 * Add a resource listener to all resources under this MsProvider.
	 * 
	 * @deprecated
	 * @param listener
	 *            object that receives the specified events.
	 */
	public void addResourceListener(MsResourceListener listener);

	/**
	 * Add a link listener to all terminations.
	 * 
	 * @param listener
	 *            object that receives the specified events.
	 */
	public void addLinkListener(MsLinkListener listener);

	/**
	 * Removes link listener
	 * 
	 * @param listener
	 *            object that receives the specified events.
	 */
	public void removeLinkListener(MsLinkListener listener);

	/**
	 * Creates a new instance of the session with no links.
	 * 
	 * @return MsSession object.
	 */
	public MsSession createSession();

	/**
	 * Get instance of {@link MsEventFactory}
	 * 
	 * @return instance of <code>MsEventFactory</code>
	 */
	public MsEventFactory getEventFactory();


	/**
	 * Get the MsConnection for the msConnectionId passed.
	 * 
	 * @param msConnectionId
	 * @return instance of <code>MsConnection</code>
	 */
	public MsConnection getMsConnection(String msConnectionId);

	/**
	 * Gets List of MsConnection object for given endpointName
	 * 
	 * @param endpointName
	 * @return List of <code>MsConnection</code>
	 */
	public List<MsConnection> getMsConnections(String endpointName);
	
	/**
	 * Gets List of MsLink object for given endpointName
	 * @param endpointName Endpoint name
	 * @return List of <code>MsLink</code>
	 */
	public List<MsLink> getMsLinks(String endpointName);

}
