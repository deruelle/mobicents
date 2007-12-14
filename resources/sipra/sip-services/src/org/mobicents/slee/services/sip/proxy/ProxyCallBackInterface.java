package org.mobicents.slee.services.sip.proxy;

import javax.sip.ClientTransaction;
import javax.sip.ServerTransaction;
import javax.sip.SipException;
import javax.sip.header.ViaHeader;
import javax.sip.message.Request;
import javax.sip.message.Response;

public interface ProxyCallBackInterface {

	//We relly on external timers
	void setCTimer(ClientTransaction ctx,double seconds);
	void updateCTimer(ClientTransaction ctx,double seconds);
	void cancelCTimer(ClientTransaction ctx);
	
	
	//We need to allow something external to play with sending, in case it needs to to its magic
	public  ClientTransaction sendRequest(Request request, boolean waitForResponse) throws SipException;
	public  void sendStatelessRequest(Request request) throws SipException;
	public  void sendStatelessResponse(Response response) throws SipException;
    
	public  void endCallProcessing();
    
	public  int getFinishedBranches();
	public void setFinishedBranches(int active);
	
	public void setForwardedInviteViaHeader(ViaHeader value);
	
	public ViaHeader getForwardedInviteViaHeader();
	
}
