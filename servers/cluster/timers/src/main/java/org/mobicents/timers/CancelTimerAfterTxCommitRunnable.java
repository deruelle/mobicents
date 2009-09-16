package org.mobicents.timers;

import java.io.Serializable;

import org.apache.log4j.Logger;


/**
 * Runnable to cancel a timer task after the tx commits.
 * @author martins
 *
 */
public class CancelTimerAfterTxCommitRunnable implements Runnable {

	private static final Logger logger = Logger.getLogger(CancelTimerAfterTxCommitRunnable.class);
	
	private final FaultTolerantScheduler executor;	
	private TimerTask task;

	CancelTimerAfterTxCommitRunnable(TimerTask task,FaultTolerantScheduler executor) {
		this.task = task;
		this.executor = executor;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		
		final TimerTaskData taskData = task.getData();
		final Serializable taskID = taskData.getTaskID();
		
		if (logger.isDebugEnabled()) {
			logger.debug("Cancelling timer task for timer ID "+taskID);
		}
		
		executor.getLocalRunningTasksMap().remove(taskID);
		
		try {
			task.getScheduledFuture().cancel(false);		
		}
		catch (Throwable e) {
			logger.error(e.getMessage(),e);
		}
	}
	
}
