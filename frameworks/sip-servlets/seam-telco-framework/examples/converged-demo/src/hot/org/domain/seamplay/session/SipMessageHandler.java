package org.domain.seamplay.session;

import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;

@Name("sipMessageHandler")
@Scope(ScopeType.STATELESS)
public class SipMessageHandler {
	
	/*
	 * This method display a messing the the browser asynchronously with
	 * server push.
	 */
	private void displayMessage(String message) {
		StaticSharedStore.messages.offer(message);
		if(StaticSharedStore.messages.size()>20) {
			StaticSharedStore.messages.poll();
		}
		
		// makeDirty is what actually causes the refresh
		StaticSharedStore.makeDirty();
	}
	
	@Observer("INVITE")
	public void doInvite(SipServletRequest request) throws Exception {
		displayMessage("INVITE from " + request.getFrom());
		request.createResponse(180).send();
		request.createResponse(200).send();
	}
	
	@Observer("BYE")
	public void doBye(SipServletRequest request) throws Exception {
		displayMessage("BYE from " + request.getFrom());
		request.createResponse(200).send();
	}
	
	@Observer("REGISTER")
	public void doRegister(SipServletRequest request) throws Exception {
		displayMessage("REGISTER from " + request.getFrom());
		request.createResponse(200).send();
	}
	
	@Observer("RESPONSE")
	public void doRegister(SipServletResponse response) throws Exception {
		if(response.getMethod().equalsIgnoreCase("INVITE")) {
			if(response.getStatus()>=200 && response.getStatus()<300) {
				response.createAck().send();
			}
		}
	}
}
