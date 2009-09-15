package org.mobicents.rtsp;

import org.jboss.netty.handler.codec.http.HttpResponse;

/**
 * 
 * @author amit bhayani
 *
 */
public interface RtspResponse extends HttpResponse {

	public String debug();
}
