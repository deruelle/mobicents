package org.mobicents.slee.examples.callforwardblock.controller;

import java.util.Iterator;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;
import javax.sip.address.URI;
import javax.sip.header.CallIdHeader;
import javax.sip.header.ToHeader;
import javax.sip.message.Request;
import javax.sip.message.Response;
import javax.slee.ActivityContextInterface;
import javax.slee.Address;
import javax.slee.ChildRelation;
import javax.slee.InitialEventSelector;
import javax.slee.SbbContext;
import javax.slee.SbbLocalObject;
import javax.slee.UnrecognizedActivityException;
import javax.slee.facilities.NameAlreadyBoundException;
import javax.slee.nullactivity.NullActivity;
import javax.slee.profile.ProfileFacility;
import javax.slee.profile.ProfileID;
import javax.slee.profile.UnrecognizedProfileNameException;
import javax.slee.profile.UnrecognizedProfileTableNameException;

import org.apache.log4j.Logger;
import org.mobicents.slee.examples.callforwardblock.BaseSbb;
import org.mobicents.slee.examples.callforwardblock.events.CallBlockReqEvent;
import org.mobicents.slee.examples.callforwardblock.events.CallBlockSuccessEvent;
import org.mobicents.slee.examples.callforwardblock.events.CallForwardEvent;
import org.mobicents.slee.examples.callforwardblock.events.CallForwardFailureEvent;
import org.mobicents.slee.examples.callforwardblock.events.CallForwardSuccessEvent;
import org.mobicents.slee.examples.callforwardblock.profile.CallForwardBlockProfileCMP;

/**
 * Controller SBB is the root SBB for all activity.
 * @author hchin
 */
public abstract class ControllerSbb extends BaseSbb {
	private static Logger log = Logger.getLogger(ControllerSbb.class); 
	protected ProfileFacility profileFacility;
	
	/**
	 * Process INVITE request by looking up profile table to see if the request should
	 * be blocked or forward.
	 * @param event RequestEvent that was issued
	 * @param aci ActivityContextInterface that generated this event
	 */
	public void onInviteEvent(RequestEvent event, ActivityContextInterface aci) {
		log.info("ControllerSbb received INVITE");
		boolean forward = false;
		boolean block = false;
		// lookup the To Header to see if destination should be forwarded, blocked or proxy
		try {
			ToHeader toHdr = (ToHeader)event.getRequest().getHeader(ToHeader.NAME);
			String toAddr = toHdr.getAddress().getURI().toString();
			int start = toAddr.indexOf(":");
			toAddr = toAddr.substring(start+1, toAddr.length());
			CallForwardBlockProfileCMP profile = null;
			ProfileID pid = profileFacility.getProfileByIndexedAttribute("CallForwardBlockProfileTable", 
					"address", toAddr);
			if (pid == null) {
				// did not find profile based on indexed attribute??? lookup all profiles
				Iterator it = profileFacility.getProfiles("CallForwardBlockProfileTable").iterator();
				while (it.hasNext()) {
					ProfileID id = (ProfileID)it.next();
					CallForwardBlockProfileCMP profileTmp = getCallForwardBlockProfileCMP(id);
					if (profileTmp.getAddress().equals(toAddr))
						profile = profileTmp;
				}
			} else {
				profile = getCallForwardBlockProfileCMP(pid);
			}
			if (profile != null) {
				log.info("found a profile that matches destination address");
				if (profile.getCallforward().booleanValue()) {
					forward = true;
					// change the requestUri to go to new destination address
					String destination = profile.getForwardToDestination();
					log.info("Call Forwarding INVITE to new destination: " + destination);
					String[] strUri = destination.split("@");
					URI reqUri = addrFactory.createSipURI(strUri[0], strUri[1]);
					event.getRequest().setRequestURI(reqUri);
				} else if (profile.getCallblock().booleanValue())
					block = true;
			}
		} catch (Exception e) {
			log.error("Error getting profile by index attribute. ", e);
		}

		// attach child SBB to localized ActivityContext for sending private messages
		ActivityContextInterface controllerACI = acNamingFacility.lookup(CONTROLLER_ACTIVITY_CONTEXT_NAME);
		ControllerActivityContextInterface caci = this.asSbbActivityContextInterface(controllerACI);
		caci.attach(getSbbLocalObject());
		
		if (forward) {
			log.info("Process INVITE to CallForwardSbb");
			SbbLocalObject callFwdChildSbb = proxyEventToChildSbb(aci, getCallForwardSbbChild());
			caci.attach(callFwdChildSbb);
		} else if (block) {
			log.info("Firing INVITE to CallBlockSbb");
			SbbLocalObject callBlockChildSbb = proxyEventToChildSbb(aci, getCallBlockSbbChild());
			caci.attach(callBlockChildSbb);
			CallBlockReqEvent blockEvent = new CallBlockReqEvent();
			blockEvent.setRequest(event.getRequest());
			fireCallBlockReqEvent(blockEvent, aci, null);
		} else {
			log.info("Proxying INVITE to ProxySbb");
			proxyEventToChildSbb(aci, getProxySbbChild());
			aci.detach(getSbbLocalObject());
		}
		return;
	}
	
	public void onAckEvent(RequestEvent event, ActivityContextInterface aci) {
		if (event.getDialog() != null) {
			log.info("ControllerSbb ACK received, proxying event to CallForwardSbb");
			proxyEventToChildSbb(aci, getCallForwardSbbChild());
		} else {
			log.info("AckEvent does not support dialog, proxying event to child ProxySBB");
			proxyEventToChildSbb(aci, getProxySbbChild());
		}
	}

	public void onByeEvent(RequestEvent event, ActivityContextInterface aci) {
		if (event.getDialog() != null) {
			log.info("Proxying ByeEvent to child ByeSBB");
			proxyEventToChildSbb(aci, getCallForwardSbbChild());
		} else {
			log.info("ByeEvent does not support dialog, proxying event to child ProxySBB");
			proxyEventToChildSbb(aci, getProxySbbChild());
		}
	}

	public void onRegisterEvent(javax.sip.RequestEvent event, ActivityContextInterface aci) {
		log.info("Proxying RegisterEvent to child RegisterSBB");
		proxyEventToChildSbb(aci, getRegistrarSbbChild());
	}

	public void onCallBlockSuccessEvent(CallBlockSuccessEvent event, ActivityContextInterface aci) {
		log.info("Received CallBlockSuccessEvent");
	}

	public void onCallForwardFailureEvent(CallForwardFailureEvent event, ActivityContextInterface aci) {
		log.info("Received CallForwardFailureEvent");
	}

	public void onCallForwardSuccessEvent(CallForwardSuccessEvent event, ActivityContextInterface aci) {
		log.info("Received CallForwardSuccessEvent");
	}

	// Define Fire event methods
	public abstract void fireCallBlockReqEvent (CallBlockReqEvent event, ActivityContextInterface aci, 
			Address address);
	public abstract void fireCallForwardEvent (CallForwardEvent event, ActivityContextInterface aci, 
			Address address);
	
	// Define child relation methods
	public abstract ChildRelation getCallBlockSbbChild();
	public abstract ChildRelation getCallForwardSbbChild();
	public abstract ChildRelation getByeSbbChild();
	public abstract ChildRelation getRegistrarSbbChild();
	public abstract ChildRelation getProxySbbChild();
	// Define Profile CMP method
	public abstract CallForwardBlockProfileCMP getCallForwardBlockProfileCMP(ProfileID profileID) 
		throws UnrecognizedProfileNameException, UnrecognizedProfileTableNameException;
	// Define Activity Context Interface narrow method
	public abstract ControllerActivityContextInterface asSbbActivityContextInterface( ActivityContextInterface aci);

	// Define Life cycle methods
	public void setSbbContext(SbbContext context) {
		super.setSbbContext(context);
		// also get the Profile Facility
		Context ctxt = null;
		try {
			ctxt = (Context)new InitialContext().lookup(JNDI_ENV_PATH);
			profileFacility = (ProfileFacility)ctxt.lookup(JNDI_PROFILE_FACILITY_NAME);
		} catch (NamingException ne) {
			log.warn("Problem when executing setSbbContext", ne);
		}

	}
	public void sbbCreate() throws javax.slee.CreateException {
		log.info("sbbCreate for ControllerSbb");
	}
	public void sbbActivate() {
		ActivityContextInterface controllerACI = acNamingFacility.lookup(CONTROLLER_ACTIVITY_CONTEXT_NAME);
		if (controllerACI == null) {
			log.info("Creating NullActivityContext");
			NullActivity nullActivity = nullActivityFactory.createNullActivity();
			ActivityContextInterface nullACI = null;
			try {
				nullACI = nullACIFactory.getActivityContextInterface(nullActivity);
			} catch (UnrecognizedActivityException uae) {
				log.error("Exception trying to get ACI for nullActivity: " + uae.getMessage());
			}
			try {
				acNamingFacility.bind(nullACI, CONTROLLER_ACTIVITY_CONTEXT_NAME);
			} catch (NameAlreadyBoundException nabe) {
				log.error("Name " + CONTROLLER_ACTIVITY_CONTEXT_NAME + " already bound: " + nabe.getMessage());
			}
		}
	}

	// Define Initial Event Selector method
	/**
	 * For initial events SLEE container calls this method to compute convergence name
	 * to help route the event to the appropriate SBB.
	 * @param ies InitialEventSelector
	 * @return InitialEventSelector with appropriate custom name set
	 */
    public InitialEventSelector callIDSelect( InitialEventSelector ies) {
    	log.info("Current convergence name = " + ies.getCustomName());
		Object event = ies.getEvent();
		String callId = null;
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
		} else {
			// If something else, use activity context.
			ies.setActivityContextSelected(true);
			return ies;
		}
		// Set the convergence name
		log.info("Setting convergence name to: " + callId);
		ies.setCustomName(callId);
		return ies;
    }

}
