package com.ptin.xmas.control.events;

import java.util.Random;
import java.io.Serializable;

public final class FromUnavailableEvent implements Serializable {
    public static int EVENT_CODE = 8;
    
	public FromUnavailableEvent() {
		id = new Random().nextLong() ^ System.currentTimeMillis();
	}

	public boolean equals(Object o) {
		if (o == this) return true;
		if (o == null) return false;
		return (o instanceof FromUnavailableEvent) && ((FromUnavailableEvent)o).id == id;
	}
	
	public int hashCode() {
		return (int) id;
	}
	
	public String toString() {
		return "FromUnavailableEvent[" + hashCode() + "]";
	}

	private final long id;
}
