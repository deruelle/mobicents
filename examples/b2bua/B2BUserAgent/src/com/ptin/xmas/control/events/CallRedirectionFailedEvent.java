package com.ptin.xmas.control.events;

import java.util.Random;
import java.io.Serializable;

public final class CallRedirectionFailedEvent implements Serializable {
    public static int EVENT_CODE = 21;
    
	public CallRedirectionFailedEvent() {
		id = new Random().nextLong() ^ System.currentTimeMillis();
	}

	public boolean equals(Object o) {
		if (o == this) return true;
		if (o == null) return false;
		return (o instanceof CallRedirectionFailedEvent) && ((CallRedirectionFailedEvent)o).id == id;
	}
	
	public int hashCode() {
		return (int) id;
	}
	
	public String toString() {
		return "CallRedirectionFailedEvent[" + hashCode() + "]";
	}

	private final long id;
}
