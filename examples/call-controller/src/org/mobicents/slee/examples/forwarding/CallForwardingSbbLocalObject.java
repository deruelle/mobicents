package org.mobicents.slee.examples.forwarding;

import javax.sip.address.Address;
import javax.slee.ActivityContextInterface;

public interface CallForwardingSbbLocalObject extends javax.slee.SbbLocalObject {

	// TODO: Any methods defined here must be implemented in CallForwardingSbb.java
	
	/**
	 * TRUE: If the called user is available. <br>
	 * FALSE: If the called user is not available.
	 * 
	 * @param event: It will be an INVITE.
	 */
	public boolean accept(javax.sip.RequestEvent event);
	
	/**
	 * Address: if the called user is subscribed for Call Forwarding and <br>
	 * there is a backup address available.
	 * 
	 * null: if the called user is not subscribed to Call Forwarding or there
	 * isn't any backup address available.
	 * 
	 * @param event: It will be an INVITE.
	 * @param ac: To send MOVED_TEMPORARILY to the client if the INVITE is redirected.
	 */
	public Address forwarding(javax.sip.RequestEvent event, ActivityContextInterface ac);
}
