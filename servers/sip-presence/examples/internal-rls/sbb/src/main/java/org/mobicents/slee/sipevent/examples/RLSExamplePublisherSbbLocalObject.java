package org.mobicents.slee.sipevent.examples;

import org.mobicents.slee.sippresence.client.PresenceClientControlParentSbbLocalObject;

public interface RLSExamplePublisherSbbLocalObject extends PresenceClientControlParentSbbLocalObject {

	/**
	 * Stores the parent sbb local object, to be used on callbacks
	 * @param parentSbb
	 */
	public void setParentSbb(RLSExamplePublisherParentSbbLocalObject parentSbb);
	
	/**
	 * Starts publishing for the specified publisher...
	 * @param publisher
	 */
	public void start(String publisher);
	
	/**
	 * Stops publishing...
	 */
	public void stop();
	
}
