package org.mobicents.media.server.spi.events.line;

import org.mobicents.media.server.spi.events.PkgFactory;
import org.mobicents.media.server.spi.events.RequestedEvent;
import org.mobicents.media.server.spi.events.RequestedSignal;
import org.mobicents.media.server.spi.events.pkg.Line;

public class LinePkgFactory implements PkgFactory {

	public RequestedEvent createRequestedEvent(String eventID) {
		if (Line.CONGESTION_TONE.getEventName().equals(eventID)) {
			LineRequestedEvent lineRequestedEvent = new LineRequestedEvent();
			lineRequestedEvent.setID(Line.CONGESTION_TONE);
			return lineRequestedEvent;
		}
		return null;
	}

	public RequestedSignal createRequestedSignal(String signalID) {
		if (Line.CONGESTION_TONE.getEventName().equals(signalID)) {
			LineRequestedSignal lineRequestedSignal = new LineRequestedSignal();
			lineRequestedSignal.setID(Line.CONGESTION_TONE);
			return lineRequestedSignal;
		}
		return null;
	}

}
