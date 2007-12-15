package com.ptin.xmas.control.events;

import java.util.Random;
import java.io.Serializable;

public final class ToUnavailableEvent implements Serializable {
    public static int EVENT_CODE = 13;
    
	public ToUnavailableEvent() {
		id = new Random().nextLong() ^ System.currentTimeMillis();
	}

	public boolean equals(Object o) {
		if (o == this) return true;
		if (o == null) return false;
		return (o instanceof ToUnavailableEvent) && ((ToUnavailableEvent)o).id == id;
	}
	
	public int hashCode() {
		return (int) id;
	}
	
	public String toString() {
		return "ToUnavailableEvent[" + hashCode() + "]";
	}

	private final long id;
}
