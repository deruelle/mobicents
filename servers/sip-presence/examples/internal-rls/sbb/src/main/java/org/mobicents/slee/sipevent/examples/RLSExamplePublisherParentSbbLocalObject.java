package org.mobicents.slee.sipevent.examples;

import javax.slee.SbbLocalObject;

public interface RLSExamplePublisherParentSbbLocalObject extends SbbLocalObject {

	public void publisherStarted(String publisher);
	
	public void publisherNotStarted(String publisher);
	
}
