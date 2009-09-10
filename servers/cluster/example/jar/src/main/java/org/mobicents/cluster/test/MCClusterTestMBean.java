package org.mobicents.cluster.test;

import javax.transaction.TransactionManager;

import org.jboss.ha.cachemanager.CacheManager;
import org.mobicents.ftf.FTFRegistration;
import org.mobicents.ftf.resource.ResourceGroup;
import org.mobicents.slee.core.timers.FaultTolerantScheduler;
import org.mobicents.slee.runtime.cache.MobicentsCache;

public interface MCClusterTestMBean {

	public void setCacheMgr(CacheManager cm);

	public CacheManager getCacheMgr();

	public TransactionManager getJta();

	public void setJta(TransactionManager jta);

	public void setFaultTolerantScheduler(FaultTolerantScheduler fts);

	public FaultTolerantScheduler getFaultTolerantScheduler();

	public void createTimer(long milis);

	public FTFRegistration getFtfRegistration();

	public void setFtfRegistration(FTFRegistration ftfRegistration);

	public ResourceGroup getResourceGroup();

	public void setResourceGroup(ResourceGroup resourceGroup);

	public MobicentsCache getMobicentsCache();

	public void setMobicentsCache(MobicentsCache mobicentsCache);

}
