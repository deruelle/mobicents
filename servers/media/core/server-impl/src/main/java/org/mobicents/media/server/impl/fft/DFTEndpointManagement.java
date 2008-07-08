package org.mobicents.media.server.impl.fft;

import org.apache.log4j.Logger;
import org.mobicents.media.server.impl.jmx.EndpointManagement;
import org.mobicents.media.server.impl.jmx.EndpointManagementMBean;
import org.mobicents.media.server.impl.packetrelay.PREndpointManagement;
import org.mobicents.media.server.spi.Endpoint;

public class DFTEndpointManagement extends EndpointManagement implements
		DFTEndpointManagementMBean {
	
	private Logger logger = Logger.getLogger(PREndpointManagement.class);

	@Override
	public Endpoint createEndpoint() throws Exception {
		// TODO Auto-generated method stub
		return new DFTEndpointImpl(getJndiName());
	}
	
	public EndpointManagementMBean cloneEndpointManagementMBean() {
		DFTEndpointManagement clone = new DFTEndpointManagement();
		try {
			clone.setJndiName(this.getJndiName());
			clone.setBindAddress(this.getBindAddress());
			clone.setJitter(this.getJitter());
			clone.setPacketizationPeriod(this.getPacketizationPeriod());
			clone.setPortRange(this.getPortRange());
			clone.setPCMA(this.getPCMA());
			clone.setPCMU(this.getPCMU());
			clone.setSpeex(this.getSpeex());
			clone.setDTMF(this.getDTMF());
		} catch (Exception ex) {
			logger.error("PREndpointManagement clonning failed ", ex);
			return null;
		}
		return clone;
	}         	

}
