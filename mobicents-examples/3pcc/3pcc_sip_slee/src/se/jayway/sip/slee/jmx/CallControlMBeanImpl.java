 /*
  * Mobicents: The Open Source SLEE Platform      
  *
  * Copyright 2003-2005, CocoonHive, LLC., 
  * and individual contributors as indicated
  * by the @authors tag. See the copyright.txt 
  * in the distribution for a full listing of   
  * individual contributors.
  *
  * This is free software; you can redistribute it
  * and/or modify it under the terms of the 
  * GNU Lesser General Public License as
  * published by the Free Software Foundation; 
  * either version 2.1 of
  * the License, or (at your option) any later version.
  *
  * This software is distributed in the hope that 
  * it will be useful, but WITHOUT ANY WARRANTY; 
  * without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR 
  * PURPOSE. See the GNU Lesser General Public License
  * for more details.
  *
  * You should have received a copy of the 
  * GNU Lesser General Public
  * License along with this software; 
  * if not, write to the Free
  * Software Foundation, Inc., 51 Franklin St, 
  * Fifth Floor, Boston, MA
  * 02110-1301 USA, or see the FSF site:
  * http://www.fsf.org.
  */

package se.jayway.sip.slee.jmx;
 
import java.util.HashMap;

import javax.management.NotCompliantMBeanException;
import javax.management.StandardMBean;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.resource.ResourceException;
import javax.slee.EventTypeID;
import javax.slee.SLEEException;
import javax.slee.connection.SleeConnection;
import javax.slee.connection.SleeConnectionFactory;

import org.apache.log4j.Logger;

import se.jayway.sip.slee.event.TerminationEvent;
import se.jayway.sip.slee.event.ThirdPCCTriggerEvent;

  
 


public class CallControlMBeanImpl extends StandardMBean implements CallControlMBean {
	private Logger log = Logger.getLogger(CallControlMBeanImpl.class);
	private final static String CACHE_JNDI_NAME = "java:jayway"; 
	
	public CallControlMBeanImpl() throws NotCompliantMBeanException, Exception {
		super(CallControlMBean.class);
		
		InitialContext ctx = null;
			try {
				ctx = new InitialContext();
				ctx.lookup(CACHE_JNDI_NAME);
			} catch ( NamingException e ) {
				log.info("Binding cache to " + CACHE_JNDI_NAME);
				// This is expected if there is not yet an object bound
				try {
					ctx.bind(CACHE_JNDI_NAME, new HashMap());
				} catch (Exception ex) {
					log.error("Error in constructor, could not bind cache", ex);
					throw ex;
				}
			}
			
	}
	public String makeCall(String caller, String callee) throws NamingException {
		log.debug("Enter makeCall with caller : " + caller + " and callee : " + callee);
		String returnValue = null; // The globally unique id for the launched callcontrol flow
		SleeConnectionFactory factory = null;
		SleeConnection conn = null;
		try {
			InitialContext ctx = new InitialContext();
			factory = (SleeConnectionFactory)ctx.lookup("java:MobicentsConnectionFactory");
			log.debug("Got SleeConnectionFactory : " + factory);
		} catch ( NamingException e) {
			log.error("Failed to lookup SleeConnectionFactory", e);
			throw e;
		}
		
		// Now go on and establish the connection
		try {
			conn = factory.getConnection();
			log.debug("Got SleeConnection : " + conn);
		} catch (ResourceException e) {
			log.error("Failed to obtain SleeConnection from factory", e);
			throw new SLEEException("Failed to obtain SleeConnection from factory", e);
		}
		
		try {
			// Fire off the event
			
			ThirdPCCTriggerEvent event = new ThirdPCCTriggerEvent(caller, callee);
			returnValue = event.getGUID();
			EventTypeID eventTypeID = conn.getEventTypeID("se.jayway.sip.slee.event.ThirdPCCTriggerEvent","Jayway","0.1");
			log.debug("Got EventTypeID : " + eventTypeID);
			
			log.debug("Firing event");
			conn.fireEvent(event, eventTypeID, null, null);
			log.debug("Fired event successfully");
		} catch (Exception e) {
			
		} finally {
			// 
			try {
			conn.close();
			} catch ( ResourceException e ) {
				log.error("Failed to close SleeConnection");
			}
		}
		
		return returnValue;
	} 
	
	public void terminateCall(String callIdentifier) throws ResourceException, NamingException {
		SleeConnectionFactory factory = null;
		SleeConnection conn = null;
		try {
			InitialContext ctx = new InitialContext();
			factory = (SleeConnectionFactory)ctx.lookup("java:MobicentsConnectionFactory");
			log.debug("Got SleeConnectionFactory : " + factory);
		} catch ( NamingException e) {
			log.error("Failed to lookup SleeConnectionFactory", e);
			throw e;
		}
		
		// Now go on and establish the connection
		try {
			conn = factory.getConnection();
			log.debug("Got SleeConnection : " + conn);
		} catch (ResourceException e) {
			log.error("Failed to obtain SleeConnection from factory", e);
			throw e;
		}
		
		try {
			// Fire off the event
			
			TerminationEvent event = new TerminationEvent(callIdentifier);
			EventTypeID eventTypeID = conn.getEventTypeID("se.jayway.sip.slee.event.TerminationEvent","Jayway","0.1");
			log.debug("Got EventTypeID : " + eventTypeID);
			
			log.debug("Firing event");
			conn.fireEvent(event, eventTypeID, null, null);
			log.debug("Fired event successfully");
		} catch (Exception e) {
			
		} finally {
			// 
			try {
			conn.close();
			} catch ( ResourceException e ) {
				log.error("Failed to close SleeConnection");
			}
		}
	}

	
}
