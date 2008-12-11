package org.mobicents.mscontrol.impl.events.line;

import org.mobicents.media.server.spi.events.EventFactory;
import org.mobicents.media.server.spi.events.RequestedSignal;
import org.mobicents.media.server.spi.events.line.LineRequestedSignal;
import org.mobicents.mscontrol.events.MsEventIdentifier;
import org.mobicents.mscontrol.events.line.MsLineRequestedSignal;
import org.mobicents.mscontrol.impl.events.BaseRequestedSignal;

/**
 * 
 * @author amit bhayani
 *
 */
public class LineRequestedSignalImpl extends BaseRequestedSignal implements MsLineRequestedSignal {

	private MsEventIdentifier msEventIdentifier = null;

	@Override
	public RequestedSignal convert() {
		EventFactory factory = new EventFactory();
		LineRequestedSignal signal = (LineRequestedSignal) factory.createRequestedSignal(msEventIdentifier
				.getPackageName(), msEventIdentifier.getEventName());

		return signal;
	}

	public String getTone() {		
		return null;
	}

	public String setTone() {
		return null;
	}

	public MsEventIdentifier getID() {
		return this.msEventIdentifier;
	}

	public void setID(MsEventIdentifier msEventIdentifier) {
		this.msEventIdentifier = msEventIdentifier;
	}

}
