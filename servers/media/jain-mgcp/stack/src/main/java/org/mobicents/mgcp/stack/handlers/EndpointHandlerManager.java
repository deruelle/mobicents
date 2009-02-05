package org.mobicents.mgcp.stack.handlers;

import org.mobicents.mgcp.stack.EndpointHandler;
import org.mobicents.mgcp.stack.ThreadPoolQueueExecutor;

public interface EndpointHandlerManager {

	public EndpointHandler getEndpointHandler(String endpointId,
			boolean useFakeOnWildcard);

	public void removeEndpointHandler(String endpointId);

	/**
	 * This fnction switches mapping from fakeId to specificEndpointId. If
	 * endpoint handler bound to specific endpoint id exists it is returned
	 * without any modification otherwise return value is zero and endpoint
	 * handler mapping has been switched.
	 * 
	 * @param fakeId
	 * @param specificEndpointId
	 * @return
	 */
	public EndpointHandler switchMapping(String fakeId,
			String specificEndpointId);
	
	public ThreadPoolQueueExecutor getNextExecutor();
}
