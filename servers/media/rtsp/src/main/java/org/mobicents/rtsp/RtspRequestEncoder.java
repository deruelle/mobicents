package org.mobicents.rtsp;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.handler.codec.http.HttpMessage;
import org.jboss.netty.handler.codec.http.HttpMessageEncoder;
import org.jboss.netty.handler.codec.http.HttpRequest;

public class RtspRequestEncoder extends HttpMessageEncoder {

	public RtspRequestEncoder() {
		super();
	}

	@Override
	protected void encodeInitialLine(ChannelBuffer buf, HttpMessage message)
			throws Exception {
		HttpRequest request = (HttpRequest) message;
		buf.writeBytes(request.getMethod().toString().getBytes("ASCII"));
		buf.writeByte(RtspCodecUtil.SP);
		buf.writeBytes(request.getUri().getBytes("ASCII"));
		buf.writeByte(RtspCodecUtil.SP);
		buf.writeBytes(request.getProtocolVersion().toString()
				.getBytes("ASCII"));
		buf.writeBytes(RtspCodecUtil.CRLF);

	}

}
