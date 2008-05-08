package org.mobicents.slee.resource.diameter.base.events;

import net.java.slee.resource.diameter.base.events.DeviceWatchdogRequest;
import net.java.slee.resource.diameter.base.events.DiameterCommand;
import net.java.slee.resource.diameter.base.events.DiameterHeader;
import net.java.slee.resource.diameter.base.events.avp.AvpList;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentityAvp;

public class DeviceWatchdogRequestImpl extends DiameterMessageImpl implements DeviceWatchdogRequest
{

	@Override
	public String getLongName() {

		return "Device-Watchdog-Request";
	}

	@Override
	public String getShortName() {
		
		return "DWR";
	}

	public long getOriginStateId() {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean hasOriginHost() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean hasOriginRealm() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean hasOriginStateId() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setOriginStateId(long originStateId) {
		// TODO Auto-generated method stub
		
	}

  

}
