package org.mobicents.media.server.local.management;

public interface ConnectionLocalManagement {

	public long getConnectionCreationTime();

	public String getOtherEnd();

	public boolean isGatherStats();

	public void setGatherStats(boolean gatherStats);

	public int getPacketsSent();

	public int getPacketsReceived();

	public int getOctetsReceived();

	public int getOctetsSent();

	public int getInterArrivalJitter();

	public int getPacketsLost();

}
