package org.mobicents.cache;

import org.apache.log4j.Logger;
import org.jboss.cache.Fqn;
import org.jboss.cache.Node;

/**
 * Common base proxy for runtime cached data. 
 * @author martins
 *
 */
public class CacheData {

	private static final Logger logger = Logger.getLogger(CacheData.class);
	
	private Node node;
	private final Fqn nodeFqn;
	
	private boolean isRemoved;
	private final MobicentsCache mobicentsCache;
	
	public CacheData(Fqn nodeFqn, MobicentsCache mobicentsCache) {		
		this.nodeFqn = nodeFqn;	
		this.mobicentsCache = mobicentsCache;
		this.node = mobicentsCache.getJBossCache().getRoot().getChild(nodeFqn);
		if (logger.isDebugEnabled()) {
			logger.debug("cache node "+nodeFqn+" retrieved. node: "+this.node);
		}
	}
	
	/**
	 * Verifies if node where data is stored exists in cache
	 * @return
	 */
	public boolean exists() {
		return node != null;
	}

	/**
	 * Creates node to hold data in cache
	 */
	public boolean create() {
		if (!exists()) {
			node = mobicentsCache.getJBossCache().getRoot().addChild(nodeFqn);
			if (logger.isDebugEnabled()) {
				logger.debug("created cache node "+nodeFqn);
			}
			return true;
		}
		else {
			return false;
		}
	}
	
	/**
	 * Returns true if it was requested to remove the data from cache
	 * @return
	 */
	public boolean isRemoved() {
		return isRemoved;
	}
	
	/**
	 * Removes node that holds data in cache
	 */
	public void remove() {
		if (exists() && !isRemoved()) {
			isRemoved = true;
			node.getParent().removeChild(nodeFqn.getLastElement());	
			if (logger.isDebugEnabled()) {
				logger.debug("removed cache node "+nodeFqn);
			}
		}
	}
	
	/**
	 * 
	 * Retrieves the cache {@link Node} which holds the data in cache
	 * 
	 * Throws {@link IllegalStateException} if remove() was invoked
	 */
	protected Node getNode() {
		if (isRemoved()) {
			throw new IllegalStateException();
		}
		return node;
	}
	
	/**
	 * 
	 * @return
	 */
	public MobicentsCache getMobicentsCache() {
		return mobicentsCache;
	}
	
	/**
	 * Retrieves the node fqn
	 * @return the nodeFqn
	 */
	public Fqn getNodeFqn() {
		return nodeFqn;
	}
}