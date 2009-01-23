/*
 * MsSessionListener.java
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
 * The class interested in receiving the {@link MsSessionEvent} should implement
 * this interface. When ever there is change in state of {@link MsLink},
 * instance of {@link MsLinkEvent} is fired.
 * 
 * @author Oleg Kulikov
 */
public interface MsSessionListener extends Serializable {
	/**
	 * Indicates that the state of the <code>MsSession</code> object has
	 * changed to <code>MsSessionState.IDLE</code>. This is when the
	 * MsSession is created
	 * 
	 * param evt the <code>MsSessionEvent</code> with eventID
	 * <code>MsSessionEventID.SESSION_IDLE</code>.
	 */
	public void sessionCreated(MsSessionEvent evt);

	/**
	 * Indicates that the state of the <code>MsSession</code> object has
	 * changed to <code>MsSession.ACTIVE</code>.
	 * 
	 * param evt the <code>MsSessionEvent</code> with eventID
	 * <code>MsSessionEventID.SESSION_ACTIVE</code>.
	 */
	public void sessionActive(MsSessionEvent evt);

	/**
	 * Indicates that the state of the <code>MsSession</code> object has
	 * changed to <code>MsSession.INVALID</code>.
	 * 
	 * param evt the <code>MsSessionEvent</code> with eventID
	 * <code>MsSessionEventID.SESSION_INVALID</code>.
	 */
	public void sessionInvalid(MsSessionEvent evt);
}
