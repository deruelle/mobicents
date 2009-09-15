package org.mobicents.media.server.ctrl.rtsp;

import java.io.File;
import java.net.URI;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.mobicents.rtsp.DefaultRtspResponse;
import org.mobicents.rtsp.RtspHeaders;
import org.mobicents.rtsp.RtspRequest;
import org.mobicents.rtsp.RtspResponse;
import org.mobicents.rtsp.RtspResponseStatus;
import org.mobicents.rtsp.RtspVersion;

/**
 * 
 * @author amit bhayani
 * 
 */
public class DescribeAction implements Callable<RtspResponse> {

	private static Logger logger = Logger.getLogger(DescribeAction.class);

	private RtspController rtspController = null;
	private RtspRequest request = null;
	private final SdpUtils sdpUtils;

	public DescribeAction(RtspController rtspController, RtspRequest request) {
		this.rtspController = rtspController;
		this.request = request;
		this.sdpUtils = new SdpUtils();
	}

	public RtspResponse call() throws Exception {
		RtspResponse response = null;

		String mediaDir = this.rtspController.getMediaDir();
		URI objUri = new URI(this.request.getUri());
		mediaDir += objUri.getPath();

		if (logger.isDebugEnabled()) {
			logger.debug("Final path = " + mediaDir);
		}
		File f = new File(mediaDir);
		if (f.isFile() && f.exists()) {

			String sdp = this.sdpUtils.getSdp(f, this.rtspController.getBindAddress(), 9000, this.request.getUri());

			response = new DefaultRtspResponse(RtspVersion.RTSP_1_0, RtspResponseStatus.OK);
			response.setHeader(HttpHeaders.Names.SERVER, RtspController.SERVER);
			response.setHeader(RtspHeaders.Names.CSEQ, this.request.getHeader(RtspHeaders.Names.CSEQ));
			response.setHeader(HttpHeaders.Names.CONTENT_TYPE, "application/sdp");
			response.setHeader(HttpHeaders.Names.CONTENT_LENGTH, String.valueOf(sdp.length()));
			response.setContent(ChannelBuffers.copiedBuffer(sdp, "UTF-8"));

		} else {
			response = new DefaultRtspResponse(RtspVersion.RTSP_1_0, RtspResponseStatus.NOT_FOUND);
			response.setHeader(HttpHeaders.Names.SERVER, RtspController.SERVER);
			response.setHeader(RtspHeaders.Names.CSEQ, this.request.getHeader(RtspHeaders.Names.CSEQ));
		}

		return response;
	}

}
