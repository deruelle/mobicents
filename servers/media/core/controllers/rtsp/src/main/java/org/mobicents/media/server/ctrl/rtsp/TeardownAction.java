package org.mobicents.media.server.ctrl.rtsp;

import java.util.Collection;
import java.util.concurrent.Callable;

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
public class TeardownAction implements Callable<RtspResponse> {

	private RtspController rtspController = null;
	private RtspRequest request = null;

	public TeardownAction(RtspController rtspController, RtspRequest request) {
		this.rtspController = rtspController;
		this.request = request;
	}

	public RtspResponse call() throws Exception {
		RtspResponse response = null;
		String sessionId = this.request.getHeader(RtspHeaders.Names.SESSION);
		if (sessionId != null) {
			Session session = this.rtspController.getSession(sessionId);
			if (session != null) {
				Collection<ConnectionActivity> connActivities = session.getActivities();

				// TODO : As of now its only Audio
				ConnectionActivity connActivity = connActivities.iterator().next();
				connActivity.close();

				response = new DefaultRtspResponse(RtspVersion.RTSP_1_0, RtspResponseStatus.OK);
				response.setHeader(HttpHeaders.Names.SERVER, RtspController.SERVER);
				response.setHeader(RtspHeaders.Names.CSEQ, this.request.getHeader(RtspHeaders.Names.CSEQ));
				response.setHeader(RtspHeaders.Names.SESSION, session.getId());

				session = null;
				
				return response;

			} else {
				response = new DefaultRtspResponse(RtspVersion.RTSP_1_0, RtspResponseStatus.SESSION_NOT_FOUND);
				response.setHeader(HttpHeaders.Names.SERVER, RtspController.SERVER);
				response.setHeader(RtspHeaders.Names.CSEQ, this.request.getHeader(RtspHeaders.Names.CSEQ));
				response.setHeader(RtspHeaders.Names.SESSION, sessionId);
				return response;
			}

		} else {
			response = new DefaultRtspResponse(RtspVersion.RTSP_1_0, RtspResponseStatus.BAD_REQUEST);
			response.setHeader(HttpHeaders.Names.SERVER, RtspController.SERVER);
			response.setHeader(RtspHeaders.Names.CSEQ, this.request.getHeader(RtspHeaders.Names.CSEQ));
			return response;
		}
	}

}
