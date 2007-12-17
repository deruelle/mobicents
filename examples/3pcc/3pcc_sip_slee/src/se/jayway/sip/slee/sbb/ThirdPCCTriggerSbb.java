 /*
  * Mobicents: The Open Source SLEE Platform      
  *
  * Copyright 2003-2005, CocoonHive, LLC., 
  * and individual contributors as indicated
  * by the @authors tag. See the copyright.txt 
  * in the distribution for a full listing of   
  * individual contributors.
  *
  * This is free software; you can redistribute it
  * and/or modify it under the terms of the 
  * GNU Lesser General Public License as
  * published by the Free Software Foundation; 
  * either version 2.1 of
  * the License, or (at your option) any later version.
  *
  * This software is distributed in the hope that 
  * it will be useful, but WITHOUT ANY WARRANTY; 
  * without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR 
  * PURPOSE. See the GNU Lesser General Public License
  * for more details.
  *
  * You should have received a copy of the 
  * GNU Lesser General Public
  * License along with this software; 
  * if not, write to the Free
  * Software Foundation, Inc., 51 Franklin St, 
  * Fifth Floor, Boston, MA
  * 02110-1301 USA, or see the FSF site:
  * http://www.fsf.org.
  */

package se.jayway.sip.slee.sbb;

import java.text.ParseException;

import javax.ejb.TransactionRequiredLocalException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sip.ClientTransaction;
import javax.sip.Dialog;
import javax.sip.InvalidArgumentException;
import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;
import javax.sip.SipException;
import javax.sip.SipProvider;
import javax.sip.address.Address;
import javax.sip.header.CallIdHeader;
import javax.sip.header.Header;
import javax.sip.message.Request;
import javax.sip.message.Response;
import javax.slee.ActivityContextInterface;
import javax.slee.ChildRelation;
import javax.slee.CreateException;
import javax.slee.InitialEventSelector;
import javax.slee.RolledBackContext;
import javax.slee.SLEEException;
import javax.slee.SbbContext;
import javax.slee.SbbLocalObject;
import javax.slee.UnrecognizedActivityException;

import org.apache.log4j.Logger;
import org.mobicents.slee.resource.sip.SipActivityContextInterfaceFactory;
import org.mobicents.slee.resource.sip.SipFactoryProvider;

import se.jayway.sip.slee.event.CancellationEvent;
import se.jayway.sip.slee.event.TerminationEvent;
import se.jayway.sip.slee.event.ThirdPCCTriggerEvent;
import se.jayway.sip.util.CacheException;
import se.jayway.sip.util.CacheFactory;
import se.jayway.sip.util.CacheUtility;
import se.jayway.sip.util.Session;
import se.jayway.sip.util.SessionAssociation;
import se.jayway.sip.util.SipUtils;
import se.jayway.sip.util.SipUtilsFactorySingleton;
import se.jayway.sip.util.StateCallback;
/**
 * Sbb for controlling the callflow in a 3pcc application. <br>
 * This Sbb is resposible for triggering the callflow (initiated by e.g. a servlet)
 * and terminating it. <br><br>
 * For this there are two custom events ThirdPCCTriggerEvent and TerminationEvent.<br>
 * The ThirdPCCTriggerEvent has an identifier that is used to identify the session for 
 * subsequent operations (e.g. termination). <br><br>
 * The TerminationEvent has an identifier that identifies the session to be teared down.
 * <br><br>
 * This Sbb also processes BYE events because it will typically be used as root Sbb and the BYE 
 * requests sent from UAs will cause root Sbbs to be created (the activity being a Sip transaction). 
 * @author niklas
 *
 */
public abstract class ThirdPCCTriggerSbb implements javax.slee.Sbb {
	private static Logger log = Logger.getLogger(ThirdPCCTriggerSbb.class);

	private SbbContext sbbContext; // This SBB's SbbContext

	private SipFactoryProvider factoryProvider;

	private SipProvider sipProvider;

	private SipActivityContextInterfaceFactory activityContextInterfaceFactory;

	private CacheUtility cache;

	private SipUtils sipUtils;
	
	public abstract se.jayway.sip.slee.sbb.ThirdPCCTriggerSbbActivityContextInterface asSbbActivityContextInterface(
			ActivityContextInterface aci);

	// child relation
	public abstract ChildRelation getCallControlSbbChild();

	
	
	private String callControlSipAddress;
	
	
	/**
	 * We use the ActivityContext for custom events (ThirdPartyPCCTriggerEvent and TerminationEvent)
	 * and callId for Request.BYE events.
	 * <br>
	 * In fact, it does not matter if we use callId for BYEs because they will cause new
	 * root Sbbs to be created in both cases.
	 * <br><br>
	 *  
	 */ 
	public InitialEventSelector initialEventSelect(InitialEventSelector ies) {
		
		Object event = ies.getEvent();
		String callId = null;
		if ( log.isDebugEnabled() ) {
			log.debug("initialEventSelect with event " + event);
		}
		if (event instanceof ResponseEvent) {
			// If response event, the convergence name to callId
			Response response = ((ResponseEvent) event).getResponse();
			callId = ((CallIdHeader) response.getHeader(CallIdHeader.NAME))
					.getCallId();
		} else if (event instanceof RequestEvent) {
			// If request event, the convergence name to callId
			Request request = ((RequestEvent) event).getRequest();
			callId = ((CallIdHeader) request.getHeader(CallIdHeader.NAME))
					.getCallId();
		} else if(event instanceof ThirdPCCTriggerEvent) {
			String guid = ((ThirdPCCTriggerEvent)event).getGUID();
			ies.setCustomName(guid);
			return ies;
		} else if (event instanceof TerminationEvent ) {
			String guid = ((TerminationEvent)event).getGuid();
			ies.setCustomName(guid);
			return ies;
		} else if (event instanceof CancellationEvent ) {
			String guid = ((CancellationEvent)event).getGuid();
			ies.setCustomName(guid);
			return ies;
		} else {
			// If something else, use activity context.
			ies.setActivityContextSelected(true);
			return ies;
		}
		// Set the convergence name
		if(log.isDebugEnabled()) {
			log.debug("Setting convergence name to: " + callId);
		}
		ies.setCustomName(callId);
		return ies;
	}
	/**
	 * This method requires a ThirdPCCTriggerEvent with an identifier and two sip addresses on the form 
	 * 'sip:usernam@domain:port' or 'sip:username@ipaddress:port'.
	 * <br>
	 * <br>
	 * It fires off an INVITE to callee and then creates a child Sbb (CallControlSbb) that is resposible 
	 * for the main part of the call flow. <br>
	 * The child Sbb is attached to the ActivityContext representing the client transaction in order 
	 * to receive responses. 
	 * @param event
	 * @param aci
	 */
	public void onThirdPCCTriggerEvent(
			se.jayway.sip.slee.event.ThirdPCCTriggerEvent event,
			ActivityContextInterface aci) {


		if(log.isDebugEnabled()) {
			log.debug("Received " + event + " in onThirdPartyPCCTriggerEvent");
			log.debug("Activitycount = " + sbbContext.getActivities().length);
			log.debug("This activity = " + aci.getActivity());
		}

		ChildRelation relation = getCallControlSbbChild();
		SbbLocalObject child;
		try {
			// Retrieve the callee addresses from the event
//			Address callerAddress = sipUtils.convertURIToAddress(event
//					.getCallerURI());
			
			// Set the caller address to the address of our call controller
			Address callerAddress = sipUtils.convertURIToAddress(getCallControlSipAddress());
			//Set the display name to the caller address so callee know who's ringing
			callerAddress.setDisplayName(event.getCallerURI());
			
			// Retrieve the callee addresses from the event
			Address calleeAddress = sipUtils.convertURIToAddress(event
					.getCalleeURI());

			// Build the INVITE request
			Request request = sipUtils.buildInvite(callerAddress, calleeAddress, null, 1);
			// Create a new transaction based on the generated request
			ClientTransaction ct = sipProvider.getNewClientTransaction(request);
			
			// Get activity context from factory
			ActivityContextInterface ac = activityContextInterfaceFactory
					.getActivityContextInterface(ct);

			Header h = ct.getRequest().getHeader(CallIdHeader.NAME);
			String calleeCallId = ((CallIdHeader) h).getCallId();

			SessionAssociation sa = new SessionAssociation("se.jayway.sip.slee.sbb.CallControlSbb$InitialState");
			// enable state sharing via callback interface (which may be null)
			StateCallback callback = event.getStateCallback();
			if ( callback != null ) {
				if ( callback.getSessionState() == null ) { // This may or may not be set by the component triggering the callflow
					callback.setSessionState("se.jayway.sip.slee.sbb.CallControlSbb$InitialState");
				}
				sa.setStateCallback(event.getStateCallback());
				
			}
			
			Session calleeSession = new Session(calleeCallId);
			calleeSession.setSipAddress(calleeAddress);
			calleeSession.setToBeCancelledClientTransaction(ct);
			// The dialog for the client transaction in which the INVITE is sent
			Dialog dialog = ct.getDialog();
			if ( dialog != null && log.isDebugEnabled() ) {
				log.debug("Obtained dialog from ClientTransaction : automatic dialog support on");
			}
			if ( dialog == null ) {
				// Automatic dialog support turned off
				try {
					dialog = sipProvider.getNewDialog(ct);
					if ( log.isDebugEnabled() ) {
						log.debug("Obtained dialog for INVITE request to callee with getNewDialog");
					}
				} catch (Exception e) {
					log.error("Error getting dialog", e);
				}
			} 
			if ( log.isDebugEnabled() ) {
				log.debug("Obtained dialog in onThirdPCCTriggerEvent : callId = " + dialog.getCallId().getCallId());
			}
			calleeSession.setDialog(dialog);
			
			sa.setCalleeSession(calleeSession);
			
			Session callerSession = new Session();
			
			// Create a new caller address from caller URI specified in the event (the real caller address)
			// since we need this in the next INVITE.
			callerAddress = sipUtils.convertURIToAddress(event.getCallerURI());
			callerSession.setSipAddress(callerAddress);
			//Since we don't have the client transaction for the caller yet, just set
			//the to be cancelled client transaction to null.
			callerSession.setToBeCancelledClientTransaction(null);
			sa.setCallerSession(callerSession);
			
			//	Add GUID to the cache as a key for the session association
			cache.put(event.getGUID(), sa);

			// put the callId for the callee dialog in the cache
			cache.put(calleeCallId, sa);

			// Create child SBB
			child = relation.create();

			// Attach child SBB to the activity context
			ac.attach(child);
			// Send the INVITE request
			ct.sendRequest();

		} catch (TransactionRequiredLocalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SLEEException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CreateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SipException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnrecognizedActivityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * Terminates a session represented by a TerminationEvent. <br><br>
	 * This method only dispatches the call signalling to the child Sbb (CallControlSbb) 
	 * by means of a synchronous call.  
	 * @param event
	 * @param aci
	 */
	public void onTerminationEvent(TerminationEvent event, ActivityContextInterface aci) {
		if ( log.isDebugEnabled() ) {
			log.debug("Received " + event + " in onTerminationEvent");
			log.debug("Activitycount = " + sbbContext.getActivities().length);
			log.debug("This activity = " + aci.getActivity());
		}
		
		ChildRelation childRelation = getCallControlSbbChild();
		
		try {
			CallControlSbbLocalObject child = (CallControlSbbLocalObject) childRelation.create();
			child.terminate(event.getGuid());
		} catch (javax.slee.TransactionRequiredLocalException e) {
			log.error("Exception while invoking terminate on CallControlSbb child", e);
		} catch (SLEEException e) {
			log.error("Exception while invoking terminate on CallControlSbb child", e);
		} catch (CreateException e) {
			log.error("Exception while invoking terminate on CallControlSbb child", e);
		} 
	}
	
	
	/**
	 * Cancels a session represented by a CancellationEvent. 
	 * @param event
	 * @param aci
	 */
	public void onCancellationEvent(CancellationEvent event, ActivityContextInterface aci) {
		if ( log.isDebugEnabled() ) {
			log.debug("Received " + event + " in onCancellationEvent");
			log.debug("Activitycount = " + sbbContext.getActivities().length);
			log.debug("This activity = " + aci.getActivity());
		}
		
		ChildRelation childRelation = getCallControlSbbChild();
		
		try {
			CallControlSbbLocalObject child = (CallControlSbbLocalObject) childRelation.create();
			child.cancel(event.getGuid());
		} catch (javax.slee.TransactionRequiredLocalException e) {
			log.error("Exception while invoking cancel on CallControlSbb child", e);
		} catch (SLEEException e) {
			log.error("Exception while invoking cancel on CallControlSbb child", e);
		} catch (CreateException e) {
			log.error("Exception while invoking cancel on CallControlSbb child", e);
		} 
	}
	/**
	 * This method is receives a BYE (typically sent by one of the UAs in the 3pcc session)
	 * and terminates the other dialog in the 3pcc session by sending a BYE. 
	 * @param event
	 * @param aci
	 */
	public void onByeEvent(RequestEvent event, ActivityContextInterface aci) {
		if(log.isDebugEnabled()) {
			log.debug("Received " + event + " in onByeEvent");
			
			log.debug("Activitycount = " + sbbContext.getActivities().length);
			log.debug("This activity = " + aci.getActivity());
		}
		
		ChildRelation relation = getCallControlSbbChild();
		SbbLocalObject child;
		
		// Create child SBB
		try {
			
			
			child = relation.create();
			// Attach child SBB to the activity context
			aci.attach(child);
			
			// Detach ourselves
			aci.detach(getSbbContext().getSbbLocalObject());
			
		} catch (javax.slee.TransactionRequiredLocalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SLEEException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CreateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}

	public void setSbbContext(SbbContext context) {
		this.sbbContext = context;
		if(log.isDebugEnabled()) {
			log.debug("setSbbContext called in ThirdPCCTrigger.");
		}

		try {
			
			InitialContext ctx = new InitialContext();			
			// Create the cache used for the session association
			cache = CacheFactory.getInstance().getCache();
			
			if(log.isDebugEnabled()) {
				log.debug("Got Cache instance!");
			}
			
			Context myEnv = (Context) ctx.lookup("java:comp/env");

			// Getting JAIN SIP Resource Adaptor interfaces
			factoryProvider = (SipFactoryProvider) myEnv
					.lookup("slee/resources/jainsip/1.1/provider");

			sipProvider = factoryProvider.getSipProvider();
			
			// Check that the callControlSipAddress is present
			callControlSipAddress = (String)myEnv.lookup("callControlSipAddress");

			activityContextInterfaceFactory = (SipActivityContextInterfaceFactory) myEnv
					.lookup("slee/resources/jainsip/1.1/acifactory");
			sipUtils = SipUtilsFactorySingleton.getInstance().getSipUtils();
		} catch (NamingException ne) {
			ne.printStackTrace();
		} catch (CacheException ce) {
			ce.printStackTrace();
		}
		
	}

	public void unsetSbbContext() {
		this.sbbContext = null;
	}

	// TODO: Implement the lifecycle methods if required
	public void sbbCreate() throws javax.slee.CreateException {
		if(log.isDebugEnabled()) {
			log.debug("sbbCreate called");
		}
		
	}

	public void sbbPostCreate() throws javax.slee.CreateException {
		if(log.isDebugEnabled()) {
			log.debug("sbbPostCreate called");
		}
	}

	public void sbbActivate() {
		if(log.isDebugEnabled()) {
			log.debug("sbbActivate called");
		}
	}

	public void sbbPassivate() {
		if(log.isDebugEnabled()) {
			log.debug("sbbPassivate called");
		}
	}

	public void sbbRemove() {
		log.debug("sbbRemove called");
	}

	public void sbbLoad() {
		if(log.isDebugEnabled()) {
			log.debug("sbbLoad called");
		}
	}

	public void sbbStore() {
		if(log.isDebugEnabled()) {
			log.debug("sbbStore called");
		}
	}

	public void sbbExceptionThrown(Exception exception, Object event,
			ActivityContextInterface activity) {
	}

	public void sbbRolledBack(RolledBackContext context) {
	}

	/**
	 * Convenience method to retrieve the SbbContext object stored in
	 * setSbbContext. TODO: If your SBB doesn't require the SbbContext object
	 * you may remove this method, the sbbContext variable and the variable
	 * assignment in setSbbContext().
	 * 
	 * @return this SBB's SbbContext object
	 */

	protected SbbContext getSbbContext() {
		return sbbContext;
	}
	
	public String getCallControlSipAddress() {
		return callControlSipAddress;
	}

	public void setCallControlSipAddress(String username) {
		this.callControlSipAddress = username;
	}

}