package org.mobicents.slee.resource.diameter.base.events;

import net.java.slee.resource.diameter.base.events.DeviceWatchdogAnswer;

import org.jdiameter.api.Message;

/**
 * 
 * Implementation of Device-Watchdog-Answer Diameter Message.
 *
 * <br>Super project:  mobicents
 * <br>3:24:36 PM May 21, 2008 
 * <br>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a> 
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a> 
 * @author Erick Svenson
 */
public class DeviceWatchdogAnswerImpl extends DiameterMessageImpl implements DeviceWatchdogAnswer
{

	public DeviceWatchdogAnswerImpl(Message message) {
		super(message);
	}

	@Override
	public String getLongName() {
		
		return "Device-Watchdog-Answer";
	}

	@Override
	public String getShortName() {

		return "DWA";
	}

}
