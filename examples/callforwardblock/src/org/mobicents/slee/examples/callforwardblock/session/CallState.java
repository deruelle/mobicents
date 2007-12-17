package org.mobicents.slee.examples.callforwardblock.session;

/**
 * Typesafe enumeration of valid Call States
 * TODO: switch to JDK1.5 use enum instead
 * @author hchin
 *
 */
public class CallState {
	/** name representation of call state */
	private String name = "";
	
	private CallState(String name) {
		this.name = name;
	}
	
	public String toString() {
		return this.name;
	}
	
	// Define valid enumerations for call state
	/** IDLE state, has not started any signaling */
	public static final CallState IDLE = new CallState("Idle");
	/** TRYING state, SIP Response 100 Trying */
	public static final CallState TRYING = new CallState("Trying");
	/** RINGING state, SIP Response 180 Ringing */
	public static final CallState RINGING = new CallState("Ringing");
	/** CONNECTED state, after receiving SIP Response 200 OK from INVITE */
	public static final CallState CONNECTED = new CallState("Connected");
	/** END state, SIP Bye request was received or send by UA */
	public static final CallState END = new CallState("End");
	/** FAILED state, any SIP response > 200 */
	public static final CallState FAILED = new CallState("Failed");
}
