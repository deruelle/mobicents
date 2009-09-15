package org.mobicents.rtsp;

import org.jboss.netty.handler.codec.http.HttpMessage;
import org.jboss.netty.handler.codec.http.HttpMessageDecoder;

/**
 * 
 * @author abhayani
 * 
 */
public class RtspResponseDecoder extends HttpMessageDecoder {

	public RtspResponseDecoder() {
		super();
	}

	public RtspResponseDecoder(int maxInitialLineLength, int maxHeaderSize,
			int maxChunkSize) {
		super(maxInitialLineLength, maxHeaderSize, maxChunkSize);
	}

	@Override
	protected HttpMessage createMessage(String[] initialLine) throws Exception {
		return new DefaultRtspResponse(RtspVersion.valueOf(initialLine[0]),
				new RtspResponseStatus(Integer.valueOf(initialLine[1]),
						initialLine[2]));
	}

	@Override
	protected boolean isDecodingRequest() {
		return false;
	}

}
