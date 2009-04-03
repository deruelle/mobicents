package org.mobicents.javax.media.mscontrol.networkconnection;

import javax.media.mscontrol.networkconnection.NetworkConnection;
import javax.media.mscontrol.resource.Error;
import javax.media.mscontrol.resource.EventType;
import javax.media.mscontrol.resource.Qualifier;
import javax.media.mscontrol.resource.Trigger;

/**
 * 
 * @author amit bhayani
 * 
 */
public class NetworkConnectionEventImpl implements javax.media.mscontrol.networkconnection.NetworkConnectionEvent {

	private NetworkConnection source = null;
	private EventType eventType = null;
	private String errorText = null;
	private Error error = Error.e_OK;

	public NetworkConnectionEventImpl(NetworkConnection source, EventType eventType) {
		this.source = source;
		this.eventType = eventType;
	}

	public NetworkConnectionEventImpl(NetworkConnection source, EventType eventType, Error error, String errorText) {
		this(source, eventType);

		this.error = error;
		this.errorText = errorText;
	}

	public Qualifier getQualifier() {
		return null;
	}

	public Trigger getRTCTrigger() {
		// TODO Auto-generated method stub
		return null;
	}

	public Error getError() {
		return this.error;
	}

	public String getErrorText() {
		return this.errorText;
	}

	public EventType getEventType() {
		return this.eventType;
	}

	public NetworkConnection getSource() {
		return this.source;
	}

	@Override
	public String toString() {
		return "EventType = " + this.eventType + " Error = " + this.error + " ErrorText = " + this.errorText
				+ "Source = " + this.source + " ";
	}
}
