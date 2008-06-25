package org.mobicents.slee.xdm.server.subscription;

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
import org.mobicents.slee.sipevent.server.subscription.NotifyContent;
import org.mobicents.slee.sipevent.server.subscription.SubscriptionControlSbb;
import org.mobicents.slee.sipevent.server.subscription.pojo.Subscription;
import org.mobicents.slee.sipevent.server.subscription.pojo.SubscriptionKey;
import org.mobicents.slee.xdm.server.XDMClientControlParentSbbLocalObject;
import org.mobicents.slee.xdm.server.XDMClientControlSbbLocalObject;
import org.openxdm.xcap.common.key.XcapUriKey;
import org.openxdm.xcap.common.uri.AttributeSelector;
import org.openxdm.xcap.common.uri.DocumentSelector;
import org.openxdm.xcap.common.uri.NodeSelector;
import org.openxdm.xcap.server.slee.resource.appusagecache.AppUsageCacheResourceAdaptorSbbInterface;
import org.openxdm.xcap.server.slee.resource.datasource.DataSourceActivityContextInterfaceFactory;
import org.openxdm.xcap.server.slee.resource.datasource.DataSourceSbbInterface;

/**
 * Subscription control sbb for a XDM Server.
 * @author eduardomartins
 *
 */
public abstract class XcapDiffSubscriptionControlSbb extends SubscriptionControlSbb implements XcapDiffSubscriptionControlSbbLocalObject {

	protected static Logger logger = Logger.getLogger(XcapDiffSubscriptionControlSbb.class);
	
	private AppUsageCacheResourceAdaptorSbbInterface appUsageCache;
	private DataSourceSbbInterface dataSourceSbbInterface = null;
	private DataSourceActivityContextInterfaceFactory dataSourceACIF = null;
	
	@Override
	public void setSbbContext(SbbContext sbbContext) {
		// TODO Auto-generated method stub
		super.setSbbContext(sbbContext);
		try {
			appUsageCache = (AppUsageCacheResourceAdaptorSbbInterface) context.lookup("slee/resources/xdm/appusagecache/sbbrainterface");
			dataSourceSbbInterface = (DataSourceSbbInterface) context.lookup("slee/resources/xdm/datasource/sbbrainterface");
			dataSourceACIF = (DataSourceActivityContextInterfaceFactory) context.lookup("slee/resources/xdm/datasource/1.0/acif");
		} catch (NamingException e) {
			logger.error("Can't set sbb context.", e);
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
	
	public HeaderFactory getHeaderFactory() {
		// exposing to XcapDiffSubscriptionControl
		return headerFactory;
	}
	
	protected Logger getLogger() {
		// exposing to XcapDiffSubscriptionControl
		return logger;
	}
	
	protected String getContactAddressString() {
		return "Mobicents XDM Server";
	}
	
	protected String[] getEventPackages() {
		// delegate to XcapDiffSubscriptionControl
		return XcapDiffSubscriptionControl.getEventPackages();
	}
	
	// ---------------------
	
	protected void isSubscriberAuthorized(RequestEvent event,
			String subscriber, String notifier, String eventPackage,
			String eventId, int expires) {
		// exposing to XcapDiffSubscriptionControl
		XcapDiffSubscriptionControl.isSubscriberAuthorized(this, event, subscriber, notifier, eventPackage, eventId, expires);	
	}
	
	@Override
	protected void removingSubscription(Subscription subscription) {
		// delegate to XcapDiffSubscriptionControl
		XcapDiffSubscriptionControl.removingSubscription(this, subscription);
	}

	protected NotifyContent getNotifyContent(Subscription subscription) {
		// delegate to XcapDiffSubscriptionControl
		return XcapDiffSubscriptionControl.getNotifyContent(this,subscription);
	}
	
	protected Object filterContentPerSubscriber(String subscriber, String notifier, String eventPackage, Object unmarshalledContent) {
		// delegate to XcapDiffSubscriptionControl
		return XcapDiffSubscriptionControl.filterContentPerSubscriber(this,subscriber,notifier,eventPackage,unmarshalledContent);
	}
	
	/*
	 * JAXB context is thread safe
	 */
	private static final JAXBContext jaxbContext = initJAXBContext();
	private static JAXBContext initJAXBContext() {
		try {
			return JAXBContext.newInstance(
					"org.openxdm.xcap.common.xcapdiff" +
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
		// redefined to be used by XcapDiffSubscriptionControl
		super.newSubscriptionAuthorization(event, subscriber, notifier, eventPackage,
				eventId, expires, responseCode);
	}
	
	public void attributeUpdated(DocumentSelector documentSelector,
			NodeSelector nodeSelector, AttributeSelector attributeSelector,
			Map<String, String> namespaces, String oldETag, String newETag,
			String documentAsString, String attributeValue) {
		
		documentUpdated(documentSelector, oldETag, newETag, documentAsString);
	}
	
	public void documentUpdated(DocumentSelector documentSelector,
			String oldETag, String newETag, String documentAsString) {
		// delegate to XcapDiffSubscriptionControl
		XcapDiffSubscriptionControl.documentUpdated(this, documentSelector, oldETag, newETag, documentAsString);
	}
	
	public void elementUpdated(DocumentSelector documentSelector,
			NodeSelector nodeSelector, Map<String, String> namespaces,
			String oldETag, String newETag, String documentAsString,
			String elementAsString) {
		// delegate to documentUpdated
		documentUpdated(documentSelector, oldETag, newETag, documentAsString);
	}
	
	// --- not used
	
	public void deleteResponse(XcapUriKey key, int responseCode, String tag) {
		throw new UnsupportedOperationException();
	}
	
	public void getResponse(XcapUriKey key, int responseCode, String mimetype,
			String content, String tag) {
		throw new UnsupportedOperationException();
		
	}
	
	public DataSourceSbbInterface getDataSourceSbbInterface() {
		return dataSourceSbbInterface;
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
}
