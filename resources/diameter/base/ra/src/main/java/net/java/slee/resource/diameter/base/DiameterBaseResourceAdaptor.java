package net.java.slee.resource.diameter.base;

import java.io.InputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import javax.naming.NamingException;
import javax.slee.Address;
import javax.slee.AddressPlan;
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

import net.java.slee.resource.diameter.base.events.DiameterMessage;
import net.java.slee.resource.http.events.HttpServletRequestEvent;

import org.apache.log4j.Logger;
import org.jdiameter.api.Answer;
import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.Configuration;
import org.jdiameter.api.IllegalStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.NetWork;
import org.jdiameter.api.NetworkReqListener;
import org.jdiameter.api.Request;
import org.jdiameter.api.Session;
import org.jdiameter.api.Stack;
import org.jdiameter.server.impl.StackImpl;
import org.jdiameter.server.impl.helpers.XMLConfiguration;
import org.mobicents.slee.container.SleeContainer;
import org.mobicents.slee.resource.ResourceAdaptorActivityContextInterfaceFactory;
import org.mobicents.slee.resource.ResourceAdaptorEntity;
import org.mobicents.slee.resource.ResourceAdaptorState;
import org.mobicents.slee.resource.diameter.base.events.AbortSessionAnswerImpl;
import org.mobicents.slee.resource.diameter.base.events.AbortSessionRequestImpl;
import org.mobicents.slee.resource.diameter.base.events.AccountingAnswerImpl;
import org.mobicents.slee.resource.diameter.base.events.AccountingRequestImpl;
import org.mobicents.slee.resource.diameter.base.events.CapabilitiesExchangeAnswerImpl;
import org.mobicents.slee.resource.diameter.base.events.CapabilitiesExchangeRequestImpl;
import org.mobicents.slee.resource.diameter.base.events.DeviceWatchdogAnswerImpl;
import org.mobicents.slee.resource.diameter.base.events.DeviceWatchdogRequestImpl;
import org.mobicents.slee.resource.diameter.base.events.DiameterMessageImpl;
import org.mobicents.slee.resource.diameter.base.events.DisconnectPeerAnswerImpl;
import org.mobicents.slee.resource.diameter.base.events.DisconnectPeerRequestImpl;
import org.mobicents.slee.resource.diameter.base.events.ReAuthAnswerImpl;
import org.mobicents.slee.resource.diameter.base.events.ReAuthRequestImpl;
import org.mobicents.slee.resource.diameter.base.events.SessionTerminationAnswerImpl;
import org.mobicents.slee.resource.diameter.base.events.SessionTerminationRequestImpl;
import org.mobicents.slee.resource.http.events.HttpServletRequestEventImpl;

/**
 * Diameter Resource Adaptor
 * 
 * <br>Super project:  mobicents
 * <br>1:20:00 AM May 9, 2008
 * <br>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a> 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a> 
 * @author Eric Svenson
 */
public class DiameterBaseResourceAdaptor implements ResourceAdaptor, NetworkReqListener, Serializable
{
  private static final long serialVersionUID = 1L;

  private static transient Logger logger = Logger.getLogger(DiameterBaseResourceAdaptor.class);

  private ResourceAdaptorState state;
  
  private Stack stack;
  
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
   * The activity context interface factory defined in
   * DiameterRAActivityContextInterfaceFactoryImpl
   */
  private transient DiameterActivityContextInterfaceFactory acif = null;

  /**
   * A link to the DiameterProvider which then will be exposed to Sbbs
   */
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
      
      this.activities = new ConcurrentHashMap();
      
      this.state = ResourceAdaptorState.CONFIGURED;
    }
    catch (Exception e) {
      logger.error( "Error Configuring Diameter Base RA Entity", e );
    }
    
    try {
      
      // Initialize the protocol stack
      initStack();
      
      // Resource Adaptor ready to rumble!
      this.state = ResourceAdaptorState.ACTIVE;
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
    
    this.state = ResourceAdaptorState.UNCONFIGURED;
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
    
    // Stop the stack
    try
    {
      stack.stop(5, TimeUnit.SECONDS);
    }
    catch ( Exception e )
    {
      logger.error( "Diameter Base RA :: Failure while stopping " );
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
    
    try
    {
      NetWork network = stack.unwrap(NetWork.class);
      
      for (ApplicationId appId : stack.getMetaData().getLocalPeerInfo().getCommonApplications())
      {
        network.remNetworkReqListener(appId);
      }
    }
    catch (InternalException e)
    {
      logger.error(e);
    }
    
    for (ActivityHandle activityHandle : activities.keySet())
    {
      try
      {
        logger.info("Ending activity [" + activityHandle + "]");
        Session[] sa = activities.values().toArray(new Session[activities.size()]);
        for (Session s : sa)
        {
          s.release();
        }
      }
      catch (Exception e)
      {
        logger.error("Error Deactivating Activity", e);
      }
    }
      
    this.state = ResourceAdaptorState.STOPPING;
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
    // Stop the stack
    this.stack.destroy();
    
    // Clean up!
    this.acif = null;
    this.activities = null;
    this.bootstrapContext = null;
    this.eventLookup = null;
    this.raProvider = null;
    this.sleeEndpoint = null;
    this.stack = null;
    
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
    logger.info( "Diameter Base RA :: eventProcessingFailed :: handle[" + handle + "], event[" + event + "], eventID[" + eventID + "], address[" + address + "], flags[" + flags + "], reason[" + reason + "]." );
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
    logger.info( "Diameter Base RA :: eventProcessingSuccessful :: handle[" + handle + "], event[" + event + "], eventID[" + eventID + "], address[" + address + "], flags[" + flags + "]." );
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

    if (!(activity instanceof DiameterActivity))
      return null;
    
    DiameterActivity inActivity = (DiameterActivity) activity;
    for (Map.Entry<ActivityHandle, DiameterActivity> activityInfo : this.activities.entrySet())
    {
        Object curActivity = activityInfo.getValue();
        if (curActivity.equals(inActivity))
          return activityInfo.getKey();
    }
    
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

  /**
   * Set up the JNDI naming context
   */
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
  
  /**
   * Clean the JNDI naming context
   */
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
  
  
  /**
   * Initializes the RA Diameter Stack.
   *
   * @throws Exception 
   */
  private synchronized void initStack() throws Exception
  {
    InputStream is = null;
    
    try
    {
      // Create and configure stack
      this.stack = new StackImpl();
      
      // Get configuration
      String configFile = "/jdiameter-config.xml";
      is = this.getClass().getResourceAsStream(configFile);
      
      // Load the configuration
      Configuration config =  new XMLConfiguration( is );

      this.stack.init( config );
      
      NetWork network = stack.unwrap(NetWork.class);
      
      for (ApplicationId appId : stack.getMetaData().getLocalPeerInfo().getCommonApplications())
          network.addNetworkReqListener(appId, this);
      
      this.stack.start();
    }
    finally
    {
      if( is != null )
        is.close();
        
      is = null;
    }
    
    logger.info("Diameter Base RA :: Successfully initialized stack.");
  }

  private static final Map<Integer, String> events;

  // FIXME alexandre: Use dictionary
  static {
      Map<Integer, String> temp = new HashMap<Integer, String>();
      temp.put(274, "AbortSession");
      temp.put(271, "Accounting");
      temp.put(257, "CapabilitiesExchange");
      temp.put(280, "DeviceWatchdog");
      temp.put(282, "DisconnectPeer");
      temp.put(258, "ReAuth");
      temp.put(275, "SessionTermination");
      events = Collections.unmodifiableMap(temp);
  }
  
  /**
   * RA Entry Point
   */
  public Answer processInitialRequest( Session session, Request request )
  {
    DiameterActivityHandle handle = createSessionHandle(session);
    
    fireEvent(handle, events.get(request.getCommandCode()) + "Request", request, null);
    
    return null;
  }
  
  /**
   * Create the Diameter Activity Handle for an activity
   * @param activity the Activity object to create the handle from
   * @return a DiameterActivityHandle for the provided activity object
   */
  private DiameterActivityHandle createSessionHandle(Session activity) {
    try {
        logger.info("Creating activity context.");
        DiameterActivityHandle activityHandle = new DiameterActivityHandle(activity.getSessionId());
        
        // FIXME alexandre: Create real activity here
        activities.put(activityHandle, this.raProvider.createActivity());
        
        sleeEndpoint.activityStarted( activityHandle );
        
        logger.info("Activity started [" + activityHandle + "]");
        
        return activityHandle;
    }
    catch (Exception e)
    {
        logger.error( "Error creating activity", e);
        throw new RuntimeException("Error creating activity", e);
    }
  }
  
  /**
   * Create Event object from request/answer
   * 
   * @param request the request to create the event from, if any.
   * @param answer the answer to create the event from, if any.
   * @return a DiameterMessage object wrapping the request/answer
   */
  private Object createEvent(Request request, Answer answer)
  {
    if(request == null && answer == null)
      return null;
    
    int commandCode = (request != null ? request.getCommandCode() : answer.getCommandCode());
    
    switch (commandCode)
    {
      case 274: // ASR/ASA
        return request != null ? new AbortSessionRequestImpl(request) : new AbortSessionAnswerImpl(answer);
      case 271: // ACR/ACA
        return request != null ? new AccountingRequestImpl(request) : new AccountingAnswerImpl(answer);
      case 257: // CER/CEA
        return request != null ? new CapabilitiesExchangeRequestImpl(request) : new CapabilitiesExchangeAnswerImpl(answer);
      case 280: // DWR/DWA
        return request != null ? new DeviceWatchdogRequestImpl(request) : new DeviceWatchdogAnswerImpl(answer);
      case 282: // DPR/DPA
        return request != null ? new DisconnectPeerRequestImpl(request) : new DisconnectPeerAnswerImpl(answer);
      case 258: // RAR/RAA
        return request != null ? new ReAuthRequestImpl(request) : new ReAuthAnswerImpl(answer);
      case 275: // STR/STA
        return request != null ? new SessionTerminationRequestImpl(request) : new SessionTerminationAnswerImpl(answer);
    }
    
    return new DiameterMessageImpl(request != null ? request : answer);
  }   

  private void fireEvent(Request request, Answer answer)
  {
    String sessionId = (request != null ? request.getSessionId() : answer.getSessionId());
    int commandCode = (request != null ? request.getCommandCode() : answer.getCommandCode());
    String postfix = (request != null ? "Request" : "Answer");
    
    this.fireEvent( new DiameterActivityHandle(sessionId),
        events.get(commandCode) + postfix, request, answer );
  }

  /**
   * Method for firing event to SLEE
   * 
   * @param handle the handle for the activity where event will be fired on
   * @param name the unqualified Event name
   * @param request the request that will be wrapped in the event, if any
   * @param answer the answer that will be wrapped in the event, if any
   */
  private void fireEvent(ActivityHandle handle, String name, Request request, Answer answer)
  {
    try
    {
      int eventID = eventLookup.getEventID("net.java.slee.resource.diameter.base.events." + name, "java.net", "0.8");
      
      DiameterMessage event = (DiameterMessage) createEvent(request, answer);
      
      sleeEndpoint.fireEvent(handle, event, eventID, null);
    }
    catch (Exception e)
    {
        logger.warn("Can not send event", e);
    }
  }
}
