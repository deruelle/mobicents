/*
 * MsSessionEvent.java
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
 * This is the interface for all MsSession-related events.
 * 
 * @author Oleg Kulikov
 * @author amit.bhayani
 */
public interface MsSessionEvent extends Serializable {

	/**
	 * Returns the {@link MsSession} object associated with this event.
	 * 
	 * @return the MsSession object associated with this event.
	 */
	public MsSession getSource();

	/**
	 * Returns the {@link MsSessionEventID} of event.
	 * 
	 * @return the id of event
	 */
	public MsSessionEventID getEventID();

	/**
	 * Returns the cause of event. Cause is Connection created or dropped or
	 * Link created or dropped
	 * 
	 * @return instance of MsSessionEventCause
	 */
	public MsSessionEventCause getEventCause();

	/**
	 * When the session fires Event for STATE, it passes the Connection or Link
	 * Object which caused this state change
	 * 
	 * @return The {@link MsConnection} or {@link MsLink} object
	 */
	public Object getCauseObject();
}
