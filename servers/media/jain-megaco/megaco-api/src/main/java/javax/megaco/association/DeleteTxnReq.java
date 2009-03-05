package javax.megaco.association;

import javax.megaco.AssociationEvent;


public class DeleteTxnReq extends AssociationEvent {

	private int txnHandle = -1;

	public DeleteTxnReq(Object source, int assocHandle)
			throws IllegalArgumentException {
		super(source, assocHandle);
		// TODO Auto-generated constructor stub
	}

	@Override
	public final int getAssocOperIdentifier() {

		return AssocEventType.M_DELETE_TXN_REQ;
	}

	/**
	 * Gets an object identifier that specifies the transaction identifier. If
	 * the transaction identifier is set to 0, then this would be the case when
	 * the transaction identifier is to represent all transactions.<br><br>
	 * If the transaction identifier is not set, then this method returns 0, indicating all transactions. 
	 * 
	 * @return Returns an integer value that specifies the transaction identifier.
	 */
	public final int getTxnHandle() {
		if(txnHandle==-1)
			return 0;
		
		return txnHandle;
		
	}
	
	/**
	 * This method sets the transaction identifier. To delete all transactions, the transaction identifier is set to 0. 
	 * @param transactionHandle A reference to transaction identifier.
	 * @throws IllegalArgumentException This exception is raised if the value of transaction handle passed to this method is less than 0.
	 */
	public final void setTxnHandle(int transactionHandle)     throws IllegalArgumentException
	{
		
		if(transactionHandle<0)
			throw new IllegalArgumentException("Txn Handle can not be less than zero");
		
		this.txnHandle=transactionHandle;
	}

}
