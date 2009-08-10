package org.mobicents.media.server.ctrl.mgcp.evt.tone;

import org.mobicents.media.server.ctrl.mgcp.MgcpController;
import org.mobicents.media.server.ctrl.mgcp.evt.GeneratorFactory;
import org.mobicents.media.server.ctrl.mgcp.evt.SignalGenerator;

/**
 * 
 * @author amit.bhayani
 *
 */
public class MultiFreqToneGeneratorFactory implements GeneratorFactory {
	private String name;
	private String packageName;

	private String resourceName;
	private int eventID;


	public MultiFreqToneGeneratorFactory() {
	}

	public String getEventName() {
		return this.name;
	}

	public String getPackageName() {
		return this.packageName;
	}

	public void setEventName(String eventName) {
		this.name = eventName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getResourceName() {
		return resourceName;
	}

	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}



	public SignalGenerator getInstance(MgcpController controller, String parms) {
		return new MultiFreqToneGenerator(this.resourceName, parms);
	}

}
