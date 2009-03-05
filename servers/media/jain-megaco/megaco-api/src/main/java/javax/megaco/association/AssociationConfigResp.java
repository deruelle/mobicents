package javax.megaco.association;

import javax.megaco.AssociationEvent;
import javax.megaco.ErrorCode;

import javax.megaco.MethodInvocationException;
import javax.megaco.ReturnStatus;

public class AssociationConfigResp extends AssociationEvent {

	protected ReturnStatus eventStatus = null;
	protected ErrorCode errorCode = null;

	public AssociationConfigResp(Object source, int assocHandle)
			throws IllegalArgumentException {
		super(source, assocHandle);
		// TODO Auto-generated constructor stub
	}

	@Override
	public int getAssocOperIdentifier() {
		return AssocEventType.M_ASSOC_CONFIG_RESP;
	}

	/**
	 * This method returns whether the execution of create association request
	 * was success or not.
	 * 
	 * @return Returns an integer value that identifies whether the association
	 *         configuration event issued earlier could be performed successfuly
	 *         or not. The values are field constants defined in class
	 *         ReturnStatus. If the returnStatus is not set, then this method
	 *         would return value null.
	 */
	public final ReturnStatus getEventStatus() {
		//return eventStatus == null ? 0 : eventStatus.getReturnStatus();
		return eventStatus;
	}

	/**
	 * This method sets the status of whether the execution of the association
	 * configuration request was success or not.
	 * 
	 * @param returnStatus
	 *            - The return status of the processing of the association
	 *            configuration event. The static object corresponding to the
	 *            return status which are one of the derived classes of the
	 *            ReturnStatus shall be set.
	 * @throws IllegalArgumentException
	 *             This exception is raised if the reference of Return Status
	 *             passed to this method is NULL.
	 */
	public final void setEventStatus(ReturnStatus returnStatus)
			throws IllegalArgumentException {
		if (returnStatus == null)
			throw new IllegalArgumentException("Event status can not be null.");

		this.eventStatus = returnStatus;
	}

	/**
	 * This method returns the error code qualifying why the association
	 * configuration event could not be processed successfuly.
	 * 
	 * @return Returns an integer value that identifies the error code
	 *         specifying why the execution of the association configuration
	 *         event could not be successful. The possible values are field
	 *         constants defined for the class ErrorCode. If the error code is
	 *         not set, then this method would return value 0.
	 * 
	 * @throws IllegalStateException
	 *             - This exception would be raised if the return status is set
	 *             to M_SUCCESS, the error code is not set and hence should not
	 *             invoke this method.
	 */
	public final ErrorCode getErrorCode() throws IllegalStateException {
		if (getEventStatus() == null || getEventStatus().getReturnStatus() ==ReturnStatus.M_SUCCESS) {
			throw new IllegalStateException(
					"Event status is success, error code is not premited.");
		}
		//return errorCode == null ? 0 : errorCode.getErrorCode();
		return errorCode;
	}

	/**
	 *     This method sets the error code specifying why the association configuration event could not be executed successfuly.
	 * 
	 * @param errorCode
	 *                The error code corresponding to why the association configuration event could not be executed successfuly.
	 * @throws IllegalArgumentException
	 *             This exception would be raised in following conditions <br>
	 *             1. If the return status is not set to M_FAILURE, the error
	 *             code should not be set. <br>
	 *             2. If the error code object passed to this method is set to
	 *             NULL.
	 */
	public final void setErrorCode(ErrorCode errorCode)
			throws IllegalArgumentException {
		if (errorCode == null)
			throw new IllegalArgumentException("Error code can not be null.");
		if (getEventStatus()==null || getEventStatus().getReturnStatus() != ReturnStatus.M_FAILURE) {
			throw new IllegalArgumentException(
					"Event status is not failure, error code is not premited.");
		}
		this.errorCode = errorCode;
	}

}
