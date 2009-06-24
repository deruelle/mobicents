package org.mobicents.javax.media.mscontrol.mediagroup.signals;

import javax.media.mscontrol.EventType;
import javax.media.mscontrol.MediaErr;
import javax.media.mscontrol.Qualifier;
import javax.media.mscontrol.Value;
import javax.media.mscontrol.mediagroup.signals.SignalDetector;
import javax.media.mscontrol.mediagroup.signals.SignalDetectorEvent;
import javax.media.mscontrol.resource.Trigger;

/**
 * 
 * @author amit bhayani
 * 
 */
public class SignalDetectorEventImpl implements SignalDetectorEvent {

	private SignalDetector detector = null;
	private EventType eventType = null;
	private Qualifier qualifier = null;
	private Trigger rtcTrigger = null;

	private String errorText = null;
	private MediaErr error = MediaErr.NO_ERROR;

	private int patterIndex = -1;

	private String signal = null;

	private boolean isSuccessful = false;

	public SignalDetectorEventImpl(SignalDetector detector, EventType eventType, boolean isSuccessful) {
		this.detector = detector;
		this.eventType = eventType;
		this.isSuccessful = isSuccessful;
	}

	public SignalDetectorEventImpl(SignalDetector detector, EventType eventType, boolean isSuccessful, MediaErr error,
			String errorText) {
		this(detector, eventType, isSuccessful);
		this.errorText = errorText;
		this.error = error;
	}

	public SignalDetectorEventImpl(SignalDetector detector, EventType eventType, boolean isSuccessful, String signal) {
		this(detector, eventType, isSuccessful);
		this.isSuccessful = isSuccessful;
	}

	public SignalDetectorEventImpl(SignalDetector detector, EventType eventType, boolean isSuccessful, String signal,
			int patterIndex, Qualifier qualifier, Trigger rtcTrigger) {
		this(detector, eventType, isSuccessful, signal);
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

	public MediaErr getError() {
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

	public boolean isSuccessful() {
		return this.isSuccessful;
	}

}
