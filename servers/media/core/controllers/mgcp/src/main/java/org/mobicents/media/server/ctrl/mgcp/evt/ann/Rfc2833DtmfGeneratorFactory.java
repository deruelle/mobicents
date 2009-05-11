package org.mobicents.media.server.ctrl.mgcp.evt.ann;

import org.mobicents.media.server.ctrl.mgcp.MgcpController;
import org.mobicents.media.server.ctrl.mgcp.evt.GeneratorFactory;
import org.mobicents.media.server.ctrl.mgcp.evt.SignalGenerator;

/**
 * 
 * @author amit bhayani
 * 
 */
public class Rfc2833DtmfGeneratorFactory implements GeneratorFactory {

	private String name;
	private String packageName;

	private String resourceName;
	private int eventID;

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
    
    public void setResourceID(String resourceName) {
        this.resourceName = resourceName;
    }

	public SignalGenerator getInstance(MgcpController controller, String digit) {
		return new Rfc2833DtmfGenerator(resourceName, digit);
	}

	/**
	 * Call the factory method to create instance of Rfc2833DtmfGenerator with
	 * non default value of duration and volume.
	 * 
	 * @param controller
	 * @param digit
	 * @param duration
	 * @param volume
	 * @return
	 */
	public SignalGenerator getInstance(MgcpController controller, String digit, int duration, int volume) {
		return new Rfc2833DtmfGenerator(resourceName, digit, duration, volume);
	}

}
