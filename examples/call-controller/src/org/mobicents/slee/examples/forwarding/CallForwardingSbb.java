/***************************************************
 *                                                 *
 *  Mobicents: The Open Source VoIP Platform       *
 *                                                 *
 *  Distributable under LGPL license.              *
 *  See terms of license at gnu.org.               *
 *                                                 *
 ***************************************************/
package org.mobicents.slee.examples.forwarding;

import java.text.ParseException;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sip.InvalidArgumentException;
import javax.sip.ServerTransaction;
import javax.sip.SipException;
import javax.sip.address.Address;
import javax.sip.address.URI;
import javax.sip.header.ContactHeader;
import javax.sip.header.HeaderFactory;
import javax.sip.header.ToHeader;
import javax.sip.message.Request;
import javax.sip.message.Response;
import javax.slee.ActivityContextInterface;
import javax.slee.AddressPlan;
import javax.slee.SLEEException;
import javax.slee.SbbContext;
import javax.slee.TransactionRequiredLocalException;
import javax.slee.profile.UnrecognizedProfileNameException;
import javax.slee.profile.UnrecognizedProfileTableNameException;

import org.mobicents.slee.examples.common.ProfileSbb;
import org.mobicents.slee.examples.profiles.ControllerProfileCMP;
import org.mobicents.slee.services.sip.common.SipSendErrorResponseException;
import org.mobicents.slee.services.sip.registrar.LocationService;
import org.mobicents.slee.services.sip.registrar.LocationServiceException;
import org.mobicents.slee.services.sip.registrar.RegistrationBinding;

/**
 * This SBB is used to know whether or not the called user is available and
 * the backup address when the called user is not available. <BR>
 * The forwarding will consist in sending the caller a MOVED_TEMPORARILY 
 * message with the new contact address.
 * @author Victor
 *
 */
public abstract class CallForwardingSbb extends ProfileSbb {
	Logger logger = Logger.getLogger(org.mobicents.slee.examples.forwarding.CallForwardingSbb.class.getName());
	
	private LocationService locationService = new LocationService();
	
	public boolean accept(javax.sip.RequestEvent event){
		// To know if the called user is available.
		boolean available = false;
		
		try {
			Request request = event.getRequest();	
			ToHeader toHeader = (ToHeader)request.getHeader(ToHeader.NAME);
			URI toURI = toHeader.getAddress().getURI();
			
			URI contactURI = findLocalTarget(toURI);
			
			if (contactURI != null) {
				available = true;	
			}
			
		}catch (SipSendErrorResponseException e) {
			logger.log(Level.WARNING, e.getMessage(), e);
		}
		
		return available;
	}
	
	public Address forwarding(javax.sip.RequestEvent event, ActivityContextInterface ac){
		// To know if the called user has the Forwarding service activated
		// boolean enable = false;
		Address toAddress = null;		
		Request request = event.getRequest();
		
		try {
			// Checking if the called user has any backup address
			ToHeader toHeader = (ToHeader)request.getHeader(ToHeader.NAME);
			String toURI = toHeader.getAddress().getURI().toString();
			
			Address backupAddress = getBackupAddress(toURI);
			
			if (backupAddress != null) {
				// Checking whether the called user has any backup address.
				toAddress = getAddressFactory().createAddress(backupAddress.toString());
				// Notifying the caller that the call has to be redirected
				ServerTransaction st = (ServerTransaction) ac.getActivity();
				ContactHeader contactHeader = getHeaderFactory().createContactHeader(toAddress);
				Response response = getMessageFactory().createResponse(Response.MOVED_TEMPORARILY, request);
				response.setHeader(contactHeader);
				st.sendResponse(response);
				// The Request-URI of the new request uses the value
				// of the Contact header field in the response		
			}

		}catch (ParseException e) {
			logger.log(Level.WARNING, e.getMessage(), e);
		}catch (TransactionRequiredLocalException e) {
			logger.log(Level.WARNING, e.getMessage(), e);
		}catch (SLEEException e) {
			logger.log(Level.WARNING, e.getMessage(), e);
		}catch (SipException e) {
			logger.log(Level.WARNING, e.getMessage(), e);
		}catch (InvalidArgumentException e) {
			logger.log(Level.WARNING, e.getMessage(), e);
		}
		
		return toAddress;
	}
	
	/**
	 * Attempts to find a locally registered contact address for the given URI,
	 * using the location service interface.
	 */
	private URI findLocalTarget(URI uri) throws SipSendErrorResponseException {
		String addressOfRecord = uri.toString();
		URI target = null;
		Map bindings = null;
		
		try {
			bindings = locationService.getBindings(addressOfRecord);
			
		} catch (LocationServiceException lse) {
			lse.printStackTrace();
		}
		
		if (bindings != null & !bindings.isEmpty()) {
			Iterator it = bindings.values().iterator();
			
			while (it.hasNext()) {
				RegistrationBinding binding = (RegistrationBinding) it.next();
				logger.info("BINDINGS: " + binding + "\n");
				ContactHeader header = binding.getContactHeader(getAddressFactory(), getHeaderFactory());
				logger.info("CONTACT HEADER: " + header + "\n");
				
				if (header == null) { // entry expired
					continue; // see if there are any more contacts...
				}
				
				Address na = header.getAddress();
				logger.info("\nAddress: " + na);
				target = na.getURI();
				break;
			}
			
			if (target == null) {
				logger.warning("findLocalTarget: No contacts for " 
						+ addressOfRecord + " found.\n");
				throw new SipSendErrorResponseException("User temporarily unavailable", 
						Response.TEMPORARILY_UNAVAILABLE);
			}
		}
		
		return target;
	}
	
	/**
	 * Attempt to find a Backup Address, but the method returns null
	 * if there isn't any address to forward the INVITE. 
	 */
    private Address getBackupAddress(String sipAddress) {
    	Address backupAddress = null;
    	ControllerProfileCMP profile = lookup(new javax.slee.Address(AddressPlan.SIP,sipAddress));
    	   		
    	if (profile != null) {

    		javax.slee.Address address = profile.getBackupAddress();
    		
    		if (address != null) {
        	
    			try {
    				backupAddress = getAddressFactory().createAddress(address.getAddressString());
           
    			} catch (ParseException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
    		}
    	}
    	  	
    	return backupAddress;
    }
    
    public void setSbbContext(SbbContext context) {	
    	super.setSbbContext(context);
    	
        // To create Header objects from a particular implementation of JAIN SIP
        headerFactory = getSipFactoryProvider().getHeaderFactory();
    	}
    
    protected final HeaderFactory getHeaderFactory() { return headerFactory; }
    
	private HeaderFactory headerFactory;
	
	public abstract ControllerProfileCMP getControllerProfileCMP(javax.slee.profile.ProfileID profileID) 
	throws UnrecognizedProfileNameException, UnrecognizedProfileTableNameException;
}