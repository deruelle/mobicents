package com.ptin.xmas.control.events;

import java.util.Random;
import java.io.Serializable;

public final class ToConnectedEvent implements Serializable {
    public static int EVENT_CODE = 19;
    
	public ToConnectedEvent() {
		id = new Random().nextLong() ^ System.currentTimeMillis();
	}

	public boolean equals(Object o) {
		if (o == this) return true;
		if (o == null) return false;
		return (o instanceof ToConnectedEvent) && ((ToConnectedEvent)o).id == id;
	}
	
	public int hashCode() {
		return (int) id;
	}
	
	public String toString() {
		return "ToConnectedEvent[" + hashCode() + "]";
	}

	private final long id;
}
