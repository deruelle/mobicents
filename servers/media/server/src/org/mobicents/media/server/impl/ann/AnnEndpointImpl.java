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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Timer;
import javax.media.format.UnsupportedFormatException;
import javax.media.protocol.PushBufferStream;
import org.apache.log4j.Logger;

import org.mobicents.media.server.impl.BaseEndpoint;
import org.mobicents.media.server.impl.BaseConnection;
import org.mobicents.media.server.impl.Signal;
import org.mobicents.media.server.impl.jmf.proxy.MediaPushProxy;

import org.mobicents.media.server.spi.NotificationListener;
import org.mobicents.media.server.spi.UnknownSignalException;
import org.mobicents.media.server.spi.events.Announcement;

/**
 *
 * @author Oleg Kulikov
 */
public class AnnEndpointImpl extends BaseEndpoint {
    public final static Timer TIMER = new Timer();
    public final static int PACKETIZATION_PERIOD = 20;
    
    protected transient MediaPushProxy mediaProxy;
    protected transient Logger logger;
    private transient Signal signal;

    /**
     * Creates a new instance of AnnEndpointImpl
     */
    public AnnEndpointImpl(String localName) {
        super(localName);
        this.setMaxConnectionsAvailable(1);
        logger = Logger.getLogger(AnnEndpointImpl.class);
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
            
            //terminate push proxy 
            mediaProxy.setInputStream(null);
            
            //clen timer
            TIMER.purge();
        } catch (UnsupportedFormatException e) {
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

    /**
     * (Non Java-doc).
     *
     * @see org.mobicents.server.spi.BaseEndpoint#addAudioStream(PushBufferStream, String)
     */
    public void addAudioStream(PushBufferStream stream, String connectionID) {
    }

    /**
     * (Non Java-doc).
     *
     * @see org.mobicents.server.spi.BaseEndpoint#getAudioStream(BaseConnection)
     */
    public Collection <PushBufferStream> getAudioStreams(BaseConnection connection) {
        List list = new ArrayList();

        mediaProxy = new MediaPushProxy(PACKETIZATION_PERIOD, connection.getAudioFormat());
        mediaProxy.start();
        
        list.add(mediaProxy);
        return list;
    }
}
