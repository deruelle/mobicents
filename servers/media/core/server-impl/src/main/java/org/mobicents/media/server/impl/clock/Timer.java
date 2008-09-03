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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * Provides repited execution at a reqular time intervals.
 * 
 * @author Oleg Kulikov
 */
public class Timer {

    public final static Quartz quartz = new Quartz();
    private final static ExecutorService threadPool = Executors.newCachedThreadPool();
    
    private Runnable handler;
    private boolean stopped = true;
    private Semaphore semaphore = new Semaphore(0);

    /**
     * Creates new instance of the timer.
     */
    public Timer() {        
    }

    public void setListener(Runnable handler) {
        this.handler = handler;
    }

    /**
     * Starts execution;
     */
    public void start() {
        stopped = false;
        quartz.addTimer(this);
    }

    /**
     * Terminates execution.
     */
    public void stop() {
        quartz.removeTimer(this);
        stopped = true;
    }


    /**
     * Heart beat signals.
     */
    public void heartBeat() {
        if (!stopped && threadPool != null)
          threadPool.execute(handler);
    }
}
