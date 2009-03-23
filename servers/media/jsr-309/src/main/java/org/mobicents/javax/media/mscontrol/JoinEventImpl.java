package org.mobicents.javax.media.mscontrol;

import java.io.Serializable;

import javax.media.mscontrol.JoinEvent;
import javax.media.mscontrol.Joinable;
import javax.media.mscontrol.MediaObject;
import javax.media.mscontrol.resource.Error;
import javax.media.mscontrol.resource.Symbol;

public class JoinEventImpl implements JoinEvent {

	private Serializable context = null;
	private Joinable other= null;
	private Joinable current = null;
	private Symbol eventID = null;
	private Symbol error = Error.e_OK;
	private String errorText = null;
	private MediaObject source = null;
	
	public JoinEventImpl(MediaObject source, Serializable context, Joinable other, Joinable current, Symbol eventId){
		this.source = source;
		this.context = context;
		this.other = other;
		this.current = current;
		this.eventID = eventId;
	}
	
	public JoinEventImpl(MediaObject source, Serializable context, Joinable other, Joinable current, Symbol eventID, Symbol error,
			String errorText) {
		this( source, context,  other,  current,  eventID);
		this.error = error;
		this.errorText = errorText;
	}

	public Serializable getContext() {
		return this.context;
	}

	public Joinable getOtherJoinable() {
		return this.other;
	}

	public Joinable getThisJoinable() {	
		return this.current;
	}

	public Symbol getError() {
		return this.error;
	}

	public String getErrorText() {		
		return this.errorText;
	}

	public Symbol getEventID() {
		return this.eventID;
	}

	public MediaObject getSource() {
		return this.source;
	}

}
