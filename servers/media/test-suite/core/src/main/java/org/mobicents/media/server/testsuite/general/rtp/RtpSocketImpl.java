/**
 * Start time:10:17:34 2009-08-03<br>
 * Project: mobicents-media-server-test-suite<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */
package org.mobicents.media.server.testsuite.general.rtp;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ScheduledFuture;

import javax.sdp.Attribute;

import org.apache.log4j.Logger;
import org.mobicents.media.Buffer;
import org.mobicents.media.Format;
import org.mobicents.media.server.impl.rtp.RtpHeader;
import org.mobicents.media.server.testsuite.general.Timer;

/**
 * Start time:10:17:34 2009-08-03<br>
 * Project: mobicents-media-server-test-suite<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski</a>
 */
public class RtpSocketImpl implements RtpSocket {

	private static final transient Logger log = Logger.getLogger(RtpSocketImpl.class);



	private int localPort;
	private String localAddress;

	private int remotePort;
	String remoteAddress;
	private InetSocketAddress remoteInetSocketAddress = null;

	// holder for dynamic payloads.
	private Vector<Attribute> rtpMap = new Vector<Attribute>();

	private final List<RtpSocketListener> dataSinks = new ArrayList<RtpSocketListener>();

	private RtpSocketFactoryImpl rtpSocketFactoryImpl;
	private Timer timer;

	/**
	 * 
	 * @param transceiver
	 * @param timer
	 * @param formatMap
	 * @param rtpSocketFactoryImpl2
	 */
	public RtpSocketImpl(Transceiver transceiver, Timer timer, Vector<Attribute> formatMap, RtpSocketFactoryImpl rtpSocketFactory) {
		this.rtpSocketFactoryImpl = rtpSocketFactory;
		this.timer = timer;
		this.rtpMap.addAll(formatMap);
		
		this.localAddress = this.rtpSocketFactoryImpl.getBindAddress();
		this.localPort = this.rtpSocketFactoryImpl.getPort();
	}




	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mobicents.media.server.testsuite.general.rtp.RtpSocket#addListener
	 * (org.mobicents.media.server.testsuite.general.rtp.RtpSocketListener)
	 */
	public void addListener(RtpSocketListener listener) {
		this.dataSinks.add(listener);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mobicents.media.server.testsuite.general.rtp.RtpSocket#removeListener
	 * (org.mobicents.media.server.testsuite.general.rtp.RtpSocketListener)
	 */
	public void removeListener(RtpSocketListener listener) {
		this.dataSinks.remove(listener);

	}

	public void resetRtpMap() {
		this.rtpMap.clear();
		this.rtpMap.addAll(this.rtpSocketFactoryImpl.getFormatMap());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.media.server.testsuite.general.rtp.RtpSocket#close()
	 */
	public void close() {
		this.rtpSocketFactoryImpl.rtpSockets.remove(remoteInetSocketAddress);

	}

	/**
	 * Assigns remote end.
	 * 
	 * @param address
	 *            the address of the remote party.
	 * @param port
	 *            the port number of the remote party.
	 */
	public void setPeer(InetAddress address, int port) throws IOException {
		remoteAddress = address.getHostAddress();
		remotePort = port;
		remoteInetSocketAddress = new InetSocketAddress(remoteAddress, remotePort);
		this.rtpSocketFactoryImpl.rtpSockets.put(remoteInetSocketAddress, this);

		if (log.isDebugEnabled()) {
			log.debug("Connect RTP socket[" + localAddress + ":" + localPort + " to " + remoteAddress + ":" + remotePort);
		}

	}

	/**
	 * Gets address to which this socked is bound.
	 * 
	 * @return either local address to which this socket is bound
	 */
	public String getLocalAddress() {
		return localAddress;
	}

	/**
	 * Returns port number to which this socked is bound.
	 * 
	 * @return port number or -1 if socket not bound.
	 */
	public int getLocalPort() {
		return localPort;
	}

	public void notify(Exception e) {
		for (RtpSocketListener l : this.dataSinks)
			l.error(e);
	}

	/* (non-Javadoc)
	 * @see org.mobicents.media.server.testsuite.general.rtp.RtpSocket#receive(org.mobicents.media.server.testsuite.general.rtp.RtpPacket)
	 */
	public void receive(RtpPacket rtpPacket) {
		for (RtpSocketListener l : this.dataSinks)
			l.receive(rtpPacket);
		
	}

}
