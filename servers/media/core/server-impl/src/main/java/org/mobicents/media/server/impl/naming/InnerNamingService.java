/*
 * Mobicents, Communications Middleware
 * 
 * Copyright (c) 2008, Red Hat Middleware LLC or third-party
 * contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Middleware LLC.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 *
 * Boston, MA  02110-1301  USA
 */
package org.mobicents.media.server.impl.naming;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.jboss.beans.metadata.api.annotations.Install;
import org.jboss.beans.metadata.api.annotations.Uninstall;
import org.mobicents.media.server.EndpointImpl;
import org.mobicents.media.server.spi.Endpoint;
import org.mobicents.media.server.spi.NamingService;
import org.mobicents.media.server.spi.ResourceUnavailableException;

/**
 * 
 * @author kulikov
 */
public class InnerNamingService implements NamingService {
	private HashMap<String, Endpoint> endpoints = new HashMap();
	private NameParser nameParser;

	private final static Logger logger = Logger.getLogger(InnerNamingService.class);

	public void start() {
		nameParser = new NameParser();
		logger.info("Started");
	}

	public void stop() {
		logger.info("Stopped");
		Collection<Endpoint> list = endpoints.values();
		for (Endpoint endpoint : list) {
			endpoint.stop();
			logger.info("Stopped endpoint: local name = " + endpoint.getLocalName());
		}
	}

	@Install
	public void addEndpoint(Endpoint endpoint) {
		EndpointImpl factory = (EndpointImpl) endpoint;
		Iterator<NameToken> tokens = nameParser.parse(endpoint.getLocalName()).iterator();
		ArrayList<String> prefixes = new ArrayList();
		prefixes.add("");

		Collection<String> names = getNames(prefixes, tokens.next(), tokens);
		for (String name : names) {
			EndpointImpl enp = new EndpointImpl();
			enp.setLocalName(name);
			enp.setTimer(factory.getTimer());
			enp.setRtpFactory(factory.getRtpFactory());
			enp.setRxChannelFactory(factory.getRxChannelFactory());
			enp.setTxChannelFactory(factory.getTxChannelFactory());
			enp.setSourceFactory(factory.getSourceFactory());
			try {
				enp.start();
				endpoints.put(name, enp);
				logger.info("Started endpoint: " + name);
			} catch (Exception e) {
				logger.error("Could not start endpoint: local name = " + name, e);
			}
		}
	}

	@Uninstall
	public void removeEndpoint(Endpoint endpoint) {
		endpoints.remove(endpoint.getLocalName());
		logger.info("Unregistered endpoint: local name " + endpoint.getLocalName());
	}

	public Endpoint lookup(String endpointName, boolean allowInUse) throws ResourceUnavailableException {
		if (endpointName.endsWith("$")) {
			return null; // findAny(endpointName);
		} else {
			return find(endpointName, allowInUse);
		}
		// return null;
	}

	public synchronized Endpoint find(String name, boolean allowInUse) throws ResourceUnavailableException {
		Endpoint endpt = endpoints.get(name);
		if (endpt == null) {
			throw new ResourceUnavailableException("No Endpoint found for " + name);
		}
		if (endpt.isInUse() && !allowInUse) {
			throw new ResourceUnavailableException("Endpoint " + name + " is in use");
		} else {
			endpt.setInUse(true);
			return endpt;
		}
	}

	protected Collection<String> getNames(Collection<String> prefixes, NameToken token, Iterator<NameToken> tokens) {
		ArrayList<String> list = new ArrayList();
		if (!tokens.hasNext()) {
			while (token.hasMore()) {
				String s = token.next();
				for (String prefix : prefixes) {
					list.add(prefix + "/" + s);
				}
			}
			return list;
		} else {
			Collection<String> newPrefixes = new ArrayList();
			while (token.hasMore()) {
				String s = token.next();
				for (String prefix : prefixes) {
					newPrefixes.add(prefix + "/" + s);
				}
			}
			return getNames(newPrefixes, tokens.next(), tokens);
		}
	}
}
