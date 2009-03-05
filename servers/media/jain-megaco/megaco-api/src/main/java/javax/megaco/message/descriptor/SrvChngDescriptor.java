package javax.megaco.message.descriptor;

import java.io.Serializable;


import javax.megaco.ParameterNotSetException;
import javax.megaco.association.LocalAddr;
import javax.megaco.association.MethodExtensionException;
import javax.megaco.association.SrvChngMethod;
import javax.megaco.association.SrvChngReason;
import javax.megaco.message.Descriptor;
import javax.megaco.message.DescriptorType;

/**
 * The class extends JAIN MEGACO Descriptor. This class represents the service
 * change descriptor defined in the MEGACO protocol. This descriptor describes
 * the service change method, reason, delay, address, profile, mgcid, version,
 * the timestamp and any extension to it. Among these the service change method
 * is mandatory.
 */
public class SrvChngDescriptor extends Descriptor implements Serializable {

	private SrvChngMethod srvChngMethod;
	private String svcChngMethodExtension;
	private SrvChngReason srvChngReason;
	private Integer srvChngDelay;
	private LocalAddr srvChngAddress;
	private String srvChngProfile;
	private LocalAddr handOffMGCId;
	private Integer protocolVersion;
	private String dateValue;
	private String timeValue;
	private String parameterExtension;

	/**
	 * Constructs a SrvChng Descriptor. The service change descriptor class
	 * contain service change method and atleast one of service change method,
	 * reason, delay, address, profile, mgcid, version, the timestamp and any
	 * extension parameter.
	 */
	public SrvChngDescriptor() {
		super.descriptorId = DescriptorType.M_SERVICE_CHANGE_DESC;
	}

	/**
	 * This method cannot be overridden by the derived class. This method
	 * returns that the descriptor identifier is of type descriptor SrvChng.
	 * This method overrides the corresponding method of the base class
	 * Descriptor.
	 * 
	 * @return Returns an integer value that identifies this service change
	 *         object as the type of service change descriptor. It returns the
	 *         the value M_SERVICE_CHANGE_DESC of a Service Change Descriptor.
	 */
	public int getDescriptorId() {
		return super.descriptorId;
	}

	/**
	 * Gets the integer value which identifies the service change method.
	 * 
	 * @return Returns the integer value of service change method. The values of
	 *         the service change method are defined in SrvChngMethod. The
	 *         service change method is to be mandatorily set. If Service Change
	 *         method is not set then this method would return null.
	 */
	public SrvChngMethod getSrvChngMethod() {
		return this.srvChngMethod;
	}

	/**
	 * This method sets the service change method. The values of the service
	 * change method are defined in SrvChngMethod. The service change method is
	 * to be mandatorily set.
	 * 
	 * @param method
	 *            The object reference of service change method.
	 * @throws IllegalArgumentException
	 *             : This exception is raised if the reference of Service Change
	 *             Method passed to this method is NULL.
	 */
	public void setSrvChngMethod(SrvChngMethod method) throws IllegalArgumentException {
		if (method == null) {
			throw new IllegalArgumentException("SrvChngMethod must not be null.");
		}

		this.srvChngMethod = method;
	}

	/**
	 * Gets the string value of the extended service change method.
	 * 
	 * @return Returns string value of the extended service change method. This
	 *         is to be set only if the service change method is set to
	 *         {@link javax.megaco.association.SrvChngMethod.M_EXTENSION}.
	 * @throws javax.megaco.association.MethodExtensionException
	 *             - Thrown if service change method has not been set to
	 *             {@link javax.megaco.association.SrvChngMethod.M_EXTENSION}.
	 */
	public java.lang.String getSvcChngMethodExtension() throws javax.megaco.association.MethodExtensionException {

		if (this.srvChngMethod == null || this.srvChngMethod.getSrvChngMethodId() != srvChngMethod.M_EXTENSION) {
			throw new MethodExtensionException("SrvChngMethod must be set to: SVC_CHNG_METHOD_EXTENSION");
		}
		return this.svcChngMethodExtension;

	}

	/**
	 * This method sets the extended service change method. This needs to be set
	 * if and only if the service change method is {@link javax.megaco.association.SrvChngMethod.M_EXTENSION}.
	 * 
	 * @param extMethod
	 *            - The string value of the extended service change method.
	 * @throws javax.megaco.association.MethodExtensionException
	 *             - Thrown if service change method has not been set to
	 *             {@link javax.megaco.association.SrvChngMethod.M_EXTENSION}.
	 * @throws IllegalArgumentException
	 *             - Thrown if extension string does not follow the rules of the
	 *             extension parameter, e.g, should start with X+ or X- etc.
	 */
	public void setSvcChngMethodExtension(java.lang.String extMethod) throws javax.megaco.association.MethodExtensionException, IllegalArgumentException {

		if (this.srvChngMethod == null || this.srvChngMethod.getSrvChngMethodId() != srvChngMethod.M_EXTENSION) {
			throw new MethodExtensionException("SrvChngMethod must be set to: SVC_CHNG_METHOD_EXTENSION");
		}

		DescriptorUtils.checkMethodExtensionRules(extMethod);

		this.svcChngMethodExtension = extMethod;

	}

	/**
	 * Gets the integer value which identifies the service change reason.
	 * 
	 * @return Returns the object reference corresponding to the service change
	 *         reason. The values of the service change reason are defined in
	 *         SrvChngReason. If the ServiceChangeReason is not set, then this
	 *         method would return value null.
	 */
	public SrvChngReason getSrvChngReason() {
		return this.srvChngReason;
	}

	/**
	 * This method sets the service change reason. The values of the service
	 * change reason are defined in SrvChngReason.
	 * 
	 * @param reasonCode
	 *            The object reference to the corresponding service change
	 *            reason.
	 * @throws IllegalArgumentException
	 *             - Thrown if the reference of ServiceChange reason passed to
	 *             this method is NULL.
	 */
	public void setSrvChngReason(SrvChngReason reasonCode) throws IllegalArgumentException {

		if (reasonCode == null) {
			throw new IllegalArgumentException("SrvChngReason must not be null.");
		}

		this.srvChngReason = reasonCode;
	}

	/**
	 * Gets the service change delay value received from peer in the service
	 * change.
	 * 
	 * @return Returns the service change delay parameter as an integer value.
	 * @throws javax.megaco.ParameterNotSetException
	 *             - Thrown if service change delay parameter has not been set.
	 */
	public int getSrvChngDelay() throws javax.megaco.ParameterNotSetException {
		if (!isSrvChngDelayPresent()) {
			throw new ParameterNotSetException("SrvChngDelay is not present.");
		}

		return this.srvChngDelay.intValue();
	}

	/**
	 * 
	 This method sets the service change delay value.
	 * 
	 * @param delay
	 *            The service change delay as an integer value.
	 * @throws IllegalArgumentException
	 *             : This exception is raised if the value of service change
	 *             delay passed to this method is less than 0.
	 */
	public void setSrvChngDelay(int delay) throws IllegalArgumentException {
		if (delay <= 0) {
			throw new IllegalArgumentException("Delay must be greater than zero.");
		}

		this.srvChngDelay = new Integer(delay);
	}

	/**
	 * Identifies whether the service change delay is present.
	 * 
	 * @return Returns true if the service change delay is present.
	 */
	public boolean isSrvChngDelayPresent() {
		return this.srvChngDelay != null;
	}

	/**
	 * Gets the service change address. If the service change command has been
	 * received from peer, then it refers to the the new transport address the
	 * peer intends to use subsequently.
	 * 
	 * @return Returns the service change address. If the service change address
	 *         is missing, then the method would return NULL.
	 */
	public LocalAddr getSrvChngAddress() {
		return this.srvChngAddress;
	}

	/**
	 * This method sets the service change address. This is not a mandatory
	 * parameter.
	 * 
	 * @param LocalAddr
	 *            The object reference of service change address.
	 * @throws IllegalArgumentException
	 *             - Thrown if a parameter in service change address is set such
	 *             that it is invalid.
	 */
	public void setSrvChngAddress(LocalAddr localAddr) throws IllegalArgumentException {

		// FIXME: not present in jdoc
		if (localAddr == null) {
			throw new IllegalArgumentException("LocalAddr must not be null.");
		}
		// FIXME: add error checks?

		this.srvChngAddress = localAddr;

	}

	/**
	 * Gets the service change profile value received from peer in the service
	 * change.
	 * 
	 * @return Returns the service change profile parameter as a string value.
	 *         If the service change profile is not set then it returns a NULL
	 *         value.
	 */
	public java.lang.String getSrvChngProfile() {
		return this.srvChngProfile;
	}

	/**
	 * This method sets the service change profile value.
	 * 
	 * @param profile
	 *            The service change profile as a string value.
	 * @throws IllegalArgumentException
	 *             - Thrown if service change profile parameter has invalid
	 *             format.
	 */
	public void setSrvChngProfile(java.lang.String profile) throws IllegalArgumentException {
		if (profile == null) {
			throw new IllegalArgumentException("profile must not be null.");
		}

		DescriptorUtils.checkSrvcChngProfileRules(profile);

		this.srvChngProfile = profile;
	}

	/**
	 * Gets the identity of the MGC to which the association is to be handoffed.
	 * As specified in the protocol, in case of failure of MGC, it would handoff
	 * the control of the MG to the new MGC. This is conveyed using service
	 * change command on ROOT termination, with service change method set to
	 * M_HANDOFF and transport parameters of the new MGC
	 * specified in the mgcidToTry field of the service change descriptor. This
	 * mgcidToTry field of the service change descriptor is represented using
	 * HandedOffMGCId field on this class.
	 * 
	 * @return Returns the identity of the MGC to which the association is to be
	 *         handoffed.
	 */
	public LocalAddr getHandOffMGCId() {
		return this.handOffMGCId;
	}

	/**
	 * This method sets the identity of the MGC to which the association is to
	 * be handoffed.
	 * 
	 * As specified in the protocol, in case of failure of MGC, it would handoff
	 * the control of the MG to the new MGC. This is conveyed using service
	 * change command on ROOT termination, with service change method set to
	 * M_HANDOFF and transport parameters of the new MGC
	 * specified in the mgcidToTry field of the service change descriptor. This
	 * mgcidToTry field of the service change descriptor is represented using
	 * HandedOffMGCId field on this class.
	 * 
	 * @param mgcidToTry
	 *            The identity of the MGC to which the association is to be
	 *            handoffed.
	 * @throws IllegalArgumentException
	 *             - Thrown if a parameter in mgc Id is set such that it is
	 *             invalid.
	 */
	public void setHandOffMGCId(LocalAddr mgcidToTry) throws IllegalArgumentException {

		// FIXME: not present in jdoc
		if (mgcidToTry == null) {
			throw new IllegalArgumentException("LocalAddr must not be null.");
		}

		if (srvChngMethod == null || srvChngMethod.getSrvChngMethodId() != srvChngMethod.M_HANDOFF) {
			throw new IllegalArgumentException("SrvChngMethodId must be set to: SVC_CHNG_METHOD_HANDOFF.");
		}

		// FIXME: add error checks?
		this.handOffMGCId = mgcidToTry;

	}

	/**
	 * Gets the protocol version value received from peer in the service change.
	 * This is ther protocol version after negotiation.
	 * 
	 * @return Returns the protocol version parameter as an integer value.
	 * @throws javax.megaco.ParameterNotSetException
	 *             - Thrown if protocol version has not been set.
	 */
	public int getProtocolVersion() throws javax.megaco.ParameterNotSetException {
		if (!isProtocolVersionPresent()) {
			throw new ParameterNotSetException("Protocol Version has not been set.");
		}

		return this.protocolVersion.intValue();
	}

	/**
	 * This method sets the protocol version value.
	 * 
	 * @param version
	 *            The protocol version as an integer value.
	 * @throws IllegalArgumentException
	 *             - Thrown if service change version parameter is less than or
	 *             equal to 0.
	 */
	public void setProtocolVersion(int version) throws IllegalArgumentException {

		if (version <= 0) {
			throw new IllegalArgumentException("Protocol Version must be greater than zero.");
		}

		this.protocolVersion = new Integer(version);

	}

	/**
	 * Identifies whether the protocol version is present.
	 * 
	 * @return Returns true if the protocol version is present.
	 */
	public boolean isProtocolVersionPresent() {
		return this.protocolVersion != null;
	}

	/**
	 * Gets the string value of the date value of timestamp in service change
	 * descriptor.
	 * 
	 * @return Returns string value of the date value of timestamp in service
	 *         change. If the date value of timestamp in service change is not
	 *         set then a NULL value is returned.
	 */
	public java.lang.String getDateValue() {
		return this.dateValue;
	}

	/**
	 * This method sets the date value of timestamp in service change
	 * descriptor.
	 * 
	 * @param dateValue
	 *            The string value of the date value of timestamp in service
	 *            change descriptor.
	 * @throws IllegalArgumentException
	 *             - Thrown if date value of timestamp in service change
	 *             descriptor is not of 8 digit length.
	 */
	public void setDateValue(java.lang.String dateValue) throws IllegalArgumentException {

		if (dateValue == null) {
			new IllegalArgumentException("DateValue must nto be null.");
		}

		DescriptorUtils.checkTimeStampRules(dateValue);
		this.dateValue = dateValue;
	}

	/**
	 * Gets the string value of the time value of timestamp in service change
	 * descriptor.
	 * 
	 * @return Returns string value of the time value of timestamp in service
	 *         change descriptor. If the time value of timestamp in service
	 *         change descriptor is not set then a NULL value is returned.
	 */
	public java.lang.String getTimeValue() {
		return this.timeValue;
	}

	/**
	 * This method sets the time value of timestamp in service change
	 * descriptor.
	 * 
	 * @param timeValue
	 *            The string value of the time value of timestamp in service
	 *            change descriptor.
	 * @throws IllegalArgumentException
	 *             - Thrown if time value of timestamp in service change
	 *             descriptor is not of 8 digit length.
	 */
	public void setTimeValue(java.lang.String timeValue) throws IllegalArgumentException {

		if (timeValue == null) {
			new IllegalArgumentException("TimeValue must nto be null.");
		}

		DescriptorUtils.checkTimeStampRules(timeValue);
		this.timeValue = timeValue;
	}

	/**
	 * Gets the string value of the extended service change parameter.
	 * 
	 * @return Returns string value of the extended service change parameter. If
	 *         the service change parameter is not set then this a NULL value is
	 *         returned.
	 */
	public java.lang.String getParameterExtension() {
		return this.parameterExtension;
	}

	/**
	 * This method sets the extended service change parameter.
	 * 
	 * @param extMethod
	 *            The string value of the extended service change parameter.
	 * @throws IllegalArgumentException
	 *             - Thrown if extension string does not follow the rules of the
	 *             extension parameter, e.g, should start with X+ or X- etc.
	 */
	public void setParameterExtention(java.lang.String extMethod) throws IllegalArgumentException {
		if (extMethod == null) {
			new IllegalArgumentException("ExtMethod must nto be null.");
		}

		DescriptorUtils.checkMethodExtensionRules(extMethod);

		this.parameterExtension = extMethod;
	}

}
