package com.ptin.xmas.control.events;

import java.util.Random;
import java.io.Serializable;

public final class FromRejectedEvent implements Serializable {
    public static int EVENT_CODE = 5;
    
	public FromRejectedEvent() {
		id = new Random().nextLong() ^ System.currentTimeMillis();
	}

	public boolean equals(Object o) {
		if (o == this) return true;
		if (o == null) return false;
		return (o instanceof FromRejectedEvent) && ((FromRejectedEvent)o).id == id;
	}
	
	public int hashCode() {
		return (int) id;
	}
	
	public String toString() {
		return "FromRejectedEvent[" + hashCode() + "]";
	}

	private final long id;
}
