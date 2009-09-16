package org.mobicents.timers.cache;

import java.util.Collections;
import java.util.Set;

import org.jboss.cache.Fqn;
import org.jboss.cache.Node;
import org.mobicents.cache.CacheData;
import org.mobicents.cluster.MobicentsCluster;


/**
 * 
 * Proxy object for timer facility entity data management through JBoss Cache
 * 
 * @author martins
 * 
 */

public class FaultTolerantSchedulerCacheData extends CacheData {
			
	/**
	 * 
	 * @param txManager 
	 * @param txManager
	 */
	public FaultTolerantSchedulerCacheData(Fqn baseFqn, MobicentsCluster cluster) {
		super(baseFqn,cluster.getMobicentsCache());
	}

	public Set<?> getTaskIDs() {
		final Node<?,?> node = getNode();
		if (!node.isLeaf()) {
			return node.getChildrenNames();			
		}
		else {
			return Collections.EMPTY_SET;
		}
	}
	
}
