package org.mobicents.media.server.ctrl.rtsp;

import java.net.URI;
import java.util.Collection;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.mobicents.media.server.spi.Connection;
import org.mobicents.media.server.spi.resource.AudioPlayer;
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
public class PlayAction implements Callable<RtspResponse> {

	private static Logger logger = Logger.getLogger(PlayAction.class);

	private RtspController rtspController = null;
	private RtspRequest request = null;

	public PlayAction(RtspController rtspController, RtspRequest request) {
		this.rtspController = rtspController;
		this.request = request;
	}

	public RtspResponse call() throws Exception {
		RtspResponse response = null;
		String sessionId = this.request.getHeader(RtspHeaders.Names.SESSION);
		if (sessionId != null) {
			Session session = this.rtspController.getSession(sessionId);
			if (session != null) {
				if (session.getState() == SessionState.READY) {
					// As of now we support only READY Session State

					String mediaDir = "file:" + this.rtspController.getMediaDir();
					URI objUri = new URI(this.request.getUri());
					mediaDir += objUri.getPath();

					if (logger.isDebugEnabled()) {
						logger.debug("Final path = " + mediaDir);
					}

					Collection<ConnectionActivity> connActivities = session.getActivities();

					// TODO : As of now its only Audio
					ConnectionActivity connActivity = connActivities.iterator().next();
					Connection connection = connActivity.getMediaConnection();

					AudioPlayer audioPlayer = (AudioPlayer) connection.getEndpoint().getComponent(
							this.rtspController.getAudioPlayer());

					if (audioPlayer == null) {
						// May be its at Connection if its custom Endpoint
						audioPlayer = (AudioPlayer) connection.getComponent(this.rtspController.getAudioPlayer(),
								Connection.CHANNEL_TX);
					}

					audioPlayer.setURL(mediaDir);
					audioPlayer.start();

					response = new DefaultRtspResponse(RtspVersion.RTSP_1_0, RtspResponseStatus.OK);
					response.setHeader(HttpHeaders.Names.SERVER, RtspController.SERVER);
					response.setHeader(RtspHeaders.Names.CSEQ, this.request.getHeader(RtspHeaders.Names.CSEQ));
					response.setHeader(RtspHeaders.Names.SESSION, session.getId());

					session.setState(SessionState.PLAYING);

					return response;
				} else {
					response = new DefaultRtspResponse(RtspVersion.RTSP_1_0, RtspResponseStatus.METHOD_NOT_VALID);
					response.setHeader(HttpHeaders.Names.SERVER, RtspController.SERVER);
					response.setHeader(RtspHeaders.Names.CSEQ, this.request.getHeader(RtspHeaders.Names.CSEQ));
					return response;
				}

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
