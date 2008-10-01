package org.mobicents.mscontrol;

/**
 * Whenever the state of MsConnection changes MsConnectionEvent is fired and
 * change in state is represented by MsConnectionEventID
 */
public enum MsConnectionEventID {
	/**
	 * <ul>
	 * <li><code>CONNECTION_INITIALIZED</code> As soon as new MsConnection is
	 * created CONNECTION_INITIALIZED is fired</li>
	 * 
	 * <li><code>CONNECTION_CREATED</code> Fired as soon as creation of
	 * org.mobicents.media.server.spi.Connection (which represents actual
	 * connection) is successful </li>
	 * 
	 * <li><code>CONNECTION_MODIFIED</code> Fired as soon as modification of
	 * MsConnection is successful</li>
	 * 
	 * <li><code>CONNECTION_DELETED</code> Fired as soon as MsConnection is
	 * successfully released</li>
	 * 
	 * <li><code>CONNECTION_DELETED</code> Fired as soon as MsConnection is
	 * successfully released</li>
	 * 
	 * <li><code>TX_FAILED</code> Fired as soon as creation of MsConnection
	 * fails for reason's specified in MsConnectionEventCause. This is
	 * deprecated and will be replaced by CONNECTION_FAILED </li>
	 * 
	 * </ul>
	 */
        CONNECTION_CREATED, 
        CONNECTION_HALF_OPEN, 
        CONNECTION_OPEN, 
        CONNECTION_FAILED, 
        CONNECTION_DISCONNECTED, 

}
