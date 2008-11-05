/*
 * MsLink.java
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
 * A <code>MsLink</code> represents link between two
 * <code>org.mobicents.media.server.spi.Endpoint</code>
 * 
 * Application creates instance of <code>MsLink</code> by calling
 * <code>MsSession.createLink(MsLinkMode)</code> As soon MsLink is created
 * MsSession call's MsLink.fireMsLinkCreated() to fire the Event
 * <code>MsLinkEventID.LINK_CREATED</code>
 * 
 * <code>MsLink</code> maintains the reference to <code>MsSession</code>
 * object for life of <code>MsLink</code>
 * 
 * @author Oleg Kulikov
 * @author amit.bhayani
 */
public interface MsLink extends Serializable {

	/**
	 * Gets the Link ID
	 * 
	 * @return the unique identifier of this <code>MsLink</code>
	 */
	public String getId();

	/**
	 * Returns the state of MsLink
	 * 
	 * @return Instance of {@link MsLinkState}
	 */
	public MsLinkState getState();

	/**
	 * Gets the session to which this links belongs
	 * 
	 * @return the session object.
	 */
	public MsSession getSession();

	/**
	 * Joins specified endpoints.
	 * 
	 * @param a
	 *            the name of the first endpoint.
	 * @param b
	 *            the name of the second endpoint.
	 */
	public void join(String a, String b);

	/**
	 * Returns back the Endpoints names that this link is trying to join
	 * 
	 * @return MsEndpoint[]. Array has precisely two Endpoints that this Link is
	 *         connecting
	 */
	public MsEndpoint[] getEndpoints();

	/**
	 * Get the <code>MsLinkMode</code> of this MsLink
	 * 
	 * @return MsLinkMode
	 */
	public MsLinkMode getMode();

	/**
	 * While creating the MsLink, application passes the <code>MsLinkMode</code>.
	 * At any time if MsLinkMode needs to be changed this method is called
	 * passing appropriate MsLinkMode. If the link is already in same mode as
	 * that of passed, nothing happens.
	 * 
	 * @param mode
	 *            MsLinkMode
	 */
	public void setMode(MsLinkMode mode);

	/**
	 * Drops this link
	 */
	public void release();

	/**
	 * Adds link listener at MsLink level. The registered listener will be
	 * called for state change of this MsLink only. <br/> The order is such that
	 * <code>MsLinkListener</code> registered at <code>MsProvider</code>
	 * will be called before <code>MsLinkListener</code> at MsLink level
	 * 
	 * @param listener
	 *            the listener object.
	 */
	public void addLinkListener(MsLinkListener listener);

	/**
	 * Removes connection listener.
	 * 
	 * @param listener
	 *            the listener object was added previously.
	 */
	public void removeLinkListener(MsLinkListener listener);

	/**
	 * Add the <code>MsNotificationListener</code> at MsLink level. The
	 * registered listener will be called for state change of this MsLink only.
	 * <br/> The order is such that <code>MsNotificationListener</code>
	 * registered at <code>MsProvider</code> will be called before
	 * <code>MsNotificationListener</code> at MsLink level
	 * 
	 * @param listener
	 *            Instance of MsNotificationListener to be registered
	 */
	public void addNotificationListener(MsNotificationListener listener);

	/**
	 * Remove the instance of MsNotificationListener to no longer receive the
	 * events for state change of MsLink
	 * 
	 * @param listener
	 *            Instance of MsNotificationListener to be de-registered
	 */
	public void removeNotificationListener(MsNotificationListener listener);

}
