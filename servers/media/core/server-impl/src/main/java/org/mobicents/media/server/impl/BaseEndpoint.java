/*
 * BaseEndpoint.java
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
package org.mobicents.media.server.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Timer;

import org.apache.log4j.Logger;
import org.mobicents.media.Format;
import org.mobicents.media.format.AudioFormat;
import org.mobicents.media.server.spi.Connection;
import org.mobicents.media.server.spi.ConnectionListener;
import org.mobicents.media.server.spi.ConnectionState;
import org.mobicents.media.server.spi.Endpoint;
import org.mobicents.media.server.spi.FacilityException;
import org.mobicents.media.server.spi.NotificationListener;
import org.mobicents.media.server.spi.ResourceUnavailableException;
import org.mobicents.media.server.spi.TooManyConnectionsException;
import org.mobicents.media.server.spi.UnknownSignalException;
import org.mobicents.media.server.impl.events.EventPackage;

import EDU.oswego.cs.dl.util.concurrent.ConcurrentReaderHashMap;
import org.mobicents.media.MediaSink;
import org.mobicents.media.MediaSource;
import org.mobicents.media.server.spi.ConnectionMode;
import org.mobicents.media.server.spi.events.NotifyEvent;
import org.mobicents.media.server.spi.events.RequestedEvent;
import org.mobicents.media.server.spi.events.RequestedSignal;

/**
 * The basic implementation of the endpoint.
 * 
 * An BaseEndpoint is a logical representation of a physical entity, such as an
 * analog phone or a channel in a trunk. Endpoints are sources or sinks of data
 * and can be physical or virtual. Physical endpoint creation requires hardware
 * installation while software is sufficient for creating a virtual
 * BaseEndpoint. An interface on a gateway that terminates a trunk connected to
 * a PSTN switch is an example of a physical BaseEndpoint. An audio source in an
 * audio-content server is an example of a virtual BaseEndpoint.
 * 
 * @author Oleg Kulikov.
 */
public abstract class BaseEndpoint implements Endpoint {

    private final static AudioFormat PCMA = new AudioFormat(AudioFormat.ALAW, 8000, 8, 1);
    private final static AudioFormat PCMU = new AudioFormat(AudioFormat.ULAW, 8000, 8, 1);
    private final static AudioFormat SPEEX = new AudioFormat(AudioFormat.SPEEX, 8000, 8, 1);
    private final static AudioFormat G729 = new AudioFormat(AudioFormat.G729, 8000, 8, 1);
    private final static AudioFormat LINEAR = new AudioFormat(AudioFormat.LINEAR, 8000, 16, 1,
            AudioFormat.LITTLE_ENDIAN, AudioFormat.SIGNED);
    private final static AudioFormat DTMF = new AudioFormat("telephone-event");
    private final static Format[] formats = new Format[]{LINEAR, DTMF};
    private String localName;
    private String rtpFactoryName;
    private HashMap<String, HashMap> mediaSources = new HashMap();
    private HashMap<String, HashMap> mediaSinks = new HashMap();
    private boolean hasConnections;
    private ConcurrentReaderHashMap connections = new ConcurrentReaderHashMap();
    private int maxConnections = 0;
    private ArrayList<NotificationListener> listeners = new ArrayList();
    protected ArrayList<ConnectionListener> connectionListeners = new ArrayList();
    protected ConnectionStateGuard guard=new ConnectionStateGuard(connectionListeners);
    protected static Timer connectionTimer = new Timer();
    private transient Logger logger = Logger.getLogger(BaseEndpoint.class);

    public BaseEndpoint(String localName) {
        this.localName = localName;
    }

    /**
     * (Non Java-doc).
     * 
     * @see org.mobicents.media.server.spi.Endpoint#getLocalName();
     */
    public String getLocalName() {
        return localName;
    }

    public Format[] getSupportedFormats() {
        return formats;
    }

    /**
     * Gets the name of used RTP Factory.
     * 
     * @return the JNDI name of the RTP Factory
     */
    public String getRtpFactoryName() {
        return rtpFactoryName;
    }

    /**
     * Sets the name of used RTP Factory.
     * 
     * @param rtpFactoryName
     *            the JNDI name of the RTP Factory.
     */
    public void setRtpFactoryName(String rtpFactoryName) {
        this.rtpFactoryName = rtpFactoryName;
    }

    /**
     * Gets the maximum amount of the connections that endpoint can implement.
     * 
     * @return the maximum available connections.
     */
    public int getMaxConnectionsAvailable() {
        return maxConnections;
    }

    /**
     * Sets the maximum amount of the connections that endpoint can implement.
     * 
     * @return the maximum available connections.
     */
    public void setMaxConnectionsAvailable(int amount) {
        this.maxConnections = amount;
    }

    /**
     * Indicates that endpoint has connections.
     * 
     * @return true if endpoint executing one or more connection and false
     *         otherwise.
     */
    public boolean hasConnections() {
        return this.hasConnections;
    }

    /**
     * Gets connection with specified identifier.
     * 
     * @param connectionID
     *            the identifier of the connection to return
     */
    public Connection getConnection(String connectionID) {
        return (Connection) connections.get(connectionID);
    }

    /**
     * Gets all connections which are executed by this endpoint.
     * 
     * @return collection of RtpConnectionImpl objects.
     */
    public Collection<Connection> getConnections() {
        return connections.values();
    }

    /**
     * Used for internal connection creation.
     * 
     * @param endpoint
     *            the endpoint which creates connection.
     * @param mode
     *            the mode of connection been created.
     * @return Object implementing connection. *
     * @throws org.mobicents.media.server.spi.ResourceUnavailableException
     */
    protected Connection doCreateConnection(Endpoint endpoint, ConnectionMode mode) throws ResourceUnavailableException {
        return new RtpConnectionImpl(endpoint, mode);
    }

    /**
     * (Non Java-doc).
     * 
     * @see org.mobicents.media.server.spi.Endpoint#createConnection(int);
     */
    public synchronized Connection createConnection(ConnectionMode mode) throws TooManyConnectionsException,
            ResourceUnavailableException {
        try {
            if (connections.size() == maxConnections) {
                throw new TooManyConnectionsException("Maximum " + maxConnections + " connections allowed");
            }

            Connection connection = doCreateConnection(this, mode);
            connection.addListener(guard);
            connections.put(connection.getId(), connection);

            HashMap<String, MediaSource> sourceMap = initMediaSources();
            for (MediaSource source : sourceMap.values()) {
                source.connect(((BaseConnection) connection).getMux());
                    source.addListener((BaseConnection) connection);
            }
            mediaSources.put(connection.getId(), sourceMap);

            HashMap<String, MediaSink> sinkMap = initMediaSinks();
            for (MediaSink sink : sinkMap.values()) {
                sink.connect(((BaseConnection) connection).getDemux());
                    sink.addListener((BaseConnection) connection);
            }
            mediaSinks.put(connection.getId(), sinkMap);
            return connection;
        } finally {
            hasConnections = connections.size() > 0;
        }
    }

    /**
     * (Non Java-doc).
     * 
     * @see org.mobicents.media.server.spi.Endpoint#createConnection(int);
     */
    public synchronized Connection createLocalConnection(ConnectionMode mode) throws TooManyConnectionsException,
            ResourceUnavailableException {
        // hasConnections = true;
        try {
            if (connections.size() == maxConnections) {
                throw new TooManyConnectionsException("Maximum " + maxConnections + " connections allowed");
            }

            synchronized (this) {
                Connection connection = new LocalConnectionImpl(this, mode);
                connection.addListener(guard);
                connections.put(connection.getId(), connection);

                HashMap<String, MediaSource> sourceMap = initMediaSources();
                for (MediaSource source : sourceMap.values()) {
                    source.connect(((BaseConnection) connection).getMux());
                    source.addListener((BaseConnection) connection);
                }
                mediaSources.put(connection.getId(), sourceMap);
                HashMap<String, MediaSink> sinkMap = initMediaSinks();
                for (MediaSink sink : sinkMap.values()) {
                    sink.connect(((BaseConnection) connection).getDemux());
                    sink.addListener((BaseConnection) connection);
                }
                mediaSinks.put(connection.getId(), sinkMap);
                return connection;
            }
        } finally {
            hasConnections = connections.size() > 0;
        }
    }

    /**
     * (Non Java-doc).
     * 
     * @see org.mobicents.media.server.spi.Endpoint#deleteConnection();
     */
    public synchronized void deleteConnection(String connectionID) {
        BaseConnection connection = (BaseConnection) connections.remove(connectionID);
        if (connection != null) {
            connection.detect(null);
            //clean
            HashMap map = mediaSources.remove(connection.getId());
            Collection<MediaSource> gens = map.values();
            for (MediaSource generator : gens) {
                generator.stop();
                generator.disconnect(connection.getMux());
                generator.dispose();
            }
            map.clear();

            map = mediaSinks.remove(connection.getId());
            Collection<MediaSink> dets = map.values();
            for (MediaSink detector : dets) {
                detector.disconnect(connection.getDemux());
                detector.dispose();
            }
            map.clear();

            connection.close();
            logger.info("Deleted connection " + connection);
        }

        hasConnections = connections.size() > 0;
    }

    /**
     * (Non Java-doc).
     * 
     * @see org.mobicents.media.server.spi.Endpoint#deleteAllConnections();
     */
    public synchronized void deleteAllConnections() {
        Iterator list = connections.values().iterator();
        while (list.hasNext()) {
            Connection connection = (Connection) list.next();
            deleteConnection(connection.getId());
        }
    }

    /**
     * Initialized media sources executed by this endpoint.
     * Specific endpoint should overwrite this method.
     * 
     * @return the map of the media sources.
     */
    public abstract HashMap initMediaSources();

    /**
     * Initialized media sinks executed by this endpoint.
     * Specific endpoint should overwrite this method.
     * 
     * @return the map of the media sinks.
     */
    public abstract HashMap initMediaSinks();

    /**
     * Gets the map of the media sources executed by this endpoint and associated
     * with specified connection.
     * 
     * @param connectionID the identifier of the connection.
     * @return the map where key is an identifier of media source and value is 
     * a media source instance
     */
    public HashMap getMediaSources(String connectionID) {
        return mediaSources.get(connectionID);
    }

    /**
     * Gets the map of the media sinks executed by this endpoint and associated
     * with specified connection.
     * 
     * @param connectionID the identifier of the connection.
     * @return the map where key is an identifier of media sink and value is 
     * a media sink instance
     */
    public HashMap getMediaSinks(String connectionID) {
        return mediaSinks.get(connectionID);
    }

    /**
     * Gets the specified media source executed by this endpoint and associated
     * with specified connection.
     * 
     * @param id the identifier of the media source
     * @param connection the connection with which this connection is associated
     * @return the media source instance.
     */
    public MediaSource getMediaSource(Generator id, Connection connection) {
        return (MediaSource) mediaSources.get(connection.getId()).get(id);
    }

    /**
     * Gets the specified media sink executed by this endpoint and associated
     * with specified connection.
     * 
     * @param id the identifier of the media sink
     * @param connection the connection with which this connection is associated
     * @return the media source instance.
     */
    public MediaSink getMediaSink(Generator id, Connection connection) {
        return (MediaSink) mediaSinks.get(connection.getId()).get(id);
    }

    /**
     * Adds a Listener.
     * 
     * @param listener
     *            the listener instance to add.
     */
    public void addNotificationListener(NotificationListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes a listener.
     * 
     * @param listener
     *            the listener instance for remove.
     */
    public void removeNotificationListener(NotificationListener listener) {
        listeners.remove(listener);
    }

    public void addConnectionListener(ConnectionListener listener) {
        connectionListeners.add(listener);
    }

    public void removeConnectionListener(ConnectionListener listener) {
        connectionListeners.remove(listener);
    }

    /**
     * Sends specified event to registered listeners.
     * 
     * @param event
     *            the event to be sent.
     */
    public synchronized void sendEvent(NotifyEvent event) {
    }

    protected String getPackageName(String eventID) {
        String[] tokens = eventID.split("\\.");

        if (tokens.length == 1) {
            return tokens[0];
        }

        String s = tokens[0];
        for (int i = 1; i < tokens.length - 1; i++) {
            s += "." + tokens[i];
        }
        return s;
    }

    protected String getEventName(String eventID) {
        String[] tokens = eventID.split("\\.");
        return tokens[tokens.length - 1];
    }

    private AbstractSignal getSignal(RequestedSignal signal) throws UnknownSignalException, FacilityException {
        try {
            EventPackage eventPackage = EventPackageFactory.load(signal.getID().getPackageName());
            return eventPackage.getSignal(signal);
        } catch (ClassNotFoundException e) {
            logger.error("Wrong package name: ", e);
            throw new UnknownSignalException(signal.getID().getFQN());
        } catch (Exception e) {
            logger.error("Unexpected error: ", e);
            throw new FacilityException(e.getMessage());
        }
    }



    public void execute(RequestedSignal[] signals, RequestedEvent[] events) {
    }

    public void execute(RequestedSignal[] signals, RequestedEvent[] events, String connectionID) {
        System.out.println("Connection ID=" + connectionID);
        BaseConnection connection = (BaseConnection) this.getConnection(connectionID);
        System.out.println("Connection =" + connection);
        
        connection.detect(null);
        for (int i = 0; i < events.length; i++) {
            System.out.println("Detectting event= " + events[i].getID().getEventName() + ", handler=" + events[i].getHandler());
            connection.detect(events[i]);
        }
        System.out.println("Listeners ready =");

        if (signals.length > 0) {
            try {
                AbstractSignal signal = getSignal(signals[0]);
                signal.apply(connection);
        System.out.println("Play signal =" + signal);
            } catch (Exception e) {
                logger.error("Execute signal error", e);
            }
        }
    }

    private class ConnectionStateGuard implements ConnectionListener
    {

    	 protected ArrayList<ConnectionListener> connectionListeners =null;
    	 
    	 
		public ConnectionStateGuard(
				ArrayList<ConnectionListener> connectionListeners) {
			super();
			this.connectionListeners = connectionListeners;
		}


		public void onStateChange(Connection connection,
				ConnectionState oldState) {
			synchronized(connectionListeners)
			{
				for (ConnectionListener cl : connectionListeners) {
					cl.onStateChange(connection, oldState);
				}
			}	
		}
    	
    }
    
}
