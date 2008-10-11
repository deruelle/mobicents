package org.mobicents.media.container.management.console.client.endpoint;

import org.mobicents.media.container.management.console.client.rtp.XFormat;

public class EndpointFullInfo extends EndpointShortInfo {

	protected String rtpFactoryJNDIName = null;
	protected ConnectionInfo[] connectionsInfo = null;
	protected XFormat[] audioCodecs = null;
	protected XFormat[] videoCodes = null;

	public EndpointFullInfo() {
		super();
		// TODO Auto-generated constructor stub
	}

	public EndpointFullInfo(EndpointType type, String name, int gen,
			int connections, long creationTime, long packets,
			long numberOfBytes, boolean getherPerformance,
			String rtpFactoryJNDIName, ConnectionInfo[] connectionsInfo,
			XFormat[] audioCodecs, XFormat[] videoCodes) {
		super(type, name, gen, connections, creationTime, packets,
				numberOfBytes, getherPerformance);
		this.rtpFactoryJNDIName = rtpFactoryJNDIName;
		this.connectionsInfo = connectionsInfo;
		this.audioCodecs = audioCodecs;
		this.videoCodes = videoCodes;
	}

	public String getRtpFactoryJNDIName() {
		return rtpFactoryJNDIName;
	}

	public void setRtpFactoryJNDIName(String rtpFactoryJNDIName) {
		this.rtpFactoryJNDIName = rtpFactoryJNDIName;
	}

	public ConnectionInfo[] getConnectionsInfo() {
		return connectionsInfo;
	}

	public void setConnectionsInfo(ConnectionInfo[] connectionsInfo) {
		this.connectionsInfo = connectionsInfo;
	}

	public XFormat[] getAudioCodecs() {
		return audioCodecs;
	}

	public void setAudioCodecs(XFormat[] audioCodecs) {
		this.audioCodecs = audioCodecs;
	}

	public XFormat[] getVideoCodes() {
		return videoCodes;
	}

	public void setVideoCodes(XFormat[] videoCodes) {
		this.videoCodes = videoCodes;
	}

	
	
}
