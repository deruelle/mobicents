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

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Map;

import net.java.stun4j.StunException;

import org.apache.log4j.Logger;
import org.mobicents.media.Format;
import org.mobicents.media.server.impl.clock.TimerImpl;

/**
 * 
 * @author Oleg Kulikov
 */
public class RtpFactory {

	private Integer jitter;

	private InetAddress bindAddress;
	private String stunAddress;

	private int lowPortNumber;
	private int highPortNumber;

	private TimerImpl timer;
	private Map<Integer, Format> formatMap;

	private LinkedList<RtpSocket> rtpSockets = new LinkedList<RtpSocket>();

	private transient Logger logger = Logger.getLogger(RtpFactory.class);

	/**
	 * Creates RTP Factory instance
	 */
	public RtpFactory() {
	}

	public void stop() {
		RtpSocket.readerThread.shutdown();

		for (RtpSocket r : rtpSockets) {
			r.close();
		}

		logger.info("Stopped RTP Factory");
	}

	/**
	 * Gets media processing timer used by RTP socket.
	 * 
	 * @return timer object.
	 */
	public TimerImpl getTimer() {
		return timer;
	}

	/**
	 * Assigns media processing timer.
	 * 
	 * @param timer
	 *            tmer object.
	 */
	public void setTimer(TimerImpl timer) {
		this.timer = timer;
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
	 * @param bindAddress
	 *            IP address as string or host name.
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
	 * Gets the address of stun server if present.
	 * 
	 * @return the address of stun server or null if not assigned.
	 */
	public String getStunAddress() {
		return stunAddress;
	}

	/**
	 * Assigns address of the STUN server.
	 * 
	 * @param address
	 *            the address of the stun server in format host[:port]. if port
	 *            is not set then default port is used.
	 */
	public void setStunAddress(String address) {
		this.stunAddress = address;
	}

	/**
	 * Constructs new RTP socket.
	 * 
	 * @return the RTPSocketInstance.
	 * @throws StunException
	 * @throws IOException
	 * @throws SocketException
	 * @throws StunException
	 * @throws IOException
	 */
	public RtpSocket getRTPSocket() throws SocketException, IOException, StunException {
		RtpSocket rtpSocket = rtpSockets.poll();

		if (rtpSocket == null) {
			rtpSocket = new RtpSocket(timer, formatMap, this);
			rtpSocket.init(bindAddress, lowPortNumber, highPortNumber);
		}
		return rtpSocket;
	}

	public void releaseRTPSocket(RtpSocket rtpSocket) {
		rtpSockets.add(rtpSocket);
	}

	public Map<Integer, Format> getFormatMap() {
		return this.formatMap;
	}

	public void setFormatMap(Map<Integer, Format> formatMap) {
		this.formatMap = formatMap;
	}
}
