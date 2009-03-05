package javax.megaco.message;

import javax.megaco.CommandEvent;

/**
 * The class extends JAIN MEGACO Command Events. This class is used to represent
 * megaco context information response event. It is used when the application
 * needs to respond to a command or an action request received with context
 * properties only. Through this the application would be able to respond with
 * context properties only. This event would be received by the application when
 * the application is only expecting a context property in the response with no
 * accompanying commands.
 * 
 * 
 */
public class ContextInfoResp extends CommandEvent {
	/**
	 * Constructs a Context Information Response Event object.
	 * 
	 * @param source
	 *            - A reference to the object, the "source", that is logically
	 *            deemed to be the object upon which the Event in question
	 *            initially occurred.
	 * @param assocHandle
	 *            - The association handle to uniquely identify the MG-MGC pair.
	 *            This is allocated by the stack when the Listener registers
	 *            with the provider with a unique MG-MGC identity.
	 * @param txnHandle
	 *            - The transaction handle that shall uniquely identify the
	 *            transaction id for the transaction in which the command shall
	 *            be sent.
	 * 
	 * <br>
	 *            1. The transaction handle is allocated by the stack either on
	 *            request from User application or on receipt of the transaction
	 *            indication from peer. <br>
	 *            2. If the response is to be sent for the transaction received,
	 *            then the application sends the same transaction handle that
	 *            has been received by it in the indication. <br>
	 *            3. If the confirmation is to be sent by the stack to the
	 *            application due to receipt of a response from the peer stack
	 *            for a request sent by the stack, then the transaction handle
	 *            shall be same as received in the command request by the stack.
	 * 
	 * @param actionHandle
	 *            - The action handle uniquely identifies the action within a
	 *            transaction. The action handle field is used for
	 *            request-response synchronisation.
	 * 
	 * <br>
	 *            1. If the request is sent from application to the remote
	 *            entity, then the action handle is allocated by the
	 *            application. On receipt of the response from the peer for the
	 *            same request, the stack will use the same action handle when
	 *            giving the confirmation to the application. <br>
	 *            2. If the indication received from stack is to be sent to the
	 *            application, then the action handle is allocated by the stack.
	 *            The response sent by the application to the stack mus have the
	 *            same action handle as received in the indication.
	 * 
	 *            Note: The action handle can be different from the context id
	 *            when there are multiple action in the same transaction all
	 *            having context id as 'null' or 'choose' or '*'.
	 *            isLastActionInTxn - This parameter specifies whether the
	 *            action is last action in the transaction.
	 * @param isLastActionInTxn
	 * 
	 * @throws IllegalArgumentException
	 *             : This exception is raised if the value of transaction handle
	 *             or the action handle passed to this method is less than 0.
	 */
	public ContextInfoResp(java.lang.Object source, int assocHandle, int txnHandle, int actionHandle, boolean isLastActionInTxn) throws IllegalArgumentException {
		super(source, assocHandle, txnHandle, actionHandle, false, isLastActionInTxn);
		if (txnHandle < 0 || actionHandle < 0) {
			IllegalArgumentException invalidArgumentException = new IllegalArgumentException("txnHandle or actionHandle cannot be less than 0 for ContextInfoResp");
			// invalidArgumentException.setAssocHandle(assocHandle);
			throw invalidArgumentException;
		}
	}

	@Override
	public int getCommandIdentifier() {
		return CommandType.M_ACTION_RESP;
	}

}
