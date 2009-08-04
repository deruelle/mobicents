package org.mobicents.rtsp;

import org.jboss.netty.channel.Channel;

/**
 * 
 * @author amit.bhayani
 *
 */
public interface RtspListener {
	public void onRtspRequest(RtspRequest request, Channel chanel);

	public void onRtspResponse(RtspResponse response);
}
