package org.mobicents.slee.examples.callforwardblock.session;

import java.io.Serializable;

public class CallSession implements Serializable {
	private CallState state;
	private ServerCallLeg caller;
	private ClientCallLeg callee;
	
	public CallSession(CallState state) {
		this.state = state;
	}

	public ClientCallLeg getCallee() {
		return callee;
	}

	public void setCallee(ClientCallLeg callee) {
		this.callee = callee;
	}

	public ServerCallLeg getCaller() {
		return caller;
	}

	public void setCaller(ServerCallLeg caller) {
		this.caller = caller;
	}

	public CallState getState() {
		return state;
	}

	public void setState(CallState state) {
		this.state = state;
	}
	
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("CallSession: [state=").
			append(state).append("] [").
			append("caller=").
			append(caller).append("] ").
			append("callee=").
			append(callee).append("]");
		return buf.toString();
	}
}
