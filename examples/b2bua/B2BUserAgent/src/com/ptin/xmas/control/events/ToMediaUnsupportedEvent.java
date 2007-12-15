package com.ptin.xmas.control.events;

import java.util.Random;
import java.io.Serializable;

public final class ToMediaUnsupportedEvent implements Serializable {
    public static int EVENT_CODE = 22;
    
	public ToMediaUnsupportedEvent() {
		id = new Random().nextLong() ^ System.currentTimeMillis();
	}

	public boolean equals(Object o) {
		if (o == this) return true;
		if (o == null) return false;
		return (o instanceof ToMediaUnsupportedEvent) && ((ToMediaUnsupportedEvent)o).id == id;
	}
	
	public int hashCode() {
		return (int) id;
	}
	
	public String toString() {
		return "ToMediaUnsupportedEvent[" + hashCode() + "]";
	}

	private final long id;
}
