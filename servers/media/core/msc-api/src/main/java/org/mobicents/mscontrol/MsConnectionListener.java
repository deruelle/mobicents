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
 * The class interested in receiving the MsConnectionEvent should implement this
 * interface. When ever there is change in state of MsConnection, instance of
 * MsConnectionEvent is fired.
 * 
 * @author Oleg Kulikov
 * @author amit.bhayani
 */
public interface MsConnectionListener extends Serializable {

    /**
     * This method is called when the MsConnectionEventID.CONNECTION_CREATED is
     * fired. Fired when MsConnection.modify(String localDesc, String
     * remoteDesc) is called and creation of
     * <code>org.mobicents.media.server.spi.Connection</code> is successful
     * 
     * @param event
     */
    public void connectionCreated(MsConnectionEvent event);

    public void connectionFailed(MsConnectionEvent event);

    /**
     * This method is called when MsConnectionEventID.CONNECTION_MODIFIED is
     * fired. Fired when MsConnection.modify(String localDesc, String
     * remoteDesc) is called and modification of
     * <code>org.mobicents.media.server.spi.Connection</code> is successful
     * 
     * @param event
     */
    public void connectionHalfOpen(MsConnectionEvent event);

    public void connectionOpen(MsConnectionEvent event);

    /**
     * This method is called when MsConnectionEventID.CONNECTION_DELETED is
     * fired. Fired when MsConnection.release() is called.
     * 
     * @param event
     */
    public void connectionDisconnected(MsConnectionEvent event);
}
