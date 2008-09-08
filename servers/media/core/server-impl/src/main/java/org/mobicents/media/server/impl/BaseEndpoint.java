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
import java.util.Set;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.mobicents.media.Format;
import org.mobicents.media.format.AudioFormat;
import org.mobicents.media.server.impl.common.ConnectionMode;
import org.mobicents.media.server.spi.Connection;
import org.mobicents.media.server.spi.ConnectionListener;
import org.mobicents.media.server.spi.Endpoint;
import org.mobicents.media.server.spi.FacilityException;
import org.mobicents.media.server.spi.NotificationListener;
import org.mobicents.media.server.spi.ResourceUnavailableException;
import org.mobicents.media.server.spi.TooManyConnectionsException;
import org.mobicents.media.server.spi.UnknownSignalException;
import org.mobicents.media.server.spi.events.EventDetector;
import org.mobicents.media.server.spi.events.EventPackage;
import org.mobicents.media.server.spi.events.NotifyEvent;
import org.mobicents.media.server.spi.events.Options;
import org.mobicents.media.server.spi.events.Signal;

import EDU.oswego.cs.dl.util.concurrent.ConcurrentReaderHashMap;

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
    protected HashMap<String, Signal> signals = new HashMap();
    protected ConcurrentHashMap<String, EventDetector> detectors = new ConcurrentHashMap<String, EventDetector>();
    private boolean hasConnections;
    private ConcurrentReaderHashMap connections = new ConcurrentReaderHashMap();
    private int maxConnections = 0;
    private ArrayList<NotificationListener> listeners = new ArrayList();
    protected ArrayList<ConnectionListener> connectionListeners = new ArrayList();
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
        hasConnections = true;
        try {
            if (connections.size() == maxConnections) {
                throw new TooManyConnectionsException("Maximum " + maxConnections + " connections allowed");
            }

            // Connection connection = new RtpConnectionImpl(this, mode);
            Connection connection = doCreateConnection(this, mode);
            connections.put(connection.getId(), connection);

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
    public Connection createLocalConnection(ConnectionMode mode) throws TooManyConnectionsException,
            ResourceUnavailableException {
        // hasConnections = true;
        try {
            if (connections.size() == maxConnections) {
                throw new TooManyConnectionsException("Maximum " + maxConnections + " connections allowed");
            }

            synchronized (this) {
                Connection connection = new LocalConnectionImpl(this, mode);
                connections.put(connection.getId(), connection);

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
    public void deleteConnection(String connectionID) {
        BaseConnection connection = (BaseConnection) connections.remove(connectionID);

        synchronized (this) {
            if (connection != null) {
                // disable all signals
                Signal signal = signals.get(connectionID);
                if (signal != null) {
                    signal.stop();
                    connection.getMux().disconnect(signal);
                    signals.remove(connectionID);
                }

                // disable all detectors
                EventDetector detector = detectors.get(connectionID);
                connection.getDemux().disconnect(detector);
                detectors.remove(connectionID);

                connection.close();
                logger.info("Deleted connection " + connection);
            }
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
     * Adds a Listener.
     * 
     * @param listener
     *            the listener instance to add.
     */
    public void addNotifyListener(NotificationListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes a listener.
     * 
     * @param listener
     *            the listener instance for remove.
     */
    public void removeNotifyListener(NotificationListener listener) {
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
        for (NotificationListener listener : listeners) {
            listener.update(event);
        }
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

    private Signal getSignal(String signalID, Options options) throws UnknownSignalException, FacilityException {
        try {
            EventPackage eventPackage = EventPackageFactory.load(this.getPackageName(signalID));
            return eventPackage.getSignal(getEventName(signalID), options);
        } catch (ClassNotFoundException e) {
            logger.error("Wrong package name: ", e);
            throw new UnknownSignalException(signalID);
        } catch (Exception e) {
            logger.error("Unexpected error: ", e);
            throw new FacilityException(e.getMessage());
        }
    }

    private EventDetector getDetector(String eventID, Options options) throws UnknownSignalException, FacilityException {
        try {
            EventPackage eventPackage = EventPackageFactory.load(getPackageName(eventID));
            return eventPackage.getDetector(getEventName(eventID), options);
        } catch (ClassNotFoundException e) {
            throw new UnknownSignalException(eventID);
        } catch (Exception e) {
            throw new FacilityException(e.getMessage());
        }
    }

    public void play(String signalID, Options options, String connectionID, NotificationListener listener)
            throws UnknownSignalException, FacilityException {
        System.out.println("Requested signal=" + signalID + ", connection=" + connectionID);
        if (logger.isDebugEnabled()) {
            logger.debug("Requested signal ID=" + signalID);
        }

        Signal signal = getSignal(signalID, options);
        signal.addListener(listener);

        BaseConnection connection = (BaseConnection) this.getConnection(connectionID);

        Signal currentSignal = signals.remove(connectionID);

        if (currentSignal != null) {
            if (logger.isDebugEnabled()) {
                logger.debug("Disable current signal:" + currentSignal.getID());
            }
            System.out.println("***** TONE STOPPED AND REMOVED***");
            currentSignal.stop();
            currentSignal.disconnect(connection.getMux());
        }

		if (currentSignal != null) {
			if (logger.isDebugEnabled()) {
				logger.debug("Disable current signal:" + currentSignal.getID());
			}
			
			currentSignal.stop();
			currentSignal.disconnect(connection.getMux());
		}

            if (logger.isDebugEnabled()) {
                logger.debug("Starting signal ID=" + signalID);
            }
            signal.start();

            if (signal instanceof EventDetector) {
                ((EventDetector) signal).connect(connection.getDemux());
            }
        } catch (Exception e) {
            logger.error("Could not start signal", e);
            throw new FacilityException(e.getMessage());
        }
    }

    public void play(String signalID, Options options, NotificationListener listener) throws UnknownSignalException,
            FacilityException {
        System.out.println("REQUESTED EVENT FOR ENDPOINT");
    }

    /**
     * Asks the to detect requested event and report.
     * 
     * Such events may include, for example, fax tones, continuity tones, or
     * on-hook transition.
     * 
     * @param eventID
     *            the identifier of the event.
     * @param the
     *            Call Agent callback interface currently controlling that
     *            endpoint.
     * @persistent true if event is always detected on the endpoint.
     */
    public void subscribe(String eventID, Options options, NotificationListener listener)
            throws UnknownSignalException, FacilityException {
    }

    /**
     * Asks the endpoint to detect requested event on a specified connection and
     * report.
     * 
     * Such events may include, for example, fax tones, continuity tones, or
     * on-hook transition.
     * 
     * @param eventID
     *            the identifier of the event.
     * @param connectionID
     *            the identifier of the connection.
     * @param the
     *            Call Agent callback interface currently controlling that
     *            endpoint.
     */
    public void subscribe(String eventID, Options options, String connectionID, NotificationListener listener)
            throws UnknownSignalException, FacilityException {
        EventDetector detector = this.getDetector(eventID, options);
        detector.addListener(listener);

        BaseConnection connection = (BaseConnection) this.getConnection(connectionID);
        Set<String> keys = detectors.keySet();
        for (String key : keys) {
            if (key.startsWith(connectionID)) {
                EventDetector det = detectors.remove(key);
                if (logger.isDebugEnabled()) {
                    logger.debug("*** DISCONNECTING detector");
                }
                det.disconnect(connection.getDemux());
            }
        }
        detector.connect(connection.getDemux());
        detectors.put(connectionID + eventID.toString(), detector);
    }
}
