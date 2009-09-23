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

import gov.nist.javax.sip.stack.SIPDialog;

import java.util.Properties;

import org.mobicents.ha.javax.sip.ClusteredSipStack;

/**
 * Interface defining the contract between the cache instance and the Sip Stack
 * 
 * @author jean.deruelle@gmail.com
 *
 */
public interface SipCache {

	/**
	 * Set the Clustered Sip Stack that created this sip cache instance 
	 * @param clusteredSipStack the Clustered Sip Stack that created this sip cache instance
	 */
	void setClusteredSipStack(ClusteredSipStack clusteredSipStack);
	/**
	 * Set the configuration Properties that have been passed to the sip stack upon its creation 
	 * @param configurationProperties the configuration Properties that have been passed to the sip stack upon its creation 
	 */
	void setConfigurationProperties(Properties configurationProperties);

	/**
	 * Initializes the cache
	 * @throws Exception if anything goes wrong
	 */
	void init() throws SipCacheException;
	
	/**
	 * Start the cache
	 * @throws Exception if anything goes wrong
	 */
	void start() throws SipCacheException;
	
	/**
	 * Stop the cache
	 * @throws Exception if anything goes wrong
	 */
	void stop() throws SipCacheException;
	
	/**
	 * Retrieve the dialog with the passed dialogId from the cache
	 * @param dialogId id of the dialog to retrieve from the cache 
	 * @return the dialog with the passed dialogId from the cache, null if not found
	 */
	SIPDialog getDialog(String dialogId) throws SipCacheException;
	/**
	 * Store the dialog into the cache
	 * @param dialog the dialog to store
	 */
	void putDialog(SIPDialog dialog) throws SipCacheException;
	/**
	 * Remove the dialog from the cache
	 * @param dialogId the id of the dialog to remove
	 */
	void removeDialog(String dialogId) throws SipCacheException;

}
