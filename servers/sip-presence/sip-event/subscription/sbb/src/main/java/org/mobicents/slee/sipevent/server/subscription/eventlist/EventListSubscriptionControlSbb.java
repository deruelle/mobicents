package org.mobicents.slee.sipevent.server.subscription.eventlist;

import java.io.StringReader;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import javax.sip.RequestEvent;
import javax.sip.header.AcceptHeader;
import javax.sip.header.SupportedHeader;
import javax.sip.message.Response;
import javax.slee.ActivityContextInterface;
import javax.slee.ChildRelation;
import javax.slee.CreateException;
import javax.slee.RolledBackContext;
import javax.slee.Sbb;
import javax.slee.SbbContext;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;
import org.mobicents.slee.sipevent.server.subscription.EventListSubscriberParentSbbLocalObject;
import org.mobicents.slee.sipevent.server.subscription.EventListSubscriberSbbLocalObject;
import org.mobicents.slee.sipevent.server.subscription.EventListSubscriptionControlParentSbbLocalObject;
import org.mobicents.slee.sipevent.server.subscription.EventListSubscriptionControlSbbLocalObject;
import org.mobicents.slee.sipevent.server.subscription.FlatListMakerParentSbbLocalObject;
import org.mobicents.slee.sipevent.server.subscription.FlatListMakerSbbLocalObject;
import org.mobicents.slee.sipevent.server.subscription.pojo.Subscription;
import org.mobicents.slee.sipevent.server.subscription.pojo.SubscriptionKey;
import org.mobicents.slee.xdm.server.XDMClientControlParentSbbLocalObject;
import org.mobicents.slee.xdm.server.XDMClientControlSbbLocalObject;
import org.openxdm.xcap.client.appusage.rlsservices.jaxb.RlsServices;
import org.openxdm.xcap.client.appusage.rlsservices.jaxb.ServiceType;
import org.openxdm.xcap.common.key.GlobalDocumentUriKey;
import org.openxdm.xcap.common.key.XcapUriKey;
import org.openxdm.xcap.common.uri.AttributeSelector;
import org.openxdm.xcap.common.uri.DocumentSelector;
import org.openxdm.xcap.common.uri.NodeSelector;

/**
 * 
 * 
 * @author Eduardo Martins
 * 
 */
public abstract class EventListSubscriptionControlSbb implements Sbb,
		EventListSubscriptionControlSbbLocalObject {

	private static final Logger logger = Logger
			.getLogger(EventListSubscriptionControlSbb.class);

	/**
	 * caching rls-services of xdm
	 */
	private static final RlsServicesCache rlsServicesCache = new RlsServicesCache();

	/**
	 * key that points to the global index document of rls-services in the xdm
	 * server
	 */
	private static final GlobalDocumentUriKey globalRLSDocumentKey = new GlobalDocumentUriKey(
			"rls-services", "index");

	/**
	 * can verify if a service type object has a certain event package
	 */
	private static final ServiceTypePackageVerifier serviceTypePackageVerifier = new ServiceTypePackageVerifier();
	
	// --- parent sbb

	public abstract void setParentSbbCMP(
			EventListSubscriptionControlParentSbbLocalObject sbbLocalObject);

	public abstract EventListSubscriptionControlParentSbbLocalObject getParentSbbCMP();

	public void setParentSbb(
			EventListSubscriptionControlParentSbbLocalObject sbbLocalObject) {
		setParentSbbCMP(sbbLocalObject);
	}

	// --- rls management of cached rls-services

	public void initRLSCache() {
		if (logger.isInfoEnabled()) {
			logger
					.info("Mobicents Resource List Server: starting cache of XDM's global rls-services document.");
		}
		XDMClientControlSbbLocalObject xdm = getXDMClientControlSbb();
		// ask for the global document
		xdm.get(globalRLSDocumentKey, null);
		// subscribe to get notifications on updates
		xdm.subscribeDocument(globalRLSDocumentKey.getDocumentSelector());
	}

	public void shutdownRLSCache() {
		XDMClientControlSbbLocalObject xdmClientSbb = getXDMClientControlSbb();
		for(String serviceURI : rlsServicesCache.getFlatListServiceURIs()) {
			rlsServicesCache.removeFlatList(serviceURI, xdmClientSbb);
		} 
		xdmClientSbb.unsubscribeDocument(globalRLSDocumentKey.getDocumentSelector());
	}

	private void updateCache(String document) {
		if (logger.isDebugEnabled()) {
			logger.debug("Mobicents Resource List Server: updating cache...");
		}
		Set<String> servicesToRemove = new HashSet<String>(rlsServicesCache.getFlatListServiceURIs());
		StringReader stringReader = new StringReader(document);
		try {
			Unmarshaller unmarshaller = context.createUnmarshaller();
			Object obj = unmarshaller.unmarshal(stringReader);
			if (obj instanceof RlsServices) {
				for (ServiceType serviceType : ((RlsServices) obj).getService()) {
					servicesToRemove.remove(serviceType.getUri());
					getFlatListMakerSbb().makeFlatList(serviceType);
				}
			}
		} catch (Exception e) {
			logger.error("failed to unmarshall rls services content", e);
		} finally {
			stringReader.close();
		}
		if (!servicesToRemove.isEmpty()) {
			XDMClientControlSbbLocalObject xdmClientSbb = getXDMClientControlSbb();
			for (String serviceToRemove : servicesToRemove) {
				rlsServicesCache.removeFlatList(serviceToRemove, xdmClientSbb);
				getParentSbbCMP().rlsServiceRemoved(serviceToRemove);
			}
		}
	}

	public void flatListMade(FlatList flatList) {

		XDMClientControlSbbLocalObject xdmClientSbb = getXDMClientControlSbb();
		
		if (rlsServicesCache.putFlatList(flatList,xdmClientSbb)) {
			// warn the parent sbb, perhaps there are subscriptions for this flat
			// list uri that are
			// not set for a rls service, this can happen when a new list is
			// created and a subscription arrives before the rlscache is updated
			getParentSbbCMP().rlsServiceUpdated(flatList.getServiceType().getUri());
		}
		if (logger.isInfoEnabled()) {
			logger.info("Mobicents Resource List Server: updated cache with "
					+ flatList);
		}
	}

	public void getResponse(XcapUriKey key, int responseCode, String mimetype,
			String content, String tag) {
		if (logger.isDebugEnabled()) {
			logger
					.debug("Got "
							+ responseCode
							+ " response for retreival of global rls services document.");
		}
		if (responseCode == 200) {
			// the global doc of rls-services in xdm was updated, update our
			// cache
			updateCache(content);
		}
	}

	public void documentUpdated(DocumentSelector documentSelector,
			String oldETag, String newETag, String documentAsString) {
		if (documentSelector.getAUID().equals("resource-lists")) {
			for (FlatList flatList : rlsServicesCache.getFlatListsLinkedToResourceList(documentSelector)) {
				// rebuild flat list
				// FIXME flat list should instead just be updated
				getFlatListMakerSbb().makeFlatList(flatList.getServiceType());
			}
		}
		else {
			// rls-services global doc
			updateCache(documentAsString);
		}
	}

	public void attributeUpdated(DocumentSelector documentSelector,
			NodeSelector nodeSelector, AttributeSelector attributeSelector,
			Map<String, String> namespaces, String oldETag, String newETag,
			String documentAsString, String attributeValue) {
		// FIXME for now redirect to document updated
		documentUpdated(documentSelector, oldETag, newETag, documentAsString);
	}

	public void elementUpdated(DocumentSelector documentSelector,
			NodeSelector nodeSelector, Map<String, String> namespaces,
			String oldETag, String newETag, String documentAsString,
			String elementAsString) {
		// FIXME for now redirect to document updated
		documentUpdated(documentSelector, oldETag, newETag, documentAsString);
	}

	// --- rls logic

	public int validateSubscribeRequest(String subscriber, String notifier,
			String eventPackage, RequestEvent event) {

		FlatList flatList = rlsServicesCache.getFlatList(notifier);

		if (flatList != null) {

			if (logger.isDebugEnabled()) {
				logger.debug(notifier + " is a resource list.");
			}

			if (event != null) {
				// check event list support is present in UA
				boolean isEventListSupported = false;
				for (ListIterator<?> lit = event.getRequest().getHeaders(
						SupportedHeader.NAME); lit.hasNext();) {
					SupportedHeader sh = (SupportedHeader) lit.next();
					if (sh.getOptionTag().equals("eventlist")) {
						isEventListSupported = true;
						break;
					}
				}
				if (!isEventListSupported) {
					if (logger.isInfoEnabled()) {
						logger
								.info("SIP subscription request for resource list doesn't included Supported: eventlist header");
					}
					return Response.EXTENSION_REQUIRED;
				}

				boolean isMultipartAccepted = false;
				boolean isRlmiAccepted = false;
				for (ListIterator<?> lit = event.getRequest().getHeaders(
						AcceptHeader.NAME); lit.hasNext();) {
					AcceptHeader ah = (AcceptHeader) lit.next();
					if (ah.allowsAllContentTypes()
							&& ah.allowsAllContentSubTypes()) {
						isMultipartAccepted = true;
						isRlmiAccepted = true;
						break;
					}
					if (!isMultipartAccepted
							&& ah.getContentSubType().equals("related")
							&& ah.getContentType().equals("multipart")) {
						isMultipartAccepted = true;
					}
					if (!isRlmiAccepted
							&& ah.getContentSubType().equals("rlmi+xml")
							&& ah.getContentType().equals("application")) {
						isRlmiAccepted = true;
					}
				}
				if (!isMultipartAccepted || !isRlmiAccepted) {
					if (logger.isInfoEnabled()) {
						logger
								.info("SIP subscription request for resource list doesn't included proper Accept headers");
					}
					return Response.NOT_ACCEPTABLE;
				}
			}
			// check service's packages contains provided event package
			if (!serviceTypePackageVerifier.hasPackage(flatList
					.getServiceType(), eventPackage)) {
				if (logger.isInfoEnabled()) {
					logger.info("Resource list " + notifier
							+ " doesn't applies to event package "
							+ eventPackage);
				}
				return Response.BAD_EVENT;
			}

			if (flatList.getEntries().isEmpty()
					&& flatList.getStatus() != Response.OK) {
				// we got an error flattening the list and there are no entries
				// so lets make the subscription fail
				if (logger.isInfoEnabled()) {
					logger
							.info("Resource list "
									+ notifier
									+ " can't be subscribed due to failure in making flat list");
				}
				return flatList.getStatus();
			} else {
				// it is a subscribe for a resource list and it is ok (note: the
				// flat list may had errors, but it's not empty so we let it
				// proceed
				if (logger.isDebugEnabled()) {
					logger.debug("Resource list " + notifier
							+ " subscription request validated with sucess.");
				}
				return Response.OK;
			}
		} else {
			// no resource list found
			if (logger.isDebugEnabled()) {
				logger.debug(notifier + " is not a resource list.");
			}
			return Response.NOT_FOUND;
		}

	}

	public boolean createSubscription(Subscription subscription) {

		// get flat list
		FlatList flatList = rlsServicesCache.getFlatList(subscription
				.getNotifierWithParams());
		if (flatList == null) {
			return false;
		}
		// now create a event list subscriber child sbb
		EventListSubscriberSbbLocalObject subscriptionChildSbb = null;
		try {
			subscriptionChildSbb = (EventListSubscriberSbbLocalObject) getEventListSubscriberChildRelation()
					.create();
			subscriptionChildSbb
					.setParentSbb((EventListSubscriberParentSbbLocalObject) this.sbbContext
							.getSbbLocalObject());
		} catch (Exception e) {
			logger.error("Failed to create child sbb", e);
			return false;
		}

		// give the child control over the subscription
		subscriptionChildSbb.subscribe(subscription, flatList);
		return true;
	}

	public void refreshSubscription(Subscription subscription) {
		EventListSubscriberSbbLocalObject childSbb = getEventListSubscriberSbb(subscription
				.getKey());
		if (childSbb != null) {
			childSbb.resubscribe(subscription);
		} else {
			logger
					.warn("trying to refresh a event list subscription but child sbb not found");
		}
	}

	public void removeSubscription(Subscription subscription) {
		EventListSubscriberSbbLocalObject childSbb = getEventListSubscriberSbb(subscription
				.getKey());
		if (childSbb != null) {
			childSbb.unsubscribe(subscription);
		} else {
			logger
					.warn("trying to unsubscribe a event list subscription but child sbb not found");
		}
	}

	public void notifyEventListSubscriber(SubscriptionKey key,
			MultiPart multiPart) {
		if (logger.isDebugEnabled()) {
			logger.debug("notifying event list subscription " + key);
		}
		// just send to parent
		getParentSbbCMP().notifyEventListSubscriber(key, multiPart);
	}

	public Subscription getSubscription(SubscriptionKey key) {
		// just send to parent
		return getParentSbbCMP().getSubscription(key);
	}

	// --- FLAT LIST MAKER SBB

	public abstract ChildRelation getFlatListMakerChildRelation();

	public FlatListMakerSbbLocalObject getFlatListMakerSbb() {
		try {
			FlatListMakerSbbLocalObject childSbb = (FlatListMakerSbbLocalObject) getFlatListMakerChildRelation()
					.create();
			childSbb.setParentSbb((FlatListMakerParentSbbLocalObject) sbbContext.getSbbLocalObject());
			return childSbb;
		} catch (Exception e) {
			logger.error("Failed to create child sbb", e);
			return null;
		}
	}

	// --- EVENT LIST SUBSCRIBER SBB

	public abstract ChildRelation getEventListSubscriberChildRelation();

	public EventListSubscriberSbbLocalObject getEventListSubscriberSbb(
			SubscriptionKey subscriptionKey) {
		ChildRelation childRelation = getEventListSubscriberChildRelation();
		EventListSubscriberSbbLocalObject subscriptionChildSbb = null;
		// let's see if child is already created
		for (Iterator<?> it = childRelation.iterator(); it.hasNext();) {
			EventListSubscriberSbbLocalObject childSbb = (EventListSubscriberSbbLocalObject) it
					.next();
			SubscriptionKey childSbbSubscriptionKey = childSbb
					.getSubscriptionKey();
			if (childSbbSubscriptionKey != null
					&& childSbbSubscriptionKey.equals(subscriptionKey)) {
				subscriptionChildSbb = childSbb;
				break;
			}
		}
		return subscriptionChildSbb;
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
				logger.error("Failed to create child sbb", e);
				return null;
			}
			setXDMClientControlChildSbbCMP(childSbb);
			childSbb
					.setParentSbb((XDMClientControlParentSbbLocalObject) this.sbbContext
							.getSbbLocalObject());
		}
		return childSbb;
	}

	// --- JAXB

	private static final JAXBContext context = initJAXBContext();

	private static JAXBContext initJAXBContext() {
		try {
			return JAXBContext
					.newInstance("org.openxdm.xcap.client.appusage.rlsservices.jaxb");
		} catch (JAXBException e) {
			logger.error("failed to create jaxb context for rls services", e);
			return null;
		}
	}

	// ----------- SBB OBJECT's LIFE CYCLE

	private SbbContext sbbContext;

	public void setSbbContext(SbbContext sbbContext) {
		this.sbbContext = sbbContext;
	}

	public void sbbActivate() {
	}

	public void sbbCreate() throws CreateException {
	}

	public void sbbExceptionThrown(Exception arg0, Object arg1,
			ActivityContextInterface arg2) {
	}

	public void sbbLoad() {
	}

	public void sbbPassivate() {
	}

	public void sbbPostCreate() throws CreateException {
	}

	public void sbbRemove() {
	}

	public void sbbRolledBack(RolledBackContext arg0) {
	}

	public void sbbStore() {
	}

	public void unsetSbbContext() {
		this.sbbContext = null;
	}
}