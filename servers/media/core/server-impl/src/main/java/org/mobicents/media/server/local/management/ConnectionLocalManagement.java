package org.mobicents.media.server.local.management;

public interface ConnectionLocalManagement {

	
	public long getConnectionCreationTime() throws IllegalArgumentException;
	public String getOtherEnd() throws IllegalArgumentException;
	public long getNumberOfPackets() throws IllegalArgumentException;
	
}
