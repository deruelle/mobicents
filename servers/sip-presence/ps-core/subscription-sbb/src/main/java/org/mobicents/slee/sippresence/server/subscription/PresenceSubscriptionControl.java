package org.mobicents.slee.sippresence.server.subscription;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import javax.sip.ServerTransaction;
import javax.sip.message.Response;
import javax.xml.bind.JAXBElement;

import org.apache.log4j.Logger;
import org.mobicents.slee.sipevent.server.publication.pojo.ComposedPublication;
import org.mobicents.slee.sipevent.server.subscription.NotifyContent;
import org.mobicents.slee.sipevent.server.subscription.pojo.Subscription;
import org.mobicents.slee.sipevent.server.subscription.pojo.SubscriptionKey;
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

/**
 * Logic for the sip presence subscription control, which complements the SIP
 * Event generic logic.
 * 
 * @author martins
 * 
 */
public class PresenceSubscriptionControl {

	private static Logger logger = Logger
			.getLogger(PresenceSubscriptionControl.class);
	private static final String[] eventPackages = { "presence" };

	private PresenceSubscriptionControlSbbLocalObject sbb;

	public PresenceSubscriptionControl(
			PresenceSubscriptionControlSbbLocalObject sbb) {
		this.sbb = sbb;
	}

	public static String[] getEventPackages() {
		return eventPackages;
	}

	public void isSubscriberAuthorized(String subscriber,
			String subscriberDisplayName, String notifier, SubscriptionKey key,
			int expires, String content, String contentType,
			String contentSubtype, boolean eventList, String presRulesAUID,
			String presRulesDocumentName, ServerTransaction serverTransaction) {

		// get current combined rule from cmp
		Map combinedRules = sbb.getCombinedRules();
		if (combinedRules == null) {
			combinedRules = new HashMap();
		}
		PresRuleCMPKey cmpKey = new PresRuleCMPKey(subscriber, notifier, key
				.getEventPackage(), key.getRealEventId());
		OMAPresRule combinedRule = (OMAPresRule) combinedRules.get(cmpKey);

		if (combinedRule == null) {
			// rule not found, lookup for another key with same subscriber and
			// notifier
			for (Object keyObject : combinedRules.keySet()) {
				PresRuleCMPKey k = (PresRuleCMPKey) keyObject;
				if (k.getSubscriber().equals(subscriber)
						&& k.getNotifierWithoutParams().equals(cmpKey.getNotifierWithoutParams())) {
					// found compatible key, get rule and assign to this key
					// also
					combinedRule = (OMAPresRule) combinedRules.get(k);
					combinedRules.put(cmpKey, combinedRule);
					sbb.setCombinedRules(combinedRules);
					break;
				}
			}
		}
		if (combinedRule == null) {
			// get pres-rules doc on xdm
			DocumentSelector documentSelector = getDocumentSelector(cmpKey.getNotifierWithoutParams(),
					presRulesAUID, presRulesDocumentName);
			if (documentSelector == null) {
				sbb.getParentSbbCMP().newSubscriptionAuthorization(subscriber,
						subscriberDisplayName, notifier, key, expires,
						Response.FORBIDDEN,eventList,serverTransaction);
			} else {
				// we need to go to the xdm, reply accepted for subscription
				// state be pending
				combinedRules.put(cmpKey, new OMAPresRule());
				sbb.setCombinedRules(combinedRules);
				sbb.getParentSbbCMP().newSubscriptionAuthorization(subscriber,
						subscriberDisplayName, notifier, key, expires,
						Response.ACCEPTED,eventList,serverTransaction);
				// ask for pres-rules doc
				sbb.getXDMClientControlSbb().get(
						new DocumentUriKey(documentSelector),null);
			}
		} else {
			// great, we already had the doc, send response code to the abstract
			// subscription control
			sbb.getParentSbbCMP().newSubscriptionAuthorization(subscriber,
					subscriberDisplayName, notifier, key, expires,
					combinedRule.getSubHandling().getResponseCode(),eventList, serverTransaction);
		}

	}

	public void removingSubscription(Subscription subscription,
			String presRulesAUID, String presRulesDocumentName) {

		if (logger.isDebugEnabled()) {
			logger.debug("removingSubscription(" + subscription + ")");
		}
		// get combined rules cmp
		Map combinedRules = sbb.getCombinedRules();
		if (combinedRules != null) {
			// remove subscription from map
			if (logger.isDebugEnabled()) {
				logger.debug("combined rules: " + combinedRules.keySet());
			}
			PresRuleCMPKey cmpKey = new PresRuleCMPKey(subscription
					.getSubscriber(), subscription.getNotifier(), subscription
					.getKey().getEventPackage(), subscription.getKey()
					.getRealEventId());
			if (combinedRules.remove(cmpKey) != null) {
				if (logger.isDebugEnabled()) {
					logger.debug("removed rule from combined rules map ("
							+ subscription + ")");
				}
				// check if subscription to pres-rules still needed
				boolean subscriptionNeeded = false;
				for (Object k : combinedRules.keySet()) {
					PresRuleCMPKey presRuleCMPKey = (PresRuleCMPKey) k;
					if (presRuleCMPKey.getNotifierWithoutParams().equals(
							cmpKey.getNotifierWithoutParams())
							&& presRuleCMPKey.getEventPackage().equals(
									subscription.getKey().getEventPackage())) {
						if (logger.isDebugEnabled()) {
							logger.debug("subscription needed due to rule "
									+ presRuleCMPKey);
						}
						subscriptionNeeded = true;
						break;
					}
				}
				if (!subscriptionNeeded) {
					if (logger.isDebugEnabled()) {
						logger.debug("subscription not needed");
					}
					DocumentSelector documentSelector = getDocumentSelector(
							cmpKey.getNotifierWithoutParams(), presRulesAUID,
							presRulesDocumentName);
					sbb.getXDMClientControlSbb().unsubscribeDocument(
							documentSelector);
				}
			}
		}
	}

	/**
	 * async get response from xdm client
	 */
	public void getResponse(XcapUriKey key, int responseCode, String mimetype,
			String content) {

		DocumentSelector documentSelector = null;
		if (key instanceof DocumentUriKey) {
			documentSelector = ((DocumentUriKey) key).getDocumentSelector();
		} else if (key instanceof AttributeUriKey) {
			documentSelector = ((AttributeUriKey) key).getDocumentSelector();
		} else if (key instanceof ElementUriKey) {
			documentSelector = ((ElementUriKey) key).getDocumentSelector();
		} else {
			try {
				documentSelector = Parser.parseDocumentSelector(key
						.getResourceSelector().getDocumentSelector());
			} catch (ParseException e) {
				// won't happen
				logger
						.error(
								"bug, a xcap uri key document selector string could not be parsed",
								e);
			}
		}

		if (responseCode == 200) {
			// just simulate a document update
			documentUpdated(documentSelector, null, null, content);
		} else {
			// let's be friendly with clients without xcap, allow subscription
			String notifierWithoutParams = getUser(documentSelector);
			if (logger.isInfoEnabled()) {
				logger.info(notifierWithoutParams+ " pres-rules not found, allowing subscription");
			}
			Map combinedRules = sbb.getCombinedRules();
			if (combinedRules != null) {
				for (Object object : combinedRules.keySet()) {
					PresRuleCMPKey cmpKey = (PresRuleCMPKey) object;
					if (cmpKey.getNotifierWithoutParams().equals(notifierWithoutParams)) {
						OMAPresRule combinedRule = (OMAPresRule) combinedRules
								.get(cmpKey);
						combinedRule.setProvideAllDevices(true);
						combinedRule.setProvideAllAttributes(true);
						combinedRule.setProvideAllPersons(true);
						combinedRule.setProvideAllServices(true);
						combinedRule.setSubHandling(SubHandlingAction.allow);
						// notify auth changed
						sbb.getParentSbbCMP()
								.authorizationChanged(
										cmpKey.getSubscriber(),
										cmpKey.getNotifier(),
										"presence",
										cmpKey.getEventId(),
										combinedRule.getSubHandling()
												.getResponseCode());
					}
				}
				sbb.setCombinedRules(combinedRules);
			}
		}

		// subscribe to changes in this pres-rules doc
		sbb.getXDMClientControlSbb().subscribeDocument(documentSelector);

	}

	public void documentUpdated(DocumentSelector documentSelector,
			String oldETag, String newETag, String documentAsString) {

		if (!documentSelector.getAUID().equals(
				"org.openmobilealliance.pres-rules")
				&& !documentSelector.getAUID().equals("pres-rules")) {
			// not a pres-rules doc, maybe it's coming here because of
			// integrated servers using xdm client
			return;
		}

		String notifierWithoutParams = getUser(documentSelector);

		// unmarshall doc
		Ruleset ruleset = unmarshallRuleset(documentAsString);
		if (ruleset == null) {
			logger
					.error("rcvd ruleset update from xdm client but unmarshalling of ruleset failed, ignoring update");
			return;
		}

		// get current combined rules from cmp
		Map combinedRules = sbb.getCombinedRules();
		if (combinedRules == null) {
			combinedRules = new HashMap();
		}
		// for each combined rules that has the user that updated the doc as
		// notifier reprocess the rules
		for (Object key : combinedRules.keySet()) {
			PresRuleCMPKey cmpKey = (PresRuleCMPKey) key;
			if (cmpKey.getNotifierWithoutParams().equals(notifierWithoutParams)) {
				OMAPresRule oldCombinedRule = (OMAPresRule) combinedRules
						.get(cmpKey);
				RulesetProcessor rulesetProcessor = new RulesetProcessor(cmpKey
						.getSubscriber(), notifierWithoutParams, ruleset, sbb);
				OMAPresRule newCombinedRule = rulesetProcessor
						.getCombinedRule();
				combinedRules.put(cmpKey, newCombinedRule);
				// check permission changed
				if (oldCombinedRule.getSubHandling().getResponseCode() != newCombinedRule
						.getSubHandling().getResponseCode()) {
					sbb.getParentSbbCMP().authorizationChanged(
							cmpKey.getSubscriber(), cmpKey.getNotifier(), "presence",
							cmpKey.getEventId(),
							newCombinedRule.getSubHandling().getResponseCode());
				}
			}
		}

		sbb.setCombinedRules(combinedRules);

	}

	/**
	 * interface used by rules processor to get sphere for a notifier
	 */
	public String getSphere(String notifier) {

		// get ridden of notifier uri params, if any
		String notifierWithoutParams = notifier.split(";")[0];
		
		ComposedPublication composedPublication = sbb.getPublicationChildSbb()
				.getComposedPublication(notifierWithoutParams, "presence");
		if (composedPublication != null
				&& composedPublication.getUnmarshalledContent().getValue() instanceof Presence) {
			Presence presence = (Presence) composedPublication
					.getUnmarshalledContent().getValue();
			for (Object anyObject : presence.getAny()) {
				JAXBElement anyElement = (JAXBElement) anyObject;
				if (anyElement.getValue() instanceof Person) {
					Person person = (Person) anyElement.getValue();
					for (Object anotherAnyObject : person.getAny()) {
						JAXBElement anotherAnyElement = (JAXBElement) anotherAnyObject;
						if (anotherAnyElement.getValue() instanceof Sphere) {
							Sphere sphere = ((Sphere) anotherAnyElement
									.getValue());
							String result = null;
							for (Object contentObject : sphere.getContent()) {
								if (contentObject instanceof String) {
									if (result == null) {
										result = (String) contentObject;
									} else {
										result += " " + (String) contentObject;
									}
								} else if (contentObject instanceof JAXBElement) {
									JAXBElement contentElement = (JAXBElement) contentObject;
									if (result == null) {
										result = contentElement.getName()
												.getLocalPart();
									} else {
										result += " "
												+ contentElement.getName()
														.getLocalPart();
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

	public NotifyContent getNotifyContent(Subscription subscription) {
		try {
			
			ComposedPublication composedPublication = sbb
					.getPublicationChildSbb().getComposedPublication(
							subscription.getNotifier(),
							subscription.getKey().getEventPackage());
			if (composedPublication != null) {
				return new NotifyContent(composedPublication
						.getUnmarshalledContent(), sbb.getHeaderFactory()
						.createContentTypeHeader(
								composedPublication.getContentType(),
								composedPublication.getContentSubType()));
			}
		} catch (Exception e) {
			logger.error("failed to get notify content", e);
		}
		return null;
	}

	public Object filterContentPerSubscriber(String subscriber,
			String notifier, String eventPackage, Object unmarshalledContent) {
		
		// get ridden of notifier uri params, if any
		//String notifier = subscription.getNotifier().split(";")[0];
		
		// TODO apply transformations, including polite-block (see pres-rules
		// specs)
		return unmarshalledContent;
	}

	private Ruleset unmarshallRuleset(String documentAsString) {
		// unmarshall doc
		StringReader stringReader = new StringReader(documentAsString);
		try {
			return (Ruleset) sbb.getUnmarshaller().unmarshal(stringReader);
		} catch (Exception e) {
			logger.error("unmarshalling of ruleset failed", e);
			return null;
		} finally {
			stringReader.close();
		}
	}

	// --------- AUX

	/**
	 * from a user return the document selector pointing to it's pres-rules
	 * 
	 * @param user
	 * @return
	 */
	private DocumentSelector getDocumentSelector(String user,
			String presRulesAUID, String presRulesDocumentName) {
		return new DocumentSelector(presRulesAUID, "users/" + user,
				presRulesDocumentName);
	}

	/**
	 * from a document selector point to a pres-rules doc return the user
	 * @param documentSelector
	 * @return
	 */
	private String getUser(DocumentSelector documentSelector) {
		return documentSelector.getDocumentParent()
				.substring("users/".length());
	}

}
