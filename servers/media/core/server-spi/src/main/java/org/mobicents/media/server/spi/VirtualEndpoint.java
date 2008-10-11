package org.mobicents.media.server.spi;


public interface VirtualEndpoint {
	public Endpoint createEndpoint();
	public Endpoint getEndpoint(String name);
	public String[] getEndpointsNames();
}
