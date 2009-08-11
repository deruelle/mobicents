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
import org.apache.log4j.Logger;
import org.mobicents.media.Buffer;
import org.mobicents.media.BufferFactory;
import org.mobicents.media.Format;
import org.mobicents.media.Inlet;
import org.mobicents.media.MediaSink;
import org.mobicents.media.MediaSource;
import org.mobicents.media.server.spi.Timer;
import org.mobicents.media.server.spi.events.NotifyEvent;

/**
 * The base implementation of the Media source.
 * 
 * <code>AbstractSource</code> and <code>AbstractSink</code> are implement general wirring contruct. All media
 * components have to extend one of these classes.
 * 
 * @author Oleg Kulikov
 */
public abstract class AbstractSource extends BaseComponent implements MediaSource, Runnable {

    protected transient MediaSink otherParty;
    private Timer timer;
    private volatile ScheduledFuture thread;
    private ReentrantLock state = new ReentrantLock();
    private BufferFactory bufferFactory = new BufferFactory(1);
    private long sequenceNumber;
    private long packetsTransmitted;
    private long bytesTransmitted;
    private boolean started;
    private NotifyEvent evtStarted;
    private NotifyEvent evtCompleted;
    private NotifyEvent evtStopped;
    private boolean warn;
    private Logger logger;
    private boolean completed = false;

    /**
     * Creates new instance of source with specified name.
     * 
     * @param name
     *            the name of the sink to be created.
     */
    public AbstractSource(String name) {
        super(name);
        logger = Logger.getLogger(getClass());
        evtStarted = new NotifyEventImpl(this, NotifyEvent.STARTED);
        evtCompleted = new NotifyEventImpl(this, NotifyEvent.COMPLETED);        
        evtStopped = new NotifyEventImpl(this, NotifyEvent.STOPPED);
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
    protected void beforeStart() throws Exception {
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

            // if timer assigned (synchronized from timer) then start timer
            // if synchronized from other component just exit silently
            if (timer != null) {
                thread = timer.synchronize(this);
            }
            started = true;
            if (logger.isDebugEnabled()) {
                logger.debug(this + " started");
            }
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
            if (logger.isDebugEnabled()) {
                logger.debug(this + " stopped");
            }
            stopped();
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

    public boolean isMultipleConnectionsAllowed() {
        return false;
    }

    /**
     * (Non Java-doc).
     * 
     * @see org.mobicents.media.MediaSource#connect(MediaSink).
     */
    public void connect(MediaSink otherParty) {
        // check argument
        if (otherParty == null) {
            throw new IllegalArgumentException("Other party can not be null");
        }

        if (otherParty instanceof AbstractSink && !otherParty.isMultipleConnectionsAllowed()) {
            AbstractSink sink = (AbstractSink) otherParty;
            sink.otherParty = this;
            this.otherParty = sink;
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
     * @see org.mobicents.media.MediaSource#diconnection(MediaSink).
     */
    public void disconnect(MediaSink otherParty) {
        // check argument
        if (otherParty == null) {
            throw new IllegalArgumentException("Other party can not be null");
        }

        if (otherParty instanceof AbstractSink && !otherParty.isMultipleConnectionsAllowed()) {
            if (otherParty == this.otherParty) {
                AbstractSink sink = (AbstractSink) otherParty;
                sink.otherParty = null;
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

    public void connect(Inlet inlet) {
        connect(inlet.getInput());
    }

    public void disconnect(Inlet inlet) {
        disconnect(inlet.getInput());
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
     * This method must be overriden by concrete media source. The media have to fill buffer with media data and
     * attributes.
     * 
     * @param buffer
     *            the buffer object for media.
     * @param sequenceNumber
     *            the number of timer ticks from the begining.
     */
    public abstract void evolve(Buffer buffer, long sequenceNumber);

    protected String getSupportedFormatList() {
        String s = "";
        Format[] formats = otherParty.getFormats();
        for (int i = 0; i < formats.length; i++) {
            s += formats[i] + ";";
        }
        return s;
    }

    public void run() {
        Buffer buffer = bufferFactory.allocate();
        try {
            evolve(buffer, sequenceNumber);
        } catch (Exception e) {
            logger.error("Not able to evolve data", e);
            return;
        }

        if (buffer.isDiscard()) {
            buffer.dispose();
            return;
        }

        if (buffer.isEOM()) {
            buffer.dispose();
            completed();
            return;
        }

        sequenceNumber++;

        boolean isAcceptable = false;
        try {
            isAcceptable = otherParty.isAcceptable(buffer.getFormat());
        } catch (Exception e) {
        }

        if (isAcceptable) {
            try {
                if (logger.isTraceEnabled()) {
                    logger.trace(this + " sending " + buffer + " to " + otherParty);
                }

                otherParty.receive(buffer);
                packetsTransmitted++;
                bytesTransmitted += buffer.getLength();
                warn = false;
            } catch (Exception e) {
                logger.error("Can not deliver packet to " + otherParty, e);
                failed(NotifyEvent.TX_FAILED, e);
            }
        } else {
            if (!warn) {
                logger.warn(this + " fmt={" + buffer.getFormat() + "} is not acceptable by " + otherParty + "supported formats: [" + getSupportedFormatList() + "]");
                warn = true;
            }
        }

    }

    /**
     * Sends notification that media processing has been started.
     */
    protected void started() {
        sendEvent(evtStarted);
    }

    /**
     * Sends failure notification.
     * 
     * @param eventID
     *            failure event identifier.
     * @param e
     *            the exception caused failure.
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
        sendEvent(evtCompleted);
    }

    /**
     * Sends notification that detection is terminated.
     * 
     */
    protected void stopped() {
        sendEvent(evtStopped);
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
