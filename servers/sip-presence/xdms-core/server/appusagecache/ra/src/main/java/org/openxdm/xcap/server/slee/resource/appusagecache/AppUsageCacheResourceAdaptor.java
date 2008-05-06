package org.openxdm.xcap.server.slee.resource.appusagecache;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

import javax.slee.Address;
import javax.slee.resource.ActivityHandle;
import javax.slee.resource.BootstrapContext;
import javax.slee.resource.FailureReason;
import javax.slee.resource.Marshaler;
import javax.slee.resource.ResourceAdaptor;
import javax.slee.resource.ResourceException;

import org.apache.log4j.Logger;

/**
 * This is the OPENXDM Resource Adaptor's Implementation.
 * 
 * @author Eduardo Martins
 * @version 1.0
 * 
 */

public class AppUsageCacheResourceAdaptor implements ResourceAdaptor,
		Serializable {
    
 	private static final long serialVersionUID = 1L;
	static private transient Logger log;
    static {
        log = Logger.getLogger(AppUsageCacheResourceAdaptor.class);
    }
    
    private transient ConcurrentHashMap<String,AppUsagePool> pools;    
    private transient int appUsagePoolSize = 25;
    private transient boolean isActive = false;
    private transient AppUsageCacheResourceAdaptorSbbInterface sbbInterface;
       
    public AppUsageCacheResourceAdaptor() { }
    
    public void entityCreated(BootstrapContext ctx) throws javax.slee.resource.ResourceException {    	
	    if (log.isDebugEnabled()) {
	    	log.debug("entityCreated");
	    }
	}

	public void entityRemoved() {
		 if (log.isDebugEnabled()) {
	    	log.debug("entityRemoved");
	    }
	}

	public void entityActivated() throws ResourceException {
	    if (log.isDebugEnabled()) {
	    	log.debug("entityActivated");
	    }	   
	     if (!isActive) {
	    	// create pool map
	    	pools = new ConcurrentHashMap<String,AppUsagePool>();  
			// init sbb interface
			sbbInterface = new AppUsageCacheResourceAdaptorSbbInterfaceImpl(this);
			isActive = true;
	    }		        
	}

	public void entityDeactivating() {}

	public void entityDeactivated() {
		if (log.isDebugEnabled()) {
			log.debug("entityDeactivated");
		}
		if(isActive) {
        	pools = null;
        	sbbInterface = null;
        	isActive = false;
        }      
	}

	public void eventProcessingSuccessful(ActivityHandle arg0, Object arg1,
			int arg2, Address arg3, int arg4) {}

	public void eventProcessingFailed(ActivityHandle arg0, Object arg1,
			int arg2, Address arg3, int arg4, FailureReason arg5) {}

	public void endActivity(ActivityHandle ah) {}
	
	public void activityEnded(ActivityHandle ah) {}

	public void activityUnreferenced(ActivityHandle ah) {}

	public void queryLiveness(ActivityHandle ah) {}

	public Object getActivity(ActivityHandle handle) {
		return null;
	}

	public ActivityHandle getActivityHandle(Object activity) {
		return null;
	}

	public Object getSBBResourceAdaptorInterface(String arg0) {
		if (log.isDebugEnabled()) {
			log.debug("getSBBResourceAdaptorInterface");
		}
		return this.sbbInterface;
	}

	public Marshaler getMarshaler() {		
		return null;
	}

	public void serviceInstalled(String arg0, int[] arg1, String[] arg2) {}

	public void serviceActivated(String arg0) {}

	public void serviceDeactivated(String arg0) {}

	public void serviceUninstalled(String arg0) {}
        
    public ConcurrentHashMap<String, AppUsagePool> getPools() {
		return pools;
	}
    
    public int getAppUsagePoolSize() {
		return appUsagePoolSize;
	}
    
}

