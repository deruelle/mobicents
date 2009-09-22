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
package org.mobicents.ha.javax.sip.cache;

import java.lang.reflect.Constructor;
import java.util.Properties;

import javax.sip.PeerUnavailableException;

import org.mobicents.ha.javax.sip.ClusteredSipStack;

/**
 * Factory responsible for creating the SipCache instance based on the class name provided by the configuration property <b>org.mobicents.ha.javax.sip.CACHE_CLASS_NAME</b> of the sip stack
 * 
 * @author jean.deruelle@gmail.com
 *
 */
public class SipCacheFactory {

	public static SipCache createSipCache(ClusteredSipStack clusteredSipStack,
			Properties configurationProperties) throws PeerUnavailableException {
		String cacheClassName = configurationProperties.getProperty(ClusteredSipStack.CACHE_CLASS_NAME_PROPERTY);
		if (cacheClassName == null) {
			throw new IllegalArgumentException("the sip cache class name can't be null, please set the org.mobicents.ha.javax.sip.CACHE_CLASS_NAME property accordingly");
		}
		try {
            // create parameters argument to identify constructor
            Class[] paramTypes = new Class[0];
            // get constructor of SipStack in order to instantiate
            Constructor sipStackConstructor = 
            	Class.forName(cacheClassName).getConstructor(paramTypes);
            // Wrap properties object in order to pass to constructor of
            // SipSatck
            Object[] conArgs = new Object[0];
            // Creates a new instance of SipStack Class with the supplied
            // properties.
            SipCache sipCache = (SipCache) sipStackConstructor.newInstance(conArgs);
            sipCache.setClusteredSipStack(clusteredSipStack);
            sipCache.setConfigurationProperties(configurationProperties);
            
            return sipCache;
        } catch (Exception e) {
            String errmsg = "The SipCache class name: "
                    + cacheClassName
                    + " could not be instantiated. Ensure the org.mobicents.ha.javax.sip.CACHE_CLASS_NAME property has been set correctly and that the class is on the classpath.";
            throw new PeerUnavailableException(errmsg, e);
        }
	}

	

}
