package org.mobicents.slee.sipevent.examples;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.slee.ActivityContextInterface;
import javax.slee.ChildRelation;
import javax.slee.CreateException;
import javax.slee.RolledBackContext;
import javax.slee.SLEEException;
import javax.slee.SbbContext;
import javax.slee.TransactionRequiredLocalException;
import javax.slee.facilities.TimerEvent;
import javax.slee.facilities.TimerFacility;
import javax.slee.facilities.TimerOptions;
import javax.slee.facilities.TimerPreserveMissed;
import javax.slee.nullactivity.NullActivity;
import javax.slee.nullactivity.NullActivityContextInterfaceFactory;
import javax.slee.nullactivity.NullActivityFactory;

import org.apache.log4j.Logger;
import org.mobicents.slee.sippresence.client.PresenceClientControlParentSbbLocalObject;
import org.mobicents.slee.sippresence.client.PresenceClientControlSbbLocalObject;

/**
 * 
 * @author Eduardo Martins
 * 
 */
public abstract class RLSExamplePublisherSbb implements javax.slee.Sbb,
	RLSExamplePublisherSbbLocalObject {

	String eventPackage = "presence";
	String contentType = "application";
	String contentSubType = "pidf+xml";
	int expires = 300;

	private String getDocument(String publisher) {
		return  
		"<?xml version='1.0' encoding='UTF-8'?>" +
		"<presence xmlns='urn:ietf:params:xml:ns:pidf' xmlns:dm='urn:ietf:params:xml:ns:pidf:data-model' xmlns:rpid='urn:ietf:params:xml:ns:pidf:rpid' xmlns:c='urn:ietf:params:xml:ns:pidf:cipid' entity='"+publisher+"'>" +
			"<tuple id='t54bb0569'><status><basic>open</basic></status></tuple>" +
			"<dm:person id='p65f3307a'>" +
				"<rpid:activities><rpid:busy/></rpid:activities>" +
				"<dm:note>Busy</dm:note>" +
			"</dm:person>" +
		"</presence>";
	}
	
	// --- INTERNAL CHILD SBB

	public abstract ChildRelation getPresenceClientControlSbbChildRelation();

	public abstract PresenceClientControlSbbLocalObject getPresenceClientControlSbbCMP();

	public abstract void setPresenceClientControlSbbCMP(
			PresenceClientControlSbbLocalObject value);

	private PresenceClientControlSbbLocalObject getPresenceClientControlSbb()
			throws TransactionRequiredLocalException, SLEEException,
			CreateException {
		PresenceClientControlSbbLocalObject childSbb = getPresenceClientControlSbbCMP();
		if (childSbb == null) {
			childSbb = (PresenceClientControlSbbLocalObject) getPresenceClientControlSbbChildRelation()
					.create();
			setPresenceClientControlSbbCMP(childSbb);
			childSbb
					.setParentSbb((PresenceClientControlParentSbbLocalObject) this.sbbContext
							.getSbbLocalObject());
		}
		return childSbb;
	}

	// --- CMPs

	public abstract void setParentSbbCMP(RLSExamplePublisherParentSbbLocalObject value);

	public abstract RLSExamplePublisherParentSbbLocalObject getParentSbbCMP();
	
	public abstract void setPublisher(String value);

	public abstract String getPublisher();
	
	public abstract void setETag(String eTag);

	public abstract String getETag();

	// --- SBB LOCAL OBJECT
	
	public void setParentSbb(RLSExamplePublisherParentSbbLocalObject parentSbb) {
		setParentSbbCMP(parentSbb);
	}
	
	public void start(String publisher) {

		try {
			setPublisher(publisher);
			// subscribe
			getPresenceClientControlSbb().newPublication("requestId",
					publisher, getDocument(publisher), contentType,
					contentSubType, expires);
		} catch (Exception e) {
			log4j.error(e.getMessage(), e);
			getParentSbbCMP().publisherNotStarted(publisher);
		}
	}

	public void newPublicationError(Object requestId, int error) {
		log4j.info("error on mew publication: requestId=" + requestId
				+ ",error=" + error);	
		
		getParentSbbCMP().publisherNotStarted(getPublisher());
	}
	
	public void newPublicationOk(Object requestId, String tag, int expires)
			throws Exception {
		log4j.info("publication ok: eTag=" + tag);
		// save etag in cmp
		setETag(tag);
		// let's set a periodic timer in a null activity to refresh the
		// publication
		TimerOptions timerOptions = new TimerOptions();
		timerOptions.setPersistent(true);
		timerOptions.setPreserveMissed(TimerPreserveMissed.ALL);
		
		NullActivity nullActivity = nullActivityFactory.createNullActivity();
		ActivityContextInterface aci = nullACIFactory.getActivityContextInterface(nullActivity);
		aci.attach(this.sbbContext.getSbbLocalObject());
		timerFacility.setTimer(aci, null, System.currentTimeMillis() + (expires-1)
				* 1000, (expires-1) * 1000, 0, timerOptions);
		
		getParentSbbCMP().publisherStarted(getPublisher());
		
	}
	
	public void onTimerEvent(TimerEvent event, ActivityContextInterface aci) {
		// refresh publication
		String publisher = getPublisher();
		try {
			getPresenceClientControlSbb().refreshPublication(
					"requestId", publisher, getETag(),
					expires);
		} catch (Exception e) {
			log4j.error(e.getMessage(),e);		
		}
	}

	public void refreshPublicationOk(Object requestId, String tag, int expires)
			throws Exception {
		log4j.info("refreshed publication ok: requestId=" + requestId
				+ ",eTag=" + tag + ",expires=" + expires);
		// update tag in cmp, it changes on refreshes too
		setETag(tag);
	}

	public void refreshPublicationError(Object requestId, int error) {
		log4j.info("erro when refreshing publication: requestId=" + requestId
				+ ",error=" + error);
	}

	/**
	 * stop publishing
	 */
	public void stop() {
		try {
			getPresenceClientControlSbb().removePublication("requestId", getPublisher(), getETag());
		} catch (Exception e) {
			log4j.error(e.getMessage(),e);			
		}
	}
	
	public void removePublicationError(Object requestId, int error) {
		log4j.info("error wehn removing publication: requestId=" + requestId
				+ ",error=" + error);
	}
	
	public void removePublicationOk(Object requestId) throws Exception {
		log4j.info("publication removed!");		
	}
	
	// --- SBB OBJECT

	private SbbContext sbbContext = null; // This SBB's context

	private TimerFacility timerFacility = null;
	private NullActivityContextInterfaceFactory nullACIFactory;
	private NullActivityFactory nullActivityFactory;

	/**
	 * Called when an sbb object is instantied and enters the pooled state.
	 */
	public void setSbbContext(SbbContext sbbContext) {

		this.sbbContext = sbbContext;
		try {
			Context context = (Context) new InitialContext()
					.lookup("java:comp/env");
			timerFacility = (TimerFacility) context
				.lookup("slee/facilities/timer");
			nullACIFactory = (NullActivityContextInterfaceFactory) context
				.lookup("slee/nullactivity/activitycontextinterfacefactory");
			nullActivityFactory = (NullActivityFactory) context
				.lookup("slee/nullactivity/factory");
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
			.getLogger(RLSExamplePublisherSbb.class);

}