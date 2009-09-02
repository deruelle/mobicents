package org.mobicents.ftf;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.TransactionManager;

import org.apache.log4j.Logger;
import org.jboss.cache.Cache;
import org.jboss.cache.config.BuddyReplicationConfig;
import org.jboss.cache.config.Configuration;
import org.jboss.ha.framework.server.ClusterPartition;
import org.mobicents.ftf.election.SingletonElector;
import org.mobicents.ftf.resource.ResourceGroup;


/**
 * Start time:09:38:13 2009-08-07<br>
 * Project: jb51-cluster<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */
public class FTFRegistration implements FTFRegistrationMBean {

	private static final transient Logger log = Logger.getLogger(FTFRegistration.class);
	private List<CachePlaceHolder> registeredListeners = new ArrayList<CachePlaceHolder>();
	private SingletonElector defaultElector;

	private TransactionManager transactionManager;
	private ClusterPartition clusterPartition;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.ftf.FTFRegistrationMBean#getDefaultElectionPolicy()
	 */
	public SingletonElector getDefaultElectionPolicy() {
		return this.defaultElector;
	}

	public Cache registerInFTF(ResourceGroup resourceGroup, Cache cache, SingletonElector elector) {
		if (cache == null) {
			throw new IllegalArgumentException("Cache must must be null");
		}
		if (resourceGroup == null) {
			throw new IllegalArgumentException("ResourceGroup must must be null");
		}
		FTFReplicationListener listener = fetchProperListener(cache, resourceGroup.getReplicationListener());
		if (elector != null) {
			listener.setElector(elector);
		} else {
			listener.setElector(this.defaultElector);
		}

		CachePlaceHolder cph = new CachePlaceHolder(cache,resourceGroup,listener);
		

		listener.setTxMgr(this.transactionManager);
		listener.setWatchedCache(cache);
		if(listener.getResourceGroup()==null)
		{
			listener.setResourceGroup(resourceGroup);
		}
		this.registeredListeners.add(cph);
		cache.addCacheListener(listener);
		listener.init();
		return cache;

	}

	public Cache registerInFTF(ResourceGroup resourceGroup, Cache cache) {
		return this.registerInFTF(resourceGroup, cache, this.defaultElector);
	}

	public void deregisterInFTF(ResourceGroup resourceGroup) {
		CachePlaceHolder cph = new CachePlaceHolder(null,resourceGroup,resourceGroup.getReplicationListener());
		int index = registeredListeners.indexOf(cph);
		if (index < 0) {
			log.error("There is no listener for this ResoruceGroup[" + resourceGroup.getResourceGroupName() + "]");
			return;
		}
		cph = this.registeredListeners.remove(index);
		// should we clean cache data here?
		cph.getC().removeCacheListener(cph.getListener());
	}



	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mobicents.ftf.FTFRegistrationMBean#setDefaultElectionPolicy(org.jboss
	 * .ha.framework.interfaces.HASingletonElectionPolicy)
	 */
	public void setDefaultElectionPolicy(SingletonElector elector) {

		this.defaultElector = elector;

	}

	public TransactionManager getTransactionManager() {
		return transactionManager;
	}

	public void setTransactionManager(TransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	public ClusterPartition getClusterPartition() {
		return clusterPartition;
	}

	public void setClusterPartition(ClusterPartition clusterPartition) {
		this.clusterPartition = clusterPartition;
	}

	// ///////////////////
	// PRIVATE SECTION //
	// ///////////////////
	/**
	 * @param c
	 * @param listener
	 * @return
	 */
	private FTFReplicationListener fetchProperListener(Cache c, FTFReplicationListener listener) {
		// should we check if we got correct?
		if (listener != null)
			return listener;

		Configuration cConfig = c.getConfiguration();
		BuddyReplicationConfig cBuddyRepl = cConfig.getBuddyReplicationConfig();

		if (cBuddyRepl == null || !cBuddyRepl.isEnabled()) {
			return new FTFClusterWideReplicationSICacheListener();
		}

		if (cBuddyRepl != null && cBuddyRepl.isEnabled()) {
			if (cBuddyRepl.isAutoDataGravitation()) {
				// FIXME: this might be not ok, but for now it makes things
				// spin.
				throw new IllegalArgumentException("Passed cache has enabled auto data gravitation");
			} else {
				// return FTFBuddyGroupReplication(); //-- note that CluterWide
				// should work, but will ignore buddy groups.
				throw new UnsupportedOperationException("Not Yet Impl");
			}

		}
		throw new IllegalArgumentException("Could not find proper listener for this configuration, please pass proper one as argument.");
	}

	private class CachePlaceHolder {
		private Cache c;
		private ResourceGroup resourceGroup;
		private FTFReplicationListener listener;

		public CachePlaceHolder() {
			super();
		}

		public CachePlaceHolder(Cache c, ResourceGroup resourceGroup, FTFReplicationListener listener) {
			super();
			this.c = c;
			this.resourceGroup = resourceGroup;
			this.listener = listener;
		}

		public FTFReplicationListener getListener() {
			return listener;
		}

		public void setListener(FTFReplicationListener listener) {
			this.listener = listener;
		}

		public Cache getC() {
			return c;
		}

		public void setC(Cache c) {
			this.c = c;
		}

		public ResourceGroup getResourceGroup() {
			return resourceGroup;
		}

		public void setResourceGroup(ResourceGroup resourceGroup) {
			this.resourceGroup = resourceGroup;
		}


		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((resourceGroup == null) ? 0 : resourceGroup.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			CachePlaceHolder other = (CachePlaceHolder) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (resourceGroup == null) {
				if (other.resourceGroup != null)
					return false;
			} else if (!resourceGroup.equals(other.resourceGroup))
				return false;
			return true;
		}

		private FTFRegistration getOuterType() {
			return FTFRegistration.this;
		}

	}





}
