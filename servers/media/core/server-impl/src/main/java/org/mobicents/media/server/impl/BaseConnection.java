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
package org.mobicents.media.server.impl;

import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantLock;
import org.jboss.util.id.UID;
import org.mobicents.media.server.local.management.ConnectionLocalManagement;
import org.mobicents.media.server.spi.Connection;
import org.mobicents.media.server.spi.ConnectionListener;
import org.mobicents.media.server.spi.ConnectionMode;
import org.mobicents.media.server.spi.ConnectionState;
import org.mobicents.media.server.spi.Endpoint;
import org.mobicents.media.server.spi.NotificationListener;
import org.mobicents.media.server.spi.ResourceUnavailableException;
import org.mobicents.media.server.spi.events.NotifyEvent;

/**
 * 
 * @author Oleg Kulikov
 */
public abstract class BaseConnection implements Connection, NotificationListener, ConnectionLocalManagement {

    protected String id;
    private int index;
    
    protected BaseEndpoint endpoint;
    protected String endpointName;
    protected ConnectionMode mode;
    protected ConnectionState state;
    private int lifeTime = 30;
    private boolean timerStarted = false;
    private ReentrantLock stateLock = new ReentrantLock();
    private List<ConnectionListener> listeners = new CopyOnWriteArrayList();
//    private List<RequestedEvent> eventListeners = new CopyOnWriteArrayList();
    
    private transient TimerTask closeTask;
    private long connectionCreationTime = System.currentTimeMillis();
    private long packets;
    
    private class CloseConnectionTask extends TimerTask {
        public void run() {
            timerStarted = false;
            endpoint.deleteConnection(id);
        }
    }

    public BaseConnection(Endpoint endpoint, ConnectionMode mode) throws ResourceUnavailableException {
        this.id = genID();
        this.mode = mode;

        this.endpoint = (BaseEndpoint) endpoint;
        this.endpointName = endpoint.getLocalName();

        this.endpoint.connections.put(id, this);
        this.index = this.endpoint.getIndex();
        setState(ConnectionState.NULL);
        
        this.endpoint.allocateMediaSinks(this);        
        
        setLifeTime(lifeTime);
        setState(ConnectionState.NULL);
    }

    /**
     * (Non-Javadoc).
     * 
     * @see org.mobicents.media.server.spi.Connection#getId();
     */
    public String getId() {
        return id;
    }

    public int getIndex() {
        return index;
    }
    
    /**
     * Gets the current state of this connection.
     * 
     * @return current state;
     */
    public ConnectionState getState() {
        return this.state;
    }

    /**
     * Non Java-doc).
     * 
     * @see org.mobicents.media.server.spi.Connection#getLifeTime().
     */
    public int getLifeTime() {
        return lifeTime;
    }

    /**
     * Non Java-doc).
     * 
     * @see org.mobicents.media.server.spi.Connection#setLifeTime(int).
     */
    public void setLifeTime(int lifeTime) {
        this.lifeTime = lifeTime;
    }

    /**
     * Generates unique identifier for this connection.
     * 
     * @return hex view of the unique integer.
     */
    private String genID() {
        return (new UID()).toString();
    }

    /**
     * (Non-Javadoc).
     * 
     * @see org.mobicents.media.server.spi.Connection#getMode();
     */
    public ConnectionMode getMode() {
        return mode;
    }

    /**
     * (Non-Javadoc).
     * 
     * @see org.mobicents.media.server.spi.Connection#setMode(int);
     */
    public void setMode(ConnectionMode mode) {
        if (mode == this.mode) {
            return;
        }

        ConnectionMode oldMode = this.mode;
        this.mode = mode;

        endpoint.notifyEndpoint(this, oldMode);
        for (ConnectionListener cl : listeners) {
            cl.onModeChange(this, oldMode);
        }

    }

    /**
     * (Non-Javadoc).
     * 
     * @see org.mobicents.media.server.spi.Connection#getEndpoint(int);
     */
    public Endpoint getEndpoint() {
        return endpoint;
    }


    /**
     * (Non-Javadoc).
     * 
     * @see org.mobicents.media.server.spi.Connection#addListener(ConnectionListener)
     */
    public void addListener(ConnectionListener listener) {
        listeners.add(listener);
    }

    /**
     * (Non-Javadoc).
     * 
     * @see org.mobicents.media.server.spi.Connection#removeListener(ConnectionListener)
     */
    public void removeListener(ConnectionListener listener) {
        listeners.remove(listener);
    }

    protected void setState(ConnectionState newState) {
        if (newState == this.state) {
            return;
        }

        ConnectionState oldState = this.state;
        this.state = newState;


        if (state != ConnectionState.CLOSED) {
            int timeout = getTimeout(newState);
            scheduleTermination(timeout);
        }

        endpoint.notifyEndpoint(this, oldState);
        for (ConnectionListener cl : listeners) {
            cl.onStateChange(this, oldState);
        }

    }

    public void update(NotifyEvent event) {
    }

    /**
     * Used for test cases only.
     * 
     * @return true if life time started.
     */
    protected boolean getLifeTimeTimerState() {
        return timerStarted;
    }

    /**
     * Releases all resources requested by this connection.
     * 
     * @throws InterruptedException
     */
    protected void close() {
        if (timerStarted) {
            closeTask.cancel();
            timerStarted = false;
            BaseEndpoint.stateTimer.purge();
        }
        
        endpoint.releaseIndex(index);
        endpoint.releaseMediaSinks(this);
        endpoint.releaseMediaSources(this);
        
        try {
            this.setState(ConnectionState.CLOSED);
        } finally {
            endpoint.connections.remove(id);
        }
    }

    /**
     * Gets time out for specified state.
     * 
     * @param state the state identifier
     * @return the value of timeout in seconds
     */
    private int getTimeout(ConnectionState state) {
        switch (state) {
            case OPEN:
                return this.endpoint.connectionLifeTime * 60;
            default:
                return 10;
        }
    }

    /**
     * Schedules connection termination due to timeout or lifetime expire
     * 
     * @param timeout the value of timeout in seconds
     */
    private void scheduleTermination(int timeout) {
        //cancel previous termination task if started
        if (timerStarted) {
            closeTask.cancel();
            BaseEndpoint.stateTimer.purge();
            timerStarted = false;
        }

        //schedule new termination task
        this.closeTask = new CloseConnectionTask();
        BaseEndpoint.stateTimer.schedule(closeTask, timeout * 1000);
        timerStarted = true;
    }

    public void lockState() throws InterruptedException {
        this.stateLock.lock();
    }

    public void releaseState() {
        this.stateLock.unlock();
    }

   

    public long getConnectionCreationTime() throws IllegalArgumentException {
        return connectionCreationTime;
    }

    public long getNumberOfPackets() throws IllegalArgumentException {
        return packets;
    }
}
