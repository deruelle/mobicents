package org.mobicents.slee.xdm.server.subscription;

import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sip.ServerTransaction;
import javax.sip.header.HeaderFactory;
import javax.slee.ActivityContextInterface;
import javax.slee.ChildRelation;
import javax.slee.CreateException;
import javax.slee.RolledBackContext;
import javax.slee.SLEEException;
import javax.slee.Sbb;
import javax.slee.SbbContext;
import javax.slee.TransactionRequiredLocalException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import net.java.slee.resource.sip.SleeSipProvider;

import org.apache.log4j.Logger;
import org.mobicents.slee.sipevent.server.subscription.ImplementedSubscriptionControlParentSbbLocalObject;
import org.mobicents.slee.sipevent.server.subscription.NotifyContent;
import org.mobicents.slee.sipevent.server.subscription.pojo.Subscription;
import org.mobicents.slee.sipevent.server.subscription.pojo.SubscriptionKey;
import org.mobicents.slee.xdm.server.XDMClientControlParentSbbLocalObject;
import org.mobicents.slee.xdm.server.XDMClientControlSbbLocalObject;
import org.openxdm.xcap.common.key.XcapUriKey;
import org.openxdm.xcap.common.uri.AttributeSelector;
import org.openxdm.xcap.common.uri.DocumentSelector;
import org.openxdm.xcap.common.uri.NodeSelector;
import org.openxdm.xcap.server.slee.resource.datasource.DataSourceSbbInterface;

/**
 * Subscription control sbb for a XDM Server.
 * 
 * @author eduardomartins
 * 
 */
public abstract class XcapDiffSubscriptionControlSbb implements Sbb,
		XcapDiffSubscriptionControlSbbLocalObject {

	private static Logger logger = Logger
			.getLogger(XcapDiffSubscriptionControlSbb.class);

	// private AppUsageCacheResourceAdaptorSbbInterface appUsageCache;
	private DataSourceSbbInterface dataSourceSbbInterface = null;
	// private DataSourceActivityContextInterfaceFactory dataSourceACIF = null;

	/**
	 * JAIN-SIP provider & factories
	 * 
	 * @return
	 */
	// private SipActivityContextInterfaceFactory
	// sipActivityContextInterfaceFactory;
	protected SleeSipProvider sipProvider;
	// private AddressFactory addressFactory;
	// private MessageFactory messageFactory;
	protected HeaderFactory headerFactory;

	/**
	 * SbbObject's sbb context
	 */
	private SbbContext sbbContext;

	public void setSbbContext(SbbContext sbbContext) {
		this.sbbContext = sbbContext;
		// retrieve factories, facilities & providers
		try {
			Context context = (Context) new InitialContext()
					.lookup("java:comp/env");
			// sipActivityContextInterfaceFactory =
			// (SipActivityContextInterfaceFactory) context
			// .lookup("slee/resources/jainsip/1.2/acifactory");
			sipProvider = (SleeSipProvider) context
					.lookup("slee/resources/jainsip/1.2/provider");
			// addressFactory = sipProvider.getAddressFactory();
			headerFactory = sipProvider.getHeaderFactory();
			// messageFactory = sipProvider.getMessageFactory();
			// appUsageCache = (AppUsageCacheResourceAdaptorSbbInterface)
			// context.lookup("slee/resources/xdm/appusagecache/sbbrainterface");
			dataSourceSbbInterface = (DataSourceSbbInterface) context
					.lookup("slee/resources/xdm/datasource/sbbrainterface");
			// dataSourceACIF = (DataSourceActivityContextInterfaceFactory)
			// context.lookup("slee/resources/xdm/datasource/1.0/acif");
		} catch (NamingException e) {
			logger.error("Can't set sbb context.", e);
		}
	}

	// ------------ ImplementedSubscriptionControlSbbLocalObject

	public abstract ImplementedSubscriptionControlParentSbbLocalObject getParentSbbCMP();

	public abstract void setParentSbbCMP(
			ImplementedSubscriptionControlParentSbbLocalObject sbbLocalObject);

	public void setParentSbb(
			ImplementedSubscriptionControlParentSbbLocalObject sbbLocalObject) {
		setParentSbbCMP(sbbLocalObject);
	}

	public String[] getEventPackages() {
		return new XcapDiffSubscriptionControl(this).getEventPackages();
	}

	public void isSubscriberAuthorized(String subscriber,
			String subscriberDisplayName, String notifier, SubscriptionKey key,
			int expires, String content, String contentType,
			String contentSubtype,ServerTransaction serverTransaction) {
		// exposing to XcapDiffSubscriptionControl
		new XcapDiffSubscriptionControl(this).isSubscriberAuthorized(
				subscriber, subscriberDisplayName, notifier, key, expires,
				content, contentType, contentSubtype,serverTransaction);
	}

	public void removingSubscription(Subscription subscription) {
		// delegate to XcapDiffSubscriptionControl
		new XcapDiffSubscriptionControl(this)
				.removingSubscription(subscription);
	}

	public NotifyContent getNotifyContent(Subscription subscription) {
		// delegate to XcapDiffSubscriptionControl
		return new XcapDiffSubscriptionControl(this)
				.getNotifyContent(subscription);
	}

	public Object filterContentPerSubscriber(String subscriber,
			String notifier, String eventPackage, Object unmarshalledContent) {
		// delegate to XcapDiffSubscriptionControl
		return new XcapDiffSubscriptionControl(this)
				.filterContentPerSubscriber(subscriber, notifier, eventPackage,
						unmarshalledContent);
	}

	public Marshaller getMarshaller() {
		try {
			return jaxbContext.createMarshaller();
		} catch (JAXBException e) {
			logger.error("failed to create marshaller", e);
			return null;
		}
	}

	// ------------ XcapDiffSubscriptionControlSbbLocalObject

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
				logger.error("Failed to create child sbb",e);
				return null;
			}
			setXDMClientControlChildSbbCMP(childSbb);
			childSbb
					.setParentSbb((XDMClientControlParentSbbLocalObject) this.sbbContext
							.getSbbLocalObject());
		}
		return childSbb;
	}

	// --- CMP

	public abstract void setSubscriptionsMap(Map rules);

	public abstract Map getSubscriptionsMap();

	public DataSourceSbbInterface getDataSourceSbbInterface() {
		return dataSourceSbbInterface;
	}

	public HeaderFactory getHeaderFactory() {
		return headerFactory;
	}

	public Unmarshaller getUnmarshaller() {
		try {
			return jaxbContext.createUnmarshaller();
		} catch (JAXBException e) {
			logger.error("failed to create unmarshaller", e);
			return null;
		}
	}

	// ------------ XDMClientControlParentSbbLocalObject

	public void attributeUpdated(DocumentSelector documentSelector,
			NodeSelector nodeSelector, AttributeSelector attributeSelector,
			Map<String, String> namespaces, String oldETag, String newETag,
			String documentAsString, String attributeValue) {

		documentUpdated(documentSelector, oldETag, newETag, documentAsString);
	}

	public void documentUpdated(DocumentSelector documentSelector,
			String oldETag, String newETag, String documentAsString) {
		// delegate to XcapDiffSubscriptionControl
		new XcapDiffSubscriptionControl(this).documentUpdated(documentSelector,
				oldETag, newETag, documentAsString);
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

	// --------- JAXB

	/*
	 * JAXB context is thread safe
	 */
	private static final JAXBContext jaxbContext = initJAXBContext();

	private static JAXBContext initJAXBContext() {
		try {
			return JAXBContext.newInstance("org.openxdm.xcap.common.xcapdiff"
					+ ":org.openxdm.xcap.client.appusage.resourcelists.jaxb");
		} catch (JAXBException e) {
			logger.error("failed to create jaxb context");
			return null;
		}
	}

	// ----------- SBB OBJECT's LIFE CYCLE

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