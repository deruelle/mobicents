package org.mobicents.ipbx.session;

import javax.servlet.sip.SipSession;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;

@Name("callActionConnectionHandler")
@Scope(ScopeType.STATELESS)
public class CallActionConnectionHandler {
	@In SipSession sipSession;
	@Logger 
	private static Log logger;
	
	/*
	@Observer("connectionHalfOpen")
	public void connectionHalfOpen(MsConnectionEvent event) {
		logger.info("Making a call on " + sipSession.toString());
		MsConnection connection = event.getConnection();
		String sdp = connection.getLocalDescriptor();

		SipServletRequest inviteRequest = (SipServletRequest) sipSession.getAttribute("inviteRequest");
		try {
			inviteRequest.setContentLength(sdp.length());
			inviteRequest.setContent(sdp.getBytes(), "application/sdp");						
			inviteRequest.send();
		} catch (Exception e) {
			logger.error("An unexpected exception occured while sending the request", e);
		}	
	}*/
}
