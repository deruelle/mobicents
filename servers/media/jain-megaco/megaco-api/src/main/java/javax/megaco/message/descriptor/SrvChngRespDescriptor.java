package javax.megaco.message.descriptor;

import java.io.Serializable;


import javax.megaco.ParameterNotSetException;
import javax.megaco.association.LocalAddr;
import javax.megaco.association.SrvChngMethod;
import javax.megaco.association.SrvChngReason;
import javax.megaco.message.Descriptor;
import javax.megaco.message.DescriptorType;

/**
 * The class extends JAIN MEGACO Descriptor. This class represents the service
 * change response descriptor of the MEGACO protocol. This descriptor describes
 * the service change address, mgcId, profile and version in the service change
 * response descriptor.
 */
public class SrvChngRespDescriptor extends Descriptor implements Serializable {

	private LocalAddr srvChngAddress;
	private LocalAddr handOffMGCId;
	private String srvChngProfile;
	private String timeValue;
	private String dateValue;
	private Integer protocolVersion;

	
	//FIXME: this is not present:
	private SrvChngMethod srvChngMethod;
	/**
	 * Constructs a SrvChng Response Descriptor which would conatin atleast one
	 * of service change address, mgcid, profile and version in service change
	 * resonse descriptor.
	 */
	public SrvChngRespDescriptor() {
		super.descriptorId = DescriptorType.M_SERVICE_CHANGE_RESP_DESC;
	}
	
	/**
	 * Constructs a SrvChng Response Descriptor which would conatin atleast one
	 * of service change address, mgcid, profile and version in service change
	 * resonse descriptor.
	 * @param  srvChngReason - sets srvc chng reason.
	 */
	public SrvChngRespDescriptor(SrvChngMethod srvChngMethod) {
		super.descriptorId = DescriptorType.M_SERVICE_CHANGE_RESP_DESC;
		this.srvChngMethod = srvChngMethod;
	}

	/**
	 * This method cannot be overridden by the derived class. This method
	 * returns that the descriptor identifier is of type descriptor
	 * SrvChngResponse. This method overrides the corresponding method of the
	 * base class Descriptor.
	 * 
	 * @return Returns an integer value that identifies this service change
	 *         object as the type of service change descriptor. It returns that
	 *         it is SrvChng response Descriptor i.e.,
	 *         M_SERVICE_CHANGE_RESP_DESC.
	 */
	public int getDescriptorId() {
		return super.descriptorId;
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
	
	
}
