package org.mobicents.javax.media.mscontrol.networkconnection;

import javax.media.mscontrol.networkconnection.NetworkConnection;
import javax.media.mscontrol.resource.Error;
import javax.media.mscontrol.resource.Symbol;

public class NetworkConnectionEventImpl implements javax.media.mscontrol.networkconnection.NetworkConnectionEvent {

	private NetworkConnection source = null;
	private Symbol eventId = null;
	private String errorText = null;
	private Symbol error = Error.e_OK;

	public NetworkConnectionEventImpl(NetworkConnection source, Symbol eventId) {
		this.source = source;
		this.eventId = eventId;
	}

	public NetworkConnectionEventImpl(NetworkConnection source, Symbol eventId, Symbol error, String errorText) {
		this(source, eventId);

		this.error = error;
		this.errorText = errorText;
	}

	public Symbol getQualifier() {
		return null;
	}

	public Symbol getRTCTrigger() {
		// TODO Auto-generated method stub
		return null;
	}

	public Symbol getError() {
		return this.error;
	}

	public String getErrorText() {
		return this.errorText;
	}

	public Symbol getEventID() {
		return this.eventId;
	}

	public NetworkConnection getSource() {
		return this.source;
	}

	@Override
	public String toString() {
		return "EventId = " + this.eventId + " Error = " + this.error + " ErrorText = " + this.errorText + "Source = "
				+ this.source + " ";
	}
}
