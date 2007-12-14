package org.mobicents.slee.services.sip.registrar;

import javax.sip.address.AddressFactory;
import javax.sip.header.HeaderFactory;
import javax.sip.message.MessageFactory;

public interface ResourcesProvider {

	
	public AddressFactory getAddressFactory() ;
    public HeaderFactory getHeaderFactory() ;
    public MessageFactory getMessageFactory() ;
	
	
}
