package org.mobicents.slee.sippresence.server.subscription;

import java.util.Iterator;
import java.util.Map;

import javax.sip.RequestEvent;
import javax.sip.header.HeaderFactory;
import javax.slee.ChildRelation;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;
import org.mobicents.slee.sipevent.server.publication.PublicationControlSbbLocalObject;
import org.mobicents.slee.sipevent.server.subscription.NotifyContent;
import org.mobicents.slee.sipevent.server.subscription.SubscriptionControlSbb;
import org.mobicents.slee.sipevent.server.subscription.pojo.Subscription;
import org.mobicents.slee.xdm.server.XDMClientControlParentSbbLocalObject;
import org.mobicents.slee.xdm.server.XDMClientControlSbbLocalObject;
import org.openxdm.xcap.common.key.XcapUriKey;
import org.openxdm.xcap.common.uri.AttributeSelector;
import org.openxdm.xcap.common.uri.DocumentSelector;
import org.openxdm.xcap.common.uri.NodeSelector;

/**
 * Subscription control sbb for a SIP Presence Server.
 * @author eduardomartins
 *
 */
public abstract class PresenceSubscriptionControlSbb extends SubscriptionControlSbb implements PresenceSubscriptionControlSbbLocalObject {

	private static Logger logger = Logger.getLogger(PresenceSubscriptionControlSbb.class);
	
	// --- PUBLICATION CHILD SBB
	public abstract ChildRelation getPublicationControlChildRelation();
	public PublicationControlSbbLocalObject getPublicationChildSbb() {
		ChildRelation childRelation = getPublicationControlChildRelation();
		Iterator childRelationIterator =  childRelation.iterator();
		if (childRelationIterator.hasNext()) {
			return (PublicationControlSbbLocalObject) childRelationIterator.next();
		}
		else {
			try {
				return (PublicationControlSbbLocalObject) childRelation.create();
			} catch (Exception e) {
				logger.error("failed to create child sbb", e);
				return null;
			}
		}		
	}
	
	// --- XDM CLIENT CHILD SBB
	public abstract ChildRelation getXDMClientControlChildRelation();
	public XDMClientControlSbbLocalObject getXDMChildSbb() {
	
		ChildRelation childRelation = getXDMClientControlChildRelation();
		Iterator childRelationIterator =  childRelation.iterator();
		if (childRelationIterator.hasNext()) {
			return (XDMClientControlSbbLocalObject) childRelationIterator.next();
		}
		else {
			try {
				XDMClientControlSbbLocalObject sbbLocalObject = (XDMClientControlSbbLocalObject) childRelation.create();
				sbbLocalObject.setParentSbb((XDMClientControlParentSbbLocalObject)this.sbbContext.getSbbLocalObject());
				return sbbLocalObject;
			} catch (Exception e) {
				logger.error("failed to create child sbb", e);
				return null;
			}
		}		
		
	}
	
	// ------------ cmps
	public abstract void setCombinedRules(Map rules);
	public abstract Map getCombinedRules();
	
	protected Logger getLogger() {
		return logger;
	}
	
	public HeaderFactory getHeaderFactory() {
		// exposing to PresenceSubscriptionControl
		return headerFactory;
	}
	
	@Override
	public void authorizationChanged(String subscriber, String notifier,
			String eventPackage, int authorizationCode) {
		// TODO Auto-generated method stub
		super.authorizationChanged(subscriber, notifier, eventPackage,
				authorizationCode);
	}
	
	public void newSubscriptionAuthorization(RequestEvent event,
			String subscriber, String notifier, String eventPackage,
			String eventId, int expires, int responseCode) {
		// TODO Auto-generated method stub
		super.newSubscriptionAuthorization(event, subscriber, notifier, eventPackage,
				eventId, expires, responseCode);
	}
	
	protected String getContactAddressString() {
		return "Mobicents Presence Server";
	}
	
	protected String[] getEventPackages() {
		return PresenceSubscriptionControl.getEventPackages();
	}
	
	// ---------------------

	protected void isSubscriberAuthorized(RequestEvent event, String subscriber,
			String notifier, String eventPackage, String eventId, int expires) {
		
		PresenceSubscriptionControl.isSubscriberAuthorized(this, event, subscriber, notifier, eventPackage, eventId, expires);
	}
	
	/**
	 * async get response from xdm client
	 */
	public void getResponse(XcapUriKey key, int responseCode, String mimetype,
		String content, String tag) {
		
		PresenceSubscriptionControl.getResponse(this, key, responseCode, mimetype, content);
	}
	
	@Override
	protected void removingSubscription(Subscription subscription) {
		PresenceSubscriptionControl.removingSubscription(this, subscription);
	}
	
	/**
	 * a pres-rules doc subscribed was updated
	 */
	public void documentUpdated(DocumentSelector documentSelector,String oldETag,String newETag,String documentAsString) {
		
		PresenceSubscriptionControl.documentUpdated(this, documentSelector, oldETag, newETag, documentAsString);
	}

	// atm only processing update per doc "granularity"
	public void attributeUpdated(DocumentSelector documentSelector,
			NodeSelector nodeSelector, AttributeSelector attributeSelector,
			Map<String, String> namespaces, String oldETag, String newETag,
			String documentAsString, String attributeValue) {
		documentUpdated(documentSelector, oldETag, newETag, documentAsString);	
	}
	public void elementUpdated(DocumentSelector documentSelector,
			NodeSelector nodeSelector, Map<String, String> namespaces,
			String oldETag, String newETag, String documentAsString,
			String elementAsString) {
		documentUpdated(documentSelector, oldETag, newETag, documentAsString);
	}
	
	/**
	 * interface used by rules processor to get sphere for a notifier
	 */
	public String getSphere(String notifier) {
		return PresenceSubscriptionControl.getSphere(this, notifier);
	}
	
	protected NotifyContent getNotifyContent(Subscription subscription) {
		return PresenceSubscriptionControl.getNotifyContent(this, subscription);
	}
	
	protected Object filterContentPerSubscriber(String subscriber, String notifier, String eventPackage, Object unmarshalledContent) {
		return PresenceSubscriptionControl.filterContentPerSubscriber(this,subscriber, notifier, eventPackage, unmarshalledContent);
	}
	
	protected Marshaller getMarshaller() {
		try {
			return jaxbContext.createMarshaller();
		} catch (JAXBException e) {
			logger.error("failed to create marshaller",e);
			return null;
		}
	}
	
	public Unmarshaller getUnmarshaller() {
		try {
			return jaxbContext.createUnmarshaller();
		} catch (JAXBException e) {
			logger.error("failed to create unmarshaller",e);
			return null;
		}
	}
	
	// --------- JAXB
	
	/*
	 * JAXB context is thread safe
	 */
	private static final JAXBContext jaxbContext = initJAXBContext();
	private static JAXBContext initJAXBContext() {
		try {
			return JAXBContext.newInstance(
					"org.mobicents.slee.sippresence.pojo.pidf" +
					":org.mobicents.slee.sippresence.pojo.rpid" +
					":org.mobicents.slee.sippresence.pojo.datamodel" +
					":org.mobicents.slee.sippresence.pojo.commonschema"+
					":org.openxdm.xcap.client.appusage.presrules.jaxb.commonpolicy" +
					":org.openxdm.xcap.client.appusage.presrules.jaxb" +
					":org.openxdm.xcap.client.appusage.omapresrules.jaxb");
		} catch (JAXBException e) {
			logger.error("failed to create jaxb context");
			return null;
		}
	}
	
	// unused methods from xdm client sbb
	
	public void deleteResponse(XcapUriKey key, int responseCode, String tag) {
		throw new UnsupportedOperationException();
	}

	public void putResponse(XcapUriKey key, int responseCode, String tag) {
		throw new UnsupportedOperationException();
	}
	
}
