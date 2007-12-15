package com.ptin.xmas.control.events;

import java.util.Random;
import java.io.Serializable;

public final class ToTryingEvent implements Serializable {
    public static int EVENT_CODE = 23;
    
	public ToTryingEvent() {
		id = new Random().nextLong() ^ System.currentTimeMillis();
	}

	public boolean equals(Object o) {
		if (o == this) return true;
		if (o == null) return false;
		return (o instanceof ToTryingEvent) && ((ToTryingEvent)o).id == id;
	}
	
	public int hashCode() {
		return (int) id;
	}
	
	public String toString() {
		return "ToTryingEvent[" + hashCode() + "]";
	}

	private final long id;
}
