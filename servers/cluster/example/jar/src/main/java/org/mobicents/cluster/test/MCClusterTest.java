package org.mobicents.cluster.test;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;

import org.jboss.beans.metadata.api.annotations.Stop;
import org.jboss.cache.Cache;
import org.jboss.cache.factories.annotations.Start;
import org.mobicents.ftf.FTFRegistration;
import org.mobicents.ftf.resource.ResourceGroup;
import org.mobicents.slee.core.timers.FaultTolerantScheduler;
import org.mobicents.slee.core.timers.timer.FaulTolerantTimer;
import org.mobicents.slee.runtime.cache.MobicentsCache;


public class MCClusterTest implements MCClusterTestMBean {

	
	org.jboss.ha.cachemanager.CacheManager cm;
	private TransactionManager jta;


	// private LIstener listener;
	private FaulTolerantTimer faultTolerantTimer;
	private FaultTolerantScheduler fts;
	private FTFRegistration ftfRegistration;
	private ResourceGroup resourceGroup;
	private MobicentsCache mobicentsCache;



	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.jb51.cluster.ClusterTestBean#getCacheMgr()
	 */
	public org.jboss.ha.cachemanager.CacheManager getCacheMgr() {
		return this.cm;
	}

	public void setCacheMgr(org.jboss.ha.cachemanager.CacheManager cm) {
		if (cm != null) {
			// this.listener = new LIstener(this);
			try {
//				cache = cm.getCache(_CACHE_NAME, true);
//				// cache.addCacheListener(this.listener);
//				cache.create();
//				cache.start();

				// t.scheduleAtFixedRate(new CacheTimerTask(), 25000, 25000);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}



	@Start
	public void start() {
		System.err.println("Starting");

		if(ftfRegistration != null && resourceGroup !=null)
		{
			ftfRegistration.registerInFTF(resourceGroup, mobicentsCache.getJBossCache());
		}
		
		
		System.err.println("Started");
	}

	@Stop
	public void stop()
	{
		if(ftfRegistration != null && resourceGroup !=null)
		{
			ftfRegistration.deregisterInFTF(resourceGroup);
		}
	}
	public TransactionManager getJta() {
		return jta;
	}

	public void setJta(TransactionManager jta) {
		this.jta = jta;
	}

	/* (non-Javadoc)
	 * @see org.mobicents.jb51.cluster.MCClusterTestMBean#createTimer(long)
	 */
	public void createTimer(long milis) {

		try {
			jta.begin();
		} catch (NotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.faultTolerantTimer.schedule(new SerTimerTask(), milis);
		try {
			jta.commit();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RollbackException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (HeuristicMixedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (HeuristicRollbackException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	public void setFaultTolerantScheduler(FaultTolerantScheduler fts)
	{
		this.fts = fts;
		this.faultTolerantTimer = new FaulTolerantTimer(fts);
	}
	public FaultTolerantScheduler getFaultTolerantScheduler()
	{
		return this.fts;
	}
	public FTFRegistration getFtfRegistration() {
		return ftfRegistration;
	}

	public void setFtfRegistration(FTFRegistration ftfRegistration) {
		this.ftfRegistration = ftfRegistration;
	}

	public ResourceGroup getResourceGroup() {
		return resourceGroup;
	}

	public void setResourceGroup(ResourceGroup resourceGroup) {
		this.resourceGroup = resourceGroup;
	}

	public MobicentsCache getMobicentsCache() {
		return mobicentsCache;
	}

	public void setMobicentsCache(MobicentsCache mobicentsCache) {
		this.mobicentsCache = mobicentsCache;
	}
	
	
	//static facotry method to get CL
	public static ClassLoader getClassLoader()
	{
		return MCClusterTest.class.getClassLoader();
	}

	
	 
}
