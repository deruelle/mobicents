package com.ptin.xmas.control.events;

import java.util.Random;
import java.io.Serializable;

public final class ToDisconnectedEvent implements Serializable {
    public static int EVENT_CODE = 4;
    
	public ToDisconnectedEvent() {
		id = new Random().nextLong() ^ System.currentTimeMillis();
	}

	public boolean equals(Object o) {
		if (o == this) return true;
		if (o == null) return false;
		return (o instanceof ToDisconnectedEvent) && ((ToDisconnectedEvent)o).id == id;
	}
	
	public int hashCode() {
		return (int) id;
	}
	
	public String toString() {
		return "ToDisconnectedEvent[" + hashCode() + "]";
	}

	private final long id;
}
