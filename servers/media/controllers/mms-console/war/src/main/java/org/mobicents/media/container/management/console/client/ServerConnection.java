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
package org.mobicents.media.container.management.console.client;

import org.mobicents.media.container.management.console.client.common.UserInterface;
import org.mobicents.media.container.management.console.client.endpoint.EndpointManagementService;
import org.mobicents.media.container.management.console.client.endpoint.EndpointManagementServiceAsync;
import org.mobicents.media.container.management.console.client.platform.PlatformManagementService;
import org.mobicents.media.container.management.console.client.platform.PlatformManagementServiceAsync;
import org.mobicents.media.container.management.console.client.rtp.RTPManagementService;
import org.mobicents.media.container.management.console.client.rtp.RTPManagementServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.ServiceDefTarget;


/**
 * 
 * <br><br>Super project:  mobicents-media-server-controllers
 * <br>12:39:51 2008-09-12	
 * <br>
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski </a> 
 */
public class ServerConnection {

	final static public RTPManagementServiceAsync rtpManagementServiceAsync;
	final static public EndpointManagementServiceAsync endpointManagementServiceAsync;
	 static public PlatformManagementServiceAsync platformManagementServiceAsync;
    //final static public LogServiceAsync logServiceAsync;
    
	static {
		
		rtpManagementServiceAsync=(RTPManagementServiceAsync) GWT.create(RTPManagementService.class);
		ServiceDefTarget rtpManagementServiceAsyncEndpoint = (ServiceDefTarget) rtpManagementServiceAsync;
		rtpManagementServiceAsyncEndpoint.setServiceEntryPoint(GWT.getModuleBaseURL().replaceAll("org.mobicents.media.container.management.console.ManagementConsole", "") + "/RTPManagementService");
		
		
		
		
		
		endpointManagementServiceAsync=(EndpointManagementServiceAsync) GWT.create(EndpointManagementService.class);
		ServiceDefTarget endpointManagementServiceAsyncEndpoint = (ServiceDefTarget) endpointManagementServiceAsync;
		endpointManagementServiceAsyncEndpoint.setServiceEntryPoint(GWT.getModuleBaseURL().replaceAll("org.mobicents.media.container.management.console.ManagementConsole", "") + "/EndpointManagementService");
		

		platformManagementServiceAsync=(PlatformManagementServiceAsync) GWT.create(PlatformManagementService.class);
		ServiceDefTarget platformManagementServiceAsyncEndpoint = (ServiceDefTarget) platformManagementServiceAsync;
		platformManagementServiceAsyncEndpoint.setServiceEntryPoint(GWT.getModuleBaseURL().replaceAll("org.mobicents.media.container.management.console.ManagementConsole", "") + "/PlatformManagementService");
		
		
	}
	
}
