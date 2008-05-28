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
 * 
 * @author Oleg Kulikov
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

	public List<MsSessionListener> getSessionListeners();

	/**
	 * Add a connection listener to all connections under this MsProvider.
	 * 
	 * @param MsConnectionListener
	 *            object that receives the specified events.
	 */
	public void addConnectionListener(MsConnectionListener connectionListener);
	
	public List<MsConnectionListener> getConnectionListeners();

	/**
	 * Add a resource listener to all resources under this MsProvider.
	 * 
	 * @param MsResourceListener
	 *            object that receives the specified events.
	 */
	public void addResourceListener(MsResourceListener listener);

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
	
	public List<MsLinkListener> getLinkListeners();

	/**
	 * Creates a new instance of the session with no links.
	 * 
	 * @return MsSession object.
	 */
	public MsSession createSession();

	public MsSignalGenerator getSignalGenerator(String endpointName);

	public MsSignalDetector getSignalDetector(String endpointName);

	/**
	 * Get the MsCOnnection Activity for the msConnectionId passed.
	 * 
	 * @param msConnectionId
	 * @return
	 */
	public MsConnection getMsConnection(String msConnectionId);

}
