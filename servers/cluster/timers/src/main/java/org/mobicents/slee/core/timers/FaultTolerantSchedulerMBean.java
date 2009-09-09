/**
 * Start time:08:36:37 2009-08-17<br>
 * Project: mobicents-jainslee-server-core<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */
package org.mobicents.slee.core.timers;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import javax.transaction.TransactionManager;

import org.mobicents.slee.runtime.cache.TimerTasksCacheData;

/**
 * Start time:08:36:37 2009-08-17<br>
 * Project: mobicents-jainslee-server-core<br>
 * Simple bean interface, fault tolerant scheduler should be singleton object
 * per cache it makes use of.
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */
public interface FaultTolerantSchedulerMBean {
	/**
	 * Retrieves
	 * 
	 * @return
	 */
	public TimerTasksCacheData getCacheData();

	/**
	 * 
	 * @return
	 */
	public ScheduledThreadPoolExecutor getExecutor();

	/**
	 * 
	 * @return
	 */
	public ConcurrentHashMap<Serializable, TimerTask> getLocalRunningTasks();

	public void schedule(TimerTask task);

	/**
	 * Cancels a local running task with the specified ID.
	 * 
	 * @param taskID
	 * @return the task canceled
	 */
	public TimerTask cancel(Serializable taskID);

	public void shutdownNow();

	public TransactionManager getTxManager() ;

	public void setTxManager(TransactionManager txManager) ;

	public TimerTaskFactory getTimerTaskFactory() ;

	public void setTimerTaskFactory(TimerTaskFactory timerTaskFactory) ;

	public int getCorePoolSize() ;

	public void setCorePoolSize(int corePoolSize) ;

	public void setCacheData(TimerTasksCacheData cacheData) ;
}
