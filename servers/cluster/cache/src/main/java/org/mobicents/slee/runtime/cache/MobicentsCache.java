package org.mobicents.slee.runtime.cache;

import javax.slee.ServiceID;
import javax.transaction.TransactionManager;

import org.apache.log4j.Logger;
import org.jboss.cache.Cache;
import org.jboss.cache.CacheFactory;
import org.jboss.cache.CacheStatus;
import org.jboss.cache.Fqn;
import org.jboss.cache.Region;
import org.jboss.cache.config.Configuration;
import org.jboss.cache.config.Configuration.CacheMode;
import org.jboss.ha.cachemanager.CacheManager;

/**
 * The container's HA and FT data source.
 * 
 * @author martins
 * 
 */
public class MobicentsCache {

	private static Logger logger = Logger.getLogger(MobicentsCache.class);

	private final Cache<?,?> jBossCache;
	private TimerTasksCacheData timerTasksCacheData;
	private CacheManager haCacheManager;
	private String cacheName;
	private TransactionManager txManager;
	public MobicentsCache(CacheFactory cacheFactory, Configuration cacheConfiguration, TransactionManager txManager) {
		//we must ensure that
		cacheConfiguration.setInactiveOnStartup(true);
		if(logger.isInfoEnabled())
			logger.info("SLEE Cache creation.");
		this.jBossCache = cacheFactory.createCache();
		this.txManager = txManager;
	
		//this.timerTasksCacheData = new TimerTasksCacheData(jBossCache,txManager);
		if(logger.isInfoEnabled())
			logger.info("SLEE Cache created. Mode:"+jBossCache.getConfiguration().getCacheMode());

	}

	public MobicentsCache(CacheManager haCacheManager,String cacheName, TransactionManager txManager) throws Exception {
		
		this.haCacheManager = haCacheManager;
		this.cacheName = cacheName;
		this.jBossCache = this.haCacheManager.getCache(cacheName, true);
		this.txManager = txManager;
		//this.timerTasksCacheData = new TimerTasksCacheData(jBossCache,txManager);
		
		if(logger.isInfoEnabled())
			logger.info("SLEE Cache created.");
	}

	public Cache<?, ?> getJBossCache() {
		return jBossCache;
	}

	
	/**
	 * Retrieves an instance of an {@link ActivityContextCacheData} for the
	 * Activity Context with the specified id.
	 * 
	 * @param activityContextHandle
	 * @return
	 */
	public ActivityContextCacheData getActivityContextCacheData(
			Object activityContextHandle) {
		return new ActivityContextCacheData(activityContextHandle, jBossCache);
	}

	/**
	 * Retrieves an instance of an {@link ActivityContextFactoryCacheData}, the
	 * cache proxy for the Activity Context Factory
	 * 
	 * @return
	 */
	public ActivityContextFactoryCacheData getActivityContextFactoryCacheData() {
		return new ActivityContextFactoryCacheData(jBossCache);
	}

	/**
	 * Retrieves an instance of an
	 * {@link ActivityContextNamingFacilityCacheData}, the cache proxy for the
	 * Activity Context Naming Facility
	 * 
	 * @return
	 */
	public ActivityContextNamingFacilityCacheData getActivityContextNamingFacilityCacheData() {
		return new ActivityContextNamingFacilityCacheData(jBossCache);
	}

	/**
	 * Retrieves an instance of an {@link SbbEntityCacheData}, the cache proxy
	 * for the sbb entity table with the specified id.
	 * 
	 * @param sbbEntityId
	 * @return
	 */
	public SbbEntityCacheData getSbbEntityCacheData(String sbbEntityId) {
		return new SbbEntityCacheData(sbbEntityId, jBossCache);
	}

	/**
	 * Retrieves an instance of an {@link ServiceCacheData}, the cache proxy
	 * for the service with the specified id.
	 * 
	 * @param serviceID
	 * @return
	 */
	public ServiceCacheData getServiceCacheData(ServiceID serviceID) {
		return new ServiceCacheData(serviceID, jBossCache);
	}

	/**
	 * Retrieves an instance of an {@link TimerTasksCacheData}, the cache
	 * proxy for the Timer Facility
	 * 
	 * @return
	 */
	public TimerTasksCacheData getTimerFacilityCacheData() {
		if(this.timerTasksCacheData == null)
		{
			this.timerTasksCacheData = new TimerTasksCacheData(jBossCache,txManager);
		}
		return this.timerTasksCacheData;
	}
	/**
	 * @param classLoader
	 */
	
	
	
	
	public void startRootRegion(ClassLoader classLoader) {
		Region rootRegion=this.jBossCache.getRegion(Fqn.ROOT, true);
		//tmp?
		
		rootRegion.registerContextClassLoader(classLoader);
		
		//jBossCache.create();
		if(jBossCache.getCacheStatus()!= CacheStatus.STARTED)
			jBossCache.start();
		rootRegion.activate();
		if(logger.isInfoEnabled())
			logger.info("SLEE Cache started, status: "+this.jBossCache.getCacheStatus()+", Mode: "+this.jBossCache.getConfiguration().getCacheModeString());
		
		
	}
	
	
	public void stop()
	{
		if(this.haCacheManager!=null)
		{
			this.haCacheManager.releaseCache(this.cacheName);
			//manager will take care of stoping, if not requireed anymore.
		}else
		{
			this.jBossCache.stop();
			this.jBossCache.destroy();
		}
	}
	
}
