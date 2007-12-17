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

import javax.naming.NamingException;
import javax.resource.ResourceException;

public interface CallControlMBean {
	/**
	 * Launches the callflow.
	 * @param caller A sip URI on the form 'sip:username@domain:port' or 'sip:username@ipaddress:port'
	 * @param callee A sip URI on the form 'sip:username@domain:port' or 'sip:username@ipaddress:port'
	 * @return The identifier for the launched callflow that can be used to terminate the calls once established.
	 * @throws ResourceException
	 * @throws NamingException
	 */
	public String makeCall(String caller, String callee) throws ResourceException, NamingException;
	/**
	 * Terminates the calls after a completet successful setup. 
	 * <br>
	 * Do not use this method to cancel a call i a state where the setup is still ongoing, e.g.
	 * when the callee has responded 200 and the caller has not responded 200.
	 * 
	 * @param callIdentifier
	 */
	public void terminateCall(String callIdentifier) throws ResourceException, NamingException;
}
