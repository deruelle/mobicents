package org.mobicents.slee.services.sip.proxy;

import java.util.Map;
import java.util.Set;

public interface RegistrationInformationAccess {

	
	/**
	 * 
	 * @param sipAddress - address for which we have to search bindings
	 * @return copy of map that stores {@link RegistrationBinding}  objects for passed sipAddress, if no bindings are available it return empty map.
	 * key==contact addres, value==RegistrationBinding
	 */
	public Map getBindings(String sipAddress);
	
	/** 
	 * 
	 * @return Set containing all sip:users registered
	 */
	public Set getRegisteredUsers();
}
