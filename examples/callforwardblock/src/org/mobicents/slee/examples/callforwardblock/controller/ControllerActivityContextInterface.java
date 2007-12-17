package org.mobicents.slee.examples.callforwardblock.controller;

import javax.slee.ActivityContextInterface;

public interface ControllerActivityContextInterface extends
		ActivityContextInterface {
	public ActivityContextInterface getInviteACI();
	public void setInviteACI(ActivityContextInterface aci);
}
