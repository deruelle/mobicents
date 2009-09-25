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
import gov.nist.javax.sip.stack.SIPClientTransaction;
import gov.nist.javax.sip.stack.SIPDialog;
import gov.nist.javax.sip.stack.SIPServerTransaction;

import java.util.Properties;

import org.jboss.cache.CacheException;
import org.jboss.cache.Fqn;
import org.jboss.cache.Node;
import org.mobicents.cache.MobicentsCache;
import org.mobicents.ha.javax.sip.ClusteredSipStack;
import org.mobicents.ha.javax.sip.SipStackImpl;

/**
 * Implementation of the SipCache interface, backed by a Mobicents Cache (JBoss Cache 3.X Cache).
 * The configuration of Mobicents Cache can be set throught the following Mobicents SIP Stack property :
 * <b>org.mobicents.ha.javax.sip.JBOSS_CACHE_CONFIG_PATH</b>
 * 
 * @author jean.deruelle@gmail.com
 *
 */
public class MobicentsSipCache implements SipCache {
	public static final String JBOSS_CACHE_CONFIG_PATH = "org.mobicents.ha.javax.sip.JBOSS_CACHE_CONFIG_PATH";
	public static final String DEFAULT_FILE_CONFIG_PATH = "META-INF/cache-configuration.xml"; 
	
	ClusteredSipStack clusteredSipStack = null;
	Properties configProperties = null;	
	
	protected MobicentsCache cache;
	protected JBossJainSipCacheListener cacheListener;
	
	protected Node<String, SIPDialog> dialogRootNode = null;
	protected Node<String, SIPClientTransaction> clientTxRootNode = null;
	protected Node<String, SIPServerTransaction> serverTxRootNode = null;
	
	/**
	 * 
	 */
	public MobicentsSipCache() {}

	/* (non-Javadoc)
	 * @see org.mobicents.ha.javax.sip.cache.SipCache#getDialog(java.lang.String)
	 */
	public SIPDialog getDialog(String dialogId) throws SipCacheException {		
		try {
			SIPDialogCacheData cacheData = new SIPDialogCacheData(Fqn.fromString(SipStackImpl.DIALOG_ROOT + dialogId), cache);
			return cacheData.getSIPDialog(dialogId);			
		} catch (CacheException e) {
			throw new SipCacheException("A problem occured while retrieving the following dialog " + dialogId + " from Mobicents Cache", e);
		}
	}

	/* (non-Javadoc)
	 * @see org.mobicents.ha.javax.sip.cache.SipCache#putDialog(gov.nist.javax.sip.stack.SIPDialog)
	 */
	public void putDialog(SIPDialog dialog) throws SipCacheException {
		SIPDialogCacheData cacheData = new SIPDialogCacheData(Fqn.fromString(SipStackImpl.DIALOG_ROOT + dialog.getDialogId()), cache);
		cacheData.create();
		cacheData.putSIPDialog(dialog);
	}

	/* (non-Javadoc)
	 * @see org.mobicents.ha.javax.sip.cache.SipCache#removeDialog(java.lang.String)
	 */
	public void removeDialog(String dialogId) throws SipCacheException {
		SIPDialogCacheData cacheData = new SIPDialogCacheData(Fqn.fromString(SipStackImpl.DIALOG_ROOT + dialogId), cache);
		cacheData.remove();
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

	public void init() throws SipCacheException {
		String pojoConfigurationPath = configProperties.getProperty(JBOSS_CACHE_CONFIG_PATH, DEFAULT_FILE_CONFIG_PATH);
		if (clusteredSipStack.getStackLogger().isLoggingEnabled(StackLogger.TRACE_INFO)) {
			clusteredSipStack.getStackLogger().logInfo(
					"Mobicents JAIN SIP JBoss Cache Configuration path is : " + pojoConfigurationPath);
		}
		try {			
			cache = new MobicentsCache(pojoConfigurationPath);						
			cacheListener = new JBossJainSipCacheListener(clusteredSipStack);
			cache.getJBossCache().addCacheListener(cacheListener);			
		} catch (Exception e) {
			throw new SipCacheException("Couldn't init Mobicents Cache", e);
		}
		SIPDialogCacheData dialogRoot = new SIPDialogCacheData(Fqn.fromString(SipStackImpl.DIALOG_ROOT), cache);
		dialogRoot.create();
	}

	public void start() throws SipCacheException {
		// Cache has already been starte, nothing to do here
	}

	public void stop() throws SipCacheException {
		cache.stop();		
	}

}
