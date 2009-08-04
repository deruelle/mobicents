package org.mobicents.rtsp;

import java.net.URI;
import java.net.URISyntaxException;

import org.jboss.netty.handler.codec.http.DefaultHttpMessage;
import org.jboss.netty.handler.codec.http.HttpMethod;

/**
 * 
 * @author amit.bhayani
 * 
 */
public class DefaultRtspRequest extends DefaultHttpMessage implements
		RtspRequest {

	private final RtspMethod method;
	private final String uri;
	private final String host;
	private final int port;

	public DefaultRtspRequest(RtspVersion rtspVersion, RtspMethod method,
			String uri) throws URISyntaxException {
		super(rtspVersion);
		if (method == null) {
			throw new NullPointerException("method");
		}
		if (uri == null) {
			throw new NullPointerException("uri");
		}

		URI objUri = new URI(uri);
		String scheme = objUri.getScheme() == null ? "rtsp" : objUri
				.getScheme();
		host = objUri.getHost() == null ? "localhost" : objUri.getHost();
		port = objUri.getPort() == -1 ? 5050 : objUri.getPort();

		if (!scheme.equalsIgnoreCase("rtsp")) {
			throw new UnsupportedOperationException("Only rtsp is supported");
		}

		this.method = method;
		this.uri = uri;
	}

	public HttpMethod getMethod() {
		return method;
	}

	public String getUri() {
		return uri;
	}

	@Override
	public String toString() {
		return getMethod().toString() + ' ' + getUri() + ' '
				+ getProtocolVersion().getText();
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

}
