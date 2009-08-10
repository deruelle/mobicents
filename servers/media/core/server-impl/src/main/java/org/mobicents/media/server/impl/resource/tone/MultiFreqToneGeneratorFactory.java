package org.mobicents.media.server.impl.resource.tone;

import org.mobicents.media.Component;
import org.mobicents.media.ComponentFactory;
import org.mobicents.media.server.spi.Endpoint;
import org.mobicents.media.server.spi.ResourceUnavailableException;

/**
 * 
 * @author amit.bhayani
 *
 */
public class MultiFreqToneGeneratorFactory implements ComponentFactory {

	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Component newInstance(Endpoint endpoint) throws ResourceUnavailableException {
		MultiFreqToneGeneratorImpl gen = new MultiFreqToneGeneratorImpl(this.name, endpoint.getTimer());
		gen.setEndpoint(endpoint);
		return gen;
	}

}
