/*
 * MgcpResourceAdaptor.java
 *
 * Media Gateway Control Protocol (MGCP) Resource Adaptor.
 *
 * The source code contained in this file is in in the public domain.
 * It can be used in any project or product without prior permission,
 * license or royalty payments. There is  NO WARRANTY OF ANY KIND,
 * EXPRESS, IMPLIED OR STATUTORY, INCLUDING, WITHOUT LIMITATION,
 * THE IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE,
 * AND DATA ACCURACY.  We do not warrant or make any representations
 * regarding the use of the software or the  results thereof, including
 * but not limited to the correctness, accuracy, reliability or
 * usefulness of the software.
 */

package org.mobicents.slee.resource.mgcp.ra;

import jain.protocol.ip.mgcp.message.CreateConnection;
import jain.protocol.ip.mgcp.message.CreateConnectionResponse;
import jain.protocol.ip.mgcp.message.DeleteConnection;
import jain.protocol.ip.mgcp.message.DeleteConnectionResponse;
import jain.protocol.ip.mgcp.message.parms.CallIdentifier;
import jain.protocol.ip.mgcp.message.parms.ConnectionIdentifier;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import javax.slee.Address;
import javax.slee.AddressPlan;
import javax.slee.UnrecognizedActivityException;
import javax.slee.UnrecognizedEventException;

import javax.slee.resource.ActivityIsEndingException;
import javax.slee.resource.ResourceAdaptorTypeID;
import javax.slee.resource.ResourceException;
import javax.slee.resource.ResourceAdaptor;
import javax.slee.resource.BootstrapContext;
import javax.slee.resource.SleeEndpoint;
import javax.slee.resource.ActivityHandle;
import javax.slee.resource.FailureReason;
import javax.slee.resource.Marshaler;

import javax.slee.facilities.EventLookupFacility;
import javax.slee.facilities.FacilityException;
import org.mobicents.mgcp.JainMgcpStackImpl;

import org.mobicents.slee.container.SleeContainer;
import org.mobicents.slee.resource.ResourceAdaptorActivityContextInterfaceFactory;
import org.mobicents.slee.resource.ResourceAdaptorEntity;

import jain.protocol.ip.mgcp.JainMgcpCommandEvent;
import jain.protocol.ip.mgcp.JainMgcpEvent;
import jain.protocol.ip.mgcp.JainMgcpListener;
import jain.protocol.ip.mgcp.JainMgcpProvider;
import jain.protocol.ip.mgcp.JainMgcpResponseEvent;
import jain.protocol.ip.mgcp.DeleteProviderException;
import jain.protocol.ip.mgcp.message.Constants;
import jain.protocol.ip.mgcp.message.Notify;
import jain.protocol.ip.mgcp.message.parms.NotifiedEntity;
import jain.protocol.ip.mgcp.message.parms.ReturnCode;

import org.apache.log4j.Logger;

/**
 *
 * @author Oleg Kulikov
 */
public class MgcpResourceAdaptor implements ResourceAdaptor, Serializable, JainMgcpListener {
    /** Holds activities under activity handle keys */
    private ConcurrentHashMap activities;
    
    /** Holds handlers under activities keys */
    private HashMap handlers;
    
    /**
     * The goal of the resource adaptor architecture is to define a management
     * contract between a SLEE application server and a resource adaptor that
     * allows the SLEE to manage to lifecycle of the resource adaptor. This
     * contract enables a SLEE to bootstrap a resource instance at deployment
     * time or during application server startup and to notify the resource
     * adaptor of its undeployment or during the orderly shutdown of the SLEE.
     */
    private BootstrapContext bootstrapContext;
    
    /**
     * This interface defines the methods that are required to represent a
     * proprietary JAIN MGCP protocol stack, the implementation of which will be
     * vendor-specific. Methods are defined for creating and deleting instances
     * of a JainMgcpProvider.
     */
    private JainMgcpStackImpl stack;
    
    /**
     * A MGCP resource object refers to an object created by a resource adaptor
     * entity. An SBB can obtain access to a resource object looking up a
     * resource object in the JNDI component environment of the SBB.
     */
    private JainMgcpProvider mgcpProvider;
    private JainMgcpProvider localProvider;
    
    /**
     * The SLEE endpoint defines the contract between the SLEE and the resource
     * adaptor that enables the resource adaptor to deliver events asynchronously
     * to SLEE endpoints residing in the SLEE. This contract serves as a generic
     * contract that allows a wide range of resources to be plugged into a SLEE
     * environment via the resource adaptor architecture.
     *
     * For further information see JSLEE v1.1 Specification, Early Draft Review
     * Page 307.
     * The sleeEndpoint will be initialized in entityCreated() method.
     */
    private transient SleeEndpoint sleeEndpoint = null;
    
    /**
     * EventLookupFacility is used to look up the event id of incoming events
     */
    private transient EventLookupFacility eventLookup = null;
    
    private Integer port = new Integer(2728);
    private String localAddress;
    
    private Logger logger = Logger.getLogger(MgcpResourceAdaptor.class);
    
    private MgcpActivityContextInterfaceFactoryImpl acif;
    
    /** Creates a new instance of MgcpResourceAdaptor */
    public MgcpResourceAdaptor() {
    }
    
    public Integer getPort() {
        return port;
    }
    
    public void setPort(Integer port) {
        this.port = port;
    }
    
    /**
     * This method is called by the SLEE when a resource adaptor object instance
     * is bootstrapped, either when a resource adaptor entity is created or
     * during SLEE startup. The configuration properties of the resource adaptor
     * entity are set before this method is invoked. The SLEE implementation
     * will construct the resource
     *
     * @param bootstrapContext contains references to the SLEE endpoint and the
     * facilities that may be used by the resource adaptor instance.
     */
    public void entityCreated(BootstrapContext bootstrapContext) throws ResourceException {
        System.out.println("--------- RA ENTITY CREATED --------");
        this.bootstrapContext = bootstrapContext;
        this.sleeEndpoint = bootstrapContext.getSleeEndpoint();
        this.eventLookup = bootstrapContext.getEventLookupFacility();
        this.handlers = new HashMap();
        this.activities = new ConcurrentHashMap();
    }
    
    /**
     * This method is called by the SLEE when a resource adaptor object instance
     * is being removed, either when a resource adaptor entity is deleted or
     * during SLEE shutdown. When receiving this invocation the resource adaptor
     * object is expected to close any system resources it has allocated.
     * The SLEE may garbage collect the resource adaptor object at this time.
     */
    public void entityRemoved() {
    }
    
    /**
     * This method is called by the SLEE to notify a resource adaptor object
     * that the resource adaptor entity is being activated. Before this method
     * is invoked the SLEE transitions the SleeEndpoint associated with the
     * resource adaptor object to the Active state. This allows the resource
     * adaptor object to fire events to the SLEE for new activities during the
     * entityActivated method invocation. If this method returns successfully,
     * the resource adaptor entity transitions to the Active state.
     */
    public void entityActivated() throws ResourceException {
        try {
            localAddress = InetAddress.getLocalHost().getHostName() + ":" + port;
        } catch (UnknownHostException e) {
            //should never happen
        }
        
        logger.info("Creating local MGCP provider");
        try {
            stack = new JainMgcpStackImpl();
            stack.setPort(port.intValue());
            
            localProvider = stack.createProvider();
            localProvider.addJainMgcpListener(this);
            
            mgcpProvider = new MgcpProviderLocal(this, localProvider);
            //mgcpProvider.addJainMgcpListener(this);
        } catch (Exception e) {
            throw new ResourceException(e.getMessage());
        }
        
        logger.info("Local MGCP provider created successfully");
        initializeNamingContext();
    }
    
    /**
     * The method is called by the SLEE to notify the resource adaptor object
     * that the resource adaptor entity is deactivating. On receipt of this
     * invocation the resource adaptor object is expected to perform any
     * internal transitions necessary such that it does not create any new
     * activities.
     *
     * Remove this resource adaptor entity from provider's listeners.
     */
    public void entityDeactivating() {
        mgcpProvider.removeJainMgcpListener(this);
    }
    
    /**
     * This method is called by the SLEE to notify the resource adaptor object
     * that the SLEE is not aware of any activities owned by the resource
     * adaptor entity and thus is transitioning the resource adaptor entity
     * from the Stopping state to the Inactive state.
     */
    public void entityDeactivated() {
        clearNamingContext();
        try {
            stack.deleteProvider(mgcpProvider);
        } catch (DeleteProviderException ex) {
            logger.error("Unexpected error while deleting provider", ex);
        }
    }
    
    /**
     * The SLEE calls this method to inform the resource adaptor object that the
     * specified event was processed successfully by the SLEE. An event is
     * considered to be processed successfully if the SLEE has attempted to
     * deliver the event to all interested SBBs. It is not required that the SBB
     * event handler or SBB rolled back transactions commit for event processing
     * to still be successful, unless failure to commit is caused by a system
     * level failure. This method need not be called by the same thread that
     * invoked the fireEvent method for this event.
     *
     * @param handle the activity handle of the activity upon which the event was fired.
     * @param event is the event fired on the activity. This value may be null
     * if no actual event was fired.
     * @param eventID the unique identifier of the event type fired to the SLEE.
     * If the event argument is null, the value of this parameter is undefined.
     * @param address the default address on which the event was fired. If a null
     * default address was specified, this value is also null. If the event
     * argument is null, the value of this parameter is undefined.
     * @param flags the necessary information that enables any post-event
     * processing required by the resource adaptor.
     */
    public void eventProcessingSuccessful(ActivityHandle handle, Object event,
            int eventID, Address address, int flags) {
    }
    
    /**
     * The SLEE calls this method to inform the resource adaptor object that
     * the specified event was processed unsuccessfully by the SLEE. Event
     * processing can fail if, for example, the SLEE doesn't have enough resource
     * to process the event, a SLEE node fails during event processing or a system
     * level failure prevents the SLEE from committing transactions.
     * This method need not be called by the same thread that invoked the fireEvent
     * method for this event.
     *
     * @param handle the activity handle of the activity upon which the event was fired.
     * @param event is the event fired on the activity. This value may be null
     * if no actual event was fired.
     * @param eventID the unique identifier of the event type fired to the SLEE.
     * If the event argument is null, the value of this parameter is undefined.
     * @param address the default address on which the event was fired. If a null
     * default address was specified, this value is also null. If the event
     * argument is null, the value of this parameter is undefined.
     * @param flags the necessary information that enables any post-event
     * processing required by the resource adaptor.
     * @param reason informs the resource adaptor why the event processing failed.
     */
    public void eventProcessingFailed(ActivityHandle handle, Object event,
            int eventID, Address address, int flags, FailureReason reason) {
    }
    
    /**
     * The SLEE calls this method to inform the resource adaptor that the SLEE
     * has completed activity end processing for the activity represented by
     * the activity handle. The resource adaptor should release any resource
     * related to this activity as the SLEE will not ask for it again.
     *
     * @param handle the activity handle of the activity which has ended.
     */
    public void activityEnded(ActivityHandle handle) {
        System.out.println("---ENDED ACTIVITY : " + handle);
        Object activity = activities.remove(handle);
        handlers.remove(activity);
    }
    
    /**
     * The SLEE calls this method to inform the resource adaptor that the
     * activity's Activity Context object is no longer attached to any SBB
     * entities and is no longer referenced by any SLEE Facilities.
     * This enables the resource adaptor to implicitly end the Activity object.
     *
     * @param handle the activity handle of the activity which has been unreferenced.
     */
    public void activityUnreferenced(ActivityHandle handle) {
        activityEnded(handle);
    }
    
    /**
     * The SLEE calls this method to query if a specific activity belonging to
     * this resource adaptor object is alive. This implies that the resource
     * adaptor will check the underlying resource to see if the activity is
     * still active. In this case the SLEE would retain the activity context of
     * the activity and SBB attached to the activity context.
     * If the activity is not alive the resource adaptor should call the
     * appropriate activityEnding method on the SleeEndpoint, if the activity is
     * still alive the resource adaptor is not expected to do anything.
     *
     * The resource adaptor object should not block on this method, for example
     * if the resource adaptor object needs to query an external system to
     * determine of an activity is alive, it should not block this method until
     * a response is received.
     *
     * @param handle the activity which the SLEE is querying.
     */
    public void queryLiveness(ActivityHandle handle) {
        if (!activities.containsKey(handle)) {
            try {
                sleeEndpoint.activityEnding(handle);
            } catch (Exception e) {
                logger.error("Unexpected error while ending activity", e);
            }
        }
    }
    
    /**
     * The SLEE calls this method to get access to the underlying activity for
     * an activity handle. The resource adaptor is expected to pass back a
     * non-null object.
     *
     * @param the activity handle of the requested activity.
     * @return the activity object.
     */
    public Object getActivity(ActivityHandle handle) {
        return activities.get(handle);
    }
    
    /**
     * The SLEE calls this method to get an activity handle for an activity
     * created by the underlying resource. This method is invoked by the SLEE
     * when it needs to construct an activity context for an activity via an
     * activity context interface factory method invoked by an SBB.
     *
     * @param activity the activity object.
     * @return activity handle or null if there is no handlers for specified activity.
     */
    public ActivityHandle getActivityHandle(Object activity) {
        return (ActivityHandle) handlers.get(activity.toString());
    }
    
    /**
     * The SLEE calls this method to get access to the underlying resource
     * adaptor interface that enables the SBB to invoke the resource adaptor,
     * to send messages for example.
     *
     * @return Jain MGCP provider.
     */
    public Object getSBBResourceAdaptorInterface(String string) {
        return mgcpProvider;
    }
    
    /**
     * The SLEE calls this method to get reference to the Marshaler object.
     * The resource adaptor implements the <code>Marshaler</code> interface.
     * The Marshaler is used by the SLEE to convert between object and
     * distributable forms of events and event handles. This ensures the SLEE
     * has flexibility to process events, for example in a clustered environment
     * it is conceptually possible for the SLEE to process the event on a node
     * other than the node the event was created on. The method takes no input
     * parameters. All resource adaptors must implement a Marshaler object hence
     * this method must return a non-null value.
     *
     * @return Marshaler object.
     */
    public Marshaler getMarshaler() {
        return null;
    }
    
    /**
     * The SLEE calls this method to signify to the resource adaptor that a
     * service has been installed and is interested in a specific set of events.
     * The SLEE passes an event filter which identifies a set of event types that
     * services in the SLEE are interested in. The SLEE calls this method once
     * a service is installed.
     *
     * @param serviceKey is the key of the service associated to the resource adaptor.
     * @param eventIDs the set of events the service identified by the service
     * key is interested in.
     * @param resourceOptions the set of resource options that map to the events
     * that are included in this filter.
     */
    public void serviceInstalled(String serviceKey, int[] eventIDs, String[] resourceOptions) {
    }
    
    /**
     * The SLEE calls this method to signify that a service has been uninstalled
     * in the SLEE. The event types associated to the service key are no longer
     * of interest to a particular application. The association between the
     * event types and the service key is deleted from the resource adaptor.
     * This doesn't preclude that another service may be interested in these
     * event types. The SLEE calls this method once a service is uninstalled.
     *
     * @param serviceKey the key of the service associated to the resource adaptor.
     */
    public void serviceUninstalled(String serviceKey) {
    }
    
    /**
     * The SLEE calls this method to inform the resource adaptor that a service
     * has been activated and is interested in the event types associated to the
     * service key. The service must be installed with the resource adaptor
     * via the serviceInstalled method before it can be activated.
     * The SLEE can call this method irrespective of the state of the resource adaptor.
     *
     * @param serviceKey the key of the service associated to the resource adaptor.
     */
    public void serviceActivated(String serviceKey) {
    }
    
    /**
     * The SLEE calls this method to inform the SLEE that a service has been
     * deactivated and is no longer interested in the event types associated to
     * the service key. The SLEE can call this method irrespective of the state
     * of the resource adaptor.
     *
     * @param the key of the service currently active in the resource adaptor.
     */
    public void serviceDeactivated(String serviceKey) {
    }
    
    /**
     * Creates handle for transaction activity.
     *
     * @param txID transaction activity object.
     * @return transaction activity handle object.
     */
    protected ActivityHandle createTxHandle(Integer txID) {
        System.out.println("*****CREATE TX HANLDE IN*******");
        showActivities();
        TransactionHandle txh = new TransactionHandle(txID.intValue());
        synchronized(activities) {
            activities.put(txh, txID);
        }
        synchronized(handlers) {
            handlers.put(txID.toString(), txh);
        }
        System.out.println("*****CREATE TX HANLDE OUT*******");
        showActivities();
        return txh;
    }
    
    
    /**
     * Creates handle for call activity.
     *
     * @param callID the identifier of the call.
     * @return handle for call activity.
     */
    protected ActivityHandle createCallHandle(CallIdentifier callID) {
        System.out.println("*****CREATE CALL HANLDE IN*******");
        showActivities();
        CallHandle h = new CallHandle(callID);
        synchronized(activities) {
            activities.put(h, callID);
        }
        synchronized (handlers) {
            handlers.put(callID.toString(), h);
        }
        System.out.println("*****CREATE CALL HANLDE OUT*******");
        showActivities();
        return h;
    }
    
    protected ActivityHandle createConnectionlHandle(ConnectionIdentifier connectionID) {
        ConnectionHandle h = new ConnectionHandle(connectionID);
        activities.put(h, connectionID);
        handlers.put(connectionID.toString(), h);
        return h;
    }
    
    /**
     * Ends notified entity activity.
     *
     * @param entityID the full name of the notified entity.
     */
    protected void endNotifiedEntityActivity(String entityID) {
        activityEnded(new EntityHandler(entityID));
    }
    
    /**
     * Processes a Command Event object received from a JainMgcpProvider
     *
     * @param event received command event.
     */
    public void processMgcpCommandEvent(JainMgcpCommandEvent event) {
        if (logger.isDebugEnabled()) {
            logger.debug(bootstrapContext.getEntityName() + " Receive request TX ID = "  + event.getTransactionHandle());
        }
        
        TransactionHandle txh = (TransactionHandle) createTxHandle(
                new Integer(event.getTransactionHandle()));
        
        switch (event.getObjectIdentifier()) {
            case Constants.CMD_AUDIT_CONNECTION :
                fireEvent("net.java.slee.resource.mgcp.AUDIT_CONNECTION", txh, event);
                break;
            case Constants.CMD_AUDIT_ENDPOINT :
                fireEvent("net.java.slee.resource.mgcp.AUDIT_ENDPOINT", txh, event);
                break;
            case Constants.CMD_CREATE_CONNECTION : {
                onCreateConnection(txh, (CreateConnection) event);
                fireEvent("net.java.slee.resource.mgcp.CREATE_CONNECTION", txh, event);
            }
            break;
            
            case Constants.CMD_DELETE_CONNECTION :
                fireEvent("net.java.slee.resource.mgcp.DELETE_CONNECTION", txh, event);
                break;
            case Constants.CMD_ENDPOINT_CONFIGURATION :
                fireEvent("net.java.slee.resource.mgcp.ENDPOINT_CONFIGURATION", txh, event);
                break;
            case Constants.CMD_MODIFY_CONNECTION :
                fireEvent("net.java.slee.resource.mgcp.MODIFY_CONNECTION", txh, event);
                break;
            case Constants.CMD_NOTIFICATION_REQUEST :
                fireEvent("net.java.slee.resource.mgcp.NOTIFICATION_REQUEST", txh, event);
                break;
            case Constants.CMD_NOTIFY :
                fireEvent("net.java.slee.resource.mgcp.NOTIFY", txh, event);
                
                //try to fire event on notified entity activity
                Notify notify = (Notify) event;
                NotifiedEntity entity = notify.getNotifiedEntity();
                
                // notified entity not specified
                if (entity == null) break;
                
                //fire notify event
                String entityID = entity.toString();
                EntityHandler h = new EntityHandler(entityID);
                fireEvent("net.java.slee.resource.mgcp.NOTIFY", h, event);
                break;
            case Constants.CMD_RESTART_IN_PROGRESS :
                fireEvent("net.java.slee.resource.mgcp.RESTART_IN_PROGRESS", txh, event);
                break;
            default :
                logger.warn("Unexpected event type: " + event.getObjectIdentifier());
        }
        
        //ending tx activity
        try {
            sleeEndpoint.activityEnding(txh);
            if (logger.isDebugEnabled()) {
                logger.debug("Ends activity TX ID " + txh.getId());
            }
        } catch (Exception ex) {
            logger.error("Unexpected error while ending activity: " + txh);
        }
        
    }
    
    /**
     * Processes a Response Event object (acknowledgment to a Command Event
     * object) received from a JainMgcpProvider.
     *
     * @param event The JAIN MGCP Response Event Object that is to be processed.
     */
    public void processMgcpResponseEvent(JainMgcpResponseEvent event) {
        if (logger.isDebugEnabled()) {
            logger.debug("Receive response TX ID = "  + event.getTransactionHandle());
        }
        
        Integer txID = new Integer(event.getTransactionHandle());
        TransactionHandle txh = (TransactionHandle) getActivityHandle(txID);
        
        if (txh == null) {
            logger.warn("Unknown transaction [TX ID = : " + txh.getId() + "]");
            return;
        }
        
        switch (event.getObjectIdentifier()) {
            case Constants.RESP_AUDIT_CONNECTION :
                fireEvent("net.java.slee.resource.mgcp.AUDIT_CONNECTION_RESPONSE", txh, event);
                break;
            case Constants.RESP_AUDIT_ENDPOINT :
                fireEvent("net.java.slee.resource.mgcp.AUDIT_ENDPOINT_RESPONSE", txh, event);
                break;
            case Constants.RESP_CREATE_CONNECTION :
                onCreateConnectionResponse(txh, (CreateConnectionResponse) event);
                fireEvent("net.java.slee.resource.mgcp.CREATE_CONNECTION_RESPONSE", txh, event);
                break;
            case Constants.RESP_DELETE_CONNECTION :
                fireEvent("net.java.slee.resource.mgcp.DELETE_CONNECTION_RESPONSE", txh, event);
                break;
            case Constants.RESP_ENDPOINT_CONFIGURATION :
                fireEvent("net.java.slee.resource.mgcp.ENDPOINT_CONFIGURATION_RESPONSE", txh, event);
                break;
            case Constants.RESP_MODIFY_CONNECTION :
                fireEvent("net.java.slee.resource.mgcp.MODIFY_CONNECTION_RESPONSE", txh, event);
                break;
            case Constants.RESP_NOTIFICATION_REQUEST :
                fireEvent("net.java.slee.resource.mgcp.NOTIFICATION_REQUEST_RESPONSE", txh, event);
                break;
            case Constants.RESP_NOTIFY :
                fireEvent("net.java.slee.resource.mgcp.NOTIFY_RESPONSE", txh, event);
                break;
            case Constants.RESP_RESTART_IN_PROGRESS :
                fireEvent("net.java.slee.resource.mgcp.RESTART_IN_PROGRESS_RESPONSE", txh, event);
                break;
            default :
                logger.warn("Unexpected event type: " + event.getObjectIdentifier());
        }
        
        //ending tx activity
        ReturnCode status = event.getReturnCode();
        if (status.getValue() != ReturnCode.TRANSACTION_BEING_EXECUTED) {
            try {
                sleeEndpoint.activityEnding(txh);
                if (logger.isDebugEnabled()) {
                    logger.debug("Ends activity TX ID " + txh.getId());
                }
            } catch (Exception ex) {
                logger.error("Unexpected error while ending activity: " + txh);
            }
        } else if (logger.isDebugEnabled()) {
            logger.debug("TX ID = " + txh.getId() + " stay in pending state");
        }
    }
    
    /**
     * Fires specified event on specified context.
     *
     * @param eventName the name of the event that is to be fired.
     * @param handle the activity handle object.
     * @param event the Jain MGCP event object.
     */
    protected void fireEvent(String eventName, ActivityHandle handle, JainMgcpEvent event) {
        if (handle == null) {
            logger.warn("Unknown activity handle" + handle);
            return;
        }
        
        int eventID = -1;
        try {
            eventID = eventLookup.getEventID(eventName, "net.java","1.0");
        } catch (FacilityException fe) {
            logger.error("Caught a FacilityException: ", fe);
            throw new RuntimeException("MgcpResourceAdaptor.fireEvent(): FacilityException caught. ", fe);
        } catch (UnrecognizedEventException ue) {
            logger.error("Caught an UnrecognizedEventException: ", ue);
            throw new RuntimeException("MgcpResourceAdaptor.fireEvent(): UnrecognizedEventException caught.", ue);
        }
        
        if (eventID == -1) {
            logger.warn("Unknown event type: " + eventName);
            return;
        }
        
        try {
            Address address = new Address(AddressPlan.IP, "localhost");
            sleeEndpoint.fireEvent(handle, event, eventID, address);
            logger.info("Fire event: " + eventName);
        } catch (IllegalStateException ise) {
            logger.error("Caught an IllegalStateException: ", ise);
        } catch (ActivityIsEndingException aiee) {
            logger.error("Caught an ActivityIsEndingException: ", aiee);
        } catch (UnrecognizedActivityException uaee) {
            logger.error("Caught an UnrecognizedActivityException: ", uaee);
        }
    }
    
    private void onCreateConnection(TransactionHandle txh, CreateConnection event) {
        txh.setCallID(event.getCallIdentifier());
        if (logger.isDebugEnabled()) {
            logger.debug(bootstrapContext.getEntityName() + 
                    " onCreateConnection: TX ID = " + txh.getId() + " hold call identifier: " + event.getCallIdentifier());
        }
    }
    
    private void onCreateConnectionResponse(TransactionHandle txh, CreateConnectionResponse event) {
        CallIdentifier callID = txh.getCallID();
        CallHandle callHandle = (CallHandle) createCallHandle(callID);
        
        if (logger.isDebugEnabled()) {
            logger.debug(bootstrapContext.getEntityName() + " TX ID = " + txh.getId() + " started call activity : " + callID);
        }
        
        ConnectionIdentifier connectionID = event.getConnectionIdentifier();
        ConnectionHandle connectionHandle = (ConnectionHandle) createConnectionlHandle(connectionID);
        callHandle.add(connectionHandle);
        
        if (logger.isDebugEnabled()) {
            logger.debug(bootstrapContext.getEntityName() + " TX ID = " + txh.getId() + " started connection activity: " + connectionID);
        }
    }
    
    private void onDeleteConnection(TransactionHandle txh, DeleteConnection event) {
        txh.setCallID(event.getCallIdentifier());
        txh.setConnectionIdentifier(event.getConnectionIdentifier());
    }
    
    private void onDeleteResponse(TransactionHandle txh, DeleteConnectionResponse event) {
        if (txh.getCallID() != null) {
            CallHandle callHandle = (CallHandle) getActivityHandle(txh.getCallID());
            Iterator list = callHandle.getConnectionHandlers().iterator();
            
            while (list.hasNext()) {
                ConnectionHandle connectionHandle = (ConnectionHandle) list.next();
                callHandle.remove(connectionHandle);
                activityEnded(connectionHandle);
                if (logger.isDebugEnabled()) {
                    logger.debug("TX ID = " + txh.getId() + " ended connection activity: " + connectionHandle.getId());
                }
            }
            
            activityEnded(callHandle);
            if (logger.isDebugEnabled()) {
                logger.debug("TX ID = " + txh.getId() + " ended call activity: " + callHandle.getId());
            }
        } else if (txh.getConnectionID() != null) {
            ConnectionHandle connectionHandle = (ConnectionHandle) getActivityHandle(txh.getConnectionID());
            activityEnded(connectionHandle);
            if (logger.isDebugEnabled()) {
                logger.debug("TX ID = " + txh.getId() + " ended connection activity: " + connectionHandle.getId());
            }
            //@todo remove from call handle
        } else {
            //end connections for endpoint
        }
    }
    
    private void showActivities() {
        //java.util.Iterator list = handlers.keySet().iterator();
        System.out.println("------------------------------");
        System.out.println(bootstrapContext.getEntityName());
        System.out.println("map " + handlers + ", size " + handlers.size());
/*        while (list.hasNext()) {
            Object key = list.next();
            System.out.println(key + " : " + handlers.get(key));
        }
 */
        System.out.println("------------------------------");
    }
    /**
     * Sends Jain Mgcp Event.
     *
     * @param event an event to send.
     */
    protected void send(JainMgcpEvent evt) {
        showActivities();
        
        switch (evt.getObjectIdentifier()) {
            case Constants.CMD_CREATE_CONNECTION : {
                Integer txID = new Integer(evt.getTransactionHandle());
                TransactionHandle txh = (TransactionHandle) this.createTxHandle(txID);
                onCreateConnection(txh, (CreateConnection) evt);
                break;
            }
            
            case Constants.RESP_CREATE_CONNECTION : {
                Integer txID = new Integer(evt.getTransactionHandle());
                TransactionHandle txh = (TransactionHandle) getActivityHandle(txID);
                onCreateConnectionResponse(txh, (CreateConnectionResponse) evt);
                break;
            }
        }
        localProvider.sendMgcpEvents(new JainMgcpEvent[]{evt});
    }
    
    /**
     * Registers MgcpActivityContextInterfaceFactory with JNDI name.
     *
     */
    private void initializeNamingContext() {
        logger.info("Initialize naming context");
        
        SleeContainer sleeContainer = SleeContainer.lookupFromJndi();
        logger.debug("SLEE container: " + sleeContainer);
        
        String entityName = bootstrapContext.getEntityName();
        logger.debug("Entity name: " + entityName);
        
        ResourceAdaptorEntity resourceAdaptorEntity =
                (ResourceAdaptorEntity) sleeContainer.getResourceAdaptorEnitity(entityName);
        logger.debug("Resource Adaptor Entity: " + resourceAdaptorEntity);
        
        ResourceAdaptorTypeID resourceAdaptorTypeId =
                resourceAdaptorEntity.getInstalledResourceAdaptor().getRaType().getResourceAdaptorTypeID();
        logger.debug("Resource Adaptor Type ID: " + resourceAdaptorTypeId);
        
        acif = new MgcpActivityContextInterfaceFactoryImpl(
                resourceAdaptorEntity.getServiceContainer(), this, entityName);
        sleeContainer.getActivityContextInterfaceFactories().put(resourceAdaptorTypeId, acif);
        
        String jndiName = ((ResourceAdaptorActivityContextInterfaceFactory)acif).getJndiName();
        
        int i = jndiName.indexOf(':');
        int j = jndiName.lastIndexOf('/');
        
        String prefix = jndiName.substring(i + 1, j);
        String name = jndiName.substring(j + 1);
        
        sleeContainer.registerWithJndi(prefix, name, acif);
    }
    
    /**
     * Unregisters MgcpActivityContextInterfaceFactory
     */
    private void clearNamingContext() {
        String jndiName = ((ResourceAdaptorActivityContextInterfaceFactory)
        acif).getJndiName();
        int i = jndiName.indexOf(':');
        String name = jndiName.substring(i + 1);
        
        SleeContainer.unregisterWithJndi(name);
    }
    
}
