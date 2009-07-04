package org.mobicents.slee.sippresence.server.integrated.subscription;

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
import javax.slee.Sbb;
import javax.slee.SbbContext;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import net.java.slee.resource.sip.SleeSipProvider;

import org.apache.log4j.Logger;
import org.mobicents.slee.sipevent.server.publication.PublicationControlSbbLocalObject;
import org.mobicents.slee.sipevent.server.subscription.ImplementedSubscriptionControlParentSbbLocalObject;
import org.mobicents.slee.sipevent.server.subscription.NotifyContent;
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
import org.openxdm.xcap.server.slee.resource.datasource.DataSourceSbbInterface;

/**
 * Implemented Subscription control child sbb for an integrated XCAP Diff and
 * SIP Presence Server.
 * 
 * @author eduardomartins
 * 
 */
public abstract class IntegratedSubscriptionControlSbb implements Sbb,
		IntegratedSubscriptionControlSbbLocalObject {

	private static Logger logger = Logger
			.getLogger(IntegratedSubscriptionControlSbb.class);

	private static final String[] eventPackages = initEventPackages();

	private static String[] initEventPackages() {
		int xcapDiffArrayLenght = XcapDiffSubscriptionControl
				.getEventPackages().length;
		int presenceArrayLenght = PresenceSubscriptionControl
				.getEventPackages().length;
		int resultArrayLenght = xcapDiffArrayLenght + presenceArrayLenght;
		String[] result = new String[resultArrayLenght];
		for (int i = 0; i < presenceArrayLenght; i++) {
			result[i] = PresenceSubscriptionControl.getEventPackages()[i];
		}
		for (int i = 0; i < xcapDiffArrayLenght; i++) {
			result[i + presenceArrayLenght] = XcapDiffSubscriptionControl
					.getEventPackages()[i];
		}
		return result;
	}

	private DataSourceSbbInterface dataSourceSbbInterface;
	private String presRulesAUID;
	private String presRulesDocumentName;

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
			presRulesAUID = (String) context.lookup("presRulesAUID");
			presRulesDocumentName = (String) context
					.lookup("presRulesDocumentName");
			dataSourceSbbInterface = (DataSourceSbbInterface) context
					.lookup("slee/resources/xdm/datasource/sbbrainterface");
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
		return eventPackages;
	}

	private boolean contains(String[] array, String eventPackage) {
		for (String s : array) {
			if (s.equals(eventPackage)) {
				return true;
			}
		}
		return false;
	}

	public void isSubscriberAuthorized(String subscriber,
			String subscriberDisplayName, String notifier, SubscriptionKey key,
			int expires, String content, String contentType,
			String contentSubtype, boolean eventList,ServerTransaction serverTransaction) {

		if (contains(PresenceSubscriptionControl.getEventPackages(), key
				.getEventPackage())) {
			new PresenceSubscriptionControl(this).isSubscriberAuthorized(
					subscriber, subscriberDisplayName, notifier, key, expires,
					content, contentType, contentSubtype, eventList, presRulesAUID,
					presRulesDocumentName,serverTransaction);
		} else if (contains(XcapDiffSubscriptionControl.getEventPackages(), key
				.getEventPackage())) {
			new XcapDiffSubscriptionControl(this).isSubscriberAuthorized(
					subscriber, subscriberDisplayName, notifier, key, expires,
					content, contentType, contentSubtype,eventList,serverTransaction);
		}
	}

	public void removingSubscription(Subscription subscription) {
		if (contains(PresenceSubscriptionControl.getEventPackages(),
				subscription.getKey().getEventPackage())) {
			new PresenceSubscriptionControl(this).removingSubscription(
					subscription, presRulesAUID, presRulesDocumentName);
		} else if (contains(XcapDiffSubscriptionControl.getEventPackages(),
				subscription.getKey().getEventPackage())) {
			new XcapDiffSubscriptionControl(this)
					.removingSubscription(subscription);
		}
	}

	public NotifyContent getNotifyContent(Subscription subscription) {
		if (contains(PresenceSubscriptionControl.getEventPackages(),
				subscription.getKey().getEventPackage())) {
			return new PresenceSubscriptionControl(this)
					.getNotifyContent(subscription);
		} else if (contains(XcapDiffSubscriptionControl.getEventPackages(),
				subscription.getKey().getEventPackage())) {
			return new XcapDiffSubscriptionControl(this)
					.getNotifyContent(subscription);
		} else {
			return null;
		}
	}

	public Object filterContentPerSubscriber(String subscriber,
			String notifier, String eventPackage, Object unmarshalledContent) {
		if (contains(PresenceSubscriptionControl.getEventPackages(),
				eventPackage)) {
			return new PresenceSubscriptionControl(this)
					.filterContentPerSubscriber(subscriber, notifier,
							eventPackage, unmarshalledContent);
		} else if (contains(XcapDiffSubscriptionControl.getEventPackages(),
				eventPackage)) {
			return new XcapDiffSubscriptionControl(this)
					.filterContentPerSubscriber(subscriber, notifier,
							eventPackage, unmarshalledContent);
		} else {
			logger
					.warn("filterContentPerSubscriber() invoked with unknown event package");
			return null;
		}
	}

	public Marshaller getMarshaller() {
		try {
			return jaxbContext.createMarshaller();
		} catch (JAXBException e) {
			logger.error("failed to create marshaller", e);
			return null;
		}
	}

	// ------------ PresenceSubscriptionControlSbbLocalObject

	// --- PUBLICATION CHILD SBB
	public abstract ChildRelation getPublicationControlChildRelation();

	public abstract PublicationControlSbbLocalObject getPublicationControlChildSbbCMP();

	public abstract void setPublicationControlChildSbbCMP(
			PublicationControlSbbLocalObject value);

	public PublicationControlSbbLocalObject getPublicationChildSbb() {
		PublicationControlSbbLocalObject childSbb = getPublicationControlChildSbbCMP();
		if (childSbb == null) {
			try {
				childSbb = (PublicationControlSbbLocalObject) getPublicationControlChildRelation()
						.create();
			} catch (Exception e) {
				logger.error("Failed to create child sbb", e);
				return null;
			}
			setPublicationControlChildSbbCMP(childSbb);
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

	// --- CMPs
	public abstract void setSubscriptionsMap(Map rules);

	public abstract Map getSubscriptionsMap();

	public abstract void setCombinedRules(Map rules);

	public abstract Map getCombinedRules();

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

	/**
	 * async get response from xdm client
	 */
	public void getResponse(XcapUriKey key, int responseCode, String mimetype,
			String content, String tag) {
		new PresenceSubscriptionControl(this).getResponse(key, responseCode,
				mimetype, content);
	}

	/**
	 * a pres-rules doc subscribed was updated
	 */
	public void documentUpdated(DocumentSelector documentSelector,
			String oldETag, String newETag, String documentAsString) {
		new PresenceSubscriptionControl(this).documentUpdated(documentSelector,
				oldETag, newETag, documentAsString);
		new XcapDiffSubscriptionControl(this).documentUpdated(documentSelector,
				oldETag, newETag, documentAsString);
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

	// unused methods from xdm client sbb

	public void deleteResponse(XcapUriKey key, int responseCode, String responseContent, String tag) {
		throw new UnsupportedOperationException();
	}

	public void putResponse(XcapUriKey key, int responseCode, String responseContent, String tag) {
		throw new UnsupportedOperationException();
	}

	// ---------- PublishedSphereSource
	/**
	 * interface used by rules processor to get sphere for a notifier
	 */
	public String getSphere(String notifier) {
		return new PresenceSubscriptionControl(this).getSphere(notifier);
	}

	// --------- JAXB

	/*
	 * JAXB context is thread safe
	 */
	private static final JAXBContext jaxbContext = initJAXBContext();

	private static JAXBContext initJAXBContext() {
		try {
			return JAXBContext
					.newInstance("org.mobicents.slee.sippresence.pojo.pidf"
							+ ":org.mobicents.slee.sippresence.pojo.pidf.oma"
							+ ":org.mobicents.slee.sippresence.pojo.rpid"
							+ ":org.mobicents.slee.sippresence.pojo.datamodel"
							+ ":org.mobicents.slee.sippresence.pojo.commonschema"
							+ ":org.openxdm.xcap.client.appusage.presrules.jaxb.commonpolicy"
							+ ":org.openxdm.xcap.client.appusage.presrules.jaxb"
							+ ":org.openxdm.xcap.client.appusage.omapresrules.jaxb"
							+ ":org.openxdm.xcap.common.xcapdiff"
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