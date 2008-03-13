/*
 * AnnEndpointImpl.java
 *
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
package org.mobicents.media.server.impl.ann;

import org.apache.log4j.Logger;

import org.mobicents.media.server.impl.BaseEndpoint;
import org.mobicents.media.server.impl.BaseResourceManager;
import org.mobicents.media.server.impl.Signal;
import org.mobicents.media.server.impl.jmf.proxy.MediaPushProxy;

import org.mobicents.media.server.spi.NotificationListener;
import org.mobicents.media.server.spi.UnknownSignalException;
import org.mobicents.media.server.spi.events.Announcement;

/**
 * Implements Announcement access point.
 * 
 * @author Oleg Kulikov
 */
public class AnnEndpointImpl extends BaseEndpoint {
    
    protected transient MediaPushProxy mediaProxy;
    protected transient Logger logger;
    private transient Signal signal;

    /**
     * Creates a new instance of AnnEndpointImpl
     * 
     * @param localName the local name of the endpoint.
     */
    public AnnEndpointImpl(String localName) {
        super(localName);
        this.setMaxConnectionsAvailable(1);
        logger = Logger.getLogger(AnnEndpointImpl.class);
    }

    /**
     * Provides initialization of endpoint specific resource manager.
     * 
     * @return the endpoint specific resource manager.
     */
    @Override
    public BaseResourceManager initResourceManager() {
        return new AnnResourceManager();
    }
    
    /**
     * (Non Java-doc).
     *
     * @see org.mobicents.media.server.spi.Endpoint#deleteConnection();
     */
    @Override
    public synchronized void deleteConnection(String connectionID) {
        try {
            //disbale current signal if enabled
            if (signal != null) {
                signal.stop();
            }
        } finally {
            super.deleteConnection(connectionID);
        }
    }

    /**
     * (Non Java-doc).
     *
     * @see org.mobicents.server.spi.BaseEndpoint#play(int, String NotificationListener, boolean.
     */
    public void play(int signalID, String[] params, String connectionID,
            NotificationListener listener, boolean keepAlive) throws UnknownSignalException {

        //disable current signal
        if (signal != null) {
            signal.stop();
        }

        if (params == null) {
            return;
        }
        
        switch (signalID) {
            case Announcement.PLAY:
                signal = new AnnouncementSignal(this, listener, params);
                signal.start();
                logger.info("play announcement [url=" + params[0] + "]");
                break;
            default:
                throw new UnknownSignalException("Signal is unknown: " + signalID);
        }
    }
}
