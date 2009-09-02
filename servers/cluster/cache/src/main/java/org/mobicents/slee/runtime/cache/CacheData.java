package org.mobicents.slee.runtime.cache;

import org.apache.log4j.Logger;
import org.jboss.cache.Cache;
import org.jboss.cache.CacheStatus;
import org.jboss.cache.Fqn;
import org.jboss.cache.Node;

/**
 * Common base proxy for runtime cached data. 
 * @author martins
 *
 */
public class CacheData {

	private static final Logger log = Logger.getLogger(CacheData.class);
	
	private Node node;
	protected final Fqn nodeFqn;
	
	private boolean isRemoved;
	private final Cache jBossCache;
	
	protected CacheData(Fqn nodeFqn, Cache jBossCache) {		
		this.nodeFqn = nodeFqn;	
		this.jBossCache = jBossCache;
		//in case cache is in stopped state, this would cause exception
		//if(this.jBossCache.getCacheStatus() == CacheStatus.STARTED)
		this.node = this.jBossCache.getRoot().getChild(nodeFqn);
		if (log.isDebugEnabled()) {
			log.debug("cache node "+nodeFqn+" retrieved. node: "+this.node);
		}
	}
	
	
	/**
	 * Verifies if node where data is stored exists in cache
	 * @return
	 */
	public boolean exists() {
//		if(node == null)
//		{
//			if(this.jBossCache.getCacheStatus() == CacheStatus.STARTED)
//			{
//				return this.jBossCache.getRoot().hasChild(this.nodeFqn);
//			}else
//			{
//				return false;
//			}
//		}else
//		{
//			return true;
//		}
		return this.node!=null;
	}

	/**
	 * Creates node to hold data in cache
	 */
	 public void _create() {
		if (!exists()) {
			node = jBossCache.getRoot().addChild(nodeFqn);
			if (log.isDebugEnabled()) {
				log.debug("created cache node "+nodeFqn);
			}
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
			if (log.isDebugEnabled()) {
				log.debug("removed cache node "+nodeFqn);
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
		//deffered fetch, since in some cases on create cache may be still stopped
//		if(node == null && this.jBossCache.getCacheStatus() == CacheStatus.STARTED)
//		{
//			node = this.jBossCache.getNode(nodeFqn);
//		}
		return node;
	}
	
	/**
	 * 
	 * @return
	 */
	public Cache getCache() {
		return jBossCache;
	}
}
