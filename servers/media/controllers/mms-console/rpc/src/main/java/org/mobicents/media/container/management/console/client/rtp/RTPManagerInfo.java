package org.mobicents.media.container.management.console.client.rtp;



import com.google.gwt.user.client.rpc.IsSerializable;

public class RTPManagerInfo implements IsSerializable {

	
	protected String objectName=null;
	protected XFormat[] audioFormats=null;
	protected XFormat[] videoFormats=null;
	protected String jndiName;
    
	protected String bindAddress;
	protected Integer packetizationPeriod;
	protected Integer jitter;
	protected Integer[] portRange;
    
	protected String stunServerAddress;
	protected Integer stunServerPort;
	protected boolean useStun = false;
	protected boolean usePortMapping = true;
	protected String publicAddressFromStun = null;
	public String getObjectName() {
		return objectName;
	}
	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}
	public XFormat[] getAudioFormats() {
		return audioFormats;
	}
	public void setAudioFormats(XFormat[] audioFormats) {
		System.out.println("Setting fromats");
		for(int i=0;i<audioFormats.length;i++)
		{
			System.out.println("["+i+"] "+audioFormats[i]);	
		}
		this.audioFormats = audioFormats;
	}
	public XFormat[] getVideoFormats() {
		return videoFormats;
	}
	public void setVideoFormats(XFormat[] videoFormats) {
		this.videoFormats = videoFormats;
	}
	public String getJndiName() {
		return jndiName;
	}
	public void setJndiName(String jndiName) {
		this.jndiName = jndiName;
	}
	public String getBindAddress() {
		return bindAddress;
	}
	public void setBindAddress(String bindAddress) {
		this.bindAddress = bindAddress;
	}
	public Integer getPacketizationPeriod() {
		return packetizationPeriod;
	}
	public void setPacketizationPeriod(Integer packetizationPeriod) {
		this.packetizationPeriod = packetizationPeriod;
	}
	public Integer getJitter() {
		return jitter;
	}
	public void setJitter(Integer jitter) {
		this.jitter = jitter;
	}
	public Integer[] getPortRange() {
		return portRange;
	}
	public void setPortRange(Integer[] portRange) {
		this.portRange = portRange;
	}
	public String getStunServerAddress() {
		return stunServerAddress;
	}
	public void setStunServerAddress(String stunServerAddress) {
		this.stunServerAddress = stunServerAddress;
	}
	public Integer getStunServerPort() {
		return stunServerPort;
	}
	public void setStunServerPort(Integer stunServerPort) {
		this.stunServerPort = stunServerPort;
	}
	public boolean getUseStun() {
		return useStun;
	}
	public void setUseStun(boolean useStun) {
		this.useStun = useStun;
	}
	public boolean getUsePortMapping() {
		return usePortMapping;
	}
	public void setUsePortMapping(boolean usePortMapping) {
		this.usePortMapping = usePortMapping;
	}
	public String getPublicAddressFromStun() {
		return publicAddressFromStun;
	}
	public void setPublicAddressFromStun(String publicAddressFromStun) {
		this.publicAddressFromStun = publicAddressFromStun;
	}
	public RTPManagerInfo(XFormat[] audioFormats, String bindAddress, Integer jitter, String jndiName, String objectName, Integer packetizationPeriod, Integer[] portRange,
			String publicAddressFromStun, String stunServerAddress, Integer stunServerPort, boolean usePortMapping, boolean useStun, XFormat[] videoFormats) {
		super();
		this.audioFormats = audioFormats;
		this.bindAddress = bindAddress;
		this.jitter = jitter;
		this.jndiName = jndiName;
		this.objectName = objectName;
		this.packetizationPeriod = packetizationPeriod;
		this.portRange = portRange;
		this.publicAddressFromStun = publicAddressFromStun;
		this.stunServerAddress = stunServerAddress;
		this.stunServerPort = stunServerPort;
		this.usePortMapping = usePortMapping;
		this.useStun = useStun;
		this.videoFormats = videoFormats;
	}
	public RTPManagerInfo() {
		super();
	}

	
	
}
