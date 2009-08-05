package org.mobicents.media.server.impl.resource.audio.soundcard;

import org.mobicents.media.Component;
import org.mobicents.media.ComponentFactory;
import org.mobicents.media.server.spi.Endpoint;
import org.mobicents.media.server.spi.ResourceUnavailableException;

/**
 * 
 * @author amit bhayani
 *
 */
//TODO : Add section for hardware selection that can be configured through *-beans.xml
public class PlayerFactory implements ComponentFactory {

	private String name;

	public Component newInstance(Endpoint endpoint) throws ResourceUnavailableException {
		PlayerImpl p = new PlayerImpl(this.name);
		p.setEndpoint(endpoint);
		return p;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
