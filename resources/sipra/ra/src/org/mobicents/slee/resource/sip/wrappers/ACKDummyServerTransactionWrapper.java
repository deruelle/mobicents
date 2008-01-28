package org.mobicents.slee.resource.sip.wrappers;

import javax.sip.Dialog;
import javax.sip.InvalidArgumentException;
import javax.sip.ObjectInUseException;
import javax.sip.ServerTransaction;
import javax.sip.SipException;
import javax.sip.Transaction;
import javax.sip.TransactionState;
import javax.sip.header.ViaHeader;
import javax.sip.message.Request;
import javax.sip.message.Response;

import org.apache.log4j.Logger;
import org.mobicents.slee.resource.sip.SipActivityHandle;
import org.mobicents.slee.resource.sip.SipResourceAdaptor;

public class ACKDummyServerTransactionWrapper implements ServerTransaction,
		SecretWrapperInterface {

	private static Logger logger = Logger
			.getLogger(ACKDummyServerTransactionWrapper.class);
	// this holds reference to DialogWrapper
	private Dialog dialog = null;
	private Request request = null;

	private String branchID = null;
	private SipActivityHandle activityHandle=null;
	public ACKDummyServerTransactionWrapper(Dialog dialog, Request request) {
		super();
		this.dialog = dialog;
		this.request = request;

		try {
			branchID = ((ViaHeader) request.getHeaders(ViaHeader.NAME).next())
					.getBranch();
			this.activityHandle=new SipActivityHandle(branchID+"_"+request.getMethod());
		} catch (Exception e) {

		}
		
	}

	public void enableRetransmissionAlerts() throws SipException {

	}

	public void sendResponse(Response arg0) throws SipException,
			InvalidArgumentException {

	}

	public Object getApplicationData() {

		return null;
	}

	public String getBranchId() {

		return branchID;
	}

	public Dialog getDialog() {

		return this.dialog;
	}

	public Request getRequest() {

		return this.request;
	}

	public int getRetransmitTimer() throws UnsupportedOperationException {

		throw new UnsupportedOperationException("[OPERATION NOT PERMITED]ACK is pseduo transaction, it cant be used as TX for other methods!!!");
	}

	public TransactionState getState() {

		return TransactionState.TERMINATED;
	}

	public void setApplicationData(Object arg0) {

		throw new RuntimeException(
				"[OPERATION NOT PERMITED]ACK is pseduo transaction, it cant be used as TX for other methods!!!");
	}

	public void setRetransmitTimer(int arg0)
			throws UnsupportedOperationException {
		throw new RuntimeException(
				"[OPERATION NOT PERMITED]ACK is pseduo transaction, it cant be used as TX for other methods!!!");

	}

	public void terminate() throws ObjectInUseException {
		throw new RuntimeException(
				"[OPERATION NOT PERMITED]ACK is pseduo transaction, it cant be used as TX for other methods!!!");

	}

	public Transaction getRealTransaction() {

		return null;
	}

	public void setDialogWrapper(DialogWrapper wrapperDialog) {
		dialog = wrapperDialog;

	}

	@Override
	protected void finalize() throws Throwable {

		this.dialog = null;
		this.request = null;
		super.finalize();

	}

	public SipActivityHandle getActivityHandle() {

		return this.activityHandle;
	}

}
