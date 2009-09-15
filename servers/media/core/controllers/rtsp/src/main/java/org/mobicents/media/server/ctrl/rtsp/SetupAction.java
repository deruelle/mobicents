package org.mobicents.media.server.ctrl.rtsp;

import java.io.File;
import java.net.URI;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;
import org.jboss.netty.handler.codec.http.HttpHeaders;

import org.mobicents.media.server.spi.Connection;
import org.mobicents.media.server.spi.ConnectionMode;
import org.mobicents.media.server.spi.Endpoint;
import org.mobicents.media.server.spi.ResourceUnavailableException;
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
public class SetupAction implements Callable<RtspResponse> {

	// TODO : Multicast not taken care
	// TODO : Only UDP is supported, check that no TCP in Transport

	private static final Logger logger = Logger.getLogger(SetupAction.class);
	private final String ENDPOINT_NAME = "/mobicents/media/aap/$";

	private static final ConnectionMode mode = ConnectionMode.SEND_ONLY;

	private final RtspController rtspController;
	private final RtspRequest request;

	private final SdpUtils sdpUtils;

	private final String remoteIp;

	private String clientPort = null;

	public SetupAction(RtspController rtspController, RtspRequest request, String remoteIp) {
		this.rtspController = rtspController;
		this.request = request;

		this.sdpUtils = new SdpUtils();
		this.remoteIp = remoteIp;
	}

	public RtspResponse call() throws Exception {
		String sessionId = this.request.getHeader(RtspHeaders.Names.SESSION);
		Session session = null;
		Endpoint endpoint = null;
		RtspResponse response = null;

		if (logger.isDebugEnabled()) {
			logger.debug("SETUP Request to Play for Session Id = " + sessionId + " Request = " + this.request);
		}

		if (sessionId != null) {
			// Existing session
			session = this.rtspController.getSession(sessionId);

			if (session != null) {
				// Check state to be != PLAYING or RECORDING
				if (session.getState() == SessionState.PLAYING || session.getState() == SessionState.RECORDING) {

					// We don't support changing the Transport while state is PLAYING or RECORDING
					response = new DefaultRtspResponse(RtspVersion.RTSP_1_0, RtspResponseStatus.METHOD_NOT_VALID);
					response.setHeader(HttpHeaders.Names.SERVER, RtspController.SERVER);
					response.setHeader(RtspHeaders.Names.CSEQ, this.request.getHeader(RtspHeaders.Names.CSEQ));
					return response;
				} else {
					// TODO : What here?
				}
			} else {
				response = new DefaultRtspResponse(RtspVersion.RTSP_1_0, RtspResponseStatus.SESSION_NOT_FOUND);
				response.setHeader(HttpHeaders.Names.SERVER, RtspController.SERVER);
				response.setHeader(RtspHeaders.Names.CSEQ, this.request.getHeader(RtspHeaders.Names.CSEQ));
				return response;
			}

		} else {
			// new session
			session = new Session(this.rtspController);
			try {
				endpoint = rtspController.getNamingService().lookup(ENDPOINT_NAME, false);
			} catch (ResourceUnavailableException e) {
				logger.warn("There is no free endpoint: " + ENDPOINT_NAME);

				response = new DefaultRtspResponse(RtspVersion.RTSP_1_0, RtspResponseStatus.SERVICE_UNAVAILABLE);
				response.setHeader(HttpHeaders.Names.SERVER, RtspController.SERVER);
				response.setHeader(RtspHeaders.Names.CSEQ, this.request.getHeader(RtspHeaders.Names.CSEQ));

				return response;

			}

			Connection connection = null;
			try {
				connection = endpoint.createConnection(mode);
				if (logger.isDebugEnabled()) {
					logger.debug("SessionId=" + session.getId() + ", Endpoint: " + endpoint.getLocalName()
							+ ", Created connection ");
				}
			} catch (Exception e) {
				logger.error(e);
				response = new DefaultRtspResponse(RtspVersion.RTSP_1_0, RtspResponseStatus.SERVICE_UNAVAILABLE);
				response.setHeader(HttpHeaders.Names.SERVER, RtspController.SERVER);
				response.setHeader(RtspHeaders.Names.CSEQ, this.request.getHeader(RtspHeaders.Names.CSEQ));

				return response;
			}

			int port = -1;
			String transport = this.request.getHeader(RtspHeaders.Names.TRANSPORT);
			String[] transParameters = transport.split(";");
			for (String s : transParameters) {
				if (s.contains("client_port")) {
					this.clientPort = s;
					String[] values = s.split("=");
					values = values[1].split("-");
					port = Integer.parseInt(values[0]);
					break;
				}
			}

			String mediaDir = this.rtspController.getMediaDir();
			String sdp = null;
			URI objUri = new URI(this.request.getUri());
			mediaDir += objUri.getPath();

			if (logger.isDebugEnabled()) {
				logger.debug("Final path = " + mediaDir);
			}
			File f = new File(mediaDir);
			if (f.isFile() && f.exists()) {
				sdp = this.sdpUtils.getSdp(f, this.remoteIp, port, this.request.getUri());
			} else {
				response = new DefaultRtspResponse(RtspVersion.RTSP_1_0, RtspResponseStatus.NOT_FOUND);
				response.setHeader(HttpHeaders.Names.SERVER, RtspController.SERVER);
				response.setHeader(RtspHeaders.Names.CSEQ, this.request.getHeader(RtspHeaders.Names.CSEQ));
				return response;
			}

			try {
				connection.setRemoteDescriptor(sdp);
			} catch (Exception e) {
				logger.error(e);
				response = new DefaultRtspResponse(RtspVersion.RTSP_1_0, RtspResponseStatus.SERVICE_UNAVAILABLE);
				response.setHeader(HttpHeaders.Names.SERVER, RtspController.SERVER);
				response.setHeader(RtspHeaders.Names.CSEQ, this.request.getHeader(RtspHeaders.Names.CSEQ));

				return response;
			}

			String localDesc = connection.getLocalDescriptor();
			int serverPort = sdpUtils.getAudioPort(localDesc);

			transport = "RTP/AVP/UDP;unicast;" + this.clientPort + ";server_port=" + serverPort + "-" + serverPort;

			response = new DefaultRtspResponse(RtspVersion.RTSP_1_0, RtspResponseStatus.OK);
			response.setHeader(HttpHeaders.Names.SERVER, RtspController.SERVER);
			response.setHeader(RtspHeaders.Names.CSEQ, this.request.getHeader(RtspHeaders.Names.CSEQ));
			response.setHeader(RtspHeaders.Names.SESSION, session.getId());
			response.setHeader(RtspHeaders.Names.TRANSPORT, transport);

			session.setState(SessionState.READY);
			ConnectionActivity connectionActivity = session.addConnection(connection);

			if (logger.isDebugEnabled()) {
				logger.debug("Created connection activity=" + connectionActivity.getID() + ", origin connection ID ="
						+ connection.getId());
			}

			this.rtspController.addSession(session);
			if (logger.isDebugEnabled()) {
				logger.debug("Save reference to the callID=" + session.getId());
			}

		}
		return response;
	}
}
