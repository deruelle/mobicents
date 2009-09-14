/**
 * 
 */
package org.mobicents.cluster.cache;

import org.apache.log4j.Logger;
import org.jgroups.Address;

/**
 * Default impl for cluster cache data indexing, which relies on storing a data
 * field with the cluster node address.
 * 
 * @author martins
 * 
 */
public class DefaultClusteredCacheDataIndexingHandler implements ClusteredCacheDataIndexingHandler {

	private static final Logger logger = Logger.getLogger(ClusteredCacheData.class);
	
	/**
	 * 
	 */
	private static final String CLUSTER_NODE_ADDRESS_NODE_KEY = "cnaddress";
	
	/*
	 * (non-Javadoc)
	 * @see org.mobicents.cluster.cache.ClusteredCacheDataIndexingHandler#setClusterNodeAddress(org.mobicents.cluster.cache.ClusteredCacheData, org.jgroups.Address)
	 */
	public void setClusterNodeAddress(ClusteredCacheData cacheData, Address clusterNodeAddress) {
		cacheData.getNode().put(CLUSTER_NODE_ADDRESS_NODE_KEY,clusterNodeAddress);
		if (logger.isDebugEnabled()) {
			logger.debug("Node "+cacheData.getNode().getFqn()+" indexed to cluster node "+cacheData.getMobicentsCache().getJBossCache().getLocalAddress());
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mobicents.cluster.cache.ClusteredCacheData#getClusterNodeAddress()
	 */
	public Address getClusterNodeAddress(ClusteredCacheData cacheData) {
		return (Address) cacheData.getNode().get(CLUSTER_NODE_ADDRESS_NODE_KEY);
	}
}
