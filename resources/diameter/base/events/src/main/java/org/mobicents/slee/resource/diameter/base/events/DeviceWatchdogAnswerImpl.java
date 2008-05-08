package org.mobicents.slee.resource.diameter.base.events;

import net.java.slee.resource.diameter.base.events.DeviceWatchdogAnswer;
import net.java.slee.resource.diameter.base.events.DiameterCommand;
import net.java.slee.resource.diameter.base.events.DiameterHeader;
import net.java.slee.resource.diameter.base.events.avp.AvpList;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentityAvp;
import net.java.slee.resource.diameter.base.events.avp.FailedAvp;

public class DeviceWatchdogAnswerImpl extends DiameterMessageImpl implements DeviceWatchdogAnswer
{

	@Override
	public String getLongName() {
		
		return "Device-Watchdog-Answer";
	}

	@Override
	public String getShortName() {

		return "DWA";
	}

	public String getErrorMessage() {
		// TODO Auto-generated method stub
		return null;
	}

	public FailedAvp[] getFailedAvps() {
		// TODO Auto-generated method stub
		return null;
	}

	public long getOriginStateId() {
		// TODO Auto-generated method stub
		return 0;
	}

	public long getResultCode() {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean hasErrorMessage() {
		// TODO Auto-generated method stub
		return false;
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

	public boolean hasResultCode() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setErrorMessage(String errorMessage) {
		// TODO Auto-generated method stub
		
	}

	public void setFailedAvp(FailedAvp failedAvp) {
		// TODO Auto-generated method stub
		
	}

	public void setFailedAvps(FailedAvp[] failedAvps) {
		// TODO Auto-generated method stub
		
	}

	public void setOriginStateId(long originStateId) {
		// TODO Auto-generated method stub
		
	}

	public void setResultCode(long resultCode) {
		// TODO Auto-generated method stub
		
	}

  
}
