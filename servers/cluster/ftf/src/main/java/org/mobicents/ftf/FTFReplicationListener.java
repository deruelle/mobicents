/**
 * Start time:09:43:38 2009-08-07<br>
 * Project: jb51-cluster<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */
package org.mobicents.ftf;

import java.util.List;

import javax.transaction.TransactionManager;

import org.jboss.cache.Cache;
import org.jboss.cache.Fqn;
import org.jboss.ha.framework.interfaces.HASingletonElectionPolicy;
import org.jboss.ha.framework.server.ClusterPartition;
import org.jgroups.stack.IpAddress;
import org.mobicents.ftf.election.SingletonElector;
import org.mobicents.ftf.resource.ResourceGroup;

/**
 * Start time:09:43:38 2009-08-07<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */
public abstract class FTFReplicationListener {

	public final static String _INDEX_NODE = "_INDEX_NODE_";

	// Required, we should always run in tx.
	protected TransactionManager txMgr;

	protected SingletonElector elector;

	// we dont have Buddygroup, instead we use View from jgroups
	protected List<IpAddress> lastActualView;
	protected Cache watchedCache;

	protected ResourceGroup resourceGroup;

	public TransactionManager getTxMgr() {
		return txMgr;
	}

	public void setTxMgr(TransactionManager txMgr) {
		this.txMgr = txMgr;
	}

	public SingletonElector getElector() {
		return elector;
	}

	public void setElector(SingletonElector elector) {
		this.elector = elector;
	}

	public Cache getWatchedCache() {
		return watchedCache;
	}

	public void setWatchedCache(Cache watchedCache) {
		this.watchedCache = watchedCache;
	}

	public ResourceGroup getResourceGroup() {
		return resourceGroup;
	}

	public void setResourceGroup(ResourceGroup resourceGroup) {
		this.resourceGroup = resourceGroup;
	}

	public void init() {
		if (this.lastActualView == null)
			this.lastActualView = (List<IpAddress>) watchedCache.getMembers();
	}

}
