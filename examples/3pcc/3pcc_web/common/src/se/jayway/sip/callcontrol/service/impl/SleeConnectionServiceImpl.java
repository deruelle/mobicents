package se.jayway.sip.callcontrol.service.impl;

import java.util.HashMap;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.resource.ResourceException;
import javax.slee.EventTypeID;
import javax.slee.UnrecognizedActivityException;
import javax.slee.UnrecognizedEventException;
import javax.slee.connection.SleeConnection;
import javax.slee.connection.SleeConnectionFactory;

import org.apache.log4j.Logger;

import se.jayway.sip.callcontrol.exception.flow.SessionUnavailableException;
import se.jayway.sip.callcontrol.exception.slee.SleeConnectionException;
import se.jayway.sip.callcontrol.exception.slee.SleeEventException;
import se.jayway.sip.callcontrol.service.CallControlService;
import se.jayway.sip.callcontrol.service.ExceptionErrorDefinitions;
import se.jayway.sip.slee.event.CancellationEvent;
import se.jayway.sip.slee.event.TerminationEvent;
import se.jayway.sip.slee.event.ThirdPCCTriggerEvent;
import se.jayway.sip.util.DefaultStateCallback;
import se.jayway.sip.util.StateCallback;


public class SleeConnectionServiceImpl implements CallControlService, ExceptionErrorDefinitions {
	private Logger log = Logger.getLogger(SleeConnectionServiceImpl.class);
	protected final static String CACHE_JNDI_NAME = "java:jayway"; 
	
	private SleeConnectionFactory factory;
    private HashMap callbackMap;
	private static SleeConnectionServiceImpl instance = null;
	
	
	/**
	 * Common constructor
	 * @throws SleeConnectionException
	 * 		   If SleeConnectionFactory cannot be found in JNDI. 
	 */
	public SleeConnectionServiceImpl() throws SleeConnectionException  {
		super();
		initContext();
		callbackMap = new HashMap();
	}
	
	public static SleeConnectionServiceImpl getInstance() throws SleeConnectionException {
		if(instance==null){
			instance = new SleeConnectionServiceImpl();
		}
		return instance;
	}
	
	
	private void initContext() throws SleeConnectionException {
		InitialContext ctx = null;
		try {
			// Create the cache used for the session association
//			 callbackMap = CacheFactory.getInstance().getCache();
			
			ctx = new InitialContext();
			factory = (SleeConnectionFactory) ctx.lookup("java:MobicentsConnectionFactory");
		} catch ( NamingException e ) {
			throw new SleeConnectionException(ERROR_SLEE_FACTORY_JNDI_LOOKUP);
		} 
	}
	
	protected void fireEvent(Object event, String eventName, String eventVendor, String eventVersion) throws SleeConnectionException, SleeEventException {
		SleeConnection sleeConnection = null;
		// Now go on and establish the connection
		try {
			sleeConnection = factory.getConnection();
			log.debug("Got SleeConnection : " + sleeConnection);
		} catch (ResourceException e) {
			throw new SleeConnectionException(ERROR_SLEE_CONNECT);
		}
		
		try {
			EventTypeID eventTypeID = sleeConnection.getEventTypeID(eventName, eventVendor, eventVersion);
			
			if(log.isDebugEnabled()) {
				log.debug("Got EventTypeID : " + eventTypeID);
				log.debug("Firing event");
			}
			
			try {
				sleeConnection.fireEvent(event, eventTypeID, null, null);
			} catch (UnrecognizedActivityException e) {
				throw new SleeEventException(ERROR_SLEE_ACTIVITY_NOT_RECOGNIZED + eventName + " " + eventVendor + " " + eventVersion);
			}
			
			if(log.isDebugEnabled()) {
				log.debug("Fired event successfully");
			}
			
		} catch (UnrecognizedEventException e) {
			throw new SleeEventException(ERROR_SLEE_EVENT_TYPE_NOT_RECOGNIZED + eventName + " " + eventVendor + " " + eventVersion);
		} catch (ResourceException e) {
			throw new SleeEventException(ERROR_SLEE_FIREEVENT + eventName + " " + eventVendor + " " + eventVersion);
		} finally {			
			try {
				sleeConnection.close();
			} catch (ResourceException e) {
				throw new SleeConnectionException(ERROR_SLEE_DISCONNECT);
			}
		}
	}

	/*
	 *  (non-Javadoc)
	 * @see se.jayway.sip.callcontrol.service.CallControlService#init()
	 */
	public void init() { //throws CommunicationException {
		return;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see se.jayway.sip.callcontrol.service.CallControlService#getCallInformation(java.lang.String)
	 */
	public String getCallInformation(String guid) throws SessionUnavailableException {
		StateCallback callback = getStateCallback(guid);
		
		if(log.isDebugEnabled()) {
			log.debug("Callback = " + callback);
		}
		
		String status = callback.getSessionState();
		
		if ( status == null ) {
			// This is a hack to make the GUI show the feedback for the
			// initial state if the first callInformation request is made before
			// status is not null
			status = "InitialState";
		}
		return status;
	}

	/*
	 *  (non-Javadoc)
	 * @see se.jayway.sip.callcontrol.service.CallControlService#makeCall(java.lang.String, java.lang.String)
	 */
	public String makeCall(String caller, String callee) throws SleeEventException, SleeConnectionException {
		String guid = null;
		// Create the event
		ThirdPCCTriggerEvent event = new ThirdPCCTriggerEvent(caller, callee);
		//Retrieve GUID
		guid = event.getGUID();
		StateCallback callback = new DefaultStateCallback();
		event.setStateCallback(callback);
	
		if(log.isDebugEnabled()) {
			log.debug("Putting[guid="+guid+", callback="+callback);
		}
		callbackMap.put(guid, callback);

		final String eventName = "se.jayway.sip.slee.event.ThirdPCCTriggerEvent";
		final String eventVendor = "Jayway";
		final String eventVersion = "0.1";
		
		// Fire off the event
		fireEvent(event, eventName, eventVendor, eventVersion);
		
		return guid;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see se.jayway.sip.callcontrol.service.CallControlService#endCall(java.lang.String)
	 */
	public void endCall(String callId) throws SessionUnavailableException, SleeConnectionException, SleeEventException  {
		
		// Create the event
		TerminationEvent event = new TerminationEvent(callId);
		
		final String eventName = "se.jayway.sip.slee.event.TerminationEvent";
		final String eventVendor = "Jayway";
		final String eventVersion = "0.1";
		
		// If session with call identifier callId does not exist
		checkSessionAvailability(callId);
		
		// Fire off the event 
		fireEvent(event, eventName, eventVendor, eventVersion);
		
	}

	/*
	 *  (non-Javadoc)
	 * @see se.jayway.sip.callcontrol.service.CallControlService#cancelCallRequest(java.lang.String)
	 */
	public void cancelCallRequest(String callId) throws SessionUnavailableException, SleeEventException, SleeConnectionException {
//		 Create the event
		CancellationEvent event = new CancellationEvent(callId);
		
		final String eventName = "se.jayway.sip.slee.event.CancellationEvent";
		final String eventVendor = "Jayway";
		final String eventVersion = "0.1";
		
		// If session with call identifier callId does not exist
		checkSessionAvailability(callId);
				
		// Fire off the event
		fireEvent(event, eventName, eventVendor, eventVersion);		
	}
	
	/**
	 * Returns the StateCallback match the supplied callId if found
	 * 
	 * @param callId
	 *            String identifying the call
	 * @return The StateCallback match the supplied callId if found
	 * @throws SessionUnavailableException
	 *             If no matching StateCallback is found for the supplied callId
	 */	
	public StateCallback getStateCallback(String guid) throws SessionUnavailableException {
		StateCallback callback = (StateCallback) callbackMap.get(guid);
		
		if(log.isDebugEnabled()) {
			log.debug("callbackMap.size = " + callbackMap.size());
			log.debug("getCallInformation guid  = " + guid);
		}
		
		//If no session matching the guid was found
		if(callback == null) {
			throw new SessionUnavailableException(ERROR_SESSION_UNAVAILABLE + guid + " exists.");
		}
		return callback;
	}

	
	/**
	 * Check if the SIP session already exists. This is determined by the
	 * SessionUnavailableException being thrown or not.
	 * 
	 * @param callId
	 *            String identifying the call
	 * @throws SessionUnavailableException
	 *             If no session matching the supplied callId is found
	 */
	protected void checkSessionAvailability(String callId) throws SessionUnavailableException {
		getStateCallback(callId);
	}
}