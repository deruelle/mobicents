package org.mobicents.timers.timer;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import javax.transaction.TransactionManager;

import org.mobicents.cluster.MobicentsCluster;
import org.mobicents.timers.FaultTolerantScheduler;
import org.mobicents.timers.PeriodicScheduleStrategy;

/**
 * A fault tolerant implementation of a {@link Timer}, using the
 * {@link FaultTolerantScheduler}.
 * 
 * There is no guarantee that fail over recover works correctly with not serializable {@link TimerTask}s
 * 
 * @author martins
 * 
 */
public class FaultTolerantTimer extends java.util.Timer {

	/**
	 * 
	 */
	private final FaultTolerantScheduler scheduler;
	
	/**
	 * 
	 */
	private final FaultTolerantTimerTimerTaskFactory timerTaskFactory;
	
	/**
	 * 
	 * @param cacheData
	 * @param txManager
	 */
	public FaultTolerantTimer(String name, MobicentsCluster cluster, byte priority, TransactionManager txManager) {
		timerTaskFactory = new FaultTolerantTimerTimerTaskFactory();
		scheduler = new FaultTolerantScheduler(name,16, cluster, priority, txManager, timerTaskFactory);
		timerTaskFactory.setScheduler(scheduler);
	}
	
	/**
	 *  
	 * @return the scheduler
	 */
	public FaultTolerantScheduler getScheduler() {
		return scheduler;
	}
	
	@Override
	public void cancel() {
		scheduler.shutdownNow();
	}
	
	@Override
	public int purge() {
		int count = 0;
		for (org.mobicents.timers.TimerTask timerTask : scheduler.getLocalRunningTasks()) {
			FaultTolerantTimerTimerTask ftTimerTask = (FaultTolerantTimerTimerTask) timerTask;
			if (ftTimerTask.isCanceled()) {
				scheduler.cancel(ftTimerTask.getData().getTaskID());
				count++;
			}
		}
		return count;
	}
	
	@Override
	public void schedule(TimerTask task, Date firstTime, long period) {
		final org.mobicents.timers.TimerTask taskWrapper = timerTaskFactory.newTimerTask(new FaultTolerantTimerTimerTaskData(task, UUID.randomUUID(),firstTime.getTime() - System.currentTimeMillis(),period,PeriodicScheduleStrategy.withFixedDelay));
		scheduler.schedule(taskWrapper);
	}
	
	@Override
	public void schedule(TimerTask task, Date time) {
		final org.mobicents.timers.TimerTask taskWrapper = timerTaskFactory.newTimerTask(new FaultTolerantTimerTimerTaskData(task, UUID.randomUUID(),time.getTime() - System.currentTimeMillis(),-1,null));
		scheduler.schedule(taskWrapper);		
	}
	
	@Override
	public void schedule(TimerTask task, long delay) {
		final org.mobicents.timers.TimerTask taskWrapper = timerTaskFactory.newTimerTask(new FaultTolerantTimerTimerTaskData(task, UUID.randomUUID(),System.currentTimeMillis()+delay,-1,null));
		scheduler.schedule(taskWrapper);	
	}
	
	@Override
	public void schedule(TimerTask task, long delay, long period) {
		final org.mobicents.timers.TimerTask taskWrapper = timerTaskFactory.newTimerTask(new FaultTolerantTimerTimerTaskData(task, UUID.randomUUID(),System.currentTimeMillis()+delay,period,PeriodicScheduleStrategy.withFixedDelay));
		scheduler.schedule(taskWrapper);		
	}
	
	@Override
	public void scheduleAtFixedRate(TimerTask task, Date firstTime, long period) {
		final org.mobicents.timers.TimerTask taskWrapper = timerTaskFactory.newTimerTask(new FaultTolerantTimerTimerTaskData(task, UUID.randomUUID(),firstTime.getTime() - System.currentTimeMillis(),period,PeriodicScheduleStrategy.atFixedRate));
		scheduler.schedule(taskWrapper);
	}
	
	@Override
	public void scheduleAtFixedRate(TimerTask task, long delay, long period) {
		final org.mobicents.timers.TimerTask taskWrapper = timerTaskFactory.newTimerTask(new FaultTolerantTimerTimerTaskData(task, UUID.randomUUID(),System.currentTimeMillis()+delay,period,PeriodicScheduleStrategy.atFixedRate));
		scheduler.schedule(taskWrapper);	
	}
}
