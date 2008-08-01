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

import java.util.Collection;
import java.util.HashMap;
import org.apache.log4j.Logger;
import org.mobicents.media.server.impl.BaseConnection;
import org.mobicents.media.server.impl.BaseEndpoint;
import org.mobicents.media.server.impl.BaseResourceManager;
import org.mobicents.media.server.impl.Signal;
import org.mobicents.media.server.impl.common.events.EventID;
import org.mobicents.media.server.impl.events.ann.AnnouncementPackage;
import org.mobicents.media.server.impl.jmf.proxy.MediaPushProxy;
import org.mobicents.media.server.spi.NotificationListener;
import org.mobicents.media.server.spi.UnknownSignalException;


/**
 * Implements Announcement access point.
 * 
 * @author Oleg Kulikov
 */
public class AnnEndpointImpl extends BaseEndpoint {
    

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
                signal = null;
            }
        } finally {
            super.deleteConnection(connectionID);
        }
    }

    private String getConnectionID() {
        Collection <BaseConnection> connections = getConnections();
        return connections.iterator().next().getId();
    }
    /**
     * (Non Java-doc).
     *
     * @see org.mobicents.server.spi.BaseEndpoint#play(int, String NotificationListener, boolean.
     */
    public void play(EventID signalID, String[] params, String connectionID,
            NotificationListener listener, boolean keepAlive, boolean startRecordingImmediately) throws UnknownSignalException {

        //disable current signal
        if (signal != null) {
            signal.stop();
        }

        if (params == null) {
            return;
        }
        
        if (connectionID == null) {
            connectionID = getConnectionID();
        }
        
        switch (signalID) {
            case PLAY:
                //signal = new AnnouncementSignal(this, listener, params);
                //signal.start();
                AnnouncementPackage pkg = new AnnouncementPackage(this);
                HashMap opts = new HashMap();
                opts.put("announcement.url", params[0]);
                signal = pkg.play(signalID, opts, connectionID, listener);
                logger.info("play announcement [url=" + params[0] + "]");
                break;
            default:
                throw new UnknownSignalException("Signal is unknown: " + signalID);
        }
    }

}
