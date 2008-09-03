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
package org.mobicents.media.server.impl.enp.cnf;

import java.util.Collection;
import java.util.HashMap;
import org.apache.log4j.Logger;
import org.mobicents.media.server.impl.BaseEndpoint;

import org.mobicents.media.server.impl.common.ConnectionMode;
import org.mobicents.media.server.impl.common.ConnectionState;
import org.mobicents.media.server.impl.enp.cnf.AudioMixer;
import org.mobicents.media.server.spi.Connection;
import org.mobicents.media.server.spi.ConnectionListener;
import org.mobicents.media.server.spi.Endpoint;
import org.mobicents.media.server.spi.ResourceUnavailableException;

/**
 *
 * @author Oleg Kulikov
 */
public class ConfEndpointImpl extends BaseEndpoint implements ConnectionListener {

    protected HashMap audioSources = new HashMap();
    protected HashMap audioSinks = new HashMap();
    
    private HashMap mixers = new HashMap();

    private transient Logger logger = Logger.getLogger(ConfEndpointImpl.class);
    
    public ConfEndpointImpl(String localName) {
        super(localName);
        this.setMaxConnectionsAvailable(1000);
    }

    @Override
    protected Connection doCreateConnection(Endpoint endpoint, ConnectionMode mode) throws ResourceUnavailableException {
        Connection connection = super.doCreateConnection(endpoint, mode);
        
        AudioMixer mixer = new AudioMixer(connection.toString());
        mixers.put(connection.getId(), mixer);
        mixer.start();
        
        return connection;
    }    

    /**
     * Attaches connection's receiver stream to other connections.
     * 
     * @param connection the connection which receiver stream will be attached.
     */
    private void attachReceiver(Connection connection) {
//        MediaSplitter splitter = intakes.get(connection.getId());
        //walk over existing connection and create new input stream
        //on each mixer and assign it MediaHandler for current Primary Sink
        Collection<Connection> connections = this.getConnections();
        for (Connection conn : connections) {
            if (!conn.getId().equals(connection.getId())) {
                AudioMixer mixer = (AudioMixer) mixers.get(conn.getId());
                if (mixer != null) {
//                    splitter.connect(mixer);
                }
            }
        }
    }

    /**
     * Attach receiver streams from "otrher" connections to current mixer.
     * Register mixer as secondary source for current primary source.
     * 
     * @param connection the connection which mixer will be used to attach to.
     */
    private void attachSender(Connection connection) {
        Collection<Connection> connections = getConnections();
        AudioMixer mixer = (AudioMixer) mixers.get(connection.getId());
        mixer.start();
        for (Connection conn : connections) {
            if (!conn.getId().equals(connection.getId())) {
//                MediaSplitter splitter = intakes.get(conn.getId());
//                splitter.connect(mixer);
            }
        }
    }

    public void detachReceiver(Connection connection) {
//        MediaSplitter splitter = intakes.get(connection.getId());

        Collection<Connection>connections = getConnections();
        for (Connection conn : connections) {
            if (!conn.getId().equals(connection.getId())) {
                    AudioMixer mixer = (AudioMixer) mixers.get(conn.getId());
//                    mixer.disconnect(splitter);
            }
        }
    }

    public void detachSender(Connection connection) {
        AudioMixer mixer = (AudioMixer) mixers.get(connection.getId());
        Collection<Connection>connections = getConnections();
        
        for (Connection conn : connections) {
            if (!conn.getId().equals(connection.getId())) {
//                MediaSplitter splitter = intakes.get(conn.getId());
//                splitter.disconnect(mixer);
            }
        }
    }

    public synchronized void onStateChange(Connection connection, ConnectionState oldState) {
        switch (connection.getState()) {
            //endpoint can receive media, so all existing mixers should
            //be registered as secondary sources for primary source.   
            case HALF_OPEN:
                if (logger.isDebugEnabled()) {
                    logger.debug("localName=" + getLocalName() + ", Attaching receiver");
                }
                attachReceiver(connection);
                break;
            case OPEN:
                if (logger.isDebugEnabled()) {
                    logger.debug("localName=" + getLocalName() + ", Attaching sender");
                }
                attachSender(connection);
                break;
            case CLOSED:
                if (logger.isDebugEnabled()) {
                    logger.debug("localName=" + getLocalName() + ", Detaching receiver");
                }
                detachReceiver(connection);
                if (logger.isDebugEnabled()) {
                    logger.debug("localName=" + getLocalName() + ", Detaching sender");
                }
                detachSender(connection);
                break;
        }
    }

}
