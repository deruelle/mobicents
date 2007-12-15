package org.mobicents.slee.examples.callforwardblock.events;

import java.util.Random;
import java.io.Serializable;

import javax.sip.message.Request;

public final class CallBlockReqEvent implements Serializable {
	private final long id;
	private Request request;
	
	public CallBlockReqEvent() {
		id = new Random().nextLong() ^ System.currentTimeMillis();
	}

	public boolean equals(Object o) {
		if (o == this) return true;
		if (o == null) return false;
		return (o instanceof CallBlockReqEvent) && ((CallBlockReqEvent)o).id == id;
	}
	
	public int hashCode() {
		return (int) id;
	}
	
	public String toString() {
		return "CallBlockReqEvent[" + hashCode() + "]";
	}

	public Request getRequest() {
		return request;
	}

	public void setRequest(Request request) {
		this.request = request;
	}
}
