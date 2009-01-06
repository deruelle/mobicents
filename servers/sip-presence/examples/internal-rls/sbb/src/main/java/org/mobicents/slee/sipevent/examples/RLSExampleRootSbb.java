package org.mobicents.slee.sipevent.examples;

import java.util.Iterator;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.slee.ActivityContextInterface;
import javax.slee.ActivityEndEvent;
import javax.slee.ChildRelation;
import javax.slee.RolledBackContext;
import javax.slee.SbbContext;
import javax.slee.serviceactivity.ServiceActivityFactory;

import org.apache.log4j.Logger;
import org.mobicents.slee.sipevent.server.subscription.SubscriptionClientControlParentSbbLocalObject;
import org.mobicents.slee.sipevent.server.subscription.SubscriptionClientControlSbbLocalObject;

/**
 * Example of an application that uses
 * {@link SubscriptionClientControlSbbLocalObject} as a child sbb, and
 * implements {@link SubscriptionClientControlParentSbbLocalObject}, to
 * interact with the Mobicents SIP Event Subscription service.
 * 
 * @author Eduardo Martins
 * 
 */
public abstract class RLSExampleRootSbb implements javax.slee.Sbb,
		RLSExampleRootSbbLocalObject {

	String presenceDomain = System.getProperty("bind.address","127.0.0.1");
	String[] publishers = {"sip:alice@"+presenceDomain,"sip:bob@"+presenceDomain};
	
	/*
	 * service activation event, create subscription
	 */
	public void onServiceStartedEvent(
			javax.slee.serviceactivity.ServiceStartedEvent event,
			ActivityContextInterface aci) {

		// check if it's my service that is starting
		if (serviceActivityFactory.getActivity().equals(aci.getActivity())) {
			log4j.info("Service activated...");
			try {
				RLSExamplePublisherSbbLocalObject child  = (RLSExamplePublisherSbbLocalObject) getPublisherChildRelation().create();
				child.setParentSbb((RLSExamplePublisherParentSbbLocalObject)this.sbbContext.getSbbLocalObject());
				child.start(publishers[0]);
			} catch (Exception e) {
				log4j.error(e.getMessage(),e);
			}
		} else {
			// another service activated, we don't want to receive further
			// events on this activity
			aci.detach(sbbContext.getSbbLocalObject());
		}
	}

	public void publisherNotStarted(String publisher) {
		log4j.info("publisher didn't started "+publisher);		
	}
	
	public void publisherStarted(String publisher) {
		log4j.info("publisher started "+publisher);
		if (publisher.equals(publishers[0])) {
			// start the other publisher
			try {
				RLSExamplePublisherSbbLocalObject child  = (RLSExamplePublisherSbbLocalObject) getPublisherChildRelation().create();
				child.setParentSbb((RLSExamplePublisherParentSbbLocalObject)this.sbbContext.getSbbLocalObject());
				child.start(publishers[1]);
			} catch (Exception e) {
				log4j.error(e.getMessage(),e);
			}
		}	
		else {
			try {
				RLSExampleSubscriberSbbLocalObject child  = (RLSExampleSubscriberSbbLocalObject) getSubscriberChildRelation().create();
				child.setParentSbb((RLSExampleSubscriberParentSbbLocalObject)this.sbbContext.getSbbLocalObject());
				child.start(publishers);				
			} catch (Exception e) {
				log4j.error(e.getMessage(),e);
			}
		}
	}
	
	public void subscriberNotStarted() {
		log4j.info("subscriber didn't started ");			
	}
	
	public void subscriberStarted() {
		log4j.info("subscriber started");	
		
	}
	
	public void subscriberStopped() {
		log4j.info("subscriber stopped");		
	}
		
	
	/**
	 * service deactivation, unsubscribe
	 * 
	 * @param event
	 * @param aci
	 */
	public void onActivityEndEvent(ActivityEndEvent event,
			ActivityContextInterface aci) {
		
		log4j.info("Service deactivated...");
		try {
			try {
				for (Iterator it = getPublisherChildRelation().iterator(); it.hasNext(); ) {
					((RLSExamplePublisherSbbLocalObject)it.next()).stop();
				}
				for (Iterator it = getSubscriberChildRelation().iterator(); it.hasNext(); ) {
					((RLSExampleSubscriberSbbLocalObject)it.next()).stop();
				}				
			} catch (Exception e) {
				log4j.error(e.getMessage(),e);
			}
		} catch (Exception e) {
			log4j.error(e);
		}

	}

	// --- CHILDS
	
	public abstract ChildRelation getPublisherChildRelation();
	
	public abstract ChildRelation getSubscriberChildRelation();
	
	// --- SBB OBJECT

	private SbbContext sbbContext = null; // This SBB's context

	private ServiceActivityFactory serviceActivityFactory = null;
	
	/**
	 * Called when an sbb object is instantied and enters the pooled state.
	 */
	public void setSbbContext(SbbContext sbbContext) {

		this.sbbContext = sbbContext;
		try {
			Context context = (Context) new InitialContext()
					.lookup("java:comp/env");
			serviceActivityFactory = (ServiceActivityFactory) context
					.lookup("slee/serviceactivity/factory");
		} catch (Exception e) {
			log4j.error("Unable to retrieve factories, facilities & providers",
					e);
		}
	}

	public void unsetSbbContext() {
		log4j.info("unsetSbbContext()");
		this.sbbContext = null;
	}

	public void sbbCreate() throws javax.slee.CreateException {
	}

	public void sbbPostCreate() throws javax.slee.CreateException {
	}

	public void sbbActivate() {
	}

	public void sbbPassivate() {
	}

	public void sbbRemove() {
	}

	public void sbbLoad() {
	}

	public void sbbStore() {
	}

	public void sbbExceptionThrown(Exception exception, Object event,
			ActivityContextInterface activity) {
	}

	public void sbbRolledBack(RolledBackContext sbbRolledBack) {
	}

	private static Logger log4j = Logger
			.getLogger(RLSExampleRootSbb.class);

}