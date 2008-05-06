package net.java.slee.resource.diameter.base;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

import javax.naming.NamingException;
import javax.slee.Address;
import javax.slee.UnrecognizedActivityException;
import javax.slee.facilities.EventLookupFacility;
import javax.slee.management.UnrecognizedResourceAdaptorEntityException;
import javax.slee.resource.ActivityHandle;
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

public class DiameterBaseResourceAdaptor implements ResourceAdaptor, Serializable
{
  private static final long serialVersionUID = 1L;

  private static transient Logger logger = Logger.getLogger(DiameterBaseResourceAdaptor.class);

  /**
  * The BootstrapContext provides the resource adaptor with the required
  * capabilities in the SLEE to execute its work. The bootstrap context is
  * implemented by the SLEE. The BootstrapContext object holds references to
  * a number of objects that are of interest to many resource adaptors. For
  * further information see JSLEE v1.1 Specification Page 305. The
  * bootstrapContext will be set in entityCreated() method.
  */
  private transient BootstrapContext bootstrapContext = null;
  
  /**
   * The SLEE endpoint defines the contract between the SLEE and the resource
   * adaptor that enables the resource adaptor to deliver events
   * asynchronously to SLEE endpoints residing in the SLEE. This contract
   * serves as a generic contract that allows a wide range of resources to be
   * plugged into a SLEE environment via the resource adaptor architecture.
   * For further information see JSLEE v1.1 Specification Page 307 The
   * sleeEndpoint will be initialized in entityCreated() method.
   */
  private transient SleeEndpoint sleeEndpoint = null;

  /**
   * the EventLookupFacility is used to look up the event id of incoming
   * events
   */
  private transient EventLookupFacility eventLookup = null;

  /**
   * The list of activites stored in this resource adaptor. If this resource
   * adaptor were a distributed and highly available solution, this storage
   * were one of the candidates for distribution.
   */
  private transient ConcurrentHashMap<ActivityHandle, DiameterActivity> activities = null;

  /**
   * the activity context interface factory defined in
   * DiameterRAActivityContextInterfaceFactoryImpl
   */
  private transient DiameterActivityContextInterfaceFactory acif = null;

  // a link to the DiameterProvider which then will be exposed to Sbbs
  private transient DiameterProvider raProvider = null;

  public DiameterBaseResourceAdaptor()
  {
    logger.info( "Diameter Base RA :: DiameterBaseResourceAdaptor." );
  }
  
  /**
   * implements javax.slee.resource.ResourceAdaptor Please refer to JSLEE v1.1
   * Specification Page 301 for further information. <br>
   * The SLEE calls this method to inform the resource adaptor that the SLEE
   * has completed activity end processing for the activity represented by the
   * activity handle. The resource adaptor should release any resource related
   * to this activity as the SLEE will not ask for it again.
   */
  public void activityEnded( ActivityHandle handle )
  {
    logger.info( "Diameter Base RA :: activityEnded :: handle[" + handle + "." );
    
    this.activities.remove( handle );
  }

  /**
   * implements javax.slee.resource.ResourceAdaptor Please refer to JSLEE v1.1
   * Specification Page 301 for further information. <br>
   * The SLEE calls this method to inform the resource adaptor that the
   * activity’s Activity Context object is no longer attached to any SBB
   * entities and is no longer referenced by any SLEE Facilities. This enables
   * the resource adaptor to implicitly end the Activity object.
   */
  public void activityUnreferenced( ActivityHandle handle )
  {
    logger.info( "Diameter Base RA :: activityUnreferenced :: handle[" + handle + "]." );
    
    this.activityEnded( handle );
  }

  /**
   * implements javax.slee.resource.ResourceAdaptor The JSLEE v1.1
   * Specification does not include entityActivated(). However, the API
   * description of JSLEE v1.1 does include this method already. So, the
   * documentation follows the code. <br>
   * This method is called in context of project Mobicents in context of
   * resource adaptor activation. More precisely,
   * org.mobicents.slee.resource.ResourceAdaptorEntity.activate() calls this
   * method entityActivated(). This method signals the resource adaptor the
   * transition from state "INACTIVE" to state "ACTIVE".
   */
  public void entityActivated() throws ResourceException
  {
    logger.info( "Diameter Base RA :: entityActivated." );
    
    try
    {
      logger.info( "Activating Diameter Base RA Entity" );
      
      this.raProvider = new DiameterProviderImpl(this);
      
      initializeNamingContext();
      
      activities = new ConcurrentHashMap();
    }
    catch (Exception e) {
      logger.error( "Error Activating Diameter Base RA Entity", e );
    }
  }

  /**
   * implements javax.slee.resource.ResourceAdaptor Please refer to JSLEE v1.1
   * Specification Page 298 for further information. <br>
   * This method is called by the SLEE when a resource adaptor object instance
   * is bootstrapped, either when a resource adaptor entity is created or
   * during SLEE startup. The SLEE implementation will construct the resource
   * adaptor object and then invoke the entityCreated method before any other
   * operations can be invoked on the resource adaptor object.
   */
  public void entityCreated( BootstrapContext bootstrapContext ) throws ResourceException
  {
    logger.info( "Diameter Base RA :: entityCreated :: bootstrapContext[" + bootstrapContext + "]." );
    
    this.bootstrapContext = bootstrapContext;
    this.sleeEndpoint = bootstrapContext.getSleeEndpoint();
    this.eventLookup = bootstrapContext.getEventLookupFacility();    
  }

  /**
   * implements javax.slee.resource.ResourceAdaptor The JSLEE v1.1
   * Specification does not include entityDeactivated(). However, the API
   * description of JSLEE v1.1 does include this method already. So, the
   * documentation follows the code. <br>
   * This method is called in context of project Mobicents in context of
   * resource adaptor deactivation. More precisely,
   * org.mobicents.slee.resource.ResourceAdaptorEntity.deactivate() calls this
   * method entityDeactivated(). The method call is done AFTER the call to
   * entityDeactivating(). This method signals the resource adaptor the
   * transition from state "STOPPING" to state "INACTIVE".
   */
  public void entityDeactivated()
  {
    logger.info( "Diameter Base RA :: entityDeactivated." );
    
    logger.info( "Diameter Base RA :: Cleaning RA Activities." );
    for(ActivityHandle handle: activities.keySet())
    {
      try 
      {
        sleeEndpoint.activityEnding(handle);
      }
      catch (UnrecognizedActivityException uae)
      {
        logger.error("Error ending activity", uae);
      }
    }
    activities.clear();
    activities = null;
    
    logger.info("Diameter Base RA :: Cleaning naming context.");
    
    try
    {
      cleanNamingContext();
    }
    catch (NamingException e)
    {
      logger.error("Diameter Base RA :: Cannot unbind naming context.");
    }
    
    logger.info("Diameter Base RA :: RA Stopped.");    
  }

  /**
   * This method is called in context of project Mobicents in context of
   * resource adaptor deactivation. More precisely,
   * org.mobicents.slee.resource.ResourceAdaptorEntity.deactivate() calls this
   * method entityDeactivating() PRIOR to invoking entityDeactivated(). This
   * method signals the resource adaptor the transition from state "ACTIVE" to
   * state "STOPPING".
   */
  public void entityDeactivating()
  {
    logger.info( "Diameter Base RA :: entityDeactivating." );
  }

  /**
   * implements javax.slee.resource.ResourceAdaptor Please refer to JSLEE v1.1
   * Specification Page 299 for further information. <br>
   * This method is called by the SLEE when a resource adaptor object instance
   * is being removed, either when a resource adaptor entity is deleted or
   * during SLEE shutdown. When receiving this invocation the resource adaptor
   * object is expected to close any system resources it has allocated.
   */
  public void entityRemoved()
  {
    logger.info( "Diameter Base RA :: entityRemoved." );
  }

  /**
   * implements javax.slee.resource.ResourceAdaptor Please refer to JSLEE v1.1
   * Specification Page 300 for further information. <br>
   * The SLEE calls this method to inform the resource adaptor object that the
   * specified event was processed unsuccessfully by the SLEE. Event
   * processing can fail if, for example, the SLEE doesn’t have enough
   * resource to process the event, a SLEE node fails during event processing
   * or a system level failure prevents the SLEE from committing transactions.
   */
  public void eventProcessingFailed( ActivityHandle handle, Object event, int eventID, Address address, int flags, FailureReason reason )
  {
    logger.info( "Diameter Base RA :: eventProcessingFailed :: + handle[" + handle + "], event[" + event + "], eventID[" + eventID + "], address[" + address + "], flags[" + flags + "], reason[" + reason + "]." );
  }

  /**
   * implements javax.slee.resource.ResourceAdaptor Please refer to JSLEE v1.1
   * Specification Page 300 for further information. <br>
   * The SLEE calls this method to inform the resource adaptor object that the
   * specified event was processed successfully by the SLEE. An event is
   * considered to be processed successfully if the SLEE has attempted to
   * deliver the event to all interested SBBs.
   */
  public void eventProcessingSuccessful( ActivityHandle handle, Object event, int eventID, Address address, int flags )
  {
    logger.info( "Diameter Base RA :: eventProcessingSuccessful :: + handle[" + handle + "], event[" + event + "], eventID[" + eventID + "], address[" + address + "], flags[" + flags + "]." );
  }

  /**
   * implements javax.slee.resource.ResourceAdaptor Please refer to JSLEE v1.1
   * Specification Page 301 for further information. <br>
   * The SLEE calls this method to get access to the underlying activity for an
   * activity handle. The resource adaptor is expected to pass back a non-null
   * object.
   */
  public Object getActivity( ActivityHandle handle )
  {
    logger.info( "Diameter Base RA :: getActivity :: handle[" + handle + "]." );

    return this.activities.get( handle );
  }

  /**
   * implements javax.slee.resource.ResourceAdaptor Please refer to JSLEE v1.1
   * Specification Page 301 for further information. <br>
   * The SLEE calls this method to get an activity handle for an activity
   * created by the underlying resource. This method is invoked by the SLEE
   * when it needs to construct an activity context for an activity via an
   * activity context interface factory method invoked by an SBB.
   */
  public ActivityHandle getActivityHandle( Object activity )
  {
    logger.info( "Diameter Base RA :: getActivityHandle :: activity[" + activity + "]." );

    return null;
  }

  /**
   * implements javax.slee.resource.ResourceAdaptor Please refer to JSLEE v1.1
   * Specification Page 302 for further information. <br>
   * The SLEE calls this method to get reference to the Marshaler object. The
   * resource adaptor implements the Marshaler interface. The Marshaler is
   * used by the SLEE to convert between object and distributable forms of
   * events and event handles.
   */
  public Marshaler getMarshaler()
  {
    logger.info( "Diameter Base RA :: getMarshaler" );

    return null;
  }

  /**
   * implements javax.slee.resource.ResourceAdaptor Please refer to JSLEE v1.1
   * Specification Page 302 for further information. <br>
   * The SLEE calls this method to get access to the underlying resource
   * adaptor interface that enables the SBB to invoke the resource adaptor, to
   * send messages for example.
   */
  public Object getSBBResourceAdaptorInterface( String className )
  {
    logger.info( "Diameter Base RA :: getSBBResourceAdaptorInterface :: className[" + className + "]." );
    
    return this.raProvider;
  }

  /**
   * implements javax.slee.resource.ResourceAdaptor Please refer to JSLEE v1.1
   * Specification Page 301 for further information. <br>
   * The SLEE calls this method to query if a specific activity belonging to
   * this resource adaptor object is alive.
   */
  public void queryLiveness( ActivityHandle handle )
  {
    logger.info( "Diameter Base RA :: queryLiveness :: handle[" + handle +"]." );
  }

  /**
   * implements javax.slee.resource.ResourceAdaptor Please refer to JSLEE v1.1
   * Specification Page 303 for further information. <br>
   * The SLEE calls this method to inform the resource adaptor that a service
   * has been activated and is interested in the event types associated to the
   * service key. The service must be installed with the resource adaptor via
   * the serviceInstalled method before it can be activated.
   */
  public void serviceActivated( String serviceKey )
  {
    logger.info( "Diameter Base RA :: serviceActivated :: serviceKey[" + serviceKey + "]." );
  }

  /**
   * implements javax.slee.resource.ResourceAdaptor Please refer to JSLEE v1.1
   * Specification Page 304 for further information. <br>
   * The SLEE calls this method to inform the SLEE that a service has been
   * deactivated and is no longer interested in the event types associated to
   * the service key.
   */
  public void serviceDeactivated( String serviceKey )
  {
    logger.info( "Diameter Base RA :: serviceDeactivated :: serviceKey[" + serviceKey + "]." );
  }

  /**
   * implements javax.slee.resource.ResourceAdaptor Please refer to JSLEE v1.1
   * Specification Page 302 for further information. <br>
   * The SLEE calls this method to signify to the resource adaptor that a
   * service has been installed and is interested in a specific set of events.
   * The SLEE passes an event filter which identifies a set of event types
   * that services in the SLEE are interested in. The SLEE calls this method
   * once a service is installed.
   */
  public void serviceInstalled( String serviceKey, int[] eventIDs, String[] resourceOptions )
  {
    logger.info( "Diameter Base RA :: serviceInstalled :: serviceKey[" + serviceKey + "], eventIDs[" + eventIDs + "], resourceOptions[" + resourceOptions + "]." );
  }

  /**
   * implements javax.slee.resource.ResourceAdaptor Please refer to JSLEE v1.1
   * Specification Page 303 for further information. <br>
   * The SLEE calls this method to signify that a service has been
   * un-installed in the SLEE. The event types associated to the service key
   * are no longer of interest to a particular application.
   */
  public void serviceUninstalled( String serviceKey )
  {
    logger.info( "Diameter Base RA :: serviceUninstalled :: serviceKey[" + serviceKey + "]." );
  }

  // set up the JNDI naming context
  private void initializeNamingContext() throws NamingException
  {
    // get the reference to the SLEE container from JNDI
    SleeContainer container = SleeContainer.lookupFromJndi();
    
    // get the entities name
    String entityName = bootstrapContext.getEntityName();

    ResourceAdaptorEntity resourceAdaptorEntity;
    
    try
    {
      resourceAdaptorEntity = ((ResourceAdaptorEntity) container
          .getResourceAdaptorEntity(entityName));
    }
    catch ( UnrecognizedResourceAdaptorEntityException uraee )
    {
      throw new NamingException("Failure setting up Naming Context. RA Entity not found.");
    }
    
    ResourceAdaptorTypeID raTypeId = resourceAdaptorEntity.getInstalledResourceAdaptor()
        .getRaType().getResourceAdaptorTypeID();
    
    // create the ActivityContextInterfaceFactory
    acif = new DiameterActivityContextInterfaceFactoryImpl(resourceAdaptorEntity
        .getServiceContainer(), entityName);
    
    // set the ActivityContextInterfaceFactory
    resourceAdaptorEntity.getServiceContainer().getActivityContextInterfaceFactories()
        .put(raTypeId, acif);

    try
    {
      if (this.acif != null)
      {
        // parse the string = java:slee/resources/RAFrameRA/raframeacif
        String jndiName = ((ResourceAdaptorActivityContextInterfaceFactory) acif).getJndiName();
        int begind = jndiName.indexOf(':');
        int toind = jndiName.lastIndexOf('/');
        String prefix = jndiName.substring(begind + 1, toind);
        String name = jndiName.substring(toind + 1);
        
        logger.info("Diameter Base RA :: Registering in JNDI :: Prefix[" + prefix + "], Name[" + name + "].");
        
        SleeContainer.registerWithJndi(prefix, name, this.acif);
        logger.debug("Diameter Base RA :: Registered in JNDI successfully.");
      }
    }
    catch (IndexOutOfBoundsException e)
    {
      // not register with JNDI
      logger.debug(e);
    }
  }

  // clean the JNDI naming context
  private void cleanNamingContext() throws NamingException
  {
    try
    {
      if (this.acif != null)
      {
        // parse the string = java:slee/resources/RAFrameRA/raframeacif
        String jndiName = ((ResourceAdaptorActivityContextInterfaceFactory) this.acif).getJndiName();
        
        // remove "java:" prefix
        int begind = jndiName.indexOf(':');
        String javaJNDIName = jndiName.substring(begind + 1);
        
        logger.info("Diameter Base RA :: Unregistering from JNDI :: Name[" + javaJNDIName + "].");
        
        SleeContainer.unregisterWithJndi(javaJNDIName);
        logger.debug("Diameter Base RA :: Unregistered from JNDI successfully.");
      }
    } catch (IndexOutOfBoundsException e) {
      logger.debug(e);
    }
  }
  
}
