package org.mobicents.slee.services.sip.common;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sip.address.URI;

public interface LocationInterface {

	public List<String> getUserLocations(URI reqeustURI);

	public RegistrationBinding addUserLocation(String sipAddress,String contactAddress, String comment,
			long expiresDelta, float q, String id, long seq) throws LocationServiceException;

	public Set<String> getRegisteredUsers();
	
	public Map<String,RegistrationBinding> getUserBindings(String sipAddress) throws LocationServiceException;
	
	public void removeBinding(String sipAddress, String contactAddress)throws LocationServiceException;
	
	
}
