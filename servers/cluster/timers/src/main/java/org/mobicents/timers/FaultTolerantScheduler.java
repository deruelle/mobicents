package org.mobicents.timers;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import javax.transaction.Transaction;
import javax.transaction.TransactionManager;


import org.apache.log4j.Logger;
import org.jboss.cache.Fqn;
import org.jgroups.Address;
import org.mobicents.cluster.MobicentsCluster;
import org.mobicents.cluster.cache.ClusteredCacheData;
import org.mobicents.timers.cache.FaultTolerantSchedulerCacheData;
import org.mobicents.timers.cache.TimerTaskCacheData;

/**
 * 
 * @author martins
 *
 */
public class FaultTolerantScheduler {

	private static final Logger logger = Logger.getLogger(FaultTolerantScheduler.class);
	
	/**
	 * the executor of timer tasks
	 */
	private final ScheduledThreadPoolExecutor executor;
	
	/**
	 * the scheduler cache data
	 */
	private final FaultTolerantSchedulerCacheData cacheData;
	
	/**
	 * the jta tx manager
	 */
	private final TransactionManager txManager;
	
	/**
	 * the local running tasks. NOTE: never ever check for values, class instances may differ due cache replication, ALWAYS use keys.
	 */
	private final ConcurrentHashMap<Serializable, TimerTask> localRunningTasks = new ConcurrentHashMap<Serializable, TimerTask>();
	
	/**
	 * the timer task factory associated with this scheduler
	 */
	private TimerTaskFactory timerTaskFactory;
	
	/**
	 * the base fqn used to store tasks data in mobicents cluster's cache
	 */
	private final Fqn baseFqn;
	
	/**
	 * the scheduler name
	 */
	private final String name;
		
	/**
	 * the mobicents cluster 
	 */
	private final MobicentsCluster cluster;
	
	/**
	 * listener for fail over events in mobicents cluster
	 */
	private final ClientLocalListener clusterClientLocalListener;
	
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
		this.cacheData = new FaultTolerantSchedulerCacheData(baseFqn,cluster);
		if (!cacheData.exists()) {
			cacheData.create();
		}
		this.timerTaskFactory = timerTaskFactory;
		this.txManager = txManager;		
		clusterClientLocalListener = new ClientLocalListener(priority);
		cluster.addLocalListener(clusterClientLocalListener);
	}

	/**
	 * Retrieves the {@link TimerTaskData} associated with the specified taskID. 
	 * @param taskID
	 * @return null if there is no such timer task data
	 */
	public TimerTaskData getTimerTaskData(Serializable taskID) {
		TimerTaskCacheData timerTaskCacheData = new TimerTaskCacheData(taskID, baseFqn, cluster);
		if (timerTaskCacheData.exists()) {
			return timerTaskCacheData.getTaskData();
		}
		else {
			return null;
		}
	}
	
	/**
	 * Retrieves the executor of timer tasks.
	 * @return
	 */
	ScheduledThreadPoolExecutor getExecutor() {
		return executor;
	}
	
	/**
	 * Retrieves local running tasks map.
	 * @return
	 */
	ConcurrentHashMap<Serializable, TimerTask> getLocalRunningTasksMap() {
		return localRunningTasks;
	}
	
	/**
	 * Retrieves a set containing all local running tasks. Removals on the set
	 * will not be propagated to the internal state of the scheduler.
	 * 
	 * @return
	 */
	public Set<TimerTask> getLocalRunningTasks() {
		return new HashSet<TimerTask>(localRunningTasks.values());
	}
	
	/**
	 *  Retrieves the scheduler name.
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 *  Retrieves the priority of the scheduler as a client local listener of the mobicents cluster.
	 * @return the priority
	 */
	public byte getPriority() {
		return clusterClientLocalListener.getPriority();
	}
	
	/**
	 * Retrieves the jta tx manager.
	 * @return
	 */
	public TransactionManager getTransactionManager() {
		return txManager;
	}

	/**
	 * Retrieves the timer task factory associated with this scheduler.
	 * @return
	 */
	public TimerTaskFactory getTimerTaskFactory() {
		return timerTaskFactory;
	}
	
	// logic 
	
	/**
	 * Schedules the specified task.
	 * 
	 * @param task
	 */
	public void schedule(TimerTask task) {
		
		final TimerTaskData taskData = task.getData(); 
		final Serializable taskID = taskData.getTaskID();
		
		if (logger.isDebugEnabled()) {
			logger.debug("Scheduling task with id "+taskID);
		}
		localRunningTasks.put(taskID, task);
		
		// store the task and data
		TimerTaskCacheData timerTaskCacheData = new TimerTaskCacheData(taskID, baseFqn, cluster);
		if (timerTaskCacheData.create()) {
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
	private void recover(TimerTaskData taskData) {
		TimerTask task = timerTaskFactory.newTimerTask(taskData);
		if (logger.isDebugEnabled()) {
			logger.debug("Recovering task with id "+taskData.getTaskID());
		}
		task.beforeRecover();
		schedule(task);
	}

	public void shutdownNow() {
		if (logger.isDebugEnabled()) {
			logger.debug("Shutdown now.");
		}
		cluster.removeLocalListener(clusterClientLocalListener);
		executor.shutdownNow();
		localRunningTasks.clear();
	}
	
	@Override
	public String toString() {
		return "FaultTolerantScheduler [ name = "+name+" ]";
	}
	
	public String toDetailedString() {
		return "FaultTolerantScheduler [ name = "+name+" , local tasks = "+localRunningTasks.size()+" , all tasks "+cacheData.getTaskIDs().size()+" ]";
	}
	
	public void stop() {
		this.shutdownNow();		
	}
	
	private class ClientLocalListener implements org.mobicents.cluster.ClientLocalListener {

		/**
		 * the priority of the scheduler as a client local listener of the mobicents cluster
		 */
		private final byte priority;
				
		/**
		 * @param priority
		 */
		public ClientLocalListener(byte priority) {
			this.priority = priority;
		}

		/* (non-Javadoc)
		 * @see org.mobicents.cluster.client.LocalListener#getBaseFqn()
		 */
		public Fqn getBaseFqn() {
			return baseFqn;
		}

		/* (non-Javadoc)
		 * @see org.mobicents.cluster.ClientLocalListener#getPriority()
		 */
		public byte getPriority() {
			return priority;
		}

		/* (non-Javadoc)
		 * @see org.mobicents.cluster.ClientLocalListener#failOverClusterMember(org.jgroups.Address)
		 */
		public void failOverClusterMember(Address address) {
			
		}
		
		/* (non-Javadoc)
		 * @see org.mobicents.ftf.FTFListener#lostOwnership(org.mobicents.slee.runtime.cache.ClusteredCacheData)
		 */
		public void lostOwnership(ClusteredCacheData clusteredCacheData) {
			
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
		
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return FaultTolerantScheduler.this.toString();
		}
		
	}
}
