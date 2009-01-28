package javax.megaco.message;

import javax.megaco.CommandEvent;
import javax.megaco.InvalidArgumentException;

public class CommandResp extends CommandEvent {

	private int cmdRespIdentifier;
	private boolean isLastCommandResp;

	private CommandResp(java.lang.Object source, int assocHandle,
			int txnHandle, int actionHandle, boolean isLastCommandResp,
			boolean isFirstCommandInAction, int cmdRespIdentifier)
			throws javax.megaco.InvalidArgumentException {
		super(source, assocHandle, txnHandle, actionHandle, isLastCommandResp,
				isFirstCommandInAction);
		if (txnHandle < 0 || actionHandle < 0) {
			InvalidArgumentException invalidArgumentException = new InvalidArgumentException(
					"txnHandle or actionHandle cannot be less than 0 for CommandReq");
			invalidArgumentException.setAssocHandle(assocHandle);
			throw invalidArgumentException;
		}

		this.cmdRespIdentifier = cmdRespIdentifier;
	}

	@Override
	public int getCommandIdentifier() {
		return CommandType.M_ACTION_RESP;
	}

	public int getResponseIdentifier() {
		return this.cmdRespIdentifier;
	}

	public boolean isLastCommandResp() {
		return this.isLastCommandResp;
	}

	public void setLastCommandResp() {
		this.isLastCommandResp = true;
	}

	public static CommandResp MegacoCmdRespAdd(java.lang.Object source,
			int assocHandle, int txnHandle, int actionHandle,
			boolean isLastCommandResp, boolean isFirstCommandInAction)
			throws javax.megaco.InvalidArgumentException {
		CommandResp resp = new CommandResp(source, assocHandle, txnHandle,
				actionHandle, isLastCommandResp, isFirstCommandInAction,
				CmdResponseType.M_ADD_RESP);
		return resp;
	}

	public static CommandResp MegacoCmdRespModify(java.lang.Object source,
			int assocHandle, int txnHandle, int actionHandle,
			boolean isLastCommandResp, boolean isFirstCommandInAction)
			throws javax.megaco.InvalidArgumentException {
		CommandResp resp = new CommandResp(source, assocHandle, txnHandle,
				actionHandle, isLastCommandResp, isFirstCommandInAction,
				CmdResponseType.M_MODIFY_RESP);
		return resp;
	}

	public static CommandResp MegacoCmdRespMove(java.lang.Object source,
			int assocHandle, int txnHandle, int actionHandle,
			boolean isLastCommandResp, boolean isFirstCommandInAction)
			throws javax.megaco.InvalidArgumentException {
		CommandResp resp = new CommandResp(source, assocHandle, txnHandle,
				actionHandle, isLastCommandResp, isFirstCommandInAction,
				CmdResponseType.M_MOVE_RESP);
		return resp;
	}

	public static CommandResp MegacoCmdRespSrvChng(java.lang.Object source,
			int assocHandle, int txnHandle, int actionHandle,
			boolean isLastCommandResp, boolean isFirstCommandInAction)
			throws javax.megaco.InvalidArgumentException {
		CommandResp resp = new CommandResp(source, assocHandle, txnHandle,
				actionHandle, isLastCommandResp, isFirstCommandInAction,
				CmdResponseType.M_SERVICE_CHANGE_RESP);
		return resp;
	}

	public static CommandResp MegacoCmdRespNotify(java.lang.Object source,
			int assocHandle, int txnHandle, int actionHandle,
			boolean isLastCommandResp, boolean isFirstCommandInAction)
			throws javax.megaco.InvalidArgumentException {
		CommandResp resp = new CommandResp(source, assocHandle, txnHandle,
				actionHandle, isLastCommandResp, isFirstCommandInAction,
				CmdResponseType.M_NOTIFY_RESP);
		return resp;
	}

	public static CommandResp MegacoCmdRespAuditCap(java.lang.Object source,
			int assocHandle, int txnHandle, int actionHandle,
			boolean isLastCommandResp, boolean isFirstCommandInAction)
			throws javax.megaco.InvalidArgumentException {
		CommandResp resp = new CommandResp(source, assocHandle, txnHandle,
				actionHandle, isLastCommandResp, isFirstCommandInAction,
				CmdResponseType.M_AUDIT_CAP_RESP);
		return resp;
	}

	public static CommandResp MegacoCmdRespAuditVal(java.lang.Object source,
			int assocHandle, int txnHandle, int actionHandle,
			boolean isLastCommandResp, boolean isFirstCommandInAction)
			throws javax.megaco.InvalidArgumentException {
		CommandResp resp = new CommandResp(source, assocHandle, txnHandle,
				actionHandle, isLastCommandResp, isFirstCommandInAction,
				CmdResponseType.M_AUDIT_VAL_RESP);
		return resp;
	}

	public static CommandResp MegacoCmdRespSubtract(java.lang.Object source,
			int assocHandle, int txnHandle, int actionHandle,
			boolean isLastCommandResp, boolean isFirstCommandInAction)
			throws javax.megaco.InvalidArgumentException {
		CommandResp resp = new CommandResp(source, assocHandle, txnHandle,
				actionHandle, isLastCommandResp, isFirstCommandInAction,
				CmdResponseType.M_SUBTRACT_RESP);
		return resp;
	}
}
