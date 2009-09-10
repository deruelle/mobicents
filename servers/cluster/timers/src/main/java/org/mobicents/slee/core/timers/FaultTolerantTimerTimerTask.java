package org.mobicents.slee.core.timers;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;

import org.mobicents.slee.core.timers.timer.FaultTolerantTimerTimerTaskData;

import EDU.oswego.cs.dl.util.concurrent.Takable;

/**
 * A concrete {@link FaultTolerantScheduler} {@link TimerTask} to wrap a {@link java.util.TimerTask}.
 * 
 * @author martins
 *
 */
public class FaultTolerantTimerTimerTask extends TimerTask {

	/**
	 * 
	 */
	private FaultTolerantScheduler scheduler;
	
	/**
	 * 
	 */
	private final FaultTolerantTimerTimerTaskData taskData;
	
	/**
	 * 
	 * @param taskData
	 */
	public FaultTolerantTimerTimerTask(FaultTolerantTimerTimerTaskData taskData) {
		super(taskData);
		this.taskData = taskData;
		//taskData.getJavaUtilTimerTask().period = taskData.getPeriod();
		setPeriod(taskData.getJavaUtilTimerTask(),taskData.getPeriod());
		
	}
	
	

	/**
	 * 
	 * @param scheduler
	 */
	public void setScheduler(FaultTolerantScheduler scheduler) {
		this.scheduler = scheduler;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mobicents.slee.core.timers.TimerTask#run()
	 */
	public void run() {
		if (isCanceled()) {
			if (scheduler != null) {
				scheduler.cancel(this.getData().getTaskID());
			}
		}
		else {
			try{
				taskData.getJavaUtilTimerTask().run();
			}catch(Exception e)
			{
				e.printStackTrace();
			}
			if(taskData.getPeriodicScheduleStrategy() == null)
			{
				if (scheduler != null) {
					scheduler.remove(taskData,true);
				}
			}
			//FIXME: do we need to check other strategies here?
		}				
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean isCanceled() {
		//return taskData.getJavaUtilTimerTask().state == java.util.TimerTask.CANCELLED;
		//DO NOT USE taskData.getJavaUtilTimerTask().getClass() for this, it does not work...
		if(getTaskStatus(taskData.getJavaUtilTimerTask())== 3 )
		{
			return true;
		}else
		{
			return false;
		}
	}
	private int getTaskStatus(final java.util.TimerTask timerTask)
	{
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
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException("Fialed to get status");
		}
		
	}
}