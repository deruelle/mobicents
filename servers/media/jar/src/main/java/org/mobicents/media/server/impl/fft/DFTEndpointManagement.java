package org.mobicents.media.server.impl.fft;

import org.mobicents.media.server.impl.jmx.EndpointManagement;
import org.mobicents.media.server.spi.Endpoint;

public class DFTEndpointManagement extends EndpointManagement implements
		DFTEndpointManagementMBean {

	@Override
	public Endpoint createEndpoint() throws Exception {
		// TODO Auto-generated method stub
		return new DFTEndpointImpl(getJndiName());
	}

}
