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

package se.jayway.sip.callcontrol.web;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import se.jayway.sip.callcontrol.exception.ConfigurationException;
import se.jayway.sip.callcontrol.exception.GeneralException;
import se.jayway.sip.callcontrol.exception.flow.SessionUnavailableException;
import se.jayway.sip.callcontrol.exception.slee.SleeConnectionException;
import se.jayway.sip.callcontrol.factory.ObjectFactory;
import se.jayway.sip.callcontrol.service.CallControlService;

/**
 * Servlet implementation class for Servlet: CallControlServlet
 * 
 */
public class CallControlServlet extends javax.servlet.http.HttpServlet
		implements javax.servlet.Servlet {

	private static final long serialVersionUID = -7105287997812073145L;
	private Logger log = Logger.getLogger(CallControlServlet.class);

	private final static String GUID_KEY = "guid";
	private final static String CALL_SERVICE_KEY = "call_service";
	private final static String CALL_SERVICE_DECOR_KEY = "call_service_decoration";
	private String callerAddress;
	private String calleeAddress;
	private CallControlService callControlService;

	/*
	 * (non-Java-doc)
	 * 
	 * @see javax.servlet.http.HttpServlet#HttpServlet()
	 */
	public CallControlServlet() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.GenericServlet#init()
	 */
	public void init() {
	}

	/*
	 * (non-Java-doc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request,
	 *      HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		final String action = request.getParameter("action");
		HttpSession session = request.getSession();

		if (action.equalsIgnoreCase("getCallInformation")) {
			// Get the guid from the session
			String guid = (String) session.getAttribute(GUID_KEY);

			String status;
			try {
				status = callControlService.getCallInformation(guid);
			} catch (SessionUnavailableException e) {
				status = e.getMessage();
			} catch (GeneralException e) {
				status = e.getMessage();
			}
			forwardStatus(request, response, status);
		}
	}

	/*
	 * (non-Java-doc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest request,
	 *      HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		final String action = request.getParameter("action");
		final String callServiceDecor = request.getParameter("call_service_decoration");

		String guid = null;
		HttpSession session = request.getSession();
		String callControlServiceAsString = "<call_service>";
		
		// Get the slee connection service implementation from factory
		try {
			ObjectFactory objectFactory = ObjectFactory.getInstance();
			callControlService = (CallControlService) objectFactory.getCallControlService();
			callControlServiceAsString = objectFactory.getCallControlServiceAsString();
		} catch (SleeConnectionException e) {
			//updateSessionStatus(e.getMessage(), guid);
			forwardStatus(request, response, e.getMessage());
		} catch (ConfigurationException e) {
			//updateSessionStatus(e.getMessage(), guid);
			forwardStatus(request, response, e.getMessage());
		} catch (ClassCastException e) {
			/*updateSessionStatus(
					"Error in configuration file: Have you defined an incorrect call service?",
					guid);*/
			forwardStatus(request, response, 
				"Error in configuration file: Have you defined an incorrect call service?");			
		} catch (GeneralException e) {
			//updateSessionStatus(e.getMessage(), guid);
			forwardStatus(request, response, e.getMessage());
		}

		// Put call service indicator in the session
		request.setAttribute(CALL_SERVICE_KEY, callControlServiceAsString);
		
		try {		
			if (action.equalsIgnoreCase("makeCall")) {
				// Get caller and callee SIP addresses
				callerAddress = request.getParameter("caller");
				calleeAddress = request.getParameter("callee");
				// Send the "make call" message
				guid = callControlService.makeCall(callerAddress, calleeAddress);
				// Put the GUID in the session
				session.setAttribute(GUID_KEY, guid);
				// set initial JSP text decoration of call service
				session.setAttribute(CALL_SERVICE_DECOR_KEY, "blink");
				// Forward to requesting page
				forwardToCallControl(request, response, callerAddress, calleeAddress);
			} else if (action.equalsIgnoreCase("endCall")) {
				// Get the GUID from the session
				guid = (String) session.getAttribute(GUID_KEY);
				// Send the "end call" message
				callControlService.endCall(guid);
				// Update JSP text decoration of call service
				session.setAttribute(CALL_SERVICE_DECOR_KEY, callServiceDecor);
				// Forward to requesting page
				forwardToCallControl(request, response, callerAddress, calleeAddress);
			} else if (action.equalsIgnoreCase("cancelRequest")) {
				// Get the GUID from the session
				guid = (String) session.getAttribute(GUID_KEY);
				// Send the "cancel call" message
				callControlService.cancelCallRequest(guid);
				// Update JSP text decoration of call service
				session.setAttribute(CALL_SERVICE_DECOR_KEY, callServiceDecor);
				// Forward to requesting page
				forwardToCallControl(request, response, callerAddress, calleeAddress);
			}
		} 
		catch (Exception e) {
			forwardStatus(request, response, e.getMessage());
		}
	}

	/**
	 * Cancel a call
	 * 
	 * @param guid
	 * @param session
	 * @throws GeneralException 
	 * @throws SleeConnectionException 
	 * @throws SleeEventException 
	 * @throws SessionUnavailableException 
	 */
	/*private void cancelCall(String guid) throws SessionUnavailableException, SleeEventException, SleeConnectionException, GeneralException {
		callControlService.cancelCallRequest(guid);
	}/

	/**
	 * End a call
	 * 
	 * @param guid
	 * @param session
	 * @throws GeneralException 
	 * @throws SleeConnectionException 
	 * @throws SleeEventException 
	 * @throws SessionUnavailableException 
	 */
	/*private void endCall(String guid) throws SessionUnavailableException, SleeEventException, SleeConnectionException, GeneralException {
		callControlService.endCall(guid);
	}*/

	/**
	 * Invokes a third party call control between caller and callee
	 * 
	 * @param caller
	 * @param callee
	 * @return the GUID
	 * @throws ConfigurationException 
	 * @throws GeneralException 
	 * @throws SleeConnectionException 
	 * @throws SleeEventException 
	 */
	/*private String makeCall(String caller, String callee) throws SleeEventException, SleeConnectionException, GeneralException, ConfigurationException {
		String guid = null;
		guid = callControlService.makeCall(caller, callee);
		return guid;
	}*/

	/**
	 * 
	 * 
	 * @param aDestination
	 * @param aRequest
	 * @param aResponse
	 * @throws ServletException
	 * @throws IOException
	 */
	private void forward(String aDestination, HttpServletRequest aRequest,
			HttpServletResponse aResponse) {
		RequestDispatcher dispatcher = aRequest
				.getRequestDispatcher(aDestination);
		try {
			dispatcher.forward(aRequest, aResponse);
		} catch (ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void forwardStatus(HttpServletRequest request, HttpServletResponse response, String status) {
		forward("jsp/callInformation.jsp?status=" + status, request,
				response);
	}
	
	private void forwardToCallControl(HttpServletRequest request, HttpServletResponse response, 
									  String callerAddress, String calleeAddress) {
		forward("jsp/callControl.jsp?caller=" + callerAddress + "&callee="
				+ calleeAddress, request, response);
	}
	
	/**
	 * Update the status stored in the session
	 * 
	 * @param session
	 * @param newStatus
	 */
	/*private void updateSessionStatus(final String newStatus, String guid) {
		StateCallback stateCallback = null;
		if (callControlService instanceof SleeConnectionServiceImpl) {
			try {
				stateCallback = (StateCallback) ((SleeConnectionServiceImpl) callControlService)
						.getStateCallback(guid);
			} catch (SessionUnavailableException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			stateCallback.setSessionState(newStatus);
		}
	}*/

}