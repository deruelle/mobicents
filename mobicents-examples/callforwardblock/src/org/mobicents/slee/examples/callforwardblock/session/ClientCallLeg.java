package org.mobicents.slee.examples.callforwardblock.session;

import javax.sip.ClientTransaction;
import javax.sip.address.Address;

/**
 * Represents the client side of a call leg in B2BUA situation
 * @author hchin
 */
public class ClientCallLeg extends CallLeg {
	/** Caller callID */
	private String callerCallId;
	private ClientTransaction clientTx;

	public ClientCallLeg(String callId, Address addr) {
		super(callId, addr);
	}

	public String getCallerCallId() {
		return callerCallId;
	}

	public void setCallerCallId(String callerCallId) {
		this.callerCallId = callerCallId;
	}

	public ClientTransaction getClientTx() {
		return clientTx;
	}

	public void setClientTx(ClientTransaction clientTx) {
		this.clientTx = clientTx;
	}

}
