package org.mobicents.mgcp.stack.handlers;

import org.mobicents.mgcp.stack.EndpointHandler;

public interface TransactionHandlerManagement extends Runnable{

	void clearEndpointHandler();

	void setEndpointHandler(EndpointHandler endpointHandler);
}
