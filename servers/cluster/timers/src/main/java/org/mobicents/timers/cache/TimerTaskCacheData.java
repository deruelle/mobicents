package org.mobicents.timers.cache;

import java.io.Serializable;

import org.jboss.cache.Fqn;
import org.mobicents.cluster.MobicentsCluster;
import org.mobicents.cluster.cache.ClusteredCacheData;
import org.mobicents.timers.TimerTask;
import org.mobicents.timers.TimerTaskData;

/**
 * 
 * Proxy object for timer task data management through JBoss Cache
 * 
 * @author martins
 * 
 */

public class TimerTaskCacheData extends ClusteredCacheData {
	
	/**
	 * the node's data map key where task data is stored
	 */
	private static final String CACHE_NODE_MAP_KEY = "taskdata";
	
	/**
	 * 
	 */
	public TimerTaskCacheData(Serializable taskID, Fqn baseFqn, MobicentsCluster mobicentsCluster) {
		super(Fqn.fromRelativeElements(baseFqn, taskID),mobicentsCluster);
	}

	/**
	 * Sets the task data.
	 * 
	 * @param taskData
	 */
	public void setTaskData(TimerTaskData taskData) {
		getNode().put(CACHE_NODE_MAP_KEY,taskData);
	}

	/**
	 * Retrieves the task data
	 * @return
	 */
	public TimerTaskData getTaskData() {
		return (TimerTaskData) getNode().get(CACHE_NODE_MAP_KEY);		
	}

	/**
	 * Retrieves the {@link TimerTask} id from the specified {@link ClusteredCacheData}.
	 * 
	 * @param clusteredCacheData
	 * @return
	 *  
	 */
	public static Serializable getTaskID(ClusteredCacheData clusteredCacheData) throws IllegalArgumentException {
		return (Serializable) clusteredCacheData.getNodeFqn().getLastElement();
	}
	
}
