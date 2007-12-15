package org.mobicents.slee.examples.callforwardblock.events;

import java.util.Random;
import java.io.Serializable;

public final class CallForwardSuccessEvent implements Serializable {

	public CallForwardSuccessEvent() {
		id = new Random().nextLong() ^ System.currentTimeMillis();
	}

	public boolean equals(Object o) {
		if (o == this) return true;
		if (o == null) return false;
		return (o instanceof CallForwardSuccessEvent) && ((CallForwardSuccessEvent)o).id == id;
	}
	
	public int hashCode() {
		return (int) id;
	}
	
	public String toString() {
		return "CallForwardSuccessEvent[" + hashCode() + "]";
	}

	private final long id;
}
