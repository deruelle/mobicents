package org.mobicents.media.container.management.console.client.endpoint;




import com.google.gwt.user.client.rpc.AsyncCallback;

public interface EndpointManagementServiceAsync {

	
	public void getEndpointsShortInfo(AsyncCallback callback);
	public void getEndpointsShortInfo(EndpointType type,AsyncCallback callback);
	public void getEndpointInfo(String endpointName, EndpointType type,AsyncCallback callback);
	public void getConnectionInfo(String endpointName, EndpointType type, String connectionId,AsyncCallback callback);
	public void setGatherPerformanceData(String endpointName,EndpointType type, boolean value, AsyncCallback gatherDataSetAsyncCallback);
	public void setRTPFactoryJNDIName(String endpointName, EndpointType type,String jndiName,AsyncCallback callback);
	public void destroyEndpoint(String name, EndpointType type,	AsyncCallback destroyActionAsyncCallback);
	public void destroyConnection(String name, EndpointType type, String connectionId,	AsyncCallback destroyActionAsyncCallback);
}
