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
 * This class extends the ClusteredSipStack to provide an implementation backed by JBoss Cache 1.4.X
 * 
 * @author jean.deruelle@gmail.com
 *
 */
public class SipStackImpl extends ClusteredSipStack {
	
	public static final String SIP_DEFAULT_CACHE_CLASS_NAME = "org.mobicents.ha.javax.sip.cache.JBossTreeSipCache";
	
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
