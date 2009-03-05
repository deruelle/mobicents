package javax.megaco.association;

import javax.megaco.AssociationEvent;

import javax.megaco.ParameterNotSetException;

public class AssociationInd extends AssociationEvent {

	protected AssocIndReason assocIndReason = null;
	protected LocalAddr localAddr = null;
	protected SrvChngReason srvChangeReason = null;
	protected LocalAddr srvChngAddress = null;
	protected LocalAddr handOffMGCId = null;
	protected AssocState assocState = null;
	protected SrvChngReason srvChngMethod = null;
	protected String srvChngMethodExtension = null;
	protected RemoteAddr remoteAddr = null;
	protected Integer srvChngDelay = null;
	protected Integer protocolVersion = null;
	protected String srvChngProfile = null;
	protected String parameterExtension = null;

	/**
	 * Constructs an Association Indication Event object.
	 * 
	 * @param source
	 *            - A reference to the object, the "source", that is logically
	 *            deemed to be the object upon which the Event in question
	 *            initially occurred.
	 * @param assocHandle
	 *            - The association handle to uniquely identify the MG-MGC pair.
	 *            This is allocated by the stack when the Listener registers
	 *            with the provider with a unique MG-MGC identity.
	 * @param assocIndReason
	 *            - This indicates the reason for the change in the state of the
	 *            association.
	 * @throws IllegalArgumentException
	 *             This exception is raised if the reference of Association
	 *             Indication Reason passed to this method is NULL.
	 */
	public AssociationInd(Object source, int assocHandle,
			AssocIndReason assocIndReason) throws IllegalArgumentException {
		super(source, assocHandle);
		if (assocIndReason == null) {
			throw new IllegalArgumentException("assocIndReason can not be null");
		}

		this.assocIndReason = assocIndReason;

	}

	@Override
	public int getAssocOperIdentifier() {
		return AssocEventType.M_ASSOC_STATE_IND;
	}

	/**
	 * Gets the local entity transport address. The local address specifies the
	 * transport address which the stack would use to send MEGACO messages to
	 * peer.
	 * 
	 * @return Returns the local entity transport address. If the local address
	 *         field is not present, then this method would return NULL.
	 */
	public LocalAddr getLocalAddr() {
		return localAddr;
	}

	/**
	 * This method sets the local entity identity. The localAddr specifies the
	 * transport address which the stack would use to send transactions to peer.
	 * 
	 * @param localAddr
	 *            localAddr - The local entity transport address. Since the
	 *            format of the message header Id is same as that of the
	 *            transport address, the value of this may or may not be
	 *            different from the local entity configured in the user Id as
	 *            specified in the addMegacoListener method of MegacoProvider
	 *            interface.
	 * @throws IllegalArgumentException
	 *             This exception is raised if the local transport address
	 *             specified is invalid.
	 */
	public void setLocalAddr(LocalAddr localAddr)
			throws IllegalArgumentException {
		this.localAddr = localAddr;
	}

	/**
	 * Gets the integer value which identifies the service change reason. This
	 * parameter is required on if the application is MG. The MG stack would use
	 * the same ServiceChange Reason in the ServiceChange command request sent
	 * to peer MGC.
	 * 
	 * @return Returns the integer value corresponding to the service change
	 *         reason. If the ServiceChangeReason is not set, then this method
	 *         would return value 0. The possible values are field constants
	 *         defined for the class {@link SrvChngReason}.
	 */
	public int getSrvChangeReason() {
		return srvChangeReason == null ? 0 : srvChangeReason
				.getSrvChngReasonId();
	}

	/**
	 * This method sets the service change reason. This parameter is required on
	 * if the application is MG. The MG stack would use the same ServiceChange
	 * Reason in the ServiceChange command request sent to peer MGC.
	 * 
	 * @param reason
	 *            - The object reference to ServiceChange Reason.
	 * @throws IllegalArgumentException
	 *             This exception is raised if the reference of Service Change
	 *             Reason passed to this method is NULL.
	 */
	public void setSrvChangeReason(SrvChngReason reason)
			throws IllegalArgumentException {
		if (reason == null) {
			throw new IllegalArgumentException("Change reason can not be null");
		}
		this.srvChangeReason = reason;
	}

	/**
	 * Gets the service change address.
	 * 
	 * @return Returns the service change address. if the service change address
	 *         is not set, then this method would return NULL.
	 */
	public LocalAddr getSrvChngAddress() {
		return srvChngAddress;
	}

	/**
	 * This method sets the service change address. The local entity specfied
	 * earlier is the transport address would be used for initial registration
	 * and then subsequently, the address specified in the serviceChngAddress
	 * field would be used for the local transport address. If the application
	 * is MGC, then on receipt of ServiceChange command (on ROOT termination),
	 * it will reply with ServiceChangeAddress field in
	 * serviceChangeReplyDescriptor descriptor, with value as set using this
	 * method. And If the application is MG, then the stack will send the
	 * ServiceChange command (on ROOT termination) with ServiceChangeAddress
	 * field in serviceChangeDescriptor descriptor, with value as set using this
	 * method.
	 * 
	 * @param srvChngAddress
	 *            The service change address.
	 * @throws IllegalArgumentException
	 *             This exception is raised if the service change address
	 *             specified is invalid.
	 */
	public void setSrvChngAddress(LocalAddr srvChngAddress)
			throws IllegalArgumentException {
		// FIXME: IllegalArgumentException
		this.srvChngAddress = srvChngAddress;
	}

	/**
	 * Gets the identity of the MGC to which the association is to be handoffed.
	 * This parameter may be set if the application is MGC.
	 * 
	 * @return Returns the identity of the MGC to which the association is to be
	 *         handoffed. If the HandedOff MGC Id is missing, then method
	 *         returns NULL.
	 */
	public LocalAddr getHandOffMGCId() {
		return handOffMGCId;
	}

	/**
	 * This method sets the identity of the MGC to which the association is to
	 * be handoffed. This parameter may be set if the application is MGC. If
	 * this parameter is set, then on receipt of service change request from MG
	 * (with ROOT termination), the stack will reply with MgcIdToTry field in
	 * serviceChangeDescriptor descriptor, with value as set using this method.
	 * 
	 * @param handOffMGCId
	 *            The identity of the MGC to which the association is to be
	 *            handoffed.
	 * @throws IllegalArgumentException
	 *             This exception is raised if the HandedOffMGCId specified is
	 *             invalid.
	 */
	public void setHandOffMGCId(LocalAddr handOffMGCId)
			throws IllegalArgumentException {
		// FIXME: IllegalArgumentException
		this.handOffMGCId = handOffMGCId;
	}

	// FIXME: jdoc say it returns int, all other methods do....
	/**
	 * Gets the object reference of association state.
	 * 
	 * @return Returns the integer value of association state. The values of the
	 *         association state are defined in AssocState. The association
	 *         state is to be set mandatorily. If the assoc state field is
	 *         missing, then this method would return NULL.
	 */
	public AssocState getAssociationState() {
		return this.assocState;
	}

	/**
	 * This method sets the association state. The values of the association
	 * state are defined in AssocState. The association state is to be set
	 * mandatorily.
	 * 
	 * @param associationState
	 *            The object reference of association state.
	 * @throws IllegalArgumentException
	 *             This exception is raised if the reference of Association
	 *             State passed to this method is NULL.
	 */
	public void setAssociationState(AssocState associationState)
			throws IllegalArgumentException {
		if (associationState == null) {
			throw new IllegalArgumentException("Value can not be null");
		}
		this.assocState = associationState;
	}

	/**
	 * Gets the integer value which identifies the service change method. This
	 * parameter is required on if the application is MG. The MG stack would use
	 * the same ServiceChange Method in ServiceChange command request sent to
	 * peer MGC.
	 * 
	 * @return Returns the integer value corresponding to the service change
	 *         method. If the ServiceChangeMethod is not set, then this method
	 *         would return value 0. The possible values are field constants
	 *         defined for the class SrvChngMethod.
	 */
	public int getSrvChngMethod() {
		return srvChngMethod == null ? 0 : srvChngMethod.getSrvChngReasonId();
	}

	/**
	 * This method sets the service change method. This parameter is required on
	 * if the application is MG. The MG stack would use the same ServiceChange
	 * Method in ServiceChange command request sent to peer MGC.
	 * 
	 * @param method
	 *            - The object reference to ServiceChange Method.
	 * @throws IllegalArgumentException
	 *             This exception is raised if the reference of Service Change
	 *             Reason passed to this method is NULL.
	 */
	public void setSrvChngMethod(SrvChngReason method)
			throws IllegalArgumentException {
		if (method == null) {
			throw new IllegalArgumentException("Change method can not be null");
		}
		this.srvChngMethod = method;
	}

	/**
	 * Gets the string value of the extended service change method.
	 * 
	 * 
	 * @return Returns string value of the extended service change method. This
	 *         is to be set only if the service change method is set to
	 *         {@link javax.megaco.association.SrvChngMethod.M_EXTENSION}.
	 * @throws javax.megaco.association.MethodExtensionException
	 *             javax.megaco.association.MethodExtensionException - Thrown if
	 *             service change method has not been set to
	 *             {@link javax.megaco.association.SrvChngMethod.M_EXTENSION}
	 */
	public java.lang.String getSrvChngMethodExtension()
			throws javax.megaco.association.MethodExtensionException,
			IllegalArgumentException {
		if (getSrvChngMethod() != SrvChngMethod.M_EXTENSION) {
			throw new MethodExtensionException(
					"Changed Method is not equal to {@link javax.megaco.association.SrvChngMethod.M_EXTENSION}");
		}

		return this.srvChngMethodExtension;
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
	public void setSrvChngMethodExtension(java.lang.String extMethod)
			throws javax.megaco.association.MethodExtensionException,
			IllegalArgumentException {
		if (getSrvChngMethod() != SrvChngMethod.M_EXTENSION) {
			throw new MethodExtensionException(
					"Changed Method is not equal to SrvChngMethod.{@link javax.megaco.association.SrvChngMethod.M_EXTENSION}");
		}
		// FIXME IllegalArgumentException - Thrown if extension
		// string does not follow the rules of the extension parameter, e.g,
		// should start with X+ or X- etc.
		
		
		this.srvChngMethodExtension = extMethod;
	}

	/**
	 * Gets the List of remote entity transport address for the User Id. The
	 * remote address specified in the address to which the stack would send
	 * MEGACO messages. There is one to one correspondence between the list of
	 * remote address specified here and the list of remote entity Ids in the
	 * UserId class. The messages comming from a particular remote entity must
	 * have same message header Id as the corresponding remote entity Id in
	 * UserId class.
	 * 
	 * @return Returns the list of remote entity transport address. If the
	 *         remote address parameter is not present, then this method would
	 *         return NULL.
	 */
	public RemoteAddr getRemoteAddr() {
		return remoteAddr;
	}

	/**
	 * Sets the list of remote entity identities of the user Id. The remote
	 * address specified in the address to which the stack would send MEGACO
	 * messages. There is one to one correspondence between the list of remote
	 * address specified here and the list of remote entity Ids in the UserId
	 * class. The messages comming from a particular remote entity must have
	 * same message header Id as the corresponding remote entity Id in UserId
	 * class.
	 * 
	 * @param remoteAddr
	 *            -- List of remote entity transport addresses of the MGC/MG.
	 * @throws IllegalArgumentException
	 *             This exception is raised if the reference of Remote Address
	 *             passed to this method is NULL.
	 */
	public void setRemoteAddr(RemoteAddr remoteAddr)
			throws IllegalArgumentException {
		this.remoteAddr = remoteAddr;
	}

	/**
	 * Gets the integer value of the delay parameter for the service change.
	 * This is in milliseconds. This method must be invoked after invoking the
	 * isSrvChngdelayPresent() method. The stack would use the same as the
	 * ServiceChangeDelay in the ServiceChange command request sent to peer.
	 * 
	 * @return Returns the integer value of the delay value in milliseconds.
	 * @throws javax.megaco.ParameterNotSetException
	 *             This exception is raised if the service change delay
	 *             parameter has not been set.
	 */
	public int getSrvChngDelay() throws javax.megaco.ParameterNotSetException {
		if (!isSrvChngDelayPresent()) {
			throw new ParameterNotSetException();
		}

		return this.srvChngDelay;
	}

	/**
	 * Sets the integer value of the delay parameter for the service change.
	 * This is in milliseconds. This automatically sets the service change delay
	 * value to be present. The stack would use the same as the
	 * ServiceChangeDelay in the ServiceChange command request sent to peer.
	 * 
	 * 
	 * @param delay
	 *            - The integer value of the delay value in milliseconds.
	 * @throws IllegalArgumentException
	 *             This exception is raised if the value of service change delay
	 *             passed to this method is less than 0.
	 */
	public void setSrvChngDelay(int delay)
			throws IllegalArgumentException {
		if (delay < 0) {
			throw new IllegalArgumentException(
					"Delay can not be less than zero");
		}

		this.srvChngDelay = delay;

	}

	/**
	 * Returns TRUE if the service change delay parameter is present.
	 * 
	 * @return TRUE if service change delay parameter has been set, else returns
	 *         FALSE.
	 */
	public boolean isSrvChngDelayPresent() {
		return this.srvChngDelay != null;
	}

	/**
	 * This method sets the protocol version value that is to be used for the
	 * specified association.
	 * 
	 * @param version
	 *            The protocol version as an integer value.
	 * @throws IllegalArgumentException
	 *             This exception is raised if the value of protocol version
	 *             passed to this method is less than 0.
	 */
	public void setProtocolVersion(int version)
			throws IllegalArgumentException {
		if (version < 0) {
			throw new IllegalArgumentException(
					"Value can not be less than zero");
		}

		this.protocolVersion = version;
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
	 * Gets the protocol version value received from peer in the service change.
	 * This is ther protocol version after negotiation.
	 * 
	 * @return Returns the protocol version parameter as an integer value.
	 * @throws javax.megaco.ParameterNotSetException
	 *             This exception is raised if the service change delay has not
	 *             been specified.
	 */
	public int getProtocolVersion()
			throws javax.megaco.ParameterNotSetException {
		if (!isProtocolVersionPresent()) {
			throw new ParameterNotSetException();
		}

		return this.protocolVersion;
	}

	/**
	 * Gets the service change profile value to be sent to peer in the service
	 * change.
	 * 
	 * @return Returns the service change profile parameter as a string value.
	 *         If service change profile is not set then a NULL value is
	 *         returned.
	 */
	public java.lang.String getSrvChngProfile() {
		return this.srvChngProfile;
	}

	public void setSrvChngProfile(java.lang.String profile)
			throws IllegalArgumentException {
		if (profile == null)
			throw new IllegalArgumentException("Value can not be null");
		this.srvChngProfile = profile;
	}

	/**
	 * Gets the string value of the extended service change parameter.
	 * 
	 * @return Returns string value of the extended service change parameter. If
	 *         the service change parameter is not set then this a NULL value is
	 *         returned.
	 */
	public String getParameterExtension() {
		return parameterExtension;
	}

	/**
	 * This method sets the extended service change parameter.
	 * 
	 * @param extMethod
	 *            - The string value of the extended service change parameter.
	 * @throws IllegalArgumentException
	 *             Thrown if extension string does not follow the rules of the
	 *             extension parameter, e.g, should start with X+ or X- etc.
	 */
	public void setParameterExtension(java.lang.String profile)
			throws IllegalArgumentException {
		if (profile == null)
			throw new IllegalArgumentException("Value can not be null");
		// FIXME:IllegalArgumentException - Thrown if extension
		// string does not follow the rules of the extension parameter, e.g,
		// should start with X+ or X- etc.

		this.parameterExtension = profile;
	}

	/**
	 * This method returns the reference of the AssocIndReason object as set for
	 * this class in the constructor.
	 * 
	 * @return Reference of AssocIndReason object.
	 */
	public AssocIndReason getAssocIndReason() {
		return this.assocIndReason;
	}
	

}
