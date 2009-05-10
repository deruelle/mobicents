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
import java.util.Set;
import java.util.Timer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.locks.ReentrantLock;

import javax.sdp.SdpFactory;

import org.apache.log4j.Logger;
import org.mobicents.media.Component;
import org.mobicents.media.Format;
import org.mobicents.media.MediaSink;
import org.mobicents.media.MediaSource;
import org.mobicents.media.server.impl.rtp.RtpFactory;
import org.mobicents.media.server.impl.rtp.RtpSocket;
import org.mobicents.media.server.spi.Connection;
import org.mobicents.media.server.spi.ConnectionListener;
import org.mobicents.media.server.spi.ConnectionMode;
import org.mobicents.media.server.spi.ConnectionState;
import org.mobicents.media.server.spi.Endpoint;
import org.mobicents.media.server.spi.NotificationListener;
import org.mobicents.media.server.spi.ResourceUnavailableException;
import org.mobicents.media.server.spi.TooManyConnectionsException;

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

    /** The local name of the endpoint */
    private String localName;
    
    /** The JNDI name of the RTP Factory */
    private RtpFactory rtpFactory;
    /** This flag indicates is this endpoint in use or not*/
    private boolean isInUse = false;
    /** The list of indexes available for connection enumeration within endpoint */
    private ArrayList<Integer> connectionIndexes = new ArrayList();    
    /** The last generated connection's index*/
    private int lastIndex = -1;    
    /** Holder for created connections */
    protected transient HashMap connections = new HashMap();
    /** The number of max allowed connections for this endpoint */
    protected int maxConnections = 0;
    /** The queue of currently plaing signals */
//    private HashMap<String, SignalQueue> signalQueues = new HashMap();
    
    /** Listeners for events detected on the endpoint */
    protected transient ArrayList<NotificationListener> listeners = new ArrayList<NotificationListener>();
    /** Connection state listeners */
    protected transient ArrayList<ConnectionListener> connectionListeners = new ArrayList<ConnectionListener>();
//    private transient HashMap<String, EventPackage> eventPackages = new HashMap();
    
    private transient ExecutorService commandExecutor;
    private transient ExecutorService eventQueue;
    /** Timer for connection's state*/
    protected transient static Timer stateTimer = new Timer("ConnectionStateTimer");
    /** Thread handling receved packets */
    private transient ScheduledExecutorService receiverThread;
    /** Thread handling outgoing packets */
    private transient ScheduledExecutorService transmittorThread;
    /** Maximum conversation time allowed for connection */
    protected int connectionLifeTime = 30;
    protected boolean gatherStatistics = false;
    protected long packets = 0;
    protected long numberOfBytes = 0;
    
    /** SDP Factory instance*/
    private transient SdpFactory sdpFactory = SdpFactory.getInstance();
    /** lock instance */
    protected ReentrantLock lock = new ReentrantLock();
    /** Logger instance */
    private transient Logger logger = Logger.getLogger(this.getClass());       

    /**
     * Creates new endpoint instance.
     * 
     * @param localName
     */
    public BaseEndpoint(String localName) {
        this.localName = localName;
    }

    /**
     * Generates index for connection.
     * 
     * The connection uses this method to ask endpoint for new lowerest index.
     * The index is unique withing endpoint but it is not used as connection 
     * identifier outside of the endpoint.
     * 
     * @return the lowerest available integer value.
     */
    protected int getIndex() {
        return connectionIndexes.isEmpty() ? ++lastIndex : connectionIndexes.remove(0);
    }

    /**
     * Notify endpoint that specified index not longer is associated with 
     * any connection and may be reused.
     * 
     * @param i the index value.
     */
    protected void releaseIndex(int i) {
        connectionIndexes.add(i);
    }

    /**
     * (Non Java-doc.)
     * 
     * @see org.mobicents.media.server.spi.Endpoint#start() 
     */
    public void start() throws ResourceUnavailableException {
        receiverThread = Executors.newSingleThreadScheduledExecutor(new ReceiverThreadFactory());
        transmittorThread = Executors.newSingleThreadScheduledExecutor(new TransmittorThreadFactory());
        commandExecutor = Executors.newSingleThreadExecutor(new CommandThreadFactory());
        eventQueue = Executors.newSingleThreadExecutor(new EventQueueThreadFactory());
    }

    /**
     * (Non Java-doc.)
     * 
     * @see org.mobicents.media.server.spi.Endpoint#stop() 
     */
    public void stop() {
        receiverThread.shutdownNow();
        transmittorThread.shutdownNow();

        commandExecutor.shutdownNow();
        eventQueue.shutdownNow();
    }
    
    public void gracefulStop(){
    	if(this.isInUse()){
    		
    	}
    }

    /**
     * (Non Java-doc).
     * 
     * @see org.mobicents.media.server.spi.Endpoint#getLocalName();
     */
    public String getLocalName() {
        return localName;
    }

    /**
     * Gets the reference to used RTP Factory instance.
     * 
     * This method is used by concrete endpoint to preinstantiate RTP stuff.
     * The RTP Factory is bounded to JNDI under <code>rtpFactoryName</code>
     * which is provided by management interface. If JNDI name not specified this 
     * method creates new RTPFactory instance with assigned G711 codec only. 
     * The default RTP factory is used by junit tests.
     * 
     * @return RtpFactory instance.
     */
    protected RtpFactory getRtpFactory()  {
    	return this.rtpFactory;
    }

    /**
     * Gets the SDP Factory used by RTP connection to parse and create SDPs
     * during connect procedure.
     * 
     * The endpoint precreates instance of SDP factory for reducing time of 
     * factory access
     * 
     * @return the instance of SDP factory.
     */
    protected SdpFactory getSdpFactory() {
        return sdpFactory;
    }

    /**
     * Gets the receiver thread.
     * Media components use receiver thread to schedule handing of received packets
     * 
     * @return the receiver thread which as ExecutorService object
     */
    public ScheduledExecutorService getReceiverThread() {
        return receiverThread;
    }

    /**
     * Gets the reference to the trasmittor thread which handles outgoing packets.
     * Media components use this thread to schedule handing of outgoing packets.
     * 
     * @return thread as ExecutorService.
     */
    public ScheduledExecutorService getTransmittorThread() {
        return transmittorThread;
    }



    /**
     * Sets the name of used RTP Factory.
     * 
     * @param rtpFactoryName
     *            the JNDI name of the RTP Factory.
     */
    public void setRtpFactory(RtpFactory rtpFactory) {
        this.rtpFactory = rtpFactory;
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
     * Gets the primary sink instance.
     * 
     * @return the primary sink of the endpoint.
     */
    public abstract MediaSink getPrimarySink(Connection connection);

    /**
     * Gets the primary sink instance.
     * 
     * @return the primary source of the endpoint.
     */
    public abstract MediaSource getPrimarySource(Connection connection);

    /**
     * Sets maximum connection's life time.
     * 
     * @param connectionLifeTime the value in minutes
     */
    public void setConnectionLifeTime(int connectionLifeTime) {
        this.connectionLifeTime = connectionLifeTime;
    }

    /**
     * Gets the maximum duration of media conversation.
     * 
     * @return the time in minutes.
     */
    public int getConnectionLifeTime() {
        return this.connectionLifeTime;
    }

    /**
     * (Non Java-doc.)
     * 
     * @see org.mobicents.media.server.spi.Endpoint#hasConnections() 
     */
    public boolean hasConnections() {
        return !connections.isEmpty();
    }

    /**
     * Shows the usage status of this enpoint.
     * 
     * @return true if the endpoint is in use.
     */
    public boolean isInUse() {
        return this.isInUse;
    }
    
    /**
     * Marks this endpoint as used or not used.
     * 
     * @param isInUse true for used undpoint and false for unused.
     */
    public void setInUse(boolean isInUse) {
        this.isInUse = isInUse;
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
    public Connection createConnection(ConnectionMode mode) throws TooManyConnectionsException, ResourceUnavailableException {
    	lock.lock();
        try {
            if (connections.size() == maxConnections) {
                throw new TooManyConnectionsException("Maximum " + maxConnections + " connections allowed");
            }
            Connection connection = new RtpConnectionImpl(this, mode);
            return connection;
        } catch (Exception e) {
            logger.error("Could not create RTP connection", e);
            throw new ResourceUnavailableException(e.getMessage());
        } finally {
            lock.unlock();
        }
    }

    /**
     * (Non Java-doc).
     * 
     * @see org.mobicents.media.server.spi.Endpoint#createConnection(int);
     */
    public Connection createLocalConnection(ConnectionMode mode) throws TooManyConnectionsException, ResourceUnavailableException {
        lock.lock();
        try {
            if (connections.size() == maxConnections) {
                throw new TooManyConnectionsException("Maximum " + maxConnections + " connections allowed for " + getLocalName());
            }
            Connection connection = new LocalConnectionImpl(this, mode);
//            signalQueues.put(connection.getId(), new SignalQueue(this));
            return connection;
        } catch(TooManyConnectionsException e){
            logger.error("Could not create Local connection", e);
            throw e;
        } catch (ResourceUnavailableException e) {
            logger.error("Could not create Local connection", e);
            throw e;
        } catch(Exception e){        	
            logger.error("Could not create Local connection", e);
            throw new ResourceUnavailableException(e.getMessage());        	
        } finally {
            lock.unlock();
        }
    }

    /**
     * (Non Java-doc).
     * 
     * @see org.mobicents.media.server.spi.Endpoint#deleteConnection();
     */
    public void deleteConnection(String connectionID) {
        lock.lock();
        try {
//            SignalQueue signalQueue = signalQueues.remove(connectionID);
//            if (signalQueue != null) {
//                signalQueue.reset();
//            }
            BaseConnection connection = (BaseConnection) connections.get(connectionID);
            if (connection != null) {
//                connection.detect(null);
                connection.close();
            }
            isInUse = connections.size() > 0;
        } finally {
            lock.unlock();
        }
    }

    public Component getComponent(String name) {
        return null;
    }
    
    /**
     * (Non Java-doc).
     * 
     * @see org.mobicents.media.server.spi.Endpoint#deleteAllConnections();
     */
    public void deleteAllConnections() {
        lock.lock();
        try {
            BaseConnection[] list = new BaseConnection[connections.size()];
            connections.values().toArray(list);
            for (int i = 0; i < list.length; i++) {
                list[i].close();
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * The list of formats supported by the endpoint.
     * 
     * @return the list of Format object.
     */
    public abstract Format[] getFormats();
    
    /**
     * Used by RTP connection to allocate RTP socket.
     * 
     * @param connection the connection instance which requires RTP socket.
     * @return the instance of RTP socket.
     * @throws org.mobicents.media.server.spi.ResourceUnavailableException
     */
    public abstract RtpSocket allocateRtpSocket(Connection connection) throws ResourceUnavailableException;

    /**
     * Used by RTP connection to release unused RTP scoket.
     * The RTP connection call this method during close procedure.
     * 
     * @param rtpSocket the RTP socket to release
     * @param connection the connection which releases socket.
     */
    public abstract void deallocateRtpSocket(RtpSocket rtpSocket, Connection connection);

    /**
     * Instructs endpoint to allocate all media sources associated with a connection.
     * 
     * @param connection the connection for which endpoint should prepare sources
     * @param formats the list of formats indicating requires sources.
     */
    public abstract void allocateMediaSources(Connection connection, Format[] formats);

    /**
     * Asks endpoint to allocate media sinks associated with the connection.
     * 
     * @param connection the connection for which endpoint allocate sinks.
     */
    public abstract void allocateMediaSinks(Connection connection);

    /**
     * Releases media sources associated with specified connection.
     * 
     * @param connection the connection instance.
     */
    public abstract void releaseMediaSources(Connection connection);

    /**
     * Releases media sinks associated with specified connection.
     * @param connection the connection instance.
     */
    public abstract void releaseMediaSinks(Connection connection);

    /**
     * Gets the specified media source executed by this endpoint and associated
     * with specified connection.
     * 
     * @param id the identifier of the media source
     * @param connection the connection with which this connection is associated
     * @return the media source instance.
     */
    protected abstract MediaSource getMediaSource(MediaResource id, Connection connection);

    /**
     * Gets the specified media sink executed by this endpoint and associated
     * with specified connection.
     * 
     * @param id the identifier of the media sink
     * @param connection the connection with which this connection is associated
     * @return the media source instance.
     */
    protected abstract MediaSink getMediaSink(MediaResource id, Connection connection);

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
    public void sendEvent(Runnable handler) {
        eventQueue.submit(handler);
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

    /**
     * Narrows specified format map to the list of required formats.
     * 
     * The result of the method execution is a map with a common subset of 
     * initial format map and required formats.
     * 
     * @param fmtMap - the map to narrow
     * @param formats - the list of required formats
     */
    protected void narrow(HashMap<Integer, Format> fmtMap, Format[] formats) {
        int count = 0;
        int[] trash = new int[fmtMap.size()];
        
        Set <Integer> payloads  = fmtMap.keySet();
        for (Integer payload : payloads) {
            Format fmt = fmtMap.get(payload);
            for (int i = 0; i < formats.length; i++) {
                if (fmt.equals(formats[i])) {
                    break;
                }
            }
            trash[count++] = payload;
        }
        
        for (int i = 0; i < count; i++) {
            fmtMap.remove(trash[i]);
        }
    }
    
    // ###############################
    // # MANAGEMENT FUNCTIONS        #
    // ###############################
    public long getCreationTime() {
        // TODO Auto-generated method stub
        return 0;
    }
    
    public int getConnectionsCount() {
        return connections.keySet().size();
    }

    public boolean getGatherPerformanceFlag() {
        return gatherStatistics;
    }

    public long getNumberOfBytes() {
        return numberOfBytes;
    }

    public long getPacketsCount() {
        return packets;
    }

    public void setGatherPerformanceData(boolean flag) {

        gatherStatistics = flag;
        //Make connections do this, this will reset all stats
        //This happens because even after break stats are inaccurate.
        for (Object c : connections.values()) {
            BaseConnection connection = (BaseConnection) c;
            connection.setGatherStats(flag);
        }
    }

    public long getConnectionCreationTime(String connectionId) throws IllegalArgumentException {
        BaseConnection connection = (BaseConnection) this.connections.get(connectionId);
        if (connection == null) {
            throw new IllegalArgumentException("Connection does not exist.");
        }
        return connection.getConnectionCreationTime();
    }

    public String[] getConnectionIds() {
        String[] tmp = (String[]) this.connections.keySet().toArray(new String[this.connections.keySet().size()]);
        return tmp;
    }

    public String getConnectionLocalSDP(String connectionId)
            throws IllegalArgumentException {
        BaseConnection connection = (BaseConnection) this.connections.get(connectionId);
        if (connection == null) {
            throw new IllegalArgumentException("Connection does not exist.");
        }
        return connection.getLocalDescriptor();
    }

    public String getConnectionRemoteSDP(String connectionId)
            throws IllegalArgumentException {
        BaseConnection connection = (BaseConnection) this.connections.get(connectionId);
        if (connection == null) {
            throw new IllegalArgumentException("Connection does not exist.");
        }
        return connection.getRemoteDescriptor();
    }

    public String getOtherEnd(String connectionId)
            throws IllegalArgumentException {
        BaseConnection connection = (BaseConnection) this.connections.get(connectionId);
        if (connection == null) {
            throw new IllegalArgumentException("Connection does not exist.");
        }
        return connection.getOtherEnd();
    }

    public String getConnectionState(String connectionId) throws IllegalArgumentException {
        BaseConnection connection = (BaseConnection) this.connections.get(connectionId);
        if (connection == null) {
            throw new IllegalArgumentException("Connection does not exist.");
        }
        return connection.getState().toString();
    }

    public String getConnectionMode(String connectionId) throws IllegalArgumentException {
        BaseConnection connection = (BaseConnection) this.connections.get(connectionId);
        if (connection == null) {
            throw new IllegalArgumentException("Connection does not exist.");
        }
        return connection.getMode().toString();
    }

    public void notifyEndpoint(Connection connection, ConnectionState oldState) {
        for (int index = 0; index < connectionListeners.size(); index++) {
            connectionListeners.get(index).onStateChange(connection, oldState);
        }

    }

    public void notifyEndpoint(Connection connection, ConnectionMode oldMode) {
        for (int index = 0; index < connectionListeners.size(); index++) {
            connectionListeners.get(index).onModeChange(connection, oldMode);
        }
    }


    public int getInterArrivalJitter(String connectionId)
            throws IllegalArgumentException {
        BaseConnection connection = (BaseConnection) this.connections.get(connectionId);
        if (connection == null) {
            throw new IllegalArgumentException("Connection does not exist.");
        }
        return connection.getInterArrivalJitter();
    }

    public int getOctetsReceived(String connectionId)
            throws IllegalArgumentException {
        BaseConnection connection = (BaseConnection) this.connections.get(connectionId);
        if (connection == null) {
            throw new IllegalArgumentException("Connection does not exist.");
        }
        return connection.getOctetsReceived();
    }

    public int getOctetsSent(String connectionId)
            throws IllegalArgumentException {
        BaseConnection connection = (BaseConnection) this.connections.get(connectionId);
        if (connection == null) {
            throw new IllegalArgumentException("Connection does not exist.");
        }
        return connection.getOctetsSent();
    }

    public int getPacketsLost(String connectionId)
            throws IllegalArgumentException {
        BaseConnection connection = (BaseConnection) this.connections.get(connectionId);
        if (connection == null) {
            throw new IllegalArgumentException("Connection does not exist.");
        }
        return connection.getPacketsLost();
    }

    public int getPacketsReceived(String connectionId)
            throws IllegalArgumentException {
        BaseConnection connection = (BaseConnection) this.connections.get(connectionId);
        if (connection == null) {
            throw new IllegalArgumentException("Connection does not exist.");
        }
        return connection.getPacketsReceived();
    }

    public int getPacketsSent(String connectionId) throws IllegalArgumentException {
        BaseConnection connection = (BaseConnection) this.connections.get(connectionId);
        if (connection == null) {
            throw new IllegalArgumentException("Connection does not exist.");
        }
        return connection.getPacketsSent();
    }


    private class CommandThreadFactory implements ThreadFactory {

        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r, "CommandThread[" + localName + "]");
            thread.setPriority(Thread.MIN_PRIORITY);
            return thread;
        }
    }

    private class EventQueueThreadFactory implements ThreadFactory {

        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r, "EventQueue[" + localName + "]");
            thread.setPriority(Thread.MIN_PRIORITY);
            return thread;
        }
    }

    private class ReceiverThreadFactory implements ThreadFactory {

        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r, "Receiver[" + localName + "]");
            thread.setPriority(Thread.NORM_PRIORITY);
            return thread;
        }
    }

    private class TransmittorThreadFactory implements ThreadFactory {

        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r, "Transmittor[" + localName + "]");
            thread.setPriority(Thread.MAX_PRIORITY);
            return thread;
        }
    }
}
