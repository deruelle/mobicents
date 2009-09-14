package org.mobicents.timers.timer;


import org.mobicents.timers.FaultTolerantScheduler;
import org.mobicents.timers.TimerTask;
import org.mobicents.timers.TimerTaskData;

/**
 * 
 * @author martins
 *
 */
public class FaultTolerantTimerTimerTaskFactory implements org.mobicents.timers.TimerTaskFactory {
	
	private FaultTolerantScheduler scheduler;
	
	/*
	 * (non-Javadoc)
	 * @see org.mobicents.slee.core.timers.TimerTaskFactory#newTimerTask(org.mobicents.slee.core.timers.TimerTaskData)
	 */
	public TimerTask newTimerTask(TimerTaskData data) {
		if (scheduler == null) {
			throw new IllegalStateException("unable to create data, scheduler is not set");
		}
		return new FaultTolerantTimerTimerTask((org.mobicents.timers.timer.FaultTolerantTimerTimerTaskData) data,scheduler);
	}

	/**
	 *  
	 * @param scheduler the scheduler to set
	 */
	public void setScheduler(FaultTolerantScheduler scheduler) {
		this.scheduler = scheduler;
	}
	
}
