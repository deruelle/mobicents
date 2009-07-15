package org.mobicents.javax.media.mscontrol.mediagroup;

import javax.media.mscontrol.EventType;
import javax.media.mscontrol.MediaEvent;

import org.mobicents.javax.media.mscontrol.EventDetectorFactory;

public class RecorderEventDetectorFactory extends EventDetectorFactory {

	EventType mediaEventType = null;

	public RecorderEventDetectorFactory(String pkgName, String eventName, boolean isOnEndpoint, EventType mediaEventType) {
		super(pkgName, eventName, isOnEndpoint);
		this.mediaEventType = mediaEventType;
	}

	@Override
	public MediaEvent generateMediaEvent() {
		return new RecorderEventImpl(this.mediaEventType);
	}

	public EventType getMediaEventType() {
		return mediaEventType;
	}

}
