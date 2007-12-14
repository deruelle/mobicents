package org.mobicents.slee.resource.sip.wrappers;

import javax.sip.ClientTransaction;
import javax.sip.Dialog;
import javax.sip.ObjectInUseException;
import javax.sip.SipException;
import javax.sip.Transaction;
import javax.sip.TransactionState;
import javax.sip.message.Request;

public class ClientTransactionWrapper implements javax.sip.ClientTransaction,SecretWrapperInterface{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private ClientTransaction realTransaction=null;
	private Object applicationData=null;
	private DialogWrapper dialogWrapper;
	
	public ClientTransactionWrapper(ClientTransaction CT, DialogWrapper dialogWrapper)
	{
		this.realTransaction=CT;
		CT.setApplicationData(this);
		this.dialogWrapper=dialogWrapper;
	}
	
	public Transaction getRealTransaction()
	{
		return realTransaction;
	}
	
	public void setDialogWrapper(DialogWrapper dialogWrapper) {
		this.dialogWrapper = dialogWrapper;
	}
	
	public void sendRequest() throws SipException {
		realTransaction.sendRequest();
		if(dialogWrapper!=null) {
			dialogWrapper.renew();
		}
	}

	public Request createCancel() throws SipException {
		
		return realTransaction.createCancel();
	}

	public Request createAck() throws SipException {
		
		return realTransaction.createAck();
	}

	public Dialog getDialog() {
		// TODO Auto-generated method stub
		return dialogWrapper;
	}

	public TransactionState getState() {
		// TODO Auto-generated method stub
		return realTransaction.getState();
	}

	public int getRetransmitTimer() throws UnsupportedOperationException {
		// TODO Auto-generated method stub
		return realTransaction.getRetransmitTimer();
	}

	public void setRetransmitTimer(int arg0) throws UnsupportedOperationException {
		realTransaction.setRetransmitTimer(arg0);
		
	}

	public String getBranchId() {
		//LEAK BUG PATCH
		return realTransaction.getBranchId();
		//return realTransaction.getBranchId()+"_"+realTransaction.getRequest().getMethod();
	}

	public Request getRequest() {
		// TODO Auto-generated method stub
		return realTransaction.getRequest();
	}

	public void setApplicationData(Object appData) {
		this.applicationData=appData;
		
	}

	public Object getApplicationData() {
		
		return applicationData;
	}

	public void terminate() throws ObjectInUseException {
		realTransaction.terminate();
		
	}
	
	public String toString()
	{
		return "[TransactionW["+super.toString()+"] WRAPPED["+realTransaction+"] BRANCHID["+realTransaction.getBranchId()+"] STATE["+realTransaction.getState()+"] DIALOG{ "+dialogWrapper+" } ]";
	}
}
