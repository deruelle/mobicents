package org.mobicents.rtsp;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipelineCoverage;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

/**
 * 
 * @author amit.bhayani
 * 
 */
@ChannelPipelineCoverage("one")
public class RtspResponseHandler extends SimpleChannelUpstreamHandler {

	private final RtspClientStackImpl rtspClientStackImpl;

	public RtspResponseHandler(RtspClientStackImpl rtspClientStackImpl) {
		this.rtspClientStackImpl = rtspClientStackImpl;
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception {
		
		RtspResponse rtspResponse = (RtspResponse)e.getMessage();
		rtspClientStackImpl.processRtspResponse(rtspResponse);

	}

}
