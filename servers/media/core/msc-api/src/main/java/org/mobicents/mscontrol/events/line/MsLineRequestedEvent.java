package org.mobicents.mscontrol.events.line;

import org.mobicents.mscontrol.events.MsRequestedEvent;

/**
 * 
 * @author amit bhayani
 *
 */
public interface MsLineRequestedEvent extends MsRequestedEvent {

	public String getTone();

	public String setTone(String tone);
}
