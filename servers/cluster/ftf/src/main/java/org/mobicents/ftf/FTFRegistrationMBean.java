/**
 * Start time:09:28:07 2009-08-07<br>
 * Project: jb51-cluster<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */
package org.mobicents.ftf;

import javax.transaction.TransactionManager;

import org.jboss.cache.Cache;
import org.jboss.ha.framework.interfaces.HASingletonElectionPolicy;
import org.jboss.ha.framework.server.ClusterPartition;
import org.mobicents.ftf.election.SingletonElector;
import org.mobicents.ftf.resource.ResourceGroup;

/**
 * Start time:09:28:07 2009-08-07<br>
 * Project: jb51-cluster<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */
public interface FTFRegistrationMBean {

	//it could be conveniant to use MobicentsCache, but thats circular dep
	public Cache registerInFTF(ResourceGroup resourceGroup,Cache cache, SingletonElector elector);

	public Cache registerInFTF(ResourceGroup resourceGroup, Cache cache);

	public void deregisterInFTF(ResourceGroup resourceGroup);

	public void setDefaultElectionPolicy(SingletonElector elector);

	public SingletonElector getDefaultElectionPolicy();

	public TransactionManager getTransactionManager();

	public void setTransactionManager(TransactionManager transactionManager);

	public ClusterPartition getClusterPartition();

	public void setClusterPartition(ClusterPartition clusterPartition);
}
