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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import javax.media.format.AudioFormat;
import javax.media.format.UnsupportedFormatException;
import javax.media.protocol.PushBufferStream;

import org.apache.log4j.Logger;
import org.mobicents.media.server.impl.BaseConnection;
import org.mobicents.media.server.impl.BaseEndpoint;

import org.mobicents.media.server.impl.dtmf.InbandDetector;
import org.mobicents.media.server.impl.dtmf.Rfc2833;
import org.mobicents.media.server.spi.Connection;
import org.mobicents.media.server.spi.Endpoint;
import org.mobicents.media.server.spi.NotificationListener;
import org.mobicents.media.server.spi.ResourceUnavailableException;
import org.mobicents.media.server.spi.UnknownSignalException;
import org.mobicents.media.server.spi.dtmf.DtmfDetector;
import org.mobicents.media.server.spi.events.Basic;

/**
 *
 * @author Oleg Kulikov
 */
public class ConfEndpointImpl extends BaseEndpoint {

    private transient HashMap mixers = new HashMap();
    private transient HashMap splitters = new HashMap();
    private transient Logger logger = Logger.getLogger(ConfEndpointImpl.class);

    public ConfEndpointImpl(String localName) {
        super(localName);
    }

    @Override
    public Connection doCreateConnection(Endpoint endpoint, int mode)
            throws ResourceUnavailableException {
        Connection connection = super.doCreateConnection(endpoint, mode);
        //initialize dtmf detector
        createDTMFDetector((BaseConnection) connection);
        return connection;
    }

    /**
     * Creates DTMF detector for the specified connection.
     * 
     * The DTMF detector is a resource of the endpoint created and initialized 
     * for each connection. The DTMF detection procedure is actualy devided into
     * three steps. On first step inactive DTMF detector is created alongside 
     * with connection using the DTMF format negotiated. The second step is used 
     * to initialize detector with media stream. The last step is used to actual 
     * start media analysis and events generation.
     * 
     * @param connection the connection for listenet dtmf.
     */
    private void createDTMFDetector(BaseConnection connection) {
        switch (connection.getDtmfFormat()) {
            case DtmfDetector.INBOUND:
                initResource(RESOURCE_DTMF_DETECTOR, connection.getId(), new InbandDetector());
                logger.debug("Intialized dtmf detector: INBAND");
                break;
            case DtmfDetector.RFC2833:
                initResource(RESOURCE_DTMF_DETECTOR, connection.getId(), new Rfc2833());
                logger.debug("Intialized dtmf detector: RFC 2833");
                break;
        }
    }

    /**
     * Initializes DTMF detector.
     * 
     * The DTMF detector is a resource of the endpoint created and initialized 
     * for each connection. The DTMF detection procedure is actualy devided into
     * three steps. On first step inactive DTMF detector is created alongside 
     * with connection using the DTMF format negotiated. The second step is used 
     * to initialize detector with media stream. The last step is used to actual 
     * start media analysis and events generation.
     * 
     * @param stream the media stream to detect tones from.
     * @param connectionID the identifier of the DTMF detector.
     */
    private void prepareDTMFDetector(PushBufferStream stream, String connectionID) {
        DtmfDetector detector = (DtmfDetector) getResource(
                Endpoint.RESOURCE_DTMF_DETECTOR, connectionID);
        try {
            detector.prepare(stream);
        } catch (UnsupportedFormatException e) {
        }
    }

    /**
     * Attach incoming streams of the specified connection as outgoing for others
     * 
     * @param splitter represents the incoming stream of the specified connection
     * @param connectionID the connection identifier.
     */
    private void splittAudioStream(LocalSplitter splitter, String connectionID) {
        Set<String> identifiers = mixers.keySet();
        for (String id : identifiers) {
            if (!id.equals(connectionID)) {
                LocalMixer mixer = (LocalMixer) mixers.get(id);
                try {
                    mixer.add(connectionID, splitter.newBranch(id));
                } catch (UnsupportedFormatException e) {
                    logger.error("Endpoint= " + this.getLocalName() +
                            ", Unexpected error for splitting streams", e);
                }
            }
        }
    }

    /**
     * Mixes endpoint's input streams into one single stream.
     * 
     * @param mixer represents mixed stream.
     * @param connection the connection which should be excluded while mixing.
     */
    private void mixAudioStreams(LocalMixer mixer, Connection connection) {
        //attach all existing streams
        Set<String> identifiers = splitters.keySet();
        for (String id : identifiers) {
            if (!id.equals(connection.getId())) {
                LocalSplitter splitter = (LocalSplitter) splitters.get(id);
                try {
                    mixer.add(id, splitter.newBranch(connection.getId()));
                } catch (UnsupportedFormatException e) {
                    e.printStackTrace();
                }
            }
        }
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
        DtmfDetector detector = (DtmfDetector) getResource(
                Endpoint.RESOURCE_DTMF_DETECTOR, connectionID);
        if (params != null && params.length > 0 && params[0] != null) {
            detector.setDtmfMask(params[0]);
        }
        detector.start();
        detector.addListener(listener);
    }

    /**
     * (Non Java-doc).
     *
     * @see org.mobicents.media.server.spi.Endpoint#deleteConnection();
     */
    @Override
    public void deleteConnection(String connectionID) {
        //drop 
        try {
            Collection<BaseConnection> connections = getConnections();
            for (BaseConnection connection : connections) {
                if (!connectionID.equals(connection.getId())) {
                    LocalMixer mixer = (LocalMixer) mixers.get(connection.getId());
                    mixer.remove(connectionID);

                    LocalSplitter splitter = (LocalSplitter) splitters.get(connection.getId());
                    splitter.remove(connectionID);
                }
            }

            LocalMixer mixer = (LocalMixer) mixers.remove(connectionID);
            mixer.stop();

            LocalSplitter splitter = (LocalSplitter) splitters.remove(connectionID);
            splitter.close();
        } finally {
            super.deleteConnection(connectionID);
        }
    }

    @Override
    public synchronized void addAudioStream(PushBufferStream stream, String connectionID) {
        AudioFormat fmt = (AudioFormat) stream.getFormat();
        BaseConnection connection = getConnection(connectionID);

        logger.debug("New stream, connectionID=" + connectionID + ", fmt=" + fmt);

        if (fmt.getEncoding().startsWith("telephone-event")) {
            if (connection.getDtmfFormat() == DtmfDetector.RFC2833) {
                prepareDTMFDetector(stream, connectionID);
                if (logger.isDebugEnabled()) {
                    logger.debug("Prepared DTMF detector (RFC 2833), stream fmt=" + fmt);
                }
            }
            return;
        }

        LocalSplitter splitter = new LocalSplitter(connectionID);
        splitter.setInputStream(stream);
        splitters.put(connectionID, splitter);

        if (logger.isDebugEnabled()) {
            logger.debug("Initialized local splitter ID=" + connectionID);
        }

        splittAudioStream(splitter, connectionID);

        if (connection.getDtmfFormat() == DtmfDetector.INBOUND) {
            this.prepareDTMFDetector(splitter.newBranch("DTMF"), connectionID);
        }
    }

    @Override
    public synchronized Collection<PushBufferStream> getAudioStreams(BaseConnection connection) {
        //create and register mixer
        List list = new ArrayList();

        LocalMixer mixer = null;
        try {
            mixer = new LocalMixer(connection.getId(), connection.getAudioFormat(),
                    this.getPacketizationPeriod(), this.getJitter());
        } catch (UnsupportedFormatException e) {
            logger.error("Unexpected error", e);
            return list;
        }

        mixer.start();
        mixers.put(connection.getId(), mixer);

        this.mixAudioStreams(mixer, connection);
        list.add(mixer.getOutputStream());
        return list;
    }

    public void play(int signalID, String[] params, String connectionID, NotificationListener listener, boolean keepAlive) throws UnknownSignalException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void subscribe(int eventID, String connectionID, String params[], NotificationListener listener) {
        switch (eventID) {
            case Basic.DTMF:
                logger.info("Start DTMF detector for connection: " + connectionID);
                this.detectDTMF(connectionID, params, listener);
                break;
        }
    }
}
