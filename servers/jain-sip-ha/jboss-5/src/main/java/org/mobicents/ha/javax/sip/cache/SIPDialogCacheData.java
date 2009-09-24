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

import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.transaction.UserTransaction;

import gov.nist.javax.sip.stack.SIPDialog;

import org.jboss.cache.Fqn;
import org.jboss.cache.Node;
import org.mobicents.cache.CacheData;
import org.mobicents.cache.MobicentsCache;

/**
 * @author jean.deruelle@gmail.com
 *
 */
public class SIPDialogCacheData extends CacheData {

	public SIPDialogCacheData(Fqn nodeFqn, MobicentsCache mobicentsCache) {
		super(nodeFqn, mobicentsCache);
	}

	public SIPDialog getSIPDialog(String dialogId) {
		if(exists()) {		
			return (SIPDialog) getNode().get(dialogId);
		} else {
			return null;
		}
	}
	
	public void putSIPDialog(SIPDialog dialog) throws SipCacheException {
		UserTransaction tx = null;
		try {
			Properties prop = new Properties();
			prop.put(Context.INITIAL_CONTEXT_FACTORY, "org.jboss.cache.transaction.DummyContextFactory");
			tx = (UserTransaction) new InitialContext(prop).lookup("UserTransaction");
			if(tx != null) {
				tx.begin();
			}			
			getNode().put(dialog.getDialogId(), dialog);
			if(tx != null) {
				tx.commit();
			}
		} catch (Exception e) {
			if(tx != null) {
				try { tx.rollback(); } catch(Throwable t) {}
			}
			throw new SipCacheException("A problem occured while putting the following dialog " + dialog.getDialogId() + "  into Mobicents Cache", e);
		} 
	}
}
