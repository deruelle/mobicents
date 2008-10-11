package org.mobicents.media.container.management.console.client.endpoint;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ConnectionInfo implements IsSerializable{

	
	protected String endpointName=null;
	protected String connecitonId=null;
	protected long creationTime=0;
	protected String localSdp=null;
	protected String remoteSdp=null;
	protected String otherEnd=null;
	protected String state=null;
	protected String mode=null;
	protected long numberOfPackets=0;



	public ConnectionInfo(String endpointName, String connecitonId,
			long creationTime, String localSdp, String remoteSdp,
			String otherEnd, String state, String mode, long numberOfPackets) {
		super();
		this.endpointName = endpointName;
		this.connecitonId = connecitonId;
		this.creationTime = creationTime;
		this.localSdp = localSdp;
		this.remoteSdp = remoteSdp;
		this.otherEnd = otherEnd;
		this.state = state;
		this.mode = mode;
		this.numberOfPackets = numberOfPackets;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public ConnectionInfo() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getEndpointName() {
		return endpointName;
	}

	public void setEndpointName(String endpointName) {
		this.endpointName = endpointName;
	}

	public String getConnecitonId() {
		return connecitonId;
	}

	public void setConnecitonId(String connecitonId) {
		this.connecitonId = connecitonId;
	}

	public long getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(long creationTime) {
		this.creationTime = creationTime;
	}

	public String getLocalSdp() {
		return localSdp;
	}

	public void setLocalSdp(String localSdp) {
		this.localSdp = localSdp;
	}

	public String getRemoteSdp() {
		return remoteSdp;
	}

	public void setRemoteSdp(String remoteSdp) {
		this.remoteSdp = remoteSdp;
	}

	public String getOtherEnd() {
		return otherEnd;
	}

	public void setOtherEnd(String otherEnd) {
		this.otherEnd = otherEnd;
	}

	public long getNumberOfPackets() {
		return numberOfPackets;
	}

	public void setNumberOfPackets(long numberOfPackets) {
		this.numberOfPackets = numberOfPackets;
	}
	
}

