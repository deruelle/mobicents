/*
 * MsConnectionEvent.java
 *
 * The Simple Media API RA
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
 * The instance of <code>MsConnectionEvent</code> is fired when ever the state
 * of {@link MsConnection} changes
 * 
 * @author Oleg Kulikov
 */
public interface MsConnectionEvent extends Serializable {

	/**
	 * Returns the underlying {@link MsConnection} object which has changed its
	 * state
	 * 
	 * @return MsConnection
	 */
	public MsConnection getConnection();

	/**
	 * Returns the {@link MsConnectionEventID} that represents the state change
	 * of <code>MsConnection</code>
	 * 
	 * @return MsConnectionEventID
	 */
	public MsConnectionEventID getEventID();

	/**
	 * Returns the cause for <code>MsConnectionEventID</code> to be fired
	 * 
	 * @return MsConnectionEventCause
	 */
	public MsConnectionEventCause getCause();

	/**
	 * Returns the message associated with event
	 * 
	 * @return String
	 */
	public String getMessage();
}
