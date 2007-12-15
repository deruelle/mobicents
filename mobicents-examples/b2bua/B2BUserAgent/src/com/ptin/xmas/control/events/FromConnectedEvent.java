package com.ptin.xmas.control.events;

import java.util.Random;
import java.io.Serializable;

public final class FromConnectedEvent implements Serializable {
    public static int EVENT_CODE = 1;
    
	public FromConnectedEvent() {
		id = new Random().nextLong() ^ System.currentTimeMillis();
	}

	public boolean equals(Object o) {
		if (o == this) return true;
		if (o == null) return false;
		return (o instanceof FromConnectedEvent) && ((FromConnectedEvent)o).id == id;
	}
	
	public int hashCode() {
		return (int) id;
	}
	
	public String toString() {
		return "FromConnectedEvent[" + hashCode() + "]";
	}

	private final long id;
}
