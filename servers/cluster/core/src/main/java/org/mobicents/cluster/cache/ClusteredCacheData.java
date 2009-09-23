/**
 * 
 */
package org.mobicents.cluster.cache;

import org.jboss.cache.Fqn;
import org.jboss.cache.Node;
import org.jgroups.Address;
import org.mobicents.cache.CacheData;
import org.mobicents.cluster.MobicentsCluster;

/**
 * 
 * Abstract class for a clustered {@link CacheData}.
 * 
 * @author martins
 *
 */
public class ClusteredCacheData extends CacheData {
	
	private final ClusteredCacheDataIndexingHandler indexingHandler;
	
	/**
	 * @param nodeFqn
	 * @param mobicentsCache
	 */
	public ClusteredCacheData(Fqn<?> nodeFqn, MobicentsCluster mobicentsCluster) {
		super(nodeFqn, mobicentsCluster.getMobicentsCache());
		indexingHandler = mobicentsCluster.getClusteredCacheDataIndexingHandler();
	}

	/* (non-Javadoc)
	 * @see org.mobicents.slee.runtime.cache.CacheData#create()
	 */
	@Override
	public boolean create() {
		if (super.create()) {
			// store local address if we are not running in local mode
			if (!getMobicentsCache().isLocalMode()) {
				setClusterNodeAddress(getMobicentsCache().getJBossCache().getLocalAddress());
			}
			return true;
		}
		else {
			return false;
		}
	}
	
	/**
	 * Sets the address of the cluster node, which owns the cache data
	 * @param clusterNodeAddress
	 */
	public void setClusterNodeAddress(Address clusterNodeAddress) {
		indexingHandler.setClusterNodeAddress(this,clusterNodeAddress);
	}
	
	/**
	 * Retrieves the address of the cluster node, which owns the cache data.
	 * 
	 * @return null if this data doesn't have info about the cluster node, which owns it
	 */
	public Address getClusterNodeAddress() {
		return indexingHandler.getClusterNodeAddress(this);
	}
	
	/* (non-Javadoc)
	 * @see org.mobicents.cache.CacheData#getNode()
	 */
	@Override
	protected Node getNode() {
		return super.getNode();
	}
}
