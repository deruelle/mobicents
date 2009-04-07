package org.mobicents.javax.media.mscontrol.mediagroup.signals;

import javax.media.mscontrol.mediagroup.signals.SignalDetector;
import javax.media.mscontrol.mediagroup.signals.SignalDetectorEvent;
import javax.media.mscontrol.resource.Error;
import javax.media.mscontrol.resource.EventType;
import javax.media.mscontrol.resource.Qualifier;
import javax.media.mscontrol.resource.Trigger;
import javax.media.mscontrol.resource.Value;

public class SignalDetectorEventImpl implements SignalDetectorEvent {

	private SignalDetector detector = null;
	private EventType eventType = null;
	private Qualifier qualifier = null;
	private Trigger rtcTrigger = null;

	private String errorText = null;
	private Error error = Error.e_OK;

	private int patterIndex = -1;

	private String signal = null;

	public SignalDetectorEventImpl(SignalDetector detector, EventType eventType, Error error, String errorText) {
		this.detector = detector;
		this.eventType = eventType;
		this.errorText = errorText;
		this.error = error;
	}

	public SignalDetectorEventImpl(SignalDetector detector, EventType eventType, String signal) {
		this.detector = detector;
		this.eventType = eventType;
		this.signal = signal;
	}

	public SignalDetectorEventImpl(SignalDetector detector, EventType eventType, String signal, int patterIndex,
			Qualifier qualifier, Trigger rtcTrigger) {
		this(detector, eventType, signal);
		this.patterIndex = patterIndex;
		this.qualifier = qualifier;
		this.rtcTrigger = rtcTrigger;
	}

	public int getPatternIndex() {
		return this.patterIndex;
	}

	public Value[] getSignalBuffer() {
		return null;
	}

	public String getSignalString() {
		return this.signal;
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

	public SignalDetector getSource() {
		return this.detector;
	}

}
