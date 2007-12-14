package org.mobicents.slee.services.sip.registrar;

import java.util.Map;

import javax.slee.SbbLocalObject;

import org.mobicents.slee.services.sip.proxy.RegistrationInformationAccess;

/**
 * This interface has to be implemented by sbbs that act as registrar service in order to give
 * sip proxy way of retrieving bindings for address.
 * Sbbs which implement this methods should also be process REGISTER requests somehow and store information about registration
 * in way that is completly abstract to proxy - db, cache, file etc...
 * @author baranowb
 *
 */
public interface RegistrationInformationAccessSbbLocalInterface extends SbbLocalObject,RegistrationInformationAccess {
	
	
	
}
