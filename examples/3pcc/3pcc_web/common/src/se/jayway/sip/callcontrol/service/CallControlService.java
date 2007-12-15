package se.jayway.sip.callcontrol.service;

import se.jayway.sip.callcontrol.exception.ConfigurationException;
import se.jayway.sip.callcontrol.exception.GeneralException;
import se.jayway.sip.callcontrol.exception.flow.SessionUnavailableException;
import se.jayway.sip.callcontrol.exception.slee.SleeConnectionException;
import se.jayway.sip.callcontrol.exception.slee.SleeEventException;

/**
 * Interface for callcontrol functionality. <br>
 * Usage: <br>
 * <code>
 * ObjectFactory objectFactory = ObjectFactory.getInstance(); 
 * CallControlService ccService = objectFactory.getCallControlService();
 * UserService userService = objectFactory.getUserService(); 
 * User user = userService.findByUsername( getRemoteUser() ); 
 * String callee = .... 
 * String caller = (String)(user.getSipAddresses.iterator().next()); 
 * String callId = ccService.makeCall(caller, callee);
 * </code>
 * 
 * @author Niklas
 * @see ETSI ES 202 391-2 V1.1.1
 */
public interface CallControlService {
	
	/**
	 * Requests to set-up a voice call between two addresses, caller and callee,
	 * provided that the invoking application is allowed to connect them. By
	 * invoking this operation the application may monitor the status of the
	 * requested call. The returned parameter, callId, can be used to identify
	 * the call.
	 * 
	 * @param caller
	 *            Sip address of the caller.
	 * @param callee
	 *            Sip address of the callee.
	 * @return A callId String which is used in the methods getCallInformation,
	 *         cancelCallRequest and endCall.
	 * @throws SleeEventException
	 *             If an error occurs when creating or sending a SLEE event.
	 * @throws SleeConnectionException
	 *             If an error occurs when creating a connection to the SLEE.
	 */
	public String makeCall(String caller, String callee)
			throws SleeEventException, SleeConnectionException, GeneralException, ConfigurationException;
			

	/**
	 * Retrieves the current status, CallInformation, of the call identified by
	 * callId. This method can be invoked multiple times by the application even
	 * if the call has already ended.
	 * 
	 * @param callId
	 *            String used to identify the call in the methods
	 *            getCallInformation, cancelCallRequest and endCall
	 * @return The current status of the call identified by callId
	 * @throws SessionUnavailableException
	 *             If no session matching the supplied callId is found
	 * 
	 */
	public String getCallInformation(String callId) throws SessionUnavailableException, GeneralException;
	
	/**
	 * Terminates the call associated with the callId.
	 * 
	 * @param callId
	 *            String used to identify the call in the methods
	 *            getCallInformation, cancelCallRequest and endCall
	 * @throws SessionUnavailableException
	 *             If no session matching the supplied callId is found 
	 * @throws SleeEventException
	 *             If an error occurs when creating or sending a SLEE event.
	 * @throws SleeConnectionException
	 *             If an error occurs when creating a connection to the SLEE.
	 */
	public void endCall(String callId) throws SessionUnavailableException, SleeEventException, SleeConnectionException,  GeneralException;
	
	/**
	 * Cancel the request to establish the call associated with callId. 
	 * 
	 * @param callId
	 *            String used to identify the call in the methods
	 *            getCallInformation, cancelCallRequest and endCall
	 * @throws SessionUnavailableException
	 *             If no session matching the supplied callId is found 
	 * @throws SleeEventException
	 *             If an error occurs when creating or sending a SLEE event.
	 * @throws SleeConnectionException
	 *             If an error occurs when creating a connection to the SLEE.
	 */
	public void cancelCallRequest(String callId) throws SessionUnavailableException, SleeEventException, SleeConnectionException, GeneralException;
	
	/**
	 * Initializes this service. This may not be necessary for all
	 * implementations. Implementations that do not need initialization should
	 * return silently.
	 *
	 */
	public void init(); //throws CommunicationException;
}
