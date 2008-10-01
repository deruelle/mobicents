package org.mobicents.media.server.spi;



/**
 * This enum represent connection mode:
 * <ul>
 * <li>SEND_ONLY - only send</li>
 * <li>RECV_ONLY - only receive</li>
 * <li>SEND_RECV - send and receive</li>
 * <ul>
 * 
 * @author baranowb
 * 
 */
public enum ConnectionMode {
	
	SEND_ONLY, RECV_ONLY, SEND_RECV;


	private ConnectionMode() {
		
	}
	
	
	
	
}
