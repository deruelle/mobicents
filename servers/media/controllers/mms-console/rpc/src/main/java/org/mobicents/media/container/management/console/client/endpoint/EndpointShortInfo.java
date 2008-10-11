package org.mobicents.media.container.management.console.client.endpoint;

import com.google.gwt.user.client.rpc.IsSerializable;

public class EndpointShortInfo implements IsSerializable {

	protected EndpointType type = null;
	protected String name = null;
	protected int GEN = -1;
	protected int connections = 0;
	protected long creationTime = 0;
	protected long packets = 0;
	protected long numberOfBytes = 0;
	protected boolean getherPerformance=false;
	// FIXME: add more info - utilization, connnections etc
	public EndpointShortInfo() {
	}

	public EndpointShortInfo(EndpointType type, String name, int gen,
			int connections, long creationTime, long packets,
			long numberOfBytes, boolean getherPerformance) {
		super();
		this.type = type;
		this.name = name;
		//GEN = gen;
		this.connections = connections;
		this.creationTime = creationTime;
		this.packets = packets;
		this.numberOfBytes = numberOfBytes;
		this.getherPerformance = getherPerformance;
	}

	public EndpointType getType() {
		return type;
	}

	public void setType(EndpointType type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	//public int getGEN() {
	//	return GEN;
	//}

	//public void setGEN(int gen) {
	//	GEN = gen;
	//}

	public int getConnections() {
		return connections;
	}

	public void setConnections(int connections) {
		this.connections = connections;
	}

	public long getPackets() {
		return packets;
	}

	public void setPackets(long packets) {
		this.packets = packets;
	}

	public long getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(long creationTime) {
		this.creationTime = creationTime;
	}

	public long getNumberOfBytes() {
		return numberOfBytes;
	}

	public void setNumberOfBytes(long numberOfBytes) {
		this.numberOfBytes = numberOfBytes;
	}

	public boolean isGetherPerformance() {
		return getherPerformance;
	}

	public void setGetherPerformance(boolean getherPerformance) {
		this.getherPerformance = getherPerformance;
	}

}
