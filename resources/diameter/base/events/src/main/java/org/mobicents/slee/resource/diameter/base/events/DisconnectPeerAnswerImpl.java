package org.mobicents.slee.resource.diameter.base.events;

import net.java.slee.resource.diameter.base.events.DisconnectPeerAnswer;

import org.jdiameter.api.Message;

/**
 * 
 * Implementation of Disconnect-Peer-Answer Diameter Message.
 *
 * <br>Super project:  mobicents
 * <br>3:34:49 PM May 21, 2008 
 * <br>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a> 
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a> 
 * @author Erick Svenson
 */
public class DisconnectPeerAnswerImpl extends DiameterMessageImpl implements DisconnectPeerAnswer
{

	public DisconnectPeerAnswerImpl(Message message) {
		super(message);
	}

	@Override
	public String getLongName() {
		
		return "Disconnect-Peer-Answer";
	}

	@Override
	public String getShortName() {
		
		return "DPA";
	}
  
}
