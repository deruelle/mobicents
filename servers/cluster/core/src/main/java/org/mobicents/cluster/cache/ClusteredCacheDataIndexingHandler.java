/**
 * 
 */
package org.mobicents.cluster.cache;

import org.jgroups.Address;

/**
 * Object that handles the management of cluster indexing for {@link ClusteredCacheData}.
 * 
 * @author martins
 *
 */
public interface ClusteredCacheDataIndexingHandler {

	/**
	 * Sets the address of the cluster node, which owns the cache data
	 * @param clusterNodeAddress
	 */
	public void setClusterNodeAddress(ClusteredCacheData cacheData, Address clusterNodeAddress);
	
	/**
	 * Retrieves the address of the cluster node, which owns the cache data.
	 * 
	 * @return null if this data doesn't have info about the cluster node, which owns it
	 */
	public Address getClusterNodeAddress(ClusteredCacheData cacheData);
	
}
