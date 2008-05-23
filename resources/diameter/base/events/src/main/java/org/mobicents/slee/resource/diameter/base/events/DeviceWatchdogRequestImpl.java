package org.mobicents.slee.resource.diameter.base.events;

import net.java.slee.resource.diameter.base.events.DeviceWatchdogRequest;

import org.jdiameter.api.Message;

/**
 * 
 * Implementation of Device-Watchdog-Request Diameter Message.
 *
 * <br>Super project:  mobicents
 * <br>3:26:19 PM May 21, 2008 
 * <br>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a> 
 * @author <a href="mailto:braislog@gmail.com"> Alexandre Mendonca </a> 
 * @author Erick Svenson
 */
public class DeviceWatchdogRequestImpl extends DiameterMessageImpl implements DeviceWatchdogRequest
{

	public DeviceWatchdogRequestImpl(Message message) {
		super(message);
	}

	@Override
	public String getLongName() {

		return "Device-Watchdog-Request";
	}

	@Override
	public String getShortName() {
		
		return "DWR";
	}

}
