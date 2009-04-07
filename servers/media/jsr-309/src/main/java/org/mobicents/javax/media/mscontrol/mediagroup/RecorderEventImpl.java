package org.mobicents.javax.media.mscontrol.mediagroup;

import javax.media.mscontrol.mediagroup.Recorder;
import javax.media.mscontrol.mediagroup.RecorderEvent;
import javax.media.mscontrol.resource.Error;
import javax.media.mscontrol.resource.EventType;
import javax.media.mscontrol.resource.Qualifier;
import javax.media.mscontrol.resource.Trigger;

public class RecorderEventImpl implements RecorderEvent {
	private Recorder recorder = null;
	private EventType eventType = null;
	private Qualifier qualifier = null;
	private Trigger rtcTrigger = null;
	private int duration = -1;

	private String errorText = null;
	private Error error = Error.e_OK;

	public RecorderEventImpl(Recorder recorder, EventType eventType, Error error, String errorText) {
		super();
		this.recorder = recorder;
		this.eventType = eventType;
		this.errorText = errorText;
		this.error = error;
	}

	public RecorderEventImpl(Recorder recorder, EventType eventType, Qualifier qualifier, Trigger rtcTrigger,
			int duration) {
		super();
		this.recorder = recorder;
		this.eventType = eventType;
		this.qualifier = qualifier;
		this.rtcTrigger = rtcTrigger;
		this.duration = duration;
	}

	public int getDuration() {
		return this.duration;
	}

	public Qualifier getQualifier() {
		return this.qualifier;
	}

	public Trigger getRTCTrigger() {
		return this.rtcTrigger;
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

	public Recorder getSource() {
		return this.recorder;
	}

}
