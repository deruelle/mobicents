package org.openxdm.xcap.server.slee.resource.datasource;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

import javax.slee.Address;
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
import org.openxdm.xcap.common.datasource.DataSource;
import org.openxdm.xcap.common.uri.DocumentSelector;

public abstract class AbstractDataSourceResourceAdaptor implements DataSourceResourceAdaptor, Serializable {
    
 	private static final long serialVersionUID = 1L;
	
 	private ResourceAdaptorState state;    
    private transient ConcurrentHashMap<ActivityHandle,ActivityObject> activities = new ConcurrentHashMap<ActivityHandle,ActivityObject>();    
    private transient SleeEndpoint sleeEndpoint;
    private transient BootstrapContext bootstrapContext;
    private transient DataSourceSbbInterface sbbInterface;
    
    private transient DataSourceActivityContextInterfaceFactory acif;
    
    private transient int documentUpdatedEventId;
    private transient int elementUpdatedEventId;
    private transient int attributeUpdatedEventId;
    
    // SLEE 1.1 RA METHODS
    
	public void activityEnded(javax.slee.resource.ActivityHandle ah) {
		Logger logger = getLogger();
		if (logger.isDebugEnabled()) {
			logger.debug("activityEnded(ActivityHandle="+ah+")");
		}		
		// just remove the handle
	    activities.remove(ah);		
	}
	
	public void activityUnreferenced(javax.slee.resource.ActivityHandle ah) {
		Logger logger = getLogger();
		if (logger.isDebugEnabled()) {
			logger.debug("activityUnreferenced");
		}				
		// no need to keep activities that have no entities attached
		endActivity((ActivityHandle)ah);		
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
			sbbInterface = new DataSourceSbbInterface(this);
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
        	documentUpdatedEventId = bootstrapContext.getEventLookupFacility().getEventID(DocumentUpdatedEvent.EVENT_NAME,DocumentUpdatedEvent.EVENT_VENDOR,DocumentUpdatedEvent.EVENT_VERSION);
        	elementUpdatedEventId = bootstrapContext.getEventLookupFacility().getEventID(ElementUpdatedEvent.EVENT_NAME,ElementUpdatedEvent.EVENT_VENDOR,ElementUpdatedEvent.EVENT_VERSION);
        	attributeUpdatedEventId = bootstrapContext.getEventLookupFacility().getEventID(AttributeUpdatedEvent.EVENT_NAME,AttributeUpdatedEvent.EVENT_VENDOR,AttributeUpdatedEvent.EVENT_VERSION);
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
				for(ActivityHandle activityHandle: activities.keySet()) {
					endActivity(activityHandle);
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
			
			sbbInterface = null;
			
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
	
	public void eventProcessingFailed(javax.slee.resource.ActivityHandle arg0, Object arg1, int arg2, Address arg3, int arg4, FailureReason arg5) {
		getLogger().warn("eventProcessingFailed(ActivityHandle="+arg0+")");			
	}
	
	public void eventProcessingSuccessful(javax.slee.resource.ActivityHandle arg0, Object arg1, int arg2, Address arg3, int arg4) {
		Logger logger = getLogger();
		if(logger.isDebugEnabled()) {
			getLogger().debug("eventProcessingSuccessful(ActivityHandle="+arg0+")");
		}
	}
	
	public Object getActivity(javax.slee.resource.ActivityHandle handle) {
		Logger logger = getLogger();
		if (logger.isDebugEnabled()) {
			logger.debug("getActivity(handle="+handle+")");
		}		
		return activities.get((ActivityHandle)handle);
	}
	
	public javax.slee.resource.ActivityHandle getActivityHandle(Object activity) {
		Logger logger = getLogger();
		if (logger.isDebugEnabled()) {
			logger.debug("getActivity(activity="+activity+")");
		}
		ActivityObject activityObject = (ActivityObject) activity;
		ActivityHandle activityHandle = new ActivityHandle(activityObject.id);
		if (activities.containsKey(activityHandle)) {
			return activityHandle;
		}
		else {
			return null;
		}	    
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
		return this.sbbInterface;
	}	
	
	public void queryLiveness(javax.slee.resource.ActivityHandle ah) {
		Logger logger = getLogger();
		if (logger.isDebugEnabled()) {
			logger.debug("queryLiveness");
		}	
		// ignore, datasource activities can live for long
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
	
	public abstract DataSource getDataSource();
	
	public abstract Logger getLogger();
	
	// THIS RA LOGIC
	
	public void postDocumentUpdatedEvent(DocumentUpdatedEvent event) {
		postEvent(event, documentUpdatedEventId,event.getDocumentSelector());
	}
	public void postElementUpdatedEvent(ElementUpdatedEvent event) {
		postEvent(event, elementUpdatedEventId,event.getDocumentSelector());
	}
	public void postAttributeUpdatedEvent(AttributeUpdatedEvent event) {
		postEvent(event, attributeUpdatedEventId,event.getDocumentSelector());
	}
	
	private void postEvent(Object event, int eventId, DocumentSelector documentSelector) {
		if (getLogger().isDebugEnabled()) {
			getLogger().debug("postEvent(documentSelector="+documentSelector.toString()+")");
}
		// try to fire event on document selector and auid activities
		fireEvent(event, eventId, new ActivityHandle(documentSelector.toString()));
		fireEvent(event, eventId, new ActivityHandle(documentSelector.getAUID()));
	}
	
	private void fireEvent(Object event, int eventId, ActivityHandle handle) {
		if (getActivity(handle) != null) {
			// fire event
			try {
				this.sleeEndpoint.fireEvent(handle, event, eventId, null);
			} catch (Exception e) {
				getLogger().error("failed to post event for "+handle.toString(), e);
			}
		}
	}
	
	public void endActivity(ActivityHandle handle) {
		// check it has activity
		if(activities.containsKey(handle)) {
			// tell slee to end the activity context
			try {
				this.sleeEndpoint.activityEnding(handle);
			} catch (Exception e) {
				getLogger().error("unable to end activity: ",e);
			}					
		}
	}
	
	/**
	 * creates a new activity, if does not exists, otherwise signal another "virtual" creation was done
	 */
	public AppUsageActivity createAppUsageActivity(String auid) {
		ActivityHandle activityHandle = new ActivityHandle(auid);
		AppUsageActivity activity = (AppUsageActivity) activities.get(activityHandle);
		if (activity == null) {
			activity = new AppUsageActivity(auid,this);
			AppUsageActivity anotherActivity = (AppUsageActivity) activities.putIfAbsent(activityHandle, activity);
			if (anotherActivity != null) {
				activity = anotherActivity;
				activity.created();
			}
		}
		else {
			activity.created();
		}
		return activity;
	}
	
	/**
	 * creates a new activity, if does not exists, otherwise signal another "virtual" creation was done
	 */
	public DocumentActivity createDocumentActivity(
			DocumentSelector documentSelector) {
		ActivityHandle activityHandle = new ActivityHandle(documentSelector.toString());
		DocumentActivity activity = (DocumentActivity) activities.get(activityHandle);
		if (activity == null) {
			activity = new DocumentActivity(documentSelector,this);
			DocumentActivity anotherActivity = (DocumentActivity) activities.putIfAbsent(activityHandle, activity);
			if (anotherActivity != null) {
				activity = anotherActivity;
				activity.created();
			}
		}
		else {
			activity.created();
		}
		return activity;
	}
	
}
