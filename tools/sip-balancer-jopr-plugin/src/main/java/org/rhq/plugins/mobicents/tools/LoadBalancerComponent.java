/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.rhq.plugins.mobicents.tools;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rhq.core.domain.configuration.Configuration;
import org.rhq.core.domain.configuration.PropertyList;
import org.rhq.core.domain.configuration.PropertyMap;
import org.rhq.core.domain.configuration.PropertySimple;
import org.rhq.plugins.jmx.MBeanResourceComponent;

/**
 * @author jean.deruelle@gmail.com
 */
public class LoadBalancerComponent extends MBeanResourceComponent {
	private static Log log = LogFactory.getLog(LoadBalancerComponent.class);
	
	@Override
	public Configuration loadResourceConfiguration() {
		Configuration configuration = super.loadResourceConfiguration();
				
		PropertyList joprNodes = new PropertyList("nodes");
		
		log.debug("creating nodes list");
		List<Object> nodes = null;
		try {
			nodes = (List<Object>) getEmsBean().getAttribute("nodes").refresh();
		} catch (Throwable e) {
			log.error("An unexpected exception occured while trying to access the Sip Balancer nodes, probable the SIPNode class is not on the classpath...", e);
		}
				
		if(nodes != null) {
			log.debug("number of nodes found : " + nodes.size());
		
			ClassLoader cl = Thread.currentThread().getContextClassLoader();		
			try {
				Thread.currentThread().setContextClassLoader(getEmsBean().getClass().getClassLoader());
				for (Object object : nodes) {
					try {
						String hostName = (String)object.getClass().getMethod("getHostName").invoke(object, new Object[0]);
						String ip = (String)object.getClass().getMethod("getIp").invoke(object, new Object[0]);
						Integer port = (Integer) object.getClass().getMethod("getPort").invoke(object, new Object[0]);
						String transports = (String)object.getClass().getMethod("getTransportsAsString").invoke(object, new Object[0]);
						
						PropertyMap joprNode = new PropertyMap(
							"node", 
							new PropertySimple("hostName", hostName),
	                        new PropertySimple("ip", ip), 
	                        new PropertySimple("port",port), 
	                        new PropertySimple("transports", transports)
	                    );
						
						joprNodes.add(joprNode);
						
						log.debug("node added to the map : " + hostName + "/" + ip + ":" + port + "[" + transports + "]");
						
					} catch (Throwable e) {
						log.error("An unexpected exception occured while trying to access the Sip Balancer node property", e);
					}
				}
			} finally {
	            Thread.currentThread().setContextClassLoader(cl);
	        }
			
			configuration.put(joprNodes);
		}
		
		return configuration;
	}
}