package org.mobicents.media.container.management.console.client.endpoint;

import org.mobicents.media.container.management.console.client.ManagementConsoleException;

import com.google.gwt.user.client.rpc.RemoteService;

public interface EndpointManagementService extends RemoteService{

	public EndpointShortInfo[] getEndpointsShortInfo() throws ManagementConsoleException;
	public EndpointShortInfo[] getEndpointsShortInfo(EndpointType type) throws ManagementConsoleException;;
	public EndpointFullInfo getEndpointInfo(String endpointName, EndpointType type)  throws ManagementConsoleException, NoSuchEndpointException;
	public ConnectionInfo getConnectionInfo(String endpointName, EndpointType type, String connectionId) throws ManagementConsoleException, NoSuchEndpointException, NoSuchConnectionException;
	public void setGatherPerformanceData(String endpointName,EndpointType type, boolean value) throws ManagementConsoleException, NoSuchEndpointException;
	public void setRTPFactoryJNDIName(String endpointName, EndpointType type,String jndiName)throws ManagementConsoleException, NoSuchEndpointException;
	public void destroyEndpoint(String name, EndpointType type) throws ManagementConsoleException, NoSuchEndpointException;
	public void destroyConnection(String name, EndpointType type, String connectionId) throws ManagementConsoleException, NoSuchEndpointException, NoSuchConnectionException;
	public EndpointFullInfo getEndpointTrunkInfo(EndpointType type) throws ManagementConsoleException, NoSuchEndpointException, NoSuchConnectionException;
}
