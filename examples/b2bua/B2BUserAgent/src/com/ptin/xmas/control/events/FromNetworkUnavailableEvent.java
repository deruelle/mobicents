package com.ptin.xmas.control.events;

import java.util.Random;
import java.io.Serializable;

public final class FromNetworkUnavailableEvent implements Serializable {
    public static int EVENT_CODE = 9;
    
	public FromNetworkUnavailableEvent() {
		id = new Random().nextLong() ^ System.currentTimeMillis();
	}

	public boolean equals(Object o) {
		if (o == this) return true;
		if (o == null) return false;
		return (o instanceof FromNetworkUnavailableEvent) && ((FromNetworkUnavailableEvent)o).id == id;
	}
	
	public int hashCode() {
		return (int) id;
	}
	
	public String toString() {
		return "FromNetworkUnavailableEvent[" + hashCode() + "]";
	}

	private final long id;
}
