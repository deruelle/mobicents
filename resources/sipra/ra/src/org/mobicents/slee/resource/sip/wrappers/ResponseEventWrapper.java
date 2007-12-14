package org.mobicents.slee.resource.sip.wrappers;

import javax.sip.ClientTransaction;
import javax.sip.Dialog;
import javax.sip.ResponseEvent;
import javax.sip.message.Response;

public class ResponseEventWrapper extends ResponseEvent{

	//private ClientTransaction clientTx=null;
	//private Dialog dialog=null;
	//private Response event=null;
	
	public ResponseEventWrapper(Object source, ClientTransaction clientTx, Dialog dialog, Response event) {
		super(source, clientTx, dialog, event);
		//this.dialog=dialog;
		//this.event=event;
		//this.clientTx=clientTx;
	}
	//public ResponseEventWrapper(Object source, ClientTransaction clientTx, Response event) {
	//	super(source, clientTx, event);
		//this.event=event;
		//this.clientTx=clientTx;
		// TODO Auto-generated constructor stub
	//}
	/*
	public Dialog 	getDialog()
	{
		return this.dialog;
	}
	public Response getResponse()
	{
		return event;
	}
	public ClientTransaction getClientTransaction()
	{
		return clientTx;
	}
	
//	hmm some unwanted methods 
	  public void setDialog(Dialog dialog)
	  {
		  this.dialog=dialog;
	  }
	  public void setClientTransaction(ClientTransaction tx)
	  {
		  this.clientTx=tx;
	  }*/
}
