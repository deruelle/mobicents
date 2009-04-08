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
package org.mobicents.media.server;

import java.util.HashMap;
import org.apache.log4j.Logger;
import org.jboss.beans.metadata.api.annotations.Install;
import org.jboss.beans.metadata.api.annotations.Uninstall;
import org.mobicents.media.server.spi.Endpoint;

/**
 *
 * @author kulikov
 */
public class InnerNamingService {
    private HashMap<String, Endpoint> endpoints = new HashMap();
    private final static Logger logger = Logger.getLogger(InnerNamingService.class);
    
    public void start() {
        logger.info("Started");
    }
    
    public void stop() {
        logger.info("Stopped");
    }
    
    @Install
    public void addEndpoint(Endpoint endpoint) {
        endpoints.put(endpoint.getLocalName(), endpoint);
        logger.info("Registered endpoint: local name = " + endpoint.getLocalName());
    }
    
    @Uninstall
    public void removeEndpoint(Endpoint endpoint) {
        endpoints.remove(endpoint.getLocalName());
        logger.info("Unregistered endpoint: local name " + endpoint.getLocalName());
    }
}
