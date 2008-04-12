package org.openxdm.xcap.server.slee.resource.datasource;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

import javax.slee.Address;
import javax.slee.UnrecognizedActivityException;
import javax.slee.resource.ActivityHandle;
import javax.slee.resource.BootstrapContext;
import javax.slee.resource.FailureReason;
import javax.slee.resource.Marshaler;
import javax.slee.resource.ResourceAdaptorTypeID;
import javax.slee.resource.ResourceException;
import javax.slee.resource.SleeEndpoint;

import org.apache.log4j.Logger;
import org.mobicents.slee.container.SleeContainer;
import org.mobicents.slee.resource.ResourceAdaptorActivityContextInterfaceFactory;
import org.mobicents.slee.resource.ResourceAdaptorEntity;
import org.mobicents.slee.resource.ResourceAdaptorState;
import org.openxdm.xcap.common.uri.DocumentSelector;

public abstract class AbstractDataSourceResourceAdaptor implements DataSourceResourceAdaptor, Serializable {
    
 	private static final long serialVersionUID = 1L;
	
 	private ResourceAdaptorState state;    
    private transient ConcurrentHashMap<DocumentActivity,DocumentActivity> activities = new ConcurrentHashMap<DocumentActivity,DocumentActivity>();    
    private transient SleeEndpoint sleeEndpoint;
    private transient BootstrapContext bootstrapContext;
    private transient DataSourceSbbInterfaceInterceptor sbbInterfaceInterceptor;
    
    private transient DataSourceActivityContextInterfaceFactory acif;
    
    @SuppressWarnings("unused")
    private transient int documentUpdatedEventId;
    
    // SLEE 1.1 RA METHODS
    
	public void activityEnded(ActivityHandle ah) {
		Logger logger = getLogger();
		if (logger.isDebugEnabled()) {
			logger.debug("activityEnded(ActivityHandle="+ah+")");
		}		
		// just remove the handle
	    activities.remove(ah);		
	}
	
	public void activityUnreferenced(ActivityHandle ah) {
		Logger logger = getLogger();
		if (logger.isDebugEnabled()) {
			logger.debug("activityUnreferenced");
		}				
		activityEnded(ah);		
	}	
	
	private void endActivity(ActivityHandle handle) throws NullPointerException, IllegalStateException, UnrecognizedActivityException {
		// get activity
		DocumentActivity activity = activities.get(handle);
		if(activity != null) {
			// tell slee to end the activity
			this.sleeEndpoint.activityEnding(activity);			
		}
	}
	
	@SuppressWarnings({"deprecation", "unchecked"})
    public void entityActivated() throws ResourceException {
		
		Logger logger = getLogger();
		if (logger.isDebugEnabled()) {
			logger.debug("entityActivated");
		}			    
		
	    if (this.state != ResourceAdaptorState.UNCONFIGURED) {
	    	String msg = "Cannot configure RA wrong state: " + this.state;
			logger.warn(msg);
			throw new ResourceException(msg);
        }				    						
	   
		try {            
            
			SleeContainer container = SleeContainer.lookupFromJndi();
			ResourceAdaptorEntity resourceAdaptorEntity = ((ResourceAdaptorEntity) container
                .getResourceAdaptorEnitity(this.bootstrapContext.getEntityName()));
			
			ResourceAdaptorTypeID raTypeId = resourceAdaptorEntity
                .getInstalledResourceAdaptor().getRaType().getResourceAdaptorTypeID();			
			
			this.acif = new DataSourceActivityContextInterfaceFactoryImpl(
                resourceAdaptorEntity.getServiceContainer(),
                this.bootstrapContext.getEntityName());
			resourceAdaptorEntity.getServiceContainer().getActivityContextInterfaceFactories().put(raTypeId, this.acif);			
			if (this.acif != null) {
				String jndiName = ((ResourceAdaptorActivityContextInterfaceFactory) this.acif)
				.getJndiName();
				int begind = jndiName.indexOf(':');
				int toind = jndiName.lastIndexOf('/');
				String prefix = jndiName.substring(begind + 1, toind);
				String name = jndiName.substring(toind + 1);
				if (logger.isDebugEnabled()) {
					logger.debug("jndiName prefix =" + prefix + "; jndiName = " + name);
				}
				SleeContainer.registerWithJndi(prefix, name, this.acif);
			}			
			// init sbb interface interceptor
			sbbInterfaceInterceptor = new DataSourceSbbInterfaceInterceptor(this);
		}
        catch (Exception ex) {
           logger.error("entityActivated() failed",ex);
           throw new ResourceException(ex.getMessage());
        }
       
        state = ResourceAdaptorState.ACTIVE;
		
	}
	
	public void entityCreated(BootstrapContext bootstrapContext) throws ResourceException {
		
		Logger logger = getLogger();
		if (logger.isDebugEnabled()) {
			logger.debug("entityCreated");
		}	
		
		this.bootstrapContext = bootstrapContext;
        this.sleeEndpoint = bootstrapContext.getSleeEndpoint();        
        try {
        	this.documentUpdatedEventId = bootstrapContext.getEventLookupFacility().getEventID(DocumentUpdatedEvent.EVENT_NAME,DocumentUpdatedEvent.EVENT_VENDOR,DocumentUpdatedEvent.EVENT_VERSION);
        } catch (Exception e) {
        	throw new ResourceException(e.getMessage());
        }       
        state = ResourceAdaptorState.UNCONFIGURED;		
	}
	
	public void entityDeactivated() {
		
		Logger logger = getLogger();
		if (logger.isDebugEnabled()) {
			logger.debug("entityDeactivated");
		}	
	    
		if(state != ResourceAdaptorState.UNCONFIGURED) {
			// end all activities
			synchronized(activities) {
				for(DocumentActivity activityHandle: activities.keySet()) {
					try {
						endActivity(activityHandle);
					} catch (Exception e) {
						logger.error("unable to end activity: ",e);
					}
				}				
			}
			if (logger.isDebugEnabled()) {
				logger.debug("All activities ended.");
			}
			
			try {
				if (this.acif != null) {
					String jndiName = ((ResourceAdaptorActivityContextInterfaceFactory) this.acif).getJndiName();
					//remove "java:" prefix
					int begind = jndiName.indexOf(':');
					String javaJNDIName = jndiName.substring(begind + 1);
					SleeContainer.unregisterWithJndi(javaJNDIName);
				}	    	
			} catch (Exception e) {
				logger.error("Can't unbind naming context",e);
			}
			
			sbbInterfaceInterceptor = null;
			
			state = ResourceAdaptorState.UNCONFIGURED;
		}
		
        if (logger.isDebugEnabled()) {
        	logger.debug("Resource Adaptor stopped.");
        }
		
	}
	
	public void entityDeactivating() {
		Logger logger = getLogger();
		if (logger.isDebugEnabled()) {
			logger.debug("entityDeactivating");
		}	
		state = ResourceAdaptorState.STOPPING;		
	}
	
	public void entityRemoved() {
		Logger logger = getLogger();
		if (logger.isDebugEnabled()) {
			logger.debug("entityRemoved");
		}			
	}
	
	public void eventProcessingFailed(ActivityHandle arg0, Object arg1, int arg2, Address arg3, int arg4, FailureReason arg5) {
		Logger logger = getLogger();
		if(logger.isDebugEnabled()) {
			String msg = new StringBuilder("eventProcessingFailed(ActivityHandle=").append(arg0).append(",Object=").append(arg1).append(",FailureReason=").append(arg5).append(")").toString();
			logger.warn(msg);
		}			
	}
	
	public void eventProcessingSuccessful(ActivityHandle arg0, Object arg1, int arg2, Address arg3, int arg4) {
		Logger logger = getLogger();
		if(logger.isDebugEnabled()) {
			String msg = new StringBuilder("eventProcessingSuccessful(ActivityHandle=").append(arg0).append(",Object=").append(arg1).append(")").toString();
			logger.debug(msg);
		}
	}
	
	public Object getActivity(ActivityHandle handle) {
		Logger logger = getLogger();
		if (logger.isDebugEnabled()) {
			logger.debug("getActivity");
		}		
	    return activities.get(handle);
	}
	
	public ActivityHandle getActivityHandle(Object activity) {
		Logger logger = getLogger();
		if (logger.isDebugEnabled()) {
			logger.debug("getActivityHandle");
		}		
	    return activities.get(activity);
	}
		
	public Marshaler getMarshaler() {
		Logger logger = getLogger();
		if (logger.isDebugEnabled()) {
			logger.debug("getMarshaler");
		}		
		return null;
	}
	
	public Object getSBBResourceAdaptorInterface(String arg0) {
		Logger logger = getLogger();
		if (logger.isDebugEnabled()) {
			logger.debug("getSBBResourceAdaptorInterface");
		}	
		return this.sbbInterfaceInterceptor;
	}	
	
	public void queryLiveness(ActivityHandle ah) {
		Logger logger = getLogger();
		if (logger.isDebugEnabled()) {
			logger.debug("queryLiveness");
		}	
		// if handle does not exist fire an activity end event
		if(!activities.containsKey(ah)) {
			try {
				endActivity(ah);
			} catch (Exception e) {
				logger.error("unable to end activity with handle: "+ah);
			}
		}	
	}
	
	public void serviceActivated(String arg0) {
		// DO NOTHING		
	}
	
	public void serviceDeactivated(String arg0) {
		// DO NOTHING		
	}
	
	public void serviceInstalled(String arg0, int[] arg1, String[] arg2) {
		// DO NOTHING		
	}
	
	public void serviceUninstalled(String arg0) {
		// DO NOTHING		
	}
	
	// ABSTRACT METHODS
	
	public abstract DataSourceSbbInterface getDataSourceSbbInterface();
	
	public abstract Logger getLogger();
	
	// THIS RA LOGIC
	
	public void postEvent(DocumentUpdatedEvent event, DocumentActivity activity) {
		// TODO Auto-generated method stub		
	}
	
	public DocumentActivity createDocumentActivity(DocumentSelector documentSelector) {
		Logger logger = getLogger();
		if (logger.isDebugEnabled()) {
			logger.debug("createDocumentActivity");
		}	
		DocumentActivity activity = new DocumentActivity(documentSelector);
		activities.put(activity,activity);
		return activity;
	}
	
	public void endDocumentActivity(DocumentSelector documentSelector) {
		Logger logger = getLogger();
		if (logger.isDebugEnabled()) {
			logger.debug("endDocumentActivity");
		}		
		try {
			endActivity(new DocumentActivity(documentSelector));
		} catch (Exception e) {
			logger.error("unable to end activity: ",e);
		}
	}
	
	public DocumentActivity getDocumentActivity(DocumentSelector documentSelector) {
		Logger logger = getLogger();
		if (logger.isDebugEnabled()) {
			logger.debug("getDocumentActivity");
		}
		return activities.get(new DocumentActivity(documentSelector));
	}
	
}
