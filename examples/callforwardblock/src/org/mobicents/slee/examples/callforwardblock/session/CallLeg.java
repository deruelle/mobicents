package org.mobicents.slee.examples.callforwardblock.session;

import java.io.Serializable;

import javax.sip.address.Address;

/**
 * Base class defining a Call Leg
 * @author hchin
 */
public abstract class CallLeg implements Serializable {
	private String callId;
	private Address sipAddress;
	
	public CallLeg(String callId, Address addr) {
		this.callId = callId;
		this.sipAddress = addr;
	}
	public String getCallId() {
		return callId;
	}
	public void setCallId(String callId) {
		this.callId = callId;
	}
	public Address getSipAddress() {
		return sipAddress;
	}
	public void setSipAddress(Address sipAddress) {
		this.sipAddress = sipAddress;
	}
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("address=").
			append(getSipAddress().toString()).
			append("callID=").
			append(getCallId());
		return buf.toString();
	}
}
