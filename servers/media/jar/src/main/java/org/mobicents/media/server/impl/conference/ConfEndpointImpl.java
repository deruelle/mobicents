/*
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
package org.mobicents.media.server.impl.conference;

import org.mobicents.media.format.UnsupportedFormatException;

import org.apache.log4j.Logger;
import org.mobicents.media.server.impl.BaseEndpoint;

import org.mobicents.media.server.impl.BaseResourceManager;
import org.mobicents.media.server.impl.common.MediaResourceType;
import org.mobicents.media.server.impl.common.events.EventID;
import org.mobicents.media.server.spi.Endpoint;
import org.mobicents.media.server.spi.MediaSink;
import org.mobicents.media.server.spi.NotificationListener;
import org.mobicents.media.server.spi.ResourceStateListener;
import org.mobicents.media.server.spi.UnknownSignalException;



/**
 *
 * @author Oleg Kulikov
 */
public class ConfEndpointImpl extends BaseEndpoint {

    private transient Logger logger = Logger.getLogger(ConfEndpointImpl.class);
    
    protected ResourceStateListener mixerStateListener;
    protected ResourceStateListener splitterStateListener;
    
    public ConfEndpointImpl(String localName) {
        super(localName);
        this.mixerStateListener = new MixerStateListener();
        this.splitterStateListener = new SplitterStateListener();
    }

    @Override
    public BaseResourceManager initResourceManager() {
        return new ConfResourceManager();
    }

    /**
     * Starts detection DTMF on specified connection with specified parameters.
     * 
     * The DTMF detector is a resource of the endpoint created and initialized 
     * for each connection. The DTMF detection procedure is actualy devided into
     * three steps. On first step inactive DTMF detector is created alongside 
     * with connection using the DTMF format negotiated. The second step is used 
     * to initialize detector with media stream. The last step is used to actual 
     * start media analysis and events generation.
     * 
     * @param connectionID the identifier of the connection
     * @param params parameters for DTMF detector.
     * @param listener the call back inetrface.
     */
    private void detectDTMF(String connectionID, String[] params,
            NotificationListener listener) {
        MediaSink detector = (MediaSink) getResource(
        		MediaResourceType.DTMF_DETECTOR, connectionID);
        if (params != null && params.length > 0 && params[0] != null) {
            ((org.mobicents.media.server.spi.dtmf.DTMF) detector).setDtmfMask(params[0]);
        }
        
        
        LocalSplitter splitter = (LocalSplitter) getResource(MediaResourceType.AUDIO_SINK, connectionID);
        while (splitter == null) {
            synchronized(this) {
                try {
                    wait(100);
                } catch (Exception e) {
                    return;
                }
            }
            splitter = (LocalSplitter) getResource(MediaResourceType.AUDIO_SINK, connectionID);
        }
        
        try {
            detector.prepare(splitter.newBranch("DTMF"));
            detector.start();
            detector.addListener(listener);
        } catch (UnsupportedFormatException e) {
            logger.error("Unexpected format", e);
        }
    }


    public void play(EventID signalID, String[] params, String connectionID, NotificationListener listener, boolean keepAlive) throws UnknownSignalException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void subscribe(EventID eventID, String connectionID, String params[], NotificationListener listener) {
        switch (eventID) {
            case DTMF:
                logger.info("Start DTMF detector for connection: " + connectionID);
                this.detectDTMF(connectionID, params, listener);
                break;
        }
    }
}
