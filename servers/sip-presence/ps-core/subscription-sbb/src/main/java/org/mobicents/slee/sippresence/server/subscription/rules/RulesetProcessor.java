package org.mobicents.slee.sippresence.server.subscription.rules;

import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.openxdm.xcap.client.appusage.presrules.jaxb.ProvideAllAttributes;
import org.openxdm.xcap.client.appusage.presrules.jaxb.ProvideDevicePermission;
import org.openxdm.xcap.client.appusage.presrules.jaxb.ProvidePersonPermission;
import org.openxdm.xcap.client.appusage.presrules.jaxb.ProvideServicePermission;
import org.openxdm.xcap.client.appusage.presrules.jaxb.UnknownBooleanPermission;
import org.openxdm.xcap.client.appusage.presrules.jaxb.commonpolicy.ActionsType;
import org.openxdm.xcap.client.appusage.presrules.jaxb.commonpolicy.ExceptType;
import org.openxdm.xcap.client.appusage.presrules.jaxb.commonpolicy.IdentityType;
import org.openxdm.xcap.client.appusage.presrules.jaxb.commonpolicy.ManyType;
import org.openxdm.xcap.client.appusage.presrules.jaxb.commonpolicy.OneType;
import org.openxdm.xcap.client.appusage.presrules.jaxb.commonpolicy.RuleType;
import org.openxdm.xcap.client.appusage.presrules.jaxb.commonpolicy.Ruleset;
import org.openxdm.xcap.client.appusage.presrules.jaxb.commonpolicy.SphereType;
import org.openxdm.xcap.client.appusage.presrules.jaxb.commonpolicy.TransformationsType;
import org.openxdm.xcap.client.appusage.presrules.jaxb.commonpolicy.ValidityType;

public class RulesetProcessor {
	
	private String subscriber;
	private String notifier;
	private PublishedSphereSource publishedSphereSource;
	private OMAPresRule combinedRule;
	
	public RulesetProcessor(String subscriber, String notifier, Ruleset ruleset, PublishedSphereSource publishedSphereSource) {
		this.subscriber = subscriber;
		this.notifier = notifier;
		this.publishedSphereSource = publishedSphereSource;
		processRuleset(ruleset);
	}
	
	public String getSubscriber() {
		return subscriber;
	}
		
	public OMAPresRule getCombinedRule() {
		return combinedRule;
	}
	
	// ----------------------------- RULE SET PROCESSING ---------------
	
	private void processRuleset(Ruleset ruleset) {
		
		for (RuleType ruleType: ruleset.getRule()) {
			// process actions
			SubHandlingAction subHandling = processActions(ruleType);		
			if (subHandling == null) {
				// continue, this rule has nothing to do with pres-rules auth
				continue;
			}
			// process conditions
			boolean permissionGranted = processConditions(ruleType);
			if (permissionGranted) {
				// process transformations
				OMAPresRule omaPresRule = processTransformations(ruleType);
				if (omaPresRule != null) {
					// set sub-handling
					omaPresRule.setSubHandling(subHandling);
					// combine rule
					if (combinedRule == null) {
						combinedRule = omaPresRule;
					}
					else {
						combinedRule.combine(omaPresRule);
					}
				}
			}	
		}		
		if(combinedRule == null) {
			combinedRule = new OMAPresRule();
		}
	}

	/**
	 * process rule's actions 
	 * @param ruleType
	 * @return the sub-handling value found
	 */
	private SubHandlingAction processActions(RuleType ruleType) {
		ActionsType actionsType = ruleType.getActions();
		if (actionsType != null) {
			List anys = actionsType.getAny();
			if(anys.size() == 1 && anys.get(0) instanceof JAXBElement) {
				JAXBElement element = (JAXBElement) anys.get(0);
				if (element.getName().getLocalPart().equals("sub-handling")) {
					String subHandlingValue = (String) element.getValue();
					// 0 is block, 10 is confirm, 20 is polite-block (not supported yet), 30 is allow
					if (subHandlingValue.equals("allow") || subHandlingValue.equals("30")) {
						return SubHandlingAction.allow;							
					}
					else if (subHandlingValue.equals("polite-block") || subHandlingValue.equals("20")) {
						return SubHandlingAction.politeblock;
					}
					else if (subHandlingValue.equals("confirm") || subHandlingValue.equals("10")) {
						return SubHandlingAction.confirm;
					}
					else if (subHandlingValue.equals("block") || subHandlingValue.equals("0")) {
						return SubHandlingAction.block;
					}
				}
			}
		}
		return null;
	}
	
	/**
	 * process rules's conditions
	 * @param ruleType
	 * @return true if permission is granted to apply actions and transformations of the rule
	 */
	private boolean processConditions(RuleType ruleType) {

		String subscriberDomain = null;
		
		// all conditions must evaluate to true
		List identityOrSphereOrValidityObjectList = ruleType.getConditions().getIdentityOrSphereOrValidity();
		if (identityOrSphereOrValidityObjectList.isEmpty()) {
			return false;
		}
		else {

			for (Object identityOrSphereOrValidityObject : identityOrSphereOrValidityObjectList) {
				if (identityOrSphereOrValidityObject instanceof JAXBElement) {
					JAXBElement identityOrSphereOrValidity = (JAXBElement) identityOrSphereOrValidityObject;

					if (identityOrSphereOrValidity.getValue() instanceof IdentityType) {
						IdentityType identityType = (IdentityType) identityOrSphereOrValidity.getValue();
						// identity permission is granted if one sub-clause matches subscriber
						boolean idPermission = false;
						for (Object oneOrManyOrAnyObject : identityType.getOneOrManyOrAny()) {
							JAXBElement oneOrManyOrAny = (JAXBElement) oneOrManyOrAnyObject;

							if (oneOrManyOrAny.getValue() instanceof OneType) {
								OneType oneType = (OneType) oneOrManyOrAny.getValue();
								if (subscriber.equals(oneType.getId())) {
									// found identity of subscriber											
									idPermission = true;
									break;
								}
							}

							else if (oneOrManyOrAny.getValue() instanceof ManyType) {
								ManyType manyType = (ManyType) oneOrManyOrAny.getValue();
								if (subscriberDomain == null) {
									int i = subscriber.indexOf('@');
									if (i>0) {
										subscriberDomain = subscriber.substring(i+1);
									}
								}
								if (manyType.getDomain() == null || domainsMatch(subscriberDomain,manyType.getDomain())) {
									boolean exceptNotFound = true;
									for(Object exceptOrAnyObject: manyType.getExceptOrAny()) {
										JAXBElement exceptOrAny = (JAXBElement) exceptOrAnyObject;
										if (exceptOrAny.getValue() instanceof ExceptType) {
											ExceptType exceptType = (ExceptType) exceptOrAny.getValue();
											if (subscriber.equals(exceptType.getId())) {
												// found subscriber as exception in domain
												exceptNotFound = false;
												break;
											}
											else if (domainsMatch(subscriberDomain, exceptType.getDomain())) {
												// found subscriber domain as exception in domain
												exceptNotFound = false;
												break;
											}
										}
									}							
									if (exceptNotFound) {
										idPermission = true;
										break;
									}
								}
							}							
						}
						if (!idPermission) {
							return false;
						}
					}

					else if (identityOrSphereOrValidity.getValue() instanceof SphereType) {
						SphereType sphereType = (SphereType) identityOrSphereOrValidity.getValue();
						// we need current sphere published by notifier
						String sphere = publishedSphereSource.getSphere(notifier);
						if (sphereType.getValue() == null || spheresMatch(sphere, sphereType.getValue())) {
							return false;
						}
					}

					else if (identityOrSphereOrValidity.getValue() instanceof ValidityType) {
						ValidityType validityType = (ValidityType) identityOrSphereOrValidity.getValue();
						/*
						 * The <validity> element is the third condition element
						 * specified in this document. It expresses the rule
						 * validity period by two attributes, a starting and an
						 * ending time. The validity condition is TRUE if the
						 * current time is greater than or equal to at least one
						 * <from> child, but less than the <until> child after it.
						 * This represents a logical OR operation across each <from>
						 * and <until> pair. Times are expressed in XML dateTime
						 * format.
						 */
						if (validityType.getFromAndUntil().size()%2 == 0) {
							// verified we have a pair number of subclauses
						}
						boolean valid = false;
						XMLGregorianCalendar calendar = null;
						try {
							calendar = DatatypeFactory.newInstance()
							.newXMLGregorianCalendar(new GregorianCalendar());
						} catch (DatatypeConfigurationException e) {
							System.err.println("Failed to create calendar to verify pres-rules condition validity");
							e.printStackTrace();
						}
						if (calendar != null) {
							for (Iterator<JAXBElement<XMLGregorianCalendar>> iterator = validityType.getFromAndUntil().iterator(); iterator.hasNext();) {
								JAXBElement<XMLGregorianCalendar> fromElement = iterator.next();
								JAXBElement<XMLGregorianCalendar> untilElement = iterator.next();
								if (fromElement.getName().getLocalPart().equals("from") && untilElement.getName().getLocalPart().equals("until")) {
									if (fromElement.getValue().compare(calendar) < 1 && untilElement.getValue().compare(calendar) > -1) {
										valid = true;
										break;
									}
								}
								else {
									// invalid condition, fail
									valid = false;
									break;
								}
							}
						}
						if (!valid) {
							return false;
						}
					}
				}
			}
			// no conditions evaluated to false;
			return true;
		}
	}

	/**
	 * process rule's transformations
	 * 
	 * @param ruleType
	 * @return a {@link OMAPresRule} filled with transformations
	 */
	private OMAPresRule processTransformations(RuleType ruleType) {
		
		// conditions applies to subscription, let's get rule transformations
		TransformationsType transformationsType = ruleType.getTransformations();
		if (transformationsType != null) {
			// create rule
			OMAPresRule rule = new OMAPresRule();
			// fill transformations
			for(Object transformationObject: transformationsType.getAny()) {

				if(transformationObject instanceof ProvideAllAttributes) {
					rule.setProvideAllAttributes(true);
				}
				/*
				else if(transformationObject instanceof ProvideDevicePermission) {
					rule.processDevicePermission((ProvideDevicePermission)transformationObject);
				}
				else if(transformationObject instanceof ProvidePersonPermission) {
					rule.processPersonPermission((ProvidePersonPermission) transformationObject);
				}
				else if(transformationObject instanceof ProvideServicePermission) {
					rule.processServicePermission((ProvideServicePermission) transformationObject);
				}
				else if(transformationObject instanceof UnknownBooleanPermission) {
					UnknownBooleanPermission unknownBooleanPermission = (UnknownBooleanPermission) transformationObject;
					rule.getUnknownBooleanAttributes().add(new UnknownBooleanAttributeTransformation(unknownBooleanPermission.getName(),unknownBooleanPermission.getNs()));
				}
				*/
				else if(transformationObject instanceof JAXBElement) {

					JAXBElement element = (JAXBElement) transformationObject;

					if (element.getName().getNamespaceURI().equals("urn:oma:xml:prs:pres-rules")) {
						// oma transformations
						if (element.getName().getLocalPart().equals("provide-registration-state")) {
							rule.setProvideRegistrationState(((Boolean)element.getValue()).booleanValue());
						}
						else if (element.getName().getLocalPart().equals("provide-network-availability")) {
							rule.setProvideNetworkAvailability(((Boolean)element.getValue()).booleanValue());
						}
						else if (element.getName().getLocalPart().equals("provide-willingness")) {
							rule.setProvideWillingness(((Boolean)element.getValue()).booleanValue());
						}
						else if (element.getName().getLocalPart().equals("provide-barring-state")) {
							rule.setProvideBarringState(((Boolean)element.getValue()).booleanValue());
						}
						else if (element.getName().getLocalPart().equals("provide-session-participation")) {
							rule.setProvideSessionParticipation(((Boolean)element.getValue()).booleanValue());
						}
						else if (element.getName().getLocalPart().equals("service-id")) {
							rule.getServiceIDs().add((String)element.getValue());
						}
						else if (element.getName().getLocalPart().equals("provide-geopriv")) {
							try {
								rule.setProvideGeopriv(GeoPrivTransformation.valueOf((String)element.getValue()));
							}
							catch (Exception e) {
								System.err.println("Failed to parse provide-geopriv value in transformation");
								e.printStackTrace();
							}
						}
						// unknown transformation, ignore
					}
					else if (element.getName().getNamespaceURI().equals("urn:ietf:params:xml:ns:pres-rules")) {
						// ietf transformations
						if(element.getName().getLocalPart().equals("provide-devices")) {
							rule.processDevicePermission((ProvideDevicePermission)element.getValue());
						}
						else if(element.getName().getLocalPart().equals("provide-persons")) {
							rule.processPersonPermission((ProvidePersonPermission)element.getValue());
						}
						else if(element.getName().getLocalPart().equals("provide-services")) {
							rule.processServicePermission((ProvideServicePermission)element.getValue());
						}
						else if(element.getName().getLocalPart().equals("provide-unknown-attribute")) {
							UnknownBooleanPermission unknownBooleanPermission = (UnknownBooleanPermission) element.getValue();
							rule.getUnknownBooleanAttributes().add(new UnknownBooleanAttributeTransformation(unknownBooleanPermission.getName(),unknownBooleanPermission.getNs()));
						}
						else if (element.getName().getLocalPart().equals("provide-place-is")) {
							rule.setProvidePlaceIs(((Boolean)element.getValue()).booleanValue());
						}
						else if (element.getName().getLocalPart().equals("provide-privacy")) {
							rule.setProvidePrivacy(((Boolean)element.getValue()).booleanValue());
						}
						else if (element.getName().getLocalPart().equals("provide-class")) {
							rule.setProvideClass(((Boolean)element.getValue()).booleanValue());
						}
						else if (element.getName().getLocalPart().equals("provide-place-type")) {
							rule.setProvidePlaceType(((Boolean)element.getValue()).booleanValue());
						}
						else if (element.getName().getLocalPart().equals("provide-relationship")) {
							rule.setProvideRelationship(((Boolean)element.getValue()).booleanValue());
						}
						else if (element.getName().getLocalPart().equals("provide-mood")) {
							rule.setProvideMood(((Boolean)element.getValue()).booleanValue());
						}
						else if (element.getName().getLocalPart().equals("provide-activities")) {
							rule.setProvideActivities(((Boolean)element.getValue()).booleanValue());
						}
						else if (element.getName().getLocalPart().equals("provide-sphere")) {
							rule.setProvideSphere(((Boolean)element.getValue()).booleanValue());
						}
						else if (element.getName().getLocalPart().equals("provide-user-input")) {
							try {
								rule.setProvideUserInput(UserInputTransformation.valueOf((String)element.getValue()));
							}
							catch (Exception e) {
								System.err.println("Failed to parse provide-user-input value in transformation");
								e.printStackTrace();
							}
						}
						else if (element.getName().getLocalPart().equals("provide-time-offset")) {
							rule.setProvideTimeOffset(((Boolean)element.getValue()).booleanValue());
						}
						else if (element.getName().getLocalPart().equals("provide-note")) {
							rule.setProvideNote(((Boolean)element.getValue()).booleanValue());
						}
						else if (element.getName().getLocalPart().equals("provide-deviceID")) {
							rule.setProvideDeviceID(((Boolean)element.getValue()).booleanValue());
						}
						else if (element.getName().getLocalPart().equals("provide-status-icon")) {
							rule.setProvideStatusIcon(((Boolean)element.getValue()).booleanValue());
						}
						// else unknown transformation, ignore
					}
					// else unknown transformation, ignore
				}
				// else unknown transformation, ignore
			}
			return rule;
		}
		else {
			// no transformations
			return null;
		}
	}	
	
	private boolean domainsMatch(String domain,String conditionDomain) {
		// conditionDomain can be null
		// FIXME proper domain matching
		/*
		 * Common policy MUST either use UTF-8 or UTF-16 to store domain names
		 * in the 'domain' attribute. For non-IDNs (Internationalized Domain
		 * Names), lowercase ASCII SHOULD be used. For the comparison operation
		 * between the value stored in the 'domain' attribute and the domain
		 * value provided via the using protocol (referred to as "protocol
		 * domain identifier"), the following rules are applicable:
		 * 
		 * 1. Translate percent-encoding for either string.
		 * 
		 * 2. Convert both domain strings using the ToASCII operation described
		 * in RFC 3490 [3].
		 * 
		 * 3. Compare the two domain strings for ASCII equality, for each label.
		 * If the string comparison for each label indicates equality, the
		 * comparison succeeds. Otherwise, the domains are not equal.
		 * 
		 * If the conversion fails in step (2), the domains are not equal.
		 * 
		 */
		return domain.equals(conditionDomain);
	}
	
	private boolean spheresMatch(String sphere, String conditionSphere) {
		// sphere can be null
		/*
		 * The <sphere> element belongs to the group of condition elements. It
		 * can be used to indicate a state (e.g., 'work', 'home', 'meeting',
		 * 'travel') the PT is currently in. A sphere condition matches only if
		 * the PT is currently in the state indicated. The state may be conveyed
		 * by manual configuration or by some protocol. For example, RPID [10]
		 * provides the ability to inform the PS of its current sphere. The
		 * application domain needs to describe in more detail how the sphere
		 * state is determined. Switching from one sphere to another causes a
		 * switch between different modes of visibility. As a result, different
		 * subsets of rules might be applicable.
		 * 
		 * The content of the 'value' attribute of the <sphere> element MAY
		 * contain more than one token. The individual tokens MUST be separated
		 * by a blank character. A logical OR is used for the matching the
		 * tokens against the sphere settings of the PT. As an example, if the
		 * content of the 'value' attribute in the sphere attribute contains two
		 * tokens 'work' and 'home' then this part of the rule matches if the
		 * sphere for a particular PT is either 'work' OR 'home'. To compare the
		 * content of the 'value' attribute in the <sphere> element with the
		 * stored state information about the PT's sphere setting a
		 * case-insensitive string comparison MUST be used for each individual
		 * token. There is neither a registry for these values nor a language-
		 * specific indication of the sphere content. As such, the tokens are
		 * treated as opaque strings.
		 * 
		 */
		String[] conditionSphereTokens = conditionSphere.split(" ");
		for (String conditionSphereToken : conditionSphereTokens){
			if (conditionSphereToken.equalsIgnoreCase(sphere)) {
				return true;
			}
		}
		return false;
	}
}
