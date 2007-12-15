package se.jayway.sip.callcontrol.client.model.impl;

import java.util.Date;

import org.csapi.www.schema.parlayx.common.v2_0.CallInformation;
import org.csapi.www.schema.parlayx.common.v2_0.CallStatus;
import org.csapi.www.schema.parlayx.common.v2_0.CallTerminationCause;

import se.jayway.sip.callcontrol.client.model.CallInformationWrapper;

/**
 * Simple class that represents information about a session.
 * 
 * @author Niklas
 * 
 * @see ETSI ES 202 391-2 V1.1.1, chapter 7.3
 */
public class CallInformationWrapperImpl implements CallInformationWrapper {
	/**
	 * Instance of the generated CallInformation class
	 */
	private CallInformation callInformation;

	/**
	 * Constructor for the CallInformation wrapper.
	 */
	public CallInformationWrapperImpl() {
		super();
		callInformation = new CallInformation();
	}

	/**
	 * Constructor for the CallInformation wrapper.
	 * 
	 * @param callInformation 
	 * 		  The call information object to wrap
	 */
	public CallInformationWrapperImpl(CallInformation callInformation) {
		super();
		this.callInformation = callInformation;
	}

	/**
	 * Constructor for the CallInformation wrapper.
	 * 
	 * @param callStatus
	 *            The current status of the call
	 * @param startTime
	 *            The time of the beginning of the call
	 * @param duration
	 *            The duration of the call expressed in seconds
	 * @param terminationCause
	 *            The cause of the termination of the call
	 */
	public CallInformationWrapperImpl(CallStatus callStatus,
			java.util.Calendar startTime, int duration,
			CallTerminationCause terminationCause) {

		callInformation = new CallInformation();
		callInformation.setCallStatus(callStatus);
		callInformation.setStartTime(startTime);
		callInformation.setDuration(duration);
		callInformation.setTerminationCause(terminationCause);
	}

	/**
	 * Returns the current status of the call.
	 * 
	 * @return The current status of the call
	 */
	public String getCallStatus() {
		return callInformation.getCallStatus().toString();
	}

	/**
	 * Returns the time of the beginning of the call.
	 * 
	 * @return The time of the beginning of the call
	 */
	public Date getStartTime() {
		return callInformation.getStartTime().getTime();
	}

	/**
	 * Returns the duration of the call expressed in seconds.
	 * 
	 * @return The duration of the call expressed in seconds
	 */
	public int getDuration() {
		return callInformation.getDuration();
	}

	/**
	 * Returns the cause of the termination of the call.
	 * 
	 * @return The cause of the termination of the call
	 */
	public String getTerminationCause() {
		return callInformation.getTerminationCause().getValue();
	}
}
