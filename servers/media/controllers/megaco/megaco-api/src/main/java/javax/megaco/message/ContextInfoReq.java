package javax.megaco.message;

import javax.megaco.CommandEvent;
import javax.megaco.InvalidArgumentException;

/**
 * The class extends JAIN MEGACO Command Events. This class is used to represent
 * megaco action request event i.e. when an action comes or is to be sent
 * without a command, but has context parameters.
 * 
 * 
 */
public class ContextInfoReq extends CommandEvent {

	public ContextInfoReq(java.lang.Object source, int assocHandle,
			int transactionHandle, int actionHandle,
			boolean isLastActionInTransaction)
			throws javax.megaco.InvalidArgumentException {
		super(source, assocHandle, transactionHandle, actionHandle, false,
				isLastActionInTransaction);

		if (transactionHandle < 0 || actionHandle < 0) {
			InvalidArgumentException invalidArgumentException = new InvalidArgumentException(
					"txnHandle or actionHandle cannot be less than 0 for ContextInfoReq");
			invalidArgumentException.setAssocHandle(assocHandle);
			throw invalidArgumentException;
		}
	}

	@Override
	public int getCommandIdentifier() {
		// TODO Auto-generated method stub
		return CommandType.M_ACTION_REQ;
	}

}
