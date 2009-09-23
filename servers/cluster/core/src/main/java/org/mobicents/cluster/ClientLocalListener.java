package org.mobicents.cluster;

import org.jboss.cache.Fqn;
import org.jgroups.Address;
import org.mobicents.cluster.cache.ClusteredCacheData;

/**
 * 
 * This interface defines callback methods which will be called when the local
 * cluster node looses or wins ownership on a certain {@link ClusteredCacheData}
 * .
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 * @author martins
 * 
 */
public interface ClientLocalListener {

	/**
	 * Retrieves the base fqn the listener has interest.
	 * @return
	 */
	public Fqn getBaseFqn();
	
	/**
	 * Retrieves the priority of the listener.
	 * @return
	 */
	public byte getPriority();

	/**
	 * Indicates that it will do fail over the cluster node with the specified {@link Address}.
	 * @param address
	 */
	public void failOverClusterMember(Address address);
	
	/**
	 * Notifies the local client that it now owns the specified {@link ClusteredCacheData}. 
	 * @param clusteredCacheData
	 */
	public void wonOwnership(ClusteredCacheData clusteredCacheData);

	/**
	 * Notifies the local client that it lost ownership of the specified {@link ClusteredCacheData}.
	 * @param clusteredCacheData
	 */
	public void lostOwnership(ClusteredCacheData clusteredCacheData);

}
