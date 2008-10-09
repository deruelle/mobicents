/*
 * MsNotifyEvent.java
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
import org.mobicents.mscontrol.events.MsEventIdentifier;


/**
 * MsNotify uses system wide defined constants for eventID and eventCause -
 * reason for that to happen is that MsNotifyEvent conveys information passed
 * throw signaling from and to Media Server
 * 
 * @author Oleg Kulikov
 */
public interface MsNotifyEvent extends Serializable {

	public Object getSource();
	public MsEventIdentifier getEventID();

}
