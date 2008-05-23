package org.mobicents.slee.resource.diameter.base.events;

import net.java.slee.resource.diameter.base.events.AbortSessionRequest;

import org.jdiameter.api.Message;

/**
 * 
 * Implementation of Abort-Session-Request Diameter Message.
 *
 * <br>Super project:  mobicents
 * <br>3:07:34 PM May 21, 2008 
 * <br>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a> 
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a> 
 * @author Erick Svenson
 */
public class AbortSessionRequestImpl extends ExtensionDiameterMessageImpl implements AbortSessionRequest {

	public AbortSessionRequestImpl(Message message) {
    super(message);
  }
	
	@Override
	public String getLongName() {

		return "Abort-Session-Request";
	}

	@Override
	public String getShortName() {
		
		return "ASR";
	}
  
}
