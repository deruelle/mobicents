package org.mobicents.rtsp;

import org.jboss.netty.handler.codec.http.HttpMessage;
import org.jboss.netty.handler.codec.http.HttpMessageDecoder;

/**
 * 
 * @author amit.bhayani
 *
 */
public class RtspRequestDecoder extends HttpMessageDecoder {

	public RtspRequestDecoder() {
		super();
	}

	public RtspRequestDecoder(int maxInitialLineLength, int maxHeaderSize,
			int maxChunkSize) {
		super(maxInitialLineLength, maxHeaderSize, maxChunkSize);
	}

	@Override
	protected HttpMessage createMessage(String[] initialLine) throws Exception {
		return new DefaultRtspRequest(RtspVersion.valueOf(initialLine[2]),
				RtspMethod.valueOf(initialLine[0]), initialLine[1]);
	}

	@Override
	protected boolean isDecodingRequest() {
		return true;
	}

}
