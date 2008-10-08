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
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Provides repited execution at a reqular time intervals.
 * 
 * @author Oleg Kulikov
 */
public class Timer {

	public final static Quartz quartz = new Quartz();
	private final static ExecutorService timerThreadPool = Executors.newCachedThreadPool(new Timer.ThreadFactoryImpl());;
	private Runnable handler;
	private boolean stopped = true;

	// private Semaphore semaphore = new Semaphore(0);

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
	public synchronized void start() {
		if (stopped) {
			stopped = false;
			quartz.addTimer(this);
		}
	}

	/**
	 * Terminates execution.
	 */
	public synchronized void stop() {
		if (!stopped) {
			quartz.removeTimer(this);
			stopped = true;
		}
	}

	/**
	 * Heart beat signals.
	 */
	public void heartBeat() {
		if (!stopped && timerThreadPool != null) {
			timerThreadPool.execute(handler);
		}
	}

	private static class ThreadFactoryImpl implements ThreadFactory {

		final ThreadGroup group;
		// This should always be a unique number. Only one Thread Pool
		static final AtomicInteger mmsTimerPoolNumber = new AtomicInteger(1);
		final AtomicInteger threadNumber = new AtomicInteger(1);
		final String namePrefix;

		ThreadFactoryImpl() {
			SecurityManager s = System.getSecurityManager();
			group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
			namePrefix = "MMSTimer-CachedThreadPool-" + mmsTimerPoolNumber.getAndIncrement() + "-thread-";
		}

		public Thread newThread(Runnable r) {
			Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
			if (t.isDaemon())
				t.setDaemon(false);
			// if (t.getPriority() != Thread.NORM_PRIORITY)
			t.setPriority(Thread.MAX_PRIORITY);
			return t;
		}

	}
}
