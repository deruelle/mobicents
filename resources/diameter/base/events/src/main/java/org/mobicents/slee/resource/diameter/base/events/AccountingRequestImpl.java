package org.mobicents.slee.resource.diameter.base.events;

import net.java.slee.resource.diameter.base.events.AccountingRequest;

import org.jdiameter.api.Message;

/**
 * 
 * Implementation of Accounting-Request Diameter Message.
 *
 * <br>Super project:  mobicents
 * <br>3:09:05 PM May 21, 2008 
 * <br>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a> 
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a> 
 * @author Erick Svenson
 */
public class AccountingRequestImpl extends AccountingAnswerImpl implements AccountingRequest
{

	public AccountingRequestImpl(Message message) {
		super(message);
		
	}

	@Override
	public String getLongName() {
		
		return "Accounting-Request";
	}

	@Override
	public String getShortName() {
	
		return "ACR";
	}
 
}
