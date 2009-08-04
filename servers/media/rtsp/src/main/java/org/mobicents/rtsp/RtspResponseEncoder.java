package org.mobicents.rtsp;

import java.io.UnsupportedEncodingException;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.handler.codec.http.HttpMessage;
import org.jboss.netty.handler.codec.http.HttpMessageEncoder;
import org.jboss.netty.handler.codec.http.HttpResponse;

/**
 * 
 * @author amit.bhayani
 *
 */
public class RtspResponseEncoder extends HttpMessageEncoder {

	public RtspResponseEncoder() {
		super();
	}

	@Override
	protected void encodeInitialLine(ChannelBuffer buf, HttpMessage message) {
		HttpResponse response = (HttpResponse) message;
		try {
			buf.writeBytes(response.getProtocolVersion().toString().getBytes(
					"ASCII"));
			buf.writeByte(RtspCodecUtil.SP);
			buf.writeBytes(String.valueOf(response.getStatus().getCode())
					.getBytes("ASCII"));
			buf.writeByte(RtspCodecUtil.SP);
			buf.writeBytes(String.valueOf(
					response.getStatus().getReasonPhrase()).getBytes("ASCII"));
			buf.writeBytes(RtspCodecUtil.CRLF);
		} catch (UnsupportedEncodingException e) {
			throw (Error) new Error().initCause(e);
		}
	}

}
