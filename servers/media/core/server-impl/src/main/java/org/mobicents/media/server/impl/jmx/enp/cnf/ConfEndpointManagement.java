/*
 * Mobicents Media Gateway
 *
 * The source code contained in this file is in in the public domain.
 * It can be used in any project or product without prior permission,
 * license or royalty payments. There is  NO WARRANTY OF ANY KIND,
 * EXPRESS, IMPLIED OR STATUTORY, INCLUDING, WITHOUT LIMITATION,
 * THE IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE,
 * AND DATA ACCURACY.  We do not warrant or make any representations
 * regarding the use of the software or the  results thereof, including
 * but not limited to the correctness, accuracy, reliability or
 * usefulness of the software.
 */
package org.mobicents.media.server.impl.jmx.enp.cnf;

import java.util.HashMap;

import org.mobicents.media.server.impl.enp.cnf.ConfEndpointImpl;
import org.apache.log4j.Logger;
import org.mobicents.media.server.impl.jmx.EndpointManagement;
import org.mobicents.media.server.impl.jmx.EndpointManagementMBean;
import org.mobicents.media.server.spi.Endpoint;
import org.mobicents.media.server.spi.VirtualEndpoint;

/**
 * 
 * @author Oleg Kulikov
 */
public class ConfEndpointManagement extends EndpointManagement implements ConfEndpointManagementMBean {

	private transient Logger logger = Logger.getLogger(ConfEndpointManagement.class);
	private HashMap<String, Endpoint> endpointsMap = new HashMap<String, Endpoint>();

	@Override
	public Endpoint createEndpoint() throws Exception {

		return new ConfEndpointImpl(this.getJndiName(), endpointsMap);
	}

	@Override
	public EndpointManagementMBean cloneEndpointManagementMBean() {
		ConfEndpointManagement clone = new ConfEndpointManagement();
		try {
			clone.setJndiName(this.getJndiName());
		} catch (Exception ex) {
			logger.error("ConfEndpointManagement clonning failed ", ex);
			return null;
		}
		return clone;
	}

	public void makeEndpoint() {
		try {
			((VirtualEndpoint) super.endpoint).createEndpoint();
		} catch (Exception e) {
			logger.error("Creation of VirtualEndpoint failed", e);
		}

	}
}
