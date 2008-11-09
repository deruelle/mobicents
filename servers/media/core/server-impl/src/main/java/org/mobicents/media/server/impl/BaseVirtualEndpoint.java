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
package org.mobicents.media.server.impl;

import java.util.HashMap;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.mobicents.media.server.local.management.EndpointLocalManagement;
import org.mobicents.media.server.spi.Endpoint;
import org.mobicents.media.server.spi.VirtualEndpoint;

/**
 * 
 * @author Oleg Kulikov
 */
public abstract class BaseVirtualEndpoint extends BaseEndpoint implements VirtualEndpoint {
	private transient static Logger logger = Logger.getLogger(BaseVirtualEndpoint.class);
	private static int GEN = 1;
	protected HashMap<String, Endpoint> endpoints = null;
	protected boolean start=true;
	public BaseVirtualEndpoint(String localName, HashMap<String, Endpoint> endpointMap) {
		super(localName);
		this.endpoints = endpointMap;
	}

	public Endpoint createEndpoint() {
		if(start)
		{	
			String localName = this.getLocalName() + "/" + "enp-" + (GEN++);
			BaseEndpoint enp = (BaseEndpoint) doCreateEndpoint(localName);

			enp.setRtpFactoryName(this.getRtpFactoryName());

			endpoints.put(localName, enp);
			return enp;
		}else
		{
			throw new IllegalStateException("Cant create endpoints while in stoped state!!");
		}
	}

	public abstract Endpoint doCreateEndpoint(String localName);

	@Override
	public void deleteConnection(String connectionID) {

		super.deleteConnection(connectionID);
		if (!this.hasConnections()) {
			if (logger.isDebugEnabled()) {
				logger.debug("Removed endpoint=" + this.getLocalName());
			}
			endpoints.remove(this.getLocalName());
		}

	}

	public Endpoint getEndpoint(String localName) {
		return endpoints.get(localName);
	}

	public String[] getEndpointsNames() {

		SortedSet<String> s = new TreeSet<String>();
		s.addAll(endpoints.keySet());
		return s.toArray(new String[s.size()]);
	}

	public EndpointLocalManagement[] getEndpoints() {

		EndpointLocalManagement[] value = this.endpoints.values().toArray(
				new EndpointLocalManagement[this.endpoints.values().size()]);

		return value;
	}

	public String[] getEndpointNames() {
		return this.endpoints.keySet().toArray(new String[this.endpoints.keySet().size()]);
	}

	public String toString() {
		return this.getClass().getSimpleName() + ":" + endpoints;
	}

	public void start() {
		this.start=true;
		
	}

	public void stop() {
		this.start=false;
		
	}

	
	
	
}
