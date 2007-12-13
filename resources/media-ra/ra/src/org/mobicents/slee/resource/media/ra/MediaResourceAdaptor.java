/***************************************************
 *                                                 *
 *  Mobicents: The Open Source VoIP Platform       *
 *                                                 *
 *  Distributable under GPL license.              *
 *  See terms of license at gnu.org.               *
 *                                                 *
 ***************************************************/
package org.mobicents.slee.resource.media.ra;

import java.util.HashMap;
import java.util.Properties;

import javax.naming.NamingException;
import javax.slee.Address;
import javax.slee.AddressPlan;
import javax.slee.InvalidStateException;
import javax.slee.UnrecognizedActivityException;
import javax.slee.UnrecognizedEventException;
import javax.slee.facilities.EventLookupFacility;
import javax.slee.facilities.FacilityException;
import javax.slee.resource.ActivityHandle;
import javax.slee.resource.ActivityIsEndingException;
import javax.slee.resource.BootstrapContext;
import javax.slee.resource.FailureReason;
import javax.slee.resource.Marshaler;
import javax.slee.resource.ResourceAdaptor;
import javax.slee.resource.ResourceAdaptorTypeID;
import javax.slee.resource.ResourceException;
import javax.slee.resource.SleeEndpoint;

import org.apache.log4j.Logger;
import org.mobicents.slee.container.SleeContainer;
import org.mobicents.slee.resource.ResourceAdaptorActivityContextInterfaceFactory;
import org.mobicents.slee.resource.ResourceAdaptorEntity;
import org.mobicents.slee.resource.media.events.MediaEvent;
import org.mobicents.slee.resource.media.ratype.ConnectionEvent;
import org.mobicents.slee.resource.media.ratype.MediaSession;
import org.mobicents.slee.resource.media.ratype.MediaConnection;
import org.mobicents.slee.resource.media.ratype.MediaContext;
import org.mobicents.slee.resource.media.ratype.MediaContextEvent;
import org.mobicents.slee.resource.media.ratype.MediaProvider;
import org.mobicents.slee.resource.media.ratype.MediaRaActivityContextInterfaceFactory;


/**
 *
 * The main Media Resource Adaptor class. Provides to SLEE
 * services basic media features such as establishing
 * a media stream with a remote client, playing a media
 * file to the remote client and recording incoming media from
 * the client.
 *
 * @author torosvi
 * @author Ivelin Ivanov
 *
 */
public class MediaResourceAdaptor implements ResourceAdaptor, EventListener {
    public Properties properties;
    private static final String LOCAL_HOST = "org.mobicents.LOCAL_HOST";
    
    static private transient Logger logger;
    static {
        logger = Logger.getLogger(MediaResourceAdaptor.class);
    }
    
    /**
     * The BootstrapContext provides the resource adaptor with the required capabilities in the
     * SLEE to execute its work. The bootstrap context is implemented by the SLEE. The BootstrapContext
     * object holds references to a number of objects that are of interest to many resource adaptors.
     * For further information see JSLEE v1.1 Specification, Early Draft Review Page 305.
     * The bootstrapContext will be set in entityCreated() method.
     */
    private transient BootstrapContext bootstrapContext = null;
    
    /**
     * The SLEE endpoint defines the contract between the SLEE and the resource
     * adaptor that enables the resource adaptor to deliver events asynchronously
     * to SLEE endpoints residing in the SLEE. This contract serves as a generic
     * contract that allows a wide range of resources to be plugged into a SLEE
     * environment via the resource adaptor architecture.
     * For further information see JSLEE v1.1 Specification,
     * Early Draft Review Page 307.
     * The sleeEndpoint will be initialized in entityCreated() method.
     */
    private transient SleeEndpoint sleeEndpoint = null;
    // The EventLookupFacility is used to look up the event id of incoming events
    private transient EventLookupFacility eventLookup = null;
    // The list of activites stored in this resource adaptor. If this resource
    // adaptor were a distributed and highly available solution, this storage
    // were one of the candidates for distribution.
    private transient HashMap activities = null;
    // The activity context interface factory defined in
    // MediaRaActivityContextInterfaceFactoryImpl
    private transient MediaRaActivityContextInterfaceFactory acif = null;
    // A link to the MediaProvider which then will be exposed to Sbbs
    private transient MediaProvider raProvider = null;
    
    /**
     * Implements javax.slee.resource.ResourceAdaptor
     * Please refer to JSLEE v1.1 Specification, Early Draft Review Page 298
     * for further information. <br>
     * This method is called by the SLEE when a resource adaptor object instance
     * is bootstrapped, either when a resource adaptor entity is created or during
     * SLEE startup. The SLEE implementation will construct the resource adaptor
     * object and then invoke the entityCreated method before any other operations
     * can be invoked on the resource adaptor object.
     */
    public void entityCreated(BootstrapContext bootstrapContext) throws ResourceException {
        logger.debug("MediaResourceAdaptor.entityCreated() called.");
        this.bootstrapContext = bootstrapContext;
        this.sleeEndpoint = bootstrapContext.getSleeEndpoint();
        this.eventLookup = bootstrapContext.getEventLookupFacility();
    }
    
    /**
     * Implements javax.slee.resource.ResourceAdaptor
     * Please refer to JSLEE v1.1 Specification, Early Draft Review Page 299
     * for further information. <br>
     * This method is called by the SLEE when a resource adaptor object instance
     * is being removed, either when a resource adaptor entity is deleted or during
     * SLEE shutdown. When receiving this invocation the resource adaptor object
     * is expected to close any system resources it has allocated.
     */
    public void entityRemoved() {
        logger.debug("MediaResourceAdaptor.entityRemoved() called.");
    }
    
    /**
     * Implements javax.slee.resource.ResourceAdaptor
     * The JSLEE v1.1 Specification does not include entityActivated().
     * However, the API description of JSLEE v1.1 does include this
     * method already. So, the documentation follows the code. <br>
     * This method is called in context of project Mobicents
     * in context of resource adaptor activation. More precisely,
     * org.mobicents.slee.resource.ResourceAdaptorEntity.activate()
     * calls this method entityActivated().
     * This method signals the resource adaptor the transition from
     * state "INACTIVE" to state "ACTIVE".
     */
    public void entityActivated() throws ResourceException {
        logger.debug("MediaResourceAdaptor.entityActivated() called.");
        
        try {
            try {
                this.configure();
                
            } catch (InvalidStateException e) {
                logger.error(e.getMessage());
            }
            
            try {
                raProvider = new MediaProviderImpl(this);
                
                initializeNamingContext();
                
            } catch (Exception ex) {
                logger.error("MediaResouceAdaptor.start(): Exception caught!", ex);
                
                throw new ResourceException(ex.getMessage());
            }
            
            activities = new HashMap();
            
        } catch (ResourceException e) {
            e.printStackTrace();
            throw new javax.slee.resource.ResourceException(
                    "MediaResourceAdaptor.entityActivated(): " +
                    "Failed to activate RAFrame Resource Adaptor!", e);
        }
    }
    
    /**
     * Implements javax.slee.resource.ResourceAdaptor
     * The JSLEE v1.1 Specification does not include entityDeactivating().
     * However, the API description of JSLEE v1.1 does include this method
     * already. So, the documentation follows the code.
     * <br>
     * This method is called in context of project Mobicents in
     * context of resource adaptor deactivation. More precisely,
     * org.mobicents.slee.resource.ResourceAdaptorEntity.deactivate()
     * calls this method entityDeactivating() PRIOR to invoking entityDeactivated().
     * This method signals the resource adaptor the transition from state "ACTIVE"
     * to state "STOPPING".
     */
    public void entityDeactivating() {
        logger.debug("MediaResourceAdaptor.entityDeactivating() called.");
    }
    
    /**
     * implements javax.slee.resource.ResourceAdaptor
     * The JSLEE v1.1 Specification does not include entityDeactivated().
     * However, the API description of JSLEE v1.1 does include this method already.
     * So, the documentation follows the code. <br>
     * This method is called in context of project Mobicents in
     * context of resource adaptor deactivation. More precisely,
     * org.mobicents.slee.resource.ResourceAdaptorEntity.deactivate()
     * calls this method entityDeactivated(). The method call is done
     * AFTER the call to entityDeactivating().
     * This method signals the resource adaptor the transition from state
     * "STOPPING" to state "INACTIVE".
     */
    public void entityDeactivated() {
        logger.debug("MediaResourceAdaptor.entityDeactivated() called.");
        try {
            cleanNamingContext();
        } catch (NamingException e) {
            logger.error("Cannot unbind naming context", e);
        }
        logger.debug("Media Resource Adaptor stopped.");
    }
    
    /**
     * Implements javax.slee.resource.ResourceAdaptor
     * Please refer to JSLEE v1.1 Specification, Early Draft Review Page 300
     * for further information. <br>
     * The SLEE calls this method to inform the resource adaptor object that
     * the specified event was processed successfully by the SLEE. An event
     * is considered to be processed successfully if the SLEE has attempted
     * to deliver the event to all interested SBBs.
     */
    public void eventProcessingSuccessful(ActivityHandle handle, Object event,
            int eventID, Address address, int flags) {
        logger.debug("MediaResourceAdaptor.eventProcessingSuccessful() called.");
    }
    
    /**
     * Implements javax.slee.resource.ResourceAdaptor
     * Please refer to JSLEE v1.1 Specification, Early Draft Review Page 300
     * for further information. <br>
     * The SLEE calls this method to inform the resource adaptor object that
     * the specified event was processed unsuccessfully by the SLEE. Event
     * processing can fail if, for example, the SLEE doesn’t have enough
     * resource to process the event, a SLEE node fails during event
     * processing or a system level failure prevents the SLEE from
     * committing transactions.
     */
    public void eventProcessingFailed(ActivityHandle handle, Object event,
            int eventID, Address address, int flags, FailureReason reason) {
        logger.debug("MediaResourceAdaptor.eventProcessingFailed() called.");
    }
    
    /**
     * Implements javax.slee.resource.ResourceAdaptor
     * Please refer to JSLEE v1.1 Specification, Early Draft Review Page 301
     * for further information. <br>
     * The SLEE calls this method to inform the resource adaptor that the SLEE
     * has completed activity end processing for the activity represented by
     * the activity handle. The resource adaptor should release any resource
     * related to this activity as the SLEE will not ask for it again.
     */
    public void activityEnded(ActivityHandle activityHandle) {
        // remove the handle from the list of activities
        activities.remove(activityHandle);
        logger.debug("MediaResourceAdaptor.activityEnded() called.");
    }
    
    /**
     * Implements javax.slee.resource.ResourceAdaptor
     * Please refer to JSLEE v1.1 Specification, Early Draft Review Page 301
     * for further information. <br>
     * The SLEE calls this method to inform the resource adaptor that the
     * activity’s Activity Context object is no longer attached to any SBB
     * entities and is no longer referenced by any SLEE Facilities. This
     * enables the resource adaptor to implicitly end the Activity object.
     */
    public void activityUnreferenced(ActivityHandle activityHandle) {
        logger.debug("MediaResourceAdaptor.activityUnreferenced() called.");
    }
    
    /**
     * Implements javax.slee.resource.ResourceAdaptor
     * Please refer to JSLEE v1.1 Specification, Early Draft Review Page 301 for further information.
     * <br>
     * The SLEE calls this method to query if a specific activity belonging to this resource adaptor object is alive.
     */
    public void queryLiveness(ActivityHandle activityHandle) {
        logger.debug("MediaResourceAdaptor.queryLifeness() called.");
    }
    
    /**
     * Implements javax.slee.resource.ResourceAdaptor
     * Please refer to JSLEE v1.1 Specification, Early Draft Review Page 301
     * for further information. <br>
     * The SLEE calls this method to get access to the underlying activity for
     * an activity handle. The resource adaptor is expected to pass back a
     * non-null object.
     */
    public Object getActivity(ActivityHandle activityHandle) {
        logger.debug("MediaResourceAdaptor.getActivity() called.");
        return activities.get(activityHandle);
    }
    
    /**
     * Implements javax.slee.resource.ResourceAdaptor
     * Please refer to JSLEE v1.1 Specification, Early Draft Review Page 301
     * for further information. <br>
     * The SLEE calls this method to get an activity handle for an activity
     * created by the underlying resource. This method is invoked by the SLEE
     * when it needs to construct an activity context for an activity via an
     * activity context interface factory method invoked by an SBB.
     */
    public ActivityHandle getActivityHandle(Object handle) {
        logger.debug("MediaResourceAdaptor.getActivityHandle(obj) called.");
        return null;
    }
    
    /**
     * Implements javax.slee.resource.ResourceAdaptor
     * Please refer to JSLEE v1.1 Specification, Early Draft Review Page 302
     * for further information. <br>
     * The SLEE calls this method to get access to the underlying resource
     * adaptor interface that enables the SBB to invoke the resource adaptor,
     * to send messages for example.
     */
    public Object getSBBResourceAdaptorInterface(String className) {
        logger.debug("MediaResourceAdaptor.getSBBResourceAdapterInterface(" +
                className + ") called.");
        return raProvider;
    }
    
    /**
     * Implements javax.slee.resource.ResourceAdaptor
     * Please refer to JSLEE v1.1 Specification, Early Draft Review Page 302
     * for further information. <br>
     * The SLEE calls this method to get reference to the Marshaler object.
     * The resource adaptor implements the Marshaler interface. The Marshaler
     * is used by the SLEE to convert between object and distributable forms
     * of events and event handles.
     */
    public Marshaler getMarshaler() {
        logger.debug("MediaResourceAdaptor.getMarshaler() called.");
        return null;
    }
    
    /**
     * Implements javax.slee.resource.ResourceAdaptor
     * Please refer to JSLEE v1.1 Specification, Early Draft Review Page 302
     * for further information. <br>
     * The SLEE calls this method to signify to the resource adaptor that a
     * service has been installed and is interested in a specific set of events.
     * The SLEE passes an event filter which identifies a set of event types that
     * services in the SLEE are interested in. The SLEE calls this method once a
     * service is installed.
     */
    public void serviceInstalled(String serviceKey, int[] eventIDs, String[] resourceOptions) {
        logger.debug("MediaResourceAdaptor.serviceInstalled() called.");
    }
    
    /**
     * Implements javax.slee.resource.ResourceAdaptor
     * Please refer to JSLEE v1.1 Specification, Early Draft Review Page 303
     * for further information. <br>
     * The SLEE calls this method to signify that a service has been un-installed
     * in the SLEE. The event types associated to the service key are no longer
     * of interest to a particular application.
     */
    public void serviceUninstalled(String serviceKey) {
        logger.debug("MediaResourceAdaptor.serviceUninstalled() called.");
    }
    
    /**
     * Implements javax.slee.resource.ResourceAdaptor
     * Please refer to JSLEE v1.1 Specification, Early Draft Review Page 303
     * for further information. <br>
     * The SLEE calls this method to inform the resource adaptor that a service
     * has been activated and is interested in the event types associated to the
     * service key. The service must be installed with the resource adaptor via
     * the serviceInstalled method before it can be activated.
     */
    public void serviceActivated(String arg0) {
        logger.debug("MediaResourceAdaptor.serviceActivated() called.");
    }
    
    /**
     * Implements javax.slee.resource.ResourceAdaptor
     * Please refer to JSLEE v1.1 Specification, Early Draft Review Page 304
     * for further information. <br>
     * The SLEE calls this method to inform the SLEE that a service has been
     * deactivated and is no longer interested in the event types associated
     * to the service key.
     */
    public void serviceDeactivated(String arg0) {
        logger.debug("MediaResourceAdaptor.serviceDeactivated() called.");
    }
    
    // Set up the JNDI naming context
    private void initializeNamingContext() throws NamingException {
        // Get the reference to the SLEE container from JNDI
        SleeContainer container = SleeContainer.lookupFromJndi();
        // Get the entities name
        String entityName = bootstrapContext.getEntityName();
        
        ResourceAdaptorEntity resourceAdaptorEntity = ((ResourceAdaptorEntity) container.getResourceAdaptorEnitity(entityName));
        ResourceAdaptorTypeID raTypeId = resourceAdaptorEntity.getInstalledResourceAdaptor().getRaType().getResourceAdaptorTypeID();
        // Create the ActivityContextInterfaceFactory
        acif = new MediaRaActivityContextInterfaceFactoryImpl(resourceAdaptorEntity.getServiceContainer(), entityName);
        // Set the ActivityContextInterfaceFactory
        resourceAdaptorEntity.getServiceContainer().getActivityContextInterfaceFactories().put(raTypeId, acif);
        
        try {
            if (this.acif != null) {
                // Parse the string = java:slee/resources/MediaRA/mediaraacif
                String jndiName = ((ResourceAdaptorActivityContextInterfaceFactory) acif).getJndiName();
                int begind = jndiName.indexOf(':');
                int toind = jndiName.lastIndexOf('/');
                String prefix = jndiName.substring(begind + 1, toind);
                String name = jndiName.substring(toind + 1);
                logger.debug("jndiName prefix =" + prefix + "; jndiName = " + name);
                SleeContainer.registerWithJndi(prefix, name, this.acif);
            }
        } catch (IndexOutOfBoundsException e) {
            // Not register with JNDI
            logger.debug(e);
        }
    }
    
    // Clean the JNDI naming context
    private void cleanNamingContext() throws NamingException {
        try {
            if (this.acif != null) {
                // Parse the string = java:slee/resources/MediaRA/mediaraacif
                String jndiName = ((ResourceAdaptorActivityContextInterfaceFactory) this.acif).getJndiName();
                //Remove "java:" prefix
                int begind = jndiName.indexOf(':');
                String javaJNDIName = jndiName.substring(begind + 1);
                logger.debug("JNDI name to unregister: " + javaJNDIName);
                SleeContainer.unregisterWithJndi(javaJNDIName);
                logger.debug("JNDI name unregistered.");
            }
        } catch (IndexOutOfBoundsException e) {
            logger.debug(e);
        }
    }
    
    public synchronized void fireConnectionConnectingEvent(ConnectionEvent evt) {
        MediaConnection connection = evt.getConnection();
        ConnectionHandle handle = new ConnectionHandle(connection.getId());
        
        activities.put(handle, connection);
        fireEvent("org.mobicents.slee.media.CONNECTION_CONNECTING", handle, evt);
    }
    
    public synchronized void fireConnectionConnectedEvent(ConnectionEvent evt) {
        MediaConnection connection = evt.getConnection();
        ConnectionHandle handle = new ConnectionHandle(connection.getId());
        fireEvent("org.mobicents.slee.media.CONNECTION_CONNECTED", handle, evt);
    }
    
    public synchronized void fireConnectionDisconnectedEvent(ConnectionEvent evt) {
        MediaConnection connection = evt.getConnection();
        ConnectionHandle handle = new ConnectionHandle(connection.getId());
        fireEvent("org.mobicents.slee.media.CONNECTION_DISCONNECTED", handle, evt);
        
        try {
            sleeEndpoint.activityEnding(handle);
            if (logger.isDebugEnabled()) {
                logger.debug("Ended media connection activity: " + connection.getId());
            }
        } catch (Exception e) {
            logger.error("Unexpected error during activity ending:", e);
        }
    }
    
    public synchronized void fireConnectionDtmfEvent(ConnectionEvent evt) {
        MediaConnection connection = evt.getConnection();
        ConnectionHandle handle = new ConnectionHandle(connection.getId());
        fireEvent("org.mobicents.slee.media.CONNECTION_DTMF", handle, evt);
    }
    
    public synchronized void fireConnectionFailedEvent(ConnectionEvent evt) {
        MediaConnection connection = evt.getConnection();
        ConnectionHandle handle = new ConnectionHandle(connection.getId());
        fireEvent("org.mobicents.slee.media.CONNECTION_FAILED", handle, evt);
    }
    
    // player's events
    public synchronized void firePlayerStartedEvent(MediaContextEvent evt) {
        MediaContext context = evt.getMediaContext();
        ContextHandle handle = new ContextHandle(context.getId());
        fireEvent("org.mobicents.slee.media.PLAYER_STARTED", handle, evt);
    }

    public synchronized void firePlayerStoppedEvent(MediaContextEvent evt) {
        MediaContext context = evt.getMediaContext();
        ContextHandle handle = new ContextHandle(context.getId());
        fireEvent("org.mobicents.slee.media.PLAYER_STOPPED", handle, evt);
    }
    
    public synchronized void firePlayerFailedEvent(MediaContextEvent evt) {
        MediaContext context = evt.getMediaContext();
        ContextHandle handle = new ContextHandle(context.getId());
        fireEvent("org.mobicents.slee.media.PLAYER_FAILED", handle, evt);
    }
    
    // recorder's events
    public synchronized void fireRecorderStartedEvent(MediaContextEvent evt) {
        MediaContext context = evt.getMediaContext();
        ContextHandle handle = new ContextHandle(context.getId());
        fireEvent("org.mobicents.slee.media.RECORDER_STARTED", handle, evt);
    }

    public synchronized void fireRecorderStoppedEvent(MediaContextEvent evt) {
        MediaContext context = evt.getMediaContext();
        ContextHandle handle = new ContextHandle(context.getId());
        fireEvent("org.mobicents.slee.media.RECORDER_STOPPED", handle, evt);
    }
    
    public synchronized void fireRecorderFailedEvent(MediaContextEvent evt) {
        MediaContext context = evt.getMediaContext();
        ContextHandle handle = new ContextHandle(context.getId());
        fireEvent("org.mobicents.slee.media.RECORDER_FAILED", handle, evt);
    }

    // context actions event
    public synchronized void fireConnectionAttachedEvent(MediaContextEvent evt) {
        MediaContext context = evt.getMediaContext();
        ContextHandle handle = new ContextHandle(context.getId());
        fireEvent("org.mobicents.slee.media.CONNECTION_ATTACHED", handle, evt);
    }

    public synchronized void fireConnectionDetachedEvent(MediaContextEvent evt) {
        MediaContext context = evt.getMediaContext();
        ContextHandle handle = new ContextHandle(context.getId());
        fireEvent("org.mobicents.slee.media.CONNECTION_DETACHED", handle, evt);
    }
    
    public synchronized void fireActionErrorEvent(MediaContextEvent evt) {
        MediaContext context = evt.getMediaContext();
        ContextHandle handle = new ContextHandle(context.getId());
        fireEvent("org.mobicents.slee.media.ACTION_ERROR", handle, evt);
    }
    
    public synchronized void endingMediaContextActivity(MediaContext mediaContext) {
        ContextHandle handle = new ContextHandle(mediaContext.getId());
        
        activities.remove(handle);
        try {
            sleeEndpoint.activityEnding(handle);
            if (logger.isDebugEnabled()) {
                logger.debug("Ended media context activity: " + mediaContext.getId());
            }
        } catch (Exception e) {
            logger.error("Unexpected error during activity ending:", e);
        }
    }
    
    private synchronized void fireEvent(String eventName, ActivityHandle activityHandle, Object event) {
        int eventID = -1;
        try {
            eventID = eventLookup.getEventID(eventName, "org.mobicents.media","1.0");
        } catch (FacilityException fe) {
            logger.error("Caught a FacilityException: ");
            fe.printStackTrace();
            throw new RuntimeException("MediaResourceAdaptor.firEvent(): FacilityException caught. ", fe);
        } catch (UnrecognizedEventException ue) {
            logger.error("Caught an UnrecognizedEventException: ");
            ue.printStackTrace();
            throw new RuntimeException("MediaResourceAdaptor.firEvent(): UnrecognizedEventException caught.", ue);
        }
        
        if (eventID == -1) {
            logger.warn("Unknown event type: " + eventName);
            return;
        }
        
        Address address = new Address(AddressPlan.IP, "localhost");
        
        try {
            sleeEndpoint.fireEvent(activityHandle, event, eventID, address);
            logger.info("Fire event: " + eventName);
        } catch (IllegalStateException ise) {
            logger.error("Caught an IllegalStateException: ");
            ise.printStackTrace();
        } catch (ActivityIsEndingException aiee) {
            logger.error("Caught an ActivityIsEndingException: ");
            aiee.printStackTrace();
        } catch (UnrecognizedActivityException uaee) {
            logger.error("Caught an UnrecognizedActivityException: ");
            uaee.printStackTrace();
        }
    }
    
    public void onEvent(Object event, MediaSession activity) {
        String eventName = ((MediaEvent)event).getName();
        String eventVendor = ((MediaEvent)event).getVendor();
        String eventVersion = ((MediaEvent)event).getVersion();
        
        // Generate the activity handle which uniquely identifies the
        // appropriate activity context
        MediaActivityHandle handle = new MediaActivityHandle(activity.getSessionId());
        // Lookup the activity
        MediaSession ms = (MediaSession) activities.get(handle);
        
        if (ms == null) {
            activities.put(handle, activity);
        }
        
        int eventID;
        System.out.println("**** LOCALHOST = " + activity.getLocalHost());
        Address address = new Address(AddressPlan.IP, activity.getLocalHost().getHostAddress());
        
        try {
            eventID = eventLookup.getEventID(eventName, eventVendor, eventVersion);
            
        } catch (FacilityException e2) {
            e2.printStackTrace();
            throw new RuntimeException("Failed to lookup event!", e2);
        } catch (UnrecognizedEventException e2) {
            e2.printStackTrace();
            throw new RuntimeException("Failed to lookup event!", e2);
            
        }
        
        if (eventID == -1) {
            // Silently drop the message because this is not a registered
            // event type.
            logger.debug("event lookup -- could not find event mapping " +
                    "-- check xml/slee-events.xml");
            return;
        }
        
        try {
            logger.info("Resource adaptor delivering event:/n " + eventID);
            sleeEndpoint.fireEvent(handle, event, eventID, address);
            
        } catch (IllegalStateException e) {
            logger.error(e.getMessage(), e);
        } catch (ActivityIsEndingException e) {
            logger.error(e.getMessage(), e);
        } catch (UnrecognizedActivityException e) {
            logger.error(e.getMessage(), e);
        }
    }
    
    private void configure() throws InvalidStateException {
        properties = new Properties();
        
        // Load default values
        try {
            properties.load(getClass().getResourceAsStream("mediara.properties"));
            if (logger.isDebugEnabled()) {
                logger.debug("Loading MEDIA RA properties: " + properties);
            }
            
            String bindAddress = properties.getProperty(LOCAL_HOST);
            
            if (bindAddress == null) {
                bindAddress = System.getProperty("jboss.bind.address");
                if (bindAddress != null)
                    properties.setProperty(LOCAL_HOST, bindAddress);
            }
            
        } catch (java.io.IOException e) {
            throw new InvalidStateException("Error loading MEDIA RA properties: " + e.getMessage());
        }
    }
    
    SleeEndpoint getSleeEndpoint() {
        return sleeEndpoint;
    }
}
