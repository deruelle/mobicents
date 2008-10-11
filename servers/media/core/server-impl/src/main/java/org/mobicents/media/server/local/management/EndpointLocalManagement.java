package org.mobicents.media.server.local.management;

public interface EndpointLocalManagement {

	public int getConnectionsCount();
	public long getCreationTime();
	public long getPacketsCount();
	public long getNumberOfBytes();
	
	public void setGatherPerformanceFlag(boolean flag);
	public boolean getGatherPerformanceFlag();
	public String getLocalName();
	public String getRTPFacotryJNDIName();
	public EndpointLocalManagement[] getEndpoints();
	public String[] getEndpointNames();
	
	
	public String[] getConnectionIds();
	
	public long getConnectionCreationTime(String connectionId) throws IllegalArgumentException;
	public String getConnectionLocalSDP(String connectionId) throws IllegalArgumentException;
	public String getConnectionRemoteSDP(String connectionId) throws IllegalArgumentException;
	public String getOtherEnd(String connectionId) throws IllegalArgumentException;
	public long getNumberOfPackets(String connectionId) throws IllegalArgumentException;
	public String getConnectionState(String connectionId) throws IllegalArgumentException;
	public String getConnectionMode(String connectionId) throws IllegalArgumentException;
	public void setRTPFacotryJNDIName(String jndiName) throws IllegalArgumentException;
	
	
	
}
