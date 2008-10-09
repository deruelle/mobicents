package org.mobicents.mscontrol;

/**
 * <code>MsConnectionState</code> represents the state of MsConnection.
 * <ul>
 * <li>IDLE : When the MsConnection is created for first time state is
 * <code>IDLE</code></li>
 * <br/>
 * <li>HALF_OPEN : When the MsConnection.modify() is called but without remote
 * SDP and creation of Connection is successful the state is
 * <code>HALF_OPEN</code></li>
 * <br/>
 * 
 * <li>OPEN : When the MsConnection.modify() is called with remote SDP and the
 * Connection is successfully modified the state is <code>OPEN</code></li>
 * <br/>
 * 
 * <li>FAILED : When the creation or modification of MsConnection fails for any
 * reason the state is <code>FAILED</code></li>
 * <br/>
 * 
 * <li>CLOSED : When the MsConnection.release() is called state is
 * <code>CLOSED</code></li>
 * 
 * </ul>
 * 
 * @author amit bhayani
 * 
 */
public enum MsConnectionState {
	IDLE, HALF_OPEN, OPEN, FAILED, CLOSED
}
