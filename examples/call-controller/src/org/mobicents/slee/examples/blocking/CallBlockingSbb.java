/***************************************************
 *                                                 *
 *  Mobicents: The Open Source VoIP Platform       *
 *                                                 *
 *  Distributable under LGPL license.              *
 *  See terms of license at gnu.org.               *
 *                                                 *
 ***************************************************/
package org.mobicents.slee.examples.blocking;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sip.InvalidArgumentException;
import javax.sip.ServerTransaction;
import javax.sip.SipException;
import javax.sip.address.SipURI;
import javax.sip.header.FromHeader;
import javax.sip.header.ToHeader;
import javax.sip.message.Request;
import javax.sip.message.Response;
import javax.slee.ActivityContextInterface;
import javax.slee.Address;
import javax.slee.AddressPlan;
import javax.slee.SLEEException;
import javax.slee.TransactionRequiredLocalException;
import javax.slee.profile.UnrecognizedProfileNameException;
import javax.slee.profile.UnrecognizedProfileTableNameException;

import org.mobicents.slee.examples.common.ProfileSbb;
import org.mobicents.slee.examples.profiles.ControllerProfileCMP;

/**
 * This SBB is used to decide whether or not an INVITE has to be blocked. <BR>
 * The CallBlockingSbb receives requests from the Controller and checks 
 * whether the To Address is blocking the From Address. <BR>
 * If the INVITE is blocked, this SBB sends a FORBIDDEN message to the 
 * caller and returns True to the Controller. <BR>
 * If the INVITE is not blocked, this SBB returns False to the Controller.
 * @author Victor
 *
 */
public abstract class CallBlockingSbb extends ProfileSbb {
	Logger logger = Logger.getLogger(org.mobicents.slee.examples.blocking.CallBlockingSbb.class.getName());
	
	public boolean accept(javax.sip.RequestEvent event, ActivityContextInterface ac){
		boolean eventblocked = false;
		Request request = event.getRequest();
			
		try {			
			FromHeader fromHeader = (FromHeader)request.getHeader(FromHeader.NAME);
			ToHeader toHeader = (ToHeader)request.getHeader(ToHeader.NAME);
		
			String fromURI = fromHeader.getAddress().getURI().toString();
			String toURI = toHeader.getAddress().getURI().toString();
				
			ArrayList targets = getBlockedArrayList(toURI);
			
			if (targets != null) {
				// Cheking whether the caller is blocked by the called user
				for (int i = 0; i < targets.size(); i++ ) {
					
					if ((targets.get(i).toString()).equals(fromURI)) {
						
						eventblocked = true;

						// Notifiying the client that the INVITE has been blocked
						ServerTransaction stBlocking = (ServerTransaction) ac.getActivity();
						Response blockingResponse = getMessageFactory().createResponse(Response.FORBIDDEN, request);
			    		stBlocking.sendResponse(blockingResponse);
					}
				}
			}
						
		}catch (TransactionRequiredLocalException e) {
			logger.log(Level.WARNING, e.getMessage(), e);
		}catch (SLEEException e) {
			logger.log(Level.WARNING, e.getMessage(), e);
		}catch (ParseException e) {
			logger.log(Level.WARNING, e.getMessage(), e);
		}catch (SipException e) {	
			logger.log(Level.WARNING, e.getMessage(), e);
		}catch (InvalidArgumentException e) {
			logger.log(Level.WARNING, e.getMessage(), e);
		}
		
		return eventblocked;
	}
	
	/**
	 * Attempt to find a list of Blocked Addresses (SIP URIs), but the method returns null
	 * if the called user (sipAddress) doesn't block to any user. 
	 */
    private ArrayList getBlockedArrayList(String sipAddress) {    	
    	ArrayList uris = null;
    	ControllerProfileCMP profile = lookup(new Address(AddressPlan.SIP,sipAddress));
        if (profile != null) {
        	Address[] addresses = profile.getBlockedAddresses();
        	
        	if (addresses != null) {
        		uris = new ArrayList(addresses.length);
        		
        		for (int i = 0; i < addresses.length; i++) {
        			String address = addresses[i].getAddressString();
        			
        			try {
        				SipURI uri = (SipURI) getAddressFactory().createURI(address);
        				uris.add(uri);
                    
        			} catch (ParseException e) {
        				logger.log(Level.WARNING, e.getMessage(), e);
        			}
        		}
        	}
        }       
        
        return uris;
    }
    
	public abstract ControllerProfileCMP getControllerProfileCMP(javax.slee.profile.ProfileID profileID) 
			throws UnrecognizedProfileNameException, UnrecognizedProfileTableNameException;
}
