package org.mobicents.slee.core.timers;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;


import org.apache.log4j.Logger;
import org.mobicents.slee.runtime.cache.TimerTaskDataUser;
import org.mobicents.slee.runtime.cache.TimerTasksCacheData;
import org.mobicents.transaction.ExtendedTransactionManager;
import org.mobicents.transaction.TransactionalAction;


/**
 * 
 * @author martins
 *
 */
public class FaultTolerantScheduler implements FaultTolerantSchedulerMBean, TimerTaskDataUser{

	private static final Logger logger = Logger.getLogger(FaultTolerantScheduler.class);
	
	/**
	 * 
	 */
	private  ScheduledThreadPoolExecutor executor;
	
	/**
	 * 
	 */
	private  TimerTasksCacheData cacheData;
	
	/**
	 * 
	 */
	private  ExtendedTransactionManager txManager;
	
	/**
	 * Contains local running tasks. NOTE: never ever check for values, class instances may differ due cache replication, ALWAYS use keys.
	 */
	private ConcurrentHashMap<Serializable, TimerTask> localRunningTasks = new ConcurrentHashMap<Serializable, TimerTask>();
	
	/**
	 * 
	 */
	private TimerTaskFactory timerTaskFactory;
	
	private int corePoolSize = 16;


	/**
	 * 
	 * @param corePoolSize
	 * @param cacheData
	 * @param txManager
	 */
	public FaultTolerantScheduler(int corePoolSize, TimerTasksCacheData cacheData, ExtendedTransactionManager txManager, TimerTaskFactory timerTaskFactory) {
		
		
		this.cacheData = cacheData;
		if (!cacheData.exists()) {
			cacheData._create();
		}
		this.txManager = txManager;
		this.timerTaskFactory = timerTaskFactory;
		
	}
	public FaultTolerantScheduler() {
		
		
	}

	/**
	 * Retrieves
	 * @return
	 */
	public TimerTasksCacheData getCacheData() {
		return cacheData;
	}
	
	/**
	 * 
	 * @return
	 */
	public ScheduledThreadPoolExecutor getExecutor() {
		return executor;
	}
	
	/**
	 * 
	 * @return
	 */
	public ConcurrentHashMap<Serializable, TimerTask> getLocalRunningTasks() {
		return localRunningTasks;
	}
	public void schedule(TimerTask task) {
		this.schedule(task, true);
	}
	/**
	 * Schedules the specified task.
	 * 
	 * @param task
	 */
	 void schedule(TimerTask task,boolean updateStorage) {
		 if(logger.isDebugEnabled())
		 {
			 logger.debug("schedule() : "+task+" - "+updateStorage);
		 }
		final TimerTaskData taskData = task.getData(); 
		final Serializable taskID = taskData.getTaskID();
		
		if (logger.isDebugEnabled()) {
			logger.debug("Scheduling task with id "+taskID);
		}
		localRunningTasks.put(taskID, task);
		// store the task and data
		if(updateStorage)
			cacheData.addTaskData(taskID, taskData);
		
		
		// schedule task
		SetTimerTransactionalAction setTimerAction = new SetTimerTransactionalAction(task, this);
		if (txManager != null) {
			TransactionalAction rollbackAction = new TransactionalAction() {				
				public void execute() {
					localRunningTasks.remove(taskID);					
				}
			};
			try {
				txManager.addAfterRollbackAction(rollbackAction);
				txManager.addAfterCommitAction(setTimerAction);
				task.setSetTimerTransactionalAction(setTimerAction);
			} catch (Throwable e) {
				e.printStackTrace();
				remove(taskID,true);
				throw new RuntimeException(e.getMessage(),e);
			}
		}
		else {
			setTimerAction.execute();
		}		
	}

	/**
	 * Cancels a local running task with the specified ID.
	 * 
	 * @param taskID
	 * @return the task canceled
	 */
	public TimerTask cancel(Serializable taskID) {
		
		if (logger.isDebugEnabled()) {
			logger.debug("Canceling task with timer id "+taskID);
		}
		
		TimerTask task = localRunningTasks.get(taskID);
		if (task != null) {
			// remove task data
			cacheData.removeTaskData(taskID);

			final SetTimerTransactionalAction setAction = task.getSetTimerTransactionalAction();
			if (setAction != null) {
				// we have a tx action scheduled to run when tx commits, to set the timer, lets simply cancel it
				setAction.cancel();
			}
			else {
				// do cancellation
				TransactionalAction cancelAction = new CancelTimerTransactionalAction(task,this);
				if (txManager != null) {
					try {
						txManager.addAfterCommitAction(cancelAction);
					} catch (Throwable e) {
						throw new RuntimeException(e.getMessage(),e);
					}
				}
				else {
					cancelAction.execute();
				}			
			}		
		}
		
		return task;
	}
	
	void remove(Serializable taskID,boolean removeFromCache) {
		if(logger.isDebugEnabled())
		{
			logger.debug("remove() : "+taskID+" - "+removeFromCache);
		}
		
		localRunningTasks.remove(taskID);
		if(removeFromCache)
			cacheData.removeTaskData(taskID);
	}
	
	/**
	 * Recovers a timer task that was running in another node.
	 * 
	 * @param taskData
	 */
	public void recover(TimerTaskData taskData) {
		TimerTask task = timerTaskFactory.newTimerTask(taskData);
		if (logger.isDebugEnabled()) {
			logger.debug("Recovering task with id "+taskData.getTaskID());
		}
		task.beforeRecover();
		schedule(task,false);
	}

	public void shutdownNow() {
		if (logger.isDebugEnabled()) {
			logger.debug("Shutdown now.");
		}
		executor.shutdownNow();
		localRunningTasks.clear();
	}
	
	@Override
	public String toString() {
		return "FaultTolerantScheduler [ local tasks = "+localRunningTasks.size()+" , all tasks "+cacheData.getTaskDatas().size()+" ]";
	}

	public ExtendedTransactionManager getTxManager() {
		return txManager;
	}

	public void setTxManager(ExtendedTransactionManager txManager) {
		this.txManager = txManager;
	}

	public TimerTaskFactory getTimerTaskFactory() {
		return timerTaskFactory;
	}

	public void setTimerTaskFactory(TimerTaskFactory timerTaskFactory) {
		this.timerTaskFactory = timerTaskFactory;
	}

	public int getCorePoolSize() {
		return corePoolSize;
	}

	public void setCorePoolSize(int corePoolSize) {
		this.corePoolSize = corePoolSize;
	}

	public void setCacheData(TimerTasksCacheData cacheData) {
		this.cacheData = cacheData;
	}

	public void start() {
		if (this.cacheData == null) {
			throw new IllegalStateException("Not fully initialized, we lack cache data.");
		}
		this.executor = new ScheduledThreadPoolExecutor(corePoolSize);
		if (!cacheData.exists()) {
			cacheData._create();
		}

		this.cacheData.setDataUser(this);

	}
	
	public void stop() {
		try {
			this.shutdownNow();
		} finally {

			this.cacheData.clearDataUser();

		}

	}
	/**
	 * @param data
	 */
	public void recoverLocalResource(Object data){
		if(data instanceof TimerTaskData)
		{
			TimerTaskData _data = (TimerTaskData) data;
			this.recover(_data);
		}else
		{
			throw new IllegalArgumentException("Wrong argument type, can not process class: "+data.getClass());
		}
	}
	/**
	 * @param taskID
	 * @return
	 */
	public boolean containsLocalResource(Object data){
		if(data instanceof TimerTaskData)
		{
			TimerTaskData _data = (TimerTaskData) data;
			return this.localRunningTasks.containsKey(_data.getTaskID());
		}else
		{
			throw new IllegalArgumentException("Wrong argument type, can not process class: "+data.getClass());
		}
	}

	/**
	 * @param taskID
	 */
	public void removeLocalResource(Object data){
		if(data instanceof TimerTaskData)
		{
			TimerTaskData _data = (TimerTaskData) data;
			this.remove(_data.getTaskID(),false);
		}else
		{
			throw new IllegalArgumentException("Wrong argument type, can not process class: "+data.getClass());
		}
	}

	
}
