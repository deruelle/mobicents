/*
 * MsConnectionListener.java
 *
 * The Simple Media Server Control API
 *
 * The source code contained in this file is in in the public domain.
 * It can be used in any project or product without prior permission,
 * license or royalty payments. There is  NO WARRANTY OF ANY KIND,
 * EXPRESS, IMPLIED OR STATUTORY, INCLUDING, WITHOUT LIMITATION,
 * THE IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE,
 * AND DATA ACCURACY.  We do not warrant or make any representations
 * regarding the use of the software or the  results thereof, including
 * but not limited to the correctness, accuracy, reliability or
 * usefulness of the software.
 */
package org.mobicents.mscontrol;

import java.io.Serializable;

/**
 * The class interested in receiving the {@link MsConnectionEvent} should
 * implement this interface. When ever there is change in state of
 * {@link MsConnection}, instance of <code>MsConnectionEvent</code> is fired.
 * 
 * @author Oleg Kulikov
 * @author amit.bhayani
 */
public interface MsConnectionListener extends Serializable {

	/**
	 * This method is called when the
	 * <code>MsConnectionEventID.CONNECTION_CREATED</code> is fired. At this
	 * stage the state of <code>MsConnection</code> is <code>IDLE</code>.
	 * Look at <code>CONNECTION_CREATED</code> of {@link MsConnectionEventID}
	 * 
	 * @param event
	 */
	public void connectionCreated(MsConnectionEvent event);

	/**
	 * This method is called when the
	 * <code>MsConnectionEventID.CONNECTION_FAILED</code> is fired. At this
	 * stage the state of <code>MsConnection</code> is <code>FAILED</code>Look
	 * at <code>CONNECTION_FAILED</code> of {@link MsConnectionEventID}
	 * 
	 * @param event
	 */
	public void connectionFailed(MsConnectionEvent event);

	/**
	 * This method is called when
	 * <code>MsConnectionEventID.CONNECTION_HALF_OPEN</code> is fired. At this
	 * stage the state of <code>MsConnection</code> is <code>HALF_OPEN</code>Look
	 * at <code>CONNECTION_HALF_OPEN</code> of {@link MsConnectionEventID}
	 * 
	 * @param event
	 */
	public void connectionHalfOpen(MsConnectionEvent event);

	/**
	 * This method is called when
	 * <code>MsConnectionEventID.CONNECTION_OPEN</code> is fired. At this
	 * stage the state of <code>MsConnection</code> is <code>OPEN</code>Look
	 * at <code>CONNECTION_OPEN</code> of {@link MsConnectionEventID}
	 * 
	 * @param event
	 */
	public void connectionOpen(MsConnectionEvent event);

	/**
	 * This method is called when
	 * <code>MsConnectionEventID.CONNECTION_DISCONNECTED</code> is fired. At
	 * this stage the state of <code>MsConnection</code> is
	 * <code>CLOSED</code>. Look at <code>CONNECTION_DISCONNECTED</code> of
	 * {@link MsConnectionEventID}
	 * 
	 * @param event
	 */
	public void connectionDisconnected(MsConnectionEvent event);
        
        public void connectionModeRecvOnly(MsConnectionEvent event);
        public void connectionModeSendOnly(MsConnectionEvent event);
        public void connectionModeSendRecv(MsConnectionEvent event);
}
