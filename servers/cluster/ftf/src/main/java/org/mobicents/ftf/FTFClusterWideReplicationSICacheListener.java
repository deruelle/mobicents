/**
 * Start time:08:32:55 2009-08-05<br>
 * Project: jb51-cluster<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */
package org.mobicents.ftf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.transaction.TransactionManager;

import org.apache.log4j.Logger;
import org.jboss.cache.Cache;
import org.jboss.cache.Fqn;
import org.jboss.cache.Node;
import org.jboss.cache.notifications.annotation.BuddyGroupChanged;
import org.jboss.cache.notifications.annotation.CacheListener;
import org.jboss.cache.notifications.annotation.CacheStarted;
import org.jboss.cache.notifications.annotation.CacheStopped;
import org.jboss.cache.notifications.annotation.NodeActivated;
import org.jboss.cache.notifications.annotation.NodeCreated;
import org.jboss.cache.notifications.annotation.NodeEvicted;
import org.jboss.cache.notifications.annotation.NodeInvalidated;
import org.jboss.cache.notifications.annotation.NodeLoaded;
import org.jboss.cache.notifications.annotation.NodeModified;
import org.jboss.cache.notifications.annotation.NodeMoved;
import org.jboss.cache.notifications.annotation.NodePassivated;
import org.jboss.cache.notifications.annotation.NodeRemoved;
import org.jboss.cache.notifications.annotation.NodeVisited;
import org.jboss.cache.notifications.annotation.ViewChanged;
import org.jboss.cache.notifications.event.BuddyGroupChangedEvent;
import org.jboss.cache.notifications.event.CacheBlockedEvent;
import org.jboss.cache.notifications.event.CacheStartedEvent;
import org.jboss.cache.notifications.event.CacheStoppedEvent;
import org.jboss.cache.notifications.event.CacheUnblockedEvent;
import org.jboss.cache.notifications.event.NodeActivatedEvent;
import org.jboss.cache.notifications.event.NodeCreatedEvent;
import org.jboss.cache.notifications.event.NodeEvictedEvent;
import org.jboss.cache.notifications.event.NodeInvalidatedEvent;
import org.jboss.cache.notifications.event.NodeLoadedEvent;
import org.jboss.cache.notifications.event.NodeModifiedEvent;
import org.jboss.cache.notifications.event.NodeMovedEvent;
import org.jboss.cache.notifications.event.NodePassivatedEvent;
import org.jboss.cache.notifications.event.NodeRemovedEvent;
import org.jboss.cache.notifications.event.NodeVisitedEvent;
import org.jboss.cache.notifications.event.TransactionCompletedEvent;
import org.jboss.cache.notifications.event.TransactionRegisteredEvent;
import org.jboss.cache.notifications.event.ViewChangedEvent;
import org.jgroups.View;
import org.jgroups.stack.IpAddress;
import org.mobicents.ftf.election.SingletonElector;
import org.mobicents.ftf.resource.ResourceGroup;
import org.mobicents.ftf.resource.ResourceGroupData;

/**
 * Start time:08:32:55 2009-08-05<br>
 * Project: jb51-cluster<br>
 * Listener that is to be used for culster wide replication(meaning no buddy
 * replication, no data graviation). It will index activity on nodes makring
 * current node as owner(this is semi gravitation behaviour(we dont delete, we
 * just mark)). It will notify if current cluster/cache node is not owner of
 * cache node(index has changed). Indexing is only at node level, there is not
 * reverse indexig, so it has to interate through whole data to check which
 * nodes should be taken over
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */
@CacheListener(sync = false)
public class FTFClusterWideReplicationSICacheListener extends FTFReplicationListener {

	private final transient Logger log = Logger.getLogger(this.getClass());

	// FQN we watch for and index, makes less callbacks to listener
	// private Fqn rootFqnOfChanges;

	// personal?
	public final static String _INDEX_NODE = "_INDEX_NODE_";

	private IpAddress localAddress;

	public FTFClusterWideReplicationSICacheListener(ResourceGroup resourceGroup, Cache watchedCache, TransactionManager txMgr, SingletonElector elector) {
		super();

		super.txMgr = txMgr;
		super.elector = elector;
		super.watchedCache = watchedCache;
		super.resourceGroup = resourceGroup;
		this.localAddress = (IpAddress) this.watchedCache.getLocalAddress();

	}

	public FTFClusterWideReplicationSICacheListener() {
		super();

	}

	// public Fqn getRootFqnOfChanges() {
	// return rootFqnOfChanges;
	// }
	//
	// public void setRootFqnOfChanges(Fqn rootFqnOfChanges) {
	// this.rootFqnOfChanges = Fqn.fromString(rootFqnOfChanges.toString());
	// // dumpFqn(rootFqnOfChanges);
	//
	// }

	// public FTFListener getCallback() {
	// return callback;
	// }
	//
	// public void setCallback(FTFListener callback) {
	// this.callback = callback;
	// }

	// event handlers for cache originated events

	/**
	 * - methods annotated such receive a notification when a node is created.
	 * Methods need to accept a parameter type which is assignable from
	 * NodeCreatedEvent .
	 * 
	 * @param event
	 */
	@NodeCreated
	public void onNodeCreateddEvent(NodeCreatedEvent event) {
		if (log.isDebugEnabled()) {
			log.debug("onNodeCreateddEvent : " + event.getFqn() + " : local[" + event.isOriginLocal() + "] pre[" + event.isPre() + "] : event local address[" + event.getCache().getLocalAddress()
					+ "]");
		}

		if (event.getCache().getLocalAddress() == null) {
			return;
		}
		if (!event.isOriginLocal() || event.isPre()) {
			// we index only local creation, otherwise its other end
			// responsibility to take care of indexing.
			// return, we do that on post?
			return;
		}

		Fqn createdNodeFqn = event.getFqn();
		if (log.isDebugEnabled()) {
			log.debug("onNodeCreateddEvent : [" + event.getFqn().size() + "]" + event.getFqn() + "--->" + this.resourceGroup + "===[" + event.getFqn().size() + "]");
		}

		if (!this.resourceGroup.belongsToResourceGroup(createdNodeFqn.getParent())) {
			// we perofrm index only for direct children, dont we?

			// dumpFqn(createdNodeFqn);
			return;
		}

		// we created it, so its our responsibility
		boolean createdTx = false;
		try {
			if (txMgr != null && txMgr.getTransaction() == null) {
				txMgr.begin();
				createdTx = true;
			}

			event.getCache().put(event.getFqn(), _INDEX_NODE, event.getCache().getLocalAddress());

		} catch (Exception e) {

			e.printStackTrace();
		} finally {
			if (createdTx) {
				try {
					this.txMgr.commit();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	/**
	 * methods annotated such receive a notification when a node is modified.
	 * Methods need to accept a parameter type which is assignable from
	 * NodeModifiedEvent .
	 */
	@NodeModified
	public void onNodeModifiedEvent(NodeModifiedEvent event) {

		if (log.isDebugEnabled()) {
			log.debug("onNodeModifiedEvent : pre[" + event.isPre() + "] : event local address[" + event.getCache().getLocalAddress() + "]");
		}

		if (event.getCache().getLocalAddress() == null) {
			return;
		}
		if (this.resourceGroup.belongsToResourceGroup(event.getFqn().getParent())) {

			// This filters /_FTF_INDEXES_ changes as well as all other changes
			boolean createdTx = false;
			try {
				if (txMgr != null && txMgr.getTransaction() == null) {
					txMgr.begin();
					createdTx = true;
				}

				switch (event.getModificationType()) {
				case PUT_DATA:
					if (event.isOriginLocal()) {
						performLocalPutData(event);
					} else {
						performRemotePutData(event);
					}
					break;
				case PUT_MAP:
					break;
				case REMOVE_DATA:
					break;

				}
			} catch (Exception e) {

				e.printStackTrace();
			} finally {
				if (createdTx) {
					try {
						this.txMgr.commit();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		} else {

		}
	}

	private void performLocalPutData(NodeModifiedEvent event) {
		// local
		if (log.isDebugEnabled()) {
			Map m = event.getData();
			log.debug("onNodeModifiedEvent.local : " + event.getFqn() + " : local[" + event.isOriginLocal() + "] pre[" + event.isPre() + "] modType[" + event.getModificationType() + "] : "
					+ Arrays.toString(m.entrySet().toArray()) + " - EVENT CACHE-> " + Arrays.toString(event.getCache().getNode(event.getFqn()).getData().entrySet().toArray()));
		}

		if (event.isPre()) {

		} else {
			// its, after set,here we can be on
			// put.key = _INDEX_NODE_ - local means we can ignore it, its local
			// op, we are marking teritory
			// put.key = !_INDEX_NODE_ - we have to check if on pre == false,
			// _INDEX_NODE_ == local node, if its not, we need to change it.
			Cache c = event.getCache();
			Node n = c.getNode(event.getFqn());

			// its post set
			Map data = event.getData();
			if (data.containsKey(_INDEX_NODE)) {
				// we mark this as our teritory.
				ResourceGroupData rgd = this.resourceGroup.getData(event.getFqn().getParent());
				if(rgd!=null)
					rgd.getCallback().onNodeOwnershipMoved(event.getFqn(), n.getData(), true);
			} else {
				// we must check if _INDEX_NODE points to local addr. if
				// not, we must change it.

				IpAddress a = (IpAddress) n.get(_INDEX_NODE);
				if (a.compareTo(c.getLocalAddress()) != 0) {
					n.put(_INDEX_NODE, c.getLocalAddress());

				}

			}
		}

	}

	private void performRemotePutData(NodeModifiedEvent event) {
		if (log.isDebugEnabled()) {
			Map m = event.getData();
			log.debug("onNodeModifiedEvent.remote : " + event.getFqn() + " : local[" + event.isOriginLocal() + "] pre[" + event.isPre() + "] modType[" + event.getModificationType() + "] : "
					+ Arrays.toString(m.entrySet().toArray()) + " - EVENT CACHE-> " + Arrays.toString(event.getCache().getNode(event.getFqn()).getData().entrySet().toArray()));
		}

		Cache c = event.getCache();
		Node n = c.getNode(event.getFqn());
		if (event.isPre()) {

		} else {
			// its, after set,here we can be on
			// put.key = _INDEX_NODE_ - local means we have to check if its
			// pointing to local
			// put.key = !_INDEX_NODE_ - we have to check if on pre == false,
			// _INDEX_NODE_ == local node, if its not, we need to change it.

			if (!event.isPre()) {
				// its post set
				Map data = event.getData();
				if (data.containsKey(_INDEX_NODE)) {
					// we mark this as our teritory.
					IpAddress a = (IpAddress) data.get(_INDEX_NODE);
					if (a.compareTo(c.getLocalAddress()) != 0) {
						ResourceGroupData rgd = this.resourceGroup.getData(event.getFqn().getParent());
						if(rgd!=null)
							rgd.getCallback().onNodeOwnershipMoved(event.getFqn(), n.getData(), false);
					}
				} else {

					// we wait, on index change we will react.

				}

			}
		}
	}

	/**
	 * - methods annotated such receive a notification when the group structure
	 * of the cluster changes. Methods need to accept a parameter type which is
	 * assignable from ViewChangedEvent .
	 */
	@ViewChanged
	public void onViewChangeEvent(ViewChangedEvent event) {
		if (log.isDebugEnabled()) {
			log.debug("onViewChangeEvent : pre[" + event.isPre() + "] : event local address[" + event.getCache().getLocalAddress() + "]");
		}
		if (event.getCache().getLocalAddress() == null) {
			return;
		}
		Vector vectorViewMembers = event.getNewView().getMembers();
		Collection c = vectorViewMembers;
		if (this.lastActualView == null) {
			// This means we got this as first event, before start
			this.lastActualView = new ArrayList<IpAddress>(c);
			if (log.isDebugEnabled()) {
				log.debug("onViewChangeEvent : pre[" + event.isPre() + "] : view was empty");
			}
			return;
		}

		View v = event.getNewView();
		// else its view change, now we have to decide if we need to take over
		if (!performTakeOver(v)) {
			// its an extending view, someone has been added, we can safely set
			// view, and term?
			if (log.isDebugEnabled()) {
				log.debug("onViewChangeEvent : pre[" + event.isPre() + "] : skipp takeover");
			}
			this.lastActualView = new ArrayList<IpAddress>(c);
			return;
		}
		List<IpAddress> _lastActualView = new ArrayList<IpAddress>(this.lastActualView);

		// else, someone died, we need to elect one node to take over data
		// ownership
		List<IpAddress> viewMembers = new ArrayList<IpAddress>(c);
		if (log.isDebugEnabled()) {
			log.debug("onViewChangeEvent : pre[" + event.isPre() + "] : viewMembers>" + Arrays.toString(viewMembers.toArray()) + " ---> LAST VIEW: " + Arrays.toString(_lastActualView.toArray()));
		}
		this.lastActualView.clear();
		this.lastActualView.addAll(viewMembers);
		_lastActualView.removeAll(viewMembers);

		if (log.isDebugEnabled()) {
			log.debug("onViewChangeEvent : pre[" + event.isPre() + "] : nodes removed->" + Arrays.toString(_lastActualView.toArray()));
		}

		for (IpAddress lostMember : _lastActualView) {
			// here we have one by one
			boolean createdTx = false;
			try {
				if (txMgr != null && txMgr.getTransaction() == null) {
					txMgr.begin();
					createdTx = true;
				}
				performTakeOver(lostMember, event.getCache());
			} catch (Exception e) {

				e.printStackTrace();
			} finally {
				if (createdTx) {
					try {
						this.txMgr.commit();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}

		return;
	}

	private boolean performTakeOver(View v) {
		Collection c = v.getMembers();
		Set<IpAddress> viewMembers = new HashSet<IpAddress>(c);

		// here we can have
		if (viewMembers.size() > this.lastActualView.size() && viewMembers.containsAll(this.lastActualView) && !this.lastActualView.containsAll(viewMembers)) {
			return false;
		}

		return true;
	}

	/**
	 * Determines if take over attempt should be made.
	 * 
	 * @param lostMember
	 * @param c
	 */
	protected void performTakeOver(IpAddress lostMember, Cache c) {

	
		// This cp nodes gets info on remove before we do, so it can sometimes
		// have less nodes than we do in actual view.
		IpAddress elected = this.elector.elect(c.getMembers());
		
		if (elected.compareTo(c.getLocalAddress()) != 0) {
			// its not us, we dont take over
			if (log.isDebugEnabled()) {
				log.debug("performTakeOver - skipping, node elected:" + elected + ", local:" + c.getLocalAddress());
			}
			return;
		}

		Fqn rootFqnOfChanges = null;
		for (ResourceGroupData rgd : this.resourceGroup.getOrderedResources()) {
			rootFqnOfChanges = rgd.getDataFqn();
			Node rootNode = c.getNode(rootFqnOfChanges);

			Set childrenNames = c.getChildrenNames(rootFqnOfChanges);

			for (Object childName : childrenNames) {
				// Here in values we store data and... inet node., we must match
				// passed one.

				Node dataChildNode = rootNode.getChild(childName);
				if (dataChildNode == null) {
					continue;
				}
				IpAddress ipNode = (IpAddress) dataChildNode.get(_INDEX_NODE);
				if (ipNode == null) {
					continue;
				}
				if (ipNode.equals(lostMember)) {
					dataChildNode.put(_INDEX_NODE, c.getLocalAddress());
				}
			}
		}

	}

	// NOTE USED FOR NOW
	/**
	 * methods annotated such receive a notification when a node is removed.
	 * Methods need to accept a parameter type which is assignable from
	 * NodeRemovedEvent .
	 */
	@NodeRemoved
	public void onNodeRemovedEvent(NodeRemovedEvent event) {

		if (log.isDebugEnabled()) {
			log.debug("onNodeRemovedEvent : " + event.getFqn() + " : local[" + event.isOriginLocal() + "] pre[" + event.isPre() + "] ");
		}

	}

	/**
	 * methods annotated such receive a notification when a node is moved.
	 * Methods need to accept a parameter type which is assignable from
	 * NodeMovedEvent .
	 */
	@NodeMoved
	public void onNodeMovedEvent(NodeMovedEvent event) {
		if (log.isDebugEnabled()) {
			log.debug("onNodeMovedEvent : " + event.getFqn() + " : local[" + event.isOriginLocal() + "] pre[" + event.isPre() + "] ");
		}
	}

	/**
	 * methods annotated such receive a notification when a node is started.
	 * Methods need to accept a parameter type which is assignable from
	 * NodeVisitedEvent .
	 */
	@NodeVisited
	public void onNodeVisitedEvent(NodeVisitedEvent event) {
		if (log.isDebugEnabled()) {
			log.debug("onNodeVisitedEvent : " + event.getFqn() + " : local[" + event.isOriginLocal() + "] pre[" + event.isPre() + "] ");
		}
	}

	/**
	 * methods annotated such receive a notification when a node is loaded from
	 * a CacheLoader . Methods need to accept a parameter type which is
	 * assignable from NodeLoadedEvent .
	 */
	@NodeLoaded
	public void onNodeLoadedEvent(NodeLoadedEvent event) {
		if (log.isDebugEnabled()) {
			log.debug("onNodeLoadedEvent : " + event.getFqn() + " : local[" + event.isOriginLocal() + "] pre[" + event.isPre() + "] ");
		}
	}

	/**
	 * methods annotated such receive a notification when a node is evicted from
	 * memory. Methods need to accept a parameter type which is assignable from
	 * NodeEvictedEvent .
	 */
	@NodeEvicted
	public void onNodeEvictedEvent(NodeEvictedEvent event) {
		if (log.isDebugEnabled()) {
			log.debug("onNodeEvictedEvent : " + event.getFqn() + " : local[" + event.isOriginLocal() + "] pre[" + event.isPre() + "] ");
		}
	}

	/**
	 * - methods annotated such receive a notification when a node is evicted
	 * from memory due to a remote invalidation event. Methods need to accept a
	 * parameter type which is assignable from NodeInvalidatedEvent .
	 */
	@NodeInvalidated
	public void onNodeInvalidatedEvent(NodeInvalidatedEvent event) {
		if (log.isDebugEnabled()) {
			log.debug("onNodeInvalidatedEvent : " + event.getFqn() + " : local[" + event.isOriginLocal() + "] pre[" + event.isPre() + "] ");
		}
	}

	/**
	 * - methods annotated such receive a notification when a node is activated.
	 * Methods need to accept a parameter type which is assignable from
	 * NodeActivatedEvent .
	 */
	@NodeActivated
	public void onNodeActivatedEvent(NodeActivatedEvent event) {

		if (log.isDebugEnabled()) {
			log.debug("onNodeActivatedEvent : " + event.getFqn() + " : local[" + event.isOriginLocal() + "] pre[" + event.isPre() + "] ");
		}
	}

	/**
	 * - methods annotated such receive a notification when a node is
	 * passivated. Methods need to accept a parameter type which is assignable
	 * from NodePassivatedEvent .
	 */
	@NodePassivated
	public void onNodePassivatedEvent(NodePassivatedEvent event) {
		if (log.isDebugEnabled()) {
			log.debug("onNodePassivatedEvent : " + event.getFqn() + " : local[" + event.isOriginLocal() + "] pre[" + event.isPre() + "] ");
		}
	}

	/**
	 * - methods annotated such receive a notification when the cache registers
	 * a javax.transaction.Synchronization with a registered transaction
	 * manager. Methods need to accept a parameter type which is assignable from
	 * TransactionRegisteredEvent .
	 */
	// @TransactionRegistered
	public void onTransactionRegisteredEvent(TransactionRegisteredEvent event) {
		if (log.isDebugEnabled()) {
			log.debug("onTransactionRegisteredEvent : local[" + event.isOriginLocal() + "] pre[" + event.isPre() + "] ");
		}
	}

	/**
	 * - methods annotated such receive a notification when the cache receives a
	 * commit or rollback call from a registered transaction manager. Methods
	 * need to accept a parameter type which is assignable from
	 * TransactionCompletedEvent .
	 */
	// @TransactionCompleted
	public void onTransactionCompletedEvent(TransactionCompletedEvent event) {
		if (log.isDebugEnabled()) {
			log.debug("onTransactionCompletedEvent : pre[" + event.isPre() + "] ");
		}
	}

	/**
	 * - methods annotated such receive a notification when the cluster requests
	 * that cache operations are blocked for a state transfer event. Methods
	 * need to accept a parameter type which is assignable from
	 * CacheBlockedEvent .
	 */
	// @CacheBlocked
	public void onCacheBlockedEvent(CacheBlockedEvent event) {
		if (log.isDebugEnabled()) {
			log.debug("onCacheBlockedEvent : pre[" + event.isPre() + "] ");
		}
	}

	/**
	 * - methods annotated such receive a notification when the cluster requests
	 * that cache operations are unblocked after a state transfer event. Methods
	 * need to accept a parameter type which is assignable from
	 * CacheUnblockedEvent .
	 */
	// @CacheUnblocked
	public void onCacheUnblockedEvent(CacheUnblockedEvent event) {
		if (log.isDebugEnabled()) {
			log.debug("onCacheUnblockedEvent : pre[" + event.isPre() + "] ");
		}
	}

	/**
	 * - methods annotated such receive a notification when a node changes its
	 * buddy group, perhaps due to a buddy falling out of the cluster or a
	 * newer, closer buddy joining. Methods need to accept a parameter type
	 * which is assignable from BuddyGroupChangedEvent.
	 */
	@BuddyGroupChanged
	public void onBuddyGroupChangedEvent(BuddyGroupChangedEvent event) {
		if (log.isDebugEnabled()) {
			log.debug("onBuddyGroupChangedEvent : pre[" + event.isPre() + "] ");
		}
	}

	/**
	 * - methods annotated such receive a notification when the cache is
	 * started. Methods need to accept a parameter type which is assignable from
	 * CacheStartedEvent .
	 */
	@CacheStarted
	public void onCacheStartedEvent(CacheStartedEvent event) {
		if (log.isDebugEnabled()) {
			log.debug("onCacheStartedEvent : pre[" + event.isPre() + "] ");
		}
	}

	/**
	 * - methods annotated such receive a notification when the cache is
	 * stopped. Methods need to accept a parameter type which is assignable from
	 * CacheStoppedEvent .
	 */
	@CacheStopped
	public void onCacheStoppedEvent(CacheStoppedEvent event) {
		if (log.isDebugEnabled()) {
			log.debug("onCacheStoppedEvent : pre[" + event.isPre() + "] ");
		}
	}

}
