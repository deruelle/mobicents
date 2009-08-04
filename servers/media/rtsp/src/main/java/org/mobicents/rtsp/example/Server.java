package org.mobicents.rtsp.example;

import java.net.UnknownHostException;

import org.jboss.netty.channel.Channel;
import org.mobicents.rtsp.DefaultRtspResponse;
import org.mobicents.rtsp.RtspListener;
import org.mobicents.rtsp.RtspRequest;
import org.mobicents.rtsp.RtspResponse;
import org.mobicents.rtsp.RtspResponseStatus;
import org.mobicents.rtsp.RtspServerStackImpl;
import org.mobicents.rtsp.RtspVersion;

public class Server {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Server server = new Server();
		server.start();

	}

	public void start() {
		try {
			RtspServerStackImpl serverStack = new RtspServerStackImpl(
					"127.0.0.1", 5050);
			MyRtspListener listener = new MyRtspListener();
			serverStack.setRtspListener(listener);
			serverStack.start();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	private class MyRtspListener implements RtspListener {

		public void onRtspRequest(RtspRequest request, Channel chanel) {
			System.out.println("Received request "+ request);
			for(String headerNa : request.getHeaderNames()){
				System.out.println(headerNa +": "+ request.getHeader(headerNa));
			}
		       RtspResponse response = new DefaultRtspResponse(RtspVersion.RTSP_1_0, RtspResponseStatus.OK);
		       chanel.write(response);
		}

		public void onRtspResponse(RtspResponse response) {
			// TODO Auto-generated method stub

		}

	}

}
