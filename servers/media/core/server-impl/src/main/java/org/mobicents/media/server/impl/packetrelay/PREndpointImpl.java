/*
 * PREndpointImpl.java
 *
 * Mobicents Media Gateway
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

package org.mobicents.media.server.impl.packetrelay;

import org.mobicents.media.server.impl.common.events.EventID;
import org.mobicents.media.server.impl.conference.ConfEndpointImpl;
import org.mobicents.media.server.spi.NotificationListener;
import org.mobicents.media.server.spi.UnknownSignalException;

/**
 *
 * @author Oleg Kulikov
 */
public class PREndpointImpl extends ConfEndpointImpl {
    
    
    /**
     * Creates a new instance of PREndpointImpl
     */
    public PREndpointImpl(String localName) {
        super(localName);
        this.setMaxConnectionsAvailable(2);
    }
    
    
    @Override
    public void play(EventID signalID, String params[], String connectionID,
            NotificationListener listener, boolean keepAlive, boolean beginRecordingImmediately) throws UnknownSignalException {
        throw new UnknownSignalException("Not supported");
    }
    
}
