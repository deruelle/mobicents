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
package org.mobicents.media.server.impl.ivr;

import java.util.Collection;
import java.util.HashMap;
import org.mobicents.media.format.AudioFormat;
import org.mobicents.media.protocol.FileTypeDescriptor;
import org.mobicents.media.server.impl.ann.AnnEndpointImpl;
import org.mobicents.media.server.impl.common.events.EventID;

import org.apache.log4j.Logger;
import org.mobicents.media.server.impl.BaseConnection;
import org.mobicents.media.server.impl.BaseResourceManager;
import org.mobicents.media.server.impl.Signal;
import org.mobicents.media.server.impl.events.ann.AnnouncementPackage;
import org.mobicents.media.server.impl.events.au.AdvancedAudioPackage;
import org.mobicents.media.server.impl.events.dtmf.DTMFPackage;
import org.mobicents.media.server.impl.events.test.TestPackage;
import org.mobicents.media.server.spi.NotificationListener;
import org.mobicents.media.server.spi.ResourceStateListener;
import org.mobicents.media.server.spi.UnknownSignalException;

/**
 *
 * @author Oleg Kulikov
 */
public class IVREndpointImpl extends AnnEndpointImpl {

    protected AudioFormat audioFormat = new AudioFormat(AudioFormat.LINEAR, 8000, 16, 1);
    protected String mediaType = FileTypeDescriptor.WAVE;
    private Signal signal;
    protected ResourceStateListener splitterStateListener;
    protected String recordDir = null;
    private DTMFPackage dtmfPackage;
    private TestPackage testPackage;
    private transient Logger logger = Logger.getLogger(IVREndpointImpl.class);

    /** Creates a new instance of IVREndpointImpl */
    public IVREndpointImpl(String localName) {
        super(localName);
        this.splitterStateListener = new SplitterStateListener();
    }

    @Override
    public BaseResourceManager initResourceManager() {
        return new IVRResourceManager();
    }

    public void setRecordDir(String recordDir) {
        this.recordDir = recordDir;
    }

    public String getRecordDir() {
        return this.recordDir;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
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
        if (dtmfPackage == null) {
            dtmfPackage = new DTMFPackage(this);
        }
        try {
            dtmfPackage.subscribe(EventID.DTMF, null, connectionID, listener);
        } catch (Exception e) {
            logger.error("Detection of DTMF failed", e);
        }
    }

    private void testSpectra(String connectionID, NotificationListener listener) {
        System.out.println("testing spectra");
        if (testPackage == null) {
            testPackage = new TestPackage(this);
        }
        try {
            testPackage.subscribe(EventID.TEST_SPECTRA, null, this.getConnection(connectionID), listener);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Detection of Spectra failed", e);
        }
    }

    /**
     * (Non Java-doc).
     *
     * @see org.mobicents.server.spi.BaseEndpoint#play(int, String NotificationListener, boolean.
     */
    @Override
    public void play(EventID signalID, String[] params, String connectionID,
            NotificationListener listener, boolean keepAlive) throws UnknownSignalException {
        logger.info("Play signal, signalID = " + signalID);

        if (signal != null) {
            signal.stop();
            signal = null;
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
                logger.info("Execute announcement signal");
                break;
            case PLAY_RECORD:
                String recordURL = null;
                if (getRecordDir() != null) {
                    recordURL = getRecordDir() + "/" + params[1];
                } else {
                    recordURL = params[1];
                }

                opts = new HashMap();
                opts.put("announcement.url", params[0]);
                opts.put("record.url", recordURL);
                
                AdvancedAudioPackage au = new AdvancedAudioPackage(this);
                signal = au.play(signalID, opts, connectionID, listener);
                logger.info("Execute Play/Record signal for connection: " + connectionID);
                break;
            case TEST_SINE:
                signal = new SineSignal(this, listener, params);
                signal.start();
                break;
            default:
                throw new UnknownSignalException("Signal is unknown: " + signalID);
        }

    }

    @Override
    public void subscribe(EventID eventID, String connectionID, String params[], NotificationListener listener) {
        switch (eventID) {
            case TEST_SPECTRA:
                logger.info("Start test/spectra signal for connection: " + connectionID);
                testSpectra(connectionID, listener);
                break;
            case DTMF:
                logger.info("Start DTMF detector for connection: " + connectionID);
                this.detectDTMF(connectionID, params, listener);
                break;
        }
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

        //terminate push proxy 
        //mediaProxy.setInputStream(null);
        } finally {
            super.deleteConnection(connectionID);
        }
    }

    private String getConnectionID() {
        Collection<BaseConnection> connections = getConnections();
        return connections.iterator().next().getId();
    }
}
