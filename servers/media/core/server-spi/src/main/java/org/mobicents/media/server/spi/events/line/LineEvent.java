package org.mobicents.media.server.spi.events.line;

import org.mobicents.media.server.spi.events.EventIdentifier;
import org.mobicents.media.server.spi.events.NotifyEvent;

/**
 * 
 * @author amit bhayani
 * 
 */
public class LineEvent implements NotifyEvent {

	private EventIdentifier eventIdentifier = null;

	public EventIdentifier getEventID() {
		return eventIdentifier;
	}

	public void setEventID(EventIdentifier eventIdentifier) {
		this.eventIdentifier = eventIdentifier;
	}

}
