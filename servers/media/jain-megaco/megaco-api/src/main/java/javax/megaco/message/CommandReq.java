package javax.megaco.message;

import javax.megaco.CommandEvent;

/**
 * The class extends JAIN MEGACO Command Events. This class is used to represent
 * megaco command request event. Using this class the application can send
 * MEGACO command request to peer.
 * 
 * 
 * 
 */
public class CommandReq extends CommandEvent {

	private boolean isCommandOptional = false;
	private boolean isReqWithWildcardResp = false;
	private int cmdRequestIdentifier;

	/**
	 * Constructs a Command Request Event object. Since the constructor if
	 * private, the user of this class will create the object of CommandReq
	 * class using methods MegacoCmdReqAdd, MegacoCmdReqMove etc. This restricts
	 * the implementation such that the command request event objects created
	 * will have cmdRequestIdentifier as one of the values defined in the class
	 * CmdRequestType.
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
	 * @param isLastCommandInTxn
	 *            - This parameter specifies whether the command is last command
	 *            in the transaction. If this flag is set to TRUE, then the
	 *            stack would sent the transaction request to peer.
	 * @param isFirstCommandInAction
	 *            - This parameter specifies whether the command is the first
	 *            command in the action. This is used to identify the Action
	 *            boundaries.
	 * @param cmdRequestIdentifier
	 *            - Identifies the value of the command request identifier for
	 *            which the command request event class has been created.
	 * @throws IllegalArgumentException
	 *             : This exception is raised if the value of transaction handle
	 *             or the action handle passed to this method is less than 0.
	 */
	private CommandReq(java.lang.Object source, int assocHandle, int txnHandle, int actionHandle, boolean isLastCommandInTxn, boolean isFirstCommandInAction, int cmdRequestIdentifier)
			throws IllegalArgumentException {

		super(source, assocHandle, txnHandle, actionHandle, isLastCommandInTxn, isFirstCommandInAction);
		if (txnHandle < 0 || actionHandle < 0) {
			IllegalArgumentException invalidArgumentException = new IllegalArgumentException("txnHandle or actionHandle cannot be less than 0 for CommandReq");
			// invalidArgumentException.setAssocHandle(assocHandle);
			throw invalidArgumentException;
		}

		this.cmdRequestIdentifier = cmdRequestIdentifier;
	}

	/**
	 * This method returns that the command identifier is of type command
	 * request Event. This method overrides the corresponding method of the base
	 * class CommandEvent.
	 * 
	 * @return Returns an integer value that identifies this event object as a
	 *         command request event i.e. M_COMMAND_REQ.
	 */
	public int getCommandIdentifier() {
		return CommandType.M_COMMAND_REQ;
	}

	/**
	 * This method identifies the Command request type of the class instance.
	 * See javax.megaco.message.CmdRequestType for the definition of the
	 * constants for the Command Request events.
	 * 
	 * @return Returns an integer value that identifies this event object as the
	 *         type of command request event. It returns whether it is add
	 *         request command or subtract request command or audit value
	 *         command request or audit capability command request or notify
	 *         request or service change request or modify request or move
	 *         request.
	 */
	public int getRequestIdentifier() {
		return this.cmdRequestIdentifier;
	}

	/**
	 * This method indicates the optional command request of the MEGACO command
	 * request. The object will have this flag set to TRUE when a command is
	 * received (e.g. ADD, SUB) from the peer with the O- option. This indicates
	 * that the peer wants other commands in the Transaction to be executed even
	 * if this one fails.
	 * 
	 * @return Returns true if the command request is optional.
	 */
	public boolean isCommandOptional() {
		return this.isCommandOptional;
	}

	/**
	 * This method indicates the wildcarded command response of the MEGACO
	 * command request. The object will have this flag set to TRUE when a
	 * command is received (e.g. ADD, SUB) from the peer with the W- option.
	 * This indicates that the peer wants single wildcarded response for the
	 * command request which may have multiple responses coresponding to it.
	 * 
	 * @return Returns true if the command request wishes the response from the
	 *         peer MG entity in form of wildcard.
	 */
	public boolean isReqWithWildcardResp() {
		return this.isReqWithWildcardResp;
	}

	/**
	 * This method shall be invoked by the application when it wishes the stack
	 * to prepend "O-" to the command to indicate to the peer (MGC or Gateway as
	 * the case may be) that this command is an optional command and should not
	 * block execution of other commands in the transaction.
	 */
	public void setCommandOptional() {
		this.isCommandOptional = true;
	}

	/**
	 * Sets the flag to indicate that the command request wishes a wildcard
	 * union response from the peer for a wild carded request. If this parameter
	 * is set, then the command sent to peer has "W-" preceeding the command
	 * name.
	 */
	public void setReqWithWildcardResp() {
		this.isReqWithWildcardResp = true;
	}

	/**
	 * This method is used for creating the CommandReq class with the request
	 * identifier set to M_ADD_REQ. This method is invoked to obtain the object
	 * reference of the class CommandReq. This method is valid only if the
	 * application is MGC. This would be used to send ADD command request to MG.
	 * * @param source - A reference to the object, the "source", that is
	 * logically deemed to be the object upon which the Event in question
	 * initially occurred.
	 * 
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
	 * @param isLastCommandInTxn
	 *            - This parameter specifies whether the command is last command
	 *            in the transaction. If this flag is set to TRUE, then the
	 *            stack would sent the transaction request to peer.
	 * @param isFirstCommandInAction
	 *            - This parameter specifies whether the command is the first
	 *            command in the action. This is used to identify the Action
	 *            boundaries.
	 * @throws IllegalArgumentException
	 *             : This exception is raised if the value of transaction handle
	 *             or the action handle passed to this method is less than 0.
	 * @return
	 */
	public static CommandReq MegacoCmdReqAdd(java.lang.Object source, int assocHandle, int txnHandle, int actionHandle, boolean isLastCommandInTxn, boolean isFirstCommandInAction)
			throws IllegalArgumentException {
		CommandReq req = new CommandReq(source, assocHandle, txnHandle, actionHandle, isLastCommandInTxn, isFirstCommandInAction, CmdRequestType.M_ADD_REQ);
		return req;
	}

	/**
	 * This method is used for creating the CommandReq class with the request
	 * identifier set to M_MODIFY_REQ. This method is invoked to obtain the
	 * object reference of the class CommandReq. This method is valid only if
	 * the application is MGC. This would be used to send MODIFY command request
	 * to MG. * @param assocHandle - The association handle to uniquely identify
	 * the MG-MGC pair. This is allocated by the stack when the Listener
	 * registers with the provider with a unique MG-MGC identity.
	 * 
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
	 * @param isLastCommandInTxn
	 *            - This parameter specifies whether the command is last command
	 *            in the transaction. If this flag is set to TRUE, then the
	 *            stack would sent the transaction request to peer.
	 * @param isFirstCommandInAction
	 *            - This parameter specifies whether the command is the first
	 *            command in the action. This is used to identify the Action
	 *            boundaries.
	 * @throws IllegalArgumentException
	 *             : This exception is raised if the value of transaction handle
	 *             or the action handle passed to this method is less than 0.
	 */
	public static CommandReq MegacoCmdReqModify(java.lang.Object source, int assocHandle, int txnHandle, int actionHandle, boolean isLastCommandInTxn, boolean isFirstCommandInAction)
			throws IllegalArgumentException {
		CommandReq req = new CommandReq(source, assocHandle, txnHandle, actionHandle, isLastCommandInTxn, isFirstCommandInAction, CmdRequestType.M_MODIFY_REQ);
		return req;
	}

	/**
	 * This method is used for creating the CommandReq class with the request
	 * identifier set to M_MOVE_REQ. This method is invoked to obtain the object
	 * reference of the class CommandReq. This method is valid only if the
	 * application is MGC. This would be used to send MOVE command request to
	 * MG.
	 * 
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
	 * @param isLastCommandInTxn
	 *            - This parameter specifies whether the command is last command
	 *            in the transaction. If this flag is set to TRUE, then the
	 *            stack would sent the transaction request to peer.
	 * @param isFirstCommandInAction
	 *            - This parameter specifies whether the command is the first
	 *            command in the action. This is used to identify the Action
	 *            boundaries.
	 * @throws IllegalArgumentException
	 *             : This exception is raised if the value of transaction handle
	 *             or the action handle passed to this method is less than 0.
	 */
	public static CommandReq MegacoCmdReqMove(java.lang.Object source, int assocHandle, int txnHandle, int actionHandle, boolean isLastCommandInTxn, boolean isFirstCommandInAction)
			throws IllegalArgumentException {
		CommandReq req = new CommandReq(source, assocHandle, txnHandle, actionHandle, isLastCommandInTxn, isFirstCommandInAction, CmdRequestType.M_MOVE_REQ);
		return req;
	}

	/**
	 * This method is used for creating the CommandReq class with the request
	 * identifier set to M_SERVICE_CHANGE_REQ. This method is invoked to obtain
	 * the object reference of the class CommandReq. This method is valid for
	 * both MG and MGC. This would be used to send ServiceChange command request
	 * to peer.
	 * 
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
	 * @param isLastCommandInTxn
	 *            - This parameter specifies whether the command is last command
	 *            in the transaction. If this flag is set to TRUE, then the
	 *            stack would sent the transaction request to peer.
	 * @param isFirstCommandInAction
	 *            - This parameter specifies whether the command is the first
	 *            command in the action. This is used to identify the Action
	 *            boundaries.
	 * @throws IllegalArgumentException
	 *             : This exception is raised if the value of transaction handle
	 *             or the action handle passed to this method is less than 0.
	 */
	public static CommandReq MegacoCmdReqSrvChng(java.lang.Object source, int assocHandle, int txnHandle, int actionHandle, boolean isLastCommandInTxn, boolean isFirstCommandInAction)
			throws IllegalArgumentException {
		CommandReq req = new CommandReq(source, assocHandle, txnHandle, actionHandle, isLastCommandInTxn, isFirstCommandInAction, CmdRequestType.M_SERVICE_CHANGE_REQ);
		return req;
	}

	/**
	 * This method is used for creating the CommandReq class with the request
	 * identifier set to M_NOTIFY_REQ. This method is invoked to obtain the
	 * object reference of the class CommandReq. This method is valid only if
	 * the application is MG. This would be used to send NOTIFY command request
	 * to MGC.
	 * 
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
	 * @param isLastCommandInTxn
	 *            - This parameter specifies whether the command is last command
	 *            in the transaction. If this flag is set to TRUE, then the
	 *            stack would sent the transaction request to peer.
	 * @param isFirstCommandInAction
	 *            - This parameter specifies whether the command is the first
	 *            command in the action. This is used to identify the Action
	 *            boundaries.
	 * @throws IllegalArgumentException
	 *             : This exception is raised if the value of transaction handle
	 *             or the action handle passed to this method is less than 0.
	 */
	public static CommandReq MegacoCmdReqNotify(java.lang.Object source, int assocHandle, int txnHandle, int actionHandle, boolean isLastCommandInTxn, boolean isFirstCommandInAction)
			throws IllegalArgumentException {
		CommandReq req = new CommandReq(source, assocHandle, txnHandle, actionHandle, isLastCommandInTxn, isFirstCommandInAction, CmdRequestType.M_NOTIFY_REQ);
		return req;
	}

	/**
	 * This method is used for creating the CommandReq class with the request
	 * identifier set to M_AUDIT_CAP_REQ. This method is invoked to obtain the
	 * object reference of the class CommandReq. This method is valid only if
	 * the application is MGC. This would be used to send AuditCapability
	 * command request to MG.
	 * 
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
	 * @param isLastCommandInTxn
	 *            - This parameter specifies whether the command is last command
	 *            in the transaction. If this flag is set to TRUE, then the
	 *            stack would sent the transaction request to peer.
	 * @param isFirstCommandInAction
	 *            - This parameter specifies whether the command is the first
	 *            command in the action. This is used to identify the Action
	 *            boundaries.
	 * @throws IllegalArgumentException
	 *             : This exception is raised if the value of transaction handle
	 *             or the action handle passed to this method is less than 0.
	 */
	public static CommandReq MegacoCmdReqAuditCap(java.lang.Object source, int assocHandle, int txnHandle, int actionHandle, boolean isLastCommandInTxn, boolean isFirstCommandInAction)
			throws IllegalArgumentException {
		CommandReq req = new CommandReq(source, assocHandle, txnHandle, actionHandle, isLastCommandInTxn, isFirstCommandInAction, CmdRequestType.M_AUDIT_CAP_REQ);
		return req;
	}

	/**
	 * This method is used for creating the CommandReq class with the request
	 * identifier set to M_AUDIT_VAL_REQ. This method is invoked to obtain the
	 * object reference of the class CommandReq. This method is valid only if
	 * the application is MGC. This would be used to send AuditValue command
	 * request to MG.
	 * 
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
	 * @param isLastCommandInTxn
	 *            - This parameter specifies whether the command is last command
	 *            in the transaction. If this flag is set to TRUE, then the
	 *            stack would sent the transaction request to peer.
	 * @param isFirstCommandInAction
	 *            - This parameter specifies whether the command is the first
	 *            command in the action. This is used to identify the Action
	 *            boundaries.
	 * @throws IllegalArgumentException
	 *             : This exception is raised if the value of transaction handle
	 *             or the action handle passed to this method is less than 0.
	 */
	public static CommandReq MegacoCmdReqAuditVal(java.lang.Object source, int assocHandle, int txnHandle, int actionHandle, boolean isLastCommandInTxn, boolean isFirstCommandInAction)
			throws IllegalArgumentException {
		CommandReq req = new CommandReq(source, assocHandle, txnHandle, actionHandle, isLastCommandInTxn, isFirstCommandInAction, CmdRequestType.M_AUDIT_VAL_REQ);
		return req;
	}

	/**
	 * This method is used for creating the CommandReq class with the request
	 * identifier set to M_SUBTRACT_REQ. This method is invoked to obtain the
	 * object reference of the class CommandReq. This method is valid only if
	 * the application is MGC. This would be used to send SUBTRACT command
	 * request to MG.
	 * 
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
	 * @param isLastCommandInTxn
	 *            - This parameter specifies whether the command is last command
	 *            in the transaction. If this flag is set to TRUE, then the
	 *            stack would sent the transaction request to peer.
	 * @param isFirstCommandInAction
	 *            - This parameter specifies whether the command is the first
	 *            command in the action. This is used to identify the Action
	 *            boundaries.
	 * @throws IllegalArgumentException
	 *             : This exception is raised if the value of transaction handle
	 *             or the action handle passed to this method is less than 0.
	 */
	public static CommandReq MegacoCmdReqSubtract(java.lang.Object source, int assocHandle, int txnHandle, int actionHandle, boolean isLastCommandInTxn, boolean isFirstCommandInAction)
			throws IllegalArgumentException {
		CommandReq req = new CommandReq(source, assocHandle, txnHandle, actionHandle, isLastCommandInTxn, isFirstCommandInAction, CmdRequestType.M_SUBTRACT_REQ);
		return req;
	}
}
