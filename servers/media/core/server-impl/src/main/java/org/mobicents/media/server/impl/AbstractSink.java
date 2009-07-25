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

import java.io.IOException;
import org.mobicents.media.Buffer;
import org.mobicents.media.MediaSink;
import org.mobicents.media.MediaSource;
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
    
    
    /**
     * Creates new instance of sink with specified name.
     * 
     * @param name the name of the sink to be created.
     */
    public AbstractSink(String name) {
        super(name);
        evtStarted = new NotifyEventImpl(this, NotifyEvent.STARTED);
        evtStopped = new NotifyEventImpl(this, NotifyEvent.STOPPED);
    }

    /**
     * (Non Java-doc).
     * 
     * @see org.mobicents.media.MediaSink#connect(MediaStream).
     */
    public void connect(MediaSource otherParty) {
        AbstractSource source = ((AbstractSource) otherParty);

        if (source.otherParty != this) {
            source.otherParty = this;
        }

        if (this.otherParty == null) {
            otherParty.connect(this);
        }
    }

    /**
     * (Non Java-doc).
     * 
     * @see org.mobicents.media.MediaSink#disconnect(MediaStream).
     */
    public void disconnect(MediaSource otherParty) {
        AbstractSource source = ((AbstractSource) otherParty);

        if (source.otherParty != null) {
            source.otherParty = null;
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
                onMediaTransfer(buffer);
                packetsReceived++;
                bytesReceived += buffer.getLength();
            } catch (Exception e) {
                failed(NotifyEvent.RX_FAILED, e);
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
