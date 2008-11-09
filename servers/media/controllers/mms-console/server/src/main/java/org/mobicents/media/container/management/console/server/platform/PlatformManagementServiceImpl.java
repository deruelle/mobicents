package org.mobicents.media.container.management.console.server.platform;

import org.mobicents.media.container.management.console.client.ManagementConsoleException;
import org.mobicents.media.container.management.console.client.endpoint.ConnectionInfo;
import org.mobicents.media.container.management.console.client.endpoint.EndpointFullInfo;
import org.mobicents.media.container.management.console.client.endpoint.EndpointManagementService;
import org.mobicents.media.container.management.console.client.endpoint.EndpointShortInfo;
import org.mobicents.media.container.management.console.client.endpoint.EndpointType;
import org.mobicents.media.container.management.console.client.endpoint.NoSuchConnectionException;
import org.mobicents.media.container.management.console.client.endpoint.NoSuchEndpointException;
import org.mobicents.media.container.management.console.client.platform.PlatformManagementService;
import org.mobicents.media.container.management.console.client.platform.ServerState;
import org.mobicents.media.container.management.console.server.ManagementConsole;
import org.mobicents.media.container.management.console.server.mbeans.EndpointManagementMBeanUtils;
import org.mobicents.media.container.management.console.server.mbeans.MMSMBeanConnection;
import org.mobicents.media.container.management.console.server.mbeans.MMSManagementMBeanUtils;
import org.mobicents.media.container.management.console.server.mbeans.VirtualEndpointManagementUtils;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class PlatformManagementServiceImpl extends RemoteServiceServlet
		implements PlatformManagementService {

	VirtualEndpointManagementUtils utils;

	public PlatformManagementServiceImpl() {
		super();
		this.utils = ManagementConsole.getInstance().getMMSConnection()
				.getMMSManagementMBeanUtils().getVirtualEndpointManagementUtils();
	}

	public ServerState getState() throws ManagementConsoleException {
		
		return utils.getState();
	}

	public void start() throws ManagementConsoleException {
		
		utils.start();
	}

	public void stop() throws ManagementConsoleException {
		utils.stop();
		
	}

	public void tearDown() throws ManagementConsoleException {
		utils.tearDown();
		
	}

	public String getVersion() throws ManagementConsoleException {
		return utils.getVersion();
	}


	
}
