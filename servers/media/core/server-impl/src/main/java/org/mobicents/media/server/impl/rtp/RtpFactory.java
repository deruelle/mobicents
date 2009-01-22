/*
 * Mobicents Media Gateway
 *
 * The source code contained in this file is in in the public domain.
 * It can be used in any project or product without prior permission,
 * license or royalty payments. There is  NO WARRANTY OF ANY KIND,
 * EXPRESS, IMPLIED OR STATUTORY, INCLUDING, WITHOUT LIMITATION,
 * THE IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE,
 * AND DATA ACCURACY.  We do not warrant or make any representations
 * regarding the use of the software or the  results thereof, including
 * but not limited to the correctness, accuracy, reliability or
 * usefulness of the software.
 */
package org.mobicents.media.server.impl.rtp;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;

import net.java.stun4j.StunAddress;
import net.java.stun4j.client.NetworkConfigurationDiscoveryProcess;
import net.java.stun4j.client.StunDiscoveryReport;

import org.apache.log4j.Logger;
import org.mobicents.media.Format;
import org.mobicents.media.server.impl.BaseEndpoint;
import org.mobicents.media.server.impl.rtp.sdp.AVProfile;

/**
 * 
 * @author Oleg Kulikov
 */
public class RtpFactory {

	private Integer period;
	private Integer jitter;

	private InetAddress bindAddress;
	private int lowPortNumber;
	private int highPortNumber;

	private String stunServerAddress;
	private int stunServerPort;
	private boolean useStun = false;
	private boolean usePortMapping = true;
	private String publicAddressFromStun = null;

	private HashMap<Integer, Format> audioFormats;

	private transient Logger logger = Logger.getLogger(RtpFactory.class);

	public RtpFactory() {
		try {
			bindAddress = InetAddress.getLocalHost();
		} catch (Exception e) {
			logger.error("Failed to get the IP address for host", e);
		}
		lowPortNumber = 1024;
		highPortNumber = 65535;

		period = 20;
		jitter = 80;

		audioFormats = new HashMap<Integer, Format>();
		audioFormats.put(0, AVProfile.PCMU);
		audioFormats.put(8, AVProfile.PCMA);
	}

	public void create() {
		System.out.println("create: " + this);
	}

	public void start() {
		logger.info("Starting RTP Factory: ");
		if (!this.usePortMapping) {
			int randomPort = 9000;			
			for (int q = 0; q <= 10; q++) {
				if (mapStun(randomPort + q * 1003, getBindAddress()))
					break;
			}
		}
		logger.info("Formats endabled = : "+ this.audioFormats);
		logger.info("Started RTP Factory: ");
	}

	public void stop() {
		System.out.println("stop: " + this);
	}

	public void destroy() {
		System.out.println("destroy: " + this);
	}

	/**
	 * Gets the IP address to which trunk is bound. All endpoints of the trunk
	 * use this address for RTP connection.
	 * 
	 * @return the IP address string to which this trunk is bound.
	 */
	public String getBindAddress() {
		return bindAddress != null ? bindAddress.getHostAddress() : null;
	}

	/**
	 * Modify the bind address. All endpoints of the trunk use this address for
	 * RTP connection.
	 * 
	 * @param the
	 *            IP address string.
	 */
	public void setBindAddress(String bindAddress) throws UnknownHostException {
		this.bindAddress = InetAddress.getByName(bindAddress);
	}

	/**
	 * Gets the size of the jitter buffer in milliseconds.
	 * 
	 * Jitter buffer is used at the receiving ends of a VoIP connection. A
	 * jitter buffer stores received, time-jittered VoIP packets, that arrive
	 * within its time window. It then plays stored packets out, in sequence,
	 * and at a constant rate for subsequent decoding. A jitter buffer is
	 * typically filled half-way before playing out packets to allow early, or
	 * late, packet-arrival jitter compensation.
	 * 
	 * Choosing a large jitter buffer reduces packet dropping from jitter but
	 * increases VoIP path delay
	 * 
	 * @return the size of the buffer in milliseconds.
	 */
	public Integer getJitter() {
		return jitter;
	}

	/**
	 * Modify size of the jitter buffer.
	 * 
	 * Jitter buffer is used at the receiving ends of a VoIP connection. A
	 * jitter buffer stores received, time-jittered VoIP packets, that arrive
	 * within its time window. It then plays stored packets out, in sequence,
	 * and at a constant rate for subsequent decoding. A jitter buffer is
	 * typically filled half-way before playing out packets to allow early, or
	 * late, packet-arrival jitter compensation.
	 * 
	 * Choosing a large jitter buffer reduces packet dropping from jitter but
	 * increases VoIP path delay
	 * 
	 * @param jitter
	 *            the new buffer's size in milliseconds
	 */
	public void setJitter(Integer jitter) {
		this.jitter = jitter;
	}

	/**
	 * Gets packetization period.
	 * 
	 * The packetization period is the period over which encoded voice bits are
	 * collected for encapsulation in packets.
	 * 
	 * Higher period will reduce VoIP overhead allowing higher channel
	 * utilization
	 * 
	 * @return packetion period for media in milliseconds.
	 */
	public Integer getPacketizationPeriod() {
		return period;
	}

	/**
	 * Modify packetization period.
	 * 
	 * The packetization period is the period over which encoded voice bits are
	 * collected for encapsulation in packets.
	 * 
	 * Higher period will reduce VoIP overhead allowing higher channel
	 * utilization
	 * 
	 * @param period
	 *            the value of the packetization period in milliseconds.
	 */
	public void setPacketizationPeriod(Integer period) {
		this.period = period;
	}

	/**
	 * Gets the available port range.
	 * 
	 * @return the string in format "lowPort-highPort".
	 */
	public String getPortRange() {
		return lowPortNumber + "-" + highPortNumber;
	}

	/**
	 * Modify port used to create RTP stream.
	 * 
	 * @param portRange
	 *            the string in format "lowPort-highPort"
	 */
	public void setPortRange(String portRange) {
		String[] tokens = portRange.split("-");
		lowPortNumber = Integer.parseInt((tokens[0]).trim());
		highPortNumber = Integer.parseInt((tokens[1]).trim());
	}

	/**
	 * Gets the STUN server address.
	 * 
	 * @return the address of the server as string.
	 */
	public String getStunServerAddress() {
		return stunServerAddress;
	}

	/**
	 * Modify STUN server address.
	 * 
	 * @param stunServerAddress
	 *            the valid address of the server
	 */
	public void setStunServerAddress(String stunServerAddress) {
		this.stunServerAddress = stunServerAddress;
	}

	/**
	 * Gets the port of the STUN server.
	 * 
	 * @return the port number.
	 */
	public int getStunServerPort() {
		return stunServerPort;
	}

	/**
	 * Configures port of the STUN server.
	 * 
	 * @param stunServerPort
	 *            port number.
	 */
	public void setStunServerPort(int stunServerPort) {
		this.stunServerPort = stunServerPort;
	}

	/**
	 * Shows does STUN enabled.
	 * 
	 * @return true if STUN is used.
	 */
	public boolean isUseStun() {
		return useStun;
	}

	/**
	 * Enables/disables STUN usage.
	 * 
	 * @param useStun
	 *            true to use STUN or false otherwise.
	 */
	public void setUseStun(boolean useStun) {
		this.useStun = useStun;
	}

	/**
	 * Shows does NAT uses port mapping.
	 * 
	 * @return true if NAT uses port mapping.
	 */
	public boolean isUsePortMapping() {
		return usePortMapping;
	}

	/**
	 * Configures NAT traversal procedure.
	 * 
	 * @param usePortMapping
	 *            true if NAT uses port mapping.
	 */
	public void setUsePortMapping(boolean usePortMapping) {
		this.usePortMapping = usePortMapping;
	}

	public String getPublicAddressFromStun() {
		return publicAddressFromStun;
	}

	public void setPublicAddressFromStun(String publicAddressFromStun) {
		this.publicAddressFromStun = publicAddressFromStun;
	}

	/**
	 * Gets the list of supported RTP audioFormats.
	 * 
	 * @return map where key is payload number and value is Format object.
	 */
	public HashMap getAudioFormats() {
		return this.audioFormats;
	}

	/**
	 * Sets a list of supported RTPFormats.
	 * 
	 * @param audioFormats
	 *            a map where key is payload number and value is Format object.
	 */
	public void setAudioFormats(HashMap<Integer, Format> formats) {		
		this.audioFormats = formats;

	}

	/**
	 * Constructs new RTP socket.
	 * 
	 * @return the RTPSocketInstance.
	 * @throws java.net.SocketException
	 */
	public RtpSocket getRTPSocket(BaseEndpoint endpoint) throws SocketException {
		RtpSocketImpl rtpSocket = null;
		if (this.isUseStun()) {
			rtpSocket = new RtpSocketImpl(endpoint, period, jitter, stunServerAddress, stunServerPort, usePortMapping,
					publicAddressFromStun, audioFormats);
		} else {
			rtpSocket = new RtpSocketImpl(endpoint, period, jitter, audioFormats);
		}

		rtpSocket.init(bindAddress, lowPortNumber, highPortNumber);
		// rtpSocket.getRtpMap().putAll(audioFormats);
		return rtpSocket;
	}
	
	private boolean mapStun(int localPort, String localAddress) {
		try {
			if (InetAddress.getByName(localAddress).isLoopbackAddress()) {
				logger.warn("The Ip address provided is the loopback address, stun won't be enabled for it");
				this.publicAddressFromStun = localAddress;
			} else {
				StunAddress localStunAddress = new StunAddress(localAddress, localPort);

				StunAddress serverStunAddress = new StunAddress(stunServerAddress, stunServerPort);

				NetworkConfigurationDiscoveryProcess addressDiscovery = new NetworkConfigurationDiscoveryProcess(
						localStunAddress, serverStunAddress);
				addressDiscovery.start();
				StunDiscoveryReport report = addressDiscovery.determineAddress();
				if (report.getPublicAddress() != null) {
					this.publicAddressFromStun = report.getPublicAddress().getSocketAddress().getAddress()
							.getHostAddress();
					// TODO set a timer to retry the binding and provide a
					// callback to update the global ip address and port
				} else {
					useStun = false;
					logger.error("Stun discovery failed to find a valid public ip address, disabling stun !");
				}
				logger.info("Stun report = " + report);
				addressDiscovery.shutDown();
			}
		} catch (Throwable t) {
			logger.error("Stun lookup has failed: " + t.getMessage());
			return false;
		}
		return true;

	}
}
