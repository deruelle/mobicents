package org.mobicents.media.server.ctrl.mgcp.evt.tone;

import jain.protocol.ip.mgcp.message.parms.RequestedAction;

import org.mobicents.media.server.ctrl.mgcp.evt.DetectorFactory;
import org.mobicents.media.server.ctrl.mgcp.evt.EventDetector;

/**
 * 
 * @author amit.bhayani
 *
 */
public class MultiFreqToneDetectorFactory implements DetectorFactory {
	private String packageName;
	private String name;

	private String resourceName;
	private int eventID;

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public void setEventName(String name) {
		this.name = name;
	}

	public String getEventName() {
		return name;
	}

	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

	public String getResourceName() {
		return resourceName;
	}

	public int getEventID() {
		return eventID;
	}

	public void setEventID(int eventID) {
		this.eventID = eventID;
	}

	public EventDetector getInstance(String params, RequestedAction[] actions) {
		return new MultiFreqToneDetector(packageName, name, resourceName, eventID, params, actions);
	}

}
