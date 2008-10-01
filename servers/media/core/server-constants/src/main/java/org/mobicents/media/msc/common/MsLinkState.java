package org.mobicents.media.msc.common;

/**
 * <code>MsLinkState</code> represents the state of MsLink.
 * <ul>
 * <li>IDLE : When the MsLink is created</li>
 * <li>JOINED : When the MsLink.join() is called and creating of link is
 * successful the state is set to JOINED</li>
 * 
 * <li>FAILED : When the MsLink.join() is called and creation of link fails the
 * state is set to FAILED</li>
 * 
 * <li>INVALID : When the MsLink.release() is called state is INVALID</li>
 * 
 * </ul>
 * 
 * @author amit bhayani
 * 
 */
public enum MsLinkState {
	IDLE, 
        CONNECTED, 
        FAILED, 
        DISCONNECTED;
}
