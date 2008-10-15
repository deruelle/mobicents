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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;

/**
 * Used as quartz controller for timekeeping.
 * 
 * @author Oleg Kulikov
 */
public class Quartz implements Serializable {

    /** The interval between heart beat time series in milliseconds */
    public static int HEART_BEAT = 20;
    private static int JITTER = HEART_BEAT / 2;
    
    private boolean active = false;
    private List<Timer> timers = Collections.synchronizedList(new ArrayList());

    private java.util.Timer q = new java.util.Timer();
    private TimerTask sync = new Sync();
    
    private ScheduledExecutorService quartz = Executors.newScheduledThreadPool(150);
    
    protected boolean testMode = false;
    protected long errors = 0;
    private long time;
    private long now;
    private long dur;
    
    protected Condition heartBeat;
    
    /**
     * Creates new instance of Quartz.
     */
    protected Quartz() {
        q.scheduleAtFixedRate(sync, HEART_BEAT, HEART_BEAT);
    }

    private class Sync extends TimerTask {

        public void run() {
            heartBeat();
        }
    }

    /**
     * Adds timer instance which will be synchronized from this quartz.
     * 
     * @param listener the timer to synchronize
     */
    protected void addTimer(Timer timer) {
        synchronized(timer) {
            timers.add(timer);
        }
        if (!active) {
            active = true;
        }
    }

    /**
     * Removes timer..
     * 
     * @param listener the timer instance
     */
    public void removeTimer(Timer timer) {
        synchronized (timers) {
            timers.remove(timer);
        }
        if (timers.size() == 0) {
            active = false;
        }
    }

    /**
     * Send heat beat impulse to all timers.
     * 
     */
    private void heartBeat() {
        synchronized (timers) {
            for (Timer timer : timers) {
                timer.heartBeat();
            }
        }
        
        //Diagnostic block.
        now = System.currentTimeMillis();
        
        if (testMode) {
           dur = time - now; 
           if ((dur - HEART_BEAT) > JITTER) {
               errors++;
           }
        }
        
        time = now;
    }

}
