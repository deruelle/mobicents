/**
 * Start time:09:56:52 2009-08-03<br>
 * Project: mobicents-media-server-test-suite<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */
package org.mobicents.media.server.testsuite.general.rtp;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.spi.SelectorProvider;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ScheduledExecutorService;

import javax.sdp.Attribute;

import org.apache.log4j.Logger;

import org.mobicents.media.server.testsuite.general.Timer;

/**
 * Start time:09:56:52 2009-08-03<br>
 * Project: mobicents-media-server-test-suite<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */
public class RtpSocketFactoryImpl implements RtpSocketFactory {

	private static final Logger log = Logger.getLogger(RtpSocketFactoryImpl.class);

	private InetAddress bindAddress;

	private int localPort=6006;


	private Timer timer;
	private Vector<Attribute> formatMap;

	protected volatile HashMap<SocketAddress, RtpSocket> rtpSockets = new HashMap<SocketAddress, RtpSocket>();
	private Transceiver transceiver;

	public RtpSocket createSocket() throws SocketException, IOException {
		RtpSocket rtpSocket = new RtpSocketImpl(transceiver, timer, formatMap, this);
		return rtpSocket;
	}

	public void releaseSocket(RtpSocket rtpSocket) {
		rtpSockets.remove(((RtpSocketImpl)rtpSocket).remoteAddress);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.mobicents.media.server.testsuite.general.rtp.RtpSocketFactory#
	 * getFormatMap()
	 */
	public Vector<Attribute> getFormatMap() {
		return this.formatMap;
	}

	

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mobicents.media.server.testsuite.general.rtp.RtpSocketFactory#getTimer
	 * ()
	 */
	public Timer getTimer() {
		return this.timer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.mobicents.media.server.testsuite.general.rtp.RtpSocketFactory#
	 * setBindAddress(java.lang.String)
	 */
	public void setBindAddress(String bindAddress) throws UnknownHostException {
		this.bindAddress = InetAddress.getByName(bindAddress);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.mobicents.media.server.testsuite.general.rtp.RtpSocketFactory#
	 * setFormatMap(java.util.Map)
	 */
	public void setFormatMap(Vector<Attribute> originalFormatMap) {
		this.formatMap = originalFormatMap;

	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mobicents.media.server.testsuite.general.rtp.RtpSocketFactory#setTimer
	 * (java.util.concurrent.ScheduledExecutorService)
	 */
	public void setTimer(Timer executor) {
		this.timer = executor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mobicents.media.server.testsuite.general.rtp.RtpSocketFactory#stop()
	 */
	public void stop() {
		if(this.transceiver!=null)
		{
			this.transceiver.stop();
			this.transceiver = null;
		}
		log.info("Stopped RTP Factory");

	}

	/* (non-Javadoc)
	 * @see org.mobicents.media.server.testsuite.general.rtp.RtpSocketFactory#getBindAddress()
	 */
	public String getBindAddress() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.mobicents.media.server.testsuite.general.rtp.RtpSocketFactory#start()
	 */
	public void start() throws SocketException, IOException {
		InetSocketAddress address = new InetSocketAddress(bindAddress, localPort);
        
        log.info("Binding RTP transceiver to " + bindAddress + ":" + localPort);
        
        transceiver = new Transceiver(rtpSockets, bindAddress, localPort);
        transceiver.start();
		
	}

	/* (non-Javadoc)
	 * @see org.mobicents.media.server.testsuite.general.rtp.RtpSocketFactory#getPort()
	 */
	public int getPort() {
		return this.localPort;
	}

	/* (non-Javadoc)
	 * @see org.mobicents.media.server.testsuite.general.rtp.RtpSocketFactory#setPort(int)
	 */
	public void setPort(int port) {
		this.localPort = port;
		
	}

	


}
