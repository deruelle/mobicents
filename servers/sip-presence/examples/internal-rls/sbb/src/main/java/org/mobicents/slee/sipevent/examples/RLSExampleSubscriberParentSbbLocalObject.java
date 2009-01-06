package org.mobicents.slee.sipevent.examples;

import javax.slee.SbbLocalObject;

public interface RLSExampleSubscriberParentSbbLocalObject extends SbbLocalObject {

	public void subscriberStarted();
	
	public void subscriberNotStarted();

	public void subscriberStopped();
	
}
