package org.mobicents.slee.resource.diameter.base.events;

import org.jdiameter.api.Message;

import net.java.slee.resource.diameter.base.events.DiameterCommand;
import net.java.slee.resource.diameter.base.events.DiameterHeader;
import net.java.slee.resource.diameter.base.events.DisconnectPeerRequest;

import net.java.slee.resource.diameter.base.events.avp.DiameterIdentityAvp;
import net.java.slee.resource.diameter.base.events.avp.DisconnectCauseType;

public class DisconnectPeerRequestImpl extends DiameterMessageImpl implements DisconnectPeerRequest
{

	public DisconnectPeerRequestImpl(Message message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getLongName() {
		return "Disconnect-Peer-Request";
	}

	@Override
	public String getShortName() {

		return "DPR";
	}

	public DisconnectCauseType getDisconnectCause() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean hasDisconnectCause() {
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

	public void setDisconnectCause(DisconnectCauseType disconnectCause) {
		// TODO Auto-generated method stub
		
	}

  
}
