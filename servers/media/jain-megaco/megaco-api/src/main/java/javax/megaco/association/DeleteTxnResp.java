/**
 * Start time:14:03:29 2009-01-30<br>
 * Project: mobicents-media-server-controllers<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package javax.megaco.association;

import javax.megaco.AssociationEvent;
import javax.megaco.ErrorCode;

import javax.megaco.MethodInvocationException;
import javax.megaco.ReturnStatus;

public class DeleteTxnResp extends AssociationEvent {

	private int txnHandle = -1;
	protected ReturnStatus eventStatus = null;
	protected ErrorCode errorCode = null;

	public DeleteTxnResp(Object source, int assocHandle) throws IllegalArgumentException {
		super(source, assocHandle);
		// TODO Auto-generated constructor stub
	}

	public DeleteTxnResp(Object source, int assocHandle, int txnHandle) throws IllegalArgumentException {
		super(source, assocHandle);
		this.setTxnHandle(txnHandle);
	}

	@Override
	public final int getAssocOperIdentifier() {
		return AssocEventType.M_DELETE_TXN_RESP;
	}

	/**
	 * This method returns whether the delete transaction event was successful
	 * or not.
	 * 
	 * @return Returns an integer value that identifies whether the delete
	 *         transaction event issued earlier could be performed successfuly
	 *         or not. The values are field constants defined in class
	 *         ReturnStatus. If the returnStatus is not set, then this method
	 *         would return value null.
	 */
	public final ReturnStatus getEventStatus() {
		// return eventStatus == null ? 0 : eventStatus.getReturnStatus();
		return eventStatus;
	}

	/**
	 * This method sets the status of whether the processing of the delete
	 * transaction event was successful or not.
	 * 
	 * @param returnStatus
	 *            - The return status of the processing of the delete
	 *            transaction event. The static object corresponding to the
	 *            return status which are one of the derived classes of the
	 *            ReturnStatus shall be set.
	 * @throws IllegalArgumentException
	 *             This exception is raised if the reference of Return Status
	 *             passed to this method is NULL.
	 */
	public final void setEventStatus(ReturnStatus returnStatus) throws IllegalArgumentException {
		if (returnStatus == null)
			throw new IllegalArgumentException("Event status can not be null.");

		this.eventStatus = returnStatus;
	}

	/**
	 * This method returns the error code qualifying why the modify association
	 * event could not be processed successfuly.
	 * 
	 * @return Returns an integer value that identifies the error code
	 *         specifying why the execution of the modify association event
	 *         could not be successful. The possible values are field constants
	 *         defined for the class ErrorCode. If the error code is not set,
	 *         then this method would return value null.
	 * @throws IllegalStateException
	 *             - This exception would be raised if the return status is set
	 *             to M_SUCCESS, the error code is not set and hence should not
	 *             invoke this method.
	 */
	public final ErrorCode getErrorCode() throws IllegalStateException {
		if (getEventStatus() == null || getEventStatus().getReturnStatus() == ReturnStatus.M_SUCCESS) {
			throw new IllegalStateException("Event status is success or not set, error code is not premited.");
		}
		// return errorCode == null ? 0 : errorCode.getErrorCode();
		return errorCode;
	}

	/**
	 * This method sets the error code specifying why the delete transaction
	 * event could not be executed successfuly.
	 * 
	 * @param errorCode
	 *            - The error code correspondingto why the delete transaction
	 *            event could not be executed successfuly. The values are
	 *            defined in AssocEventType.
	 * @throws IllegalArgumentException
	 *             This exception would be raised in following conditions <br>
	 *             1. If the return status is not set to M_FAILURE, the error
	 *             code should not be set. <br>
	 *             2. If the error code is not valid for the DeleteTxnReq event.
	 */
	public final void setErrorCode(ErrorCode errorCode) throws IllegalArgumentException {
		if (errorCode == null)
			throw new IllegalArgumentException("Error code can not be null.");
		if (getEventStatus() == null || getEventStatus().getReturnStatus() != ReturnStatus.M_FAILURE) {
			throw new IllegalArgumentException("Event status is not failure or nto set, error code is not premited.");
		}
		this.errorCode = errorCode;
	}

	// FIXME those are nto defined in jdoc....

	/**
	 * Gets an object identifier that specifies the transaction identifier. If
	 * the transaction identifier is set to 0, then this would be the case when
	 * the transaction identifier is to represent all transactions.<br>
	 * <br>
	 * If the transaction identifier is not set, then this method returns 0,
	 * indicating all transactions.
	 * 
	 * @return Returns an integer value that specifies the transaction
	 *         identifier.
	 */
	public final int getTxnHandle() {
		if (txnHandle == -1)
			return 0;

		return txnHandle;

	}

	/**
	 * This method sets the transaction identifier. To delete all transactions,
	 * the transaction identifier is set to 0.
	 * 
	 * @param transactionHandle
	 *            A reference to transaction identifier.
	 * @throws IllegalArgumentException
	 *             This exception is raised if the value of transaction handle
	 *             passed to this method is less than 0.
	 */
	public final void setTxnHandle(int transactionHandle) throws IllegalArgumentException {

		if (transactionHandle < 0)
			throw new IllegalArgumentException("Txn Handle can not be less than zero");

		this.txnHandle = transactionHandle;
	}

}
