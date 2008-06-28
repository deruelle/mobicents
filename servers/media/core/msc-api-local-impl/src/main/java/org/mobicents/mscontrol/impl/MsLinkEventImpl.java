/*
 * MsTerminationEventImpl.java
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

package org.mobicents.mscontrol.impl;

import org.mobicents.mscontrol.MsLink;
import org.mobicents.mscontrol.MsLinkEvent;
import org.mobicents.mscontrol.MsLinkListener;
import org.mobicents.media.msc.common.events.*;

/**
 * 
 * @author Oleg Kulikov
 */
public class MsLinkEventImpl implements MsLinkEvent, Runnable {

	private MsLinkImpl source;
	private MsLinkEventID eventID;
	private MsLinkEventCause cause;
	private String msg;

	/** Creates a new instance of MsTerminationEventImpl */
	public MsLinkEventImpl(MsLinkImpl source, MsLinkEventID eventID, MsLinkEventCause cause) {
		this.source = source;
		this.eventID = eventID;
		this.cause = cause;
	}

	public MsLinkEventImpl(MsLinkImpl source, MsLinkEventID eventID, MsLinkEventCause cause, String msg) {
		this.source = source;
		this.eventID = eventID;
		this.cause = cause;
		this.msg = msg;
	}

	public MsLink getSource() {
		return source;
	}

	public MsLinkEventID getEventID() {
		return eventID;
	}

	public MsLinkEventCause getCause() {
		return cause;
	}

	public String getMessage() {
		return msg;
	}

	public void run() {
		for (MsLinkListener listener : source.linkListeners) {
			switch (eventID) {
			case LINK_CREATED:
				listener.linkCreated(this);
				break;
			case LINK_JOINED:
				listener.linkJoined(this);
				break;
			case LINK_DROPPED:
				listener.linkDropped(this);
				break;
			case LINK_FAILED:
				listener.linkFailed(this);
				break;
			}
		}
	}
}
