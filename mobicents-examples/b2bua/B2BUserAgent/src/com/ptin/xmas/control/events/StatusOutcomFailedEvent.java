package com.ptin.xmas.control.events;

import java.util.Random;
import java.io.Serializable;

public final class StatusOutcomFailedEvent implements Serializable {
    public static int EVENT_CODE = 28;
    
	public StatusOutcomFailedEvent() {
		id = new Random().nextLong() ^ System.currentTimeMillis();
	}

	public boolean equals(Object o) {
		if (o == this) return true;
		if (o == null) return false;
		return (o instanceof StatusOutcomFailedEvent) && ((StatusOutcomFailedEvent)o).id == id;
	}
	
	public int hashCode() {
		return (int) id;
	}
	
	public String toString() {
		return "StatusOutcomFailedEvent[" + hashCode() + "]";
	}

	private final long id;
}
