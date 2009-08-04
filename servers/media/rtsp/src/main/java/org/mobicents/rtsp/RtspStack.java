package org.mobicents.rtsp;

import java.net.URI;

/**
 * 
 * @author amit.bhayani
 *
 */
public interface RtspStack {

	public int getPort();

	public String getAddress();

	public void start();

	public void stop();
	
	public void setRtspListener(RtspListener listener);
	
	public void sendRquest(RtspRequest rtspRequest);

}
