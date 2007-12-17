package org.mobicents.slee.service.callcontrol;

import javax.sip.ResponseEvent;
import javax.slee.SbbLocalObject;

public interface CallControlSbbLocalObject extends SbbLocalObject {
	
	public void setParent(SbbLocalObject sbbLocalObject);
	
	public ResponseEvent getResponseEvent();
	
	public void sendBye();

}
