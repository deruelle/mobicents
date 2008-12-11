package org.mobicents.media.server.spi.events.line;

import org.mobicents.media.server.spi.events.AbstractRequestedEvent;
import org.mobicents.media.server.spi.events.EventIdentifier;
import org.mobicents.media.server.spi.events.NotifyEvent;

/**
 * 
 * @author amit bhayani
 *
 */
public class LineRequestedEvent extends AbstractRequestedEvent {

	private EventIdentifier eventIdentifier = null;

	public EventIdentifier getID() {
		return eventIdentifier;
	}

	public void setID(EventIdentifier eventIdentifier) {
		this.eventIdentifier = eventIdentifier;
	}

	public boolean matches(NotifyEvent event) {
		return event.getEventID().equals(eventIdentifier);

	}
}
