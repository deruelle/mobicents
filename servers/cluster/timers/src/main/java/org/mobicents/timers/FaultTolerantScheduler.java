package org.mobicents.timers;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import javax.transaction.Transaction;
import javax.transaction.TransactionManager;


import org.apache.log4j.Logger;
import org.jboss.cache.Fqn;
import org.mobicents.cluster.ClientLocalListener;
import org.mobicents.cluster.MobicentsCluster;
import org.mobicents.cluster.cache.ClusteredCacheData;
import org.mobicents.timers.cache.FaultTolerantSchedulerCacheData;
import org.mobicents.timers.cache.TimerTaskCacheData;


/**
 * 
 * @author martins
 *
 */
public class FaultTolerantScheduler implements ClientLocalListener {

	private static final Logger logger = Logger.getLogger(FaultTolerantScheduler.class);
	
	/**
	 * 
	 */
	private final ScheduledThreadPoolExecutor executor;
	
	/**
	 * 
	 */
	private final FaultTolerantSchedulerCacheData cacheData;
	
	/**
	 * jta tx manager
	 */
	private final TransactionManager txManager;
	
	/**
	 * Contains local running tasks. NOTE: never ever check for values, class instances may differ due cache replication, ALWAYS use keys.
	 */
	private final ConcurrentHashMap<Serializable, TimerTask> localRunningTasks = new ConcurrentHashMap<Serializable, TimerTask>();
	
	/**
	 * 
	 */
	private TimerTaskFactory timerTaskFactory;
	
	/**
	 * 
	 */
	private final Fqn baseFqn;
	
	/**
	 * the scheduler name
	 */
	private final String name;
	
	/**
	 * the priority of the scheduler as a client local listener of the mobicents cluster
	 */
	private final byte priority;
	
	/**
	 * 
	 */
	private final MobicentsCluster cluster;
	
	/**
	 * 
	 * @param name
	 * @param corePoolSize
	 * @param cluster
	 * @param priority
	 * @param txManager
	 * @param timerTaskFactory
	 */
	public FaultTolerantScheduler(String name, int corePoolSize, MobicentsCluster cluster, byte priority, TransactionManager txManager,TimerTaskFactory timerTaskFactory) {
		this.name = name;
		this.executor = new ScheduledThreadPoolExecutor(corePoolSize);
		this.baseFqn = Fqn.fromElements(name);
		this.cluster = cluster;
		this.cacheData = new FaultTolerantSchedulerCacheData(baseFqn,cluster.getMobicentsCache());
		if (!cacheData.exists()) {
			cacheData.create();
		}
		this.timerTaskFactory = timerTaskFactory;
		this.txManager = txManager;
		this.priority = priority;
		cluster.addLocalListener(this);
	}

	/* (non-Javadoc)
	 * @see org.mobicents.cluster.client.LocalListener#getBaseFqn()
	 */
	public Fqn getBaseFqn() {
		return baseFqn;
	}
	
	/**
	 * Retrieves
	 * @return
	 */
	public FaultTolerantSchedulerCacheData getCacheData() {
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
	
	/**
	 *  
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/* (non-Javadoc)
	 * @see org.mobicents.cluster.ClientLocalListener#getPriority()
	 */
	public byte getPriority() {
		return priority;
	}
	
	// logic 
	
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
		if(updateStorage) {
			TimerTaskCacheData timerTaskCacheData = new TimerTaskCacheData(taskID, baseFqn, cluster);
			timerTaskCacheData.create();
			timerTaskCacheData.setTaskData(taskData);
		}
				
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
			new TimerTaskCacheData(taskID, baseFqn, cluster).remove();

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
			new TimerTaskCacheData(taskID, baseFqn, cluster).remove();
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
		cluster.removeLocalListener(this);
		executor.shutdownNow();
		localRunningTasks.clear();
	}
	
	@Override
	public String toString() {
		return "FaultTolerantScheduler [ local tasks = "+localRunningTasks.size()+" , all tasks "+cacheData.getTaskIDs().size()+" ]";
	}

	public TransactionManager getTxManager() {
		return txManager;
	}

	public TimerTaskFactory getTimerTaskFactory() {
		return timerTaskFactory;
	}
	
	public void stop() {
		this.shutdownNow();		
	}
	
	/* (non-Javadoc)
	 * @see org.mobicents.ftf.FTFListener#lostOwnership(org.mobicents.slee.runtime.cache.ClusteredCacheData)
	 */
	public void lostOwnership(ClusteredCacheData clusteredCacheData) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.mobicents.ftf.FTFListener#wonOwnership(org.mobicents.slee.runtime.cache.ClusteredCacheData)
	 */
	public void wonOwnership(ClusteredCacheData clusteredCacheData) {
		
		if (logger.isDebugEnabled()) {
			logger.debug("wonOwnership( clusterCacheData = "+clusteredCacheData+")");
		}

		try {
			Serializable taskID = TimerTaskCacheData.getTaskID(clusteredCacheData);
			TimerTaskCacheData timerTaskCacheData = new TimerTaskCacheData(taskID, baseFqn, cluster);
			recover(timerTaskCacheData.getTaskData());
		}
		catch (Throwable e) {
			logger.error(e.getMessage(),e);
		}
	}
	
}
