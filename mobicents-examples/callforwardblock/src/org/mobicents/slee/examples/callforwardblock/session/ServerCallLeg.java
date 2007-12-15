package org.mobicents.slee.examples.callforwardblock.session;

import javax.sip.ServerTransaction;
import javax.sip.address.Address;

/**
 * Represents the server side of a call leg in a B2BUA situation
 * @author hchin
 */
public class ServerCallLeg extends CallLeg {
	/** server transaction */
	private ServerTransaction serverTx;
	/** payload body */
	private byte[] content;
	private String calleeCallId;
	
	public ServerCallLeg(String callId, Address addr) {
		super(callId, addr);
	}

	public ServerTransaction getServerTx() {
		return serverTx;
	}

	public void setServerTx(ServerTransaction serverTx) {
		this.serverTx = serverTx;
	}

	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}

	public String getCalleeCallId() {
		return calleeCallId;
	}

	public void setCalleeCallId(String calleeCallId) {
		this.calleeCallId = calleeCallId;
	}

}
