package org.mobicents.slee.examples.blocking;

import javax.slee.ActivityContextInterface;

public interface CallBlockingSbbLocalObject extends javax.slee.SbbLocalObject {

	// TODO: Any methods defined here must be implemented in CallBlockingSbb.java
	
	/**
	 * TRUE: If the INVITE is blocked. <br>
	 * FALSE: If the INVITE is not blocked.
	 * 
	 * @param event: It will be an INVITE.
	 * @param ac: To send FORBIDDEN to the client if the INVITE is blocked.
	 */
	public boolean accept(javax.sip.RequestEvent event, ActivityContextInterface ac);
}
