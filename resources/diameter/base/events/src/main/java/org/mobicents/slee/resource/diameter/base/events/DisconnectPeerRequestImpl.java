package org.mobicents.slee.resource.diameter.base.events;

import net.java.slee.resource.diameter.base.events.DisconnectPeerRequest;
import net.java.slee.resource.diameter.base.events.avp.DisconnectCauseType;

import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.Message;

/**
 * 
 * Implementation of Disconnect-Peer-Request Diameter Message.
 *
 * <br>Super project:  mobicents
 * <br>3:36:52 PM May 21, 2008 
 * <br>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a> 
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a> 
 * @author Erick Svenson
 */
public class DisconnectPeerRequestImpl extends DiameterMessageImpl implements DisconnectPeerRequest
{

	public DisconnectPeerRequestImpl(Message message) {
		super(message);
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
		
		if(!hasDisconnectCause())
			return null;
		Avp avp=super.message.getAvps().getAvp(Avp.DISCONNECT_CAUSE);
		
		
		try {
			DisconnectCauseType type=DisconnectCauseType.fromInt(avp.getInteger32());
			return type;
		} catch (AvpDataException e) {
		  log.error( "", e );
		}
		
		return null;
		
	}

	public boolean hasDisconnectCause() {
		return super.message.getAvps().getAvp(Avp.DISCONNECT_CAUSE)!=null;
	}

	public void setDisconnectCause(DisconnectCauseType disconnectCause) {
		super.setAvpAsUInt32(Avp.DISCONNECT_CAUSE, disconnectCause.getValue(), true, true);
		
	}
  
}
