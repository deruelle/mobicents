 /*
  * Mobicents: The Open Source SLEE Platform      
  *
  * Copyright 2003-2006, CocoonHive, LLC., 
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

package se.jayway.sip.callcontrol.client.service.impl;

import java.rmi.RemoteException;

import org.jboss.axis.types.URI;
import org.jboss.axis.types.URI.MalformedURIException;
import org.csapi.www.schema.parlayx.common.v2_0.CallInformation;
import org.csapi.www.schema.parlayx.common.v2_0.ChargingInformation;
import org.csapi.www.schema.parlayx.common.v2_0.PolicyException;
import org.csapi.www.schema.parlayx.common.v2_0.ServiceException;
import org.csapi.www.schema.parlayx.common.v2_0.ThirdPartyCallImpl;
import org.csapi.www.schema.parlayx.common.v2_0.ThirdPartyCallImplServiceLocator;

import se.jayway.sip.callcontrol.exception.ConfigurationException;
import se.jayway.sip.callcontrol.exception.GeneralException;
import se.jayway.sip.callcontrol.exception.flow.SessionUnavailableException;
import se.jayway.sip.callcontrol.exception.slee.SleeConnectionException;
import se.jayway.sip.callcontrol.exception.slee.SleeEventException;
import se.jayway.sip.callcontrol.service.CallControlService;
import se.jayway.sip.callcontrol.service.ExceptionErrorDefinitions;


public class ParlayXServiceImplWrapper implements CallControlService, ExceptionErrorDefinitions {

	private ThirdPartyCallImpl thirdPartyCall; 
	
	public ParlayXServiceImplWrapper() throws javax.xml.rpc.ServiceException {
		thirdPartyCall = new ThirdPartyCallImplServiceLocator().getThirdPartyCall();
	}
	
	/*
	 *  (non-Javadoc)
	 * @see se.jayway.sip.callcontrol.service.CallControlService#makeCall(java.lang.String, java.lang.String)
	 */
	public String makeCall(String caller, String callee) throws SleeEventException, SleeConnectionException, GeneralException, ConfigurationException {
		String guid = null;
		try {
			 guid = thirdPartyCall.makeCall(new URI(caller), new URI(callee), getChargingInformation());
		} catch (MalformedURIException e) {
			throw new GeneralException(e.getMessage());
		} catch (PolicyException e) {
			throw new GeneralException(e.getText());
		} catch (org.csapi.www.schema.parlayx.common.v2_0.ServiceException e) {
			//If policy or service exception
			final String message = e.getText();
			
			checkMessageValidity(message);
				
			//Map ParlayX exceptions to own exceptions 
			mapConfigurationException(message);
			mapSleeException(message);

		} catch (RemoteException e) {
			throw new GeneralException(e.getMessage());
		} 
		return guid;
	}

	private void mapConfigurationException(String message) throws GeneralException, ConfigurationException {
		
		
		if(message.equals(ERROR_CONFIGURATION_CLASS_CAST) || message.startsWith(ERROR_CONFIGURATION_INVALID_SERVICE)) {
			throw new ConfigurationException(message);
		} else {
			throw new GeneralException(ERROR_INTERNAL_INCORRECT_EXCEPTION_DESC + message);
		}
			
	}

	private void mapSleeException(final String message) throws SleeEventException, SleeConnectionException, GeneralException {
		if(message.startsWith(ERROR_SLEE_ACTIVITY_NOT_RECOGNIZED) || message.startsWith(ERROR_SLEE_EVENT_TYPE_NOT_RECOGNIZED) || message.startsWith(ERROR_SLEE_FIREEVENT)) {
			throw new SleeEventException(message);
		} else if(message.equals(ERROR_SLEE_CONNECT) || message.equals(ERROR_SLEE_DISCONNECT) || message.equals(ERROR_SLEE_FACTORY_JNDI_LOOKUP)) {
			throw new SleeConnectionException(message);
		} else {		
			throw new GeneralException(ERROR_INTERNAL_INCORRECT_EXCEPTION_DESC + message);
		}
	}

	private void checkMessageValidity(final String message) throws GeneralException {
		if ("".equals(message) || message == null  ) {
			throw new GeneralException(ERROR_INTERNAL_NO_EXCEPTION_DESC);
		}
	}

	public String getCallInformation(String callId) throws SessionUnavailableException, GeneralException {
		CallInformation commonCallInformation = null;
			try {
				commonCallInformation = thirdPartyCall.getCallInformation(callId);
			} catch (PolicyException e) {
				throw new GeneralException(e.getText());
			} catch (ServiceException e) {
				final String message = e.getText();
				checkMessageValidity(message);
				//	Map ParlayX exceptions to own exceptions 
				mapSessionException(message);
			} catch (RemoteException e) {
				throw new GeneralException(e.getMessage());
			}
		 return commonCallInformation.getCallStatus().getValue();
	}

	private void mapSessionException(final String message) throws SessionUnavailableException, GeneralException {
		if(message.startsWith(ERROR_SESSION_UNAVAILABLE)) {
			throw new SessionUnavailableException(message);
		} else {
			throw new GeneralException(ERROR_INTERNAL_INCORRECT_EXCEPTION_DESC + message);
		}
	}

	public void endCall(String callId) throws SessionUnavailableException, SleeEventException, SleeConnectionException,  GeneralException {
			try {
				thirdPartyCall.endCall(callId);
			} catch (PolicyException e) {
				throw new GeneralException(e.getText());
			} catch (ServiceException e) {
				// If policy or service exception
				final String message = e.getText();
				checkMessageValidity(message);
				//	Map ParlayX SLEE exceptions to own exceptions
				mapSleeException(message);
				// Map ParlayX session exceptions to own exceptions
				mapSessionException(message);			
			} catch (RemoteException e) {
				throw new GeneralException(e.getMessage());
			}
	}

	public void cancelCallRequest(String callId) throws SessionUnavailableException, SleeEventException, SleeConnectionException, GeneralException {
			try {
				thirdPartyCall.cancelCallRequest(callId);
			} catch (PolicyException e) {
				throw new GeneralException(e.getText());
			} catch (ServiceException e) {
				// If policy or service exception
				final String message = e.getText();
				checkMessageValidity(message);
				//	Map ParlayX SLEE exceptions to own exceptions
				mapSleeException(message);
				// Map ParlayX session exceptions to own exceptions
				mapSessionException(message);			
			} catch (RemoteException e) {
				throw new GeneralException(e.getMessage());
			}
	}

	public void init() {
		// TODO Auto-generated method stub
	}
	
	/**
	 * A dummy wrapper that returns a charging information object, intended for
	 * services that include charging as an inline message part. This method
	 * uses amount = 10.0, code = "dummy code", currency = "dummy currency",
	 * description = "dummy description".
	 * 
	 * @return A dummy charging information object
	 * @see ETSI ES 202 391-1 V1.1.1, chapter 8.3
	 */
	private ChargingInformation getChargingInformation() {
		return getChargingInformation(10.0, "dummy code", "dummy currency",
				"dummy description");
	}

	/**
	 * Return a charging information object, intended for services that include
	 * charging as an inline message part.
	 * 
	 * @param amount
	 *            Amount to be charged
	 * @param code
	 *            Charging code, referencing a contract under which the charge
	 *            is applied
	 * @param currency
	 *            Currency identifier as defined in ISO 4217 [9]
	 * @param description
	 *            Description text to be use for information and billing text
	 * @return A charging information object
	 * @see ETSI ES 202 391-1 V1.1.1, chapter 8.3
	 */
	private ChargingInformation getChargingInformation(double amount,
			String code, String currency, String description) {
		// Charging related parameters
		ChargingInformation itsChargingInformation = new ChargingInformation();

		// Amount to be debited
		itsChargingInformation.setAmount(new java.math.BigDecimal(amount));

		// Charging code
		itsChargingInformation.setCode(code);

		// Currency to debit the amount
		itsChargingInformation.setCurrency(currency);

		// Description for the transaction
		itsChargingInformation.setDescription(description);

		return itsChargingInformation;
	}

}
