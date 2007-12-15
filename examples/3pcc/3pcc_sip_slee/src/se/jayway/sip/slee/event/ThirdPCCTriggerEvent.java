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

package se.jayway.sip.slee.event;

import java.util.Random;
import java.io.Serializable;

import se.jayway.sip.util.DefaultStateCallback;
import se.jayway.sip.util.StateCallback;
/**
 * Event for trigger a thirdp party call control session. This class has a set of members that represent the data 
 * necessary to manage the session setup and interaction with an external controller (e.g. a web application). <br>
 * This is essentially the sip addresses, a guid for the session and a callback interface for state sharing. 
 * 
 * @author niklasuhrberg
 *
 */
public final class ThirdPCCTriggerEvent implements Serializable {

	private String callerURI;
	private String calleeURI;
		
	private final long id;
	private String guid;
	
	private static final long serialVersionUID = -2074176824020080823L;
	
	private StateCallback stateCallback;
	
	public ThirdPCCTriggerEvent() {
		id = generateGuid();
		guid = String.valueOf(generateGuid());
		
	}
	public ThirdPCCTriggerEvent(String callerURI, String calleeURI) {
		this();
		this.callerURI = callerURI;
		this.calleeURI = calleeURI;		
	}
	
	/**
	 * Generates the a global unique identifier (guid)
	 * 
	 * @return a guid
	 */
	private long generateGuid() {
		return new Random().nextLong() ^ System.currentTimeMillis();
	}
	public boolean equals(Object o) {
		if (o == this) return true;
		if (o == null) return false;
		return (o instanceof ThirdPCCTriggerEvent) && ((ThirdPCCTriggerEvent)o).id == id;
	}
	
	public int hashCode() {
		return (int) id;
	}
	
	public String toString() {
		return "tpccTriggerEvent[" + hashCode() + "]";
	}

	public String getCalleeURI() {
		return calleeURI;
	}

	public void setCalleeURI(String calleeURI) {
		this.calleeURI = calleeURI;
	}

	public String getCallerURI() {
		return callerURI;
	}

	public void setCallerURI(String callerURI) {
		this.callerURI = callerURI;
	}
	
	public String getGUID() {
		return guid;
	}
	public StateCallback getStateCallback() {
		return stateCallback;
	}
	public void setStateCallback(StateCallback stateCallback) {
		this.stateCallback = stateCallback;
	}
}
