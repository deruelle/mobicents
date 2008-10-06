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
import org.mobicents.media.server.spi.Endpoint;
import org.mobicents.media.server.spi.VirtualEndpoint;

/**
 * 
 * @author Oleg Kulikov
 */
public abstract class BaseVirtualEndpoint extends BaseEndpoint implements VirtualEndpoint {

	private static int GEN = 1;
	private HashMap<String, Endpoint> endpoints = new HashMap();

	public BaseVirtualEndpoint(String localName) {
		super(localName);
	}

	public Endpoint createEndpoint() {
		String localName = this.getLocalName() + "/" + "enp-" + (GEN++);
		BaseEndpoint enp = (BaseEndpoint) doCreateEndpoint(localName);

		enp.setRtpFactoryName(this.getRtpFactoryName());

		endpoints.put(localName, enp);
		return enp;
	}

	public abstract Endpoint doCreateEndpoint(String localName);

	@Override
	public void deleteConnection(String connectionID) {
		super.deleteConnection(connectionID);
		if (!this.hasConnections()) {
                    System.out.println("Removed endpoint=" + this.getLocalName());
			endpoints.remove(this.getLocalName());
		}
                System.out.println("endpoints: " + endpoints);
	}

	public Endpoint getEndpoint(String localName) {
		return endpoints.get(localName);
	}

}
