package org.mobicents.rtsp;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipelineCoverage;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

/**
 * 
 * @author amit bhayani
 * 
 */
@ChannelPipelineCoverage("one")
public class RtspRequestHandler extends SimpleChannelUpstreamHandler {

	private final RtspServerStackImpl rtspServerStackImpl;

	protected RtspRequestHandler(RtspServerStackImpl rtspServerStackImpl) {
		this.rtspServerStackImpl = rtspServerStackImpl;
	}

	private volatile RtspRequest request;
	private volatile boolean readingChunks;
	private final StringBuilder responseContent = new StringBuilder();

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception {

		RtspRequest rtspRequest = (RtspRequest) e.getMessage();
		rtspServerStackImpl.processRtspRequest(rtspRequest, e.getChannel());

	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
			throws Exception {
		e.getCause().printStackTrace();
		e.getChannel().close();
	}
}
