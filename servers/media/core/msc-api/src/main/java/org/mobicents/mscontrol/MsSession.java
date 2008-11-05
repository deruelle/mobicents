/*
 * MsSession.java
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

/**
 * <p>
 * A <code>MsSession</code> is a transient association of (zero or more)
 * connection for the purposes of engaging in a real-time communications
 * interchange.
 * </p>
 * <p>
 * The session and its associated connection objects describe the control and
 * media flows taking place in a communication network. The
 * <code>MsProvider</code> adjusts the session, connection and link objects to
 * reflect the results of the combined command actions.
 * </p>
 * <p>
 * Applications create instances of a MsSession object with the
 * <code>MsProvider.createSession()</code> method, which returns a MsSession
 * object that has zero connections and is in the IDLE state.
 * </p>
 * The <code>MsSession</code> maintains a reference to its
 * <code>MsProvider</code> for the life of that <code>MsSession</code>
 * object. The <code>MsProvider</code> object instance does not change
 * throughout the lifetime of the <code>MsSession</code> object. The
 * <code>MsProvider</code> associated with a <code>MsSession</code> is
 * obtained via the <code>getProvider()</code> method.
 * 
 * @author Oleg Kulikov
 * @author amit.bhayani
 */
public interface MsSession extends Serializable {

	/**
	 * Get the unique id of this session
	 * 
	 * @return the unique identifier of this session
	 */
	public String getId();

	/**
	 * Retrieves the provider handling this session object. The Provider
	 * reference does not change once the <code>MsSession</code> object has
	 * been created, despite the state of the MsSession object.
	 * 
	 * @return MsProvider object managing this call.
	 */
	public MsProvider getProvider();

	/**
	 * Retrieves the state of the session. The state will be either IDLE, ACTIVE
	 * or INVALID. Look at {@link MsSessionState}
	 * 
	 * @return enum representing the state of the session.
	 */
	public MsSessionState getState();

	/**
	 * Creates a new network connection and attaches it to this session. The
	 * {@link MsConnection} object is associated with an endpoint name
	 * corresponding to the string given as an input parameter.
	 * 
	 * Note that following this operation the returned MsConnection object must
	 * still be "modified" which can be accomplished using the
	 * <code>MsConnection.modify(...)</code>
	 * 
	 * @param endpointName
	 *            <p>
	 *            specifies the identifier of the media server endpoint. If you
	 *            want connection to be created on new Endpoint then pass
	 *            wildcard '$' with endpointName. For example
	 *            <code>'media/trunk/Announcement/$'</code>. Once connection
	 *            is created, calling <code>connection.getEndpoint()</code>
	 *            will return instance of {@link MsEndpoint} and calling
	 *            <code>getLocalName()</code> on this returned
	 *            <code>MsEndpoint</code> will give the actual endpoint name,
	 *            for example '<code>media/trunk/Announcement/enp-1</code>'
	 *            </p>
	 *            <p>
	 *            It's also possible to create connection on already known
	 *            endpoint for example
	 *            <p>
	 *            <blockquote>
	 * 
	 * <pre>
	 * MsSession session;
	 * ...
	 * MsConnection msConnection = session.createNetworkConnection(&quot;media/trunk/Conference/enp-3&quot;);
	 * </pre>
	 * 
	 * </blockquote>
	 * </p>
	 * </p>
	 */
	public MsConnection createNetworkConnection(String endpointName);

	/**
	 * Creates local link that joines two endpoints and attach it to this
	 * session.
	 * 
	 * In some networks, we may often have to set-up connections between
	 * endpoints that are located within the same media server. Examples of such
	 * connections may be:
	 * 
	 * <ul>
	 * <li>Connecting a call to an Interactive Voice-Response unit,</li>
	 * <li>Connecting a call to a Conferencing unit,</li>
	 * <li>Routing a call from one endpoint to another,something often
	 * described as a "hairpin" connection.</li>
	 * </ul>
	 * 
	 * Local connections are much simpler to establish than network connections.
	 * In most cases, the connection will be established through some local
	 * interconnecting device, such as for example a TDM bus.
	 * 
	 * When two endpoints are managed by the same media server, it is possible
	 * to specify the link in a single command that conveys the names of the two
	 * endpoints that will be connected.
	 * 
	 * @param mode
	 *            specifies the mode of the link. Valid modes are
	 *            <ul>
	 *            <li>MsLinkMode.HALF_DUPLEX</li>
	 *            <li>MsLinkMode.DUPLEX</li>
	 *            </ul>
	 *            Look at {@link MsLinkMode}
	 * @return MsLink object managing this link.
	 */
	public MsLink createLink(MsLinkMode mode);

	/**
	 * Add a listener to this session. This also reports all state changes in
	 * the state of the session. The listener added with this method will report
	 * events on the session for as long as the listener receive INVALID event.
	 * 
	 * @param listener
	 *            object that receives the specified events
	 */
	public void addSessionListener(MsSessionListener listener);

	/**
	 * Removes a listener from this session.
	 * 
	 * @param listener
	 *            Listener object.
	 */
	public void removeSessionListener(MsSessionListener listener);

	/**
	 * Returns the list of {@link MsConnection} associated with this MsSession
	 * 
	 * @return List of <code>MsConnection</code> contained in this session
	 */
	public List<MsConnection> getConnections();

	/**
	 * Returns the list of {@link MsLink} associated with this MsSession
	 * 
	 * @return List of <code>MsLink</code> contained in this session
	 */
	public List<MsLink> getLinks();

}
