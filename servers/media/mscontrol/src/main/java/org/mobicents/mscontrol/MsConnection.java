/*
 * MsConnection.java
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

/**
 * Represents the actual RTP connection. MsConnection is created by calling
 * <code>MsSession.createNetworkConnection</code>. As soon as MsConnection is
 * created call <code>MsConnection.fireConnectionInitialized()</code> to set
 * the state of MsConection to
 * <code>MsConnectionEventID.CONNECTION_INITIALIZED</code>
 * 
 * @author Oleg Kulikov
 * @author amit.bhayani
 */
public interface MsConnection extends Serializable {

	/**
	 * Retrieves the MsConnection ID, basically a UUID
	 * 
	 * @return
	 */
	public String getId();

	/**
	 * Retrieves the MsSession that is associated with this MsConnection. This
	 * MsSession reference remains valid throughout the lifetime of the
	 * MsConnection object despite the state of the MsConnection object. This
	 * MsSession reference does not change once the MsConnection object has been
	 * created.
	 * 
	 * @return MsSession object holding this connection.
	 */
	public MsSession getSession();

	/**
	 * Gets the session descriptor of the local end.
	 * 
	 * @return session descriptor as specified by SDP.
	 */
	public String getLocalDescriptor();

	/**
	 * Gets the session descriptor of the remote end.
	 * 
	 * @return session descriptor as specified by SDP.
	 */
	public String getRemoteDescriptor();

	/**
	 * Returns the concrete endpoint which executes this endpoint.
	 * 
	 * @return the name of the endpoint on the media server or null if
	 *         connection is not created on media server yet.
	 */
	public String getEndpoint();

	/**
	 * Adds connection listener.
	 * 
	 * @param listener
	 *            the listener object.
	 */
	public void addConnectionListener(MsConnectionListener listener);

	/**
	 * Removes connection listener.
	 * 
	 * @param listener
	 *            the listener object was added previously.
	 */
	public void removeConnectionListener(MsConnectionListener listener);

	/**
	 * Creates or modify network connection on the media server side.
	 * 
	 * @param remoteDesc
	 *            the session desriptor of the remote party.
	 */
	public void modify(String localDesc, String remoteDesc);

	/**
	 * Deletes related connection from media server.
	 */
	public void release();

	/**
	 * As soon as Connection is created, MsSession calls this method to set the
	 * state of MsConnection to
	 * <code>MsConnectionEventID.CONNECTION_INITIALIZED</code>
	 */
	public void fireConnectionInitialized();
}
