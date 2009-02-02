package javax.megaco.message;

import javax.megaco.CommandEvent;
import javax.megaco.InvalidArgumentException;

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

	public ContextInfoResp(java.lang.Object source, int assocHandle,
			int txnHandle, int actionHandle, boolean isLastActionInTxn)
			throws javax.megaco.InvalidArgumentException {
		super(source, assocHandle, txnHandle, actionHandle, false,
				isLastActionInTxn);
		if (txnHandle < 0 || actionHandle < 0) {
			InvalidArgumentException invalidArgumentException = new InvalidArgumentException(
					"txnHandle or actionHandle cannot be less than 0 for ContextInfoResp");
			invalidArgumentException.setAssocHandle(assocHandle);
			throw invalidArgumentException;
		}
	}

	@Override
	public int getCommandIdentifier() {
		return CommandType.M_ACTION_RESP;
	}

}
