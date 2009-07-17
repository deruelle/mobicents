/**
 * Start time:13:36:13 2008-11-22<br>
 * Project: mobicents-media-server-controllers<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.mgcp.stack.utils;

import java.net.InetAddress;

import org.apache.log4j.Logger;

/**
 * Start time:13:36:13 2008-11-22<br>
 * Project: mobicents-media-server-controllers<br>
 * Simple packet representation, has byte[] raw data and remote port/address
 * fields
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 */
public class PacketRepresentation {	

	private byte[] rawData = null;
	private int remotePort = -1;
	private InetAddress remoteAddress = null;
	private int length = 0;

	private PacketRepresentationFactory prFactory = null;

	protected PacketRepresentation(int byteArrSize, PacketRepresentationFactory prFactory) {
		rawData = new byte[byteArrSize];
		this.prFactory = prFactory;
	}

	public byte[] getRawData() {
		return rawData;
	}

	public void setRawData(byte[] rawData) {
		this.rawData = rawData;
	}

	public int getLength() {
		return this.length;
	}

	public int getRemotePort() {
		return remotePort;
	}

	public void setLength(int length) {
		this.length = length;
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

	public void release() {
		this.prFactory.deallocate(this);
	}

}
