package org.mobicents.rtsp;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

/**
 * 
 * @author amit.bhayani
 * 
 */
public class RtspServerStackImpl implements RtspStack {

	private static Logger logger = Logger.getLogger(RtspServerStackImpl.class);

	private final String address;
	private final int port;
	private final InetAddress inetAddress;
	private Channel channel = null;
	private ServerBootstrap bootstrap = null;

	private RtspListener listener = null;

	public RtspServerStackImpl(String address, int port)
			throws UnknownHostException {
		this.address = address;
		this.port = port;
		inetAddress = InetAddress.getByName(this.address);
	}

	public String getAddress() {
		return this.address;
	}

	public int getPort() {
		return this.port;
	}

	public void start() {

		InetSocketAddress bindAddress = new InetSocketAddress(this.inetAddress,
				this.port);

		bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(
				Executors.newCachedThreadPool(), Executors
						.newCachedThreadPool()));

		// Set up the event pipeline factory.
		bootstrap.setPipelineFactory(new RtspServerPipelineFactory(this));

		// Bind and start to accept incoming connections.
		channel = bootstrap.bind(bindAddress);

		logger.info("Mobicents RTSP Server started and bound to "
				+ bindAddress.toString());

	}

	public void stop() {
		ChannelFuture cf = channel.getCloseFuture();
		cf.addListener(new ServerChannelFutureListener());

		channel.close();
		cf.awaitUninterruptibly();
		bootstrap.getFactory().releaseExternalResources();

	}

	public void setRtspListener(RtspListener listener) {
		this.listener = listener;

	}

	protected void processRtspResponse(RtspResponse rtspResponse) {
		synchronized (this.listener) {
			listener.onRtspResponse(rtspResponse);
		}
	}

	protected void processRtspRequest(RtspRequest rtspRequest, Channel channel) {
		synchronized (this.listener) {
			listener.onRtspRequest(rtspRequest, channel);
		}
	}

	private class ServerChannelFutureListener implements ChannelFutureListener {

		public void operationComplete(ChannelFuture arg0) throws Exception {
			logger.info("Mobicents RTSP Server Stop complete");
		}

	}

	public void sendRquest(RtspRequest rtspRequest) {
		throw new UnsupportedOperationException("Not Supported yet");
	}

}
