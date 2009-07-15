package org.mobicents.javax.media.mscontrol.mediagroup.signals;

import javax.media.mscontrol.EventType;
import javax.media.mscontrol.MediaEvent;

import org.mobicents.javax.media.mscontrol.EventDetectorFactory;

public class SignalDetectorEventDetectorFactory extends EventDetectorFactory {

	EventType mediaEventType = null;

	public SignalDetectorEventDetectorFactory(String pkgName, String eventName, boolean isOnEndpoint,
			EventType mediaEventType) {
		super(pkgName, eventName, isOnEndpoint);
		this.mediaEventType = mediaEventType;
	}

	@Override
	public MediaEvent generateMediaEvent() {
		return new SignalDetectorEventImpl(this.mediaEventType);
	}

	public EventType getMediaEventType() {
		return mediaEventType;
	}

}
