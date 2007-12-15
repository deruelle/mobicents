package com.ptin.xmas.control.events;

import java.util.Random;
import java.io.Serializable;

public final class UnregisteredEvent implements Serializable {
    public static int EVENT_CODE = 38;
    
	public UnregisteredEvent() {
		id = new Random().nextLong() ^ System.currentTimeMillis();
	}

	public boolean equals(Object o) {
		if (o == this) return true;
		if (o == null) return false;
		return (o instanceof UnregisteredEvent) && ((UnregisteredEvent)o).id == id;
	}
	
	public int hashCode() {
		return (int) id;
	}
	
	public String toString() {
		return "UnregisteredEvent[" + hashCode() + "]";
	}

	private final long id;
}
