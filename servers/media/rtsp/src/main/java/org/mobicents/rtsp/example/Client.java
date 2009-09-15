package org.mobicents.rtsp.example;

import java.net.URI;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.mobicents.rtsp.DefaultRtspRequest;
import org.mobicents.rtsp.RtspClientStackImpl;
import org.mobicents.rtsp.RtspListener;
import org.mobicents.rtsp.RtspMethod;
import org.mobicents.rtsp.RtspRequest;
import org.mobicents.rtsp.RtspResponse;
import org.mobicents.rtsp.RtspVersion;

/**
 * 
 * @author amit bhayani
 *
 */
public class Client {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		Client client = new Client();
		client.send();

		
	}

	public void send() throws Exception {

		URI uri = new URI("rtsp://127.0.0.1:5050/hello-world");
		RtspRequest request = new DefaultRtspRequest(RtspVersion.RTSP_1_0,
				RtspMethod.DESCRIBE, uri.toASCIIString());

		request.setHeader(HttpHeaders.Names.HOST, request.getHost());

		RtspClientStackImpl clientStack = new RtspClientStackImpl("127.0.0.1",
				5051);

		MyRtspListener listener = new MyRtspListener();
		clientStack.setRtspListener(listener);

		clientStack.start();

		clientStack.sendRquest(request);
	}

	private class MyRtspListener implements RtspListener {

		public void onRtspRequest(RtspRequest request, Channel chanel) {
			System.out.println("Received request " + request);

		}

		public void onRtspResponse(RtspResponse response) {
			System.out.println("Received RtspResponse " + response);

		}

	}

}
