package org.mobicents.media.container.management.console.server.endpoint;

import org.mobicents.media.container.management.console.client.ManagementConsoleException;
import org.mobicents.media.container.management.console.client.endpoint.ConnectionInfo;
import org.mobicents.media.container.management.console.client.endpoint.EndpointFullInfo;
import org.mobicents.media.container.management.console.client.endpoint.EndpointManagementService;
import org.mobicents.media.container.management.console.client.endpoint.EndpointShortInfo;
import org.mobicents.media.container.management.console.client.endpoint.EndpointType;
import org.mobicents.media.container.management.console.client.endpoint.NoSuchConnectionException;
import org.mobicents.media.container.management.console.client.endpoint.NoSuchEndpointException;
import org.mobicents.media.container.management.console.server.ManagementConsole;
import org.mobicents.media.container.management.console.server.mbeans.EndpointManagementMBeanUtils;
import org.mobicents.media.container.management.console.server.mbeans.MMSMBeanConnection;
import org.mobicents.media.container.management.console.server.mbeans.MMSManagementMBeanUtils;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class EndpointManagementServiceImpl extends RemoteServiceServlet
		implements EndpointManagementService {

	EndpointManagementMBeanUtils utils;

	public EndpointManagementServiceImpl() {
		super();
		this.utils = ManagementConsole.getInstance().getMMSConnection()
				.getMMSManagementMBeanUtils().getEndpointManagementUtils();
	}

	public ConnectionInfo getConnectionInfo(String endpointName,EndpointType type,
			String connectionId) throws ManagementConsoleException,
			NoSuchEndpointException, NoSuchConnectionException {


			return utils.getConnectionInfo(endpointName, type,connectionId);

	}

	public EndpointFullInfo getEndpointInfo(String endpointName,EndpointType type)
			throws ManagementConsoleException, NoSuchEndpointException {


			return utils.getEndpointInfo(endpointName,type);
		
	}

	public EndpointShortInfo[] getEndpointsShortInfo()
			throws ManagementConsoleException {

			return utils.getEndpointsShortInfo();
	
	}

	public EndpointShortInfo[] getEndpointsShortInfo(EndpointType type)
			throws ManagementConsoleException {


			EndpointShortInfo[] tmp = utils.getEndpointsShortInfo(type);

			return tmp;

	}


	public void setGatherPerformanceData(String endpointName,EndpointType type, boolean value)
			throws ManagementConsoleException, NoSuchEndpointException {
		utils.setGatherPerformanceData(endpointName,type,value);
		
	}

	public void setRTPFactoryJNDIName(String endpointName, EndpointType type,String jndiName)throws ManagementConsoleException, NoSuchEndpointException
	{
		utils.setRTPFactoryJNDIName( endpointName,  type, jndiName);
	}

	public void destroyConnection(String name, EndpointType type,
			String connectionId) throws ManagementConsoleException,
			NoSuchEndpointException, NoSuchConnectionException {
		utils.destroyConnection(name, type, connectionId);
	}

	public void destroyEndpoint(String name, EndpointType type)
			throws ManagementConsoleException, NoSuchEndpointException {
		utils.destroyEndpoint(name, type);
	}

	public EndpointFullInfo getEndpointTrunkInfo(EndpointType type) throws ManagementConsoleException, NoSuchEndpointException, NoSuchConnectionException {
		
		return utils.getEndpointTrunkInfo(type);
	}
	
}
