package org.mobicents.slee.sippresence.server.integrated.subscription;

import java.util.Iterator;
import java.util.Map;

import javax.naming.NamingException;
import javax.sip.RequestEvent;
import javax.sip.header.ContentTypeHeader;
import javax.sip.header.HeaderFactory;
import javax.slee.ChildRelation;
import javax.slee.SbbContext;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;
import org.mobicents.slee.sipevent.server.publication.PublicationControlSbbLocalObject;
import org.mobicents.slee.sipevent.server.subscription.NotifyContent;
import org.mobicents.slee.sipevent.server.subscription.SubscriptionControlSbb;
import org.mobicents.slee.sipevent.server.subscription.pojo.Subscription;
import org.mobicents.slee.sipevent.server.subscription.pojo.SubscriptionKey;
import org.mobicents.slee.sippresence.server.subscription.PresenceSubscriptionControl;
import org.mobicents.slee.xdm.server.XDMClientControlParentSbbLocalObject;
import org.mobicents.slee.xdm.server.XDMClientControlSbbLocalObject;
import org.mobicents.slee.xdm.server.subscription.XcapDiffSubscriptionControl;
import org.openxdm.xcap.common.key.XcapUriKey;
import org.openxdm.xcap.common.uri.AttributeSelector;
import org.openxdm.xcap.common.uri.DocumentSelector;
import org.openxdm.xcap.common.uri.NodeSelector;
import org.openxdm.xcap.server.slee.resource.appusagecache.AppUsageCacheResourceAdaptorSbbInterface;
import org.openxdm.xcap.server.slee.resource.datasource.DataSourceActivityContextInterfaceFactory;
import org.openxdm.xcap.server.slee.resource.datasource.DataSourceSbbInterface;

/**
 * Subscription control sbb for an integrated SIP Presence Server.
 * @author eduardomartins
 *
 */
public abstract class IntegratedSubscriptionControlSbb extends SubscriptionControlSbb implements IntegratedSubscriptionControlSbbLocalObject {

	private static Logger logger = Logger.getLogger(IntegratedSubscriptionControlSbb.class);
		
	private AppUsageCacheResourceAdaptorSbbInterface appUsageCache;
	private DataSourceSbbInterface dataSourceSbbInterface;
	private DataSourceActivityContextInterfaceFactory dataSourceACIF;
	private String presRulesAUID;
	private String presRulesDocumentName;
	
	@Override
	public void setSbbContext(SbbContext sbbContext) {
		// TODO Auto-generated method stub
		super.setSbbContext(sbbContext);
		try {
			appUsageCache = (AppUsageCacheResourceAdaptorSbbInterface) context.lookup("slee/resources/xdm/appusagecache/sbbrainterface");
			dataSourceSbbInterface = (DataSourceSbbInterface) context.lookup("slee/resources/xdm/datasource/sbbrainterface");
			dataSourceACIF = (DataSourceActivityContextInterfaceFactory) context.lookup("slee/resources/xdm/datasource/1.0/acif");
			presRulesAUID = (String) context.lookup("presRulesAUID");
			presRulesDocumentName = (String) context.lookup("presRulesDocumentName");
		} catch (NamingException e) {
			logger.error("Can't set sbb context.", e);
		}		
	}
	
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
				sbbLocalObject.setParentSbb((XDMClientControlParentSbbLocalObject)sbbContext.getSbbLocalObject());
				return sbbLocalObject;
			} catch (Exception e) {
				logger.error("failed to create child sbb", e);
				return null;
			}
		}		
		
	}
	
	// ------------ cmps
	
	public abstract void setSubscriptionsMap(Map rules);
	public abstract Map getSubscriptionsMap();
	public abstract void setCombinedRules(Map rules);
	public abstract Map getCombinedRules();
	
	public HeaderFactory getHeaderFactory() {
		return headerFactory;
	}
	
	protected Logger getLogger() {
		return logger;
	}
	
	protected String getContactAddressString() {
		return "Mobicents Integrated Presence Service";
	}
	
	private static final String[] eventPackages = initEventPackages();
	
	private static String[] initEventPackages() {
		int xcapDiffArrayLenght = XcapDiffSubscriptionControl.getEventPackages().length;
		int presenceArrayLenght = PresenceSubscriptionControl.getEventPackages().length;
		int resultArrayLenght = xcapDiffArrayLenght + presenceArrayLenght;
		String[] result = new String[resultArrayLenght];
		for(int i=0;i<presenceArrayLenght;i++) {
			result[i]=PresenceSubscriptionControl.getEventPackages()[i];
		}
		for(int i=0;i<xcapDiffArrayLenght;i++) {
			result[i+presenceArrayLenght]=XcapDiffSubscriptionControl.getEventPackages()[i];
		}
		return result;
	}
	
	protected String[] getEventPackages() {
		return eventPackages;
	}
	
	private boolean contains(String[] array, String eventPackage) {
		for(String s:array) {
			if (s.equals(eventPackage)) {
				return true;
			}
		}
		return false;
	}
	
	// ---------------------
	
	protected void isSubscriberAuthorized(RequestEvent event,
			String subscriber, String notifier, String eventPackage,
			String eventId, int expires) {
		
		if (contains(PresenceSubscriptionControl.getEventPackages(),eventPackage)) {
			PresenceSubscriptionControl.isSubscriberAuthorized(this, event, subscriber, notifier, eventPackage, eventId, expires,presRulesAUID,presRulesDocumentName);
		}
		else if (contains(XcapDiffSubscriptionControl.getEventPackages(),eventPackage)) {
			XcapDiffSubscriptionControl.isSubscriberAuthorized(this, event, subscriber, notifier, eventPackage, eventId, expires);
		}
		else {
			logger.warn("isSubscriberAuthorized() invoked with unknow event package");
		}
	}
	
	@Override
	protected void removingSubscription(Subscription subscription) {
		if (contains(PresenceSubscriptionControl.getEventPackages(),subscription.getSubscriptionKey().getEventPackage())) {
			PresenceSubscriptionControl.removingSubscription(this, subscription, presRulesAUID, presRulesDocumentName);
		}
		else if (contains(XcapDiffSubscriptionControl.getEventPackages(),subscription.getSubscriptionKey().getEventPackage())) {
			XcapDiffSubscriptionControl.removingSubscription(this, subscription);
		}
		else {
			logger.warn("removingSubscription() invoked with unknown event package");
		}
	}

	protected NotifyContent getNotifyContent(Subscription subscription) {
		if (contains(PresenceSubscriptionControl.getEventPackages(),subscription.getSubscriptionKey().getEventPackage())) {
			return PresenceSubscriptionControl.getNotifyContent(this,subscription);
		}
		else if (contains(XcapDiffSubscriptionControl.getEventPackages(),subscription.getSubscriptionKey().getEventPackage())) {
			return XcapDiffSubscriptionControl.getNotifyContent(this,subscription);
		}
		else {
			logger.warn("getNotifyContent() invoked with unknown event package");
			return null;
		}
	}
	
	protected Object filterContentPerSubscriber(String subscriber, String notifier, String eventPackage, Object unmarshalledContent) {
		if (contains(PresenceSubscriptionControl.getEventPackages(),eventPackage)) {
			return PresenceSubscriptionControl.filterContentPerSubscriber(this,subscriber,notifier,eventPackage,unmarshalledContent);
		}
		else if (contains(XcapDiffSubscriptionControl.getEventPackages(),eventPackage)) {
			return XcapDiffSubscriptionControl.filterContentPerSubscriber(this,subscriber,notifier,eventPackage,unmarshalledContent);
		}
		else {
			logger.warn("filterContentPerSubscriber() invoked with unknown event package");
			return null;
		}		
	}
	
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
					":org.openxdm.xcap.client.appusage.omapresrules.jaxb"+
					":org.openxdm.xcap.common.xcapdiff" +
					":org.openxdm.xcap.client.appusage.resourcelists.jaxb");
		} catch (JAXBException e) {
			logger.error("failed to create jaxb context");
			return null;
		}
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
	
	@Override
	public void newSubscriptionAuthorization(RequestEvent event,
			String subscriber, String notifier, String eventPackage,
			String eventId, int expires, int responseCode) {
		super.newSubscriptionAuthorization(event, subscriber, notifier, eventPackage,
				eventId, expires, responseCode);
	}
	
	public void attributeUpdated(DocumentSelector documentSelector,
			NodeSelector nodeSelector, AttributeSelector attributeSelector,
			Map<String, String> namespaces, String oldETag, String newETag,
			String documentAsString, String attributeValue) {
		
		documentUpdated(documentSelector, oldETag, newETag, documentAsString);
	}
	
	/**
	 * a pres-rules doc subscribed was updated
	 */
	public void documentUpdated(DocumentSelector documentSelector,String oldETag,String newETag,String documentAsString) {
		PresenceSubscriptionControl.documentUpdated(this, documentSelector, oldETag, newETag, documentAsString);
		XcapDiffSubscriptionControl.documentUpdated(this, documentSelector, oldETag, newETag, documentAsString);
	}
	
	
	public void elementUpdated(DocumentSelector documentSelector,
			NodeSelector nodeSelector, Map<String, String> namespaces,
			String oldETag, String newETag, String documentAsString,
			String elementAsString) {
		// delegate to documentUpdated
		documentUpdated(documentSelector, oldETag, newETag, documentAsString);
	}
	
	public DataSourceSbbInterface getDataSourceSbbInterface() {
		return dataSourceSbbInterface;
	}
	
	// --- not used
	
	public void deleteResponse(XcapUriKey key, int responseCode, String tag) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * async get response from xdm client
	 */
	public void getResponse(XcapUriKey key, int responseCode, String mimetype,
		String content, String tag) {
		
		PresenceSubscriptionControl.getResponse(this, key, responseCode, mimetype, content);
	}
	
	public void putResponse(XcapUriKey key, int responseCode, String tag) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void notifySubscriber(SubscriptionKey key, Object content,
			ContentTypeHeader contentTypeHeader) {
		// TODO Auto-generated method stub
		super.notifySubscriber(key, content, contentTypeHeader);
	}
	
	@Override
	public void authorizationChanged(String subscriber, String notifier,
			String eventPackage, int authorizationCode) {
		// TODO Auto-generated method stub
		super.authorizationChanged(subscriber, notifier, eventPackage,
				authorizationCode);
	}
	
	/**
	 * interface used by rules processor to get sphere for a notifier
	 */
	public String getSphere(String notifier) {
		return PresenceSubscriptionControl.getSphere(this, notifier);
	}
	
	
}
