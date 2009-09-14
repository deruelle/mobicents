package org.mobicents.cluster;

import org.mobicents.cache.MobicentsCache;
import org.mobicents.cluster.cache.ClusteredCacheData;
import org.mobicents.cluster.cache.ClusteredCacheDataIndexingHandler;

/**
 * 
 * @author martins
 *
 */
public interface MobicentsCluster {

	/**
	 * Adds the specified client local listener.
	 * @param localListener
	 */
	public boolean addLocalListener(ClientLocalListener localListener);
	
	/**
	 * Removes the specified client local listener.
	 * @param localListener
	 * @return
	 */
	public boolean removeLocalListener(ClientLocalListener localListener);
	
	/**
	 *  
	 * @return the mobicents cache controlled by the cluster
	 */
	public MobicentsCache getMobicentsCache();
	
	/**
	 * Retrieves the handler to manage cluster indexing of {@link ClusteredCacheData}
	 * @return
	 */
	public ClusteredCacheDataIndexingHandler getClusteredCacheDataIndexingHandler();
}
