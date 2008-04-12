package org.openxdm.xcap.server.slee.resource.appusagecache;

import org.apache.log4j.Logger;
import org.openxdm.xcap.common.appusage.AppUsage;
import org.openxdm.xcap.common.appusage.AppUsageFactory;

public class AppUsageCacheResourceAdaptorSbbInterfaceImpl implements AppUsageCacheResourceAdaptorSbbInterface {
    
	private AppUsageCacheResourceAdaptor ra;
	private static Logger logger = Logger.getLogger(AppUsageCacheResourceAdaptorSbbInterface.class);
	
	public AppUsageCacheResourceAdaptorSbbInterfaceImpl(AppUsageCacheResourceAdaptor ra) {
		this.ra = ra;
	}

	public void put(AppUsageFactory appUsageFactory) {
		// create new pool
		AppUsagePool pool = new AppUsagePool(appUsageFactory,ra.getAppUsagePoolSize());
		// add to pools atomically, if not there
		ra.getPools().putIfAbsent(appUsageFactory.getAppUsageId(),pool);
		if(logger.isDebugEnabled()) {
			logger.debug("created pool for app usage "+appUsageFactory.getAppUsageId());
		}
	}

	public AppUsage borrow(String auid) throws InterruptedException {
		if(logger.isDebugEnabled()) {
			logger.debug("borrow(auid="+auid+")");
		}		
		AppUsagePool pool = ra.getPools().get(auid);
		if (pool != null) {
			return pool.borrow();
		}
		else {
			return null;
		}		
	}

	public void release(AppUsage appUsage) {
		if(logger.isDebugEnabled()) {
			logger.debug("release(auid="+appUsage.getAUID()+")");
		}		
		AppUsagePool pool = ra.getPools().get(appUsage.getAUID());
		if (pool != null) {
			pool.release(appUsage);
		}				
	}

	public void remove(String auid) {
		if(logger.isDebugEnabled()) {
			logger.debug("remove(auid="+auid+")");
		}		
		ra.getPools().remove(auid);		
	}	
	
}