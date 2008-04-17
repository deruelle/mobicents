package org.mobicents.media.server.impl.common;

import java.io.StreamCorruptedException;



/**
 * This enum represent connection mode:
 * <ul>
 * <li>SEND - only send</li>
 * <li>RECV - only receive</li>
 * <li>SEND_RECV - send and receive</li>
 * <ul>
 * 
 * @author baranowb
 * 
 */
public enum ConnectionMode {
	
	SEND, RECV, SEND_RECV;


	private ConnectionMode() {
		
	}
	
	
	
	
}
