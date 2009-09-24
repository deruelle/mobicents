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
package gov.nist.javax.sip.stack;

import gov.nist.javax.sip.SipProviderImpl;
import gov.nist.javax.sip.message.SIPResponse;

import org.mobicents.ha.javax.sip.ClusteredSipStack;
import org.mobicents.ha.javax.sip.cache.SipCacheException;

/**
 * Extends the standard NIST SIP Stack Dialog so that it gets replicated every time there is a State change
 * 
 * @author jean.deruelle@gmail.com
 *
 */
public class HASipDialog extends SIPDialog {

	private static final long serialVersionUID = 1L;
	
	public HASipDialog(SIPTransaction transaction) {
		super(transaction);
	}
	
	public HASipDialog(SIPClientTransaction transaction, SIPResponse sipResponse) {
		super(transaction, sipResponse);
	}
	
    public HASipDialog(SipProviderImpl sipProvider, SIPResponse sipResponse) {
		super(sipProvider, sipResponse);
	}	

	/**
	 * Updates the local dialog transient attributes that were not serialized during the replication 
	 * @param sipStackImpl the sip Stack Impl that reloaded this dialog from the distributed cache
	 */
	public void initAfterLoad(ClusteredSipStack sipStackImpl) {
		setSipProvider((SipProviderImpl) sipStackImpl.getSipProviders().next());
		setStack((SIPTransactionStack)sipStackImpl);
	}		

	@Override
	public void setState(int state) {
		super.setState(state);
		//replicate the dialog when its state has been updated
		try {
			((ClusteredSipStack)getStack()).getSipCache().putDialog(this);
		} catch (SipCacheException e) {
			getStack().getStackLogger().logError("problem storing dialog " + getDialogId() + " into the distributed cache", e);
		}
	}
}
