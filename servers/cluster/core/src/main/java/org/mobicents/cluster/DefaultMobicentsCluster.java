package org.mobicents.cluster;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.transaction.TransactionManager;

import org.apache.log4j.Logger;
import org.jboss.cache.Cache;
import org.jboss.cache.Fqn;
import org.jboss.cache.config.Configuration.CacheMode;
import org.jboss.cache.notifications.annotation.CacheListener;
import org.jboss.cache.notifications.annotation.ViewChanged;
import org.jboss.cache.notifications.event.ViewChangedEvent;
import org.jgroups.Address;
import org.mobicents.cache.MobicentsCache;
import org.mobicents.cluster.cache.ClusteredCacheData;
import org.mobicents.cluster.cache.ClusteredCacheDataIndexingHandler;
import org.mobicents.cluster.cache.DefaultClusteredCacheDataIndexingHandler;
import org.mobicents.cluster.election.SingletonElector;

/**
 * Listener that is to be used for cluster wide replication(meaning no buddy
 * replication, no data gravitation). It will index activity on nodes marking
 * current node as owner(this is semi-gravitation behavior (we don't delete, we
 * just mark)). 
 * 
 * Indexing is only at node level, i.e., there is not
 * reverse indexing, so it has to iterate through whole resource group data FQNs to check which
 * nodes should be taken over.
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 * @author martins
 */

@CacheListener(sync = false)
public class DefaultMobicentsCluster implements MobicentsCluster {

	private static final Logger logger = Logger.getLogger(DefaultMobicentsCluster.class);

	private final SortedSet<ClientLocalListener> localListeners;
	private final MobicentsCache mobicentsCache;
	private final TransactionManager txMgr;
	private final SingletonElector elector;
	private final DefaultClusteredCacheDataIndexingHandler clusteredCacheDataIndexingHandler;
	
	private List<Address> currentView;
	
	public DefaultMobicentsCluster(MobicentsCache watchedCache, TransactionManager txMgr, SingletonElector elector) {
		this.localListeners = Collections.synchronizedSortedSet(new TreeSet<ClientLocalListener>(new ClientLocalListenerComparator()));
		this.mobicentsCache = watchedCache;
		final Cache cache = watchedCache.getJBossCache();
		if (!cache.getConfiguration().getCacheMode().equals(CacheMode.LOCAL)) {
			// get current cluster members
			currentView = new ArrayList<Address>(cache.getConfiguration().getRuntimeConfig().getChannel().getView().getMembers());
			// start listening to events
			cache.addCacheListener(this);		
		}
		this.txMgr = txMgr;
		this.elector = elector;
		this.clusteredCacheDataIndexingHandler = new DefaultClusteredCacheDataIndexingHandler();
	}

	/* (non-Javadoc)
	 * @see org.mobicents.cluster.MobicentsCluster#getLocalAddress()
	 */
	public Address getLocalAddress() {
		return mobicentsCache.getJBossCache().getLocalAddress();
	}
	
	/* (non-Javadoc)
	 * @see org.mobicents.cluster.MobicentsCluster#getClusterMembers()
	 */
	public List<Address> getClusterMembers() {
		if (currentView != null) {
			return Collections.unmodifiableList(currentView);
		}
		else {
			Address localAddress = getLocalAddress();
			if (localAddress == null) {
				return Collections.emptyList();
			}
			else {
				List<Address> list = new ArrayList<Address>();
				list.add(localAddress);
				return Collections.unmodifiableList(list);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.mobicents.cluster.MobicentsCluster#isHeadMember()
	 */
	public boolean isHeadMember() {
		final Address localAddress = getLocalAddress();
		if (localAddress != null) {
			final List<Address> clusterMembers = getClusterMembers();
			return !clusterMembers.isEmpty() && clusterMembers.get(0).equals(localAddress);
		}
		else {
			return true;
		}
	}
	
	/**
	 * Method handle a change on the cluster members set
	 * @param event
	 */
	@ViewChanged
	public synchronized void onViewChangeEvent(ViewChangedEvent event) {
		
		if (logger.isDebugEnabled()) {
			logger.debug("onViewChangeEvent : pre[" + event.isPre() + "] : event local address[" + event.getCache().getLocalAddress() + "]");
		}
				
		final List<Address> oldView = currentView;
		currentView = new ArrayList<Address>(event.getNewView().getMembers());
		final Address localAddress = getLocalAddress();
		
		// recover stuff from lost members
		Runnable runnable = new Runnable() {
			public void run() {
				for (Address oldMember : oldView) {
					if (!currentView.contains(oldMember)) {
						// this member is gone  
						if (logger.isDebugEnabled()) {
							logger.debug("onViewChangeEvent : processing lost member " + oldMember);
						}				
						if (elector.elect(currentView).equals(localAddress)) {
							// local member was elected to take over the work being done by the lost member
							performTakeOver(oldMember,localAddress);
						}															
					}
				}
			}
		};
		Thread t = new Thread(runnable);
		t.start();
		
	}

	/**
	 * 
	 */
	private void performTakeOver(Address lostMember, Address localAddress) {
	
		if (logger.isDebugEnabled()) {
			logger.debug("onViewChangeEvent : failing over lost member " + lostMember);
		}
		
		final Cache jbossCache = mobicentsCache.getJBossCache();
		
		for (ClientLocalListener localListener : this.localListeners) {
			
			final Fqn rootFqnOfChanges = localListener.getBaseFqn();
			
			boolean createdTx = false;
			boolean doRollback = true;
			
			try {
				if (txMgr != null && txMgr.getTransaction() == null) {
					txMgr.begin();
					createdTx = true;
				}
											
				localListener.failOverClusterMember(lostMember);
				
				for (Object childName : jbossCache.getChildrenNames(rootFqnOfChanges)) {
					// Here in values we store data and... inet node., we must match
					// passed one.
					final ClusteredCacheData clusteredCacheData = new ClusteredCacheData(Fqn.fromRelativeElements(rootFqnOfChanges, childName),this);
					if (clusteredCacheData.exists()) {
						Address address = clusteredCacheData.getClusterNodeAddress();
						if (address != null && address.equals(lostMember)) {
							// call back the listener
							localListener.wonOwnership(clusteredCacheData);
							// change ownership
							clusteredCacheData.setClusterNodeAddress(localAddress);							
						}					
					}
				}
				doRollback = false;
				
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
				
			} finally {
				if (createdTx) {					
					try {
						if (!doRollback) {
							txMgr.commit();
						}
						else {
							txMgr.rollback();
						}
					} catch (Exception e) {
						logger.error(e.getMessage(),e);
					}
				}
			}
		}

	}

	// NOTE USED FOR NOW
	
	/*
	@NodeCreated
	public void onNodeCreateddEvent(NodeCreatedEvent event) {
		if (log.isDebugEnabled()) {
			log.debug("onNodeCreateddEvent : " + event.getFqn() + " : local[" + event.isOriginLocal() + "] pre[" + event.isPre() + "] : event local address[" + event.getCache().getLocalAddress()
					+ "]");
		}
	}

	@NodeModified
	public void onNodeModifiedEvent(NodeModifiedEvent event) {
		if (log.isDebugEnabled()) {
			log.debug("onNodeModifiedEvent : pre[" + event.isPre() + "] : event local address[" + event.getCache().getLocalAddress() + "]");
		}
	}
	
	@NodeRemoved
	public void onNodeRemovedEvent(NodeRemovedEvent event) {
		if (log.isDebugEnabled()) {
			log.debug("onNodeRemovedEvent : " + event.getFqn() + " : local[" + event.isOriginLocal() + "] pre[" + event.isPre() + "] ");
		}
	}

	@NodeMoved
	public void onNodeMovedEvent(NodeMovedEvent event) {
		if (log.isDebugEnabled()) {
			log.debug("onNodeMovedEvent : " + event.getFqn() + " : local[" + event.isOriginLocal() + "] pre[" + event.isPre() + "] ");
		}
	}

	@NodeVisited
	public void onNodeVisitedEvent(NodeVisitedEvent event) {
		if (log.isDebugEnabled()) {
			log.debug("onNodeVisitedEvent : " + event.getFqn() + " : local[" + event.isOriginLocal() + "] pre[" + event.isPre() + "] ");
		}
	}

	@NodeLoaded
	public void onNodeLoadedEvent(NodeLoadedEvent event) {
		if (log.isDebugEnabled()) {
			log.debug("onNodeLoadedEvent : " + event.getFqn() + " : local[" + event.isOriginLocal() + "] pre[" + event.isPre() + "] ");
		}
	}

	@NodeEvicted
	public void onNodeEvictedEvent(NodeEvictedEvent event) {
		if (log.isDebugEnabled()) {
			log.debug("onNodeEvictedEvent : " + event.getFqn() + " : local[" + event.isOriginLocal() + "] pre[" + event.isPre() + "] ");
		}
	}

	@NodeInvalidated
	public void onNodeInvalidatedEvent(NodeInvalidatedEvent event) {
		if (log.isDebugEnabled()) {
			log.debug("onNodeInvalidatedEvent : " + event.getFqn() + " : local[" + event.isOriginLocal() + "] pre[" + event.isPre() + "] ");
		}
	}

	@NodeActivated
	public void onNodeActivatedEvent(NodeActivatedEvent event) {

		if (log.isDebugEnabled()) {
			log.debug("onNodeActivatedEvent : " + event.getFqn() + " : local[" + event.isOriginLocal() + "] pre[" + event.isPre() + "] ");
		}
	}

	@NodePassivated
	public void onNodePassivatedEvent(NodePassivatedEvent event) {
		if (log.isDebugEnabled()) {
			log.debug("onNodePassivatedEvent : " + event.getFqn() + " : local[" + event.isOriginLocal() + "] pre[" + event.isPre() + "] ");
		}
	}

	@BuddyGroupChanged
	public void onBuddyGroupChangedEvent(BuddyGroupChangedEvent event) {
		if (log.isDebugEnabled()) {
			log.debug("onBuddyGroupChangedEvent : pre[" + event.isPre() + "] ");
		}
	}

	@CacheStarted
	public void onCacheStartedEvent(CacheStartedEvent event) {
		if (log.isDebugEnabled()) {
			log.debug("onCacheStartedEvent : pre[" + event.isPre() + "] ");
		}
	}

	@CacheStopped
	public void onCacheStoppedEvent(CacheStoppedEvent event) {
		if (log.isDebugEnabled()) {
			log.debug("onCacheStoppedEvent : pre[" + event.isPre() + "] ");
		}
	}
	*/

	// LOCAL LISTENERS MANAGEMENT
	
	/*
	 * (non-Javadoc)
	 * @see org.mobicents.cluster.MobicentsCluster#addLocalListener(org.mobicents.cluster.ClientLocalListener)
	 */
	public boolean addLocalListener(ClientLocalListener localListener) {
		if (logger.isDebugEnabled()) {
			logger.debug("Adding local listener " + localListener);
		}
		return localListeners.add(localListener);		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mobicents.cluster.MobicentsCluster#removeLocalListener(org.mobicents.cluster.ClientLocalListener)
	 */
	public boolean removeLocalListener(ClientLocalListener localListener) {
		if (logger.isDebugEnabled()) {
			logger.debug("Removing local listener " + localListener);
		}
		return localListeners.remove(localListener);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mobicents.cluster.MobicentsCluster#getMobicentsCache()
	 */
	public MobicentsCache getMobicentsCache() {
		return mobicentsCache;
	}
	
	/* (non-Javadoc)
	 * @see org.mobicents.cluster.MobicentsCluster#getClusteredCacheDataIndexingHandler()
	 */
	public ClusteredCacheDataIndexingHandler getClusteredCacheDataIndexingHandler() {
		return clusteredCacheDataIndexingHandler;
	}
}
