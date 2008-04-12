package org.openxdm.xcap.server.slee.resource.appusagecache;

import org.openxdm.xcap.common.appusage.AppUsage;
import org.openxdm.xcap.common.appusage.AppUsageFactory;


/**
 * This is the OPENXDM Resource Adaptor's Interface that Sbbs can use to cache app usages.
 * 
 * @author Eduardo Martins
 * @version 1.0
 * 
 */

public interface AppUsageCacheResourceAdaptorSbbInterface {
	
	/**
	 * Caches an appusage using the factory to generate instances into a concurrency pool.
	 * @param appUsageFactory
	 */
	public void put(AppUsageFactory appUsageFactory);
	
	/**
	 * Borrows an instance of the app usage with the specified id. 
	 * @param auid
	 * @return null if there is no app usage with such id in cache.
	 * @throws InterruptedException if pool exist but borrow operation failed.
	 */
	public AppUsage borrow(String auid) throws InterruptedException;
	
	/**
	 * Returns an instance of the app usage to the cache.
	 * @param appUsage
	 */
	public void release(AppUsage appUsage);
	
	/**
	 * Removes the app usage from cache with the specified id
	 * @param auid
	 */
	public void remove(String auid);
	
}
