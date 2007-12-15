package org.mobicents.slee.examples.callforwardblock.events;

import java.util.Random;
import java.io.Serializable;

import javax.sip.message.Request;

public final class CallForwardEvent implements Serializable {
	private final long id;
	private String destinationAddress;
	private Request request;
	
	public CallForwardEvent() {
		id = new Random().nextLong() ^ System.currentTimeMillis();
	}

	public boolean equals(Object o) {
		if (o == this) return true;
		if (o == null) return false;
		return (o instanceof CallForwardEvent) && 
			((CallForwardEvent)o).id == id &&
			((CallForwardEvent)o).destinationAddress.equals(destinationAddress);
	}
	
	public int hashCode() {
		return (int) id + destinationAddress.hashCode();
	}
	
	public String toString() {
		return "CallForwardEvent[" + hashCode() + "\n" +
			"address " + destinationAddress + "]";
	}

	public String getDestinationAddress() {
		return destinationAddress;
	}

	public void setDestinationAddress(String destinationAddress) {
		this.destinationAddress = destinationAddress;
	}

	public Request getRequest() {
		return request;
	}

	public void setRequest(Request request) {
		this.request = request;
	}
	
}
