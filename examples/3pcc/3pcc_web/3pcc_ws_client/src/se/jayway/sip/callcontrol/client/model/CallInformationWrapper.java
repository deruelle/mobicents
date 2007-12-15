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

package se.jayway.sip.callcontrol.client.model;

import java.util.Date;
/**
 * Simple interface that represents information about a session.
 * @author Niklas
 * 
 * @see ETSI ES 202 391-2 V1.1.1, chapter 7.3
 */
public interface CallInformationWrapper {
	  
	/**
	 * Returns the current status of the call.
	 * 
	 * @return The current status of the call 
	 */
	public String getCallStatus();
	
	/**
	 * Returns the time of the beginning of the call.
	 * 
	 * @return The time of the beginning of the call
	 */
	public Date getStartTime();
	
	/**
	 * Returns the duration of the call expressed in seconds.
	 * 
	 * @return The duration of the call expressed in seconds
	 */
	public int getDuration();
	
	/**
	 * Returns the cause of the termination of the call.
	 * 
	 * @return The cause of the termination of the call
	 */
	public String getTerminationCause();
}
