/*
 * Mobicents: The Open Source SLEE Platform      
 *
 * Copyright 2003-2005, CocoonHive, LLC., 
 * and individual contributors as indicated
 * by the @authors tag. See the copyright.txt 
 * in the distribution for a full listing of   
 * individual contributors.
 *
 * This is free software; you can redistribute it
 * and/or modify it under the terms of the 
 * GNU Lesser General Public License as
 * published by the Free Software Foundation; 
 * either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that 
 * it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR 
 * PURPOSE. See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the 
 * GNU Lesser General Public
 * License along with this software; 
 * if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, 
 * Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site:
 * http://www.fsf.org.
 */

package se.jayway.sip.callcontrol.server.impl;

import java.rmi.RemoteException;

import org.apache.axis.types.URI;
import org.apache.log4j.Logger;
import org.csapi.www.schema.parlayx.common.v2_0.CallInformation;
import org.csapi.www.schema.parlayx.common.v2_0.CallStatus;
import org.csapi.www.schema.parlayx.common.v2_0.CallStatusWrapper;
import org.csapi.www.schema.parlayx.common.v2_0.ChargingInformation;
import org.csapi.www.schema.parlayx.common.v2_0.PolicyException;
import org.csapi.www.schema.parlayx.common.v2_0.ServiceException;
import org.csapi.www.schema.parlayx.common.v2_0.ThirdPartyCall;

import se.jayway.sip.callcontrol.exception.flow.SessionUnavailableException;
import se.jayway.sip.callcontrol.exception.slee.SleeConnectionException;
import se.jayway.sip.callcontrol.exception.slee.SleeException;
import se.jayway.sip.callcontrol.service.ExceptionErrorDefinitions;
import se.jayway.sip.callcontrol.service.impl.SleeConnectionServiceImpl;


public class ThirdPartyCallImpl implements ThirdPartyCall, ExceptionErrorDefinitions {

	static Logger log = Logger.getLogger(ThirdPartyCallImpl.class);

	private SleeConnectionServiceImpl callControl;

	/**
	 * The constructor
	 * 
	 * @throws ServiceException
	 * 		   If SleeConnectionFactory lookup in JNDI fails or
	 * 		   error in configuration file 
	 */
	public ThirdPartyCallImpl() throws RemoteException, ServiceException {
		try {
			callControl = SleeConnectionServiceImpl.getInstance();
		} catch (SleeConnectionException e) {
			throw new ServiceException(null, e.getMessage(), null);
		} catch (ClassCastException e) {
			throw new ServiceException(null, ERROR_CONFIGURATION_CLASS_CAST, null);
		} 
	}

	/**
	 * Cancel the request to establish the call associated with callId.
	 * 
	 * @param callId
	 *            String used to identify the call in the methods
	 *            getCallInformation, cancelCallRequest and endCall *
	 * @throws ServiceException
	 *             If a service is not able to process a request, and retrying
	 *             the request with the same information will also result in a
	 *             failure, and the issue is not related to a service policy
	 *             issue, then the service will issue a fault using the
	 *             ServiceException fault message. Examples of service
	 *             exceptions include invalid input, lack of availability of a
	 *             required resource or a processing error.
	 * @throws PolicyException
	 *             If a service is not able to complete because the request
	 *             fails to meet a policy criteria, then the service will issue
	 *             a fault using the PolicyException fault message. To clarify
	 *             how a Policy Exception differs from a Service Exception,
	 *             consider that all the input to an operation may be valid as
	 *             meeting the required input for the operation (thus no Service
	 *             Exception), but using that input in the execution of the
	 *             service may result in conditions that require the service not
	 *             to complete. Examples of policy exceptions include privacy
	 *             violations, requests not permitted under a governing service
	 *             agreement or input content not acceptable to the service
	 *             provider.
	 * @throws RemoteException
	 *             A RemoteException is the common superclass for a number of
	 *             communication-related exceptions that may occur during the
	 *             execution of a remote method call.
	 */
	public void cancelCallRequest(String guid) throws RemoteException,
			PolicyException, ServiceException {
		if (log.isDebugEnabled()) {
			log.debug("Cancelling call with id: " + guid);
		}
		try {
			callControl.cancelCallRequest(guid);
		} catch (SessionUnavailableException e) {
			throw new ServiceException(null, e.getMessage(), null);
		} catch (SleeException e) {
			throw new ServiceException(null, e.getMessage(), null);
		} 
	}

	/**
	 * Retrieves the current status, CallInformation, of the call identified by
	 * callId. This method can be invoked multiple times by the application even
	 * if the call has already ended.
	 * 
	 * @param callId
	 *            String used to identify the call in the methods
	 *            getCallInformation, cancelCallRequest and endCall
	 * @return The current status of the call identified by callId
	 * @throws ServiceException
	 *             If a service is not able to process a request, and retrying
	 *             the request with the same information will also result in a
	 *             failure, and the issue is not related to a service policy
	 *             issue, then the service will issue a fault using the
	 *             ServiceException fault message. Examples of service
	 *             exceptions include invalid input, lack of availability of a
	 *             required resource or a processing error.
	 * @throws PolicyException
	 *             If a service is not able to complete because the request
	 *             fails to meet a policy criteria, then the service will issue
	 *             a fault using the PolicyException fault message. To clarify
	 *             how a Policy Exception differs from a Service Exception,
	 *             consider that all the input to an operation may be valid as
	 *             meeting the required input for the operation (thus no Service
	 *             Exception), but using that input in the execution of the
	 *             service may result in conditions that require the service not
	 *             to complete. Examples of policy exceptions include privacy
	 *             violations, requests not permitted under a governing service
	 *             agreement or input content not acceptable to the service
	 *             provider.
	 * @throws RemoteException
	 *             A RemoteException is the common superclass for a number of
	 *             communication-related exceptions that may occur during the
	 *             execution of a remote method call.
	 * @see ETSI ES 202 391-2 V1.1.1, chapter 8.1.2
	 * 
	 */
	public CallInformation getCallInformation(String guid)
			throws PolicyException, ServiceException, RemoteException {
		if (log.isDebugEnabled()) {
			log.debug("Entering getCallInformation");
		}

		String status = null;
		try {
			status = callControl.getCallInformation(guid);
		} catch (SessionUnavailableException e) {
			throw new ServiceException(null, e.getMessage(), null);
		} 
		CallInformation callInformation = new CallInformation();
		callInformation.setCallStatus(CallStatusWrapper
				.createCallStatus(status));
		callInformation.setDuration(9); // May not be null
		callInformation.setStartTime(null); // May be null
		// callInformation.setTerminationCause(CallTerminationCauseWrapper.createCallTerminationCause(null));
		callInformation.setTerminationCause(null); // May be null

		if (log.isDebugEnabled()) {
			log.debug("callInformation = " + callInformation);
		}

		CallStatus callStatus = callInformation.getCallStatus();
		if (callStatus != null) {
			if (log.isDebugEnabled()) {
				log.debug("callInformation status = " + callStatus.getValue());
			}
		} else {
			if (log.isDebugEnabled()) {
				log.debug("callInformation status = null");
			}
		}
		return callInformation;
	}

	/**
	 * Terminates the call associated with the callId.
	 * 
	 * @param callId
	 *            String used to identify the call in the methods
	 *            getCallInformation, cancelCallRequest and endCall
	 * @throws ServiceException
	 *             If a service is not able to process a request, and retrying
	 *             the request with the same information will also result in a
	 *             failure, and the issue is not related to a service policy
	 *             issue, then the service will issue a fault using the
	 *             ServiceException fault message. Examples of service
	 *             exceptions include invalid input, lack of availability of a
	 *             required resource or a processing error.
	 * @throws PolicyException
	 *             If a service is not able to complete because the request
	 *             fails to meet a policy criteria, then the service will issue
	 *             a fault using the PolicyException fault message. To clarify
	 *             how a Policy Exception differs from a Service Exception,
	 *             consider that all the input to an operation may be valid as
	 *             meeting the required input for the operation (thus no Service
	 *             Exception), but using that input in the execution of the
	 *             service may result in conditions that require the service not
	 *             to complete. Examples of policy exceptions include privacy
	 *             violations, requests not permitted under a governing service
	 *             agreement or input content not acceptable to the service
	 *             provider.
	 * @throws RemoteException
	 *             A RemoteException is the common superclass for a number of
	 *             communication-related exceptions that may occur during the
	 *             execution of a remote method call.
	 */
	public void endCall(String guid) throws RemoteException, PolicyException,
			ServiceException {
		if (log.isDebugEnabled()) {
			log.debug("Ending call with id: " + guid);
		}

		try {
			callControl.endCall(guid);
		} catch (SessionUnavailableException e) {
			throw new ServiceException(null, e.getMessage(), null);
		} catch (SleeException e) {
			throw new ServiceException(null, e.getMessage(), null);
		} 
	}

	public void destroy() {
		if (log.isDebugEnabled()) {
			log.debug("destroy()");
		}
	}

	public void init() {
		if (log.isDebugEnabled()) {
			log.debug("init()");
		}
	}

	/**
	 * Requests to set-up a voice call between two addresses, caller and callee,
	 * provided that the invoking application is allowed to connect them. By
	 * invoking this operation the application may monitor the status of the
	 * requested call. The returned parameter, callId, can be used to identify
	 * the call.
	 * 
	 * @param caller
	 *            Sip address of the caller as a URI
	 * @param callee
	 *            Sip address of the callee as a URI
	 * @return A callId string which may be used in the methods
	 *         getCallInformation, cancelCallRequest and endCall
	 * @throws ServiceException
	 *             If a service is not able to process a request, and retrying
	 *             the request with the same information will also result in a
	 *             failure, and the issue is not related to a service policy
	 *             issue, then the service will issue a fault using the
	 *             ServiceException fault message. Examples of service
	 *             exceptions include invalid input, lack of availability of a
	 *             required resource or a processing error.
	 * @throws PolicyException
	 *             If a service is not able to complete because the request
	 *             fails to meet a policy criteria, then the service will issue
	 *             a fault using the PolicyException fault message. To clarify
	 *             how a Policy Exception differs from a Service Exception,
	 *             consider that all the input to an operation may be valid as
	 *             meeting the required input for the operation (thus no Service
	 *             Exception), but using that input in the execution of the
	 *             service may result in conditions that require the service not
	 *             to complete. Examples of policy exceptions include privacy
	 *             violations, requests not permitted under a governing service
	 *             agreement or input content not acceptable to the service
	 *             provider.
	 * @throws RemoteException
	 *             A RemoteException is the common superclass for a number of
	 *             communication-related exceptions that may occur during the
	 *             execution of a remote method call.
	 * @see ETSI ES 202 391-2 V1.1.1, chapter 8.1.1
	 */
	public String makeCall(URI callingParty, URI calledParty,
			ChargingInformation charging) throws RemoteException,
			PolicyException, ServiceException {
		if (log.isDebugEnabled()) {
			log.debug("makeCall[caller = " + callingParty.toString()
					+ ", callee = " + calledParty.toString() + "]");
		}
		String guid = null;

		try {
			guid = callControl.makeCall(callingParty.toString(), calledParty
					.toString());
		} catch (SleeException e) {
			throw new ServiceException(null, e.getMessage(), null);
		}

		if (log.isDebugEnabled()) {
			log.debug("makeCall id: " + guid);
		}
		return guid;
	}

}
