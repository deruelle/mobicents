package com.ptin.xmas.control.events;

import java.util.Random;
import java.io.Serializable;

public final class CallRedirectionSuccessEvent implements Serializable {
    public static int EVENT_CODE = 20;
    
	public CallRedirectionSuccessEvent() {
		id = new Random().nextLong() ^ System.currentTimeMillis();
	}

	public boolean equals(Object o) {
		if (o == this) return true;
		if (o == null) return false;
		return (o instanceof CallRedirectionSuccessEvent) && ((CallRedirectionSuccessEvent)o).id == id;
	}
	
	public int hashCode() {
		return (int) id;
	}
	
	public String toString() {
		return "CallRedirectionSuccessEvent[" + hashCode() + "]";
	}

	private final long id;
}
