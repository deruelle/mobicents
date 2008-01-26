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

import org.mobicents.media.server.spi.NotificationListener;
import org.mobicents.media.server.spi.UnknownSignalException;

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
        logger.debug("Adding audio stream");
        AudioFormat fmt = (AudioFormat) stream.getFormat();

        logger.debug("Append stream to a conference, stream format: " + fmt);

        //create and register splitter
        LocalSplitter splitter = new LocalSplitter(connectionID);
        splitter.setInputStream(stream);
        splitters.put(connectionID, splitter);

        //append stream for each mixer
        Set<String> identifiers = mixers.keySet();
        for (String id : identifiers) {
            if (!id.equals(connectionID)) {
                LocalMixer mixer = (LocalMixer) mixers.get(id);
                try {
                    mixer.add(connectionID, splitter.newBranch(id));
                } catch (UnsupportedFormatException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @Override
    public synchronized Collection <PushBufferStream> getAudioStreams(BaseConnection connection) {
        //create and register mixer
        List list = new ArrayList();
        
        LocalMixer mixer = null;
        try {
            mixer = new LocalMixer(connection.getId(), connection.getAudioFormat(),
                    this.getPacketizationPeriod(), this.getJitter());
        } catch (UnsupportedFormatException e) {
        }

        mixer.start();
        mixers.put(connection.getId(), mixer);

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

        list.add(mixer.getOutputStream());
        return list;
    }


    public void play(int signalID, String[] params, String connectionID, NotificationListener listener, boolean keepAlive) throws UnknownSignalException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
