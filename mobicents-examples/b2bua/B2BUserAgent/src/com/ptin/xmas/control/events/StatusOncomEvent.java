package com.ptin.xmas.control.events;

import java.util.Random;
import java.io.Serializable;

public final class StatusOncomEvent implements Serializable {
    public static int EVENT_CODE = 30;
    
	public StatusOncomEvent() {
		id = new Random().nextLong() ^ System.currentTimeMillis();
	}

	public boolean equals(Object o) {
		if (o == this) return true;
		if (o == null) return false;
		return (o instanceof StatusOncomEvent) && ((StatusOncomEvent)o).id == id;
	}
	
	public int hashCode() {
		return (int) id;
	}
	
	public String toString() {
		return "StatusOncomEvent[" + hashCode() + "]";
	}

	private final long id;
}
