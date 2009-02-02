package javax.megaco.message;

import javax.megaco.CommandEvent;
import javax.megaco.InvalidArgumentException;

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

	private CommandReq(java.lang.Object source, int assocHandle, int txnHandle,
			int actionHandle, boolean isLastCommandInTxn,
			boolean isFirstCommandInAction, int cmdRequestIdentifier)
			throws javax.megaco.InvalidArgumentException {

		super(source, assocHandle, txnHandle, actionHandle, isLastCommandInTxn,
				isFirstCommandInAction);
		if (txnHandle < 0 || actionHandle < 0) {
			InvalidArgumentException invalidArgumentException = new InvalidArgumentException(
					"txnHandle or actionHandle cannot be less than 0 for CommandReq");
			invalidArgumentException.setAssocHandle(assocHandle);
			throw invalidArgumentException;
		}

		this.cmdRequestIdentifier = cmdRequestIdentifier;
	}

	@Override
	public int getCommandIdentifier() {
		return CommandType.M_COMMAND_REQ;
	}

	public int getRequestIdentifier() {
		return this.cmdRequestIdentifier;
	}

	public boolean isCommandOptional() {
		return this.isCommandOptional;
	}

	public boolean isReqWithWildcardResp() {
		return this.isReqWithWildcardResp;
	}

	public void setCommandOptional() {
		this.isCommandOptional = true;
	}

	public void setReqWithWildcardResp() {
		this.isReqWithWildcardResp = true;
	}

	public static CommandReq MegacoCmdReqAdd(java.lang.Object source,
			int assocHandle, int txnHandle, int actionHandle,
			boolean isLastCommandInTxn, boolean isFirstCommandInAction)
			throws javax.megaco.InvalidArgumentException {
		CommandReq req = new CommandReq(source, assocHandle, txnHandle,
				actionHandle, isLastCommandInTxn, isFirstCommandInAction,
				CmdRequestType.M_ADD_REQ);
		return req;
	}

	public static CommandReq MegacoCmdReqModify(java.lang.Object source,
			int assocHandle, int txnHandle, int actionHandle,
			boolean isLastCommandInTxn, boolean isFirstCommandInAction)
			throws javax.megaco.InvalidArgumentException {
		CommandReq req = new CommandReq(source, assocHandle, txnHandle,
				actionHandle, isLastCommandInTxn, isFirstCommandInAction,
				CmdRequestType.M_MODIFY_REQ);
		return req;
	}

	public static CommandReq MegacoCmdReqMove(java.lang.Object source,
			int assocHandle, int txnHandle, int actionHandle,
			boolean isLastCommandInTxn, boolean isFirstCommandInAction)
			throws javax.megaco.InvalidArgumentException {
		CommandReq req = new CommandReq(source, assocHandle, txnHandle,
				actionHandle, isLastCommandInTxn, isFirstCommandInAction,
				CmdRequestType.M_MOVE_REQ);
		return req;
	}

	public static CommandReq MegacoCmdReqSrvChng(java.lang.Object source,
			int assocHandle, int txnHandle, int actionHandle,
			boolean isLastCommandInTxn, boolean isFirstCommandInAction)
			throws javax.megaco.InvalidArgumentException {
		CommandReq req = new CommandReq(source, assocHandle, txnHandle,
				actionHandle, isLastCommandInTxn, isFirstCommandInAction,
				CmdRequestType.M_SERVICE_CHANGE_REQ);
		return req;
	}

	public static CommandReq MegacoCmdReqNotify(java.lang.Object source,
			int assocHandle, int txnHandle, int actionHandle,
			boolean isLastCommandInTxn, boolean isFirstCommandInAction)
			throws javax.megaco.InvalidArgumentException {
		CommandReq req = new CommandReq(source, assocHandle, txnHandle,
				actionHandle, isLastCommandInTxn, isFirstCommandInAction,
				CmdRequestType.M_NOTIFY_REQ);
		return req;
	}

	public static CommandReq MegacoCmdReqAuditCap(java.lang.Object source,
			int assocHandle, int txnHandle, int actionHandle,
			boolean isLastCommandInTxn, boolean isFirstCommandInAction)
			throws javax.megaco.InvalidArgumentException {
		CommandReq req = new CommandReq(source, assocHandle, txnHandle,
				actionHandle, isLastCommandInTxn, isFirstCommandInAction,
				CmdRequestType.M_AUDIT_CAP_REQ);
		return req;
	}

	public static CommandReq MegacoCmdReqAuditVal(java.lang.Object source,
			int assocHandle, int txnHandle, int actionHandle,
			boolean isLastCommandInTxn, boolean isFirstCommandInAction)
			throws javax.megaco.InvalidArgumentException {
		CommandReq req = new CommandReq(source, assocHandle, txnHandle,
				actionHandle, isLastCommandInTxn, isFirstCommandInAction,
				CmdRequestType.M_AUDIT_VAL_REQ);
		return req;
	}

	public static CommandReq MegacoCmdReqSubtract(java.lang.Object source,
			int assocHandle, int txnHandle, int actionHandle,
			boolean isLastCommandInTxn, boolean isFirstCommandInAction)
			throws javax.megaco.InvalidArgumentException {
		CommandReq req = new CommandReq(source, assocHandle, txnHandle,
				actionHandle, isLastCommandInTxn, isFirstCommandInAction,
				CmdRequestType.M_SUBTRACT_REQ);
		return req;
	}
}
