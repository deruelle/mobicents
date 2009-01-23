package org.mobicents.mscontrol.impl.events.line;

import org.mobicents.media.server.spi.events.EventFactory;
import org.mobicents.media.server.spi.events.RequestedEvent;
import org.mobicents.media.server.spi.events.line.LineRequestedEvent;
import org.mobicents.mscontrol.events.MsEventAction;
import org.mobicents.mscontrol.events.MsEventIdentifier;
import org.mobicents.mscontrol.events.line.MsLineRequestedEvent;
import org.mobicents.mscontrol.impl.events.BaseRequestedEvent;

/**
 * 
 * @author amit bhayani
 *
 */
public class LineRequestedEventImpl extends BaseRequestedEvent implements MsLineRequestedEvent {

	private MsEventAction action;
	private MsEventIdentifier msEventIdentifier = null;

	@Override
	public RequestedEvent convert() {
		EventFactory factory = new EventFactory();
		try {
			LineRequestedEvent evt = (LineRequestedEvent) factory.createRequestedEvent(msEventIdentifier
					.getPackageName(), msEventIdentifier.getPackageName());
			return evt;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public MsEventAction getAction() {
		return this.action;
	}

	public MsEventIdentifier getID() {
		return msEventIdentifier;
	}

	public void setID(MsEventIdentifier msEventIdentifier) {
		this.msEventIdentifier = msEventIdentifier;
	}

	public void setEventAction(MsEventAction action) {
		this.action = action;

	}

	public String getTone() {		
		return null;
	}

	public String setTone(String tone) {
		return null;
	}

}
