package org.mobicents.slee.resource.diameter.base.events;

import org.jdiameter.api.Message;

import net.java.slee.resource.diameter.base.events.DiameterCommand;
import net.java.slee.resource.diameter.base.events.DiameterHeader;
import net.java.slee.resource.diameter.base.events.ErrorAnswer;

import net.java.slee.resource.diameter.base.events.avp.AvpNotAllowedException;
import net.java.slee.resource.diameter.base.events.avp.DiameterAvp;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentityAvp;
import net.java.slee.resource.diameter.base.events.avp.ProxyInfoAvp;

public class ErrorAnswerImpl extends ExtensionDiameterMessageImpl implements ErrorAnswer
{

	public ErrorAnswerImpl(Message message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public ProxyInfoAvp getProxyInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean hasProxyInfo() {
		// TODO Auto-generated method stub
		return false;
	}



  
}
