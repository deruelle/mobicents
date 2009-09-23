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

import org.jboss.cache.CacheException;
import org.jboss.cache.PropertyConfigurator;
import org.jboss.cache.TreeCache;
import org.mobicents.ha.javax.sip.ClusteredSipStack;
import org.mobicents.ha.javax.sip.SipStackImpl;

/**
 * Implementation of the SipCache interface, backed by a JBoss Cache 1.4.1 Tree Cache.
 * The configuration of the TreeCache can be set throught the following Mobicents SIP Stack property :
 * <b>org.mobicents.ha.javax.sip.TREE_CACHE_CONFIG_PATH</b>
 * 
 * @author jean.deruelle@gmail.com
 *
 */
public class JBossTreeSipCache implements SipCache {
	public static final String TREE_CACHE_CONFIG_PATH = "org.mobicents.ha.javax.sip.TREE_CACHE_CONFIG_PATH";
	public static final String DEFAULT_FILE_CONFIG_PATH = "META-INF/replSync-service.xml"; 
	
	ClusteredSipStack clusteredSipStack = null;
	Properties configProperties = null;	
	
	protected TreeCache treeCache;
	protected JBossTreeSipCacheListener treeCacheListener;
	
	/**
	 * 
	 */
	public JBossTreeSipCache() {}

	/* (non-Javadoc)
	 * @see org.mobicents.ha.javax.sip.cache.SipCache#getDialog(java.lang.String)
	 */
	public SIPDialog getDialog(String dialogId) throws SipCacheException {		
		try {
			return (SIPDialog) treeCache.get(SipStackImpl.DIALOG_ROOT, dialogId);
		} catch (CacheException e) {
			throw new SipCacheException("A problem occured while retrieving the following dialog " + dialogId + " from the TreeCache", e);
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
			treeCache.put(SipStackImpl.DIALOG_ROOT, dialog.getDialogId(), dialog);
			if(tx != null) {
				tx.commit();
			}
		} catch (Exception e) {
			if(tx != null) {
				try { tx.rollback(); } catch(Throwable t) {}
			}
			throw new SipCacheException("A problem occured while putting the following dialog " + dialog.getDialogId() + "  into the TreeCache", e);
		} 
	}

	/* (non-Javadoc)
	 * @see org.mobicents.ha.javax.sip.cache.SipCache#removeDialog(java.lang.String)
	 */
	public void removeDialog(String dialogId) throws SipCacheException {
		UserTransaction tx = null;
		try {
			Properties prop = new Properties();
			prop.put(Context.INITIAL_CONTEXT_FACTORY, "org.jboss.cache.transaction.DummyContextFactory");
			tx = (UserTransaction) new InitialContext(prop).lookup("UserTransaction");
			if(tx != null) {
				tx.begin();
			}
			treeCache.remove(SipStackImpl.DIALOG_ROOT, dialogId);
			if(tx != null) {
				tx.commit();
			}
		} catch (Exception e) {
			if(tx != null) {
				try { tx.rollback(); } catch(Throwable t) {}
			}
			throw new SipCacheException("A problem occured while removing the following dialog " + dialogId + " from the TreeCache", e);
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
		String pojoConfigurationPath = configProperties.getProperty(TREE_CACHE_CONFIG_PATH, DEFAULT_FILE_CONFIG_PATH);
		if (clusteredSipStack.getStackLogger().isLoggingEnabled(StackLogger.TRACE_INFO)) {
			clusteredSipStack.getStackLogger().logInfo(
					"Mobicents JAIN SIP Tree Cache Configuration path is : " + pojoConfigurationPath);
		}
		try {
			treeCache = new TreeCache();
			treeCache.createService();
			treeCacheListener = new JBossTreeSipCacheListener(clusteredSipStack);
			treeCache.addTreeCacheListener(treeCacheListener);
			PropertyConfigurator config = new PropertyConfigurator(); // configure tree cache.
			config.configure(treeCache, pojoConfigurationPath);
		} catch (Exception e) {
			throw new SipCacheException("Couldn't init the TreeCache", e);
		}
	}

	public void start() throws SipCacheException {
		try {
			treeCache.start();
		} catch (Exception e) {
			throw new SipCacheException("Couldn't start the TreeCache", e);
		}
		if (clusteredSipStack.getStackLogger().isLoggingEnabled(StackLogger.TRACE_INFO)) {
			clusteredSipStack.getStackLogger().logInfo(
					"Mobicents JAIN SIP Tree Cache started, state: " + treeCache.getStateString() + 
					", Mode: " + treeCache.getCacheMode());
		}
	}

	public void stop() throws SipCacheException {
		treeCache.stopService();
		if (clusteredSipStack.getStackLogger().isLoggingEnabled(StackLogger.TRACE_INFO)) {
			clusteredSipStack.getStackLogger().logInfo(
					"Mobicents JAIN SIP Tree Cache stopped, state: " + treeCache.getStateString() + 
					", Mode: " + treeCache.getCacheMode());
		}
		treeCache.destroyService();
	}

}
