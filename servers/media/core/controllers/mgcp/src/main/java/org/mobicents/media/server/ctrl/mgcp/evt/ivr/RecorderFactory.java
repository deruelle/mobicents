package org.mobicents.media.server.ctrl.mgcp.evt.ivr;

import org.mobicents.media.server.ctrl.mgcp.MgcpController;
import org.mobicents.media.server.ctrl.mgcp.evt.GeneratorFactory;
import org.mobicents.media.server.ctrl.mgcp.evt.SignalGenerator;

/**
 * 
 * @author amit bhayani
 * 
 */
public class RecorderFactory implements GeneratorFactory {

	private String name;
	private String packageName;
    private String resourceName;

	public String getEventName() {
		return this.name;
	}

	public SignalGenerator getInstance(MgcpController controller, String url) {
		return new Recorder(resourceName, url);
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

}
