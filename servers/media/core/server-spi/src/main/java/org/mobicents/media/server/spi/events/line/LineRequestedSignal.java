package org.mobicents.media.server.spi.events.line;

import org.mobicents.media.server.spi.events.AbstractRequestedSignal;
import org.mobicents.media.server.spi.events.EventIdentifier;

/**
 * 
 * @author amit bhayani
 *
 */
public class LineRequestedSignal extends AbstractRequestedSignal {

	private EventIdentifier eventIdentifier = null;

	public EventIdentifier getID() {
		return this.eventIdentifier;
	}

	public void setID(EventIdentifier eventIdentifier) {
		this.eventIdentifier = eventIdentifier;
	}
}
