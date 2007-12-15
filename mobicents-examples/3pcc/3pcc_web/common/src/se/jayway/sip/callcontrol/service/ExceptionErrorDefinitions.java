package se.jayway.sip.callcontrol.service;

public interface ExceptionErrorDefinitions {

	/*
	 * Error defines
	 * 
	 * NOTE: Error defines ending with a ":" may be concatenated with more information. 
	 * Therefore always use "startsWith" for these messages if doing String compare when
	 * mapping Exceptions.
	 *  
	 */
	final String ERROR_SLEE_CONNECT 	= "Failed to obtain SleeConnection from factory!";
	final String ERROR_SLEE_FACTORY_JNDI_LOOKUP	= "Failed to lookup SleeConnectionFactory in JNDI!";
	final String ERROR_SLEE_FIREEVENT 	= "Failed to fire event: ";
	final String ERROR_SLEE_DISCONNECT	= "Unable to close SleeConnection!";
	final String ERROR_SLEE_ACTIVITY_NOT_RECOGNIZED = "Activity not recognized when firing event: ";
	final String ERROR_SLEE_EVENT_TYPE_NOT_RECOGNIZED = "Event type unrecognized: ";
	final String ERROR_SESSION_UNAVAILABLE = "No session associated with callId: ";
	final String ERROR_INTERNAL_NO_EXCEPTION_DESC	= "Internal error: The exception contains no message description!";
	final String ERROR_INTERNAL_INCORRECT_EXCEPTION_DESC	= "Internal error: The exception does not match any predefined message. The message was: ";
	
	final String ERROR_CONFIGURATION_CLASS_CAST = "Error in configuration file: Have you defined an incorrect call service?";
	final String ERROR_CONFIGURATION_INVALID_SERVICE = "Invalid callService: ";
	
	
	
}
