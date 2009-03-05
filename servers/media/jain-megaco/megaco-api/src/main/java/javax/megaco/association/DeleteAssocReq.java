package javax.megaco.association;

import java.io.Serializable;

import javax.megaco.AssociationEvent;

import javax.megaco.ParameterNotSetException;

/**
 * The class extends JAIN MEGACO Association Events. This represents the delete
 * association request event. The event causes the stack initiate assoiation
 * teardown procedures with the peer. This would case the stack to send a
 * ServiceChange command with specified ServiceChangeMethod (e.g. Forced or
 * Graceful) and ServiceChangeReason. After completion of the protocol
 * procedure, the association is deleted with the peer and can no more be used
 * for exchaging commands with the peer.
 */
public class DeleteAssocReq extends AssociationEvent implements Serializable {

	protected SrvChngReason srvChangeReason = null;
	protected SrvChngReason srvChngMethod = null;
	protected LocalAddr srvChngAddress = null;
	protected LocalAddr handOffMGCId = null;
	protected String srvChngMethodExtension = null;
	protected int srvChngDelay = -1;

	public DeleteAssocReq(Object source, int assocHandle)
			throws IllegalArgumentException {
		super(source, assocHandle);
		// TODO Auto-generated constructor stub
	}

	@Override
	public int getAssocOperIdentifier() {
		return AssocEventType.M_DELETE_ASSOC_REQ;
	}

	/**
	 * Gets the integer value which identifies the service change reason. The
	 * stack would use the same ServiceChange Reason in the ServiceChange
	 * command request sent to peer.
	 * 
	 * @return Returns the integer value corresponding to the service change
	 *         reason. If the ServiceChangeReason is not set, then this method
	 *         would return value 0. The possible values are field constants
	 *         defined for the class SrvChngReason.
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
	 * Gets the object reference corresponding to the service change address.
	 * The stack would use the same ServiceChange Address in the ServiceChange
	 * command request sent to peer.
	 * 
	 * @return Returns the service change address. If the service change address
	 *         has not been specified for this class, then this method returns
	 *         NULL.
	 */
	public LocalAddr getSrvChngAddress() {
		return srvChngAddress;
	}

	/**
	 * This method sets the service change address. The stack would use the same
	 * ServiceChange Address in the ServiceChange command request sent to peer.
	 * 
	 * @param srvChngAddress
	 *            - The service change address.
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
	 * The stack would use the same ServiceChangeMGCId in the ServiceChange
	 * command request sent to peer.
	 * 
	 * @return Returns the identity of the MGC to which the association is to be
	 *         handoffed. If HandedOffMGCId is not specified for this class,
	 *         then this method returns NULL.
	 */
	public LocalAddr getHandOffMGCId() {
		return handOffMGCId;
	}

	/**
	 * This method sets the identity of the MGC to which the association is to
	 * be handoffed. This method is valid only if the application is MGC. The
	 * stack would use the same ServiceChangeMGCId in the ServiceChange command
	 * request sent to peer.
	 * 
	 * 
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
					"Changed Method is not equal to SrvChngMethod.{@link javax.megaco.association.SrvChngMethod.M_EXTENSION}");
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
	public void setSrvChngMethod(java.lang.String extMethod)
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
		if (this.srvChngDelay == -1) {
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
	 * @param delay - The integer value of the delay value in milliseconds.
	 * @throws IllegalArgumentException This exception is raised if the value of service change delay passed to this method is less than 0.
	 */
	public void setSrvChngDelay(int delay)
			throws IllegalArgumentException {
		if(delay<0)
		{
			throw new IllegalArgumentException("Delay can not be less than zero");
		}
		
		
		this.srvChngDelay=delay;
		
	}
	/**
	 * Returns TRUE if the service change delay parameter is present.
	 * @return TRUE if service change delay parameter has been set, else returns FALSE.
	 */
	public boolean isSrvChngDelayPresent()
	{
		return this.srvChngDelay!=-1;
	}
}
