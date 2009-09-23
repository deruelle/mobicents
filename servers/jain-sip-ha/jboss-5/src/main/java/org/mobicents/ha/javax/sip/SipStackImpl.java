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
package org.mobicents.ha.javax.sip;

import java.util.Properties;

import javax.sip.PeerUnavailableException;

/**
 * This class extends the regular NIST SIP Stack Implementation to cache Dialogs in a replicated cache 
 * and make use of Fault Tolerant Timers so that the NIST SIP Stack can be distributed in a cluster 
 * and calls can be failed over 
 * 
 * This class will instantiate an instance of a class implementing the org.mobicents.ha.javax.sip.cache.SipCache interface to be able to set/get dialogs and txs into/from it.
 * The cache class name is retrieved through the Properties given to the Sip Stack upon creation under the following property name : <b>org.mobicents.ha.javax.sip.CACHE_CLASS_NAME</b>
 * 
 * It will override all the calls to get/remove/put Dialogs and Txs so that we can fetch/remove/put them from/into the Cache 
 * and populate the local datastructure of the NIST SIP Stack
 * 
 * @author jean.deruelle@gmail.com
 *
 */
public class SipStackImpl extends ClusteredSipStack {
	
	public static final String SIP_DEFAULT_CACHE_CLASS_NAME = "org.mobicents.ha.javax.sip.cache.JBossSipCache";
	
	public static final String DIALOG_ROOT = "Dialogs/";
	public static final String SERVER_TX_ROOT = "ServerTransactions/";
	public static final String CLIENT_TX_ROOT = "ClientTransactions/";
	
	public SipStackImpl(Properties configurationProperties) throws PeerUnavailableException {		
		super(updateConfigProperties(configurationProperties));		
	}
	
	private static final Properties updateConfigProperties(Properties configurationProperties) {
		configurationProperties.setProperty(ClusteredSipStack.CACHE_CLASS_NAME_PROPERTY, SIP_DEFAULT_CACHE_CLASS_NAME);
		return configurationProperties;
	}
}
