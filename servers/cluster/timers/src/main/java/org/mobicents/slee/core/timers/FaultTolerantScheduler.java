package org.mobicents.slee.core.timers;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import javax.transaction.Transaction;
import javax.transaction.TransactionManager;


import org.apache.log4j.Logger;
import org.mobicents.slee.runtime.cache.TimerTaskDataUser;
import org.mobicents.slee.runtime.cache.TimerTasksCacheData;


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
	 * jta tx manager
	 */
	private  TimerTasksCacheData cacheData;
	
	/**
	 * 
	 */
	private TransactionManager txManager;
	
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
	public FaultTolerantScheduler(int corePoolSize, TimerTasksCacheData cacheData, TimerTaskFactory timerTaskFactory) {
		this.cacheData = cacheData;
		if (!cacheData.exists()) {
			cacheData._create();
		}
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
		SetTimerAfterTxCommitRunnable setTimerAction = new SetTimerAfterTxCommitRunnable(task, this);
		if (txManager != null) {
			try {
				Transaction tx = txManager.getTransaction();
				if (tx != null) {
					Runnable rollbackAction = new Runnable() {				
						public void run() {
							localRunningTasks.remove(taskID);					
						}
					};
					tx.registerSynchronization(new TransactionSynchronization(setTimerAction,rollbackAction));
					task.setSetTimerTransactionalAction(setTimerAction);
				}
				else {
					setTimerAction.run();
				}
			}
			catch (Throwable e) {
				remove(taskID,true);
				throw new RuntimeException("Unable to register tx synchronization object",e);
			}
		}
		else {
			setTimerAction.run();
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

			final SetTimerAfterTxCommitRunnable setAction = task.getSetTimerTransactionalAction();
			if (setAction != null) {
				// we have a tx action scheduled to run when tx commits, to set the timer, lets simply cancel it
				setAction.cancel();
			}
			else {
				// do cancellation
				Runnable cancelAction = new CancelTimerAfterTxCommitRunnable(task,this);
				if (txManager != null) {
					try {
						Transaction tx = txManager.getTransaction();
						if (tx != null) {
							tx.registerSynchronization(new TransactionSynchronization(cancelAction,null));
						}
						else {
							cancelAction.run();
						}
					}
					catch (Throwable e) {
						throw new RuntimeException("unable to register tx synchronization object",e);
					}
				}
				else {
					cancelAction.run();
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

	public TransactionManager getTxManager() {
		return txManager;
	}

	public void setTxManager(TransactionManager txManager) {
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
