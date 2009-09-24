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

import gov.nist.core.StackLogger;
import gov.nist.javax.sip.message.SIPRequest;
import gov.nist.javax.sip.message.SIPResponse;
import gov.nist.javax.sip.stack.HASipDialog;
import gov.nist.javax.sip.stack.SIPClientTransaction;
import gov.nist.javax.sip.stack.SIPDialog;
import gov.nist.javax.sip.stack.SIPTransaction;

import java.util.Properties;

import javax.sip.DialogState;
import javax.sip.PeerUnavailableException;
import javax.sip.ProviderDoesNotExistException;
import javax.sip.SipException;

import org.mobicents.ha.javax.sip.cache.SipCache;
import org.mobicents.ha.javax.sip.cache.SipCacheException;
import org.mobicents.ha.javax.sip.cache.SipCacheFactory;

/**
 * This class extends the regular NIST SIP Stack Implementation to cache Dialogs in a replicated cache 
 * and make use of Fault Tolerant Timers so that the NIST SIP Stack can be distributed in a cluster 
 * and calls can be failed over 
 * 
 * This class will instantiate an instance of a class implementing the org.mobicents.ha.javax.sip.cache.SipCache interface to be able to set/get dialogs and txs into/from it.
 * The cache class name is retrieved through the Properties given to the Sip Stack upon creation under the following property name : org.mobicents.ha.javax.sip.CACHE_CLASS_NAME
 * 
 * It will override all the calls to get/remove/put Dialogs and Txs so that we can fetch/remove/put them from/into the Cache 
 * and populate the local datastructure of the NIST SIP Stack
 * 
 * @author jean.deruelle@gmail.com
 *
 */
public abstract class ClusteredSipStackImpl extends gov.nist.javax.sip.SipStackImpl implements ClusteredSipStack {	
	private SipCache sipCache = null;
	
	public ClusteredSipStackImpl(Properties configurationProperties) throws PeerUnavailableException {
		super(configurationProperties);
		// get/create the jboss cache instance to store all sip stack related data into it
		sipCache = SipCacheFactory.createSipCache(this, configurationProperties);
		try {
			sipCache.init();
		} catch (Exception e) {
			throw new PeerUnavailableException("Unable to initialize the SipCache", e);
		}		
	}
	
	@Override
	public void start() throws ProviderDoesNotExistException, SipException {
		try {
			sipCache.start();
		} catch (Exception e) {
			throw new SipException("Unable to start the SipCache", e);
		}
		super.start();		
	}
	
	@Override
	public void stop() {		
		super.stop();
		try {
			sipCache.stop();
		} catch (Exception e) {
			getStackLogger().logError("Unable to stop the SipCache", e);
		}
	}
	
	@Override
	public SIPDialog createDialog(SIPTransaction transaction) {
		
		SIPDialog retval = null;
        if (transaction instanceof SIPClientTransaction) {
            String dialogId = ((SIPRequest) transaction.getRequest()).getDialogId(false);
            if (this.earlyDialogTable.get(dialogId) != null) {
                SIPDialog dialog = this.earlyDialogTable.get(dialogId);
                if (dialog.getState() == null || dialog.getState() == DialogState.EARLY) {
                    retval = dialog;
                } else {
                    retval = new HASipDialog(transaction);
                    this.earlyDialogTable.put(dialogId, retval);
                }
            } else {
                retval = new HASipDialog(transaction);
                this.earlyDialogTable.put(dialogId, retval);
            }
        } else {
            retval = new HASipDialog(transaction);
        }
        return retval;
	}
	
	@Override
	public SIPDialog createDialog(SIPClientTransaction transaction, SIPResponse sipResponse) {
        String dialogId = ((SIPRequest) transaction.getRequest()).getDialogId(false);
        SIPDialog retval = null;
        if (this.earlyDialogTable.get(dialogId) != null) {
            retval = this.earlyDialogTable.get(dialogId);
            if (sipResponse.isFinalResponse()) {
                this.earlyDialogTable.remove(dialogId);
            }

        } else {
            retval = new HASipDialog(transaction, sipResponse);
        }
        return retval;

    }

	
	@Override
	public SIPDialog getDialog(String dialogId) {
		if(getStackLogger().isLoggingEnabled(StackLogger.TRACE_DEBUG)) {
			getStackLogger().logDebug("checking if the dialog " + dialogId + " is present in the local cache");
		}		
		SIPDialog sipDialog = super.getDialog(dialogId);
		if(sipDialog == null) {
			if(getStackLogger().isLoggingEnabled(StackLogger.TRACE_DEBUG)) {
				getStackLogger().logDebug("local dialog " + dialogId + " is null, checking in the distributed cache");
			}
			sipDialog = getDialogFromDistributedCache(dialogId);
			if(sipDialog != null) {
				if(getStackLogger().isLoggingEnabled(StackLogger.TRACE_DEBUG)) {
					getStackLogger().logDebug("dialog " + dialogId + " found in the distributed cache, storing it locally");
				}
				super.putDialog(sipDialog);
			} else {
				if(getStackLogger().isLoggingEnabled(StackLogger.TRACE_DEBUG)) {
					getStackLogger().logDebug("dialog " + dialogId + " not found in the distributed cache");
				}
			}
		}
		return sipDialog;
	}		

	@Override
	public void putDialog(SIPDialog dialog) {
		putDialogIntoDistributedCache(dialog);
		super.putDialog(dialog);		
	}
		
	@Override
	public void removeDialog(SIPDialog dialog) {
		removeDialogFromDistributedCache(dialog.getDialogId());
		super.removeDialog(dialog);
	}
	
	/**
	 * Retrieve the dialog from the distributed cache
	 * @param dialogId the id of the dialog to fetch
	 * @return the SIPDialog from the distributed cache, null if nothing has been found in the cache
	 */
	protected  SIPDialog getDialogFromDistributedCache(String dialogId) {
		if(getStackLogger().isLoggingEnabled(StackLogger.TRACE_DEBUG)) {
			getStackLogger().logDebug("sipStack " + this + " checking if the dialog " + dialogId + " is present in the distributed cache");
		}	
		// fetch the corresponding dialog from the cache instance
		SIPDialog sipDialog = null;
		try {
			sipDialog = sipCache.getDialog(dialogId);
		} catch (SipCacheException e) {
			getStackLogger().logError("sipStack " + this + " problem getting dialog " + dialogId + " from the distributed cache", e);
		}
		if(sipDialog != null) {			
			((HASipDialog)sipDialog).initAfterLoad(this);
		}
		return sipDialog;
	}
	/**
	 * Store the dialog into the distributed cache
	 * @param dialog the dialog to store
	 */
	protected  void putDialogIntoDistributedCache(SIPDialog dialog) {
		String dialogId = dialog.getDialogId();	
		if(getStackLogger().isLoggingEnabled(StackLogger.TRACE_DEBUG)) {
			getStackLogger().logDebug("sipStack " + this + " storing the dialog " + dialogId + " in the distributed cache");
		}
		// put the corresponding dialog into the cache instance
		try {
			sipCache.putDialog(dialog);
		} catch (SipCacheException e) {
			getStackLogger().logError("sipStack " + this + " problem storing the dialog " + dialogId + " into the distributed cache", e);
		}
	}
	/**
	 * Remove the dialog from the distributed cache
	 * @param dialogId the id of the dialog to remove
	 */
	protected  void removeDialogFromDistributedCache(String dialogId) {
		if(getStackLogger().isLoggingEnabled(StackLogger.TRACE_DEBUG)) {
			getStackLogger().logDebug("sipStack " + this + " removing the dialog " + dialogId + " from the distributed cache");
		}
		// remove the corresponding dialog from the cache instance
		// put the corresponding dialog into the cache instance
		try {
			sipCache.removeDialog(dialogId);
		} catch (SipCacheException e) {
			getStackLogger().logError("sipStack " + this + " problem removing dialog " + dialogId + " from the distributed cache", e);
		}
	}

	/**
	 * @param sipCache the sipCache to set
	 */
	public void setSipCache(SipCache sipCache) {
		this.sipCache = sipCache;
	}

	/**
	 * @return the sipCache
	 */
	public SipCache getSipCache() {
		return sipCache;
	}
}
