package org.mobicents.media.server.ctrl.rtsp;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.mobicents.media.server.spi.NamingService;
import org.mobicents.rtsp.DefaultRtspResponse;
import org.mobicents.rtsp.RtspHeaders;
import org.mobicents.rtsp.RtspListener;
import org.mobicents.rtsp.RtspMethod;
import org.mobicents.rtsp.RtspRequest;
import org.mobicents.rtsp.RtspResponse;
import org.mobicents.rtsp.RtspResponseStatus;
import org.mobicents.rtsp.RtspServerStackImpl;
import org.mobicents.rtsp.RtspVersion;

/**
 * 
 * @author amit bhayani
 * 
 */
public class RtspController implements RtspListener {

	private static final Logger logger = Logger.getLogger(RtspController.class);

	public static final String SERVER = "Mobicents Media Server";

	private RtspServerStackImpl serverStack = null;
	private String bindAddress = "127.0.0.1";
	private int port = 554;
	private NamingService namingService;
	private String mediaDir = null;
	private String audioPlayer = null;

	private ConcurrentHashMap<String, Session> sessions = new ConcurrentHashMap<String, Session>();

	public String getBindAddress() {
		return this.bindAddress;
	}

	public void setBindAddress(String bindAddress) throws UnknownHostException {
		this.bindAddress = bindAddress;
	}

	public int getPort() {
		return this.port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getMediaDir() {
		return mediaDir;
	}

	public void setMediaDir(String mediaDir) {
		this.mediaDir = mediaDir;
	}

	public NamingService getNamingService() {
		return namingService;
	}

	public void setNamingService(NamingService namingService) {
		this.namingService = namingService;
	}

	public void create() {
		logger.info("Starting RTSP Controller module for MMS");
	}

	public void start() throws Exception {
		this.serverStack = new RtspServerStackImpl(this.bindAddress, this.port);
		this.serverStack.setRtspListener(this);
		this.serverStack.start();

		logger
				.info("Started RTSP Controller module for MMS. Bound at IP " + this.bindAddress + " at port "
						+ this.port);
	}

	public void stop() {
		logger.info("Stoping RTSP Controller module for MMS. Listening at IP " + this.bindAddress + " port "
				+ this.port);
		this.serverStack.stop();
	}

	public void destroy() {
		logger.info("Stopped RTSP Controller module for MMS");
	}

	public void onRtspRequest(RtspRequest request, Channel channel) {

		Callable<RtspResponse> action = null;
		RtspResponse response = null;
		try {

			if (request.getMethod().equals(RtspMethod.OPTIONS)) {
				action = new OptionsAction(this, request);
				response = action.call();
			} else if (request.getMethod().equals(RtspMethod.DESCRIBE)) {
				action = new DescribeAction(this, request);
				response = action.call();
			} else if (request.getMethod().equals(RtspMethod.SETUP)) {
				InetSocketAddress inetSocketAddress = (InetSocketAddress) channel.getRemoteAddress();
				String remoteIp = inetSocketAddress.getAddress().getHostAddress();
				action = new SetupAction(this, request, remoteIp);
				response = action.call();
			} else if (request.getMethod().equals(RtspMethod.PLAY)) {
				action = new PlayAction(this, request);
				response = action.call();
			} else if (request.getMethod().equals(RtspMethod.TEARDOWN)) {
				action = new TeardownAction(this, request);
				response = action.call();
			} else {
				response = new DefaultRtspResponse(RtspVersion.RTSP_1_0, RtspResponseStatus.METHOD_NOT_ALLOWED);
				response.setHeader(HttpHeaders.Names.SERVER, RtspController.SERVER);
				response.setHeader(RtspHeaders.Names.CSEQ, request.getHeader(RtspHeaders.Names.CSEQ));
				response.setHeader(RtspHeaders.Names.ALLOW, OptionsAction.OPTIONS);
			}

		} catch (Exception e) {
			logger.error("Unexpected error during processing,Caused by ", e);

			response = new DefaultRtspResponse(RtspVersion.RTSP_1_0, RtspResponseStatus.INTERNAL_SERVER_ERROR);
			response.setHeader(HttpHeaders.Names.SERVER, RtspController.SERVER);
			response.setHeader(RtspHeaders.Names.CSEQ, request.getHeader(RtspHeaders.Names.CSEQ));
		}

		logger.info("Sending Response " + response.toString()+ " For Request "+ request.toString());
		channel.write(response);
	}

	public void onRtspResponse(RtspResponse arg0) {
		// TODO Auto-generated method stub

	}

	public String getAudioPlayer() {
		return audioPlayer;
	}

	public void setAudioPlayer(String audioPlayer) {
		this.audioPlayer = audioPlayer;
	}

	protected Session getSession(String sessionId) {
		return this.sessions.get(sessionId);
	}

	protected void addSession(Session session) {
		this.sessions.put(session.getId(), session);
	}

	protected void removeSession(String sessionId) {
		this.sessions.remove(sessionId);
	}

}
