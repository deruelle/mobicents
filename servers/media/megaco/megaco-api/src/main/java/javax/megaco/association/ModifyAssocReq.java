package javax.megaco.association;

import javax.megaco.AssociationEvent;
import javax.megaco.InvalidArgumentException;
import javax.megaco.ParameterNotSetException;

public class ModifyAssocReq extends AssociationEvent {

	protected SrvChngReason srvChangeReason = null;
	protected SrvChngReason srvChngMethod = null;
	protected LocalAddr srvChngAddress = null;
	protected String srvChngMethodExtension = null;


	public ModifyAssocReq(Object source, int assocHandle)
			throws InvalidArgumentException {
		super(source, assocHandle);
		// TODO Auto-generated constructor stub
	}

	@Override
	public int getAssocOperIdentifier() {
		return AssocEventType.M_MODIFY_ASSOC_REQ;
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
	 * @throws javax.megaco.InvalidArgumentException
	 *             This exception is raised if the reference of Service Change
	 *             Reason passed to this method is NULL.
	 */
	public void setSrvChangeReason(SrvChngReason reason)
			throws javax.megaco.InvalidArgumentException {
		if (reason == null) {
			throw new InvalidArgumentException("Change reason can not be null");
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
	 * @throws javax.megaco.InvalidArgumentException
	 *             This exception is raised if the reference of Service Change
	 *             Reason passed to this method is NULL.
	 */
	public void setSrvChngMethod(SrvChngReason method)
			throws InvalidArgumentException {
		if (method == null) {
			throw new InvalidArgumentException("Change method can not be null");
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
	 * @throws InvalidArgumentException
	 *             This exception is raised if the service change address
	 *             specified is invalid.
	 */
	public void setSrvChngAddress(LocalAddr srvChngAddress)
			throws InvalidArgumentException {
		// FIXME: InvalidArgumentException
		this.srvChngAddress = srvChngAddress;
	}


	/**
	 * Gets the string value of the extended service change method.
	 * 
	 * 
	 * @return Returns string value of the extended service change method. This
	 *         is to be set only if the service change method is set to
	 *         M_SVC_CHNG_METHOD_EXTENSION.
	 * @throws javax.megaco.association.MethodExtensionException
	 *             javax.megaco.association.MethodExtensionException - Thrown if
	 *             service change method has not been set to
	 *             M_SVC_CHNG_METHOD_EXTENSION
	 */
	public java.lang.String getSrvChngMethodExtension()
			throws javax.megaco.association.MethodExtensionException,
			javax.megaco.InvalidArgumentException {
		if (getSrvChngMethod() != SrvChngMethod.M_SVC_CHNG_METHOD_EXTENSION) {
			throw new MethodExtensionException(
					"Changed Method is not equal to SrvChngMethod.M_SVC_CHNG_METHOD_EXTENSION");
		}

		return this.srvChngMethodExtension;
	}

	/**
	 * This method sets the extended service change method. This needs to be set
	 * if and only if the service change method is M_SVC_CHNG_METHOD_EXTENSION.
	 * 
	 * @param extMethod
	 *            - The string value of the extended service change method.
	 * @throws javax.megaco.association.MethodExtensionException
	 *             - Thrown if service change method has not been set to
	 *             M_SVC_CHNG_METHOD_EXTENSION.
	 * @throws javax.megaco.InvalidArgumentException
	 *             - Thrown if extension string does not follow the rules of the
	 *             extension parameter, e.g, should start with X+ or X- etc.
	 */
	public void setSrvChngMethod(java.lang.String extMethod)
			throws javax.megaco.association.MethodExtensionException,
			javax.megaco.InvalidArgumentException {
		if (getSrvChngMethod() != SrvChngMethod.M_SVC_CHNG_METHOD_EXTENSION) {
			throw new MethodExtensionException(
					"Changed Method is not equal to SrvChngMethod.M_SVC_CHNG_METHOD_EXTENSION");
		}
		// FIXME javax.megaco.InvalidArgumentException - Thrown if extension
		// string does not follow the rules of the extension parameter, e.g,
		// should start with X+ or X- etc.

		this.srvChngMethodExtension = extMethod;
	}



}
