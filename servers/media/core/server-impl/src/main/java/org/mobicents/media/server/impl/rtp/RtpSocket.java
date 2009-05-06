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
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import net.java.stun4j.StunAddress;
import net.java.stun4j.StunException;
import net.java.stun4j.client.NetworkConfigurationDiscoveryProcess;
import net.java.stun4j.client.StunDiscoveryReport;

import org.apache.log4j.Logger;
import org.mobicents.media.Buffer;
import org.mobicents.media.BufferFactory;
import org.mobicents.media.Format;
import org.mobicents.media.MediaSource;
import org.mobicents.media.server.spi.Timer;

/**
 * 
 * @author Oleg Kulikov
 */
public class RtpSocket implements Runnable {

	protected static int READ_PERIOD = 20;

	private DatagramSocket socket;
	private DatagramChannel channel;

	private ByteBuffer readerBuffer;

	private int bufferSize = 172;
	private byte[] senderBuffer = new byte[bufferSize];

	private int localPort;
	private String localAddress;

	private int remotePort;
	private String remoteAddress;
	private InetSocketAddress remoteInetSocketAddress = null;

	// holder for dynamic payloads.
	private HashMap<Integer, Format> rtpMap = new HashMap<Integer, Format>();
	private HashMap<Integer, Format> rtpMapOriginal = new HashMap<Integer, Format>();

	// packet jitter in milliseconds
	private int jitter = 60;

	// stun support
	private String stunHost;
	private int stunPort;

	// handler for receiver thread
	private ReceiveStream receiveStream;
	private SendStream sendStream;

	// timer to synchronize from
	protected Timer timer;

	private RtpHeader header = new RtpHeader();
	private int payloadType = -1;
	private Format format;
	private BufferFactory bufferFactory = new BufferFactory(10, "RTPSocket");

	protected final static ScheduledExecutorService readerThread = Executors.newSingleThreadScheduledExecutor();
	private ScheduledFuture readerTask;

	private RtpFactory rtpFactory = null;

	// logger instance
	private final static Logger logger = Logger.getLogger(RtpSocket.class);

	/**
	 * Creates a new instance of RtpSocket
	 * 
	 * @param timer
	 *            used to synchronize receiver stream.
	 * @param rtpMap
	 *            RTP payloads list.
	 */
	public RtpSocket(Timer timer, Map<Integer, Format> rtpMap, RtpFactory rtpFactory) {
		this.timer = timer;
		this.readerBuffer = ByteBuffer.allocate(bufferSize);
		READ_PERIOD = timer.getHeartBeat();
		this.jitter = rtpFactory.getJitter();
		
		rtpMapOriginal.putAll(rtpMap);
		this.rtpMap.putAll(rtpMap);

		sendStream = new SendStream(this);
		receiveStream = new ReceiveStream(this, jitter);

		this.rtpFactory = rtpFactory;

		
	}

	/**
	 * (Non Java-doc).
	 * 
	 * @see org.mobicents.media.server.impl.rtp.RtpSocket#init(InetAddress,
	 *      int);
	 */
	public int init(InetAddress localAddress, int lowPort, int highPort) throws SocketException, IOException,
			StunException {
		channel = DatagramChannel.open();
		channel.configureBlocking(false);

		socket = channel.socket();

		boolean bound = false;

		this.localAddress = localAddress.getHostAddress();
		this.localPort = lowPort;

		// looking for first unused port
		while (!bound) {
			try {
				// creating local address and trying to bind socket to this
				// address
				InetSocketAddress bindAddress = new InetSocketAddress(localAddress, localPort);
				socket.bind(bindAddress);

				// if stunHost is assigned then stun ussage is supposed
				// discovering paublic address
				if (stunHost != null) {
					InetSocketAddress publicAddress = getPublicAddress(bindAddress);
					this.localAddress = publicAddress.getHostName();
					this.localPort = publicAddress.getPort();
				}

				bound = true;
				if (logger.isDebugEnabled()) {
					logger.debug("Bound RTP socket to " + bindAddress + ", NAT public address is " + localAddress);
				}
			} catch (SocketException e) {
				// increment port number util upper limit is not reached
				localPort++;
				if (localPort > highPort) {
					throw e;
				}
			}
		}

		return localPort;
	}

	/**
	 * Gets the address of stun server if present.
	 * 
	 * @return the address of stun server or null if not assigned.
	 */
	public String getStunAddress() {
		return stunHost == null ? null : stunPort == 3478 ? stunHost : (stunHost + ":" + stunPort);
	}

	/**
	 * Assigns address of the STUN server.
	 * 
	 * @param address
	 *            the address of the stun server in format host[:port]. if port
	 *            is not set then default port is used.
	 */
	public void setStunAddress(String address) {
		String tokens[] = address.split(":");
		stunHost = tokens[0];
		if (tokens.length == 2) {
			stunPort = Integer.parseInt(tokens[1]);
		}
	}

	/**
	 * Gets address to which this socked is bound.
	 * 
	 * @return either local address to which this socket is bound or public
	 *         address in case of NAT translation.
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

	/**
	 * Gets the jitter for time of packet arrival
	 * 
	 * @return the value of jitter in milliseconds.
	 */
	public int getJitter() {
		return this.jitter;
	}

	/**
	 * Assign new value of packet time arrival jitter.
	 * 
	 * @param jitter
	 *            the value of jitter in milliseconds.
	 */
	public void setJitter(int jitter) {
		this.jitter = jitter;
	}

	/**
	 * Gets receiver stream.
	 * 
	 * @return receiver stream instance.
	 */
	public MediaSource getReceiveStream() {
		return receiveStream;
	}

	protected void startReceiver() {
		readerTask = readerThread.scheduleAtFixedRate(this, 0, READ_PERIOD, TimeUnit.MILLISECONDS);
	}

	protected void stopReceiver() {
		if (readerTask != null && !readerTask.isCancelled()) {
			readerTask.cancel(true);
		}
	}

	public void release() {
		if (receiveStream != null) {
			receiveStream.stop();
		}
		this.resetRtpMap();
		this.rtpFactory.releaseRTPSocket(this);
	}

	/**
	 * Closes socket
	 * 
	 */
	public void close() {
		if (receiveStream != null) {
			receiveStream.stop();
		}

		if (channel != null) {
			try {
				channel.disconnect();
				channel.close();
			} catch (IOException e) {
			}
		}

		if (socket != null) {
			socket.disconnect();
			socket.close();
		}
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
		channel.connect(remoteInetSocketAddress);

		if (logger.isDebugEnabled()) {
			logger.debug("Connect RTP socket[" + localAddress + ":" + localPort + " to " + remoteAddress + ":"
					+ remotePort);
		}

		// senderPacket = new DatagramPacket(senderBuffer, bufferSize, address,
		// port);
	}

	/**
	 * Gets relations between payload number and format used by this socket.
	 * 
	 * @return the map where keya are payload number and object stored under key
	 *         is <code>Format</code> object
	 */
	public HashMap<Integer, Format> getRtpMap() {
		return rtpMap;
	}

	public void setRtpMap(HashMap<Integer, Format> rtpMap) {
		this.rtpMap = rtpMap;
		if (sendStream != null) {
			sendStream.formats = new Format[rtpMap.size()];
			rtpMap.values().toArray(sendStream.formats);
		}
	}

	public void resetRtpMap() {
		this.rtpMap.clear();
		this.rtpMap.putAll(rtpMapOriginal);
	}

	/**
	 * Determines payload type for specified format.
	 * 
	 * @param format
	 *            the object representing format.
	 * @return the integer identifier of the payload or -1 if specified format
	 *         was not previously registered with addFormat().
	 */
	protected int getPayloadType(Format format) {
		Collection<Integer> keys = rtpMap.keySet();
		for (Integer key : keys) {
			Format fmt = rtpMap.get(key);
			if (fmt.equals(format)) {
				return key;
			}
		}
		return -1;
	}

	/**
	 * (Non Java-doc).
	 * 
	 * @see org.mobicents.media.server.impl.rtp.RtpSocket#startSendStream(PushBufferDataSource);
	 */
	public SendStream getSendStream() {
		return sendStream;
	}

	/**
	 * Sends media data to remote peer.
	 * 
	 * This method uses blocking sending to make sure that data is out in time.
	 * 
	 * @param buffer
	 *            the buffer which contains media data
	 * @throws java.io.IOException
	 */
	public void send(Buffer buffer) throws IOException {
		RtpHeader h = (RtpHeader) buffer.getHeader();
		byte[] headerByte = h.toByteArray();

		int len = headerByte.length + buffer.getLength();
		
		senderBuffer = new byte[len];
		
		// combine RTP header and payload
		System.arraycopy(headerByte, 0, senderBuffer, 0, headerByte.length);
		System.arraycopy((byte[]) buffer.getData(), 0, senderBuffer, headerByte.length, buffer.getLength());
		
		// construct datagram packet and send synchronously it out
		// senderPacket.setData(senderBuffer, 0, len);
		try {
			// socket.send(senderPacket);
			ByteBuffer byteBuffer1 = ByteBuffer.wrap(senderBuffer);
			if (channel.isOpen()) {
				channel.send(byteBuffer1, remoteInetSocketAddress);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private InetSocketAddress getPublicAddress(InetSocketAddress localAddress) throws StunException {
		StunAddress local = new StunAddress(localAddress.getAddress(), localAddress.getPort());
		StunAddress stun = new StunAddress(stunHost, stunPort);

		// discovery stun server
		NetworkConfigurationDiscoveryProcess addressDiscovery = new NetworkConfigurationDiscoveryProcess(local, stun);
		addressDiscovery.start();

		// determine public addrees
		StunDiscoveryReport report = addressDiscovery.determineAddress();
		return report.getPublicAddress().getSocketAddress();
	}

	public void run() {
		int count = 0;
		try {
			count = channel.read(readerBuffer);
		} catch (IOException e) {
			logger.error("Network error detected for socket [" + localAddress + ":" + localPort, e);
			return;
		}
		
		readerBuffer.flip();
		// if data arrives then extra
		if (count > 0) {
			byte[] buff = readerBuffer.array();

			// parse RTP header
			header.init(buff);

			// allocating media buffer using factory
			Buffer buffer = bufferFactory.allocate();
			buffer.setLength(count - 12);
			// the length of the payload is total length of the
			// datagram except RTP header which has 12 bytes in length
			System.arraycopy(buff, 12, (byte[]) buffer.getData(), 0, buffer.getLength());
			
			buffer.setmarker(header.getMarker());
			
			// assign format.
			// if payload not changed use the already known format
			if (payloadType != header.getPayloadType()) {
				payloadType = header.getPayloadType();
				format = rtpMap.get(payloadType);
			}

			buffer.setFormat(format);
			receiveStream.push(buffer);			
			
		}

		readerBuffer.clear();
	}

}
