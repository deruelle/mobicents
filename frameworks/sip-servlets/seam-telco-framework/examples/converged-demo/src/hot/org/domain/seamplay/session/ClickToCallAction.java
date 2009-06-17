package org.domain.seamplay.session;

import java.io.IOException;

import javax.servlet.sip.ServletParseException;
import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipFactory;
import javax.servlet.sip.SipServletRequest;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("clickToCallAction")
@Scope(ScopeType.STATELESS)
public class ClickToCallAction {
	@In SipFactory sipFactory;
	@In CallURI callURI;
	public void call() throws ServletParseException, IOException {
		SipApplicationSession appSession = sipFactory.createApplicationSession();
		SipServletRequest request = sipFactory.createRequest(appSession,
				"INVITE",
				"sip:seam-telco-framework@mobicents.org",
				callURI.getUri());
		request.send();
	}
}
