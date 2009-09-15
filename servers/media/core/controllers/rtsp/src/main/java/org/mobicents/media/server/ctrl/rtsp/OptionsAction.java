package org.mobicents.media.server.ctrl.rtsp;

import java.util.concurrent.Callable;

import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.mobicents.rtsp.DefaultRtspResponse;
import org.mobicents.rtsp.RtspHeaders;
import org.mobicents.rtsp.RtspMethod;
import org.mobicents.rtsp.RtspRequest;
import org.mobicents.rtsp.RtspResponse;
import org.mobicents.rtsp.RtspResponseStatus;
import org.mobicents.rtsp.RtspVersion;

/**
 * 
 * @author amit bhayani
 *
 */
public class OptionsAction implements Callable<RtspResponse> {

	private RtspController rtspController = null;
	private RtspRequest request = null;
	public final static String OPTIONS = RtspMethod.DESCRIBE.getName() + ", " + RtspMethod.SETUP.getName() + ", "
			+ RtspMethod.TEARDOWN.getName() + ", " + RtspMethod.PLAY.getName();

	public OptionsAction(RtspController rtspController, RtspRequest request) {
		this.rtspController = rtspController;
		this.request = request;
	}

	public RtspResponse call() throws Exception {
		RtspResponse response = new DefaultRtspResponse(RtspVersion.RTSP_1_0, RtspResponseStatus.OK);
		response.setHeader(HttpHeaders.Names.SERVER, RtspController.SERVER);
		response.setHeader(RtspHeaders.Names.CSEQ, this.request.getHeader(RtspHeaders.Names.CSEQ));
		response.setHeader(RtspHeaders.Names.PUBLIC, OPTIONS);
		return response;
	}
}
