package org.mobicents.slee.sippresence.server.subscription.rules;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.JAXBElement;

import org.openxdm.xcap.client.appusage.presrules.jaxb.ProvideDevicePermission;
import org.openxdm.xcap.client.appusage.presrules.jaxb.ProvidePersonPermission;
import org.openxdm.xcap.client.appusage.presrules.jaxb.ProvideServicePermission;

/**
 * Pres-rules object for applying transformations.
 * 
 * @author emmartins
 *
 */
public class PresRule implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2821668554015646242L;

	private SubHandlingAction subHandling = SubHandlingAction.confirm;
	
	// ------------ provide all devices, if true override all values in "provide devices"
	private boolean provideAllDevices;
	// ------------ provide devices
	private Set<String> provideDeviceClasses;
	private Set<String> provideDeviceOccurenceIds;
	private Set<String> provideDeviceDeviceIDs;

	// ------------ provide all persons, if true override all values in "provide persons"
	private boolean provideAllPersons;
	// ------------ provide persons
	private Set<String> providePersonClasses;
	private Set<String> providePersonOccurenceIds;
		
	// ------------ provide all services, if true override all values in "provide services"
	private boolean provideAllServices;
	// ------------ provide services
	private Set<String> provideServiceClasses;
	private Set<String> provideServiceOccurenceIds;
	private Set<String> provideServiceServiceURIs;
	private Set<String> provideServiceServiceURISchemes;
	
	// ----------- provide all attributes, if true overrides all "provide attributes"
	private boolean provideAllAttributes;
	// ----------- provide attributes
	private boolean provideActivities;
	private boolean provideClass;
	private boolean provideDeviceID;
	private boolean provideMood;
	private boolean providePlaceIs;
	private boolean providePlaceType;
	private boolean providePrivacy;
	private boolean provideRelationship;
	private boolean provideSphere;
	private boolean provideStatusIcon;
	private boolean provideTimeOffset;
	private UserInputTransformation provideUserInput = UserInputTransformation.false_;
	private boolean provideNote;
	private Set<UnknownBooleanAttributeTransformation> unknownBooleanAttributes; 

	/**
	 * Combine another rule with this rule.
	 */
	public void combine(PresRule other) {
		/*
		 * If a particular permission type has no value in a rule, it assumes the
		 * lowest possible value for that permission for the purpose of
		 * computing the combined permission. That value is given by the data
		 * type for booleans (FALSE) and sets (empty set), and MUST be defined
		 * by any extension to the Common Policy for other data types.
		 * 
		 * For boolean permissions, the resulting permission is TRUE if and only
		 * if at least one permission in the matching rule set has a value of
		 * TRUE and FALSE otherwise. For integer, real-valued and date-time
		 * permissions, the resulting permission is the maximum value across the
		 * permission values in the matching set of rules. For sets, it is the
		 * union of values across the permissions in the matching rule set.
		 * 
		 * 
		 */
		// here we go, booleans are combined with OR, UNION for strings lists, MAX for ints
		if (this.subHandling.getValue() < other.subHandling.getValue()) {
			this.subHandling = other.subHandling;
		}
		
		this.provideAllDevices = this.provideAllDevices || other.provideAllDevices;
		if (!this.provideAllDevices) {
			// only combine "provide devices if "provide all" is not true
			this.provideDeviceClasses.addAll(other.provideDeviceClasses);
			this.provideDeviceOccurenceIds.addAll(other.provideDeviceOccurenceIds);
			this.provideDeviceDeviceIDs.addAll(other.provideDeviceDeviceIDs);
		}
		
		this.provideAllPersons = this.provideAllPersons || other.provideAllPersons;
		if (!this.provideAllPersons) {
			// only combine "provide persons if "provide all" is not true
			this.providePersonClasses.addAll(other.providePersonClasses);
			this.providePersonOccurenceIds.addAll(other.providePersonOccurenceIds);
		}
		
		this.provideAllServices = this.provideAllServices || other.provideAllServices;
		if (!this.provideAllServices) {
			// only combine "provide services if "provide all" is not true
			this.provideServiceClasses.addAll(other.provideServiceClasses);
			this.provideServiceOccurenceIds.addAll(other.provideServiceOccurenceIds);
			this.provideServiceServiceURIs.addAll(other.provideServiceServiceURIs);
			this.provideServiceServiceURISchemes.addAll(other.provideServiceServiceURISchemes);
		}
		
		this.provideAllAttributes = this.provideAllAttributes || other.provideAllAttributes;
		if (!this.provideAllAttributes) {
			// only combine "provide attributes if "provide all" is not true
			this.provideActivities = this.provideActivities || other.provideActivities;
			this.provideClass = this.provideClass || other.provideClass;
			this.provideDeviceID = this.provideDeviceID || other.provideDeviceID;
			this.provideMood = this.provideMood || other.provideMood;
			this.providePlaceIs = this.providePlaceIs || other.providePlaceIs;
			this.providePlaceType = this.providePlaceType || other.providePlaceType;
			this.providePrivacy = this.providePrivacy || other.providePrivacy;
			this.provideRelationship = this.provideRelationship || other.provideRelationship;
			this.provideSphere = this.provideSphere || other.provideSphere;
			this.provideStatusIcon = this.provideStatusIcon || other.provideStatusIcon;
			this.provideTimeOffset = this.provideTimeOffset || other.provideTimeOffset;
			if (this.provideUserInput.getValue() < other.provideUserInput.getValue()) {
				this.provideUserInput = other.provideUserInput;
			}
			this.provideNote = this.provideNote || other.provideNote;
			this.unknownBooleanAttributes.addAll(other.unknownBooleanAttributes);
		}
	}
	
	// ---- GETTERS AND SETTERS
	
	public SubHandlingAction getSubHandling() {
		return subHandling;
	}

	public void setSubHandling(SubHandlingAction subHandling) {
		this.subHandling = subHandling;
	}

	public boolean isProvideAllDevices() {
		return provideAllDevices;
	}

	public void setProvideAllDevices(boolean provideAllDevices) {
		this.provideAllDevices = provideAllDevices;
	}

	public Set<String> getProvideDeviceClasses() {
		if (provideDeviceClasses == null) {
			provideDeviceClasses = new HashSet<String>();
		}
		return provideDeviceClasses;
	}

	public Set<String> getProvideDeviceOccurenceIds() {
		if (provideDeviceOccurenceIds == null) {
			provideDeviceOccurenceIds = new HashSet<String>();
		}
		return provideDeviceOccurenceIds;
	}

	public Set<String> getProvideDeviceDeviceIDs() {
		if (provideDeviceDeviceIDs == null) {
			provideDeviceDeviceIDs = new HashSet<String>();
		}
		return provideDeviceDeviceIDs;
	}

	public boolean isProvideAllPersons() {
		return provideAllPersons;
	}

	public void setProvideAllPersons(boolean provideAllPersons) {
		this.provideAllPersons = provideAllPersons;
	}

	public Set<String> getProvidePersonClasses() {
		if (providePersonClasses == null) {
			providePersonClasses = new HashSet<String>();
		}
		return providePersonClasses;
	}

	public Set<String> getProvidePersonOccurenceIds() {
		if (providePersonOccurenceIds == null) {
			providePersonOccurenceIds = new HashSet<String>();
		}
		return providePersonOccurenceIds;
	}

	public boolean isProvideAllServices() {
		return provideAllServices;
	}

	public void setProvideAllServices(boolean provideAllServices) {
		this.provideAllServices = provideAllServices;
	}

	public Set<String> getProvideServiceClasses() {
		if (provideServiceClasses == null) {
			provideServiceClasses = new HashSet<String>();
		}
		return provideServiceClasses;
	}

	public Set<String> getProvideServiceOccurenceIds() {
		if (provideServiceOccurenceIds == null) {
			provideServiceOccurenceIds = new HashSet<String>();
		}
		return provideServiceOccurenceIds;
	}
	
	public Set<String> getProvideServiceServiceURIs() {
		if (provideServiceServiceURIs == null) {
			provideServiceServiceURIs = new HashSet<String>();
		}
		return provideServiceServiceURIs;
	}

	public Set<String> getProvideServiceServiceURISchemes() {
		if (provideServiceServiceURISchemes == null) {
			provideServiceServiceURISchemes = new HashSet<String>();
		}
		return provideServiceServiceURISchemes;
	}
	
	public boolean isProvideAllAttributes() {
		return provideAllAttributes;
	}

	public void setProvideAllAttributes(boolean provideAllAttributes) {
		this.provideAllAttributes = provideAllAttributes;
	}

	public boolean isProvideActivities() {
		return provideActivities;
	}

	public void setProvideActivities(boolean provideActivities) {
		this.provideActivities = provideActivities;
	}

	public boolean isProvideClass() {
		return provideClass;
	}

	public void setProvideClass(boolean provideClass) {
		this.provideClass = provideClass;
	}

	public boolean isProvideDeviceID() {
		return provideDeviceID;
	}

	public void setProvideDeviceID(boolean provideDeviceID) {
		this.provideDeviceID = provideDeviceID;
	}

	public boolean isProvideMood() {
		return provideMood;
	}

	public void setProvideMood(boolean provideMood) {
		this.provideMood = provideMood;
	}

	public boolean isProvidePlaceIs() {
		return providePlaceIs;
	}

	public void setProvidePlaceIs(boolean providePlaceIs) {
		this.providePlaceIs = providePlaceIs;
	}

	public boolean isProvidePlaceType() {
		return providePlaceType;
	}

	public void setProvidePlaceType(boolean providePlaceType) {
		this.providePlaceType = providePlaceType;
	}

	public boolean isProvidePrivacy() {
		return providePrivacy;
	}

	public void setProvidePrivacy(boolean providePrivacy) {
		this.providePrivacy = providePrivacy;
	}

	public boolean isProvideRelationship() {
		return provideRelationship;
	}

	public void setProvideRelationship(boolean provideRelationship) {
		this.provideRelationship = provideRelationship;
	}

	public boolean isProvideSphere() {
		return provideSphere;
	}

	public void setProvideSphere(boolean provideSphere) {
		this.provideSphere = provideSphere;
	}

	public boolean isProvideStatusIcon() {
		return provideStatusIcon;
	}

	public void setProvideStatusIcon(boolean provideStatusIcon) {
		this.provideStatusIcon = provideStatusIcon;
	}

	public boolean isProvideTimeOffset() {
		return provideTimeOffset;
	}

	public void setProvideTimeOffset(boolean provideTimeOffset) {
		this.provideTimeOffset = provideTimeOffset;
	}

	public UserInputTransformation getProvideUserInput() {
		return provideUserInput;
	}

	public void setProvideUserInput(UserInputTransformation provideUserInput) {
		this.provideUserInput = provideUserInput;
	}

	public boolean isProvideNote() {
		return provideNote;
	}

	public void setProvideNote(boolean provideNote) {
		this.provideNote = provideNote;
	}

	public Set<UnknownBooleanAttributeTransformation> getUnknownBooleanAttributes() {
		if (unknownBooleanAttributes == null) {
			unknownBooleanAttributes = new HashSet<UnknownBooleanAttributeTransformation>();
		}
		return unknownBooleanAttributes;
	}
	
	// ------------------- processors 
	
	public void processDevicePermission(
			ProvideDevicePermission provideDevicePermission) {
		provideAllDevices = provideDevicePermission.getAllDevices() != null;
		if (!provideAllDevices) {
			for(Object deviceTransformationObject: provideDevicePermission.getDeviceIDOrOccurrenceIdOrClazz()) {
				JAXBElement deviceTransformationElement = (JAXBElement) deviceTransformationObject;
				if (deviceTransformationElement.getName().getLocalPart().equals("class")) {
					provideDeviceClasses.add((String)deviceTransformationElement.getValue());
				}
				else if (deviceTransformationElement.getName().getLocalPart().equals("occurrence-id")) {
					provideDeviceOccurenceIds.add((String)deviceTransformationElement.getValue());
				}
				else if (deviceTransformationElement.getName().getLocalPart().equals("deviceID")) {
					provideDeviceDeviceIDs.add((String)deviceTransformationElement.getValue());
				}
				// unexpected value, ignore
			}
		}		
	}
	
	public void processPersonPermission(ProvidePersonPermission providePersonPermission) {
		provideAllPersons = providePersonPermission.getAllPersons() != null;
		if (!provideAllPersons) {
			for(Object personTransformationObject: providePersonPermission.getOccurrenceIdOrClazzOrAny()) {
				JAXBElement personTransformationElement = (JAXBElement) personTransformationObject;
				if (personTransformationElement.getName().getLocalPart().equals("class")) {
					providePersonClasses.add((String)personTransformationElement.getValue());
				}
				else if (personTransformationElement.getName().getLocalPart().equals("occurrence-id")) {
					providePersonOccurenceIds.add((String)personTransformationElement.getValue());
				}
				// unexpected value, ignore
			}
		}
	}
	
	public void processServicePermission(ProvideServicePermission provideServicePermission) {
		provideAllServices = provideServicePermission.getAllServices() != null;
		if (!provideAllServices) {
			for(Object serviceTransformationObject: provideServicePermission.getServiceUriOrServiceUriSchemeOrOccurrenceId()) {
				JAXBElement serviceTransformationElement = (JAXBElement) serviceTransformationObject;
				if (serviceTransformationElement.getName().getLocalPart().equals("class")) {
					provideServiceClasses.add((String)serviceTransformationElement.getValue());
				}
				else if (serviceTransformationElement.getName().getLocalPart().equals("occurrence-id")) {
					provideServiceOccurenceIds.add((String)serviceTransformationElement.getValue());
				}
				else if (serviceTransformationElement.getName().getLocalPart().equals("service-uri")) {
					provideServiceServiceURIs.add((String)serviceTransformationElement.getValue());
				}
				else if (serviceTransformationElement.getName().getLocalPart().equals("service-uri-scheme")) {
					provideServiceServiceURISchemes.add((String)serviceTransformationElement.getValue());
				}
				// unexpected value, ignore
			}
		}
	}
		
}
