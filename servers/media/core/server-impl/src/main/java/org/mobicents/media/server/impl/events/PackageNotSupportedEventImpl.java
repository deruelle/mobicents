package org.mobicents.media.server.impl.events;

import org.mobicents.media.server.spi.events.EventIdentifier;
import org.mobicents.media.server.spi.events.NotifyEvent;

public class PackageNotSupportedEventImpl implements NotifyEvent {

	private EventIdentifier eventID;

	public PackageNotSupportedEventImpl(EventIdentifier eventID) {
		this.eventID = eventID;
	}

	public EventIdentifier getEventID() {
		return eventID;
	}

}
