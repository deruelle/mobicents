package org.mobicents.slee.sipevent.examples;

import org.mobicents.slee.sippresence.client.PresenceClientControlParentSbbLocalObject;
import org.mobicents.slee.xdm.server.XDMClientControlParentSbbLocalObject;

public interface RLSExampleSubscriberSbbLocalObject extends PresenceClientControlParentSbbLocalObject,XDMClientControlParentSbbLocalObject {

	/**
	 * Stores the parent sbb local object, to be used on callbacks
	 * @param parentSbb
	 */
	public void setParentSbb(RLSExampleSubscriberParentSbbLocalObject parentSbb);
	
	/**
	 * Creates resource list in xdm with the specified entries, then subscribes its uri
	 */
	public void start(String[] entryURIs);
	
	/**
	 * Unsubscribes resource list and removes it from the xdm
	 */
	public void stop();
	
}
