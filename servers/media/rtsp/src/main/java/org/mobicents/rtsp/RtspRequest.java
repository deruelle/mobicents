package org.mobicents.rtsp;

import org.jboss.netty.handler.codec.http.HttpRequest;

public interface RtspRequest extends HttpRequest {
	public String getHost();
	
	public int getPort();



}
