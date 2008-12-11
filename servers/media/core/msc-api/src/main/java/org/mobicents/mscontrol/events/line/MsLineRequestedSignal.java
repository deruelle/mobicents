package org.mobicents.mscontrol.events.line;

import org.mobicents.mscontrol.events.MsRequestedSignal;

/**
 * 
 * @author amit bhayani
 *
 */
public interface MsLineRequestedSignal extends MsRequestedSignal {
	public String getTone();

	public String setTone();
}
