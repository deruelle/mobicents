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

import gov.nist.core.StackLogger;
import gov.nist.javax.sip.stack.SIPDialog;

import java.util.Properties;

import org.jboss.cache.PropertyConfigurator;
import org.jboss.cache.aop.PojoCache;
import org.mobicents.ha.javax.sip.ClusteredSipStack;

/**
 * Implementation of the SipCache interface, backed by JBoss Cache 1.4.1 Pojo Cache.
 * 
 * @author jean.deruelle@gmail.com
 *
 */
public class JBossPojoSipCache implements SipCache {

	ClusteredSipStack clusteredSipStack = null;
	Properties configProperties = null;
	
	protected PojoCache pojoCache;
	protected JBossTreeSipCacheListener treeCacheListener;
	
	/**
	 * 
	 */
	public JBossPojoSipCache() {}

	/* (non-Javadoc)
	 * @see org.mobicents.ha.javax.sip.cache.SipCache#getDialog(java.lang.String)
	 */
	public SIPDialog getDialog(String dialogId) {		
		return null;
	}

	/* (non-Javadoc)
	 * @see org.mobicents.ha.javax.sip.cache.SipCache#putDialog(gov.nist.javax.sip.stack.SIPDialog)
	 */
	public void putDialog(SIPDialog dialog) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.mobicents.ha.javax.sip.cache.SipCache#removeDialog(java.lang.String)
	 */
	public void removeDialog(String dialogId) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.mobicents.ha.javax.sip.cache.SipCache#setClusteredSipStack(org.mobicents.ha.javax.sip.ClusteredSipStack)
	 */
	public void setClusteredSipStack(ClusteredSipStack clusteredSipStack) {
		this.clusteredSipStack  = clusteredSipStack;
	}

	/* (non-Javadoc)
	 * @see org.mobicents.ha.javax.sip.cache.SipCache#setConfigurationProperties(java.util.Properties)
	 */
	public void setConfigurationProperties(Properties configurationProperties) {
		this.configProperties = configurationProperties;
	}

	public void init() throws Exception {
		pojoCache = new PojoCache();
		treeCacheListener = new JBossTreeSipCacheListener(clusteredSipStack);
		pojoCache.addTreeCacheListener(treeCacheListener);
		PropertyConfigurator config = new PropertyConfigurator(); // configure tree cache.
		config.configure(pojoCache, "META-INF/replSync-service.xml");
	}

	public void start() throws Exception {
		pojoCache.start();
		if (clusteredSipStack.getStackLogger().isLoggingEnabled(StackLogger.TRACE_INFO)) {
			clusteredSipStack.getStackLogger().logInfo(
					"Mobicents JAIN SIP Cache started, state: " + pojoCache.getStateString() + 
					", Mode: " + pojoCache.getCacheMode());
		}
	}

	public void stop() throws Exception {
		pojoCache.stop();
		if (clusteredSipStack.getStackLogger().isLoggingEnabled(StackLogger.TRACE_INFO)) {
			clusteredSipStack.getStackLogger().logInfo(
					"Mobicents JAIN SIP Cache stopped, state: " + pojoCache.getStateString() + 
					", Mode: " + pojoCache.getCacheMode());
		}
		pojoCache.destroy();
	}

}
