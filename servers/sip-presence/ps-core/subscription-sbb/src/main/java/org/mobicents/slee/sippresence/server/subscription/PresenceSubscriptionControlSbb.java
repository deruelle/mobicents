package org.mobicents.slee.sippresence.server.subscription;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.sip.header.UserAgentHeader;
import javax.sip.message.Response;
import javax.slee.ChildRelation;
import javax.slee.CreateException;
import javax.slee.SLEEException;
import javax.slee.TransactionRequiredLocalException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.log4j.Logger;
import org.mobicents.slee.sipevent.server.publication.PublicationControlSbbLocalObject;
import org.mobicents.slee.sipevent.server.publication.pojo.ComposedPublication;
import org.mobicents.slee.sipevent.server.subscription.NotifyContent;
import org.mobicents.slee.sipevent.server.subscription.SubscriptionControlSbb;
import org.mobicents.slee.sipevent.server.subscription.pojo.Subscription;
import org.mobicents.slee.sippresence.pojo.datamodel.Person;
import org.mobicents.slee.sippresence.pojo.pidf.Presence;
import org.mobicents.slee.sippresence.pojo.rpid.Sphere;
import org.mobicents.slee.sippresence.server.subscription.rules.OMAPresRule;
import org.mobicents.slee.sippresence.server.subscription.rules.PresRuleCMPKey;
import org.mobicents.slee.sippresence.server.subscription.rules.PublishedSphereSource;
import org.mobicents.slee.sippresence.server.subscription.rules.RulesetProcessor;
import org.mobicents.slee.xdm.server.XDMClientControlParentSbbLocalObject;
import org.mobicents.slee.xdm.server.XDMClientControlSbbLocalObject;
import org.openxdm.xcap.client.appusage.presrules.jaxb.commonpolicy.Ruleset;
import org.openxdm.xcap.common.uri.DocumentSelector;

/**
 * Subscription control sbb for a SIP Presence Server.
 * @author eduardomartins
 *
 */
public abstract class PresenceSubscriptionControlSbb extends SubscriptionControlSbb implements XDMClientControlParentSbbLocalObject, PublishedSphereSource {

	private static Logger logger = Logger.getLogger(PresenceSubscriptionControlSbb.class);
	private final String[] eventPackages = {"presence"};
	private final boolean clientUAIsEyebeam = true;
	
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
	
	protected Logger getLogger() {
		return logger;
	}
	
	protected String getContactAddressString() {
		return "Presence Agent <sip:127.0.0.1:5060>";
	}
	
	protected String[] getEventPackages() {
		return eventPackages;
	}
	
	// ------------ cmps
	public abstract void setCombinedRules(Map rules);
	public abstract Map getCombinedRules();
	
	// ---------------------
	protected int isSubscriberAuthorized(String subscriber, UserAgentHeader subscriberUserAgent, String notifier, String eventPackage,String eventId) {
		
		// get current combined rule from cmp
		Map combinedRules = getCombinedRules();
		if (combinedRules == null) {
			combinedRules = new HashMap();
		}
		PresRuleCMPKey cmpKey = new PresRuleCMPKey(subscriber,notifier,eventPackage,eventId);
		OMAPresRule combinedRule = (OMAPresRule) combinedRules.get(cmpKey);
		
		if (combinedRule == null) {
			// rule not found, lookup for another key with same subscriber and notifier
			for(Object keyObject:combinedRules.keySet()) {
				PresRuleCMPKey k = (PresRuleCMPKey)keyObject;
				if (k.getSubscriber().equals(subscriber) && k.getNotifier().equals(notifier)) {
					// found compatible key, get rule and assing to this key also
					combinedRule = (OMAPresRule) combinedRules.get(k);
					combinedRules.put(cmpKey, combinedRule);
					break;
				}
			}
		}
		if (combinedRule == null) {
			// get pres-rules doc on xdm (doc path depends on user agent) FIXME use user agent
			DocumentSelector documentSelector = getDocumentSelector(notifier);
			if (documentSelector == null) {
				return Response.FORBIDDEN;
			}
			XDMClientControlSbbLocalObject xdm = getXDMChildSbb();	
			Object object = xdm.getDocument(documentSelector);
			// process ruleset
			if (object != null) {
				logger.info("processing pres-rules ruleset (subscriber="+subscriber+",notifier="+notifier+")");
				RulesetProcessor rulesetProcessor = new RulesetProcessor(subscriber,notifier,(Ruleset)object,this);
				combinedRule = rulesetProcessor.getCombinedRule();
				combinedRules.put(cmpKey, combinedRule);
				setCombinedRules(combinedRules);
				// subscribe to changes in pres-rules doc
				xdm.subscribeDocument(documentSelector);
			}
			else {
				// allowing subscriptions when notifier did not define pres-rules
				logger.info("authorizing subscription, pres-rules not found for notifier "+notifier);
				return Response.OK;
			}
		}
		
		return combinedRule.getSubHandling().getResponseCode();
	}
	
	@Override
	protected void removingSubscription(String subscriber, String notifier, String eventPackage, String eventId) {
		// get combined rules cmp
		Map combinedRules = getCombinedRules();
		if (combinedRules != null) {
			// remove subscription from map
			if(combinedRules.remove(new PresRuleCMPKey(subscriber,notifier,eventPackage,eventId)) != null) {
				// check if subscription to pres-rules still needed
				boolean subscriptionNeeded = false;
				for(Object k:combinedRules.keySet()) {
					PresRuleCMPKey presRuleCMPKey = (PresRuleCMPKey)k;
					if (presRuleCMPKey.getNotifier().equals(notifier) && presRuleCMPKey.getEventPackage().equals(eventPackage)) {
						subscriptionNeeded = true;
						break;
					}
				}
				if (!subscriptionNeeded) {
					DocumentSelector documentSelector = getDocumentSelector(notifier);
					getXDMChildSbb().unsubscribeDocument(documentSelector);
				}
			}
		}
	}
	
	/**
	 * from a user return the document selector pointing to it's pres-rules
	 * @param user
	 * @return
	 */
	private DocumentSelector getDocumentSelector(String user) {
		
		if(clientUAIsEyebeam) {
			String userName = null;
			int i = user.indexOf('@');
			if (i>0) {
				userName = user.substring(0, i);
				if (user.startsWith("sip:")) {
					userName = userName.substring(4);
				}
			}
			if (userName != null) {
				return new DocumentSelector("org.openmobilealliance.pres-rules","users/"+userName,"pres-rules");
			}
			else {
				return null;
			}
		}
		else {
			return new DocumentSelector("pres-rules","users/"+user,"index");
		}
		
	}
	
	/**
	 * from a document selector point to a pres-rules doc return the user
	 * @param documentSelector
	 * @return
	 */
	private String getUser(DocumentSelector documentSelector) {
		
		if(clientUAIsEyebeam) {
			return "sip:" + documentSelector.getDocumentParent().substring("users/".length()) + "@"+System.getProperty("bind.address","127.0.0.1");
		}
		else {
			return documentSelector.getDocumentParent().substring("users/".length());
		}
	}
	
	/**
	 * a pres-rules doc subscribed was updated
	 */
	public void documentUpdated(DocumentSelector documentSelector, Object document) {
		
		String notifier = getUser(documentSelector);	
				
		// get current combined rules from cmp
		Map combinedRules = getCombinedRules();
		if (combinedRules == null) {
			combinedRules = new HashMap();
		}
		// for each combined rules that has the user that updated the doc as notifier reprocess the rules
		for (Object key:combinedRules.keySet()) {
			PresRuleCMPKey cmpKey = (PresRuleCMPKey) key;
			if (cmpKey.getNotifier().equals(notifier)) {
				OMAPresRule oldCombinedRule = (OMAPresRule)combinedRules.get(cmpKey);
				RulesetProcessor rulesetProcessor = new RulesetProcessor(cmpKey.getSubscriber(),notifier,(Ruleset)document,this);
				OMAPresRule newCombinedRule = rulesetProcessor.getCombinedRule();
				combinedRules.put(cmpKey, newCombinedRule);
				// check permission changed
				if (oldCombinedRule.getSubHandling().getResponseCode() != newCombinedRule.getSubHandling().getResponseCode()) {
					authorizationChanged(cmpKey.getSubscriber(), notifier, "presence",newCombinedRule.getSubHandling().getResponseCode());
				}
			}
		}

		setCombinedRules(combinedRules);
		
	}

	/**
	 * interface used by rules processor to get sphere for a notifier
	 */
	public String getSphere(String notifier) {
		ComposedPublication composedPublication = getPublicationChildSbb().getComposedPublication(notifier, "presence");
		if (composedPublication != null && composedPublication.getUnmarshalledContent().getValue() instanceof Presence) {
			Presence presence = (Presence) composedPublication.getUnmarshalledContent().getValue();
			for (Object anyObject :presence.getAny()){
				JAXBElement anyElement = (JAXBElement) anyObject;
				if (anyElement.getValue() instanceof Person) {
					Person person = (Person) anyElement.getValue();
					for (Object anotherAnyObject :person.getAny()){
						JAXBElement anotherAnyElement = (JAXBElement) anotherAnyObject;
						if (anotherAnyElement.getValue() instanceof Sphere) {
							Sphere sphere = ((Sphere)anotherAnyElement.getValue());
							String result = null;
							for (Object contentObject :sphere.getContent()){
								if (contentObject instanceof String) {
									if (result == null) {
										result = (String)contentObject;
									}
									else {
										result += " " + (String)contentObject; 
									}
								}
								else if (contentObject instanceof JAXBElement) {
									JAXBElement contentElement = (JAXBElement) contentObject;
									if (result == null) {
										result = contentElement.getName().getLocalPart();
									}
									else {
										result += " " + contentElement.getName().getLocalPart(); 
									}
								}
							}
							return result;
						}
					}
				}
			}
		}
		return null;
	}
	
	// TODO unsubscribe callback to end subscription on pres-rules
	
	protected NotifyContent getNotifyContent(Subscription subscription) {
		try {	
			ComposedPublication composedPublication = getPublicationChildSbb().getComposedPublication(subscription.getNotifier(), subscription.getSubscriptionKey().getEventPackage());
			if (composedPublication != null) {
				return new NotifyContent(composedPublication.getUnmarshalledContent(),headerFactory.createContentTypeHeader(composedPublication.getContentType(),composedPublication.getContentSubType()));
			}
		}
		catch (Exception e) {
			getLogger().error("failed to get notify content",e);			
		}
		return null;
	}
	
	protected JAXBElement filterContentPerSubscriber(String subscriber, String notifier, String eventPackage, JAXBElement unmarshalledContent) {
		// TODO apply transformations, including polite-block (see pres-rules specs)
		return unmarshalledContent;
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
					":org.mobicents.slee.sippresence.pojo.commonschema");
		} catch (JAXBException e) {
			logger.error("failed to create jaxb context");
			return null;
		}
	}
	
	protected Marshaller getMarshaller() {
		try {
			return jaxbContext.createMarshaller();
		} catch (JAXBException e) {
			getLogger().error("failed to create unmarshaller",e);
			return null;
		}
	}
	
}
