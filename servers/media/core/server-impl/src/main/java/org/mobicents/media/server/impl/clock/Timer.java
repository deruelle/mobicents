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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Provides repited execution at a reqular time intervals.
 * 
 * @author Oleg Kulikov
 */
public class Timer implements Serializable, Runnable {

    private final static int CORE_POOL_SIZE = 25;
    
    private transient static ScheduledThreadPoolExecutor threadPool = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(10);
    private transient static TimerThreadFactory threadFactory = new TimerThreadFactory();
    
    private transient ScheduledFuture task;
    private transient TimerTask handler;

    static {
        threadPool.setThreadFactory(threadFactory);
        threadPool.setCorePoolSize(CORE_POOL_SIZE);
        threadPool.prestartAllCoreThreads();
    }
    /**
     * Creates new instance of the timer.
     */
    public Timer() {
    }


    /**
     * Starts execution;
     */
	private volatile boolean stopped = true;
	private transient Thread worker;
    /**
     * Terminates execution.
     */
	private long lastTick;
	private long next;

	public static AtomicInteger threadNumber = new AtomicInteger(1);

	/**
	 * Creates new instance of the timer.
	 */

	public void setListener(TimerTask handler) {
		this.handler = handler;
	}

	/**
	 * Starts execution;
	 */
	public void start() {
		if (stopped) {
			worker = new Thread(this, "MediaTimer-" + threadNumber.getAndIncrement());
			worker.setPriority(Thread.MAX_PRIORITY);
			stopped = false;
			lastTick = System.currentTimeMillis();
			worker.start();
		}
	}

	/**
	 * Terminates execution.
	 */
	public void stop() {
		if (worker != null) {
			worker.interrupt();
		}
		if (!stopped) {
			stopped = true;
		}
	}

	/**
	 * Heart beat signals.
	 */
	public void heartBeat() {
	}

	@SuppressWarnings("static-access")
	private void await(long t1, long t2) {
		long delay = Quartz.HEART_BEAT - (t2 - t1);
		if (delay < 0) {
			delay = 0;
		}
		try {
			Thread.currentThread().sleep(delay);
		} catch (InterruptedException e) {
			stopped = true;
		}
	}

	@SuppressWarnings("static-access")
	private void await() {
		long now = System.currentTimeMillis();
		long dif = next - now;

		if (dif < 0) {
			dif = 0;
		}

		try {
			Thread.currentThread().sleep(dif);
		} catch (InterruptedException e) {
			stopped = true;
		}

		next += Quartz.HEART_BEAT;
	}

	public void run() {
		next = System.currentTimeMillis() + Quartz.HEART_BEAT;

		if (handler != null) {
			handler.started();
		}

		while (!stopped) {
			// long t1 = System.currentTimeMillis();
			if (handler != null) {
				handler.run();
			}
			// long t2 = System.currentTimeMillis();
			// await(t1, t2);
			await();
		}

		if (handler != null) {
			handler.ended();
		}

	}

    public void run2() {
        handler.run();
    }
    
}
