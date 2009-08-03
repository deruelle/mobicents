/**
 * Start time:14:52:37 2009-08-03<br>
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
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.spi.SelectorProvider;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

/**
 * Start time:14:52:37 2009-08-03<br>
 * Project: mobicents-media-server-test-suite<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski
 *         </a>
 */
public class Transceiver implements Runnable {
	private DatagramSocket socket;
	protected DatagramChannel channel;
	private Selector readSelector;
	private InetAddress bindAddress;
	private int localPort;
	private int bufferSize = 8196;
	private ByteBuffer readerBuffer = ByteBuffer.allocate(bufferSize);
	private volatile boolean started = false;
	private HashMap<SocketAddress, RtpSocket> rtpSockets;

	public Transceiver(HashMap<SocketAddress, RtpSocket> rtpSockets, InetAddress bindAddress, int localPort) throws SocketException, IOException {
		this.rtpSockets = rtpSockets;
		this.bindAddress = bindAddress;
		this.localPort = localPort;

		channel = DatagramChannel.open();
		channel.configureBlocking(false);

		socket = channel.socket();

		// creating local address and trying to bind socket to this
		// address
		InetSocketAddress address = new InetSocketAddress(bindAddress, localPort);
		socket.bind(address);

		readSelector = SelectorProvider.provider().openSelector();
		channel.register(readSelector, SelectionKey.OP_READ);
	}

	public void start() {
		started = true;
		new Thread(this, "RTP Transceiver").start();
	}

	public void stop() {
		started = false;
		try {
			channel.disconnect();
			channel.close();
			socket.disconnect();
			socket.close();
			readSelector.close();
		} catch (Exception e) {
		}
	}

	private void notifyError(Exception e) {
		Collection<RtpSocket> list = rtpSockets.values();
		for (RtpSocket s : list) {
			s.notify(e);
		}
	}

	public void send(byte[] data, int len, SocketAddress remoteAddress) throws IOException {
		ByteBuffer byteBuffer1 = ByteBuffer.wrap(data);
		int count = 0;

		while (count < len) {
			count += channel.send(byteBuffer1, remoteAddress);
			byteBuffer1.compact();
			byteBuffer1.flip();
		}
	}

	public void run() {
		while (started) {
			try {
				readSelector.select();
				SocketAddress remoteAddress = channel.receive(readerBuffer);

				readerBuffer.flip();
				RtpSocket rtpSocket = rtpSockets.get(remoteAddress);
				
				if (rtpSocket != null) {
					RtpPacket rtpPacket = new RtpPacket(readerBuffer);
					rtpPacket.setTime(new Date(System.currentTimeMillis()));
					rtpSocket.receive(rtpPacket);
				}

				readerBuffer.clear();
			} catch (IOException e) {
				if (started) {
					notifyError(e);
				}
			}
		}
	}
}