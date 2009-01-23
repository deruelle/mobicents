package org.mobicents.mscontrol.events.connection.parameters;


import org.mobicents.mscontrol.MsNotifyEvent;



/**
 *
 */
public interface MsConnectionParametersNotifyEvent extends MsNotifyEvent {
	public int getJitter();

	public int getOctetsReceived();

	public int getOctetsSent();

	public int getPacketsLost();

	public int getPacketsReceived();

	public int getPacketsSent();
}
