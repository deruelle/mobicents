package org.mobicents.slee.sipevent.examples;

import java.io.IOException;
import java.io.StringWriter;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.slee.ActivityContextInterface;
import javax.slee.ChildRelation;
import javax.slee.RolledBackContext;
import javax.slee.SbbContext;
import javax.slee.facilities.TimerEvent;
import javax.slee.facilities.TimerFacility;
import javax.slee.facilities.TimerOptions;
import javax.slee.facilities.TimerPreserveMissed;
import javax.slee.nullactivity.NullActivity;
import javax.slee.nullactivity.NullActivityContextInterfaceFactory;
import javax.slee.nullactivity.NullActivityFactory;
import javax.xml.bind.JAXBContext;

import org.apache.log4j.Logger;
import org.mobicents.slee.sipevent.server.subscription.pojo.Subscription;
import org.mobicents.slee.sipevent.server.subscription.pojo.Subscription.Event;
import org.mobicents.slee.sipevent.server.subscription.pojo.Subscription.Status;
import org.mobicents.slee.sippresence.client.PresenceClientControlParentSbbLocalObject;
import org.mobicents.slee.sippresence.client.PresenceClientControlSbbLocalObject;
import org.mobicents.slee.xdm.server.XDMClientControlParentSbbLocalObject;
import org.mobicents.slee.xdm.server.XDMClientControlSbbLocalObject;
import org.openxdm.xcap.client.appusage.resourcelists.jaxb.EntryType;
import org.openxdm.xcap.client.appusage.resourcelists.jaxb.ListType;
import org.openxdm.xcap.client.appusage.resourcelists.jaxb.EntryType.DisplayName;
import org.openxdm.xcap.client.appusage.rlsservices.jaxb.ObjectFactory;
import org.openxdm.xcap.client.appusage.rlsservices.jaxb.PackagesType;
import org.openxdm.xcap.client.appusage.rlsservices.jaxb.RlsServices;
import org.openxdm.xcap.client.appusage.rlsservices.jaxb.ServiceType;
import org.openxdm.xcap.common.key.UserDocumentUriKey;
import org.openxdm.xcap.common.key.XcapUriKey;
import org.openxdm.xcap.server.slee.appusage.rlsservices.RLSServicesAppUsage;

/**
 * 
 * @author Eduardo Martins
 * 
 */
public abstract class RLSExampleSubscriberSbb implements javax.slee.Sbb,
	RLSExampleSubscriberSbbLocalObject {

	String presenceDomain = System.getProperty("bind.address","127.0.0.1");
	String subscriber = "sip:carol@"+presenceDomain;
	String notifier = "sip:mybuddies@"+presenceDomain;;
	String eventPackage = "presence";
	String contentType = "application";
	String contentSubType = "pidf+xml";
	int expires = 300;

	// --- PRESENCE CLIENT CHILD SBB

	public abstract ChildRelation getPresenceClientControlSbbChildRelation();

	public abstract PresenceClientControlSbbLocalObject getPresenceClientControlSbbCMP();

	public abstract void setPresenceClientControlSbbCMP(
			PresenceClientControlSbbLocalObject value);

	private PresenceClientControlSbbLocalObject getPresenceClientControlSbb() {
		PresenceClientControlSbbLocalObject childSbb = getPresenceClientControlSbbCMP();
		if (childSbb == null) {
			try {
			childSbb = (PresenceClientControlSbbLocalObject) getPresenceClientControlSbbChildRelation()
					.create();
			} catch (Exception e) {
				log4j.error("Failed to create child sbb", e);
				return null;
			}
			setPresenceClientControlSbbCMP(childSbb);
			childSbb
					.setParentSbb((PresenceClientControlParentSbbLocalObject) this.sbbContext
							.getSbbLocalObject());
		}
		return childSbb;
	}

	// --- XDM CLIENT CHILD SBB
	
	public abstract ChildRelation getXDMClientControlChildRelation();

	public abstract XDMClientControlSbbLocalObject getXDMClientControlChildSbbCMP();

	public abstract void setXDMClientControlChildSbbCMP(
			XDMClientControlSbbLocalObject value);

	public XDMClientControlSbbLocalObject getXDMClientControlSbb() {
		XDMClientControlSbbLocalObject childSbb = getXDMClientControlChildSbbCMP();
		if (childSbb == null) {
			try {
				childSbb = (XDMClientControlSbbLocalObject) getXDMClientControlChildRelation()
						.create();
			} catch (Exception e) {
				log4j.error("Failed to create child sbb", e);
				return null;
			}
			setXDMClientControlChildSbbCMP(childSbb);
			childSbb
					.setParentSbb((XDMClientControlParentSbbLocalObject) this.sbbContext
							.getSbbLocalObject());
		}
		return childSbb;
	}
	
	// --- CMPs

	public abstract void setParentSbbCMP(RLSExampleSubscriberParentSbbLocalObject value);

	public abstract RLSExampleSubscriberParentSbbLocalObject getParentSbbCMP();
	
	// --- SBB LOCAL OBJECT
	
	public void setParentSbb(RLSExampleSubscriberParentSbbLocalObject parentSbb) {
		setParentSbbCMP(parentSbb);
	}
	
	private EntryType createEntryType(String uri) {
		EntryType entryType = new EntryType();
		entryType.setUri(uri);
		DisplayName displayName = new EntryType.DisplayName();
		displayName.setValue(uri);
		entryType.setDisplayName(displayName);
		return entryType;
	}
	
	private String getRlsServices(String[] entryURIs) {
		StringWriter stringWriter = new StringWriter();
		try {			
			JAXBContext context = JAXBContext.newInstance("org.openxdm.xcap.client.appusage.rlsservices.jaxb");
			ListType listType = new ListType();
			for (String entryURI : entryURIs) {
				listType.getListOrExternalOrEntry().add(createEntryType(entryURI));
			}
			ServiceType serviceType = new ServiceType();
			serviceType.setList(listType);
			PackagesType packagesType = new PackagesType();
			packagesType.getPackageAndAny().add(new ObjectFactory().createPackagesTypePackage(eventPackage));
			serviceType.setPackages(packagesType);
			serviceType.setUri(notifier);
			RlsServices rlsServices = new RlsServices();
			rlsServices.getService().add(serviceType);
			context.createMarshaller().marshal(rlsServices, stringWriter);
			return stringWriter.toString();			
		} catch (Exception e) {
			log4j.error("failed to read rls-services.xml",e);
		}
		finally {		
			try {
				stringWriter.close();
			} catch (IOException e) {
				log4j.error(e.getMessage(),e);
			}
		}
		return null;
	}
		
	public void start(String[] entryURIs) {
		try {
			XDMClientControlSbbLocalObject xdm = getXDMClientControlSbb();
			// insert the document
			xdm.put(new UserDocumentUriKey(RLSServicesAppUsage.ID,subscriber,"index"), RLSServicesAppUsage.MIMETYPE, getRlsServices(entryURIs).getBytes("UTF-8"),null);			
		} catch (Exception e) {
			log4j.error(e.getMessage(), e);
			getParentSbbCMP().subscriberNotStarted();
		}
	}

	private String getSubscriptionId() {
		return "rls-example~"+subscriber+"~"+notifier+"~"+eventPackage;
	}
	
	public void putResponse(XcapUriKey key, int responseCode, String responseContent, String tag) {
		log4j.info("Response to the insertion of the rls services document: status="+responseCode+",content="+responseContent);
		if (responseCode != 200 && responseCode != 201) {			
			getParentSbbCMP().subscriberNotStarted();
		}
		else {			
			// now subscribe the presence of it
			getPresenceClientControlSbb().newSubscription(subscriber, "...", notifier, eventPackage, getSubscriptionId(), expires);			
		}
	}
		
	public void newSubscriptionOk(String subscriber, String notifier,
			String eventPackage, String subscriptionId, int expires,
			int responseCode) {
		
		log4j.info("subscribe ok: responseCode=" + responseCode + ",expires="
				+ expires);
		try {
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

			getParentSbbCMP().subscriberStarted();
		}
		catch (Exception e) {
			log4j.error(e.getMessage(),e);
		}
		
	}
	
	private void deleteRlsServices() {
		try {
			getXDMClientControlSbb().delete(new UserDocumentUriKey(RLSServicesAppUsage.ID,subscriber,"index"),null);			
		} catch (Exception e) {
			log4j.error(e.getMessage(), e);			
		}
	}
	
	public void newSubscriptionError(String subscriber, String notifier,
			String eventPackage, String subscriptionId, int error) {
		log4j.info("error on subscribe: error=" + error);
		deleteRlsServices();		
	}
	
	public void notifyEvent(String subscriber, String notifier,
			String eventPackage, String subscriptionId,
			Event terminationReason, Status status, String content,
			String contentType, String contentSubtype) {
		String notification = "\nNOTIFY EVENT:" + "\n+-- Subscriber: "
		+ subscriber + "\n+-- Notifier: " + notifier
		+ "\n+-- EventPackage: " + eventPackage
		+ "\n+-- SubscriptionId: " + subscriptionId				
		+ "\n+-- Subscription status: " + status
		+ "\n+-- Subscription terminationReason: " + terminationReason
		+ "\n+-- Content Type: " + contentType + '/' + contentSubtype
		+ "\n+-- Content:\n\n" + content;
		log4j.info(notification);
		if (status.equals(Subscription.Status.terminated) && terminationReason != null && terminationReason.equals(Subscription.Event.deactivated)) {
			log4j.info("The subscription was deactivated, re-subscribing");
			// re-subscribe
			getPresenceClientControlSbb().newSubscription(subscriber, "...", notifier, eventPackage, getSubscriptionId(), expires);
		}
	}
	
	public void onTimerEvent(TimerEvent event, ActivityContextInterface aci) {
		// refresh subscription
		getPresenceClientControlSbb().refreshSubscription(subscriber, notifier, eventPackage, getSubscriptionId(), expires);
	}

	public void refreshSubscriptionOk(String subscriber, String notifier,
			String eventPackage, String subscriptionId, int expires) {
		log4j.info("resubscribe Ok : expires=" + expires);

	}

	public void refreshSubscriptionError(String subscriber, String notifier,
			String eventPackage, String subscriptionId, int error) {
		log4j.info("error on resubscribe: error=" + error);
		deleteRlsServices();
	}

	public void stop() {
		getPresenceClientControlSbb().removeSubscription(subscriber, notifier, eventPackage, getSubscriptionId());
		deleteRlsServices();
	}
	
	public void removeSubscriptionError(String subscriber, String notifier,
			String eventPackage, String subscriptionId, int error) {
		log4j.info("error on unsubscribe: error=" + error);		
	}
	
	public void removeSubscriptionOk(String subscriber, String notifier,
			String eventPackage, String subscriptionId) {
		log4j.info("unsubscribe Ok");		
	}
	
	public void deleteResponse(XcapUriKey key, int responseCode, String responseContent, String tag) {
		getParentSbbCMP().subscriberStopped();		
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
			.getLogger(RLSExampleSubscriberSbb.class);

}