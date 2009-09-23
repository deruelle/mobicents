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

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.transaction.UserTransaction;

import org.jboss.cache.Cache;
import org.jboss.cache.CacheException;
import org.jboss.cache.DefaultCacheFactory;
import org.mobicents.ha.javax.sip.ClusteredSipStack;
import org.mobicents.ha.javax.sip.SipStackImpl;

/**
 * Implementation of the SipCache interface, backed by a JBoss Cache 1.4.1 Tree Cache.
 * The configuration of JBoss Cache can be set throught the following Mobicents SIP Stack property :
 * <b>org.mobicents.ha.javax.sip.TREE_CACHE_CONFIG_PATH</b>
 * 
 * @author jean.deruelle@gmail.com
 *
 */
public class JBossSipCache implements SipCache {
	public static final String JBOSS_CACHE_CONFIG_PATH = "org.mobicents.ha.javax.sip.JBOSS_CACHE_CONFIG_PATH";
	public static final String DEFAULT_FILE_CONFIG_PATH = "META-INF/cache-configuration.xml"; 
	
	ClusteredSipStack clusteredSipStack = null;
	Properties configProperties = null;	
	
	protected Cache<String, SIPDialog> cache;
	protected JBossSipCacheListener cacheListener;
	
	/**
	 * 
	 */
	public JBossSipCache() {}

	/* (non-Javadoc)
	 * @see org.mobicents.ha.javax.sip.cache.SipCache#getDialog(java.lang.String)
	 */
	public SIPDialog getDialog(String dialogId) throws SipCacheException {		
		try {
			return (SIPDialog) cache.get(SipStackImpl.DIALOG_ROOT, dialogId);
		} catch (CacheException e) {
			throw new SipCacheException("A problem occured while retrieving the following dialog " + dialogId + " from JBoss Cache", e);
		}
	}

	/* (non-Javadoc)
	 * @see org.mobicents.ha.javax.sip.cache.SipCache#putDialog(gov.nist.javax.sip.stack.SIPDialog)
	 */
	public void putDialog(SIPDialog dialog) throws SipCacheException {
		UserTransaction tx = null;
		try {
			Properties prop = new Properties();
			prop.put(Context.INITIAL_CONTEXT_FACTORY, "org.jboss.cache.transaction.DummyContextFactory");
			tx = (UserTransaction) new InitialContext(prop).lookup("UserTransaction");
			if(tx != null) {
				tx.begin();
			}
			cache.put(SipStackImpl.DIALOG_ROOT, dialog.getDialogId(), dialog);
			if(tx != null) {
				tx.commit();
			}
		} catch (Exception e) {
			if(tx != null) {
				try { tx.rollback(); } catch(Throwable t) {}
			}
			throw new SipCacheException("A problem occured while putting the following dialog " + dialog.getDialogId() + "  into JBoss Cache", e);
		} 
	}

	/* (non-Javadoc)
	 * @see org.mobicents.ha.javax.sip.cache.SipCache#removeDialog(java.lang.String)
	 */
	public void removeDialog(String dialogId) throws SipCacheException {
		try {
			cache.remove(SipStackImpl.DIALOG_ROOT, dialogId);
		} catch (CacheException e) {
			throw new SipCacheException("A problem occured while removing the following dialog " + dialogId + " from JBoss Cache", e);
		}
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
			cache = new DefaultCacheFactory<String, SIPDialog>().createCache(pojoConfigurationPath);
			cache.create();
			cacheListener = new JBossSipCacheListener(clusteredSipStack);
			cache.addCacheListener(cacheListener);			
		} catch (Exception e) {
			throw new SipCacheException("Couldn't init JBoss Cache", e);
		}
	}

	public void start() throws SipCacheException {
		try {
			cache.start();
		} catch (Exception e) {
			throw new SipCacheException("Couldn't start JBoss Cache", e);
		}
		
	}

	public void stop() throws SipCacheException {
		cache.stop();		
		cache.destroy();
	}

}
