package org.mobicents.slee.sipevent.examples;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.slee.ActivityContextInterface;
import javax.slee.ActivityEndEvent;
import javax.slee.ChildRelation;
import javax.slee.CreateException;
import javax.slee.RolledBackContext;
import javax.slee.SLEEException;
import javax.slee.SbbContext;
import javax.slee.TransactionRequiredLocalException;
import javax.slee.facilities.TimerEvent;
import javax.slee.serviceactivity.ServiceActivity;
import javax.slee.serviceactivity.ServiceActivityFactory;

import org.apache.log4j.Logger;
import org.mobicents.slee.sipevent.server.publication.PublicationClientControlParentSbbLocalObject;
import org.mobicents.slee.sipevent.server.publication.PublicationClientControlSbbLocalObject;

public abstract class InternalPublisherExampleSbb implements javax.slee.Sbb, PublicationClientControlParentSbbLocalObject {

	String entity = "sip:internal-publisher@127.0.0.1";
	String eventPackage = "presence";
	String contentType = "application";
	String contentSubType = "pidf+xml";
	String document = "<?xml version='1.0' encoding='UTF-8'?><presence xmlns='urn:ietf:params:xml:ns:pidf' xmlns:dm='urn:ietf:params:xml:ns:pidf:data-model' xmlns:rpid='urn:ietf:params:xml:ns:pidf:rpid' xmlns:c='urn:ietf:params:xml:ns:pidf:cipid' entity='sip:internal-publisher@127.0.0.1'><tuple id='t54bb0569'><status><basic>open</basic></status></tuple><dm:person id='p65f3307a'><rpid:activities><rpid:busy/></rpid:activities><dm:note>Busy</dm:note></dm:person></presence>"; 
	int expires = 3600;
	
	// --- INTERNAL CHILD SBB
	
	public abstract ChildRelation getPublicationControlChildRelation();
	public abstract PublicationClientControlSbbLocalObject getPublicationControlChildSbbCMP();
	public abstract void setPublicationControlChildSbbCMP(PublicationClientControlSbbLocalObject value);
	private PublicationClientControlSbbLocalObject getPublicationControlChildSbb() throws TransactionRequiredLocalException, SLEEException, CreateException {
		PublicationClientControlSbbLocalObject childSbb = getPublicationControlChildSbbCMP();
		if (childSbb == null) {
			childSbb = (PublicationClientControlSbbLocalObject) getPublicationControlChildRelation().create();
			setPublicationControlChildSbbCMP(childSbb);
			childSbb.setParentSbb((PublicationClientControlParentSbbLocalObject)this.sbbContext.getSbbLocalObject());
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
		try {
			//check if it's my service that is starting
			ServiceActivity sa = ((ServiceActivityFactory) new InitialContext().lookup("java:comp/env/slee/serviceactivity/factory")).getActivity();
			if (sa.equals(aci.getActivity())) {
				log4j.info("Service activated, publishing state...");
				try {
					getPublicationControlChildSbb().newPublication(entity+eventPackage, entity, eventPackage, document, contentType, contentSubType, expires);
				} catch (Exception e) {
					log4j.error(e);
				}				
			} else {
				log4j.info("Another service activated...");
				// we don't want to receive further events on this activity
				aci.detach(sbbContext.getSbbLocalObject());
			}
		} catch (NamingException e) {
			log4j.error("Can't handle service started event.", e);
		}
	}

	public void newPublicationOk(Object requestId, String tag, int expires)
			throws Exception {
		log4j.info("publication ok");
		setETag(tag);
		// TODO set a timer for refresh
	}
	
	public void onTimerEvent(TimerEvent event, ActivityContextInterface aci) {
		// modify publication
		// TODO add other state
		try {
			getPublicationControlChildSbb().modifyPublication(entity+eventPackage, entity, eventPackage, getETag(), document, contentType, contentSubType, expires);
		} catch (Exception e) {
			log4j.error(e);
		}
	}
	
	public void modifyPublicationOk(Object requestId, String tag, int expires)
			throws Exception {
		log4j.info("publication modification ok");
		// TODO set a timer for publication removal?
	}
	
	/**
	 * service deactivation, remove published state
	 * @param event
	 * @param aci
	 */
	public void onActivityEndEvent(ActivityEndEvent event, ActivityContextInterface aci) {			
		if (getETag() != null) {
			log4j.info("Service deactivated, removing publication...");
			try {
				getPublicationControlChildSbb().removePublication(entity+eventPackage, entity, eventPackage, getETag());
			} catch (Exception e) {
				log4j.error(e);
			}			
		}
		else {
			log4j.info("Service deactivated, no published state to remove.");	
		}
	}
		
	// --- OTHER CALLBACKS FROM PUBLICATION CONTROL, WHICH ARE NOT USED
	
	public void newPublicationError(Object requestId, int error) {
		log4j.info("error on mew publication: requestId="+requestId+",error="+error);	
	}
	
	public void removePublicationOk(Object requestId) throws Exception {
		log4j.info("publication removed!");
	}
	
	public void removePublicationError(Object requestId, int error) {
		log4j.info("error wehn removing publication: requestId="+requestId+",error="+error);
	}
	
	public void modifyPublicationError(Object requestId, int error) {
		log4j.info("error when modifying publication: requestId="+requestId+",error="+error);
	}
	
	public void refreshPublicationError(Object requestId, int error) {
		log4j.info("erro when refreshing publication: requestId="+requestId+",error="+error);
	}
	
	public void refreshPublicationOk(Object requestId, String tag, int expires)
			throws Exception {
		log4j.info("refreshed publication: requestId="+requestId+",eTag="+tag+",expires="+expires);
	}
	
	// --- SBB OBJECT LIFECYCLE
	
	private SbbContext sbbContext = null; // This SBB's context			

	/**
	 * Called when an sbb object is instantied and enters the pooled state.
	 */
	public void setSbbContext(SbbContext context) {
		log4j.info("setSbbContext(context=" + context.toString() + ")");
		this.sbbContext = context;
	}

	public void unsetSbbContext() {
		log4j.info("unsetSbbContext()");
		this.sbbContext = null;
	}

	public void sbbCreate() throws javax.slee.CreateException {}
	public void sbbPostCreate() throws javax.slee.CreateException {}
	public void sbbActivate() {}
	public void sbbPassivate() {}
	public void sbbRemove() {}
	public void sbbLoad() {}
	public void sbbStore() {}
	public void sbbExceptionThrown(Exception exception, Object event,
			ActivityContextInterface activity) {}
	public void sbbRolledBack(RolledBackContext sbbRolledBack) {}
	
	private static Logger log4j = Logger.getLogger(InternalPublisherExampleSbb.class);

}