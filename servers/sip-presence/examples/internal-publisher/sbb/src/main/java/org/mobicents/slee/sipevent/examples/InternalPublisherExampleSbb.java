package org.mobicents.slee.sipevent.examples;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.slee.ActivityContextInterface;
import javax.slee.ActivityEndEvent;
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
import javax.slee.serviceactivity.ServiceActivity;
import javax.slee.serviceactivity.ServiceActivityContextInterfaceFactory;
import javax.slee.serviceactivity.ServiceActivityFactory;

import org.apache.log4j.Logger;
import org.mobicents.slee.sipevent.server.publication.PublicationClientControlParentSbbLocalObject;
import org.mobicents.slee.sipevent.server.publication.PublicationClientControlSbbLocalObject;

/**
 * Example of an application that uses
 * {@link PublicationClientControlSbbLocalObject} as a child sbb, and implements
 * {@link PublicationClientControlParentSbbLocalObject}, to interact with the
 * Mobicents SIP Event Publication service.
 * 
 * @author Eduardo Martins
 * 
 */
public abstract class InternalPublisherExampleSbb implements javax.slee.Sbb,
		PublicationClientControlParentSbbLocalObject {

	String presenceDomain = System.getProperty("bind.address","127.0.0.1");
	String entity = "sip:internal-publisher@" + presenceDomain;
	String eventPackage = "presence";
	String contentType = "application";
	String contentSubType = "pidf+xml";
	String document = 
		"<?xml version='1.0' encoding='UTF-8'?>" +
		"<presence xmlns='urn:ietf:params:xml:ns:pidf' xmlns:dm='urn:ietf:params:xml:ns:pidf:data-model' xmlns:rpid='urn:ietf:params:xml:ns:pidf:rpid' xmlns:c='urn:ietf:params:xml:ns:pidf:cipid' entity='sip:internal-publisher@"+presenceDomain+"'>" +
			"<tuple id='t54bb0569'><status><basic>open</basic></status></tuple>" +
			"<dm:person id='p65f3307a'>" +
				"<rpid:activities><rpid:busy/></rpid:activities>" +
				"<dm:note>Busy</dm:note>" +
			"</dm:person>" +
		"</presence>";
	int expires = 60;

	// --- INTERNAL CHILD SBB

	public abstract ChildRelation getPublicationControlChildRelation();

	public abstract PublicationClientControlSbbLocalObject getPublicationControlChildSbbCMP();

	public abstract void setPublicationControlChildSbbCMP(
			PublicationClientControlSbbLocalObject value);

	private PublicationClientControlSbbLocalObject getPublicationControlChildSbb()
			throws TransactionRequiredLocalException, SLEEException,
			CreateException {
		PublicationClientControlSbbLocalObject childSbb = getPublicationControlChildSbbCMP();
		if (childSbb == null) {
			childSbb = (PublicationClientControlSbbLocalObject) getPublicationControlChildRelation()
					.create();
			setPublicationControlChildSbbCMP(childSbb);
			childSbb
					.setParentSbb((PublicationClientControlParentSbbLocalObject) this.sbbContext
							.getSbbLocalObject());
		}
		return childSbb;
	}

	// --- ETAG CMP

	public abstract void setETag(String eTag);

	public abstract String getETag();

	/*
	 * service activation event, publish initial state
	 */
	public void onServiceStartedEvent(
			javax.slee.serviceactivity.ServiceStartedEvent event,
			ActivityContextInterface aci) {

		// check if it's my service that is starting
		if (serviceActivityFactory.getActivity().equals(aci.getActivity())) {
			log4j.info("Service activated, publishing state...");
			try {
				getPublicationControlChildSbb().newPublication(
						entity + eventPackage, entity, eventPackage, document,
						contentType, contentSubType, expires);
			} catch (Exception e) {
				log4j.error(e);
			}
		} else {
			// another service activated, we don't want to receive further
			// events on this activity
			aci.detach(sbbContext.getSbbLocalObject());
		}
	}

	public void newPublicationOk(Object requestId, String tag, int expires)
			throws Exception {
		log4j.info("publication ok: eTag=" + tag);
		// save etag in cmp
		setETag(tag);
		// let's set a periodic timer in the service activity, that originated
		// this sbb entity (onServiceStartedEvent()...), to refresh the
		// publication
		TimerOptions timerOptions = new TimerOptions();
		timerOptions.setPersistent(true);
		timerOptions.setPreserveMissed(TimerPreserveMissed.ALL);
		ServiceActivity serviceActivity = serviceActivityFactory.getActivity();
		ActivityContextInterface aci = null;
		try {
			aci = serviceActivityContextInterfaceFactory
					.getActivityContextInterface(serviceActivity);
		} catch (Exception e) {
			log4j.error("Failed to retreive service activity aci", e);
			try {
				getPublicationControlChildSbb().refreshPublication(requestId,
						entity, eventPackage, tag, expires);
			} catch (Exception f) {
				log4j.error("Dude, now I can't get the child sbb!!", f);
			}
			return;
		}
		timerFacility.setTimer(aci, null, System.currentTimeMillis() + expires
				* 1000, expires * 1000, 0, timerOptions);
	}

	public void newPublicationError(Object requestId, int error) {
		log4j.info("error on mew publication: requestId=" + requestId
				+ ",error=" + error);
	}

	public void onTimerEvent(TimerEvent event, ActivityContextInterface aci) {
		// refresh publication
		try {
			getPublicationControlChildSbb().refreshPublication(
					entity + eventPackage, entity, eventPackage, getETag(),
					expires);
		} catch (Exception e) {
			log4j.error(e);
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
	 * service deactivation, remove published state
	 * 
	 * @param event
	 * @param aci
	 */
	public void onActivityEndEvent(ActivityEndEvent event,
			ActivityContextInterface aci) {
		if (getETag() != null) {
			log4j.info("Service deactivated, removing publication...");
			try {
				getPublicationControlChildSbb().removePublication(
						entity + eventPackage, entity, eventPackage, getETag());
			} catch (Exception e) {
				log4j.error(e);
			}
		} else {
			log4j.info("Service deactivated, no published state to remove.");
		}
	}

	public void removePublicationOk(Object requestId) throws Exception {
		log4j.info("publication removed!");
	}

	public void removePublicationError(Object requestId, int error) {
		log4j.info("error wehn removing publication: requestId=" + requestId
				+ ",error=" + error);
	}

	// --- OTHER CALLBACKS FROM PUBLICATION CONTROL, WHICH ARE NOT USED

	public void modifyPublicationOk(Object requestId, String tag, int expires)
			throws Exception {
		log4j.info("publication modification ok");
	}

	public void modifyPublicationError(Object requestId, int error) {
		log4j.info("error when modifying publication: requestId=" + requestId
				+ ",error=" + error);
	}

	// --- SBB OBJECT LIFECYCLE

	private SbbContext sbbContext = null; // This SBB's context

	private TimerFacility timerFacility = null;
	private ServiceActivityFactory serviceActivityFactory = null;
	private ServiceActivityContextInterfaceFactory serviceActivityContextInterfaceFactory = null;

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
			serviceActivityFactory = (ServiceActivityFactory) context
					.lookup("slee/serviceactivity/factory");
			serviceActivityContextInterfaceFactory = (ServiceActivityContextInterfaceFactory) context
					.lookup("slee/serviceactivity/activitycontextinterfacefactory");
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
			.getLogger(InternalPublisherExampleSbb.class);

}