package org.mobicents.media.server.local.management;

public interface EndpointLocalManagement {

	public int getConnectionsCount();
	public long getCreationTime();
	public long getPacketsCount();
	public long getNumberOfBytes();
	public void setGatherPerformanceData(boolean flag);
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
	public String getConnectionState(String connectionId) throws IllegalArgumentException;
	public String getConnectionMode(String connectionId) throws IllegalArgumentException;
	public void setRTPFacotryJNDIName(String jndiName) throws IllegalArgumentException;
	public int getPacketsSent(String connectionId) throws IllegalArgumentException;
	public int getPacketsReceived(String connectionId) throws IllegalArgumentException;
	public int getOctetsReceived(String connectionId) throws IllegalArgumentException;
	public int getOctetsSent(String connectionId) throws IllegalArgumentException;
	public int getInterArrivalJitter(String connectionId) throws IllegalArgumentException;
	public int getPacketsLost(String connectionId) throws IllegalArgumentException;
	
	
	
}
