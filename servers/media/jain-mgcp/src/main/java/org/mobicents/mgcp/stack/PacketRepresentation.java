/**
 * Start time:13:36:13 2008-11-22<br>
 * Project: mobicents-media-server-controllers<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.mgcp.stack;

import java.net.InetAddress;

/**
 * Start time:13:36:13 2008-11-22<br>
 * Project: mobicents-media-server-controllers<br>
 * Simple packet representation, has byte[] raw data and remote port/address fields
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 */
public class PacketRepresentation {

	private byte[] rawData=null;
	private int remotePort=-1;
	private InetAddress remoteAddress=null;
	
	public PacketRepresentation()
	{}
	
	
	public PacketRepresentation(byte[] rawData,
			InetAddress remoteAddress, int remotePort) {
		super();
		this.rawData = rawData;
		this.remotePort = remotePort;
		this.remoteAddress = remoteAddress;
	}


	public byte[] getRawData() {
		return rawData;
	}
	public void setRawData(byte[] rawData) {
		this.rawData = rawData;
	}
	public int getRemotePort() {
		return remotePort;
	}
	public void setRemotePort(int remotePort) {
		this.remotePort = remotePort;
	}
	public InetAddress getRemoteAddress() {
		return remoteAddress;
	}
	public void setRemoteAddress(InetAddress remoteAddress) {
		this.remoteAddress = remoteAddress;
	}
	
}
