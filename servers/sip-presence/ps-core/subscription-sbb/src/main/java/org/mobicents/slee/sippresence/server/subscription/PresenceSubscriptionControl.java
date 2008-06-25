package org.mobicents.slee.sippresence.server.subscription;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import javax.sip.RequestEvent;
import javax.sip.message.Response;
import javax.xml.bind.JAXBElement;

import org.apache.log4j.Logger;
import org.mobicents.slee.sipevent.server.publication.pojo.ComposedPublication;
import org.mobicents.slee.sipevent.server.subscription.NotifyContent;
import org.mobicents.slee.sipevent.server.subscription.pojo.Subscription;
import org.mobicents.slee.sippresence.pojo.datamodel.Person;
import org.mobicents.slee.sippresence.pojo.pidf.Presence;
import org.mobicents.slee.sippresence.pojo.rpid.Sphere;
import org.mobicents.slee.sippresence.server.subscription.rules.OMAPresRule;
import org.mobicents.slee.sippresence.server.subscription.rules.PresRuleCMPKey;
import org.mobicents.slee.sippresence.server.subscription.rules.RulesetProcessor;
import org.mobicents.slee.sippresence.server.subscription.rules.SubHandlingAction;
import org.openxdm.xcap.client.appusage.presrules.jaxb.commonpolicy.Ruleset;
import org.openxdm.xcap.common.key.AttributeUriKey;
import org.openxdm.xcap.common.key.DocumentUriKey;
import org.openxdm.xcap.common.key.ElementUriKey;
import org.openxdm.xcap.common.key.XcapUriKey;
import org.openxdm.xcap.common.uri.DocumentSelector;
import org.openxdm.xcap.common.uri.ParseException;
import org.openxdm.xcap.common.uri.Parser;

public class PresenceSubscriptionControl {

	private static Logger logger = Logger.getLogger(PresenceSubscriptionControl.class);
	private static final String[] eventPackages = {"presence"};
	private static final boolean clientUAIsEyebeam = true;	
	
	public static String[] getEventPackages() {
		return eventPackages;
	}
	
	public static void isSubscriberAuthorized(PresenceSubscriptionControlSbbLocalObject sbb, RequestEvent event, String subscriber,
			String notifier, String eventPackage, String eventId, int expires) {
		
		// get current combined rule from cmp
		Map combinedRules = sbb.getCombinedRules();
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
					sbb.setCombinedRules(combinedRules);
					break;
				}
			}
		}
		if (combinedRule == null) {
			// get pres-rules doc on xdm (doc path depends on user agent) FIXME use user agent
			DocumentSelector documentSelector = getDocumentSelector(notifier);
			if (documentSelector == null) {
				sbb.newSubscriptionAuthorization(event, subscriber, notifier, eventPackage, eventId, expires, Response.FORBIDDEN);
			}
			else {
				// we need to go to the xdm, reply accepted for subscription state be pending
				combinedRules.put(cmpKey, new OMAPresRule());
				sbb.setCombinedRules(combinedRules);
				sbb.newSubscriptionAuthorization(event, subscriber, notifier, eventPackage, eventId, expires, Response.ACCEPTED);
				// ask for pres-rules doc
				sbb.getXDMChildSbb().get(new DocumentUriKey(documentSelector));
			}
		}
		else {
			// great, we already had the doc, send response code to the abstract subscription control
			sbb.newSubscriptionAuthorization(event, subscriber, notifier, eventPackage, eventId, expires, combinedRule.getSubHandling().getResponseCode());
		}
	
	}
	
	public static void removingSubscription(PresenceSubscriptionControlSbbLocalObject sbb, Subscription subscription) {
		// get combined rules cmp
		Map combinedRules = sbb.getCombinedRules();
		if (combinedRules != null) {
			// remove subscription from map
			if(combinedRules.remove(new PresRuleCMPKey(subscription.getSubscriber(),subscription.getNotifier(),subscription.getSubscriptionKey().getEventPackage(),subscription.getSubscriptionKey().getRealEventId())) != null) {
				// check if subscription to pres-rules still needed
				boolean subscriptionNeeded = false;
				for(Object k:combinedRules.keySet()) {
					PresRuleCMPKey presRuleCMPKey = (PresRuleCMPKey)k;
					if (presRuleCMPKey.getNotifier().equals(subscription.getNotifier()) && presRuleCMPKey.getEventPackage().equals(subscription.getSubscriptionKey().getEventPackage())) {
						subscriptionNeeded = true;
						break;
					}
				}
				if (!subscriptionNeeded) {
					DocumentSelector documentSelector = getDocumentSelector(subscription.getNotifier());
					sbb.getXDMChildSbb().unsubscribeDocument(documentSelector);
				}
			}
		}
	}
	
	/**
	 * async get response from xdm client
	 */
	public static void getResponse(PresenceSubscriptionControlSbbLocalObject sbb,XcapUriKey key, int responseCode, String mimetype,
		String content) {
		
		DocumentSelector documentSelector = null;
		if (key instanceof DocumentUriKey) {
			documentSelector = ((DocumentUriKey)key).getDocumentSelector();
		}
		else if (key instanceof AttributeUriKey) {
			documentSelector = ((AttributeUriKey)key).getDocumentSelector();
		}
		else if (key instanceof ElementUriKey) {
			documentSelector = ((ElementUriKey)key).getDocumentSelector();
		}	
		else {
			try {
				documentSelector = Parser.parseDocumentSelector(key.getResourceSelector().getDocumentSelector());
			} catch (ParseException e) {
				// won't happen
				logger.error("something could not happen, a xcap uri key document selector string could not be parsed", e);
			}
		}
		
		if (responseCode == 200) {
			// just simulate a document update
			documentUpdated(sbb,documentSelector,null,null,content);			
		}
		else {
			logger.warn("did not found pres-rules in doc at "+key.getResourceSelector());
			// let's be friendly with clients without xcap, allow subscription
			String notifier = getUser(documentSelector);
			Map combinedRules = sbb.getCombinedRules();
			if (combinedRules != null) {
				for (Object object:combinedRules.keySet()) {
					PresRuleCMPKey cmpKey = (PresRuleCMPKey) object;
					if (cmpKey.getNotifier().equals(notifier)) {
						OMAPresRule combinedRule = (OMAPresRule)combinedRules.get(cmpKey);
						combinedRule.setProvideAllDevices(true);
						combinedRule.setProvideAllAttributes(true);
						combinedRule.setProvideAllPersons(true);
						combinedRule.setProvideAllServices(true);
						combinedRule.setSubHandling(SubHandlingAction.allow);
						// notify auth changed
						sbb.authorizationChanged(cmpKey.getSubscriber(),notifier,"presence",combinedRule.getSubHandling().getResponseCode());
					}
				}
				sbb.setCombinedRules(combinedRules);
			}			
		}
		// subscribe to changes in this pres-rules doc
		sbb.getXDMChildSbb().subscribeDocument(documentSelector);
	}
	
	public static void documentUpdated(PresenceSubscriptionControlSbbLocalObject sbb, DocumentSelector documentSelector,String oldETag,String newETag,String documentAsString) {
		
		if (!documentSelector.getAUID().equals("org.openmobilealliance.pres-rules") && !documentSelector.getAUID().equals("pres-rules")) {
			// not a pres-rules doc, maybe it's coming here because of integrated servers using xdm client
			return;
		}
		
		String notifier = getUser(documentSelector);	
		
		// unmarshall doc
		Ruleset ruleset = unmarshallRuleset(sbb,documentAsString);
		if (ruleset == null) {
			logger.error("rcvd ruleset update from xdm client but unmarshalling of ruleset failed, ignoring update");
			return;
		}
		
		// get current combined rules from cmp
		Map combinedRules = sbb.getCombinedRules();
		if (combinedRules == null) {
			combinedRules = new HashMap();
		}
		// for each combined rules that has the user that updated the doc as notifier reprocess the rules
		for (Object key:combinedRules.keySet()) {
			PresRuleCMPKey cmpKey = (PresRuleCMPKey) key;
			if (cmpKey.getNotifier().equals(notifier)) {
				OMAPresRule oldCombinedRule = (OMAPresRule)combinedRules.get(cmpKey);
				RulesetProcessor rulesetProcessor = new RulesetProcessor(cmpKey.getSubscriber(),notifier,ruleset,sbb);
				OMAPresRule newCombinedRule = rulesetProcessor.getCombinedRule();
				combinedRules.put(cmpKey, newCombinedRule);
				// check permission changed
				if (oldCombinedRule.getSubHandling().getResponseCode() != newCombinedRule.getSubHandling().getResponseCode()) {
					sbb.authorizationChanged(cmpKey.getSubscriber(), notifier, "presence",newCombinedRule.getSubHandling().getResponseCode());
				}
			}
		}

		sbb.setCombinedRules(combinedRules);
		
	}
	
	/**
	 * interface used by rules processor to get sphere for a notifier
	 */
	public static String getSphere(PresenceSubscriptionControlSbbLocalObject sbb,String notifier) {
		ComposedPublication composedPublication = sbb.getPublicationChildSbb().getComposedPublication(notifier, "presence");
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
	
	public static NotifyContent getNotifyContent(PresenceSubscriptionControlSbbLocalObject sbb, Subscription subscription) {
		try {	
			ComposedPublication composedPublication = sbb.getPublicationChildSbb().getComposedPublication(subscription.getNotifier(), subscription.getSubscriptionKey().getEventPackage());
			if (composedPublication != null) {
				return new NotifyContent(composedPublication.getUnmarshalledContent(),sbb.getHeaderFactory().createContentTypeHeader(composedPublication.getContentType(),composedPublication.getContentSubType()));
			}
		}
		catch (Exception e) {
			logger.error("failed to get notify content",e);			
		}
		return null;
	}
	
	public static Object filterContentPerSubscriber(PresenceSubscriptionControlSbbLocalObject sbb,String subscriber, String notifier, String eventPackage, Object unmarshalledContent) {
		// TODO apply transformations, including polite-block (see pres-rules specs)
		return unmarshalledContent;
	}
	
	private static Ruleset unmarshallRuleset(PresenceSubscriptionControlSbbLocalObject sbb,String documentAsString) {
		// unmarshall doc
		StringReader stringReader = new StringReader(documentAsString);
		try {
			return (Ruleset) sbb.getUnmarshaller().unmarshal(stringReader);
		} catch (Exception e) {
			logger.error("unmarshalling of ruleset failed",e);
			return null;
		}
		finally {
			stringReader.close();
		}
	}
	
	// --------- AUX
	
	/**
	 * from a user return the document selector pointing to it's pres-rules
	 * @param user
	 * @return
	 */
	private static DocumentSelector getDocumentSelector(String user) {
		
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
	private static String getUser(DocumentSelector documentSelector) {
		
		if(clientUAIsEyebeam) {
			return "sip:" + documentSelector.getDocumentParent().substring("users/".length()) + "@"+System.getProperty("bind.address","127.0.0.1");
		}
		else {
			return documentSelector.getDocumentParent().substring("users/".length());
		}
	}
	
	
}
