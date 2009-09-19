package org.mobicents.cache;

import org.apache.log4j.Logger;
import org.jboss.cache.Cache;
import org.jboss.cache.CacheManager;
import org.jboss.cache.DefaultCacheFactory;
import org.jboss.cache.Fqn;
import org.jboss.cache.Region;
import org.jboss.cache.config.Configuration;
import org.jboss.cache.config.Configuration.CacheMode;

/**
 * The container's HA and FT data source.
 * 
 * @author martins
 * 
 */
public class MobicentsCache {

	private static Logger logger = Logger.getLogger(MobicentsCache.class);

	private final Cache jBossCache;
	private boolean localMode;
	private final boolean managedCache;
	
	@SuppressWarnings("unchecked")
	public MobicentsCache(Configuration cacheConfiguration) {
		this.jBossCache = new DefaultCacheFactory().createCache(cacheConfiguration,false);
		this.managedCache = false;
		startCache();	
	}

	public MobicentsCache(CacheManager haCacheManager, String cacheName) throws Exception {
		this.jBossCache = haCacheManager.getCache(cacheName, true);
		this.jBossCache.create();
		this.managedCache = true;
		startCache();
	}
	
	private void startCache() {
		if (jBossCache.getConfiguration().getCacheMode() == CacheMode.LOCAL) {
			localMode = true;
		}
		final Region rootRegion = jBossCache.getRegion(Fqn.ROOT, true);
		rootRegion.registerContextClassLoader(Thread.currentThread().getContextClassLoader());
		jBossCache.start();
		rootRegion.activate();
		
		if (logger.isInfoEnabled()) {
			logger.info("Mobicents Cache started, status: " + this.jBossCache.getCacheStatus() + ", Mode: " + this.jBossCache.getConfiguration().getCacheModeString());
		}
	}
	
	public Cache getJBossCache() {
		return jBossCache;
	}
	
	public void stop() {
		if (!managedCache) {
			this.jBossCache.stop();
			this.jBossCache.destroy();
		}
	}

	/**
	 *  
	 * @return the localMode
	 */
	public boolean isLocalMode() {
		return localMode;
	}
}
