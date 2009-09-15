package org.mobicents.rtsp;


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
