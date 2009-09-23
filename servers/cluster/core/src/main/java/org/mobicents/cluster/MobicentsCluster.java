package org.mobicents.cluster;

import java.util.List;

import org.jgroups.Address;
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
	 * Retrieves the local address of the cluster node.
	 * @return
	 */
	public Address getLocalAddress();
	
	/**
	 * Retrieves the members of the cluster.
	 * @return
	 */
	public List<Address> getClusterMembers();
	
	/**
	 * Indicates if the local node is the master/head of the cluster.
	 * @return
	 */
	public boolean isHeadMember();
	
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
