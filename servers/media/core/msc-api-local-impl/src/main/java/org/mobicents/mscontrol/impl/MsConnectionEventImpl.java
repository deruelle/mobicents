/*
 * MsConnectionEventImpl.java
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

import org.apache.log4j.Logger;
import org.mobicents.mscontrol.MsConnection;
import org.mobicents.mscontrol.MsConnectionEvent;
import org.mobicents.mscontrol.MsConnectionEventCause;
import org.mobicents.mscontrol.MsConnectionEventID;
import org.mobicents.mscontrol.MsConnectionListener;

/**
 * 
 * @author Oleg Kulikov
 */
public class MsConnectionEventImpl implements MsConnectionEvent, Runnable {

	private static Logger logger = Logger.getLogger(MsConnectionEventImpl.class);

	private MsConnectionImpl connection;
	private MsConnectionEventID eventID;
	private MsConnectionEventCause cause;
	private String msg;

	/** Creates a new instance of MsConnectionEventImpl */
	public MsConnectionEventImpl(MsConnectionImpl connection, MsConnectionEventID eventID,
			MsConnectionEventCause cause, String msg) {
		this.connection = connection;
		this.eventID = eventID;
		this.cause = cause;
		this.msg = msg;
	}

	public MsConnection getConnection() {
		return connection;
	}

	public MsConnectionEventID getEventID() {
		return eventID;
	}

	public MsConnectionEventCause getCause() {
		return cause;
	}

	public String getMessage() {
		return msg;
	}

	public void run() {
		for (MsConnectionListener listener : connection.session.provider.connectionListeners) {
			if (logger.isDebugEnabled()) {
				logger.debug("**** SENDING CONNECTION EVENT " + eventID + " TO " + listener);
			}
			switch (eventID) {
			case CONNECTION_CREATED:
				listener.connectionCreated(this);
				break;
			case CONNECTION_HALF_OPEN:
				listener.connectionHalfOpen(this);
				break;
			case CONNECTION_OPEN:
				listener.connectionOpen(this);
				break;
			case CONNECTION_FAILED:
				if (logger.isDebugEnabled()) {
					logger.debug("CONNECTION FAILED");
				}
				listener.connectionFailed(this);
				break;
			case CONNECTION_DISCONNECTED:
				listener.connectionDisconnected(this);
				break;
			}
		}
	}
}
