package org.mobicents.media.server.impl.resource.dtmf;

import org.mobicents.media.Component;
import org.mobicents.media.ComponentFactory;
import org.mobicents.media.server.spi.Endpoint;
import org.mobicents.media.server.spi.ResourceUnavailableException;

/**
 * 
 * @author amit bhayani
 *
 */
public class InbandDetectorFactory implements ComponentFactory {
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Component newInstance(Endpoint endpoint)  throws ResourceUnavailableException {
		InbandDetectorImpl inbandDetector = new InbandDetectorImpl(this.name);
		inbandDetector.setEndpoint(endpoint);
		return inbandDetector;
	}

}
