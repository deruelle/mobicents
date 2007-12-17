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

package se.jayway.sip.callcontrol.client.invoker;

import java.net.InetAddress;

import javax.xml.rpc.ServiceException;

import org.apache.log4j.Logger;

import se.jayway.sip.callcontrol.client.service.impl.ParlayXServiceImplWrapper;
import se.jayway.sip.callcontrol.exception.ConfigurationException;
import se.jayway.sip.callcontrol.exception.GeneralException;
import se.jayway.sip.callcontrol.exception.flow.SessionUnavailableException;
import se.jayway.sip.callcontrol.exception.slee.SleeConnectionException;
import se.jayway.sip.callcontrol.exception.slee.SleeEventException;
import se.jayway.sip.callcontrol.service.CallControlService;

/**
 * This is a simple class that invokes a third party call using Parlay X web
 * services.
 * 
 * @author Johan Haleby and Jacob Mattsson
 * 
 */
public class ThirdPartyCallInvoker {
	private static final Logger log = Logger
			.getLogger(ThirdPartyCallInvoker.class);

	/**
	 * The main method.
	 */
	public static void main(String[] args) {
		new ThirdPartyCallInvoker().performCall();
	}

	/**
	 * Perform the call, i.e. setup call, simulate talking and disconnect.
	 */
	public void performCall() {

		// Get localhost ip address
		String localhostIp = "localhost"; // getLocalhostIP();

		// The first participant is regarded as a caller
		final String caller = "sip:caller@" + localhostIp + ":5062";
		// The other participant is regarded as a callee
		final String callee = "sip:callee@" + localhostIp + ":5064";

		// Get call control service
		CallControlService callInvoker = null;
		try {
			callInvoker = new ParlayXServiceImplWrapper();
		} catch (ServiceException e) {
			e.printStackTrace();
		}

		// Make the call and save the call ID
		String guid = makeCall(caller, callee, callInvoker);

		if (guid == null) {
			log
					.error("Error: makeCall() failed; returned callId is null! Aborting!");
			return;
		}

		// Choose one of the following 3 types of ending the call:
		// 1) Retrieve call status during setup. Wait 5 s (simulate talking)
		// and finally disconnect the call (endCall).
		//displayCallInformationAndTerminate(callInvoker, guid, 5000); // Does NOT work!!!

		// 2) Wait 2 s (simulate waiting for 100-TRYING) before cancelling
		// (cancelCall).
		// For this to make any sense, please use the SIPp scenario found in
		// 3pcc_sip_slee/test/scripts/uas.xml!
		// cancelCall(callInvoker, guid, 2000); // Does work

		// 3) Wait 15 s (simulate talking) before disconnecting (endCall).
		endCall(callInvoker, guid, 10000); // Does work

	}

	/**
	 * Cancel the connected call.
	 * 
	 * @param callInvoker
	 *            the call control web service.
	 * @param guid
	 *            the call ID the cancel.
	 * @param delay
	 *            the delay to wait before cancelling the call.
	 */
	private void cancelCall(CallControlService callInvoker, String guid,
			int delay) {
		try {
			System.out.println("\nWaiting " + delay
					+ " milliseconds before cancelling call ...");
			Thread.sleep(delay);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		try {
			System.out.println("\nCancelling call ...");
			callInvoker.cancelCallRequest(guid);
		} catch (SleeEventException e1) {
			System.out.println("SLEE Event Exception caught: "
					+ e1.getMessage());
			e1.printStackTrace();
		} catch (SleeConnectionException e1) {
			System.out.println("Slee Connection Exception caught: "
					+ e1.getMessage());
			e1.printStackTrace();
		} catch (GeneralException e1) {
			System.out.println("General Exception caught: " + e1.getMessage());
			e1.printStackTrace();
		} catch (SessionUnavailableException e) {
			System.out.println("Session Unavailable Exception caught: "
					+ e.getMessage());
			e.printStackTrace();
		}
		System.out.println("Call cancelled!");
	}
	
	/**
	 * Display call status during the call setup. Then wait some time (simulate
	 * talking) before ending the call.
	 * 
	 * @param callInvoker
	 *            the call control web service.
	 * @param guid
	 *            the call ID the cancel.
	 * @param delay
	 *            the delay to wait before ending the call.
	 */
	private void getCallInformationAndTerminate(
			CallControlService callInvoker, String guid, int delay) {
		boolean callEstablished = false;
		boolean callTerminatedDueToError = false;
		String callStatus = null;
		
			try {
				callStatus = callInvoker.getCallInformation(guid);

//				if (log.isDebugEnabled()) {
//					log.debug("CallStatus = "
//							+ callInvoker.getCallInformation(guid));
//				}
//				if ("se.jayway.sip.slee.sbb.CallControlSbb$SessionEstablishedState"
//						.equals(callStatus)) {
//					callEstablished = true;
//				} else if ("se.jayway.sip.slee.sbb.CallControlSbb$TerminationState"
//						.equals(callStatus)) {
//					callTerminatedDueToError = true;
//				}
//				System.out.println("");
//				System.out.print("\rCall status: " + callStatus);

			} catch (SessionUnavailableException e1) {
//				System.out.println("Session Unavailable Exception caught: "
//						+ e1.getMessage());
				e1.printStackTrace();
			} catch (GeneralException e1) {
//				System.out.println("General Exception caught: "
//						+ e1.getMessage());
				e1.printStackTrace();
			}

			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		
			endCall(callInvoker, guid, delay);
	}

	/**
	 * Display call status during the call setup. Then wait some time (simulate
	 * talking) before ending the call.
	 * 
	 * @param callInvoker
	 *            the call control web service.
	 * @param guid
	 *            the call ID the cancel.
	 * @param delay
	 *            the delay to wait before ending the call.
	 */
	private void displayCallInformationAndTerminate(
			CallControlService callInvoker, String guid, int delay) {
		boolean callEstablished = false;
		boolean callTerminatedDueToError = false;
		String callStatus = null;
		do {

			try {
				callStatus = callInvoker.getCallInformation(guid);

				if (log.isDebugEnabled()) {
					log.debug("CallStatus = " + callStatus);
				}
				if ("se.jayway.sip.slee.sbb.CallControlSbb$SessionEstablishedState"
						.equals(callStatus)) {
					callEstablished = true;
				} else if ("se.jayway.sip.slee.sbb.CallControlSbb$TerminationState"
						.equals(callStatus)) {
					callTerminatedDueToError = true;
				}
				System.out.println("");
				System.out.print("\rCall status: " + callStatus);

			} catch (SessionUnavailableException e1) {
				System.out.println("Session Unavailable Exception caught: "
						+ e1.getMessage());
				e1.printStackTrace();
			} catch (GeneralException e1) {
				System.out.println("General Exception caught: "
						+ e1.getMessage());
				e1.printStackTrace();
			}

			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} while (!callEstablished && !callTerminatedDueToError);

		if (callEstablished == true) {
			// Call has been established;
			// Sleep for delay milliseconds to simulate a conversation.
			endCall(callInvoker, guid, delay);
		}
	}

	/**
	 * End the call after waiting some time (simulate talking).
	 * 
	 * @param callInvoker
	 *            the call control web service.
	 * @param guid
	 *            the call ID the cancel.
	 * @param delay
	 *            the delay to wait before ending the call.
	 */
	private void endCall(CallControlService callInvoker, String guid, int delay) {
		System.out.println("Waiting " + delay + " ms before terminating ...");
		try {
			Thread.sleep(delay);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("Terminating call ...");
		try {
			callInvoker.endCall(guid);
		} catch (SleeEventException e1) {
			System.out.println("SLEE Event Exception caught: "
					+ e1.getMessage());
			e1.printStackTrace();
		} catch (SleeConnectionException e1) {
			System.out.println("Slee Connection Exception caught: "
					+ e1.getMessage());
			e1.printStackTrace();
		} catch (GeneralException e1) {
			System.out.println("General Exception caught: " + e1.getMessage());
			e1.printStackTrace();
		} catch (SessionUnavailableException e) {
			System.out.println("Session Unavailable Exception caught: "
					+ e.getMessage());
			e.printStackTrace();
		}
		System.out.println("Call terminated!");
	}

	/**
	 * Setup the call between caller and callee.
	 * 
	 * @param caller
	 *            the calling party in the call.
	 * @param callee
	 *            the called party in the call.
	 * @param callInvoker
	 *            the call control web service.
	 * @return the ID for the call being setup.
	 */
	private String makeCall(final String caller, final String callee,
			CallControlService callInvoker) {
		String guid = null;
		try {
			System.out.println("Invoking makeCall for caller " + caller
					+ " and callee " + callee + ".");
			guid = callInvoker.makeCall(caller, callee);
		} catch (SleeEventException e1) {
			System.out.println("SLEE Event Exception caught: "
					+ e1.getMessage());
			e1.printStackTrace();
		} catch (SleeConnectionException e1) {
			System.out.println("Slee Connection Exception caught: "
					+ e1.getMessage());
			e1.printStackTrace();
		} catch (GeneralException e1) {
			System.out.println("General Exception caught: " + e1.getMessage());
			e1.printStackTrace();
		} catch (ConfigurationException e) {
			System.out.println("Configuration Exception caught: "
					+ e.getMessage());
			e.printStackTrace();
		}

		if (log.isDebugEnabled()) {
			log.debug("Guid = " + guid);
		}
		System.out.println("makeCall returned callId " + guid + ".");
		return guid;
	}

	/**
	 * Returns the ip of the local machine.
	 * 
	 * @return the ip of the local machine.
	 */
	private String getLocalhostIP() {
		InetAddress thisIp = null;
		try {
			thisIp = InetAddress.getLocalHost();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return thisIp.getHostAddress();
	}

}
