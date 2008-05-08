package org.mobicents.slee.resource.diameter.base.events;

import net.java.slee.resource.diameter.base.events.DiameterCommand;
import net.java.slee.resource.diameter.base.events.DiameterHeader;
import net.java.slee.resource.diameter.base.events.ExtensionDiameterMessage;
import net.java.slee.resource.diameter.base.events.avp.AvpList;
import net.java.slee.resource.diameter.base.events.avp.AvpNotAllowedException;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentityAvp;

public class ExtensionDiameterMessageImpl extends DiameterMessageImpl implements ExtensionDiameterMessage
{

	@Override
	public String getLongName() {
		//FIXME: baranowb; not documented
		return "Extension-Message";
	}

	@Override
	public String getShortName() {
		//FIXME: baranowb; not documented
		return "EM";
	}

	public AvpList getExtensionAvps() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setExtensionAvps(AvpList avps) throws AvpNotAllowedException {
		// TODO Auto-generated method stub
		
	}

	

}
