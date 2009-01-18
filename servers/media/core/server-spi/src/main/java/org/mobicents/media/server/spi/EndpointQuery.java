/*
 * AnyQuery.java
 *
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
package org.mobicents.media.server.spi;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

/**
 * The VirtualEndpoint like AnnEndpoint, PREndpoint, LoopEndpoint etc are
 * registered with JNDI name as media/trunk/Announcement,
 * media/trunk/PacketRelay, media/test/trunk/Loopback respectively. In case if
 * the application wants to use new endpoint it can call lookup(endpointName)
 * with wild-card '$'. For example media/trunk/Announcement/$. This will return
 * a new VirtualEndpoint with specific name like media/trunk/Announcement/enp-2,
 * application can store this endpoint name for latter use and call
 * lookup("media/trunk/Announcement/enp-2") to get particular instance of
 * Endpoint
 * 
 * @author Oleg Kulikov
 */
public class EndpointQuery {

	protected static Logger logger = Logger.getLogger(EndpointQuery.class.getCanonicalName());
	private static final ConcurrentHashMap<String, ConcurrentHashMap<String, Endpoint>> ENDPOINT_MAPS = new ConcurrentHashMap<String, ConcurrentHashMap<String, Endpoint>>();
	private static final EndpointQuery ENDPOINTQUERY = new EndpointQuery();

	private EndpointQuery() {

	}

	public static EndpointQuery getInstance() {
		return ENDPOINTQUERY;
	}

	public synchronized void addEndpoint(String stringEndpointName, Endpoint endpoint) {
		EndpointName endpointName = new EndpointName(stringEndpointName);
		ConcurrentHashMap<String, Endpoint> endpointMap = ENDPOINT_MAPS.get(endpointName.getContextName());
		if (endpointMap == null) {
			endpointMap = new ConcurrentHashMap<String, Endpoint>();
			ENDPOINT_MAPS.put(endpointName.getContextName(), endpointMap);
		}
		endpointMap.put(stringEndpointName, endpoint);
	}

	/**
	 * This matches xxxxxxxxxxxx/end-y<br>
	 * where x is any character, y is any number of digits
	 */
	public synchronized Endpoint findAny(String name) throws ResourceUnavailableException {
		EndpointName endpointName = new EndpointName(name);
		ConcurrentHashMap<String, Endpoint> endpointMap = ENDPOINT_MAPS.get(endpointName.getContextName());
		if (endpointMap != null) {

			for (Endpoint b : endpointMap.values()) {
				if (!b.isInUse()) {
					b.setInUse(true);
					return b;
				}
			}
		}

		throw new ResourceUnavailableException("Endpoint unknown or in use[" + name + "]");
	}

	public synchronized Endpoint find(String name) throws ResourceUnavailableException {
		EndpointName endpointName = new EndpointName(name);
		ConcurrentHashMap<String, Endpoint> endpointMap = ENDPOINT_MAPS.get(endpointName.getContextName());
		if (endpointMap != null) {
			Endpoint b = endpointMap.get(name);
			if (b != null)
				return b;
		}
		throw new ResourceUnavailableException("Endpoint unknown or in use[" + name + "]");
	}

	public Endpoint lookup(String name) throws ResourceUnavailableException {
		if (name.endsWith("$")) {
			return findAny(name);
		} else {
			return find(name);
		}
	}

	public synchronized void remove(String name) {
		EndpointName endpointName = new EndpointName(name);
		ConcurrentHashMap<String, Endpoint> endpointMap = ENDPOINT_MAPS.get(endpointName.getContextName());
		if (endpointMap != null) {
			Endpoint endpoint = endpointMap.remove(name);
			if (logger.isDebugEnabled()) {
				if (endpoint == null) {
					logger.debug("remove failed. No Endpoint found for endpoint name " + name);
				} else {
					logger.debug("removed successfully enbdpoint = " + endpoint.getLocalName());
				}
			}
			if (endpointMap.size() == 0) {
				ENDPOINT_MAPS.remove(endpointName.getContextName());
			}
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("remove failed. No EndpointMap found for conetxt " + endpointName.getContextName());
			}
		}

	}
}

/**
 * 
 * EndpointName breaks the JNDI name of Endpoint into context and actual name.
 * For example for jndiname "media/trunk/Conference/enp-1" the contextName is
 * "media/trunk/Conference" and name is "enp-1"
 * 
 * @author Oleg Kulikov
 * 
 */
class EndpointName {

	private String contextName;
	private String name;

	public EndpointName(String fqn) {
		parse(fqn);
	}

	public String getContextName() {
		return contextName;
	}

	public String getName() {
		return name;
	}

	private void parse(String fqn) {
		int pos = 0;
		int idx = 0;
		while (idx != -1) {
			idx = fqn.indexOf("/", pos);
			if (idx != -1) {
				pos = idx + 1;
			}
		}

		contextName = fqn.substring(0, pos - 1).trim();
		name = fqn.substring(pos).trim();
	}
}