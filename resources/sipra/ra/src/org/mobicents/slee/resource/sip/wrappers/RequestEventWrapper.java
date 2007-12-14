package org.mobicents.slee.resource.sip.wrappers;

import javax.sip.Dialog;
import javax.sip.RequestEvent;
import javax.sip.ServerTransaction;
import javax.sip.message.Request;
//Minor cheat, so we can hide it
//javax.sip.RequestEvent is not finall but it has private fields 
/**
 * @author B. Baranowski
 */
public class RequestEventWrapper extends RequestEvent{

	
	//private Object source=null;
	//private ServerTransaction serverTx=null;
	//private Dialog dialog=null;
	//private Request event=null;
	public RequestEventWrapper(Object source, ServerTransaction serverTx, Dialog dialog, Request event) {
		//we have to to this.
		super(source, serverTx, dialog, event);
		//this.source=source;
		//this.serverTx=serverTx;
		//this.dialog=dialog;
		//this.event=event;
		// TODO Auto-generated constructor stub
		
	}
	/*
	public Dialog 	getDialog()
	{
		return this.dialog;
	}
    
public Request 	getRequest()
{
	return this.event;
	}
  public ServerTransaction 	getServerTransaction()
  {
	  return this.serverTx;
  }
  
  //hmm some unwanted methods 
  public void setDialog(Dialog dialog)
  {
	  this.dialog=dialog;
  }
  public void setServerTransaction(ServerTransaction tx)
  {
	  this.serverTx=tx;
  }*/
}
