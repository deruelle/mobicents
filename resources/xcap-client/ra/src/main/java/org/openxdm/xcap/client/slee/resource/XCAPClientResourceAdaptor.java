package org.openxdm.xcap.client.slee.resource;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.slee.Address;
import javax.slee.InvalidStateException;
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
import org.mobicents.slee.container.component.ComponentKey;
import org.mobicents.slee.resource.ResourceAdaptorActivityContextInterfaceFactory;
import org.mobicents.slee.resource.ResourceAdaptorEntity;
import org.mobicents.slee.resource.ResourceAdaptorState;
import org.openxdm.xcap.client.Response;
import org.openxdm.xcap.client.XCAPClient;
import org.openxdm.xcap.client.XCAPClientImpl;
import org.openxdm.xcap.client.key.AttributeUriKey;
import org.openxdm.xcap.client.key.DocumentUriKey;
import org.openxdm.xcap.client.key.ElementUriKey;
import org.openxdm.xcap.client.key.NamespaceBindingsUriKey;
import org.openxdm.xcap.client.key.XcapUriKey;

/** 
 * @author Eduardo Martins
 * @version 1.0
 * 
 */

public class XCAPClientResourceAdaptor implements ResourceAdaptor,
		Serializable {
    
 	private static final long serialVersionUID = 1L;
	static private transient Logger log;
    static {
        log = Logger.getLogger(XCAPClientResourceAdaptor.class);
    }
    
    private String XCAP_SERVER_HOST;
    private int XCAP_SERVER_PORT;
    private String XCAP_SERVER_ROOT;
    private static final int MAX_CONCURRENT_THREADS = 10;
    private static final ComponentKey responseEventComponentkey = new ComponentKey("ResponseEvent", "org.openxdm", "1.0");
    
   
    private ResourceAdaptorState state;    
    private transient Set<XCAPResourceAdaptorActivityHandle> handles = Collections.synchronizedSet(new HashSet<XCAPResourceAdaptorActivityHandle>());    
    private transient SleeEndpoint sleeEndpoint;
    private transient BootstrapContext bootstrapContext;
    private transient XCAPClientResourceAdaptorSbbInterface sbbInterface;
    private transient XCAPClientActivityContextInterfaceFactory acif;
    private transient XCAPClient client;  
    private transient int responseEventID;
    private transient ExecutorService executorService = Executors.newCachedThreadPool();
   
    public XCAPClientResourceAdaptor() { }
    
    public String getServerHost() {
        return XCAP_SERVER_HOST;
    }
    
    public void setServerHost(String serverHost) {
        this.XCAP_SERVER_HOST = serverHost;
    }
    
    public Integer getServerPort() {
        return Integer.valueOf(XCAP_SERVER_PORT);
    }
    
    public void setServerPort(Integer port) {
        this.XCAP_SERVER_PORT = port.intValue();
    }
    
    public String getXcapRoot() {
        return XCAP_SERVER_ROOT;
    }
    
    public void setXcapRoot(String xcapRoot) {
        this.XCAP_SERVER_ROOT = xcapRoot;
    }
    
    public void entityCreated(BootstrapContext ctx) throws javax.slee.resource.ResourceException {
    	//TODO
	    if (log.isDebugEnabled()) {
	    	log.debug("entityCreated");
	    }
	    this.init(ctx);
	}

	public void entityRemoved() {
		//TODO
	    if (log.isDebugEnabled()) {
	    	log.debug("entityRemoved");
	    }
	}

	public void entityActivated() throws ResourceException {
	    if (log.isDebugEnabled()) {
	    	log.debug("entityActivated");
	    }
	    
	    try {
	    	this.configure();
	    } catch (InvalidStateException e1) {
	    	log.warn(e1);
	    }
	    this.start();
        
	}

	public void entityDeactivating() {
		//TODO
		if (log.isDebugEnabled()) {
			log.debug("entityDeactivating");
		}
		this.stopping();

	}

	public void entityDeactivated() {
		if (log.isDebugEnabled()) {
			log.debug("entityDeactivated");
		}
		this.stop();
	}

	public void eventProcessingSuccessful(ActivityHandle arg0, Object arg1,
			int arg2, Address arg3, int arg4) {
		// log if needed
		if (log.isDebugEnabled()) {
			String msg = new StringBuilder("eventProcessingSuccessful(ActivityHandle=").append(arg0).append(",Object=").append(arg1).append(")").toString();
			log.debug(msg);
		}
	}

	public void eventProcessingFailed(ActivityHandle arg0, Object arg1,
			int arg2, Address arg3, int arg4, FailureReason arg5) {
		// log if needed
		if(log.isDebugEnabled()) {
			String msg = new StringBuilder("eventProcessingFailed(ActivityHandle=").append(arg0).append(",Object=").append(arg1).append(",FailureReason=").append(arg5).append(")").toString();
			log.warn(msg);
		}	
	}

	public void endActivity(ActivityHandle ah) {
		if (log.isDebugEnabled()) {
			log.debug("endActivity");
		}
		// tell slee to end the activity
		try {
			this.sleeEndpoint.activityEnding(ah);
		} catch (Exception e) {
			log.error("unable to end activity: ",e);
		}
	}
	
	public void activityEnded(ActivityHandle ah) {		
		if (log.isDebugEnabled()) {
			log.debug("activityEnded(ActivityHandle="+ah+")");
		}
		// just remove the handle
	    handles.remove(ah);
	}

	public void activityUnreferenced(ActivityHandle ah) {		
		if (log.isDebugEnabled()) {
			log.debug("activityUnreferenced");
		}
		activityEnded(ah);
	}

	public void queryLiveness(ActivityHandle ah) {
		if (log.isDebugEnabled()) {
			log.debug("queryLiveness");
		}
		// if handle does not exist fire an activity end event
		if(!handles.contains(ah)) {
			endActivity(ah);
		}				
	}

	public Object getActivity(ActivityHandle ah) {
	    if (log.isDebugEnabled()) {
	    	log.debug("get Activity with ActivityHandle "+ah.toString());
	    }
	    // if handle exists then recreate activity
	    if(handles.contains(ah)) {
	    	// get key	    	
	    	XcapUriKey key = ((XCAPResourceAdaptorActivityHandle)ah).getKey();
	    	// recreate activity
	    	if (key instanceof DocumentUriKey) {
	    		return new DocumentUriKeyActivityImpl(this,(DocumentUriKey)key);
	    	}
	    	else if (key instanceof ElementUriKey) {
	    		return new ElementUriKeyActivityImpl(this,(ElementUriKey)key);
	    	}
	    	else if (key instanceof AttributeUriKey) {
	    		return new AttributeUriKeyActivityImpl(this,(AttributeUriKey)key);
	    	}
	    	else if (key instanceof NamespaceBindingsUriKey) {
	    		return new NamespaceBindingsUriKeyActivityImpl(this,(NamespaceBindingsUriKey)key);
	    	}
	    	else {
	    		return new XcapUriKeyActivityImpl(this,key);
	    	}	    	
	    }
	    else {
	    	return null;
	    }
	}

	public ActivityHandle getActivityHandle(Object arg0) {
	    if (log.isDebugEnabled()) {
	    	log.debug("getActivityHandle");
	    }
	    XCAPResourceAdaptorActivityHandle activityHandle = null;
	    try {
	    	activityHandle = new XCAPResourceAdaptorActivityHandle(((XcapUriKeyActivity)arg0).getXcapUriKey());
	    }
	    catch (Exception e) {
	    	log.error("Failed to get the ActivityHandle.",e);
	    }
	    return activityHandle;
	}

	public Object getSBBResourceAdaptorInterface(String arg0) {
		if (log.isDebugEnabled()) {
			log.debug("getSBBResourceAdaptorInterface");
		}
		return this.sbbInterface;
	}

	public Marshaler getMarshaler() {
		//TODO
		if (log.isDebugEnabled()) {
			log.debug("getMarshaler");
		}
		return null;
	}

	public void serviceInstalled(String arg0, int[] arg1, String[] arg2) {				
		// EVENT FILTERING IS NO GOOD FOR THIS RA	
	}

	public void serviceActivated(String arg0) {		
		// EVENT FILTERING IS NO GOOD FOR THIS RA
	}

	public void serviceDeactivated(String arg0) {
		// EVENT FILTERING IS NO GOOD FOR THIS RA
	}

	public void serviceUninstalled(String arg0) {	
		// EVENT FILTERING IS NO GOOD FOR THIS RA
	}

	public void init(BootstrapContext bootstrapContext) throws javax.slee.resource.ResourceException {
	    if (log.isDebugEnabled()) {
	    	log.debug("init");
	    }	    
		this.bootstrapContext = bootstrapContext;
        this.sleeEndpoint = bootstrapContext.getSleeEndpoint();        
        try {
        	this.responseEventID = bootstrapContext.getEventLookupFacility().getEventID(responseEventComponentkey.getName(),responseEventComponentkey.getVendor(),responseEventComponentkey.getVersion());
        } catch (Exception e) {
        	throw new ResourceException(e.getMessage());
        }
        // TODO use JSLEE 1.1 facilities
        state = ResourceAdaptorState.UNCONFIGURED;
    }

	public void configure() throws InvalidStateException {
	    if (log.isDebugEnabled()) {
	    	log.debug("configure");
	    }
	    if (this.state != ResourceAdaptorState.UNCONFIGURED) {
			throw new InvalidStateException("Cannot configure RA wrong state: " + this.state);
        }				    						
		state = ResourceAdaptorState.CONFIGURED;			
	}
	
	public void start() throws ResourceException {
	    if (log.isDebugEnabled()) {
	    	log.debug("start");
	    }
		try {            
            
			SleeContainer container = SleeContainer.lookupFromJndi();
			ResourceAdaptorEntity resourceAdaptorEntity = ((ResourceAdaptorEntity) container
                .getResourceAdaptorEnitity(this.bootstrapContext.getEntityName()));
			
			ResourceAdaptorTypeID raTypeId = resourceAdaptorEntity
                .getInstalledResourceAdaptor().getRaType().getResourceAdaptorTypeID();			
			
			this.acif = new XCAPClientActivityContextInterfaceFactoryImpl(
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
				if (log.isDebugEnabled()) {
					log.debug("jndiName prefix =" + prefix + "; jndiName = " + name);
				}
				SleeContainer.registerWithJndi(prefix, name, this.acif);
			}
			// init client
			client = new XCAPClientImpl(XCAP_SERVER_HOST,XCAP_SERVER_PORT,XCAP_SERVER_ROOT,MAX_CONCURRENT_THREADS);
			// create sbb interface
			 sbbInterface = new XCAPClientResourceAdaptorSbbInterfaceImpl(this);
		}
        catch (Exception ex) {
            ex.printStackTrace();
            throw new ResourceException(ex.getMessage());
        }

       
        state = ResourceAdaptorState.ACTIVE;
	}

	/**
	 * Stops this resource adaptor.
	 *
	 */
	public void stop() {
	   
		if (log.isDebugEnabled()) {
	    	log.debug("stop");
	    }
	    
		// end all activities
	    synchronized(handles) {	    
	    	for(Iterator i=handles.iterator(); i.hasNext(); ) {
	    		XCAPResourceAdaptorActivityHandle handle = (XCAPResourceAdaptorActivityHandle)i.next();	    		
	    		endActivity(handle);
	    	}
	    }
	    if (log.isDebugEnabled()) {
	    	log.debug("All activities ended.");
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
            log.error("Can't unbind naming context",e);
        }

        this.sbbInterface = null;
        this.client.shutdown();
        this.client = null;
        this.executorService.shutdown();
        this.executorService = null;
        
        if (log.isDebugEnabled()) {
        	log.debug("Xmpp Resource Adaptor stopped.");
        }
	}

	public void stopping() {
	    if (log.isDebugEnabled()) {
	    	log.debug("stopping");
	    }
		state = ResourceAdaptorState.STOPPING;
	}

	public Object getFactoryInterface() {
		//TODO
	    if (log.isDebugEnabled()) {
	    	log.debug("getFactoryInterface");
	    }
		//return this.factory;
	    return null;
	}

	public Object getActivityContextInterfaceFactory() {
	    if (log.isDebugEnabled()) {
	    	log.debug("getActivityContextInterfaceFactory");
	    }
		return acif;
	}

	public void setResourceAdaptorEntity( ResourceAdaptorEntity resourceAdaptorEntity) {
	    //TODO
		if (log.isDebugEnabled()) {
	    	log.debug("setResourceAdaptorEntity");
	    }
	}
	        
    /* Receives an Event and sends it to the SLEE */
    public void processResponseEvent(ResponseEvent event, XCAPResourceAdaptorActivityHandle handle){
        
    	if (log.isDebugEnabled()) {    		            		
    		log.debug("NEW RESPONSE EVENT");        
    	}
                        
        try {                	
        	sleeEndpoint.fireEvent(handle, event, responseEventID, null);        	
        } catch (Exception e) {           
            log.warn("unable to fire event",e);
        }        
    }
    
    protected Set<XCAPResourceAdaptorActivityHandle> getHandles() {                
        return handles;
    }  
        
    /**
     * @return Returns the sleeEndpoint.
     */
    public SleeEndpoint getSleeEndpoint() {
        if (log.isDebugEnabled()) {
        	log.debug("getSleeEndpoint");
        }
        return sleeEndpoint;
    }

    /**
     * @param sleeEndpoint The sleeEndpoint to set.
     */
    public void setSleeEndpoint(SleeEndpoint sleeEndpoint) {
        if (log.isDebugEnabled()) {
        	log.debug("setSleeEndpoint");
        }
        this.sleeEndpoint = sleeEndpoint;
    }
        
	public XCAPClient getClient() {
		return client;
	}
    
	public ExecutorService getExecutorService() {
		return executorService;
	}
	
    // ASYNC REQUEST HANDLERS
    
    // GET
	
    protected class AsyncGetHandler implements Runnable {

    	private XcapUriKey key;
    	
    	public AsyncGetHandler(XcapUriKey key) {
			this.key = key;
		}
    	
    	public void run() {
			ResponseEvent event = null;
    		try {
    			// execute method and get response
    			Response response = client.get(key);
    			// create event with response
    			event = new ResponseEvent(response);
    		}
    		catch(Exception e) {
    			// create event with exception
    			event = new ResponseEvent(e);
    		}    		
    		// process event
    		processResponseEvent(event,new XCAPResourceAdaptorActivityHandle(key));			
		}
    }

    // GET AND UNMARSHALL
    
    protected class AsyncGetAndUnmarshallDocumentHandler implements Runnable {


    	private DocumentUriKey key;
    	
    	public AsyncGetAndUnmarshallDocumentHandler(DocumentUriKey key) {
			this.key = key;
		}
    	    	
    	public void run() {
			ResponseEvent event = null;
    		try {
    			// execute method and get response
    			Response response = client.getAndUnmarshallDocument(key);
    			// create event with response
    			event = new ResponseEvent(response);
    		}
    		catch(Exception e) {
    			// create event with exception
    			event = new ResponseEvent(e);
    		}    		
    		// process event
    		processResponseEvent(event,new XCAPResourceAdaptorActivityHandle(key));			
		}
    	
    }

    protected class AsyncGetAndUnmarshallElementHandler implements Runnable {


    	private ElementUriKey key;
    	
    	public AsyncGetAndUnmarshallElementHandler(ElementUriKey key) {
			this.key = key;
		}
    	
    	public void run() {    		
    		ResponseEvent event = null;
    		try {
    			// execute method and get response
    			Response response = client.getAndUnmarshallElement(key);
    			// create event with response
    			event = new ResponseEvent(response);
    		}
    		catch(Exception e) {
    			// create event with exception
    			event = new ResponseEvent(e);
    		}    		
    		// process event
    		processResponseEvent(event,new XCAPResourceAdaptorActivityHandle(key));    		
    	}
    }
    
    // MARSHALL AND PUT DOCUMENT
    
    protected class AsyncMarshallAndPutDocumentHandler implements Runnable {


    	private DocumentUriKey key;
    	private String mimetype;
    	private Object content;
    	
    	public AsyncMarshallAndPutDocumentHandler(DocumentUriKey key, String mimetype, Object content) {
			this.key = key;
			this.mimetype = mimetype;
			this.content = content;
		}
    	
    	public void run() {    		
    		ResponseEvent event = null;
    		try {
    			// execute method and get response
    			Response response = client.marshallAndPutDocument(key,mimetype,content);
    			// create event with response
    			event = new ResponseEvent(response);
    		}
    		catch(Exception e) {
    			// create event with exception
    			event = new ResponseEvent(e);
    		}    		
    		// process event
    		processResponseEvent(event,new XCAPResourceAdaptorActivityHandle(key));    		
    	}
    }
    
    protected class AsyncMarshallAndPutDocumentIfMatchHandler implements Runnable {


    	private DocumentUriKey key;
    	private String eTag;
    	private String mimetype;
    	private Object content;
    	    	
    	public AsyncMarshallAndPutDocumentIfMatchHandler(DocumentUriKey key, String eTag, String mimetype, Object content) {
			this.key = key;
			this.eTag = eTag;
			this.mimetype = mimetype;
			this.content = content;
		}
    	
    	public void run() {    		
    		ResponseEvent event = null;
    		try {
    			// execute method and get response
    			Response response = client.marshallAndPutDocumentIfMatch(key,eTag,mimetype,content);
    			// create event with response
    			event = new ResponseEvent(response);
    		}
    		catch(Exception e) {
    			// create event with exception
    			event = new ResponseEvent(e);
    		}    		
    		// process event
    		processResponseEvent(event,new XCAPResourceAdaptorActivityHandle(key));    		
    	}
    }   

    protected class AsyncMarshallAndPutDocumentIfNoneMatchHandler implements Runnable {


    	private DocumentUriKey key;
    	private String eTag;
    	private String mimetype;
    	private Object content;
    	    	
    	public AsyncMarshallAndPutDocumentIfNoneMatchHandler(DocumentUriKey key, String eTag, String mimetype, Object content) {
			this.key = key;
			this.eTag = eTag;
			this.mimetype = mimetype;
			this.content = content;
		}
    	
    	public void run() {    		
    		ResponseEvent event = null;
    		try {
    			// execute method and get response
    			Response response = client.marshallAndPutDocumentIfNoneMatch(key,eTag,mimetype,content);
    			// create event with response
    			event = new ResponseEvent(response);
    		}
    		catch(Exception e) {
    			// create event with exception
    			event = new ResponseEvent(e);
    		}    		
    		// process event
    		processResponseEvent(event,new XCAPResourceAdaptorActivityHandle(key));    		
    	}
    }   

    // MARSHALL AND PUT ELEMENT
    
    protected class AsyncMarshallAndPutElementHandler implements Runnable {


    	private ElementUriKey key;
    	private Object content;
    	
    	public AsyncMarshallAndPutElementHandler(ElementUriKey key, Object content) {
			this.key = key;
			this.content = content;
		}
    	
    	public void run() {    		
    		ResponseEvent event = null;
    		try {
    			// execute method and get response
    			Response response = client.marshallAndPutElement(key,content);
    			// create event with response
    			event = new ResponseEvent(response);
    		}
    		catch(Exception e) {
    			// create event with exception
    			event = new ResponseEvent(e);
    		}    		
    		// process event
    		processResponseEvent(event,new XCAPResourceAdaptorActivityHandle(key));    		
    	}
    }
    
    protected class AsyncMarshallAndPutElementIfMatchHandler implements Runnable {


    	private ElementUriKey key;
    	private String eTag;
    	private Object content;
    	    	
    	public AsyncMarshallAndPutElementIfMatchHandler(ElementUriKey key, String eTag, Object content) {
			this.key = key;
			this.eTag = eTag;
			this.content = content;
		}
    	
    	public void run() {    		
    		ResponseEvent event = null;
    		try {
    			// execute method and get response
    			Response response = client.marshallAndPutElementIfMatch(key,eTag,content);
    			// create event with response
    			event = new ResponseEvent(response);
    		}
    		catch(Exception e) {
    			// create event with exception
    			event = new ResponseEvent(e);
    		}    		
    		// process event
    		processResponseEvent(event,new XCAPResourceAdaptorActivityHandle(key));    		
    	}
    }   

    protected class AsyncMarshallAndPutElementIfNoneMatchHandler implements Runnable {


    	private ElementUriKey key;
    	private String eTag;
    	
    	private Object content;
    	    	
    	public AsyncMarshallAndPutElementIfNoneMatchHandler(ElementUriKey key, String eTag, Object content) {
			this.key = key;
			this.eTag = eTag;
			this.content = content;
		}
    	
    	public void run() {    		
    		ResponseEvent event = null;
    		try {
    			// execute method and get response
    			Response response = client.marshallAndPutElementIfNoneMatch(key,eTag,content);
    			// create event with response
    			event = new ResponseEvent(response);
    		}
    		catch(Exception e) {
    			// create event with exception
    			event = new ResponseEvent(e);
    		}    		
    		// process event
    		processResponseEvent(event,new XCAPResourceAdaptorActivityHandle(key));    		
    	}
    }   
    
    // PUT DOCUMENT
    
    protected class AsyncPutDocumentStringHandler implements Runnable {


    	private DocumentUriKey key;
    	private String mimetype;
    	private String content;
    	    	
    	public AsyncPutDocumentStringHandler(DocumentUriKey key, String mimetype, String content) {
			this.key = key;
			this.mimetype = mimetype;
			this.content = content;
		}
    	
    	public void run() {    		
    		ResponseEvent event = null;
    		try {
    			// execute method and get response
    			Response response = client.putDocument(key,mimetype,content);
    			// create event with response
    			event = new ResponseEvent(response);
    		}
    		catch(Exception e) {
    			// create event with exception
    			event = new ResponseEvent(e);
    		}    		
    		// process event
    		processResponseEvent(event,new XCAPResourceAdaptorActivityHandle(key));    		
    	}
    }   
    
    protected class AsyncPutDocumentStringIfMatchHandler implements Runnable {


    	private DocumentUriKey key;
    	private String eTag;
    	private String mimetype;
    	private String content;
    	    	
    	public AsyncPutDocumentStringIfMatchHandler(DocumentUriKey key, String eTag, String mimetype, String content) {
			this.key = key;
			this.eTag = eTag;
			this.mimetype = mimetype;
			this.content = content;
		}
    	
    	public void run() {    		
    		ResponseEvent event = null;
    		try {
    			// execute method and get response
    			Response response = client.putDocumentIfMatch(key,eTag,mimetype,content);
    			// create event with response
    			event = new ResponseEvent(response);
    		}
    		catch(Exception e) {
    			// create event with exception
    			event = new ResponseEvent(e);
    		}    		
    		// process event
    		processResponseEvent(event,new XCAPResourceAdaptorActivityHandle(key));    		
    	}
    }
    
    protected class AsyncPutDocumentStringIfNoneMatchHandler implements Runnable {


    	private DocumentUriKey key;
    	private String eTag;
    	private String mimetype;
    	private String content;
    	    	
    	public AsyncPutDocumentStringIfNoneMatchHandler(DocumentUriKey key, String eTag, String mimetype, String content) {
			this.key = key;
			this.eTag = eTag;
			this.mimetype = mimetype;
			this.content = content;
		}
    	
    	public void run() {    		
    		ResponseEvent event = null;
    		try {
    			// execute method and get response
    			Response response = client.putDocumentIfNoneMatch(key,eTag,mimetype,content);
    			// create event with response
    			event = new ResponseEvent(response);
    		}
    		catch(Exception e) {
    			// create event with exception
    			event = new ResponseEvent(e);
    		}    		
    		// process event
    		processResponseEvent(event,new XCAPResourceAdaptorActivityHandle(key));    		
    	}
    }
    
    protected class AsyncPutDocumentByteArrayHandler implements Runnable {


    	private DocumentUriKey key;
    	private String mimetype;
    	private byte[] content;
    	    	
    	public AsyncPutDocumentByteArrayHandler(DocumentUriKey key, String mimetype, byte[] content) {
			this.key = key;
			this.mimetype = mimetype;
			this.content = content;
		}
    	
    	public void run() {    		
    		ResponseEvent event = null;
    		try {
    			// execute method and get response
    			Response response = client.putDocument(key,mimetype,content);
    			// create event with response
    			event = new ResponseEvent(response);
    		}
    		catch(Exception e) {
    			// create event with exception
    			event = new ResponseEvent(e);
    		}    		
    		// process event
    		processResponseEvent(event,new XCAPResourceAdaptorActivityHandle(key));    		
    	}
    }   
    
    protected class AsyncPutDocumentByteArrayIfMatchHandler implements Runnable {


    	private DocumentUriKey key;
    	private String eTag;
    	private String mimetype;
    	private byte[] content;
    	    	
    	public AsyncPutDocumentByteArrayIfMatchHandler(DocumentUriKey key, String eTag, String mimetype, byte[] content) {
			this.key = key;
			this.eTag = eTag;
			this.mimetype = mimetype;
			this.content = content;
		}
    	
    	public void run() {    		
    		ResponseEvent event = null;
    		try {
    			// execute method and get response
    			Response response = client.putDocumentIfMatch(key,eTag,mimetype,content);
    			// create event with response
    			event = new ResponseEvent(response);
    		}
    		catch(Exception e) {
    			// create event with exception
    			event = new ResponseEvent(e);
    		}    		
    		// process event
    		processResponseEvent(event,new XCAPResourceAdaptorActivityHandle(key));    		
    	}
    }
    
    protected class AsyncPutDocumentByteArrayIfNoneMatchHandler implements Runnable {


    	private DocumentUriKey key;
    	private String eTag;
    	private String mimetype;
    	private byte[] content;
    	    	
    	public AsyncPutDocumentByteArrayIfNoneMatchHandler(DocumentUriKey key, String eTag, String mimetype, byte[] content) {
			this.key = key;
			this.eTag = eTag;
			this.mimetype = mimetype;
			this.content = content;
		}
    	
    	public void run() {    		
    		ResponseEvent event = null;
    		try {
    			// execute method and get response
    			Response response = client.putDocumentIfNoneMatch(key,eTag,mimetype,content);
    			// create event with response
    			event = new ResponseEvent(response);
    		}
    		catch(Exception e) {
    			// create event with exception
    			event = new ResponseEvent(e);
    		}    		
    		// process event
    		processResponseEvent(event,new XCAPResourceAdaptorActivityHandle(key));    		
    	}
    }

    // PUT ELEMENT
    
    protected class AsyncPutElementStringHandler implements Runnable {

    	private ElementUriKey key;
     	private String content;
    	    	
    	public AsyncPutElementStringHandler(ElementUriKey key, String content) {
			this.key = key;
			this.content = content;
		}
    	
    	public void run() {    		
    		ResponseEvent event = null;
    		try {
    			// execute method and get response
    			Response response = client.putElement(key,content);
    			// create event with response
    			event = new ResponseEvent(response);
    		}
    		catch(Exception e) {
    			// create event with exception
    			event = new ResponseEvent(e);
    		}    		
    		// process event
    		processResponseEvent(event,new XCAPResourceAdaptorActivityHandle(key));    		
    	}
    }   
    
    protected class AsyncPutElementStringIfMatchHandler implements Runnable {

    	private ElementUriKey key;
    	private String eTag;
    	private String content;
    	    	
    	public AsyncPutElementStringIfMatchHandler(ElementUriKey key, String eTag,String content) {
			this.key = key;
			this.eTag = eTag;
			this.content = content;
		}
    	
    	public void run() {    		
    		ResponseEvent event = null;
    		try {
    			// execute method and get response
    			Response response = client.putElementIfMatch(key,eTag,content);
    			// create event with response
    			event = new ResponseEvent(response);
    		}
    		catch(Exception e) {
    			// create event with exception
    			event = new ResponseEvent(e);
    		}    		
    		// process event
    		processResponseEvent(event,new XCAPResourceAdaptorActivityHandle(key));    		
    	}
    }
    
    protected class AsyncPutElementStringIfNoneMatchHandler implements Runnable {

    	private ElementUriKey key;
    	private String eTag;
      	private String content;
    	    	
    	public AsyncPutElementStringIfNoneMatchHandler(ElementUriKey key, String eTag, String content) {
			this.key = key;
			this.eTag = eTag;
			this.content = content;
		}
    	
    	public void run() {    		
    		ResponseEvent event = null;
    		try {
    			// execute method and get response
    			Response response = client.putElementIfNoneMatch(key,eTag,content);
    			// create event with response
    			event = new ResponseEvent(response);
    		}
    		catch(Exception e) {
    			// create event with exception
    			event = new ResponseEvent(e);
    		}    		
    		// process event
    		processResponseEvent(event,new XCAPResourceAdaptorActivityHandle(key));    		
    	}
    }
    
    protected class AsyncPutElementByteArrayHandler implements Runnable {

    	private ElementUriKey key;
    	private byte[] content;
    	    	
    	public AsyncPutElementByteArrayHandler(ElementUriKey key, byte[] content) {
			this.key = key;
			this.content = content;
		}
    	
    	public void run() {    		
    		ResponseEvent event = null;
    		try {
    			// execute method and get response
    			Response response = client.putElement(key,content);
    			// create event with response
    			event = new ResponseEvent(response);
    		}
    		catch(Exception e) {
    			// create event with exception
    			event = new ResponseEvent(e);
    		}    		
    		// process event
    		processResponseEvent(event,new XCAPResourceAdaptorActivityHandle(key));    		
    	}
    }   
    
    protected class AsyncPutElementByteArrayIfMatchHandler implements Runnable {


    	private ElementUriKey key;
    	private String eTag;
    	private byte[] content;
    	    	
    	public AsyncPutElementByteArrayIfMatchHandler(ElementUriKey key, String eTag, byte[] content) {
			this.key = key;
			this.eTag = eTag;
			this.content = content;
		}
    	
    	public void run() {    		
    		ResponseEvent event = null;
    		try {
    			// execute method and get response
    			Response response = client.putElementIfMatch(key,eTag,content);
    			// create event with response
    			event = new ResponseEvent(response);
    		}
    		catch(Exception e) {
    			// create event with exception
    			event = new ResponseEvent(e);
    		}    		
    		// process event
    		processResponseEvent(event,new XCAPResourceAdaptorActivityHandle(key));    		
    	}
    }
    
    protected class AsyncPutElementByteArrayIfNoneMatchHandler implements Runnable {

    	private ElementUriKey key;
    	private String eTag;
    	private byte[] content;
    	    	
    	public AsyncPutElementByteArrayIfNoneMatchHandler(ElementUriKey key, String eTag, byte[] content) {
			this.key = key;
			this.eTag = eTag;
			this.content = content;
		}
    	
    	public void run() {    		
    		ResponseEvent event = null;
    		try {
    			// execute method and get response
    			Response response = client.putElementIfNoneMatch(key,eTag,content);
    			// create event with response
    			event = new ResponseEvent(response);
    		}
    		catch(Exception e) {
    			// create event with exception
    			event = new ResponseEvent(e);
    		}    		
    		// process event
    		processResponseEvent(event,new XCAPResourceAdaptorActivityHandle(key));    		
    	}
    }

    // PUT ATTRIBUTE
    
    protected class AsyncPutAttributeStringHandler implements Runnable {

    	private AttributeUriKey key;
     	private String content;
    	    	
    	public AsyncPutAttributeStringHandler(AttributeUriKey key, String content) {
			this.key = key;
			this.content = content;
		}
    	
    	public void run() {    		
    		ResponseEvent event = null;
    		try {
    			// execute method and get response
    			Response response = client.putAttribute(key,content);
    			// create event with response
    			event = new ResponseEvent(response);
    		}
    		catch(Exception e) {
    			// create event with exception
    			event = new ResponseEvent(e);
    		}    		
    		// process event
    		processResponseEvent(event,new XCAPResourceAdaptorActivityHandle(key));    		
    	}
    }   
    
    protected class AsyncPutAttributeStringIfMatchHandler implements Runnable {

    	private AttributeUriKey key;
    	private String eTag;
    	private String content;
    	    	
    	public AsyncPutAttributeStringIfMatchHandler(AttributeUriKey key, String eTag,String content) {
			this.key = key;
			this.eTag = eTag;
			this.content = content;
		}
    	
    	public void run() {    		
    		ResponseEvent event = null;
    		try {
    			// execute method and get response
    			Response response = client.putAttributeIfMatch(key,eTag,content);
    			// create event with response
    			event = new ResponseEvent(response);
    		}
    		catch(Exception e) {
    			// create event with exception
    			event = new ResponseEvent(e);
    		}    		
    		// process event
    		processResponseEvent(event,new XCAPResourceAdaptorActivityHandle(key));    		
    	}
    }
    
    protected class AsyncPutAttributeStringIfNoneMatchHandler implements Runnable {

    	private AttributeUriKey key;
    	private String eTag;
      	private String content;
    	    	
    	public AsyncPutAttributeStringIfNoneMatchHandler(AttributeUriKey key, String eTag, String content) {
			this.key = key;
			this.eTag = eTag;
			this.content = content;
		}
    	
    	public void run() {    		
    		ResponseEvent event = null;
    		try {
    			// execute method and get response
    			Response response = client.putAttributeIfNoneMatch(key,eTag,content);
    			// create event with response
    			event = new ResponseEvent(response);
    		}
    		catch(Exception e) {
    			// create event with exception
    			event = new ResponseEvent(e);
    		}    		
    		// process event
    		processResponseEvent(event,new XCAPResourceAdaptorActivityHandle(key));    		
    	}
    }
    
    protected class AsyncPutAttributeByteArrayHandler implements Runnable {

    	private AttributeUriKey key;
    	private byte[] content;
    	    	
    	public AsyncPutAttributeByteArrayHandler(AttributeUriKey key, byte[] content) {
			this.key = key;
			this.content = content;
		}
    	
    	public void run() {    		
    		ResponseEvent event = null;
    		try {
    			// execute method and get response
    			Response response = client.putAttribute(key,content);
    			// create event with response
    			event = new ResponseEvent(response);
    		}
    		catch(Exception e) {
    			// create event with exception
    			event = new ResponseEvent(e);
    		}    		
    		// process event
    		processResponseEvent(event,new XCAPResourceAdaptorActivityHandle(key));    		
    	}
    }   
    
    protected class AsyncPutAttributeByteArrayIfMatchHandler implements Runnable {


    	private AttributeUriKey key;
    	private String eTag;
    	private byte[] content;
    	    	
    	public AsyncPutAttributeByteArrayIfMatchHandler(AttributeUriKey key, String eTag, byte[] content) {
			this.key = key;
			this.eTag = eTag;
			this.content = content;
		}
    	
    	public void run() {    		
    		ResponseEvent event = null;
    		try {
    			// execute method and get response
    			Response response = client.putAttributeIfMatch(key,eTag,content);
    			// create event with response
    			event = new ResponseEvent(response);
    		}
    		catch(Exception e) {
    			// create event with exception
    			event = new ResponseEvent(e);
    		}    		
    		// process event
    		processResponseEvent(event,new XCAPResourceAdaptorActivityHandle(key));    		
    	}
    }
    
    protected class AsyncPutAttributeByteArrayIfNoneMatchHandler implements Runnable {

    	private AttributeUriKey key;
    	private String eTag;
    	private byte[] content;
    	    	
    	public AsyncPutAttributeByteArrayIfNoneMatchHandler(AttributeUriKey key, String eTag, byte[] content) {
			this.key = key;
			this.eTag = eTag;
			this.content = content;
		}
    	
    	public void run() {    		
    		ResponseEvent event = null;
    		try {
    			// execute method and get response
    			Response response = client.putAttributeIfNoneMatch(key,eTag,content);
    			// create event with response
    			event = new ResponseEvent(response);
    		}
    		catch(Exception e) {
    			// create event with exception
    			event = new ResponseEvent(e);
    		}    		
    		// process event
    		processResponseEvent(event,new XCAPResourceAdaptorActivityHandle(key));    		
    	}
    }    
    
    // DELETE
    
    protected class AsyncDeleteDocumentHandler implements Runnable {


    	private DocumentUriKey key;
    	
    	    	
    	public AsyncDeleteDocumentHandler(DocumentUriKey key) {
			this.key = key;
		}
    	
    	public void run() {    		
    		ResponseEvent event = null;
    		try {
    			// execute method and get response
    			Response response = client.deleteDocument(key);
    			// create event with response
    			event = new ResponseEvent(response);
    		}
    		catch(Exception e) {
    			// create event with exception
    			event = new ResponseEvent(e);
    		}    		
    		// process event
    		processResponseEvent(event,new XCAPResourceAdaptorActivityHandle(key));    		
    	}
    }   
    
    protected class AsyncDeleteDocumentIfMatchHandler implements Runnable {


    	private DocumentUriKey key;
    	private String eTag;
    	    	
    	public AsyncDeleteDocumentIfMatchHandler(DocumentUriKey key, String eTag) {
			this.key = key;
			this.eTag = eTag;
		}
    	
    	public void run() {    		
    		ResponseEvent event = null;
    		try {
    			// execute method and get response
    			Response response = client.deleteDocumentIfMatch(key,eTag);
    			// create event with response
    			event = new ResponseEvent(response);
    		}
    		catch(Exception e) {
    			// create event with exception
    			event = new ResponseEvent(e);
    		}    		
    		// process event
    		processResponseEvent(event,new XCAPResourceAdaptorActivityHandle(key));    		
    	}
    }
    
    protected class AsyncDeleteDocumentIfNoneMatchHandler implements Runnable {

    	private DocumentUriKey key;
    	private String eTag;
    	    	
    	public AsyncDeleteDocumentIfNoneMatchHandler(DocumentUriKey key, String eTag) {
			this.key = key;
			this.eTag = eTag;
		}
    	
		public void run() {
			ResponseEvent event = null;
    		try {
    			// execute method and get response
    			Response response = client.deleteDocumentIfNoneMatch(key,eTag);
    			// create event with response
    			event = new ResponseEvent(response);
    		}
    		catch(Exception e) {
    			// create event with exception
    			event = new ResponseEvent(e);
    		}    		
    		// process event
    		processResponseEvent(event,new XCAPResourceAdaptorActivityHandle(key));			
		}
    }    
    
    // DELETE ELEMENT
    
     protected class AsyncDeleteElementHandler implements Runnable {


    	private ElementUriKey key;
    	
    	    	
    	public AsyncDeleteElementHandler(ElementUriKey key) {
			this.key = key;
		}
    	
    	public void run() {    		
    		ResponseEvent event = null;
    		try {
    			// execute method and get response
    			Response response = client.deleteElement(key);
    			// create event with response
    			event = new ResponseEvent(response);
    		}
    		catch(Exception e) {
    			// create event with exception
    			event = new ResponseEvent(e);
    		}    		
    		// process event
    		processResponseEvent(event,new XCAPResourceAdaptorActivityHandle(key));    		
    	}
    }   
    
    protected class AsyncDeleteElementIfMatchHandler implements Runnable {


    	private ElementUriKey key;
    	private String eTag;
    	    	
    	public AsyncDeleteElementIfMatchHandler(ElementUriKey key, String eTag) {
			this.key = key;
			this.eTag = eTag;
		}
    	
    	public void run() {    		
    		ResponseEvent event = null;
    		try {
    			// execute method and get response
    			Response response = client.deleteElementIfMatch(key,eTag);
    			// create event with response
    			event = new ResponseEvent(response);
    		}
    		catch(Exception e) {
    			// create event with exception
    			event = new ResponseEvent(e);
    		}    		
    		// process event
    		processResponseEvent(event,new XCAPResourceAdaptorActivityHandle(key));    		
    	}
    }
    
    protected class AsyncDeleteElementIfNoneMatchHandler implements Runnable {

    	private ElementUriKey key;
    	private String eTag;
    	    	
    	public AsyncDeleteElementIfNoneMatchHandler(ElementUriKey key, String eTag) {
			this.key = key;
			this.eTag = eTag;
		}
    	
		public void run() {
			ResponseEvent event = null;
    		try {
    			// execute method and get response
    			Response response = client.deleteElementIfNoneMatch(key,eTag);
    			// create event with response
    			event = new ResponseEvent(response);
    		}
    		catch(Exception e) {
    			// create event with exception
    			event = new ResponseEvent(e);
    		}    		
    		// process event
    		processResponseEvent(event,new XCAPResourceAdaptorActivityHandle(key));			
		}
    } 
    
    // DELETE ATTRIBUTE
    
    protected class AsyncDeleteAttributeHandler implements Runnable {


    	private AttributeUriKey key;
    	
    	    	
    	public AsyncDeleteAttributeHandler(AttributeUriKey key) {
			this.key = key;
		}
    	
    	public void run() {    		
    		ResponseEvent event = null;
    		try {
    			// execute method and get response
    			Response response = client.deleteAttribute(key);
    			// create event with response
    			event = new ResponseEvent(response);
    		}
    		catch(Exception e) {
    			// create event with exception
    			event = new ResponseEvent(e);
    		}    		
    		// process event
    		processResponseEvent(event,new XCAPResourceAdaptorActivityHandle(key));    		
    	}
    }   
    
    protected class AsyncDeleteAttributeIfMatchHandler implements Runnable {


    	private AttributeUriKey key;
    	private String eTag;
    	    	
    	public AsyncDeleteAttributeIfMatchHandler(AttributeUriKey key, String eTag) {
			this.key = key;
			this.eTag = eTag;
		}
    	
    	public void run() {    		
    		ResponseEvent event = null;
    		try {
    			// execute method and get response
    			Response response = client.deleteAttributeIfMatch(key,eTag);
    			// create event with response
    			event = new ResponseEvent(response);
    		}
    		catch(Exception e) {
    			// create event with exception
    			event = new ResponseEvent(e);
    		}    		
    		// process event
    		processResponseEvent(event,new XCAPResourceAdaptorActivityHandle(key));    		
    	}
    }
    
    protected class AsyncDeleteAttributeIfNoneMatchHandler implements Runnable {

    	private AttributeUriKey key;
    	private String eTag;
    	    	
    	public AsyncDeleteAttributeIfNoneMatchHandler(AttributeUriKey key, String eTag) {
			this.key = key;
			this.eTag = eTag;
		}
    	
		public void run() {
			ResponseEvent event = null;
    		try {
    			// execute method and get response
    			Response response = client.deleteAttributeIfNoneMatch(key,eTag);
    			// create event with response
    			event = new ResponseEvent(response);
    		}
    		catch(Exception e) {
    			// create event with exception
    			event = new ResponseEvent(e);
    		}    		
    		// process event
    		processResponseEvent(event,new XCAPResourceAdaptorActivityHandle(key));			
		}
    }    
}

