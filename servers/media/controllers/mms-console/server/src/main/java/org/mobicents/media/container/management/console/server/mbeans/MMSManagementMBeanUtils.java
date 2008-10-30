/*
 * Mobicents: The Open Source VoIP Middleware Platform
 *
 * Copyright 2003-2006, Mobicents, 
 * and individual contributors as indicated
 * by the @authors tag. See the copyright.txt 
 * in the distribution for a full listing of   
 * individual contributors.
 *
 * This is free software; you can redistribute it
 * and/or modify it under the terms of the 
 * GNU General Public License (GPL) as
 * published by the Free Software Foundation; 
 * either version 2 of
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
 * GNU General Public
 * License along with this software; 
 * if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, 
 * Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site:
 * http://www.fsf.org.
 */
package org.mobicents.media.container.management.console.server.mbeans;

import javax.management.MBeanServerConnection;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.naming.InitialContext;


import org.mobicents.media.container.management.console.client.ManagementConsoleException;


/**
 * 
 * <br><br>Super project:  mobicents-media-server-controllers
 * <br>19:31:28 2008-09-11	
 * <br>
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski </a> 
 */
public class MMSManagementMBeanUtils {

	private MBeanServerConnection mbeanServer;
	private ObjectName rtpMGMTRegex=null;
	private RTPManagementMBeanUtils rtpManagementMBeanUtils = null;
	private EndpointManagementMBeanUtils endpointManagementUtils=null;
	


	public MMSManagementMBeanUtils() throws ManagementConsoleException {
		try {
			
			
			InitialContext ctx;
			ctx = new InitialContext();

			mbeanServer = (MBeanServerConnection) ctx.lookup("jmx/rmi/RMIAdaptor");

			this.rtpMGMTRegex=new ObjectName("mobicents.media:service=RTPManager,QID=1");
			this.rtpManagementMBeanUtils=new RTPManagementMBeanUtils(this.rtpMGMTRegex,this.mbeanServer);
			ObjectName annObjectName=new ObjectName("media.mobicents:endpoint=announcement");
			ObjectName ivrObjectName=new ObjectName("media.mobicents:endpoint=ivr");
			ObjectName pktObjectName=new ObjectName("media.mobicents:endpoint=packet-relay");
			ObjectName confObjectName=new ObjectName("media.mobicents:endpoint=conf");
			ObjectName loopObjectName=new ObjectName("media.mobicents:endpoint=loopback");
			this.endpointManagementUtils=new EndpointManagementMBeanUtils(mbeanServer,ivrObjectName,confObjectName,annObjectName,pktObjectName,loopObjectName);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new ManagementConsoleException(MMSManagementMBeanUtils.doMessage(e));
		}

		
	}


	
	public RTPManagementMBeanUtils getRtpManagementMBeanUtils() {
		return rtpManagementMBeanUtils;
	}

	public EndpointManagementMBeanUtils getEndpointManagementUtils() {
		return endpointManagementUtils;
	}

	public static String doMessage(Throwable t) {
		StringBuffer sb = new StringBuffer();
		int tick = 0;
		Throwable e = t;
		do {
			StackTraceElement[] trace = e.getStackTrace();
			if (tick++ == 0)
				sb.append(e.getClass().getCanonicalName() + ":" + e.getLocalizedMessage() + "\n");
			else
				sb.append("Caused by: " + e.getClass().getCanonicalName() + ":" + e.getLocalizedMessage() + "\n");

			for (StackTraceElement ste : trace)
				sb.append("\t" + ste + "\n");

			e = e.getCause();
		} while (e != null);

		return sb.toString();

	}

}
