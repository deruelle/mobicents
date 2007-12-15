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

package se.jayway.sip.slee.sbb;

public interface ThirdPCCSbbUsage {
	/**
	 * Metric for the number of sessions where both callee and caller have responed 200 OK.
	 * @param value
	 */
	public abstract void incrementNumberOfEstablishedSessions(long value);
	/**
	 * Metric for the number of sessions where the setup failed because an exception was
	 * thrown at some stage.
	 * @param value
	 */
	public abstract void incrementNumberOfFailedSessions(long value);
	/**
	 * Metric for the number of sessions cancelled either by <br><br>
	 * 1. The callee hanging up before setup completion 
	 * 2. The web user cancelling the setup before completion.
	 * 3. DECLINE received from either party.
	 * @param value
	 */	
	public abstract void incrementNumberOfCancelledSessions(long value);
	/**
	 * Metric for the number of sessions terminated by either party hanging up or the
	 * web user terminating after setup completion.
	 * @param value
	 */
	public abstract void incrementNumberOfTerminatedSessions(long value);
}
