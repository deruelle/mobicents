/*
 * Mobicents, Communications Middleware
 * 
 * Copyright (c) 2008, Red Hat Middleware LLC or third-party
 * contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Middleware LLC.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 *
 * Boston, MA  02110-1301  USA
 */
package org.mobicents.media.server.impl;

import java.io.IOException;
import org.apache.log4j.Logger;
import org.mobicents.media.Buffer;
import org.mobicents.media.MediaSink;
import org.mobicents.media.MediaSource;
import org.mobicents.media.Outlet;
import org.mobicents.media.server.spi.events.NotifyEvent;

/**
 * The base implementation of the media sink.
 * 
 * <code>AbstractSource</code> and <code>AbstractSink</code> are implement 
 * general wirring contruct. 
 * All media components have to extend one of these classes.
 * 
 * @author Oleg Kulikov
 */
public abstract class AbstractSink extends BaseComponent implements MediaSink {

    protected transient MediaSource otherParty;
    private volatile boolean started = false;
    
    private long packetsReceived;
    private long bytesReceived;
    
    private NotifyEvent evtStarted;
    private NotifyEvent evtStopped;
    
    private Logger logger;
    /**
     * Creates new instance of sink with specified name.
     * 
     * @param name the name of the sink to be created.
     */
    public AbstractSink(String name) {
        super(name);
        logger = Logger.getLogger(getClass());
        evtStarted = new NotifyEventImpl(this, NotifyEvent.STARTED);
        evtStopped = new NotifyEventImpl(this, NotifyEvent.STOPPED);
    }

    public boolean isMultipleConnectionsAllowed() {
        return false;
    }
    
    /**
     * (Non Java-doc).
     * 
     * @see org.mobicents.media.MediaSink#connect(MediaStream).
     */
    public void connect(MediaSource otherParty) {
        if (otherParty == null) {
            throw new IllegalArgumentException("Other party can not be null");
        }
        
        if (otherParty instanceof AbstractSource && !otherParty.isMultipleConnectionsAllowed()) {
            AbstractSource source = ((AbstractSource) otherParty);
            source.otherParty = this;
            this.otherParty = source;
            
            if (logger.isDebugEnabled()) {
                logger.debug(this + " is connected to " + otherParty);
            }
        } else {
            otherParty.connect(this);
        }
    }

    /**
     * (Non Java-doc).
     * 
     * @see org.mobicents.media.MediaSink#disconnect(MediaStream).
     */
    public void disconnect(MediaSource otherParty) {
        if (otherParty == null) {
            throw new IllegalArgumentException("Other party can not be null");
        }
        
        if (otherParty instanceof AbstractSource && !otherParty.isMultipleConnectionsAllowed()) {
            if (otherParty == this.otherParty) {
                AbstractSource source = ((AbstractSource) otherParty);
                source.otherParty = null;
                this.otherParty = null;
                if (logger.isDebugEnabled()) {
                    logger.debug(this + " is disconnected from " + otherParty);
                }
            } else {
                throw new IllegalArgumentException(otherParty + " was not connected to " + this);
            }
        } else {
            otherParty.disconnect(this);
        }
    }

    
    public void connect(Outlet outlet) {
        connect(outlet.getOutput());
    }

    public void disconnect(Outlet outlet) {
        disconnect(outlet.getOutput());
    }        
    
    /**
     * (Non Java-doc).
     * 
     * @see org.mobicents.media.MediaSink#isConnected().
     */
    public boolean isConnected() {
        return otherParty != null;
    }
    

    /**
     * (Non Java-doc).
     * 
     * @see org.mobicents.media.MediaSink#isStarted().
     */
    public boolean isStarted() {
        return this.started;
    }
    
    /**
     * This methos is called when new portion of media arrives.
     * 
     * @param buffer the new portion of media data.
     */
    public abstract void onMediaTransfer(Buffer buffer) throws IOException;
    
    /**
     * (Non Java-doc).
     * 
     * @see org.mobicents.media.MediaSink#start().
     */
    public void start() {
        started = true;
        resetStats();
        started();
    }
    
    /**
     * (Non Java-doc).
     * 
     * @see org.mobicents.media.MediaSink#stop().
     */
    public void stop() {
        started = false;
        stopped();
    }
    
    /**
     * (Non Java-doc).
     * 
     * @see org.mobicents.media.MediaSink#receive().
     */
    public void receive(Buffer buffer) throws IOException {
        if (started) {
            try {
                if (logger.isTraceEnabled()) {
                    logger.trace(this + " arrive " + buffer + " from " + otherParty);
                }
                onMediaTransfer(buffer);
                packetsReceived++;
                bytesReceived += buffer.getLength();
            } catch (Exception e) {
                failed(NotifyEvent.RX_FAILED, e);
            e.printStackTrace();
            } finally {
                buffer.dispose();
            }
        }
    }
    
    /**
     * Sends failure notification.
     * 
     * @param eventID failure event identifier.
     * @param e the exception caused failure.
     */
    protected void failed(int eventID, Exception e) {
        FailureEventImpl failed = new FailureEventImpl(this, eventID, e);
        sendEvent(failed);
    }
    
    /**
     * (Non Java-doc).
     * 
     * @see org.mobicents.media.MediaSink#getPacketsReceived().
     */
    public long getPacketsReceived() {
        return packetsReceived;
    }
    
    /**
     * (Non Java-doc).
     * 
     * @see org.mobicents.media.MediaSink#getBytesReceived() 
     */
    public long getBytesReceived() {
        return bytesReceived;
    }
        
    @Override
    public void resetStats() {
        this.packetsReceived = 0;
        this.bytesReceived = 0;
    }
    
    /**
     * Sends notification that media processing has been started.
     */
    protected void started() {
        sendEvent(evtStarted);
    }
    
    /**
     * Sends notification that detection is terminated.
     * 
     */
    protected void stopped() {
        sendEvent(evtStopped);
    }
    
}
