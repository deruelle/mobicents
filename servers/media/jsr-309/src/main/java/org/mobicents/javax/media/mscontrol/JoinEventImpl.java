package org.mobicents.javax.media.mscontrol;

import java.io.Serializable;

import javax.media.mscontrol.JoinEvent;
import javax.media.mscontrol.Joinable;
import javax.media.mscontrol.MediaObject;
import javax.media.mscontrol.resource.Error;
import javax.media.mscontrol.resource.EventType;

public class JoinEventImpl implements JoinEvent {

	private Serializable context = null;
	private Joinable other = null;
	private Joinable current = null;
	private EventType eventType = null;
	private Error error = Error.e_OK;
	private String errorText = null;
	private MediaObject source = null;

	public JoinEventImpl(MediaObject source, Serializable context, Joinable other, Joinable current, EventType eventId) {
		this.source = source;
		this.context = context;
		this.other = other;
		this.current = current;
		this.eventType = eventId;
	}

	public JoinEventImpl(MediaObject source, Serializable context, Joinable other, Joinable current, EventType eventID,
			Error error, String errorText) {
		this(source, context, other, current, eventID);
		this.error = error;
		this.errorText = errorText;
	}

	public Serializable getContext() {
		return this.context;
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

	public Joinable getOtherJoinable() {
		return this.other;
	}

	public MediaObject getSource() {
		return this.source;
	}

	public Joinable getThisJoinable() {
		return this.current;
	}

	@Override
	public String toString() {
		return "Source = " + this.source + " Other = " + this.other + " Current = " + this.current + " EventId = "
				+ this.eventType + " Error = " + this.error + " ErrorText = " + this.errorText;
	}

}
