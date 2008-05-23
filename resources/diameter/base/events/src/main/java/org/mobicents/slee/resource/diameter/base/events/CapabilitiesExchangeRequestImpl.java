package org.mobicents.slee.resource.diameter.base.events;

import net.java.slee.resource.diameter.base.events.CapabilitiesExchangeRequest;

import org.jdiameter.api.Message;

/**
 * 
 * Implementation of Capabilities-ExchangeRequest Diameter Message.
 *
 * <br>Super project:  mobicents
 * <br>3:23:52 PM May 21, 2008 
 * <br>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a> 
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a> 
 * @author Erick Svenson
 */
public class CapabilitiesExchangeRequestImpl extends CapabilitiesExchangeAnswerImpl implements CapabilitiesExchangeRequest
{

	public CapabilitiesExchangeRequestImpl(Message message) {
		super(message);
	}

	@Override
	public String getLongName() {
		
		return "Capabilities-Exchange-Request";
	}

	@Override
	public String getShortName() {

		return "CER";
	}
	
}
