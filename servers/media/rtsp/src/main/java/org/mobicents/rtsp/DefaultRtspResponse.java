package org.mobicents.rtsp;

import java.util.List;
import java.util.Set;

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

	private final static String COLON = ":";
	private final static String SP = " ";
	private final static String CRLF = "\r\n";

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

	public String debug() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(this.toString());
		buffer.append(CRLF);
		Set<String> headers = this.getHeaderNames();

		for (String header : headers) {
			List<String> values = this.getHeaders(header);
			for (String value : values) {

				buffer.append(header);
				buffer.append(COLON);
				buffer.append(SP);
				buffer.append(value);
				buffer.append(CRLF);
			}
		}

		return buffer.toString();
	}

}
