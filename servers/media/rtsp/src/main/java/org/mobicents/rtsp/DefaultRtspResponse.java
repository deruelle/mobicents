package org.mobicents.rtsp;

import org.jboss.netty.handler.codec.http.DefaultHttpMessage;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;

/**
 * 
 * @author amit.bhayani
 * 
 */
public class DefaultRtspResponse extends DefaultHttpMessage implements
		RtspResponse {

	private final HttpResponseStatus status;

	public DefaultRtspResponse(RtspVersion version, HttpResponseStatus status) {
		super(version);
		if (status == null) {
			throw new NullPointerException("status");
		}
		this.status = status;
	}

	public HttpResponseStatus getStatus() {
		return this.status;
	}

	@Override
	public String toString() {
		return getProtocolVersion().getText() + ' ' + getStatus().toString();
	}

}
