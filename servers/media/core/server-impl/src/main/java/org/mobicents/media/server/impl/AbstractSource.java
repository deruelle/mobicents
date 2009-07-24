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

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.locks.ReentrantLock;
import org.mobicents.media.Buffer;
import org.mobicents.media.BufferFactory;
import org.mobicents.media.MediaSink;
import org.mobicents.media.MediaSource;
import org.mobicents.media.server.spi.Timer;
import org.mobicents.media.server.spi.events.NotifyEvent;

/**
 * The base implementation of the Media source.
 * 
 * <code>AbstractSource</code> and <code>AbstractSink</code> are implement 
 * general wirring contruct. 
 * All media components have to extend one of these classes.
 * 
 * @author Oleg Kulikov
 */
public abstract class AbstractSource extends BaseComponent 
        implements MediaSource, Runnable {

    protected transient MediaSink otherParty;
    private Timer timer;
    
    private volatile ScheduledFuture thread;
    private ReentrantLock state = new ReentrantLock();
    
    private BufferFactory bufferFactory = new BufferFactory(1);
    private long sequenceNumber;
        
    private long packetsTransmitted;
    private long bytesTransmitted;
    
    private boolean started;
    
    /**
     * Creates new instance of source with specified name.
     * 
     * @param name the name of the sink to be created.
     */
    public AbstractSource(String name) {
        super(name);
    }

    /**
     * (Non Java-doc).
     * 
     * @see org.mobicents.media.MediaSource#getSyncSource()
     */
    public Timer getSyncSource() {
        return timer;
    }

    /**
     * (Non Java-doc).
     * 
     * @see org.mobicents.media.MediaSource#setSyncSource(Timer)
     */
    public void setSyncSource(Timer timer) {
        this.timer = timer;
    }
    
    /**
     * This method is called just before start.
     * 
     * The descendant classes can verride this method and put additional logic
     */
    protected void beforeStart() throws Exception  {
    }
    
    /**
     * (Non Java-doc).
     * 
     * @see org.mobicents.media.MediaSource#start().
     */
    public void start() {
        state.lock();
        try {
            if (thread != null) {
                return;
            }
            
            if (otherParty == null) {
                throw new IllegalStateException("Component is not connected: " + this);
            }
            
            sequenceNumber = 0;
            beforeStart();
            
            //if timer assigned (synchronized from timer) then start timer
            //if synchronized from other component just exit silently
            if (timer != null) {
                thread = timer.synchronize(this);
            }
            started = true;
            started();
        } catch (Exception e) {
            failed(NotifyEvent.START_FAILED, e);
        } finally {
            state.unlock();
        }
    }    

    /**
     * (Non Java-doc).
     * 
     * @see org.mobicents.media.MediaSource#stop().
     */
    public void stop() {
        state.lock();
        try {
            if (thread != null) {
                thread.cancel(false);
                thread = null;
            }
            started = false;
            afterStop();
        } finally {
            state.unlock();
        }
    }    
    
    /**
     * This method is called immediately after processing termination.
     * 
     */
    public void afterStop() {
    }
    /**
     * (Non Java-doc).
     * 
     * @see org.mobicents.media.MediaSource#connect(MediaSink).
     */
    public void connect(MediaSink otherParty) {
        AbstractSink sink = (AbstractSink) otherParty;
        if (sink.otherParty == null) {
            sink.otherParty = this;
        }

        if (this.otherParty == null) {
            otherParty.connect(this);
        }
    }

    /**
     * (Non Java-doc).
     * 
     * @see org.mobicents.media.MediaSource#diconnection(MediaSink).
     */
    public void disconnect(MediaSink otherParty) {
        AbstractSink sink = (AbstractSink) otherParty;
        
        if (sink.otherParty != null) {
            sink.otherParty = null;
        } else {
        }

        if (this.otherParty != null) {
            otherParty.disconnect(this);
        }
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
     * This method must be overriden by concrete media source. The media have to 
     * fill buffer with media data and attributes.
     * 
     * @param buffer  the buffer object for media.
     * @param sequenceNumber the number of timer ticks from the begining.
     */
    public abstract void evolve(Buffer buffer, long sequenceNumber);
    
    public void run() {
        Buffer buffer = bufferFactory.allocate();        
        evolve(buffer, sequenceNumber++);
        
        if (buffer.isDiscard()) {
            return;
        }
        
        if (otherParty.isAcceptable(buffer.getFormat())) {
            try {
                otherParty.receive(buffer);                
                packetsTransmitted++;
                bytesTransmitted += buffer.getLength();
            } catch (Exception e) {
                failed(NotifyEvent.TX_FAILED, e);
            }
        } 
            
        if (buffer.isEOM()) {
            completed();
        }
    }

    /**
     * Sends notification that media processing has been started.
     */
    protected void started() {
        NotifyEventImpl started = new NotifyEventImpl(this, NotifyEventImpl.STARTED);
        sendEvent(started);
    }
    
    /**
     * Sends failure notification.
     * 
     * @param eventID failure event identifier.
     * @param e the exception caused failure.
     */
    protected void failed(int eventID, Exception e) {
        if (thread != null) {
            thread.cancel(false);
        }
        FailureEventImpl failed = new FailureEventImpl(this, eventID, e);
        sendEvent(failed);
    }

    /**
     * Sends notification that signal is completed.
     * 
     */
    protected void completed() {
        if (thread != null) {
            thread.cancel(false);
        }
        NotifyEventImpl completed = new NotifyEventImpl(this, NotifyEventImpl.COMPLETED);
        sendEvent(completed);
    }
    
    /**
     * (Non Java-doc).
     * 
     * @see org.mobicents.media.MediaSource#getPacketsReceived() 
     */
    public long getPacketsTransmitted() {
        return packetsTransmitted;
    }
    
    /**
     * (Non Java-doc).
     * 
     * @see org.mobicents.media.MediaSource#getBytesTransmitted() 
     */
    public long getBytesTransmitted() {
        return bytesTransmitted;
    }

    @Override
    public void resetStats() {
        this.packetsTransmitted = 0;
        this.bytesTransmitted = 0;
    }
    
}
