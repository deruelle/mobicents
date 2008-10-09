package org.mobicents.mscontrol;

/**
 * <code>MsLinkState</code> represents the state of MsLink.
 * <ul>
 * <li>IDLE : When the MsLink is created</li>
 * <br/>
 * <li>CONNECTED : When the <code>MsLink.join(endpointA, endpointB)</code> is
 * called and creating of link is successful the state is set to <code>CONNECTED</code></li>
 * <br/>
 * 
 * <li>FAILED : When the <code>MsLink.join(endpointA, endpointB)</code> is
 * called and creation of link fails the state is set to <code>FAILED</code></li>
 * <br/>
 * 
 * <li>DISCONNECTED : When the <code>MsLink.release()</code> is called state
 * is <code>DISCONNECTED</code></li>
 * 
 * </ul>
 * 
 * @author amit bhayani
 * 
 */
public enum MsLinkState {
	IDLE, CONNECTED, FAILED, DISCONNECTED;
}
