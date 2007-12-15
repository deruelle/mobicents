package com.ptin.xmas.control.events;

import java.util.Random;
import java.io.Serializable;

public final class FromBusyEvent implements Serializable {
    public static int EVENT_CODE = 6;
    
	public FromBusyEvent() {
		id = new Random().nextLong() ^ System.currentTimeMillis();
	}

	public boolean equals(Object o) {
		if (o == this) return true;
		if (o == null) return false;
		return (o instanceof FromBusyEvent) && ((FromBusyEvent)o).id == id;
	}
	
	public int hashCode() {
		return (int) id;
	}
	
	public String toString() {
		return "FromBusyEvent[" + hashCode() + "]";
	}

	private final long id;
}
