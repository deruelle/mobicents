package org.mobicents.mscontrol;

/**
 * Whenever the state of MsConnection changes MsConnectionEvent is fired and
 * change in state is represented by MsConnectionEventID *
 * <ul>
 * 
 * <li><code>CONNECTION_CREATED</code> Fired as soon as creation of
 * <code>MsConnection</code> is successful. MsConnection is not holding any
 * resources yet </li>
 * <br/>
 * 
 * <li><code>CONNECTION_HALF_OPEN</code> Fired as soon as modification of
 * MsConnection is successful. At this stage the RTP Socket is open in Media
 * Server to receive stream but has no idea of Call-Agent SDP. Application may
 * call MsConnection.modify(localDesc, null) passing null for remote SDP</li>
 * <br/>
 * 
 * <li><code>CONNECTION_OPEN</code> Fired as soon as modification of
 * MsConnection is successful and the SDP passed by Call-Agent is successfully
 * passed to RTP Connection. At this stage there is flow of RTP packets from UA
 * to media server and vice-a-versa. Its possible that application may call
 * MsConnection.modify(localDesc, remoteDesc) passing the remoteDesc (remote
 * SDP)</li>
 * <br/>
 * 
 * <li><code>CONNECTION_DISCONNECTED</code> Fired as soon as MsConnection is
 * released</li>
 * <br/>
 * 
 * <li><code>CONNECTION_FAILED</code> Fired as soon as creation of
 * MsConnection fails for reason's specified in MsConnectionEventCause.
 * Immediately after CONNECTION_FAILED, CONNECTION_DISCONNECTED will be fired
 * giving chance to listener to do the clean-up</li>
 * <br/>
 * 
 * </ul>
 */
public enum MsConnectionEventID {
	/**
	 * 
	 */
	CONNECTION_CREATED, 
        CONNECTION_HALF_OPEN, 
        CONNECTION_OPEN, 
        CONNECTION_FAILED, 
        CONNECTION_DISCONNECTED,
        MODE_SEND_RECV,
        MODE_SEND_ONLY,
        MODE_RECV_ONLY,
}
