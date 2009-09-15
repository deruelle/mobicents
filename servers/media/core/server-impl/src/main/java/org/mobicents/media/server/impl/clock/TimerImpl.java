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
package org.mobicents.media.server.impl.clock;

import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.mobicents.media.MediaSource;
import org.mobicents.media.server.spi.Timer;



/**
 * Provides repited execution at a reqular time intervals.
 * 
 * @author Oleg Kulikov
 */
public class TimerImpl implements Timer {

    public static final int _DEFAULT_T_PRIORITY = Thread.MAX_PRIORITY;
    
    private transient final ScheduledExecutorService timer = 
            Executors.newSingleThreadScheduledExecutor(new MMSClockThreadFactory());    
    private int heartBeat = 20;
    private HashMap<String, ScheduledFuture> controls = new HashMap();
    
    private volatile long timestamp;
    private Clock clock = new Clock();
    private ScheduledFuture clockControl;
    
    /**
     * Creates new instance of the timer.
     */
    public TimerImpl() {
    }

    /**
     * Gets value of interval between timer ticks.
     * 
     * @return the int value in milliseconds.
     */
    public int getHeartBeat() {
        return heartBeat;
    }

    /**
     * Modify interval between timer tick
     * 
     * @param heartBeat
     *            the new value of interval in milliseconds.
     */
    public void setHeartBeat(int heartBeat) {
        this.heartBeat = heartBeat;
    }

    /**
     * (Non Java-doc.)
     * 
     * @see org.mobicents.media.server.spi.Timer#getTimestamp() 
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * (Non Java-doc.)
     * 
     * @see org.mobicents.media.server.spi.Timer#sync(org.mobicents.media.MediaSource) 
     */
    public void sync(MediaSource mediaSource) throws IllegalArgumentException {
        if (mediaSource.getPeriod() > 0) {
            //period have to be multiple to timer's heard beat.
            //so we are recalculating the actual packetization period.
            int period = (mediaSource.getPeriod() / heartBeat) * heartBeat;
            ScheduledFuture control = timer.scheduleAtFixedRate(mediaSource, 0, period, TimeUnit.MILLISECONDS);
            controls.put(mediaSource.getId(), control);
        } else throw new IllegalArgumentException(mediaSource + " can not be synchronized from this source");
    }

    /**
     * (Non Java-doc.)
     * 
     * @see org.mobicents.media.server.spi.Timer#unsync(org.mobicents.media.MediaSource) 
     */
    public void unsync(MediaSource mediaSource) {
        ScheduledFuture control = controls.remove(mediaSource.getId());
        if (control != null) {
            control.cancel(false);
        }
    }
    
    /**
     * (Non Java-doc.)
     * 
     * @see org.mobicents.media.server.spi.Timer#start() 
     */
    public void start() {
        if (clockControl == null || clockControl.isCancelled()) {
            clockControl = timer.scheduleAtFixedRate(clock, heartBeat, heartBeat, TimeUnit.MILLISECONDS);
        }
    }
    
    /**
     * (Non Java-doc.)
     * 
     * @see org.mobicents.media.server.spi.Timer#stop() 
     */
    public void stop() {
        if (clockControl != null && !clockControl.isCancelled()) {
            clockControl.cancel(false);
        }
    }
    
    private class Clock implements Runnable {
        public void run() {
            timestamp += heartBeat;
        }
    }    
}

class MMSClockThreadFactory implements ThreadFactory {

    public static final AtomicLong sequence = new AtomicLong(0);
    private ThreadGroup factoryThreadGroup = new ThreadGroup("MMSClockThreadGroup[" + sequence.incrementAndGet() + "]");

    public Thread newThread(Runnable r) {
        Thread t = new Thread(this.factoryThreadGroup, r);
        t.setPriority(TimerImpl._DEFAULT_T_PRIORITY);
        // ??
        //t.start();
        return t;
    }
}