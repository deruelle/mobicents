package javax.megaco;

import java.util.EventObject;

import javax.megaco.message.ContextInfo;
import javax.megaco.message.Descriptor;
import javax.megaco.message.Termination;

/**
 * The base class for all JAIN MEGACO Command Events. This is an abstract class
 * and hence cannot be created as a separate object. This class acts as the base
 * class for the Command and Context request and response classes.
 * 
 * 
 */
public abstract class CommandEvent extends EventObject {

	private Descriptor[] descriptorInfo;
	private Termination termination;
	private int assocHandle;
	private int txnHandle;
	private int actionHandle;
	private boolean isFirstCommandInAction;
	private boolean txn_or_cmd_status;
	private ContextInfo contextInfo = null;
	private int exchangeId = 0;

	/**
	 * Constructs a Command Event object. This is an abstract class and can be
	 * called only by the derived classes.
	 * 
	 * @param source
	 *            A reference to the object, the "source", that is logically
	 *            deemed to be the object upon which the Event in question
	 *            initially occurred.
	 * @param assocHandle
	 *            The association handle to uniquely identify the MG-MGC pair.
	 *            This is allocated by the stack when the Listener registers
	 *            with the provider with a unique MG-MGC identity.
	 * @param txnHandle
	 *            The transaction handle that shall uniquely identify the
	 *            transaction id for the transaction in which the command shall
	 *            be sent. <br/>
	 *            <ol>
	 *            <li> The transaction handle is allocated by the stack either
	 *            on request from User application or on receipt of the
	 *            transaction indication from peer. </li>
	 *            <li> If the response is to be sent for the transaction
	 *            received, then the application sends the same transaction
	 *            handle that has been received by it in the indication. </li>
	 *            <li>If the confirmation is to be sent by the stack to the
	 *            application due to receipt of a response from the peer stack
	 *            for a request sent by the stack, then the transaction handle
	 *            shall be same as received in the command request by the stack.<br/>
	 *            There are two methods of initiating transacction request
	 *            towards peer. In the first case, the value is retuned
	 *            synchronously, if the transaction handle specified here is 0
	 *            and in the other case new allocation is requestd
	 *            asynchronously by application using CreateTxnReq/
	 *            CreateTxnResp sequence. </li>
	 *            </ol>
	 * @param actionHandle
	 * @param txn_or_cmd_status
	 *            In case of command request, this parameter specifies whether
	 *            the command is last command in the transaction. And in case of
	 *            command response, this parameter specifies whether the command
	 *            response is the last response for the wildcarded request
	 *            received.
	 * @param isFirstCommandInAction
	 */
	public CommandEvent(Object source, int assocHandle, int txnHandle,
			int actionHandle, boolean txn_or_cmd_status,
			boolean isFirstCommandInAction) {
		super(source);
		this.assocHandle = assocHandle;
		this.txnHandle = txnHandle;
		this.actionHandle = actionHandle;
		this.txn_or_cmd_status = txn_or_cmd_status;
		this.isFirstCommandInAction = isFirstCommandInAction;
	}

	/**
	 * Gets the Descriptor information for all the descriptor in this command.
	 * 
	 * @return The vector of the reference to the object identifier of type
	 *         descriptor information.
	 */
	public Descriptor[] getDescriptor() {
		return descriptorInfo;
	}

	/**
	 * Sets the vector of Descriptor Information for this command. In cases of
	 * error it returns M_FAILURE and also sets the error code in this object.
	 * Else shall return M_SUCCESS.
	 * 
	 * @param descriptorInfo
	 *            The vector of reference to the object identifier of type
	 *            descriptor information.
	 * @return
	 * @throws IllegalArgumentException
	 *             This exception is raised if the reference of vector of
	 *             Descriptor Ids passed to this method is set NULL.
	 */
	public int setDescriptor(Descriptor[] descriptorInfo)
			throws IllegalArgumentException {

		if (descriptorInfo == null) {
			IllegalArgumentException invalidArgumentException = new IllegalArgumentException(
					"Descriptor[] cannot be null for CommandEvent");

			// TODO : some check of Descriptor should be done here?
			// TODO : set the correct ExceptionInfoCode
//			invalidArgumentException
//					.setInfoCode(ExceptionInfoCode.INV_DESC_TYPE);
//			invalidArgumentException.setAssocHandle(this.assocHandle);
			throw invalidArgumentException;
		}
		this.descriptorInfo = descriptorInfo;

		return ReturnStatus.M_SUCCESS;
	}

	/**
	 * Gets the Termination for this command.
	 * 
	 * @return The reference to the object identifier of type Termination. If no
	 *         termination is specified for this command, then this method
	 *         returns NULL.
	 */
	public Termination getTermination() {
		return this.termination;
	}

	/**
	 * Sets the Termination for this command. In cases of error (termination
	 * type mismatch) it returns M_FAILURE and also sets the error code in this
	 * object. Else shall return M_SUCCESS.
	 * 
	 * @param termination
	 *            The reference to the object identifier of type Termination.
	 * @return
	 * @throws IllegalArgumentException
	 *             This exception is raised if the reference of Termination Id
	 *             passed to this method is NULL.
	 */
	public int setTermination(Termination termination)
			throws IllegalArgumentException {
		if (termination == null) {
			IllegalArgumentException invalidArgumentException = new IllegalArgumentException(
					"Termination cannot be null for CommandEvent");
//			invalidArgumentException.setAssocHandle(this.assocHandle);
//			invalidArgumentException
//					.setInfoCode(ExceptionInfoCode.INV_TERM_TYPE);
			throw invalidArgumentException;
		}
		// TODO : caheck Termination for M_FAILURE
		this.termination = termination;
		return ReturnStatus.M_SUCCESS;
	}

	public int getAssocHandle() {
		return this.assocHandle;
	}

	public int getTxnHandle() {
		return this.txnHandle;
	}

	public int getActionHandle() {
		return this.actionHandle;
	}

	/**
	 * This is a virtual method and shall be defined in the derived classes. See
	 * javax.megaco.message.CommandType for the definition of the constants for
	 * the Command events. This is not set in this object but is retrieved from
	 * the derived classes. Hence all derived classes need to implement this
	 * method.
	 * 
	 * @return Returns an integer value that identifies this event object as a
	 *         command request event or a command response event.
	 */
	public abstract int getCommandIdentifier();

	/**
	 * Stack or application needs to know whether the current command is the
	 * last command in the transaction. Stack needs to know this so that it can
	 * it can now form the MEGACO message and hence send the message to the
	 * remote stack entity.
	 * 
	 * @return Returns true if the command is the last command in the
	 *         transaction.
	 */
	public boolean isLastCommandInTxn() {
		return this.txn_or_cmd_status;
	}

	/**
	 * This flag indicates whether new action has started.
	 * 
	 * @return Returns true if the command is the first command in the Action.
	 */
	public boolean isFirstCommandInAction() {
		return this.isFirstCommandInAction;
	}

	public void setFirstCommandInAction() {
		this.isFirstCommandInAction = true;

	}

	public void setLastCommandInTxn() {
		this.txn_or_cmd_status = true;
	}

	public void setCntxtInfo(ContextInfo contextInfo)
			throws IllegalArgumentException {
		if (contextInfo == null) {
			IllegalArgumentException invalidArgumentException = new IllegalArgumentException(
					"ContextInfo cannot be null for CommandEvent");
			throw invalidArgumentException;
		}
		this.contextInfo = contextInfo;
	}

	public ContextInfo getCntxtInfo() {
		return this.contextInfo;
	}

	public int getExchangeId() {
		return this.exchangeId;
	}

	public void setExchangeId(int exchangeId)
			throws IllegalArgumentException {
		// TODO : Throw exception is raised if the exchange Id specified is
		// invalid.
		this.exchangeId = exchangeId;
	}
}
