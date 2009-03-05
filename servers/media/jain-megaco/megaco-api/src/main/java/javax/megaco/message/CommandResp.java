package javax.megaco.message;

import javax.megaco.CommandEvent;

/**
 * The class extends JAIN MEGACO Command Events. This class is used to represent
 * megaco command response event.Using this class the application can send
 * responses of MEGACO command request received from peer.
 */
public class CommandResp extends CommandEvent {

	private int cmdRespIdentifier;
	private boolean isLastCommandResp;

	/**
	 * Constructs a Command Response Event object. Since the constructor if
	 * private, the user of this class will create the object of CommandReq
	 * class using methods MegacoCmdRespAdd, MegacoCmdRespMove etc. This
	 * restricts the implementation such that the command response event objects
	 * created will have cmdResponseIdentifier as one of the values defined in
	 * the class CmdResponseType.
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
	 * @param isLastCommandResp
	 *            - This parameter specifies whether the command response is the
	 *            last response for the wildcarded request received.
	 * @param isFirstCommandInAction
	 *            - This parameter specifies whether the command is the first
	 *            command in the action.
	 * @param cmdRespIdentifier
	 *            - Identifies the value of the command request identifier for
	 *            which the command response event class has been created.
	 * @throws IllegalArgumentException
	 *             : This exception is raised if the value of transaction handle
	 *             or the action handle passed to this method is less than 0.
	 */
	private CommandResp(java.lang.Object source, int assocHandle, int txnHandle, int actionHandle, boolean isLastCommandResp, boolean isFirstCommandInAction, int cmdRespIdentifier)
			throws IllegalArgumentException {
		super(source, assocHandle, txnHandle, actionHandle, isLastCommandResp, isFirstCommandInAction);
		if (txnHandle < 0 || actionHandle < 0) {
			IllegalArgumentException invalidArgumentException = new IllegalArgumentException("txnHandle or actionHandle cannot be less than 0 for CommandReq");
			// invalidArgumentException.setAssocHandle(assocHandle);
			throw invalidArgumentException;
		}

		this.cmdRespIdentifier = cmdRespIdentifier;
	}

	/**
	 * This method cannot be overridden by the derived class. This method
	 * returns that the command identifier is of type command response Event.
	 * This method overrides the corresponding method of the base class
	 * CommandEvent.
	 * 
	 * @return Returns an integer value that identifies this event object as a
	 *         command response event i.e. M_COMMAND_RESP.
	 */
	public int getCommandIdentifier() {
		return CommandType.M_ACTION_RESP;
	}

	/**
	 * This method identifies the Command response type of the class instance.
	 * See javax.megaco.message.CmdResponseType for the definition of the
	 * constants for the Command Response events.
	 * 
	 * @return Returns an integer value that identifies this event object as the
	 *         type of command response event. It returns whether it is add
	 *         response command or subract response command or audit value
	 *         command response or audit capability command response or notify
	 *         response or service change response or modify response or move
	 *         response.
	 */
	public int getResponseIdentifier() {
		return this.cmdRespIdentifier;
	}

	/**
	 * Stack needs to check this field to know that all response for the
	 * wildcarded request has been received so as to send next command request
	 * to the application for the same transaction. Application needs this to
	 * know that all command responses for the wildcarded command request has
	 * been received.
	 * 
	 * @return Returns true if the command is the last command in the
	 *         transaction.
	 */
	public boolean isLastCommandResp() {
		return this.isLastCommandResp;
	}

	/**
	 * Sets the flag to indicate that this command response is the last response
	 * for the wildcarded command request received. Application will set this
	 * field when giving the last response for the wildcarded command request
	 * received from the stack. Similarly the stack would set this field when
	 * giving the command responses received from the peer to the application.
	 */
	public void setLastCommandResp() {
		this.isLastCommandResp = true;
	}

	/**
	 * This method is used for creating the CommandResp class with the request
	 * identifier set to M_ADD_RESP. This method is invoked to obtain the object
	 * reference of the class CommandResp. This method is valid only if the
	 * application is MG. This would be used to send ADD response for command
	 * request request received from MGC.
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
	 * @param isLastCommandResp
	 *            - This parameter specifies whether the command response is the
	 *            last response for the wildcarded request received.
	 * @param isFirstCommandInAction
	 *            - This parameter specifies whether the command is the first
	 *            command in the action.
	 * @throws IllegalArgumentException
	 *             : This exception is raised if the value of transaction handle
	 *             or the action handle passed to this method is less than 0.
	 */
	public static CommandResp MegacoCmdRespAdd(java.lang.Object source, int assocHandle, int txnHandle, int actionHandle, boolean isLastCommandResp, boolean isFirstCommandInAction)
			throws IllegalArgumentException {
		CommandResp resp = new CommandResp(source, assocHandle, txnHandle, actionHandle, isLastCommandResp, isFirstCommandInAction, CmdResponseType.M_ADD_RESP);
		return resp;
	}

	/**
	 * This method is used for creating the CommandResp class with the request
	 * identifier set to M_MODIFY_RESP. This method is invoked to obtain the
	 * object reference of the class CommandResp. This method is valid only if
	 * the application is MG. This would be used to send MODIFY response for
	 * command request request received from MGC.
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
	 * @param isLastCommandResp
	 *            - This parameter specifies whether the command response is the
	 *            last response for the wildcarded request received.
	 * @param isFirstCommandInAction
	 *            - This parameter specifies whether the command is the first
	 *            command in the action.
	 * @throws IllegalArgumentException
	 *             : This exception is raised if the value of transaction handle
	 *             or the action handle passed to this method is less than 0.
	 */
	public static CommandResp MegacoCmdRespModify(java.lang.Object source, int assocHandle, int txnHandle, int actionHandle, boolean isLastCommandResp, boolean isFirstCommandInAction)
			throws IllegalArgumentException {
		CommandResp resp = new CommandResp(source, assocHandle, txnHandle, actionHandle, isLastCommandResp, isFirstCommandInAction, CmdResponseType.M_MODIFY_RESP);
		return resp;
	}

	/**
	 * This method is used for creating the CommandResp class with the request
	 * identifier set to M_MOVE_RESP. This method is invoked to obtain the
	 * object reference of the class CommandResp. This method is valid only if
	 * the application is MG. This would be used to send MOVE response for
	 * command request request received from MGC.
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
	 * @param isLastCommandResp
	 *            - This parameter specifies whether the command response is the
	 *            last response for the wildcarded request received.
	 * @param isFirstCommandInAction
	 *            - This parameter specifies whether the command is the first
	 *            command in the action.
	 * @throws IllegalArgumentException
	 *             : This exception is raised if the value of transaction handle
	 *             or the action handle passed to this method is less than 0.
	 */
	public static CommandResp MegacoCmdRespMove(java.lang.Object source, int assocHandle, int txnHandle, int actionHandle, boolean isLastCommandResp, boolean isFirstCommandInAction)
			throws IllegalArgumentException {
		CommandResp resp = new CommandResp(source, assocHandle, txnHandle, actionHandle, isLastCommandResp, isFirstCommandInAction, CmdResponseType.M_MOVE_RESP);
		return resp;
	}

	/**
	 * This method is used for creating the CommandResp class with the request
	 * identifier set to M_SERVICE_CHANGE_RESP. This method is invoked to obtain
	 * the object reference of the class CommandResp. This method is valid for
	 * both MG and MGC application. This would be used to send ServiceChange
	 * response for command request request received from peer.
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
	 * @param isLastCommandResp
	 *            - This parameter specifies whether the command response is the
	 *            last response for the wildcarded request received.
	 * @param isFirstCommandInAction
	 *            - This parameter specifies whether the command is the first
	 *            command in the action.
	 * @throws IllegalArgumentException
	 *             : This exception is raised if the value of transaction handle
	 *             or the action handle passed to this method is less than 0.
	 */
	public static CommandResp MegacoCmdRespSrvChng(java.lang.Object source, int assocHandle, int txnHandle, int actionHandle, boolean isLastCommandResp, boolean isFirstCommandInAction)
			throws IllegalArgumentException {
		CommandResp resp = new CommandResp(source, assocHandle, txnHandle, actionHandle, isLastCommandResp, isFirstCommandInAction, CmdResponseType.M_SERVICE_CHANGE_RESP);
		return resp;
	}

	/**
	 * This method is used for creating the CommandResp class with the request
	 * identifier set to M_NOTIFY_RESP. This method is invoked to obtain the
	 * object reference of the class CommandResp. This method is valid only if
	 * the application is MGC. This would be used to send NOTIFY response for
	 * command request request received from MG.
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
	 * @param isLastCommandResp
	 *            - This parameter specifies whether the command response is the
	 *            last response for the wildcarded request received.
	 * @param isFirstCommandInAction
	 *            - This parameter specifies whether the command is the first
	 *            command in the action.
	 * @throws IllegalArgumentException
	 *             : This exception is raised if the value of transaction handle
	 *             or the action handle passed to this method is less than 0.
	 */
	public static CommandResp MegacoCmdRespNotify(java.lang.Object source, int assocHandle, int txnHandle, int actionHandle, boolean isLastCommandResp, boolean isFirstCommandInAction)
			throws IllegalArgumentException {
		CommandResp resp = new CommandResp(source, assocHandle, txnHandle, actionHandle, isLastCommandResp, isFirstCommandInAction, CmdResponseType.M_NOTIFY_RESP);
		return resp;
	}

	/**
	 * This method is used for creating the CommandResp class with the request
	 * identifier set to M_AUDIT_CAP_RESP. This method is invoked to obtain the
	 * object reference of the class CommandResp. This method is valid only if
	 * the application is MG. This would be used to send AuditCapability
	 * response for command request request received from MGC.
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
	 * @param isLastCommandResp
	 *            - This parameter specifies whether the command response is the
	 *            last response for the wildcarded request received.
	 * @param isFirstCommandInAction
	 *            - This parameter specifies whether the command is the first
	 *            command in the action.
	 * @throws IllegalArgumentException
	 *             : This exception is raised if the value of transaction handle
	 *             or the action handle passed to this method is less than 0.
	 */
	public static CommandResp MegacoCmdRespAuditCap(java.lang.Object source, int assocHandle, int txnHandle, int actionHandle, boolean isLastCommandResp, boolean isFirstCommandInAction)
			throws IllegalArgumentException {
		CommandResp resp = new CommandResp(source, assocHandle, txnHandle, actionHandle, isLastCommandResp, isFirstCommandInAction, CmdResponseType.M_AUDIT_CAP_RESP);
		return resp;
	}

	/**
	 * This method is used for creating the CommandResp class with the request
	 * identifier set to M_AUDIT_VAL_RESP. This method is invoked to obtain the
	 * object reference of the class CommandResp. This method is valid only if
	 * the application is MG. This would be used to send AuditValue response for
	 * command request request received from MGC.
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
	 * @param isLastCommandResp
	 *            - This parameter specifies whether the command response is the
	 *            last response for the wildcarded request received.
	 * @param isFirstCommandInAction
	 *            - This parameter specifies whether the command is the first
	 *            command in the action.
	 * @throws IllegalArgumentException
	 *             : This exception is raised if the value of transaction handle
	 *             or the action handle passed to this method is less than 0.
	 */
	public static CommandResp MegacoCmdRespAuditVal(java.lang.Object source, int assocHandle, int txnHandle, int actionHandle, boolean isLastCommandResp, boolean isFirstCommandInAction)
			throws IllegalArgumentException {
		CommandResp resp = new CommandResp(source, assocHandle, txnHandle, actionHandle, isLastCommandResp, isFirstCommandInAction, CmdResponseType.M_AUDIT_VAL_RESP);
		return resp;
	}

	/**
	 * This method is used for creating the CommandResp class with the request
	 * identifier set to M_SUBTRACT_RESP. This method is invoked to obtain the
	 * object reference of the class CommandResp. This method is valid only if
	 * the application is MG. This would be used to send SUBTRACT response for
	 * command request request received from MGC.
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
	 * @param isLastCommandResp
	 *            - This parameter specifies whether the command response is the
	 *            last response for the wildcarded request received.
	 * @param isFirstCommandInAction
	 *            - This parameter specifies whether the command is the first
	 *            command in the action.
	 * @throws IllegalArgumentException
	 *             : This exception is raised if the value of transaction handle
	 *             or the action handle passed to this method is less than 0.
	 */
	public static CommandResp MegacoCmdRespSubtract(java.lang.Object source, int assocHandle, int txnHandle, int actionHandle, boolean isLastCommandResp, boolean isFirstCommandInAction)
			throws IllegalArgumentException {
		CommandResp resp = new CommandResp(source, assocHandle, txnHandle, actionHandle, isLastCommandResp, isFirstCommandInAction, CmdResponseType.M_SUBTRACT_RESP);
		return resp;
	}
}
