package org.mobicents.timers.timer;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;

import org.apache.log4j.Logger;
import org.mobicents.timers.FaultTolerantScheduler;
import org.mobicents.timers.TimerTask;

/**
 * A concrete {@link FaultTolerantScheduler} {@link TimerTask} to wrap a {@link java.util.TimerTask}.
 * 
 * @author martins
 *
 */
public class FaultTolerantTimerTimerTask extends TimerTask {

	private static final Logger logger = Logger.getLogger(FaultTolerantTimerTimerTask.class);
	
	/**
	 * 
	 */
	private final FaultTolerantScheduler scheduler;
	
	/**
	 * 
	 */
	private final FaultTolerantTimerTimerTaskData taskData;
	
	/**
	 * 
	 * @param taskData
	 */
	public FaultTolerantTimerTimerTask(FaultTolerantTimerTimerTaskData taskData,FaultTolerantScheduler scheduler) {
		super(taskData);
		this.taskData = taskData;
		this.scheduler = scheduler;
		setPeriod(taskData.getJavaUtilTimerTask(),taskData.getPeriod());
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mobicents.slee.core.timers.TimerTask#run()
	 */
	public void run() {
		if (isCanceled()) {
			scheduler.cancel(this.getData().getTaskID());			
		}
		else {
			try{
				taskData.getJavaUtilTimerTask().run();
				if(taskData.getPeriodicScheduleStrategy() == null) {
					scheduler.cancel(this.getData().getTaskID());
				}
				else {
					if (logger.isDebugEnabled()) {
						logger.debug("task data has a periodic schedule strategy, not cancelling the task");
					}
				}
			} catch(Throwable e) {
				logger.error(e.getMessage(),e);
			}					
		}				
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean isCanceled() {
		
		if(getTaskStatus(taskData.getJavaUtilTimerTask()) == 3 ) {
			return true;
		} 
		else {
			return false;
		}
	}
	
	private int getTaskStatus(final java.util.TimerTask timerTask) {
		if(System.getSecurityManager()!=null)
		{
			return AccessController.doPrivileged(new PrivilegedAction<Integer>(){

				public Integer run() {
					return _getTaskStatus(timerTask);
				}

			});
		}else
		{
			return _getTaskStatus(timerTask);
		}
	}
	
	private Integer _getTaskStatus(java.util.TimerTask timerTask) {
		Class cc = java.util.TimerTask.class;
		try {
			Field stateField=cc.getDeclaredField("state");
			stateField.setAccessible(true);
			Integer taskStatus= (Integer) stateField.get(taskData.getJavaUtilTimerTask());
			stateField.setAccessible(false);
			return taskStatus;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException("Fialed to get status");
		}
	}
	
	/**
	 * @param javaUtilTimerTask
	 * @param period
	 */
	private void setPeriod(final java.util.TimerTask javaUtilTimerTask,final long period) {
		if(System.getSecurityManager()!=null)
		{
			 AccessController.doPrivileged(new PrivilegedAction(){

				public Object run() {
					_setPeriod(javaUtilTimerTask,period);
					return null;
				}

			});
		}else
		{
			_setPeriod(javaUtilTimerTask,period);
		}
	}

	/**
	 * @param javaUtilTimerTask
	 * @param period
	 */
	private void _setPeriod(java.util.TimerTask javaUtilTimerTask, long period) {
		Class cc = java.util.TimerTask.class;
		try {
			Field stateField=cc.getDeclaredField("period");
			stateField.setAccessible(true);
			stateField.set(javaUtilTimerTask,new Long(period));
			stateField.setAccessible(false);
			return;
		} catch (Throwable e) {
			throw new RuntimeException("Failed to set task period");
		}
		
	}
}