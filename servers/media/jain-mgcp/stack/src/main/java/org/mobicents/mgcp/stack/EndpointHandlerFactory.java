package org.mobicents.mgcp.stack;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.mobicents.mgcp.stack.handlers.EndpointHandlerManager;

public class EndpointHandlerFactory {

	private static Logger logger = Logger.getLogger(EndpointHandlerFactory.class);

	private int size = 0;
	private int count = 0;
	private EndpointHandlerManager jainMgcpStackImpl = null;
	private List<EndpointHandler> list = new ArrayList<EndpointHandler>();

	public EndpointHandlerFactory(int size, EndpointHandlerManager jainMgcpStackImpl) {
		this.size = size;
		this.jainMgcpStackImpl = jainMgcpStackImpl;
	}

	public EndpointHandler allocate(String endpoint) {
		EndpointHandler handler = null;

		if (!list.isEmpty()) {
			handler = list.remove(0);
			handler.init(endpoint);
			return handler;
		}

		handler = new EndpointHandler(this.jainMgcpStackImpl, this);
		handler.init(endpoint);
		count++;

		if (logger.isDebugEnabled()) {
			logger.debug("EndpointHandlerFactory underflow. Count = " + count);
		}
		return handler;
	}

	public void deallocate(EndpointHandler handler) {
		if (list.size() < size && handler != null) {
			list.add(handler);
		}
	}

	public int getSize() {
		return this.size;
	}

	public int getCount() {
		return this.count;
	}

}
